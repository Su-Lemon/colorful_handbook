package m_diary.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.myapplication.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import m_diary.assets.Diary;
import m_diary.assets.Weather;
import m_diary.clientsocket.SocketClient;
import m_diary.controls.TimeLine;
import m_diary.utils.IOUtils;
import m_diary.utils.Protocol;
import m_diary.utils.UserManager;

public class MainActivity extends AppCompatActivity {
    private ViewSwitcher viewSwitcher;
    private boolean   changed;
    public static long last_time;
    public static int totalDiaryNum;
    private int max_index;
    private TextView date;
    private TextView week;
    private TextView weather;
    private TextView temperature;
    private TextView notice;
    private ImageView weatherTag;
    public static Handler weatherHandler;

    public LinearLayout myTimeLine;
    private ScrollView myScrollView;    //自定义滑动控件
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        intiHandler();
        setOnTimeLine();
        changed = false;
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
        SocketClient.getSocketClient().sendMessage("{\"" + Protocol.REQUEST_CODE + "\":\"" + Protocol.Get_Weather + "\"}");
        SocketClient.getSocketClient().getMessage(MainActivity.this, Protocol.Get_Weather);
    }
    //初始化多线程传值
    @SuppressLint("HandlerLeak")
    private void intiHandler(){
        weatherHandler = new Handler() {
            public void handleMessage(Message message) {
                String dateStr = Weather.getDateStr();
                date.setText(dateStr);
                week.setText(Weather.week);
                weather.setText(Weather.weather);
                temperature.setText(Weather.temperature);
                notice.setText(Weather.notice);
                if(Weather.weather.contains("雨")){
                    weatherTag.setImageResource(R.drawable.rain);
                }
                else if(Weather.weather.contains("晴")){
                    weatherTag.setImageResource(R.drawable.sunny);
                }
                else if(Weather.weather.contains("多云")){
                    weatherTag.setImageResource(R.drawable.cloudy);
                }
                else if(Weather.weather.contains("阴")){
                    weatherTag.setImageResource(R.drawable.overcast);
                }
                else if(Weather.weather.contains("雪")){
                    weatherTag.setImageResource(R.drawable.snow);
                }
            }
        };
    }
    //初始化控件
    private void initViews(){
        TextView location = findViewById(R.id.Location);
        date = (TextView)findViewById(R.id.Date);
        week = (TextView)findViewById(R.id.Week);
        weather = (TextView)findViewById(R.id.WeatherText);
        temperature = (TextView)findViewById(R.id.Temperature);
        notice = (TextView)findViewById(R.id.Notice);
        myTimeLine = (LinearLayout)findViewById(R.id.ItemLayout);
        viewSwitcher = (ViewSwitcher) findViewById(R.id.mainMenuLayout);
        myScrollView = findViewById(R.id.timeLineScroll);
        weatherTag = findViewById(R.id.weatherTag);
        Typeface typeface = Typeface.createFromAsset(MainActivity.this.getAssets(),"fonts/华康少女字体.ttf");
        location.setTypeface(typeface);
        date.setTypeface(typeface);
        week.setTypeface(typeface);
        weather.setTypeface(typeface);
        notice.setTypeface(typeface);
        findViewById(R.id.Menu_include).setVisibility(View.INVISIBLE);
        findViewById(R.id.Arrow_bt).setBackgroundResource(R.drawable.arrow_selector);
    }
    /*******************功能函数**********************/
    //设置时间线控件
    private void setOnTimeLine(){
        myTimeLine.removeAllViews();
        String diaryDirPath = Environment.getExternalStorageDirectory() + "/Diary_Data/" + UserManager.username;
        File file = new File(diaryDirPath);
        int countNum = 0;
        ArrayList<String> diaryContents = new ArrayList<>();
        if(file.exists()){
            File []files = file.listFiles();
            for (File fileIndex:files){
                if(fileIndex.isDirectory()){
                    String diaryPath = diaryDirPath + "/" + countNum + "/MainFile";
                    String content = IOUtils.readFile(diaryPath);
                    if(content.equals("")){
                        continue;
                    }
                    diaryContents.add(content);
                    countNum++;
                }
            }
            totalDiaryNum = countNum;
            for(int i = totalDiaryNum-1; i>=0; i--){
                String diaryContent = diaryContents.get(i);
                Diary diary = IOUtils.getMyDiary(diaryContent);
                addItemToTimeLine(diary);
            }
        }
        else {
            totalDiaryNum = 0;
        }
    }
    private void addItemToTimeLine(Diary diary){
        TimeLine timeLine = new TimeLine(MainActivity.this, diary);
        myTimeLine.addView(timeLine);
    }
    //设置天气
    public void setWeather(String result) {
        Looper.prepare();
        Toast.makeText(MainActivity.this, "欢迎使用多彩手账！", Toast.LENGTH_SHORT).show();
        try {
            JSONObject jsonObject = new JSONObject(result);
            Weather.week = jsonObject.getString(Protocol.WEEK);
            Weather.day = jsonObject.getInt(Protocol.DAY);
            Weather.month = jsonObject.getInt(Protocol.MONTH);
            Weather.year = jsonObject.getInt(Protocol.YEAR);
            Weather.weather = jsonObject.getString(Protocol.WEATHER);
            Weather.temperature = jsonObject.getString(Protocol.TEMPERATURE);
            Weather.notice = jsonObject.getString(Protocol.NOTICE);
        }catch (JSONException e){
            e.printStackTrace();
        }
        weatherHandler.sendMessage(new Message());
    }
    //切换菜单
    private void toggleScene() {
        if(changed) {
            viewSwitcher.setDisplayedChild(1);
            findViewById(R.id.Arrow_bt).setBackgroundResource(R.drawable.arrow_selector);
        }
        else {
            viewSwitcher.setDisplayedChild(0);
            findViewById(R.id.Menu_include).setVisibility(View.VISIBLE);
            findViewById(R.id.Arrow_bt).setBackgroundResource(R.drawable.arrow_back_selector);
        }
        changed = !changed;
    }
    /*******************功能函数**********************/
    /*******************消息响应**********************/
    //打开日历
    public void openCalendar(View view){
//        totalDiaryNum = preferences.getInt(SaveActivity.TOTAL_DIARY_NUM,0);
//        max_index = preferences.getInt(date + SaveActivity.MAX_INDEX,-1);
        Button mImage = findViewById(R.id.Calendar_bt);
        ActivityOptions compat = ActivityOptions.makeScaleUpAnimation(mImage,mImage.getWidth() / 2, mImage.getHeight() / 2, 0, 0);
        Intent i = new Intent(this,CalendarActivity.class);
//        i.putExtra(Protocl.DATE,date);
//        i.putExtra(Protocl.WEATHER,weather);
//        i.putExtra(Protocl.DAY,day);
        startActivity(i,compat.toBundle());
    }
    //新日记
    public void openDiary(View view){
        Button mImage = findViewById(R.id.New_Diary_bt);
        ActivityOptions compat = ActivityOptions.makeScaleUpAnimation(mImage,mImage.getWidth() / 2, mImage.getHeight() / 2, 0, 0);
        Intent i = new Intent(this,DiaryActivity.class);
        i.putExtra(Protocol.NEW_DIARY,true);
        i.putExtra(Protocol.DIARY_INDEX, totalDiaryNum);
        i.putExtra(Protocol.DIARY_PATH, "");
        startActivity(i,compat.toBundle());
    }
    //打开菜单
    public void openMenu(View view){
        toggleScene();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        setOnTimeLine();
    }

    /*******************消息响应**********************/
}
