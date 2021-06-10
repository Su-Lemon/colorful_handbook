package m_diary.controls;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.myapplication.R;

import m_diary.activities.DiaryActivity;
import m_diary.assets.Diary;
import m_diary.utils.IOUtils;
import m_diary.utils.Protocol;
import m_diary.utils.UserManager;

public class TimeLine extends ConstraintLayout {

    private Context context;
    private Button  diaryButton;
    private ImageView timeLineBG;
    private TextView titleView;
    private TextView dateView;
    private TextView weekView;
    private TextView weatherView;
    private String title;
    private String week;
    private String weather;
    private String date;
    private String savePath;
    private int index;
    private LinearLayout linearLayout;
    private DialogInterface.OnClickListener confirm;
    private DialogInterface.OnClickListener cancel;
    private DialogInterface.OnClickListener delete;

    //构造函数
    public TimeLine(Context context, Diary diary){
        super(context);
        LayoutInflater.from(context).inflate(R.layout.timelinebt,this,true);
        this.context = context;
        date = diary.getDate();
        week = diary.week;
        title = diary.title;
        weather = diary.weather;
        savePath = diary.savePath;
        index = diary.index;
        initial_content_View();
    }
    public TimeLine(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public TimeLine(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    /*******************初始化函数**********************/
    //初始化控件
    private void initial_content_View(){
        diaryButton = findViewById(R.id.diaryBT);
        timeLineBG = findViewById(R.id.timeLineBG);
        titleView = findViewById(R.id.diaryName);
        dateView = findViewById(R.id.diaryDate);
        weekView = findViewById(R.id.diaryWeek);
        weatherView = findViewById(R.id.diaryWeather);
        initialButtonListener();
        setData();
    }
    //设置控件内容
    private void setData(){
        if(weather.contains("雨")){
            timeLineBG.setImageResource(R.drawable.rain);
        }
        else if(weather.contains("晴")){
            timeLineBG.setImageResource(R.drawable.sunny);
        }
        else if(weather.contains("多云")){
            timeLineBG.setImageResource(R.drawable.cloudy);
        }
        else if(weather.contains("阴")){
            timeLineBG.setImageResource(R.drawable.overcast);
        }
        else if(weather.contains("雪")){

        }
        weatherView.setText(weather);
        titleView.setText(title);
        dateView.setText(date);
        weekView.setText(week);
    }
    /*******************初始化函数**********************/
    /*******************消息响应**********************/
    private void initial_dialog(){
        //确定按钮
        confirm= (arg0, arg1) -> {
            //这里删除日记
            linearLayout.removeView(TimeLine.this);
            String diaryDirPath = Environment.getExternalStorageDirectory() + "/Diary_Data/" + UserManager.username + "/" + index;
            IOUtils.deleteFile(diaryDirPath);
        };
        //删除日记
        delete = (dialog, which) -> {
            AlertDialog.Builder alert_dialog_builder = new AlertDialog.Builder(context);
            alert_dialog_builder.setMessage("确定删除?");
            alert_dialog_builder.setPositiveButton("是", confirm);
            alert_dialog_builder.setNegativeButton("否", cancel);
            AlertDialog alert_dialog = alert_dialog_builder.create();
            alert_dialog.show();
        };
        //取消
        cancel= (arg0, arg1) -> arg0.cancel();
    }
    private void initialButtonListener(){
        diaryButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityOptions compat = ActivityOptions.makeScaleUpAnimation(diaryButton,diaryButton.getWidth() / 2, diaryButton.getHeight() / 2, 0, 0);
                Intent i = new Intent(context, DiaryActivity.class);
                i.putExtra(Protocol.DIARY_PATH, savePath);
                i.putExtra(Protocol.DIARY_INDEX, index);
                i.putExtra(Protocol.NEW_DIARY,false);
                context.startActivity(i,compat.toBundle());
            }
        });
        diaryButton.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder alert_dialog_builder = new AlertDialog.Builder(context);
                alert_dialog_builder.setMessage("删除这个日记?");
                alert_dialog_builder.setPositiveButton("删除", delete);
                alert_dialog_builder.setNegativeButton("取消", cancel);
                AlertDialog alert_dialog = alert_dialog_builder.create();
                alert_dialog.show();
                return true;
            }
        });
        initial_dialog();
    }
    /*******************消息响应**********************/


}
