package m_diary.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.myapplication.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import m_diary.utils.IOUtils;
import m_diary.utils.Protocol;
import m_diary.utils.UserManager;

import static m_diary.activities.DiaryActivity.diary;
import static m_diary.utils.ImgUtils.getDegree;
import static m_diary.utils.ImgUtils.rotate_ImageView;

public class PictureActivity extends AppCompatActivity {
    private DialogInterface.OnClickListener confirm;
    private DialogInterface.OnClickListener cancel;
    private boolean taken_photo;
    private boolean opened_photo;
    private String filePath;
    public Bitmap bitmap;
    private Uri imageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        ActivityCompat.requestPermissions(PictureActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, Protocol.STORAGE_PERMISSION);
        ActivityCompat.requestPermissions(PictureActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Protocol.READE_PERMISSION);
        taken_photo = false;
        opened_photo = false;
        initial_dialog();
    }
    /*******************????????????**********************/
    //????????????
    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, Protocol.CHOOSE_PHOTO); // ????????????
    }
    //???????????????
    private void openCamera(){
        filePath = Environment.getExternalStorageDirectory() + "/Diary_Data/Images/" + System.currentTimeMillis() + ".jpg";
        File outputImage = new File(filePath);
        if (!outputImage.getParentFile().exists()) {
            outputImage.getParentFile().mkdirs();
        }
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //???????????????
        if (Build.VERSION.SDK_INT < 24) {
            imageUri = Uri.fromFile(outputImage);
        } else {
            imageUri = FileProvider.getUriForFile(PictureActivity.this, "m_diary.my_provider", outputImage);
        }
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, Protocol.TAKE_PHOTO);
    }
    //????????????????????????????????????????????????
    public void copy_Bitmap(String from_File){
        String save_File_Name = getExternalFilesDir(null).getAbsolutePath() + "/Diary_Data/" + UserManager.username + "/" + diary.index + "/Picture/" + System.currentTimeMillis();
        File save_File = new File(save_File_Name);
        if (!save_File.getParentFile().exists()) {
            save_File.getParentFile().mkdirs();
        }
        else if(save_File.exists()||save_File.isDirectory()){
            save_File.delete();
        }
        try {
            InputStream inputStream = new FileInputStream(from_File);
            FileOutputStream outputStream = new FileOutputStream(save_File);
            byte[] bytes = new byte[1024];
            int i;
            while ((i = inputStream.read(bytes)) > 0) {
                outputStream.write(bytes, 0, i);
            }
            inputStream.close();
            outputStream.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        if (taken_photo) {
            IOUtils.deleteFile(filePath);
        }
        filePath = save_File_Name;
    }
    //??????????????????
    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        if(data != null) {
            Uri uri = data.getData();
            Log.d("TAG", "handleImageOnKitKat: uri is " + uri);

            if (DocumentsContract.isDocumentUri(this, uri)) {
                // ?????????document?????????Uri????????????document id??????
                String docId = DocumentsContract.getDocumentId(uri);
                if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                    String id = docId.split(":")[1]; // ????????????????????????id
                    String selection = MediaStore.Images.Media._ID + "=" + id;
                    imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
                } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                    Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                    imagePath = getImagePath(contentUri, null);
                }
            } else if ("content".equalsIgnoreCase(uri.getScheme())) {
                // ?????????content?????????Uri??????????????????????????????
                imagePath = getImagePath(uri, null);
            } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                // ?????????file?????????Uri?????????????????????????????????
                imagePath = uri.getPath();
            }
            displayImage(imagePath); // ??????????????????????????????
        }
        else{
            opened_photo = false;
            Toast.makeText(this, "You canceled opening", Toast.LENGTH_SHORT).show();
        }
    }
    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }
    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // ??????Uri???selection??????????????????????????????
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }
    //????????????
    private void displayImage(String Path) {
        if (Path != null) {
            filePath = Path;
            bitmap = BitmapFactory.decodeFile(Path);
            int degree = getDegree(Path);
            bitmap = rotate_ImageView(degree, bitmap);
            ((ImageView) findViewById(R.id.Picture_content)).setImageBitmap(bitmap);
            opened_photo = true;
            taken_photo = false;
        } else {
            Toast.makeText(this, "Failed to get image", Toast.LENGTH_SHORT).show();
        }
    }
    //?????????????????????
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }
    //??????????????????
    private void delete_record_sound(){
        File file = new File(filePath);
        if(file.exists()) {
            file.delete();
        }
    }
    /*******************????????????**********************/
    /*******************????????????**********************/
    //??????????????????????????????
    private void initial_dialog(){
        confirm=new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface arg0,int arg1)
            {
                Intent i = new Intent();
                i.putExtra(Protocol.CHANGED, false);
                PictureActivity.this.setResult(Protocol.ADD_PICTURE, i);
                if(taken_photo){
                    File delete_file = new File(filePath);
                    delete_file.delete();
                    String[] path = {filePath};
                    PictureActivity.this.getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DATA + "=?",path);
                }
                finish();
            }
        };
        cancel= (arg0, arg1) -> arg0.cancel();
    }
    //????????????
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Protocol.CAMERA_PERMISSION:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
            }break;
            case Protocol.OPEN_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
            }
            break;
        }
    }
    //??????????????????
    public void complete_picture(View view) {
        //to do:add your media
        if(opened_photo||taken_photo) {
            Intent i = new Intent();
            copy_Bitmap(filePath);
            i.putExtra(Protocol.IMG_PATH, filePath);
            i.putExtra(Protocol.CHANGED, true);
            this.setResult(Protocol.ADD_PICTURE, i);
            finish();
        }
        else{
            Toast.makeText(this, "???????????????????????????!", Toast.LENGTH_SHORT).show();
        }
    }
    //???????????????????????????
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(taken_photo || opened_photo) {
                AlertDialog.Builder alert_dialog_builder = new AlertDialog.Builder(this);
                alert_dialog_builder.setMessage("Leave without saving?");
                alert_dialog_builder.setPositiveButton("confirm", confirm);
                alert_dialog_builder.setNegativeButton("cancel", cancel);
                AlertDialog alert_dialog = alert_dialog_builder.create();
                alert_dialog.show();
            }
            else{
                Intent i = new Intent();
                i.putExtra(Protocol.CHANGED, false);
                PictureActivity.this.setResult(Protocol.ADD_PICTURE, i);
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
    //?????????????????????
    public void back_picture(View view) {
        if(taken_photo || opened_photo) {
            AlertDialog.Builder alert_dialog_builder = new AlertDialog.Builder(this);
            alert_dialog_builder.setMessage("??????????????????????");
            alert_dialog_builder.setPositiveButton("???", confirm);
            alert_dialog_builder.setNegativeButton("???", cancel);
            AlertDialog alert_dialog = alert_dialog_builder.create();
            alert_dialog.show();
        }
        else {
            Intent i = new Intent();
            i.putExtra(Protocol.CHANGED, false);
            PictureActivity.this.setResult(Protocol.ADD_PICTURE, i);
            finish();
        }
    }
    //????????????
    public void take_Photo(View view) {
        if (ContextCompat.checkSelfPermission(PictureActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PictureActivity.this, new String[]{Manifest.permission.CAMERA}, Protocol.CAMERA_PERMISSION);
        }
        else{
            // ??????????????????
            openCamera();
        }
    }
    //????????????
    public void open_picture_file(View view) {
        //?????????????????????????????????
        if (ContextCompat.checkSelfPermission(PictureActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PictureActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Protocol.OPEN_PERMISSION);
        }
        else{
            openAlbum();
        }
    }
    //????????????????????????????????????
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case Protocol.TAKE_PHOTO: {
                try {// ??????????????????????????????
                    File file = new File(filePath);
                    if(file.exists()) {
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        int degree = readPictureDegree(file.getAbsolutePath());
                        bitmap = rotate_ImageView(degree, bitmap);
                        ((ImageView) findViewById(R.id.Picture_content)).setImageBitmap(bitmap);
                        taken_photo = true;
                        opened_photo = false;
                    }
                    else{
                        Toast.makeText(this, "??????????????????!", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            break;
            case Protocol.CHOOSE_PHOTO: {
                //choose your photo
                // ???????????????????????????
                if (Build.VERSION.SDK_INT >= 19) {
                    // 4.4?????????????????????????????????????????????
                    handleImageOnKitKat(data);
                } else {
                    // 4.4??????????????????????????????????????????
                    handleImageBeforeKitKat(data);
                }
            }
            break;
        }
    }
    /*******************??????????????????**********************/
}