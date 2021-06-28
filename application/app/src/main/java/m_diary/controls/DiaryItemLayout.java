package m_diary.controls;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

import m_diary.activities.DiaryActivity;


/**
 * 日记内容布局（管理分发各种文字图片处理事件）
 */
public class DiaryItemLayout extends RelativeLayout implements View.OnTouchListener {

    private Context mContext;
    private Paint mPaint;
    //文字相关
    public String colorCode;
    public String typeFaceStr;
    public MyTextControl currentView = null;
    //用于保存创建的TextView
    public ArrayList<MyTextControl> list = new ArrayList<>();
    //记录当前操作的贴纸对象
    private Sticker mStick;
    public DiaryItemLayout(Context context) {
        super(context);
        init(context);
    }
    public DiaryItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    public DiaryItemLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    //初始化操作
    private void init(Context context) {
        this.mContext = context;
        //设置触摸监听
        setOnTouchListener(this);
        setWillNotDraw(false);
    }
    public Paint getPaint() {
        if (mPaint == null) {
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setColor(Color.BLACK);
            mPaint.setStrokeWidth(2);
        }
        return mPaint;
    }
    public void setPaint(Paint mPaint) {
        this.mPaint = mPaint;
    }
    /**
     * 添加贴纸
     *
     * @param sticker
     */
    public void addSticker(Sticker sticker) {
        int size = StickerManager.getInstance().getStickerList().size();
        if (size < 9) {
            StickerManager.getInstance().addSticker(sticker);
            StickerManager.getInstance().setFocusSticker(sticker);
            invalidate();
        } else {
            Toast.makeText(mContext, "贴纸最大数量不能超过9个", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 移除贴纸（只有在贴纸聚焦的时候才可以删除，避免误触碰）
     *
     * @param sticker
     */
    public void removeSticker(Sticker sticker) {
        if (sticker.isFocus()) {
            StickerManager.getInstance().removeSticker(sticker);
            invalidate();
        }
    }
    /**
     * 清空
     */
    public void removeAll() {
        this.removeAllViews();
        list.clear();
        StickerManager.getInstance().removeAllSticker();
        invalidate();
    }
    /***************************文字相关***************************/
    /**
     * 显示自定义对话框
     */
    public void showDialog(String message, final boolean isNew,String myColorCode, String myTypeFace) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("请输入内容:");
        View v=View.inflate(mContext, R.layout.activity_alert,null);
        builder.setView(v);
        builder.setCancelable(true);
        EditText editText=v.findViewById(R.id.et);
        editText.setText(message);
        colorCode = myColorCode;
        typeFaceStr = myTypeFace;
        if(isNew) {
            editText.setTextColor(Color.BLACK);
        }
        else{
            editText.setTextColor(Color.parseColor(colorCode));
        }
        v.findViewById(R.id.blackButton).setOnClickListener(v14 -> {
            editText.setTextColor(Color.BLACK);
            colorCode="#000000";
        });
        v.findViewById(R.id.brownButton).setOnClickListener(v15 -> {
            editText.setTextColor(Color.parseColor("#550D01"));
            colorCode="#550D01";
        });
        v.findViewById(R.id.lightredButton).setOnClickListener(v16 -> {
            editText.setTextColor(Color.parseColor("#810110"));
            colorCode="#810110";
        });
        v.findViewById(R.id.orangeButton).setOnClickListener(v17 -> {
            editText.setTextColor(Color.parseColor("#EA6D10"));
            colorCode="#EA6D10";
        });
        v.findViewById(R.id.shenlanButton).setOnClickListener(v18 -> {
            editText.setTextColor(Color.parseColor("#001B5E"));
            colorCode="#001B5E";
        });
        v.findViewById(R.id.lanlvButton).setOnClickListener(v19 -> {
            editText.setTextColor(Color.parseColor("#033853"));
            colorCode="#033853";
        });
        v.findViewById(R.id.shenlvButton).setOnClickListener(v110 -> {
            editText.setTextColor(Color.parseColor("#033510"));
            colorCode="#033510";
        });
        v.findViewById(R.id.shenziButton).setOnClickListener(v111 -> {
            editText.setTextColor(Color.parseColor("#2B055C"));
            colorCode="#2B055C";
        });
        v.findViewById(R.id.grayButton).setOnClickListener(v112 -> {
            editText.setTextColor(Color.parseColor("#717073"));
            colorCode="#717073";
        });
        v.findViewById(R.id.shenfenButton).setOnClickListener(v113 -> {
            editText.setTextColor(Color.parseColor("#FF6A6A"));
            colorCode="#FF6A6A";
        });
        v.findViewById(R.id.midredButton).setOnClickListener(v114 -> {
            editText.setTextColor(Color.parseColor("#FC4B2B"));
            colorCode="#FC4B2B";
        });
        v.findViewById(R.id.yellowButton).setOnClickListener(v115 -> {
            editText.setTextColor(Color.parseColor("#FFB90F"));
            colorCode="#FFB90F";
        });
        v.findViewById(R.id.midblueButton).setOnClickListener(v116 -> {
            editText.setTextColor(Color.parseColor("#4D97D5"));
            colorCode="#4D97D5";
        });
        v.findViewById(R.id.lan2Button).setOnClickListener(v117 -> {
            editText.setTextColor(Color.parseColor("#196A81"));
            colorCode="#196A81";
        });
        v.findViewById(R.id.caolvButton).setOnClickListener(v118 -> {
            editText.setTextColor(Color.parseColor("#3C7602"));
            colorCode="#3C7602";
        });
        v.findViewById(R.id.midpurpleButton).setOnClickListener(v119 -> {
            editText.setTextColor(Color.parseColor("#A0509D"));
            colorCode="#A0509D";
        });
        v.findViewById(R.id.whiteButton).setOnClickListener(v120 -> {
            editText.setTextColor(Color.parseColor("#FFFFFF"));
            colorCode="#FFFFFF";
        });
        v.findViewById(R.id.pinkButton).setOnClickListener(v121 -> {
            editText.setTextColor(Color.parseColor("#FFC1C1"));
            colorCode="#FFC1C1";
        });
        v.findViewById(R.id.rouseButton).setOnClickListener(v122 -> {
            editText.setTextColor(Color.parseColor("#FFD39B"));
            colorCode="#FFD39B";
        });
        v.findViewById(R.id.qianyellowButton).setOnClickListener(v123 -> {
            editText.setTextColor(Color.parseColor("#FFF68F"));
            colorCode="#FFF68F";
        });
        v.findViewById(R.id.qianlanButton).setOnClickListener(v124 -> {
            editText.setTextColor(Color.parseColor("#B0E2FF"));
            colorCode="#B0E2FF";
        });
        v.findViewById(R.id.qianlanlvButton).setOnClickListener(v125 -> {
            editText.setTextColor(Color.parseColor("#7AC5CD"));
            colorCode="#7AC5CD";
        });
        v.findViewById(R.id.qianlvButton).setOnClickListener(v126 -> {
            editText.setTextColor(Color.parseColor("#C4E48D"));
            colorCode="#C4E48D";
        });
        v.findViewById(R.id.qianzifenButton).setOnClickListener(v127 -> {
            editText.setTextColor(Color.parseColor("#FFF0F5"));
            colorCode="#FFF0F5";
        });
        //字体
        v.findViewById(R.id.kaiti).setOnClickListener(v128 -> {
            Typeface typeface= Typeface.createFromAsset(mContext.getAssets(),"fonts/楷体.ttf");
            editText.setTypeface(typeface);
            typeFaceStr="楷体.ttf";
        });
        v.findViewById(R.id.zhonghei).setOnClickListener(v129 -> {
            Typeface typeface= Typeface.createFromAsset(mContext.getAssets(),"fonts/华康中黑字体.TTF");
            editText.setTypeface(typeface);
            typeFaceStr="华康中黑字体.TTF";
        });
        v.findViewById(R.id.shaonv).setOnClickListener(v130 -> {
            Typeface typeface= Typeface.createFromAsset(mContext.getAssets(),"fonts/华康少女字体.ttf");
            editText.setTypeface(typeface);
            typeFaceStr="华康少女字体.ttf";
        });
        v.findViewById(R.id.youyuan).setOnClickListener(v1 -> {
            Typeface typeface= Typeface.createFromAsset(mContext.getAssets(),"fonts/幼圆.ttf");
            editText.setTypeface(typeface);
            typeFaceStr="幼圆.ttf";
        });
        v.findViewById(R.id.katong).setOnClickListener(v12 -> {
            Typeface typeface= Typeface.createFromAsset(mContext.getAssets(),"fonts/方正卡通简体.ttf");
            editText.setTypeface(typeface);
            typeFaceStr="方正卡通简体.ttf";
        });
        v.findViewById(R.id.guli).setOnClickListener(v13 -> {
            Typeface typeface= Typeface.createFromAsset(mContext.getAssets(),"fonts/方正古隶.ttf");
            editText.setTypeface(typeface);
            typeFaceStr="方正古隶.ttf";
        });
        v.findViewById(R.id.qiti).setOnClickListener(v131 -> {
            Typeface typeface= Typeface.createFromAsset(mContext.getAssets(),"fonts/方正启体简体.ttf");
            editText.setTypeface(typeface);
            typeFaceStr="方正启体简体.ttf";
        });
        v.findViewById(R.id.liuxing).setOnClickListener(v132 -> {
            Typeface typeface= Typeface.createFromAsset(mContext.getAssets(),"fonts/方正流行体简体.ttf");
            editText.setTypeface(typeface);
            typeFaceStr="方正流行体简体.ttf";
        });
        v.findViewById(R.id.shoujin).setOnClickListener(v133 -> {
            Typeface typeface= Typeface.createFromAsset(mContext.getAssets(),"fonts/瘦金体.ttf");
            editText.setTypeface(typeface);
            typeFaceStr="瘦金体.ttf";
        });
        v.findViewById(R.id.lishu).setOnClickListener(v134 -> {
            Typeface typeface= Typeface.createFromAsset(mContext.getAssets(),"fonts/隶书.ttf");
            editText.setTypeface(typeface);
            typeFaceStr="隶书.ttf";
        });
        v.findViewById(R.id.wawati).setOnClickListener(v135 -> {
            Typeface typeface= Typeface.createFromAsset(mContext.getAssets(),"fonts/华康娃娃体.TTF");
            editText.setTypeface(typeface);
            typeFaceStr="华康娃娃体.TTF";
        });
        v.findViewById(R.id.pingguolihei).setOnClickListener(v136 -> {
            Typeface typeface= Typeface.createFromAsset(mContext.getAssets(),"fonts/苹果丽黑.ttf");
            editText.setTypeface(typeface);
            typeFaceStr="苹果丽黑.ttf";
        });
        v.findViewById(R.id.yahei).setOnClickListener(v137 -> {
            Typeface typeface= Typeface.createFromAsset(mContext.getAssets(),"fonts/微软雅黑14M.ttf");
            editText.setTypeface(typeface);
            typeFaceStr="微软雅黑14M.ttf";
        });
        builder.setPositiveButton("确定", (dialog, which) -> {
            String content = editText.getText().toString();
            if (TextUtils.isEmpty(content)) {
                Toast.makeText(mContext, "您没有任何输入!", Toast.LENGTH_SHORT).show();
            } else {
                if (isNew) {
                    addTextView(null, 150, 500, content,colorCode, typeFaceStr, 0);
                } else {
                    addTextView(currentView, currentView.getX(), currentView.getY(), content, colorCode, typeFaceStr,currentView.getRotation());
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }
    //添加一个TextView到界面上
    @SuppressLint("ClickableViewAccessibility")
    public void addTextView(MyTextControl tv, float x, float y, String content, String colorCode, String typeFaceStr, float rotate) {
        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/" + typeFaceStr);
        float mTextSize = 35;
        if (tv == null) {
            currentView = new MyTextControl(mContext);
            currentView.setEms(8);
            currentView.setTag(System.currentTimeMillis());
            //保存并添加到list中
            list.add(currentView);
            addView(currentView);
        } else {
            currentView = tv;
        }
        currentView.setTextSize(mTextSize);
        currentView.setX(x);
        currentView.setY(y);
        currentView.setTextColor(Color.parseColor(colorCode));
        currentView.setText(content);
        currentView.setTypeface(typeface);
        currentView.typeFaceStr = typeFaceStr;
        currentView.colorCode = colorCode;
        currentView.setScaleX(currentView.scale);
        currentView.setScaleY(currentView.scale);
        currentView.setRotation(rotate);
    }
    //获取被触摸的控件
    private MyTextControl getCurrentView(){
        for(int i = 0; i < list.size(); i++){
            if(list.get(i).touched){
                return list.get(i);
            }
        }
        return null;
    }
    //图片绘制
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        List<Sticker> stickerList = StickerManager.getInstance().getStickerList();
        Sticker focusSticker = null;
        for (int i = 0; i < stickerList.size(); i++) {
            Sticker sticker = stickerList.get(i);
            if (sticker.isFocus()) {
                focusSticker = sticker;
            } else {
                sticker.onDraw(canvas, getPaint());
            }
        }
        if (focusSticker != null) {
            focusSticker.onDraw(canvas, getPaint());
        }
    }
    //消息响应
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(DiaryActivity.editable) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_DOWN: {
                    //单指是否触摸到控件
                    currentView = getCurrentView();
                    if (currentView == null) {
                        if (event.getPointerCount() == 2) {
                            //处理双指触摸屏幕，第一指没有触摸到贴纸，第二指触摸到贴纸情况
                            currentView = getCurrentView();
                        }
                    }
                    //判断是否按到删除按钮
                    mStick = StickerManager.getInstance().getDelButton(event.getX(), event.getY());
                    if (mStick != null) {
                        removeSticker(mStick);
                        mStick = null;
                    }
                    //单指是否触摸到贴纸
                    mStick = StickerManager.getInstance().getSticker(event.getX(), event.getY());
                    if (mStick == null) {
                        if (event.getPointerCount() == 2) {
                            //处理双指触摸屏幕，第一指没有触摸到贴纸，第二指触摸到贴纸情况
                            mStick = StickerManager.getInstance().getSticker(event.getX(1), event.getY(1));
                        }
                    }
                    if (mStick != null) {
                        StickerManager.getInstance().setFocusSticker(mStick);
                    }
                }break;
                case MotionEvent.ACTION_UP:{
                    if (currentView != null) {
                        currentView.touched = false;
                    }
                }
                default:
                    break;
            }
            if (currentView != null) {
                currentView.onTouch(event);
            }
            if (mStick != null && currentView ==null) {
                mStick.onTouch(event);
            } else {
                StickerManager.getInstance().clearAllFocus();
            }
            invalidate();
        }
        return true;
    }
}
