package m_diary.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

import m_diary.utils.IOUtils;
import m_diary.utils.Protocol;
import m_diary.utils.UriUtils;
import m_diary.utils.UserManager;

import static m_diary.activities.DiaryActivity.diary;

public class AudioActivity extends AppCompatActivity {
    MediaRecorder recorder = new MediaRecorder();
    private String filePath;
    private String audio_name;
    public boolean audio_enable;
    private boolean record_audio;
    private boolean opened_audio;
    public int audio_power;
    private int Duration;
    public static int second;
    public static int minute;
    private TextView record_time;
    private TextView play_time;
    private SeekBar sb;
    private MediaPlayer mp;
    private static Handler handler;
    private Timer record_timer;
    private Timer play_timer;
    private TimerTask record_time_task;
    private TimerTask play_time_task;
    private Uri audio_uri;
    private Button play_pause;
    private Button audio_mute;
    private DialogInterface.OnClickListener confirm;
    private DialogInterface.OnClickListener cancel;
    public static final int RECORDING = 1000;
    public static final int PLAYING = 1001;
    public static int max_min = 0;
    public static int max_sec = 0;
    //????????????
    Runnable start=new Runnable(){
        @Override
        public void run() {
            // TODO Auto-generated method stub
            mp.start();
            handler.post(update_sb);
            //?????????handler??????SeekBar
        }
    };
    //??????????????????
    Runnable update_sb =new Runnable(){
        @Override
        public void run() {
            // TODO Auto-generated method stub
            sb.setProgress(mp.getCurrentPosition());
            handler.postDelayed(update_sb, 1000);
            //?????????????????????
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        ActivityCompat.requestPermissions(AudioActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, Protocol.STORAGE_PERMISSION);
        ActivityCompat.requestPermissions(AudioActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Protocol.READE_PERMISSION);
        if(ContextCompat.checkSelfPermission(AudioActivity.this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AudioActivity.this, new String[]{android.Manifest.permission.RECORD_AUDIO}, Protocol.RECORD_PERMISSION);
        }
        else{
            initial_recorder();
        }
        initial();
        initial_dialogs();
        sb = findViewById(R.id.Music_seekBar);
        play_pause = findViewById(R.id.Music_Play_Pause_bt);
        audio_mute = findViewById(R.id.Audio_Mute_bt);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                float current_time = (float) seekBar.getProgress();
                int current_min = (int)((float)current_time/60000.0f);
                int current_sec = (int)((float)current_time/1000.0f) - current_min*60;
                minute = current_min;
                second = current_sec;
                String second_str;
                if (second < 10) {
                    second_str = "0" + current_sec;
                } else second_str = current_sec + "";
                String max_str = "/";
                if(max_min<10){
                    max_str += "0"+max_min + ":";
                }
                else max_str += max_min + ":";
                if(max_sec<10){
                    max_str += "0"+max_sec;
                }
                else max_str += max_sec;
                play_time.setText("?????? : 0" + current_min + ":" + second_str + max_str);
                mp.seekTo(seekBar.getProgress());
            }
        });
        play_time = findViewById(R.id.Audio_time);
        record_time = findViewById(R.id.Record_time);
        record_audio = false;
        opened_audio = false;
    }
    //?????????????????????
    @SuppressLint("HandlerLeak")
    private void initial() {
        handler = new Handler() {
            public void handleMessage(Message message) {
                switch (message.what) {
                    case RECORDING: {
                        if (second == 60) {
                            second = 0;
                            minute++;
                        }
                        String second_str = "";
                        if (second < 10) {
                            second_str = "0" + second;
                        } else second_str = second + "";
                        record_time.setText("?????? : 0" + minute + ":" + second_str);

                    }
                    break;
                    case PLAYING: {
                        if (second == 60) {
                            second = 0;
                            minute++;
                        }
                        String second_str = "";
                        if (second < 10) {
                            second_str = "0" + second;
                        } else second_str = second + "";
                        String max_str = "/";
                        if (max_min < 10) {
                            max_str += "0" + max_min + ":";
                        } else max_str += max_min + ":";
                        if (max_sec < 10) {
                            max_str += "0" + max_sec;
                        } else max_str += max_sec;
                        play_time.setText("?????? : 0" + minute + ":" + second_str + max_str);
                        if(sb.getProgress() == sb.getMax()){
                            stop_all();
                        }
                    }
                    break;
                }
            }
        };
    }
    /***********?????????????????????**********/
    //?????????????????????
    @SuppressLint("ClickableViewAccessibility")
    private void initial_recorder(){
        Button record_bt = findViewById(R.id.Record_bt);
        record_bt.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                Toast.makeText(AudioActivity.this, "Record begin!", Toast.LENGTH_SHORT).show();
                if(mp != null) {
                    if (mp.isPlaying()) {
                        View tv = new View(AudioActivity.this);
                        stop_audio(tv);
                    }
                }
                if(filePath == null) {
                    filePath = "";
                }
                filePath =getExternalFilesDir(null).getAbsolutePath() + "/Diary_Data/" + UserManager.username + "/" + diary.index + "/Audio/" + System.currentTimeMillis() + ".m4a";
                File output_audio = new File(filePath);
                if (!output_audio.getParentFile().exists()) {
                    output_audio.getParentFile().mkdirs();
                }
                try {
                    if (output_audio.exists()) {
                        output_audio.delete();
                    }
                    output_audio.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC_ELD);
                recorder.setOutputFile(output_audio);
                try {
                    recorder.prepare();
                }
                catch (IOException e){
                    e.printStackTrace();
                }
                recorder.start();
                second = 0;
                minute = 0;
                record_timer = new Timer();
                record_time_task = getRecord_time_task();
                record_timer.schedule(record_time_task,0,1000);
            }
            else if(event.getAction() == MotionEvent.ACTION_UP){
                try{
                    recorder.stop();
                    recorder.reset();
                    record_audio = true;
                    opened_audio = false;
                    stop_record_timer();
                    set_audio(filePath);
                    Toast.makeText(AudioActivity.this, "????????????!", Toast.LENGTH_SHORT).show();
                }
                catch (Exception e){
                    Toast.makeText(AudioActivity.this, "??????????????????!", Toast.LENGTH_SHORT).show();
                    recorder.reset();
                    record_audio = false;
                    second = 0;
                    minute = 0;
                    stop_record_timer();
                }
            }
            return false;
        });
    }
    //????????????
    private void stop_record() {
        recorder.stop();
        recorder.reset();
        record_audio = true;
        opened_audio = false;
        stop_record_timer();
        Toast.makeText(AudioActivity.this, "????????????????????????600s!", Toast.LENGTH_SHORT).show();
        Toast.makeText(AudioActivity.this, "????????????!", Toast.LENGTH_SHORT).show();
    }
    //?????????????????????
    private void stop_record_timer() {
        if (record_timer != null) {
            record_timer.cancel();
            record_timer = null;
        }

        if (record_time_task != null) {
            record_time_task.cancel();
            record_time_task = null;
        }
    }
    /***********?????????????????????**********/
    /*********************????????????*****************/
    //??????????????????
    public TimerTask getRecord_time_task(){
        return new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                second++;
                if((minute*60 + second)>=600) {
                    stop_record_timer();
                    stop_record();
                }
                message.what = RECORDING;
                handler.sendMessage(message);
            }
        };
    }
    //??????????????????
    public TimerTask getPlay_time_task() {
        return new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                second++;
                if((minute*60 + second)>=(float)Duration/1000.0f) {
                    stop_play_timer();
                    second = 0;
                    minute = 0;
                }
                message.what = PLAYING;
                handler.sendMessage(message);
            }
        };
    }
    //?????????????????????
    private void stop_play_timer(){
        if (play_timer != null) {
            play_timer.cancel();
            play_timer = null;
        }

        if (play_time_task != null) {
            play_time_task.cancel();
            play_time_task = null;
        }
    }
    //??????????????????
    private void set_audio(String path){
        filePath = path;
        File file = new File(filePath);
        audio_name = file.getName();
        if(record_audio) {
            audio_uri = Uri.fromFile(file);
        }
        mp = MediaPlayer.create(this, audio_uri);
        //?????????
        Duration=mp.getDuration();
        //????????????????????????
        sb.setMax(Duration);
        second = 0;
        minute = 0;
        String second_str;
        if (second < 10) {
            second_str = "0" + second;
        } else second_str = second + "";
        max_min = (int)((float)Duration/60000.0f);
        max_sec = (int)((float)Duration/1000.0f) - max_min*60;
        String max_str = "/";
        if(max_min<10){
            max_str += "0"+max_min + ":";
        }
        else max_str += max_min + ":";
        if(max_sec<10){
            max_str += "0"+max_sec;
        }
        else max_str += max_sec;
        audio_enable = true;
        AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audio_power = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        play_time.setText("time : 0" + minute + ":" + second_str + max_str);
        ((TextView)findViewById(R.id.Audio_name)).setText(audio_name);
    }
    //??????????????????????????????????????????
    private void copy_Audio(String from_File){
        String save_File_Name = Environment.getExternalStorageDirectory() + "/Diary_Data/" + UserManager.username + "/" + diary.index + "/Audio/" + System.currentTimeMillis();
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
        if (record_audio) {
            IOUtils.deleteFile(filePath);
        }
        filePath = save_File_Name;
        audio_uri = Uri.fromFile(new File(filePath));
    }
    //????????????
    private void openAlbum(){
        if(filePath == null) {
            filePath = "";
        }
        if(mp != null) {
            if (mp.isPlaying()) {
                View tv = new View(AudioActivity.this);
                stop_audio(tv);
            }
        }
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("audio/*");
        if(record_audio && !opened_audio){
            IOUtils.deleteFile(filePath);
        }
        startActivityForResult(intent, Protocol.CHOOSE_AUDIO); // ????????????
    }
    //??????????????????
    @TargetApi(19)
    private void handleAudioOnKitKat(Intent data){
        String Path = null;
        if(data != null) {
            Uri uri = data.getData();
            audio_uri = uri;
            Log.d("TAG", "handleImageOnKitKat: uri is " + uri);
            Path = UriUtils.getPath(AudioActivity.this,uri);
            opened_audio = true;
            record_audio = false;
            set_audio(Path); // ??????????????????????????????
        }
        else{
            Toast.makeText(this, "???????????????????????????", Toast.LENGTH_SHORT).show();
        }
    }
    //??????????????????
    private void handleAudioBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        audio_uri = uri;
        String audioPath = getAudioPath(uri, null);
        opened_audio = true;
        record_audio = false;
        set_audio(audioPath);
    }
    private String getAudioPath(Uri uri, String selection) {
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
    //??????????????????
    public void stop_all(){
        if(mp != null) {
            mp.seekTo(0);
            mp.pause();
            stop_play_timer();
            second = 0;
            minute = 0;
            sb.setProgress(0);
            String second_str;
            if (second < 10) {
                second_str = "0" + second;
            } else second_str = second + "";
            String max_str = "/";
            if(max_min<10){
                max_str += "0"+max_min + ":";
            }
            else max_str += max_min + ":";
            if(max_sec<10){
                max_str += "0"+max_sec;
            }
            else max_str += max_sec;
            play_time.setText("?????? : 0" + minute + ":" + second_str + max_str);
            play_pause.setBackgroundResource(R.drawable.play_bt_selector);
        }
        else{
            Toast.makeText(this, "Audio is not loaded!", Toast.LENGTH_SHORT).show();
        }
    }
    //??????????????????
    private void delete_record_audio(){
        File file = new File(filePath);
        if(file.exists()) {
            file.delete();
        }
    }
    /*******************????????????**********************/
    /*******************????????????**********************/
    //?????????????????????
    private void  initial_dialogs(){
        confirm= (arg0, arg1) -> {
            //????????????????????????
            stop_all();
            Intent i = new Intent();
            i.putExtra(Protocol.CHANGED, false);
            AudioActivity.this.setResult(Protocol.ADD_AUDIO,i);
            if(record_audio && !opened_audio){
                IOUtils.deleteFile(filePath);
            }
            finish();
        };
        //??????????????????
        cancel= (arg0, arg1) -> arg0.cancel();
    }
    //????????????
    public void stop_audio(View view){
        stop_all();
    }
    //??????
    public void mute_audio(View view){
        if(opened_audio||record_audio) {
            AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            if (!audio_enable) {
                audio_enable = true;
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audio_power, 0);
                audio_mute.setBackgroundResource(R.drawable.audio_bt_selector);
            } else {
                audio_power = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                audio_enable = false;
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0);
                audio_mute.setBackgroundResource(R.drawable.mute_bt_selector);
            }
        }
        else{
            Toast.makeText(this, "???????????????!", Toast.LENGTH_SHORT).show();
        }
    }
    //???????????????
    public void complete_audio(View view){
        //to do:add your media
        if(opened_audio||record_audio) {
            Intent i = new Intent();
            copy_Audio(filePath);
            if (filePath == null) {
                filePath = "";
            }
            if (mp != null) {
                if (mp.isPlaying()) {
                    View tv = new View(AudioActivity.this);
                    stop_audio(tv);
                }
            }
            stop_all();
            recorder.release();
            i.putExtra(Protocol.CHANGED, true);
            i.putExtra(Protocol.AUDIO_URI, audio_uri.toString());
            i.putExtra(Protocol.AUDIO_PATH, filePath);
            i.putExtra(Protocol.AUDIO_NAME, audio_name);
            this.setResult(Protocol.ADD_AUDIO, i);
            finish();
        }
        else {
            Toast.makeText(this, "???????????????????????????!", Toast.LENGTH_SHORT).show();
        }
    }
    //????????????
    public void open_audio(View view){
        //?????????????????????????????????
        if (ContextCompat.checkSelfPermission(AudioActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AudioActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Protocol.OPEN_PERMISSION);
        }
        else{
            openAlbum();
        }
    }
    //??????
    public void back_audio(View view){
        if(opened_audio || record_audio) {
            AlertDialog.Builder alert_dialog_builder = new AlertDialog.Builder(this);
            alert_dialog_builder.setMessage("Leave without saving?");
            alert_dialog_builder.setPositiveButton("confirm", confirm);
            alert_dialog_builder.setNegativeButton("cancel", cancel);
            AlertDialog alert_dialog = alert_dialog_builder.create();
            alert_dialog.show();
        }
        else {
            finish();
        }
    }
    //??????????????????
    public void play_pause_audio(View view){
        if(opened_audio || record_audio) {
            if (mp.isPlaying()) {
                mp.pause();
                stop_play_timer();
                play_pause.setBackgroundResource(R.drawable.play_bt_selector);
            } else {
                play_timer = new Timer();
                play_time_task = getPlay_time_task();
                play_timer.schedule(play_time_task,0,1000);
                handler.post(start);
                play_pause.setBackgroundResource(R.drawable.pause_bt_selector);
            }
        }
        else{
            Toast.makeText(this, "?????????????????????!", Toast.LENGTH_SHORT).show();
        }

    }
    //???????????????????????????
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(opened_audio || record_audio) {
                AlertDialog.Builder alert_dialog_builder = new AlertDialog.Builder(this);
                alert_dialog_builder.setMessage("??????????????????????");
                alert_dialog_builder.setPositiveButton("???", confirm);
                alert_dialog_builder.setNegativeButton("???", cancel);
                AlertDialog alert_dialog = alert_dialog_builder.create();
                alert_dialog.show();
            }
            else{
                Intent i = new Intent();
                i.putExtra(Protocol.CHANGED, false);
                AudioActivity.this.setResult(Protocol.ADD_AUDIO,i);
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
    //???????????????????????????
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case Protocol.CHOOSE_AUDIO:{
                // ???????????????????????????
                if (Build.VERSION.SDK_INT >= 19) {
                    // 4.4?????????????????????????????????????????????
                    handleAudioOnKitKat(data);
                } else {
                    // 4.4??????????????????????????????????????????
                    handleAudioBeforeKitKat(data);
                }
            }break;
        }
    }
    //??????????????????
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case Protocol.RECORD_PERMISSION:{
                if(ContextCompat.checkSelfPermission(AudioActivity.this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(AudioActivity.this, "????????????????????????\n????????????????????????????????????", Toast.LENGTH_SHORT).show();
                    Toast.makeText(this, "??????????????????????????????????????????", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent();
                    i.putExtra("changed", false);
                    AudioActivity.this.setResult(Protocol.ADD_AUDIO, i);
                    recorder.release();
                    finish();
                }
                else {
                    initial_recorder();
                }
            }break;
            case  Protocol.OPEN_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "???????????????????????????????????????", Toast.LENGTH_SHORT).show();
                }
            }break;
        }
    }
    /*******************??????????????????**********************/
}