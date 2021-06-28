package m_diary.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;

import com.example.myapplication.R;

import java.io.File;
import java.util.ArrayList;

import m_diary.assets.Diary;
import m_diary.assets.Weather;
import m_diary.controls.TimeLine;
import m_diary.utils.IOUtils;
import m_diary.utils.Protocol;
import m_diary.utils.UserManager;

public class CalendarActivity extends AppCompatActivity {
    private LinearLayout item_body;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        initial_view();
    }
    /******************功能函数*******************/
    //初始化控件
    private void initial_view(){
        item_body = findViewById(R.id.Item_body);
        final CalendarView myCalendar = findViewById(R.id.MyCalendar);
        myCalendar.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            item_body.removeAllViews();
            setItem_body(year, month+1, dayOfMonth);
        });
        setItem_body(Weather.year, Weather.month, Weather.day);
    }
    //设置日记内容
    private void setItem_body(int year, int month, int day){
        String diaryDirPath = Environment.getExternalStorageDirectory() + "/Diary_Data/" + UserManager.username;
        File file = new File(diaryDirPath);
        int countNum = 0;
        ArrayList<Diary> diaryContents = new ArrayList<>();
        if(file.exists()){
            File []files = file.listFiles();
            for (File fileIndex:files){
                if(fileIndex.isDirectory()){
                    String diaryPath = diaryDirPath + "/" + countNum + "/MainFile";
                    String content = IOUtils.readFile(diaryPath);
                    if(content.equals("")){
                        continue;
                    }
                    Diary diary = IOUtils.getMyDiary(content);
                    if(diary.year == year && diary.month == month && diary.day == day){
                        diaryContents.add(diary);
                        countNum++;
                    }
                }
            }
            for(int i = countNum-1; i>=0; i--){
                addItem(diaryContents.get(i));
            }
        }
    }
    //添加目标日期的日记
    private void addItem(Diary diary){
        TimeLine timeLine = new TimeLine(CalendarActivity.this, diary);
        item_body.addView(timeLine);
    }
    /******************消息响应*******************/
    //打开新日记
    public void openDiary(View view){
        //int max_index = preferences.getInt(today_date + SaveActivity.MAX_INDEX,-1);
        Button mImage = findViewById(R.id.New_Diary_bt);
        ActivityOptions compat = ActivityOptions.makeScaleUpAnimation(mImage,mImage.getWidth() / 2, mImage.getHeight() / 2, 0, 0);
        Intent i = new Intent(this,DiaryActivity.class);
        i.putExtra(Protocol.NEW_DIARY,true);
//        i.putExtra(Protocl.DIARY_INDEX,max_index+1);
        startActivity(i,compat.toBundle());
    }
    //返回
    public void back(View view) {
        finish();
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        item_body.removeAllViews();
        setItem_body(Weather.year, Weather.month, Weather.day);
    }
}