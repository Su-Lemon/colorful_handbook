package m_diary.activities;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

import m_diary.controls.MyTextControl;


public class MyRelativeLayout extends RelativeLayout implements View.OnTouchListener {
    //测试
    private Context context;
    private TextViewParams tvParams;
    private MyTextControl currentView = null;


    //用于保存创建的TextView
    public ArrayList<MyTextControl> list = new ArrayList<>();
    public List<TextViewParams> listTvParams;

    private float textSize = 0;

    private float mAngle;
    private float scale;
    private float defaultAngle;

    public int color;
    private int style;
    private String colorCode;
    private String typeFaceStr;
    /**
     * 处理View上的单击事件 用以添加TextView
     */
    public MyRelativeLayout(Context context) {
        this(context, null, 0);
    }
    public MyRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public MyRelativeLayout(final Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MyRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    public void removeAllThings(){
        this.removeAllViews();
        list.clear();
        listTvParams.clear();
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void setBackGroundBitmap(Bitmap bitmap) {
        setBackground(new BitmapDrawable(bitmap));
    }
    private boolean isLongPressed(float lastX,float lastY, float thisX,float thisY, long lastDownTime,long thisEventTime, long longPressTime){
        float offsetX = Math.abs(thisX - lastX);
        float offsetY = Math.abs(thisY - lastY);
        long intervalTime = thisEventTime - lastDownTime;
        if(offsetX <=10 && offsetY<=10 && intervalTime >= longPressTime){
            return true;
        }
        return false;
    }

    //添加一个TextView到界面上
    @SuppressLint("ClickableViewAccessibility")
    public void addTextView(MyTextControl tv, float x, float y, String content, float mTextSize, float rotate) {
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/" + typeFaceStr);
        if (tv == null) {
            currentView = new MyTextControl(context);
            currentView.setEms(8);
            currentView.setTag(System.currentTimeMillis());
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            currentView.setLayoutParams(params);
            currentView.setTextSize(mTextSize);
            currentView.setRotation(rotate);
            currentView.setX(x - currentView.getWidth());
            currentView.setY(y - currentView.getHeight());
            updateTextViewParams(currentView,rotate,scale);
            list.add(currentView);
            //保存并添加到list中
            saveTextViewparams(currentView);
            addView(currentView);
        } else {
            currentView = tv;
            updateTextViewParams(currentView,rotate,scale);
        }
        currentView.setTextColor(Color.parseColor(colorCode));
        currentView.setText(content);
        currentView.setTypeface(typeface);
        currentView.typeFaceStr = typeFaceStr;
        currentView.colorCode = colorCode;

    }
    /**
     * 初始化操作
     */
    public void init() {
        setOnTouchListener(this);
        list = new ArrayList<>();
        listTvParams = new ArrayList<>();
    }
    /**
     * 显示自定义对话框
     */
    public void showDialog(String message, final boolean isNew,String myColorCode, String myTypeFace) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("请输入内容:");
        View v=View.inflate(context, R.layout.activity_alert,null);
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
        v.findViewById(R.id.blackButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setTextColor(Color.BLACK);
                colorCode="#000000";
            }
        });
        v.findViewById(R.id.brownButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setTextColor(Color.parseColor("#550D01"));
                colorCode="#550D01";
            }
        });
        v.findViewById(R.id.lightredButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setTextColor(Color.parseColor("#810110"));
                colorCode="#810110";
            }
        });
        v.findViewById(R.id.orangeButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setTextColor(Color.parseColor("#EA6D10"));
                colorCode="#EA6D10";
            }
        });
        v.findViewById(R.id.shenlanButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setTextColor(Color.parseColor("#001B5E"));
                colorCode="#001B5E";
            }
        });
        v.findViewById(R.id.lanlvButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setTextColor(Color.parseColor("#033853"));
                colorCode="#033853";
            }
        });
        v.findViewById(R.id.shenlvButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setTextColor(Color.parseColor("#033510"));
                colorCode="#033510";
            }
        });
        v.findViewById(R.id.shenziButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setTextColor(Color.parseColor("#2B055C"));
                colorCode="#2B055C";
            }
        });
        v.findViewById(R.id.grayButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setTextColor(Color.parseColor("#717073"));
                colorCode="#717073";
            }
        });
        v.findViewById(R.id.shenfenButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setTextColor(Color.parseColor("#FF6A6A"));
                colorCode="#FF6A6A";
            }
        });
        v.findViewById(R.id.midredButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setTextColor(Color.parseColor("#FC4B2B"));
                colorCode="#FC4B2B";
            }
        });
        v.findViewById(R.id.yellowButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setTextColor(Color.parseColor("#FFB90F"));
                colorCode="#FFB90F";
            }
        });
        v.findViewById(R.id.midblueButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setTextColor(Color.parseColor("#4D97D5"));
                colorCode="#4D97D5";
            }
        });
        v.findViewById(R.id.lan2Button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setTextColor(Color.parseColor("#196A81"));
                colorCode="#196A81";
            }
        });
        v.findViewById(R.id.caolvButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setTextColor(Color.parseColor("#3C7602"));
                colorCode="#3C7602";
            }
        });
        v.findViewById(R.id.midpurpleButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setTextColor(Color.parseColor("#A0509D"));
                colorCode="#A0509D";
            }
        });
        v.findViewById(R.id.whiteButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setTextColor(Color.parseColor("#FFFFFF"));
                colorCode="#FFFFFF";
            }
        });
        v.findViewById(R.id.pinkButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setTextColor(Color.parseColor("#FFC1C1"));
                colorCode="#FFC1C1";
            }
        });
        v.findViewById(R.id.rouseButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setTextColor(Color.parseColor("#FFD39B"));
                colorCode="#FFD39B";
            }
        });
        v.findViewById(R.id.qianyellowButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setTextColor(Color.parseColor("#FFF68F"));
                colorCode="#FFF68F";
            }
        });
        v.findViewById(R.id.qianlanButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setTextColor(Color.parseColor("#B0E2FF"));
                colorCode="#B0E2FF";
            }
        });
        v.findViewById(R.id.qianlanlvButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setTextColor(Color.parseColor("#7AC5CD"));
                colorCode="#7AC5CD";
            }
        });
        v.findViewById(R.id.qianlvButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setTextColor(Color.parseColor("#C4E48D"));
                colorCode="#C4E48D";
            }
        });
        v.findViewById(R.id.qianzifenButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setTextColor(Color.parseColor("#FFF0F5"));
                colorCode="#FFF0F5";
            }
        });
        //字体
        v.findViewById(R.id.kaiti).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Typeface typeface= Typeface.createFromAsset(context.getAssets(),"fonts/楷体.ttf");
                editText.setTypeface(typeface);
                typeFaceStr="楷体.ttf";
            }
        });
        v.findViewById(R.id.zhonghei).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                style=2;
                Typeface typeface= Typeface.createFromAsset(context.getAssets(),"fonts/华康中黑字体.TTF");
                editText.setTypeface(typeface);
                typeFaceStr="华康中黑字体.TTF";
            }
        });
        v.findViewById(R.id.shaonv).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Typeface typeface= Typeface.createFromAsset(context.getAssets(),"fonts/华康少女字体.ttf");
                editText.setTypeface(typeface);
                typeFaceStr="华康少女字体.ttf";
            }
        });
        v.findViewById(R.id.youyuan).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Typeface typeface= Typeface.createFromAsset(context.getAssets(),"fonts/幼圆.ttf");
                editText.setTypeface(typeface);
                typeFaceStr="幼圆.ttf";
            }
        });
        v.findViewById(R.id.katong).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Typeface typeface= Typeface.createFromAsset(context.getAssets(),"fonts/方正卡通简体.ttf");
                editText.setTypeface(typeface);
                typeFaceStr="方正卡通简体.ttf";
            }
        });
        v.findViewById(R.id.guli).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Typeface typeface= Typeface.createFromAsset(context.getAssets(),"fonts/方正古隶.ttf");
                editText.setTypeface(typeface);
                typeFaceStr="方正古隶.ttf";
            }
        });
        v.findViewById(R.id.qiti).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Typeface typeface= Typeface.createFromAsset(context.getAssets(),"fonts/方正启体简体.ttf");
                editText.setTypeface(typeface);
                typeFaceStr="方正启体简体.ttf";
            }
        });
        v.findViewById(R.id.liuxing).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Typeface typeface= Typeface.createFromAsset(context.getAssets(),"fonts/方正流行体简体.ttf");
                editText.setTypeface(typeface);
                typeFaceStr="方正流行体简体.ttf";
            }
        });
        v.findViewById(R.id.shoujin).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Typeface typeface= Typeface.createFromAsset(context.getAssets(),"fonts/瘦金体.ttf");
                editText.setTypeface(typeface);
                typeFaceStr="瘦金体.ttf";
            }
        });
        v.findViewById(R.id.lishu).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Typeface typeface= Typeface.createFromAsset(context.getAssets(),"fonts/隶书.ttf");
                editText.setTypeface(typeface);
                typeFaceStr="隶书.ttf";
            }
        });
        v.findViewById(R.id.wawati).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Typeface typeface= Typeface.createFromAsset(context.getAssets(),"fonts/华康娃娃体.TTF");
                editText.setTypeface(typeface);
                typeFaceStr="华康娃娃体.TTF";
            }
        });
        v.findViewById(R.id.pingguolihei).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Typeface typeface= Typeface.createFromAsset(context.getAssets(),"fonts/苹果丽黑.ttf");
                editText.setTypeface(typeface);
                typeFaceStr="苹果丽黑.ttf";
            }
        });
        v.findViewById(R.id.yahei).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Typeface typeface= Typeface.createFromAsset(context.getAssets(),"fonts/微软雅黑14M.ttf");
                editText.setTypeface(typeface);
                typeFaceStr="微软雅黑14M.ttf";
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String content = editText.getText().toString();
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(context, "您没有任何输入!", Toast.LENGTH_SHORT).show();
                } else {
                    if (isNew) {
                        addTextView(null, 150, 500, content, 35, 0);
                    } else {
                        addTextView(currentView, currentView.getX(), currentView.getY(), content, 35,currentView.getRotation());
                    }
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
    /**
     * 对控件进行参数的更新操作
     *
     * @param tv
     */
    public void updateTextViewParams(MyTextControl tv, float rotation, float scale) {
        for (int i = 0; i < listTvParams.size(); i++) {
            TextViewParams param = new TextViewParams();
            if (tv.getTag().toString().equals(listTvParams.get(i).getTag())) {
                param.setRotation(rotation);
                param.setTextSize(tv.getTextSize() / 2);
                param.setMidPoint(getViewMidPoint(tv));
                param.setScale(scale);
                textSize = tv.getTextSize() / 2;
                param.setWidth(tv.getWidth());
                param.setHeight(tv.getHeight());
                param.setX(tv.getX());
                param.setY(tv.getY());
                param.setTag(listTvParams.get(i).getTag());
                param.setContent(tv.getText().toString());
                param.setTextColor(color);
                param.setStyle(style);
                listTvParams.set(i, param);
                return;
            }
        }
    }
    /**
     * //对状态进行保存操作
     *
     * @param textView
     * @return
     */
    public void saveTextViewparams(MyTextControl textView) {
        if (textView != null) {
            tvParams = new TextViewParams();
            tvParams.setRotation(0);
            tvParams.setTextSize(textView.getTextSize() / 2);
            tvParams.setX(textView.getX());
            tvParams.setY(textView.getY());
            tvParams.setWidth(textView.getWidth());
            tvParams.setHeight(textView.getHeight());
            tvParams.setContent(textView.getText().toString());
            tvParams.setMidPoint(getViewMidPoint(textView));
            tvParams.setScale(1);
            tvParams.setTag(String.valueOf((long) textView.getTag()));
            tvParams.setRotation(mAngle);
            tvParams.setTextColor(color);
            tvParams.setStyle(style);
            listTvParams.add(tvParams);
        }
    }
    /**
     * 根据TextView获取到该TextView的配置文件
     *
     * @param tv
     * @return
     */
    public TextViewParams getTextViewParams(MyTextControl tv) {
        for (int i = 0; i < listTvParams.size(); i++) {
            if (listTvParams.get(i).getTag().equals(String.valueOf((long) tv.getTag()))) {
                return listTvParams.get(i);
            }
        }
        return null;
    }
    //返回所有的TextView的参数
    public List<TextViewParams> getListTvParams() {
        List<TextViewParams> newImageList = new ArrayList<>();
        newImageList.addAll(listTvParams);
        return newImageList;
    }
    public void setListTvParams(List<TextViewParams> listTvParams) {
        List<TextViewParams> tempList = new ArrayList<>();
        tempList.addAll(listTvParams);
        this.listTvParams = tempList;
    }
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }
    /**
     * 对控件重新赋值 防止出现错乱
     *
     * @param para
     */
    public void setTextViewParams(TextViewParams para) {
        scale = para.getScale();
        textSize = para.getTextSize();
        mAngle = para.getRotation();
        defaultAngle = mAngle;
        Log.d("HHH", "defaultAngle " + defaultAngle);
    }
    /**
     * 获取中间点
     *
     * @param p1
     * @param p2
     * @return 返回两个点连线的中点
     */
    public Point getMidPiont(Point p1, Point p2) {
        return new Point((p1.x + p2.x) / 2, (p1.y + p2.y) / 2);
    }
    /**
     * 获取中间点
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public Point getMidPiont(int x1, int y1, int x2, int y2) {
        return new Point((x1 + x2) / 2, (y1 + y2) / 2);
    }
    /**
     * 该方法用于回一个View的终点坐标
     * 如果该View不存在则返回(0,0)
     *
     * @param view
     * @return
     */
    public Point getViewMidPoint(View view) {
        Point point = new Point();
        if (view != null) {
            float xx = view.getX();
            float yy = view.getY();
            int center_x = (int) (xx + view.getWidth() / 2);
            int center_y = (int) (yy + view.getHeight() / 2);
            point.set(center_x, center_y);
        } else {
            point.set(0, 0);
        }
        return point;
    }
    /**
     * 该方法用于判断某一个点是否某一个范围中
     *
     * @param width
     * @param height
     * @param startX
     * @param startY
     * @param point
     * @return
     */
    public boolean ifIsOnView(int width, int height, int startX, int startY, Point point) {
        return (point.x < (width + startX) && point.x > startX && point.y < (startY + height) && point.y > startY) ? true : false;
    }
    /**
     * 该方法用于判断某一个点是否在View上
     *
     * @param view
     * @param point
     * @return
     */
    public boolean ifIsOnView(View view, Point point) {
        int w = view.getWidth();
        int h = view.getHeight();
        float x = view.getX();
        float y = view.getY();
        return (point.x < (w + x) && point.x > x && point.y < (y + h) && point.y > y) ? true : false;
    }
    /**
     * 计算刚开始触摸的两个点构成的直线和滑动过程中两个点构成直线的角度
     *
     * @param fX  初始点一号x坐标
     * @param fY  初始点一号y坐标
     * @param sX  初始点二号x坐标
     * @param sY  初始点二号y坐标
     * @param nfX 终点一号x坐标
     * @param nfY 终点一号y坐标
     * @param nsX 终点二号x坐标
     * @param nsY 终点二号y坐标
     * @return 构成的角度值
     */
    public float angleBetweenLines(float fX, float fY, float sX, float sY, float nfX, float nfY, float nsX, float nsY) {
        float angle1 = (float) Math.atan2((fY - sY), (fX - sX));
        float angle2 = (float) Math.atan2((nfY - nsY), (nfX - nsX));
        float angle = ((float) Math.toDegrees(angle1 - angle2)) % 360;
        if (angle < -180.f) angle += 360.0f;
        if (angle > 180.f) angle -= 360.0f;
        return -angle;
    }
    //缩放实现
    public void zoom(float f) {
        currentView.setTextSize(textSize *= f);
    }
    /**
     * 计算两点之间的距离
     *
     * @param event
     * @return 两点之间的距离
     */
    public float spacing(MotionEvent event, int ID1, int ID2) {
        float x = event.getX(ID1) - event.getX(ID2);
        float y = event.getY(ID1) - event.getY(ID2);
        return (float) Math.sqrt(x * x + y * y);
    }
    /**
     * 求两个一直点的距离
     *
     * @param p1
     * @param p2
     * @return
     */
    public double spacing(Point p1, Point p2) {
        double x = p1.x - p2.x;
        double y = p1.y - p2.y;
        return Math.sqrt((x * x + y * y));
    }
    /**
     * 返回两个点之间的距离
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public double spacing(float x1, float y1, float x2, float y2) {
        double x = x1 - x2;
        double y = y1 - y2;
        return Math.sqrt((x * x + y * y));
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

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(DiaryActivity.editable) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_DOWN:
                    //单指是否触摸到控件
                    currentView = getCurrentView();
                    if (currentView == null) {
                        if (event.getPointerCount() == 2) {
                            //处理双指触摸屏幕，第一指没有触摸到贴纸，第二指触摸到贴纸情况
                            currentView = getCurrentView();
                        }
                    }
                    break;
                default:
                    break;
            }
            if (currentView != null) {
                currentView.onTouch(event);
            }
            invalidate();
        }
        return true;
    }

    /**
     * Created by cretin on 15/12/21
     * 用于记录每个TextView的状态
     */
    public class TextViewParams {
        private String tag;
        private float textSize;
        private Point midPoint;
        private float rotation;
        private float scale;
        private String content;
        private int width;
        private int height;
        private float x;
        private float y;
        public int textColor;
        private  String myColorr;
        public int textStyle;
        private String myStyle;

        public String getTextColor() {
            return myColorr;
        }


        public void setTextColor(int color) {
            textColor = color;
            myColorr=colorCode;
        }

        public void setStyle(int style){
            textStyle=style;
            myStyle=typeFaceStr;
        }

        public String getStyle(){
            return myStyle;
        }

        @Override
        public String toString() {
            return "TextViewParams{" +
                    "tag='" + tag + '\'' +
                    ", textSize=" + textSize +
                    ", midPoint=" + midPoint +
                    ", rotation=" + rotation +
                    ", scale=" + scale +
                    ", content='" + content + '\'' +
                    ", width=" + width +
                    ", height=" + height +
                    ", x=" + x +
                    ", y=" + y +
                    ", textColor="+myColorr+
                    ", style="+myStyle+
                    '}';
        }

        public String getTag() {
            return tag;
        }

        public int getColor(){ return textColor; }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public float getTextSize() {
            return textSize;
        }

        public void setTextSize(float textSize) {
            this.textSize = textSize;
        }

        public Point getMidPoint() {
            return midPoint;
        }

        public void setMidPoint(Point midPoint) {
            this.midPoint = midPoint;
        }

        public float getRotation() {
            return rotation;
        }

        public void setRotation(float rotation) {
            this.rotation = rotation;
        }

        public float getScale() {
            return scale;
        }

        public void setScale(float scale) {
            this.scale = scale;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public float getX() {
            return x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }
    }
    // 所有文字控件消息响应
//    class MyOnTouchListener implements OnTouchListener{
//        @Override
//        public boolean onTouch(View v, MotionEvent event) {
//            textView = (MyTextControl) v;
//            switch (event.getAction() & MotionEvent.ACTION_MASK) {
//                case MotionEvent.ACTION_DOWN:
//                    tvOneFinger = true;
//                    isClick = true;
//                    firstX = event.getX();
//                    firstY = event.getY();
//                    pointID1 = event.getPointerId(event.getActionIndex());
//                    //计算当前textView的位置和大小
//                    width = textView.getWidth();
//                    height = textView.getHeight();
//                    if(event.getX()<=(firstX+width+10)&&event.getX()>=firstX-10&&event.getY()<=(firstY+height+5)&&event.getY()>=firstY-5){
//                        mTv_width = event.getX() - textView.getX();
//                        mTv_height = event.getY() - textView.getY();
//                        mFlag=true;
//                    }
//                    break;
//                case MotionEvent.ACTION_POINTER_DOWN:
//                    tvOneFinger = false;
//                    isClick = false;
//                    pointID2 = event.getPointerId(event.getActionIndex());
//                    msX = event.getX(event.findPointerIndex(pointID1));
//                    msY = event.getY(event.findPointerIndex(pointID1));
//                    mfX = event.getX(event.findPointerIndex(pointID2));
//                    mfY = event.getY(event.findPointerIndex(pointID2));
//                    mFlag = false;
//                    mTv_widths = getMidPiont((int) mfX, (int) mfY, (int) msX, (int) msY).x - textView.getX();
//                    mTv_heights = getMidPiont((int) mfX, (int) mfY, (int) msX, (int) msY).y - textView.getY();
//                    oldDist = spacing(event, pointID1, pointID2);
//                    break;
//                case MotionEvent.ACTION_MOVE:
//                    //判断是否在移动
//                    if (spacing(firstX, firstY, event.getX(), event.getY()) > 2) {
//                        isClick = false;
//                    }
//                    //平移操作
//                    if(tvOneFinger) {
//                        textView.setX(event.getX() - mTv_width);
//                        textView.setY(event.getY() - mTv_height);
//                    }else {
//                        //旋转和缩放操作
//                        if (pointID1 != INVALID_POINTER_ID && pointID2 != INVALID_POINTER_ID) {
//                            float nfX, nfY, nsX, nsY;
//                            nsX = event.getX(event.findPointerIndex(pointID1));
//                            nsY = event.getY(event.findPointerIndex(pointID1));
//                            nfX = event.getX(event.findPointerIndex(pointID2));
//                            nfY = event.getY(event.findPointerIndex(pointID2));
//
//                            //如果两点中点在该View上,则考虑两个点也可以拖动该View操作
//                            textView.setX(getMidPiont((int) nfX, (int) nfY, (int) nsX, (int) nsY).x - mTv_widths);
//                            textView.setY(getMidPiont((int) nfX, (int) nfY, (int) nsX, (int) nsY).y - mTv_heights);
//                            //处理旋转模块
//                            mAngle = angleBetweenLines(mfX, mfY, msX, msY, nfX, nfY, nsX, nsY);
//                            textView.setRotation(mAngle);
//                            //处理缩放模块
//                            float newDist = spacing(event, pointID1, pointID2);
//                            scale = newDist / oldDist;
//
//                            if (newDist > oldDist + 1) {
//                                textView.setTextSize(textSize *= scale);
//                                oldDist = newDist;
//                            }
//                            if (newDist < oldDist - 1) {
//                                textView.setTextSize(textSize *= scale);
//                                oldDist = newDist;
//                            }
//                            //通知调用者我在旋转或者缩放
////                                if (myRelativeTouchCallBack != null)
//                            //myRelativeTouchCallBack.onTextViewMoving(textView);
//                        }
//                    }
//                    break;
//                case MotionEvent.ACTION_UP:
//                    //通知调用者我滑动结束了
//                    if (tvOneFinger && isClick) {
//                        showDialog(textView.getText().toString(), false,color);
//                    }else {
//                        updateTextViewParams((MyTextControl) v, mAngle, scale);
//                    }
//                    break;
//                case MotionEvent.ACTION_POINTER_UP:
////                            Toast.makeText(context, "5！", Toast.LENGTH_SHORT).show();
//                    updateTextViewParams((MyTextControl) v, mAngle, scale);
//                    break;
//                case MotionEvent.ACTION_CANCEL:
////                            Toast.makeText(context, "6！", Toast.LENGTH_SHORT).show();
//                    pointID1 = INVALID_POINTER_ID;
//                    pointID2 = INVALID_POINTER_ID;
//                    break;
//            }
////                    Toast.makeText(context, "end！", Toast.LENGTH_SHORT).show();
//            return true;
//        }
//    }
}

