package m_diary.activities;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import m_diary.assets.Audio;
import m_diary.assets.Diary;
import m_diary.assets.Picture;
import m_diary.assets.SendFileRequest;
import m_diary.assets.Text;
import m_diary.assets.Video;
import m_diary.assets.Weather;
import m_diary.clientsocket.SocketClient;
import m_diary.controls.AlbumVideoView;
import m_diary.controls.MyAudioControl;
import m_diary.controls.MyPictureControl;
import m_diary.controls.MyTextControl;
import m_diary.controls.MyVideoControl;
import m_diary.controls.Sticker;
import m_diary.controls.DiaryItemLayout;
import m_diary.controls.StickerManager;
import m_diary.utils.IOUtils;
import m_diary.utils.Protocol;
import m_diary.utils.UserManager;

import static m_diary.utils.ImgUtils.getDegree;
import static m_diary.utils.ImgUtils.rotate_ImageView;


public class DiaryActivity extends AppCompatActivity{

    public static boolean changed;//是否修改过日记内容
    public static boolean editable;
    private boolean new_Diary;
    private boolean sendMainFile = false;
    private int textNum = 0;
    private int picNum = 0;
    private int videoNum = 0;
    private int audioNum = 0;
    private int totalDiaryNum = 0;   //日记总数量
    public Button saveEditBt;
    public static Diary diary = new Diary("", "", 0, 2021, 1, 1, "星期五", "晴");
    private EditText Title;   //题目
    private MyTextControl Sub_Title; //天气日期等信息
    private ArrayList<MyTextControl> texts = new ArrayList<>(); //文本内容控件
    private ArrayList<MyPictureControl> pictures = new ArrayList<>(); //图片内容控件
    private DialogInterface.OnClickListener confirm;
    private DialogInterface.OnClickListener cancel;
    public DiaryItemLayout mDiaryItemLayout;//日记内容容器
    private ArrayList<MyAudioControl> audioControl = new ArrayList<>();
    private MyVideoControl videoControl;
//    public MyRelativeLayout textContent;
    public LinearLayout audioContent;
    //初始化操作
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);
        Intent intent = getIntent();
        totalDiaryNum = intent.getIntExtra(Protocol.DIARY_INDEX, 0);
        new_Diary = intent.getBooleanExtra(Protocol.NEW_DIARY,true);
        String diaryPath = intent.getStringExtra(Protocol.DIARY_PATH);
        changed  = false;
        sendMainFile = false;
        editable = new_Diary;
        initial_Edit();
        initial_dialogs();
        initial_content(diaryPath);
    }
    //初始化内容
    private void initial_content(String diaryPath){
        resetNum();
        if(new_Diary){
            //这里是新建日记
            diary = new Diary("", "", totalDiaryNum, Weather.year, Weather.month, Weather.day, Weather.week, Weather.weather);
        }
        else{
            //这里是打开日记
            diary = IOUtils.getMyDiary(IOUtils.readFile(diaryPath));
            setDiary(diary);
        }
    }
    //初始化提示窗口
    private void  initial_dialogs(){
        confirm= (arg0, arg1) -> {
            //此处不保存并退出
            removeAll();
            finish();
        };
        //返回继续编辑
        cancel= (arg0, arg1) -> arg0.cancel();
    }
    //初始化现有控件数据
    private void initial_Edit(){
        videoControl = new MyVideoControl(DiaryActivity.this);
        mDiaryItemLayout = findViewById(R.id.diaryItemLayout);
        //textContent = findViewById(R.id.Diary_Item);
        Title = findViewById(R.id.Title_Name);
        Sub_Title = findViewById(R.id.Sub_Title);
        saveEditBt = findViewById(R.id.save_edit_bt);
        audioContent = findViewById(R.id.audioContent);
        Title.setText(diary.title);
        Title.setEnabled(editable);
        Sub_Title.setEnabled(editable);
        Typeface typeface = Typeface.createFromAsset(getAssets(),"fonts/华康少女字体.ttf");
        Title.setTypeface(typeface);
        Sub_Title.setTypeface(typeface);
        if(new_Diary) {
            String sub_title = Weather.week + "   " + Weather.year + "年" + Weather.month + "月" + Weather.day + "日   " + Weather.weather + "\n";
            Title.setText("");
            Sub_Title.setText(sub_title);
        }else {
            //打开日记
            Title.setText(diary.title);
            Sub_Title.setText(diary.subTitle);
        }
        //控制是否能编辑保存按钮初始化
        if(editable){
            saveEditBt.setBackgroundResource(R.drawable.save_bt_selector);
        }
        else{
            saveEditBt.setBackgroundResource(R.drawable.edit_bt_selector);
        }
        Title.setOnFocusChangeListener((v, hasFocus) -> {
            changed = true;
            diary.title = Title.getText().toString();
        });
        Sub_Title.setOnFocusChangeListener((v, hasFocus) -> changed = true);
    }
    /*******************功能函数**********************/
    //重构日记
    public void setDiary(Diary diary){
        Title.setText(diary.title);
        Sub_Title.setText(diary.subTitle);
        while (diary.texts.size() != textNum){
            //重构文字
            setText(diary.texts.get(textNum));
            textNum++;
        }
        while (diary.pictures.size() != picNum){
            //重构图片
            setPicture(diary.pictures.get(picNum));
            picNum++;
        }
        while (diary.videos.size() != videoNum){
            //重构视频
            setVideo(diary.videos.get(videoNum));
            videoNum++;
        }
        while (diary.audios.size() != audioNum){
            //重构音频
            setAudio(diary.audios.get(audioNum));
            audioNum++;
        }
        resetNum();
    }
    //重构文字
    public void setText(Text text){
        MyTextControl textView = new MyTextControl(DiaryActivity.this);
        textView.setEms(8);
        textView.setTag(text.tag);
        textView.scale = text.scale;
        textView.mAngle = text.angle;
        mDiaryItemLayout.colorCode = text.textColor;
        mDiaryItemLayout.typeFaceStr = text.typeFaceStr;
        mDiaryItemLayout.addTextView(textView, text.x, text.y, text.content, text.textColor, text.typeFaceStr, text.angle);
        mDiaryItemLayout.addView(mDiaryItemLayout.currentView);
        mDiaryItemLayout.list.add(textView);
    }
    //重构图片
    public void setPicture(Picture picture){
        Bitmap bitmap = null;
        if(picture.type.equals(Sticker.PICTURE)){
            bitmap = BitmapFactory.decodeFile(picture.path);
            int degree = getDegree(picture.path);
            bitmap = rotate_ImageView(degree, bitmap); //已经处理好的图片
        }else if(picture.type.equals(Sticker.STICKER)){
            bitmap = BitmapFactory.decodeResource(DiaryActivity.this.getResources(), picture.id);
        }
        Sticker picSticker = new Sticker(DiaryActivity.this, bitmap);
        picSticker.id = picture.id;
        picSticker.path = picture.path;
        picSticker.type = picture.type;
        picSticker.restoreSet(picture);
        mDiaryItemLayout.addSticker(picSticker);
        StickerManager.getInstance().clearAllFocus();
    }
    //重构视频
    public void setVideo(Video video){
        AlbumVideoView albumVideoView = new AlbumVideoView(DiaryActivity.this);
        albumVideoView.currentPosition.x = video.x;
        albumVideoView.currentPosition.y = video.y;
        albumVideoView.setLocation(video.x, video.y);
        albumVideoView.setMeasure(video.width, video.height);
        videoControl.addVideo(video.path, albumVideoView);
    }
    //重构音频
    public void setAudio(Audio audio){
        Uri audio_uri = Uri.parse(audio.uri); //音频路径
        MyAudioControl currentView = new MyAudioControl(DiaryActivity.this, audio_uri, audio.path, audio.name);
        audioControl.add(currentView);
        audioContent.addView(currentView);
    }
    //这里添加图片
    public void addPicture(Intent data){
        String img_path = data.getStringExtra(Protocol.IMG_PATH);
        Bitmap bitmap = BitmapFactory.decodeFile(img_path);
        int degree = getDegree(img_path);
        bitmap = rotate_ImageView(degree, bitmap); //已经处理好的图片
        Sticker picSticker = new Sticker(DiaryActivity.this, bitmap);
        picSticker.path = img_path;
        picSticker.type = Sticker.PICTURE;
        mDiaryItemLayout.addSticker(picSticker);
        changed = data.getBooleanExtra(Protocol.CHANGED, false);
    }
    //这里添加视频
    public void addVideo(Intent data){
        String video_path = data.getStringExtra(Protocol.VIDEO_PATH); //视频路径
        Uri video_uri = Uri.parse(data.getStringExtra(Protocol.VIDEO_URI));
        AlbumVideoView albumVideoView = new AlbumVideoView(DiaryActivity.this);
        videoControl.addVideo(video_path, albumVideoView);  //TODO
        changed = data.getBooleanExtra(Protocol.CHANGED, false);
    }
    //这里添加音频
    public void addAudio(Intent data){
        String audio_path = data.getStringExtra(Protocol.AUDIO_PATH); //音频路径
        String audio_name = data.getStringExtra(Protocol.AUDIO_NAME); //音频名称
        String audio_uri = data.getStringExtra(Protocol.AUDIO_URI); //音频路径
        Audio audio = new Audio(audio_name, audio_path, audio_uri);
        setAudio(audio);
        changed = data.getBooleanExtra(Protocol.CHANGED, false);
    }
    //这里添加贴纸
    public void addSticker(Intent data){
        int resStickerID = data.getIntExtra(Protocol.STICKER_ID, 0);
        Bitmap bmp = BitmapFactory.decodeResource(DiaryActivity.this.getResources(), resStickerID);
        Sticker sticker = new Sticker(DiaryActivity.this, bmp);
        sticker.type = Sticker.STICKER;
        sticker.id = resStickerID;
        mDiaryItemLayout.addSticker(sticker);
        changed = data.getBooleanExtra(Protocol.CHANGED, false);
    }
    //将计数归零
    public void resetNum(){
        textNum = 0;
        picNum = 0;
        videoNum = 0;
        audioNum = 0;
    }
    //记录当前日记信息
    public void setDiaryInfo(){
        //基本信息
        if(new_Diary){
            diary.index = totalDiaryNum;
        }
        diary.title = Title.getText().toString();
        diary.subTitle = Sub_Title.getText().toString();
        diary.savePath = Environment.getExternalStorageDirectory() + "/Diary_Data/" + UserManager.username + "/" + diary.index + "/MainFile";
        //清除所有内容，并重新读取当前修改内容
        diary.texts.clear();
        diary.pictures.clear();
        diary.videos.clear();
        diary.audios.clear();
        diary.texts = getTextInfo();
        //多媒体信息
        for(int i = 0; i < StickerManager.getInstance().mStickerList.size(); i++){
            Sticker currentView = StickerManager.getInstance().mStickerList.get(i);
            Picture picture = new Picture(currentView.id, currentView.path, currentView.type, currentView.mSrcPoints);
            currentView.getMatrix().getValues(picture.matrix);
            diary.pictures.add(picture);
        }
        for(int i = 0; i < videoControl.videoViews.size(); i++){
            if (!videoControl.videoViews.get(i).isDel) {
                AlbumVideoView currentView = videoControl.videoViews.get(i);
                Video video = new Video(currentView.path, currentView.currentPosition.x, currentView.currentPosition.y, currentView.width, currentView.height);
                diary.videos.add(video);
            }
        }
        for(int i = 0; i < audioControl.size(); i++){
            if(!audioControl.get(i).isDel) {
                MyAudioControl currentView = audioControl.get(i);
                Audio audio = new Audio(currentView.name, currentView.path, currentView.uri);
                diary.audios.add(audio);
            }
        }
//        Text text = new Text();
//        text.list = textContent.list;
//        text.listTvParams = textContent.listTvParams;
//        diary.texts.add(text);
    }
    //保存日记到本地
    public void saveDiaryToLocal(){
        setDiaryInfo();
        try {
            File save_File = new File(diary.savePath);
            if (!save_File.getParentFile().exists()) {
                save_File.getParentFile().mkdirs();
            }
            else if(save_File.exists()||save_File.isDirectory()){
                save_File.delete();
            }
            String diaryStr = new Gson().toJson(diary);
            FileOutputStream outputStream = new FileOutputStream(save_File);
            byte[] bytes = diaryStr.getBytes();
            int len = bytes.length;
            outputStream.write(bytes, 0, len);
            outputStream.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    //保存日记到云端
    public void saveDiaryToServer(){
        String jsonStr = new Gson().toJson(new SendFileRequest(Protocol.SEND_FILE, UserManager.username, String.valueOf(diary.index), Protocol.TypeEnum.MainFile.toString()));
        SocketClient.getSocketClient().sendMessage(jsonStr);
        SocketClient.getSocketClient().getMessage(DiaryActivity.this, Protocol.SEND_FILE);
    }
    //发送文件
    public boolean sendFile(String result){
        Looper.prepare();
        switch (result){
            case Protocol.SEND_FILE:{
                if(!sendMainFile) {
                    Toast.makeText(DiaryActivity.this, "传输主文件！", Toast.LENGTH_SHORT).show();
                    SocketClient.getSocketClient().sendFile(DiaryActivity.this, diary.savePath);
                    SocketClient.getSocketClient().getMessage(DiaryActivity.this, Protocol.SEND_FILE);
                    sendMainFile = true;
                    return false;
                }
                else{
                    if(diary.pictures.size() != picNum){
                        if(diary.pictures.get(picNum).type.equals(Sticker.PICTURE)) {
                            //Toast.makeText(DiaryActivity.this, "传输图片 " + (picNum+1) + "!", Toast.LENGTH_SHORT).show();
                            SocketClient.getSocketClient().sendFile(DiaryActivity.this, diary.pictures.get(picNum).path);
                            SocketClient.getSocketClient().getMessage(DiaryActivity.this, Protocol.SEND_FILE);
                        }
                        picNum++;
                        while(picNum != diary.pictures.size()) {
                            try {
                                if (!diary.pictures.get(picNum).type.equals(Sticker.PICTURE)) {
                                    picNum++;
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                                return false;
                            }

                        }
                        return false;
                    }
                    else if(diary.videos.size() != videoNum){
                        //Toast.makeText(DiaryActivity.this, "传输视频 " + (videoNum+1) + "!", Toast.LENGTH_SHORT).show();
                        SocketClient.getSocketClient().sendFile(DiaryActivity.this, diary.videos.get(videoNum).path);
                        SocketClient.getSocketClient().getMessage(DiaryActivity.this, Protocol.SEND_FILE);
                        videoNum++;
                        return false;
                    }
                    else if(diary.audios.size() != audioNum) {
                        //Toast.makeText(DiaryActivity.this, "传输音频 " + (audioNum+1) + "!", Toast.LENGTH_SHORT).show();
                        SocketClient.getSocketClient().sendFile(DiaryActivity.this, diary.audios.get(audioNum).path);
                        SocketClient.getSocketClient().getMessage(DiaryActivity.this, Protocol.SEND_FILE);
                        audioNum++;
                        return false;
                    }
                }
            }break;
            case Protocol.SEND_SUCCESS:{
                if(diary.pictures.size() != picNum) {
                    try {
                        if (diary.pictures.get(picNum).type.equals(Sticker.PICTURE)) {
                            //Toast.makeText(DiaryActivity.this, "传输音频 " + (picNum+1) + "!", Toast.LENGTH_SHORT).show();
                            String jsonStr = new Gson().toJson(new SendFileRequest(Protocol.SEND_FILE, UserManager.username, String.valueOf(diary.index), Protocol.TypeEnum.Picture.toString()));
                            SocketClient.getSocketClient().sendMessage(jsonStr);
                            SocketClient.getSocketClient().getMessage(DiaryActivity.this, Protocol.SEND_FILE);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        return false;
                    }
                    return false;
                }
                else if(diary.videos.size() != videoNum) {
                    //Toast.makeText(DiaryActivity.this, "传输音频 " + (audioNum+1) + "!", Toast.LENGTH_SHORT).show();
                    String jsonStr = new Gson().toJson(new SendFileRequest(Protocol.SEND_FILE, UserManager.username, String.valueOf(diary.index), Protocol.TypeEnum.Video.toString()));
                    SocketClient.getSocketClient().sendMessage(jsonStr);
                    SocketClient.getSocketClient().getMessage(DiaryActivity.this, Protocol.SEND_FILE);
                    return false;
                }
                else if(diary.audios.size() != audioNum) {
                    //Toast.makeText(DiaryActivity.this, "传输音频 " + (audioNum+1) + "!", Toast.LENGTH_SHORT).show();
                    String jsonStr = new Gson().toJson(new SendFileRequest(Protocol.SEND_FILE, UserManager.username, String.valueOf(diary.index), Protocol.TypeEnum.Audio.toString()));
                    SocketClient.getSocketClient().sendMessage(jsonStr);
                    SocketClient.getSocketClient().getMessage(DiaryActivity.this, Protocol.SEND_FILE);
                    return false;
                }
            }break;
            case Protocol.SEND_FAILED:{
                Toast.makeText(DiaryActivity.this, "上传失败！", Toast.LENGTH_SHORT).show();
            }break;
        }
        return true;
    }

    public ArrayList<Text> getTextInfo(){
        ArrayList<Text> texts = new ArrayList<>();
        for(int i = 0; i < mDiaryItemLayout.list.size(); i++) {
            Text currentText = new Text();
            currentText.tag = mDiaryItemLayout.list.get(i).getTag().toString();
            currentText.angle = mDiaryItemLayout.list.get(i).mAngle;
            currentText.content = mDiaryItemLayout.list.get(i).getText().toString();
            currentText.scale = mDiaryItemLayout.list.get(i).scale;
            currentText.x = mDiaryItemLayout.list.get(i).getX();
            currentText.y = mDiaryItemLayout.list.get(i).getY();
            currentText.textColor = mDiaryItemLayout.list.get(i).colorCode;
            currentText.typeFaceStr = mDiaryItemLayout.list.get(i).typeFaceStr;
            texts.add(currentText);
        }
        return texts;
    }
    //移除所有内容
    public void removeAll(){
        audioContent.removeAllViews();
        videoControl.removeAll();
        mDiaryItemLayout.removeAll();
    }
    /*******************功能函数**********************/
    /*******************消息响应**********************/
    //得到回传消息
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(data != null) {
            boolean is_Get_data = data.getBooleanExtra(Protocol.CHANGED, false);
            switch (requestCode) {
                //图片
                case Protocol.ADD_PICTURE: {
                    if (is_Get_data) {
                        addPicture(data);
                    } else {
                        //no data
                        Toast.makeText(this, "你没有添加任何东西!", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
                //视频
                case Protocol.ADD_VIDEO: {
                    if (is_Get_data) {
                        //这里添加视频
                        addVideo(data);
                    } else {
                        //no data
                        Toast.makeText(this, "你没有添加任何东西!", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
                //音频
                case Protocol.ADD_AUDIO: {
                    if (is_Get_data) {
                        //data changed
                        addAudio(data);
                    } else {
                        //no data
                        Toast.makeText(this, "你没有添加任何东西!", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
                //贴纸
                case Protocol.ADD_STICKER: {
                    if (is_Get_data) {
                        //data changed
                        addSticker(data);
                    } else {
                        //no data
                        Toast.makeText(this, "你没有添加任何东西!", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            }
        }
        else{
            Toast.makeText(this, "没有数据返回!", Toast.LENGTH_SHORT).show();
        }
    }
    //保存||编辑按钮
    public void save_edit(View view){
        if(((EditText)findViewById(R.id.Title_Name)).getText().toString().isEmpty()){
            Toast.makeText(this, "题目不能为空!", Toast.LENGTH_SHORT).show();
        }
        else {
            if (editable) {
                StickerManager.getInstance().clearAllFocus();
                findViewById(R.id.save_edit_bt).setBackgroundResource(R.drawable.edit_bt_selector);
                saveDiaryToLocal();
                Toast.makeText(this, "日记已保存在本地!", Toast.LENGTH_SHORT).show();
                editable = false;
            } else {
                findViewById(R.id.save_edit_bt).setBackgroundResource(R.drawable.save_bt_selector);
                editable = true;
            }
            Title.setEnabled(editable);
            Sub_Title.setEnabled(editable);
        }
    }
    //返回按钮
    public void back(View view){
        if(changed && editable) {
            AlertDialog.Builder alert_dialog_builder = new AlertDialog.Builder(this);
            alert_dialog_builder.setMessage("不保存并离开吗?");
            alert_dialog_builder.setPositiveButton("是", confirm);
            alert_dialog_builder.setNegativeButton("否", cancel);
            AlertDialog alert_dialog = alert_dialog_builder.create();
            alert_dialog.show();
        }
        else {
            if (changed) {
                if(SocketClient.getSocketClient().isNetworkConnected(DiaryActivity.this)){
                    resetNum();
                    saveDiaryToServer();
                }
                else{
                    Toast.makeText(this, "没有网络，所以未将手账同步到云端!", Toast.LENGTH_SHORT).show();
                }
            }
            removeAll();
            finish();
        }
    }
    //添加图片
    public void open_picture(View view){
        if(editable) {
            Button mImage = findViewById(R.id.pic_button);
            ActivityOptions compat = ActivityOptions.makeScaleUpAnimation(mImage, mImage.getWidth() / 2, mImage.getHeight() / 2, 0, 0);
            Intent i = new Intent(this, PictureActivity.class);
            startActivityForResult(i, Protocol.ADD_PICTURE, compat.toBundle());
        }else{
            Toast.makeText(DiaryActivity.this, "请点击编辑按钮！", Toast.LENGTH_SHORT).show();
        }
    }
    //添加视频
    public void open_video(View view){
        if(editable) {
            Button mImage = findViewById(R.id.video_button);
            ActivityOptions compat = ActivityOptions.makeScaleUpAnimation(mImage, mImage.getWidth() / 2, mImage.getHeight() / 2, 0, 0);
            Intent i = new Intent(this, VideoActivity.class);
            startActivityForResult(i, Protocol.ADD_VIDEO, compat.toBundle());
        }else{
            Toast.makeText(DiaryActivity.this, "请点击编辑按钮！", Toast.LENGTH_SHORT).show();
        }
    }
    //添加声音
    public void open_sound(View view){
        if(editable) {
            Button mImage = findViewById(R.id.voice_button);
            ActivityOptions compat = ActivityOptions.makeScaleUpAnimation(mImage, mImage.getWidth() / 2, mImage.getHeight() / 2, 0, 0);
            Intent i = new Intent(this, AudioActivity.class);
            startActivityForResult(i, Protocol.ADD_AUDIO, compat.toBundle());
        }else{
            Toast.makeText(DiaryActivity.this, "请点击编辑按钮！", Toast.LENGTH_SHORT).show();
        }
    }
    //打开贴纸
    public void open_sticker(View view){
        if(editable) {
            Button mImage = findViewById(R.id.sticker_button);
            ActivityOptions compat = ActivityOptions.makeScaleUpAnimation(mImage, mImage.getWidth() / 2, mImage.getHeight() / 2, 0, 0);
            Intent i = new Intent(this, StickersActivity.class);
            startActivityForResult(i, Protocol.ADD_STICKER, compat.toBundle());
        }else{
            Toast.makeText(DiaryActivity.this, "请点击编辑按钮！", Toast.LENGTH_SHORT).show();
        }
    }
    //添加文字
    public void open_text(View view){
        if(editable){
            mDiaryItemLayout.showDialog("",true,"#000000", "楷体.ttf");
        }else{
            Toast.makeText(DiaryActivity.this, "请点击编辑按钮！", Toast.LENGTH_SHORT).show();
        }
//        Button mImage = findViewById(R.id.text_button);
//        ActivityOptions compat = ActivityOptions.makeScaleUpAnimation(mImage,mImage.getWidth() / 2, mImage.getHeight() / 2, 0, 0);
//        Intent i = new Intent(this,TextActivity.class);
//        i.putExtra(Protocol.OPEN_TEXT_TYPE, Protocol.NEW_TEXT);
//        startActivityForResult(i, Protocol.ADD_TEXT,compat.toBundle());
    }
    //手机上返回按钮按下
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(changed && editable) {
                AlertDialog.Builder alert_dialog_builder = new AlertDialog.Builder(this);
                alert_dialog_builder.setMessage("不保存并离开吗?");
                alert_dialog_builder.setPositiveButton("是", confirm);
                alert_dialog_builder.setNegativeButton("否", cancel);
                AlertDialog alert_dialog = alert_dialog_builder.create();
                alert_dialog.show();
            }
            else {
                if (changed) {
                    if(SocketClient.getSocketClient().isNetworkConnected(DiaryActivity.this)){
                        saveDiaryToServer();
                    }
                    else{
                        Toast.makeText(this, "没有网络，所以未将手账同步到云端!", Toast.LENGTH_SHORT).show();
                    }
                }
                removeAll();
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
    /*******************消息响应**********************/
}