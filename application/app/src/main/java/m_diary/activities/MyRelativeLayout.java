package m_diary.activities;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;


public class MyRelativeLayout extends RelativeLayout {
    //测试
    private ImageView iv;
    private Button btn;
    private Button btn1;

    private Context context;
    private TextView textView;
    private TextViewParams tvParams;

    private EditText editText;
    private boolean flag ;
    private boolean mflag ;
    private boolean onefinger;
    private boolean tvOneFinger;

    //记录是否为TextView上的单击事件
    private boolean isClick;

    public static final int DEFAULT_TEXTSIZE = 20;

    //左边点的偏移量
    float tv_width;
    float tv_height;
    float mTv_width;
    float mTv_height;
    float tv_widths;
    float tv_heights;
    float mTv_widths;
    float mTv_heights;

    //用于保存创建的TextView
    public List<TextView> list;
    public List<TextViewParams> listTvParams;
    public List<Double> listDistance;


    private float oldDist = 0;
    private float textSize = 0;
    private int num = 0;

    private int width;
    private int height;
    private float startX;
    private float startY;

    private static final int INVALID_POINTER_ID = -1;
    private float fX, fY, sX, sY;
    private float mfX, mfY, msX, msY;
    private int ptrID1, ptrID2;
    private int mptrID1, mptrID2;
    private float mAngle;
    private float scale;
    private MotionEvent mEvent;

    //记录第一个手指下落时的位置
    private float firstX;
    private float firstY;

    private float defaultAngle;


    //记录当前点击坐标
    private float currentX;
    private float currentY;

    public int color;
    private int style;
    private String colorCode;
    private String styleCode;
    public Typeface typeface;
    public static final int MOVE_LEFT = 6;
    public static final int MOVE_RIGHT = 7;
    public MyRelativeTouchCallBack getMyRelativeTouchCallBack() {
        return myRelativeTouchCallBack;
    }
    public void setMyRelativeTouchCallBack(MyRelativeTouchCallBack myRelativeTouchCallBack) {
//        long firstTime = SystemClock.uptimeMillis();
//        final MotionEvent firstEvent = MotionEvent.obtain(firstTime, firstTime, MotionEvent.ACTION_DOWN, 200, 500, 0);
//        long secondTime = firstTime + 30;
//        final MotionEvent secondEvent = MotionEvent.obtain(secondTime, secondTime, MotionEvent.ACTION_UP, 200, 500, 0);
//        dispatchTouchEvent(firstEvent);
//        dispatchTouchEvent(secondEvent);
        this.myRelativeTouchCallBack = myRelativeTouchCallBack;
    }
    //接口
    public MyRelativeTouchCallBack myRelativeTouchCallBack;
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
//        Toast.makeText(context, "init后！", Toast.LENGTH_SHORT).show();


//        iv = new ImageView(context);
//        iv.setLayoutParams(new ViewGroup.LayoutParams(20, 20));
//        iv.setBackgroundColor(Color.RED);
//        iv.setVisibility(GONE);
//        addView(iv);

//        btn = new Button(context);
//        btn.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//        btn.setText("添加TextView");
//        btn.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showDialog("",true);
//            }
//        });
////        btn.setWidth(20);
//
//        addView(btn);
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MyRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        mEvent = event;
        if (textSize == 0 && textView != null) {
            textSize = textView.getTextSize();
        }
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                Log.d("HHHH", "ACTION_DOWN");
                //此时有一个手指头落点
                onefinger = true;

                //给第一个手指落点记录落点的位置
                firstX = event.getX();
                firstY = event.getY();

                currentX = event.getX();
                currentY = event.getY();

                ptrID1 = event.getPointerId(event.getActionIndex());
                if (textView != null) {
                    //计算当前textView的位置和大小
                    width = textView.getWidth();
                    height = textView.getHeight();
                    startX = textView.getX();
                    startY = textView.getY();

                    if (event.getX() <= (startX + width) && event.getX() >= startX && event.getY() <= (startY + height) && event.getY() >= startY) {
                        //计算手势在控件上的偏移量
                        tv_width = event.getX() - startX;
                        tv_height = event.getY() - startY;
                        flag = true;
                    } else {
                        flag = false;
                    }
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                //第二个手指头落点 早已经不是点击事件
                onefinger = false;

                Log.d("HHHH", "ACTION_DOWN_POINTER");

                ptrID2 = event.getPointerId(event.getActionIndex());
                sX = event.getX(event.findPointerIndex(ptrID1));
                sY = event.getY(event.findPointerIndex(ptrID1));
                fX = event.getX(event.findPointerIndex(ptrID2));
                fY = event.getY(event.findPointerIndex(ptrID2));

                flag = false;

                if (listTvParams != null && !listTvParams.isEmpty()) {
                    //当第二个手指落指的时候 开始计算寻找最近的点
                    listDistance.clear();
                    for (int i = 0; i < listTvParams.size(); i++) {
                        listDistance.add(spacing(getMidPiont((int) fX, (int) fY, (int) sX, (int) sY), listTvParams.get(i).getMidPoint()));
                    }
//寻找最近的点
                    if (list != null && !list.isEmpty()) {
                        double min = listDistance.get(0);
                        num = 0;
                        for (int i = 1; i < listDistance.size(); i++) {
                            if (min > listDistance.get(i)) {
                                min = listDistance.get(i);
                                num = i;
                            }
                        }
                        textView = null;
                        textView = list.get(num);
                        tv_widths = getMidPiont((int) fX, (int) fY, (int) sX, (int) sY).x - textView.getX();
                        tv_heights = getMidPiont((int) fX, (int) fY, (int) sX, (int) sY).y - textView.getY();

                        oldDist = spacing(event, ptrID1, ptrID2);
                        setTextViewParams(getTextViewParams(textView));
                    }
                }

                break;
            case MotionEvent.ACTION_MOVE:
                Log.d("HHHH", "ACTION_MOVE");

                if (textView != null) {
                    //平移操作已经交给自己控件自己处理
                    //旋转和缩放操作
                    if (ptrID1 != INVALID_POINTER_ID && ptrID2 != INVALID_POINTER_ID) {
                        float nfX, nfY, nsX, nsY;
                        nsX = event.getX(event.findPointerIndex(ptrID1));
                        nsY = event.getY(event.findPointerIndex(ptrID1));
                        nfX = event.getX(event.findPointerIndex(ptrID2));
                        nfY = event.getY(event.findPointerIndex(ptrID2));

//                    //如果两点中点在该View上,则考虑两个点也可以拖动该View操作
//                    if (ifIsOnView(textView, new Point(getMidPiont((int) nfX, (int) nfY, (int) nsX, (int) nsY)))) {
//                    textView.setX(getMidPiont((int) nfX, (int) nfY, (int) nsX, (int) nsY).x - tv_widths);
//                    textView.setY(getMidPiont((int) nfX, (int) nfY, (int) nsX, (int) nsY).y - tv_heights);
//                    }
                        //处理旋转模块
                        mAngle = angleBetweenLines(fX, fY, sX, sY, nfX, nfY, nsX, nsY) + defaultAngle;
                        textView.setRotation(mAngle);

                        //处理缩放模块
                        float newDist = spacing(event, ptrID1, ptrID2);
                        scale = newDist / oldDist;
                        if (newDist > oldDist + 1) {
                            zoom(scale);
                            oldDist = newDist;
                        }
                        if (newDist < oldDist - 1) {
                            zoom(scale);
                            oldDist = newDist;
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.d("HHHH", "ACTION_UP");
                if (onefinger) {
                    if (spacing(firstX, firstY, event.getX(), event.getY()) < 10) {
                        showDialog("", true,0);
                        Toast.makeText(context, "这个是自定义View上的单击事件！", Toast.LENGTH_SHORT).show();
                    } else {
//                        if (myRelativeTouchCallBack != null) {
                            if (Math.abs(firstX - event.getX()) > Math.abs(firstY - event.getY())) {
                                if (firstX < event.getX()) {
                                    myRelativeTouchCallBack.touchMoveCallBack(MOVE_RIGHT);
                                } else {
                                    myRelativeTouchCallBack.touchMoveCallBack(MOVE_LEFT);
                                }
                            }
//                        }
                    }
                }

                ptrID1 = INVALID_POINTER_ID;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                Log.d("HHHH", "ACTION_UP_POINTER");
                ptrID2 = INVALID_POINTER_ID;
                if (list != null && !list.isEmpty()) {
                    updateTextViewParams(list.get(num), mAngle, scale);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.d("HHHH", "ACTION_CANCEL");
                ptrID1 = INVALID_POINTER_ID;
                ptrID2 = INVALID_POINTER_ID;
                break;
        }
        return true;
    }
    public void removeAllThings(){
        this.removeAllViews();
        listDistance.clear();
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

    public void addTextView(TextView tv, float x, float y, String content, int colory,float mtextSize,float rotate) {
        if (tv == null) {
            if(mtextSize == 0){
                mtextSize = 35;
            }
            textView = new TextView(context);
            textView.setEms(8);
            textView.setTag(System.currentTimeMillis());
            textView.setText(content);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            textView.setLayoutParams(params);
            textView.setTextSize(mtextSize);
            textView.setTextColor(colory);
            textView.setRotation(rotate);
            switch (style){
                case 1:
                    typeface=Typeface.createFromAsset(context.getAssets(),"fonts/楷体.ttf");
                    textView.setTypeface(typeface);
                    styleCode="楷体.ttf";
                    break;
                case 2:
                    typeface=Typeface.createFromAsset(context.getAssets(),"fonts/华康中黑字体.TTF");
                    textView.setTypeface(typeface);
                    styleCode="华康中黑字体.TTF";
                    break;
                case 3:
                    typeface=Typeface.createFromAsset(context.getAssets(),"fonts/华康少女字体.ttf");
                    textView.setTypeface(typeface);
                    styleCode="华康少女字体.ttf";
                    break;
                case 4:
                    typeface=Typeface.createFromAsset(context.getAssets(),"fonts/幼圆.ttf");
                    textView.setTypeface(typeface);
                    styleCode="幼圆.ttf";
                    break;
                case 5:
                    typeface=Typeface.createFromAsset(context.getAssets(),"fonts/方正卡通简体.ttf");
                    textView.setTypeface(typeface);
                    styleCode="方正卡通简体.ttf";
                    break;
                case 6:
                    typeface=Typeface.createFromAsset(context.getAssets(),"fonts/方正古隶.ttf");
                    textView.setTypeface(typeface);
                    styleCode="方正古隶.ttf";
                    break;
                case 7:
                    typeface=Typeface.createFromAsset(context.getAssets(),"fonts/方正启体简体.ttf");
                    textView.setTypeface(typeface);
                    styleCode="方正启体简体.ttf";
                    break;
                case 8:
                    typeface=Typeface.createFromAsset(context.getAssets(),"fonts/方正流行体简体.ttf");
                    textView.setTypeface(typeface);
                    styleCode="方正流行体简体.ttf";
                    break;
                case 9:
                    typeface=Typeface.createFromAsset(context.getAssets(),"fonts/瘦金体.ttf");
                    textView.setTypeface(typeface);
                    styleCode="瘦金体.ttf";
                    break;
                case 10:
                    typeface=Typeface.createFromAsset(context.getAssets(),"fonts/隶书.ttf");
                    textView.setTypeface(typeface);
                    styleCode="隶书.ttf";
                    break;
                case 11:
                    typeface=Typeface.createFromAsset(context.getAssets(),"fonts/华康娃娃体.TTF");
                    textView.setTypeface(typeface);
                    styleCode="华康娃娃体.TTF";
                    break;
                case 12:
                    typeface=Typeface.createFromAsset(context.getAssets(),"fonts/苹果丽黑.ttf");
                    textView.setTypeface(typeface);
                    styleCode="苹果丽黑.ttf";
                    break;
                case 13:
                    typeface=Typeface.createFromAsset(context.getAssets(),"fonts/微软雅黑14M.ttf");
                    textView.setTypeface(typeface);
                    styleCode="微软雅黑14M.ttf";
                    break;
            }
            textView.setX(x - textView.getWidth());
            textView.setY(y - textView.getHeight());
            updateTextViewParams(textView,rotate,scale);
            textView.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
//                    Toast.makeText(context, "addTextView后的onTouch！", Toast.LENGTH_SHORT).show();
                    textView = (TextView) v;
                    mEvent=event;
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_DOWN:
//                            Toast.makeText(context, "1！", Toast.LENGTH_SHORT).show();
                            tvOneFinger = true;
                            isClick = true;

                            firstX = event.getX();
                            firstY = event.getY();

                            mptrID1 = event.getPointerId(event.getActionIndex());
                            //计算当前textView的位置和大小
                            width = textView.getWidth();
                            height = textView.getHeight();
                            if(mEvent.getX()<=(firstX+width+10)&&mEvent.getX()>=firstX-10&&mEvent.getY()<=(firstY+height+5)&&mEvent.getY()>=firstY-5){
                                if (mEvent != null) {
                                    mTv_width = mEvent.getX() - textView.getX();
                                    mTv_height = mEvent.getY() - textView.getY();
                                }
                                mflag=true;
                            }
//                            if (mEvent != null) {
//                                mTv_width = mEvent.getX() - textView.getX();
//                                mTv_height = mEvent.getY() - textView.getY();
//                            }
//                            mflag = true;
                            break;
                        case MotionEvent.ACTION_POINTER_DOWN:
//                            Toast.makeText(context, "2！", Toast.LENGTH_SHORT).show();
                            tvOneFinger = false;
                            isClick = false;

                            mptrID2 = event.getPointerId(event.getActionIndex());
                            msX = mEvent.getX(event.findPointerIndex(mptrID1));
                            msY = mEvent.getY(event.findPointerIndex(mptrID1));
                            mfX = mEvent.getX(event.findPointerIndex(mptrID2));
                            mfY = mEvent.getY(event.findPointerIndex(mptrID2));

                            mflag = false;
//
                            mTv_widths = getMidPiont((int) mfX, (int) mfY, (int) msX, (int) msY).x - textView.getX();
                            mTv_heights = getMidPiont((int) mfX, (int) mfY, (int) msX, (int) msY).y - textView.getY();
//
                            oldDist = spacing(event, mptrID1, mptrID2);

                            break;
                        case MotionEvent.ACTION_MOVE:
//                            Toast.makeText(context, "3！", Toast.LENGTH_SHORT).show();
                            //平移操作
                            if (mflag && mEvent != null) {
                                textView.setX(mEvent.getX() - mTv_width);
                                textView.setY(mEvent.getY() - mTv_height);
                                //通知调用者我在平移
//                                if (myRelativeTouchCallBack != null)
                                myRelativeTouchCallBack.onTextViewMoving(textView);
                            }

                            if (spacing(firstX, firstY, event.getX(), event.getY()) > 2) {
                                isClick = false;
                            }

                            //旋转和缩放操作
                            if (mptrID1 != INVALID_POINTER_ID && mptrID2 != INVALID_POINTER_ID) {
                                float nfX, nfY, nsX, nsY;
                                nsX = mEvent.getX(event.findPointerIndex(mptrID1));
                                nsY = mEvent.getY(event.findPointerIndex(mptrID1));
                                nfX = mEvent.getX(event.findPointerIndex(mptrID2));
                                nfY = mEvent.getY(event.findPointerIndex(mptrID2));

                                //如果两点中点在该View上,则考虑两个点也可以拖动该View操作
                                textView.setX(getMidPiont((int) nfX, (int) nfY, (int) nsX, (int) nsY).x - mTv_widths);
                                textView.setY(getMidPiont((int) nfX, (int) nfY, (int) nsX, (int) nsY).y - mTv_heights);
                                //处理旋转模块
                                mAngle = angleBetweenLines(mfX, mfY, msX, msY, nfX, nfY, nsX, nsY);
                                textView.setRotation(mAngle);
                                //处理缩放模块
                                float newDist = spacing(event, mptrID1, mptrID2);
                                scale = newDist / oldDist;

                                if (newDist > oldDist + 1) {
                                    textView.setTextSize(textSize *= scale);
                                    oldDist = newDist;
                                }
                                if (newDist < oldDist - 1) {
                                    textView.setTextSize(textSize *= scale);
                                    oldDist = newDist;
                                }
                                //通知调用者我在旋转或者缩放
//                                if (myRelativeTouchCallBack != null)
                                myRelativeTouchCallBack.onTextViewMoving(textView);
                            }
                            break;
                        case MotionEvent.ACTION_UP:
//                            Toast.makeText(context, "4！", Toast.LENGTH_SHORT).show();
                            //通知调用者我滑动结束了
//                            if (myRelativeTouchCallBack != null)
                            myRelativeTouchCallBack.onTextViewMovingDone();
                            mptrID1 = INVALID_POINTER_ID;
                            updateTextViewParams((TextView) v, mAngle, scale);
//                            Toast.makeText(context, "移动结束！", Toast.LENGTH_SHORT).show();

                            if (tvOneFinger && isClick) {
                                showDialog(textView.getText().toString(), false,color);
                                System.out.println("color:!!!!!!!!!!!!!!!!!!!"+color);
                            }

                            break;
                        case MotionEvent.ACTION_POINTER_UP:
//                            Toast.makeText(context, "5！", Toast.LENGTH_SHORT).show();
                            mptrID2 = INVALID_POINTER_ID;
                            updateTextViewParams((TextView) v, mAngle, scale);
                            break;
                        case MotionEvent.ACTION_CANCEL:
//                            Toast.makeText(context, "6！", Toast.LENGTH_SHORT).show();
                            mptrID1 = INVALID_POINTER_ID;
                            mptrID2 = INVALID_POINTER_ID;
                            break;
                    }
//                    Toast.makeText(context, "end！", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
            list.add(textView);
            //保存并添加到list中
            saveTextViewparams(textView);
            addView(textView);
        } else {
            textView = tv;
            textView.setText(content);
            textView.setTextColor(colory);
            switch (style){
                case 1:
                    typeface=Typeface.createFromAsset(context.getAssets(),"fonts/楷体.ttf");
                    textView.setTypeface(typeface);
                    styleCode="楷体.ttf";
                    break;
                case 2:
                    typeface=Typeface.createFromAsset(context.getAssets(),"fonts/华康中黑字体.TTF");
                    textView.setTypeface(typeface);
                    styleCode="华康中黑字体.TTF";
                    break;
                case 3:
                    typeface=Typeface.createFromAsset(context.getAssets(),"fonts/华康少女字体.ttf");
                    textView.setTypeface(typeface);
                    styleCode="华康少女字体.ttf";
                    break;
                case 4:
                    typeface=Typeface.createFromAsset(context.getAssets(),"fonts/幼圆.ttf");
                    textView.setTypeface(typeface);
                    styleCode="幼圆.ttf";
                    break;
                case 5:
                    typeface=Typeface.createFromAsset(context.getAssets(),"fonts/方正卡通简体.ttf");
                    textView.setTypeface(typeface);
                    styleCode="方正卡通简体.ttf";
                    break;
                case 6:
                    typeface=Typeface.createFromAsset(context.getAssets(),"fonts/方正古隶.ttf");
                    textView.setTypeface(typeface);
                    styleCode="方正古隶.ttf";
                    break;
                case 7:
                    typeface=Typeface.createFromAsset(context.getAssets(),"fonts/方正启体简体.ttf");
                    textView.setTypeface(typeface);
                    styleCode="方正启体简体.ttf";
                    break;
                case 8:
                    typeface=Typeface.createFromAsset(context.getAssets(),"fonts/方正流行体简体.ttf");
                    textView.setTypeface(typeface);
                    styleCode="方正流行体简体.ttf";
                    break;
                case 9:
                    typeface=Typeface.createFromAsset(context.getAssets(),"fonts/瘦金体.ttf");
                    textView.setTypeface(typeface);
                    styleCode="瘦金体.ttf";
                    break;
                case 10:
                    typeface=Typeface.createFromAsset(context.getAssets(),"fonts/隶书.ttf");
                    textView.setTypeface(typeface);
                    styleCode="隶书.ttf";
                    break;
                case 11:
                    typeface=Typeface.createFromAsset(context.getAssets(),"fonts/华康娃娃体.TTF");
                    textView.setTypeface(typeface);
                    styleCode="华康娃娃体.TTF";
                    break;
                case 12:
                    typeface=Typeface.createFromAsset(context.getAssets(),"fonts/苹果丽黑.ttf");
                    textView.setTypeface(typeface);
                    styleCode="fonts/苹果丽黑.ttf";
                    break;
                case 13:
                    typeface=Typeface.createFromAsset(context.getAssets(),"fonts/微软雅黑14M.ttf");
                    textView.setTypeface(typeface);
                    styleCode="微软雅黑14M.ttf";
                    break;
            }
            updateTextViewParams(textView,rotate,scale);
        }

    }
    /**
     * 初始化操作
     */
    public void init() {
        ptrID1 = INVALID_POINTER_ID;
        ptrID2 = INVALID_POINTER_ID;
        mptrID1 = INVALID_POINTER_ID;
        mptrID2 = INVALID_POINTER_ID;

        list = new ArrayList<>();
        listTvParams = new ArrayList<>();
        listDistance = new ArrayList<>();

        editText = new EditText(context);
        editText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        editText.setVisibility(GONE);
        addView(editText);
    }
    /**
     * 显示自定义对话框
     */
    public void showDialog(String message, final boolean isNew,int mycolor) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("请输入内容:");
        View v=View.inflate(context, R.layout.activity_alert,null);
        builder.setView(v);
        builder.setCancelable(true);
        EditText editText=v.findViewById(R.id.et);
        editText.setText(message);
        color=mycolor;
        if(isNew) {
            editText.setTextColor(Color.BLACK);
        }
        else{
            System.out.println(color);
            switch (color){
                case 0:
                    editText.setTextColor(Color.parseColor("#000000"));
                    break;
                case 1:
                    editText.setTextColor(Color.parseColor("#550D01"));
                    break;
                case 2:
                    editText.setTextColor(Color.parseColor("#810110"));
                    break;
                case 3:
                    editText.setTextColor(Color.parseColor("#EA6D10"));
                    break;
                case 4:
                    editText.setTextColor(Color.parseColor("#001B5E"));
                    break;
                case 5:
                    editText.setTextColor(Color.parseColor("#033853"));
                    break;
                case 6:
                    editText.setTextColor(Color.parseColor("#033510"));
                    break;
                case 7:
                    editText.setTextColor(Color.parseColor("#2B055C"));
                    break;
                case 8:
                    editText.setTextColor(Color.parseColor("#717073"));
                    break;
                case 9:
                    editText.setTextColor(Color.parseColor("#FF6A6A"));
                    break;
                case 10:
                    editText.setTextColor(Color.parseColor("#FC4B2B"));
                    break;
                case 11:
                    editText.setTextColor(Color.parseColor("#FFB90F"));
                    break;
                case 12:
                    editText.setTextColor(Color.parseColor("#4D97D5"));
                    break;
                case 13:
                    editText.setTextColor(Color.parseColor("#196A81"));
                    break;
                case 14:
                    editText.setTextColor(Color.parseColor("#3C7602"));
                    break;
                case 15:
                    editText.setTextColor(Color.parseColor("#A0509D"));
                    break;
                case 16:
                    editText.setTextColor(Color.parseColor("#FFFFFF"));
                    break;
                case 17:
                    editText.setTextColor(Color.parseColor("#FFC1C1"));
                    break;
                case 18:
                    editText.setTextColor(Color.parseColor("#FFD39B"));
                    break;
                case 19:
                    editText.setTextColor(Color.parseColor("#FFF68F"));
                    break;
                case 20:
                    editText.setTextColor(Color.parseColor("#B0E2FF"));
                    break;
                case 21:
                    editText.setTextColor(Color.parseColor("#7AC5CD"));
                    break;
                case 22:
                    editText.setTextColor(Color.parseColor("#C4E48D"));
                    break;
                case 23:
                    editText.setTextColor(Color.parseColor("#FFF0F5"));
                    break;

            }

        }
//        editText.setTextColor(Color.BLACK);
        v.findViewById(R.id.blackButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                color=0;
                editText.setTextColor(Color.BLACK);
                colorCode="#000000";
            }
        });
        v.findViewById(R.id.brownButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                color=1;
                editText.setTextColor(Color.parseColor("#550D01"));
                colorCode="#550D01";
            }
        });
        v.findViewById(R.id.lightredButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                color=2;
                editText.setTextColor(Color.parseColor("#810110"));
                colorCode="#810110";
            }
        });
        v.findViewById(R.id.orangeButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                color=3;
                editText.setTextColor(Color.parseColor("#EA6D10"));
                colorCode="#EA6D10";
            }
        });
        v.findViewById(R.id.shenlanButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                color=4;
                editText.setTextColor(Color.parseColor("#001B5E"));
                colorCode="#001B5E";
            }
        });
        v.findViewById(R.id.lanlvButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                color=5;
                editText.setTextColor(Color.parseColor("#033853"));
                colorCode="#033853";
            }
        });
        v.findViewById(R.id.shenlvButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                color=6;
                editText.setTextColor(Color.parseColor("#033510"));
                colorCode="#033510";
            }
        });
        v.findViewById(R.id.shenziButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                color=7;
                editText.setTextColor(Color.parseColor("#2B055C"));
                colorCode="#2B055C";
            }
        });
        v.findViewById(R.id.grayButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                color=8;
                editText.setTextColor(Color.parseColor("#717073"));
                colorCode="#717073";
            }
        });
        v.findViewById(R.id.shenfenButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                color=9;
                editText.setTextColor(Color.parseColor("#FF6A6A"));
                colorCode="#FF6A6A";
            }
        });
        v.findViewById(R.id.midredButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                color=10;
                editText.setTextColor(Color.parseColor("#FC4B2B"));
                colorCode="#FC4B2B";
            }
        });
        v.findViewById(R.id.yellowButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                color=11;
                editText.setTextColor(Color.parseColor("#FFB90F"));
                colorCode="#FFB90F";
            }
        });
        v.findViewById(R.id.midblueButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                color=12;
                editText.setTextColor(Color.parseColor("#4D97D5"));
                colorCode="#4D97D5";
            }
        });
        v.findViewById(R.id.lan2Button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                color=13;
                editText.setTextColor(Color.parseColor("#196A81"));
                colorCode="#196A81";
            }
        });
        v.findViewById(R.id.caolvButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                color=14;
                editText.setTextColor(Color.parseColor("#3C7602"));
                colorCode="#3C7602";
            }
        });
        v.findViewById(R.id.midpurpleButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                color=15;
                editText.setTextColor(Color.parseColor("#A0509D"));
                colorCode="#A0509D";
            }
        });
        v.findViewById(R.id.whiteButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                color=16;
                editText.setTextColor(Color.parseColor("#FFFFFF"));
                colorCode="#FFFFFF";
            }
        });
        v.findViewById(R.id.pinkButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                color=17;
                editText.setTextColor(Color.parseColor("#FFC1C1"));
                colorCode="#FFC1C1";
            }
        });
        v.findViewById(R.id.rouseButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                color=18;
                editText.setTextColor(Color.parseColor("#FFD39B"));
                colorCode="#FFD39B";
            }
        });
        v.findViewById(R.id.qianyellowButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                color=19;
                editText.setTextColor(Color.parseColor("#FFF68F"));
                colorCode="#FFF68F";
            }
        });
        v.findViewById(R.id.qianlanButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                color=20;
                editText.setTextColor(Color.parseColor("#B0E2FF"));
                colorCode="#B0E2FF";
            }
        });
        v.findViewById(R.id.qianlanlvButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                color=21;
                editText.setTextColor(Color.parseColor("#7AC5CD"));
                colorCode="#7AC5CD";
            }
        });
        v.findViewById(R.id.qianlvButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                color=22;
                editText.setTextColor(Color.parseColor("#C4E48D"));
                colorCode="#C4E48D";
            }
        });
        v.findViewById(R.id.qianzifenButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                color=23;
                editText.setTextColor(Color.parseColor("#FFF0F5"));
                colorCode="#FFF0F5";
            }
        });

        v.findViewById(R.id.kaiti).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                style=1;
                Typeface typeface= Typeface.createFromAsset(context.getAssets(),"fonts/楷体.ttf");
                editText.setTypeface(typeface);
                styleCode="楷体.ttf";
            }
        });

        v.findViewById(R.id.zhonghei).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                style=2;
                Typeface typeface= Typeface.createFromAsset(context.getAssets(),"fonts/华康中黑字体.TTF");
                editText.setTypeface(typeface);
                styleCode="华康中黑字体.TTF";
            }
        });

        v.findViewById(R.id.shaonv).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                style=3;
                Typeface typeface= Typeface.createFromAsset(context.getAssets(),"fonts/华康少女字体.ttf");
                editText.setTypeface(typeface);
                styleCode="华康少女字体.ttf";
            }
        });

        v.findViewById(R.id.youyuan).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                style=4;
                Typeface typeface= Typeface.createFromAsset(context.getAssets(),"fonts/幼圆.ttf");
                editText.setTypeface(typeface);
                styleCode="幼圆.ttf";
            }
        });

        v.findViewById(R.id.katong).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                style=5;
                Typeface typeface= Typeface.createFromAsset(context.getAssets(),"fonts/方正卡通简体.ttf");
                editText.setTypeface(typeface);
                styleCode="方正卡通简体.ttf";
            }
        });

        v.findViewById(R.id.guli).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                style=6;
                Typeface typeface= Typeface.createFromAsset(context.getAssets(),"fonts/方正古隶.ttf");
                editText.setTypeface(typeface);
                styleCode="方正古隶.ttf";
            }
        });

        v.findViewById(R.id.qiti).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                style=7;
                Typeface typeface= Typeface.createFromAsset(context.getAssets(),"fonts/方正启体简体.ttf");
                editText.setTypeface(typeface);
                styleCode="方正启体简体.ttf";
            }
        });

        v.findViewById(R.id.liuxing).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                style=8;
                Typeface typeface= Typeface.createFromAsset(context.getAssets(),"fonts/方正流行体简体.ttf");
                editText.setTypeface(typeface);
                styleCode="方正流行体简体.ttf";
            }
        });

        v.findViewById(R.id.shoujin).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                style=9;
                Typeface typeface= Typeface.createFromAsset(context.getAssets(),"fonts/瘦金体.ttf");
                editText.setTypeface(typeface);
                styleCode="瘦金体.ttf";
            }
        });

        v.findViewById(R.id.lishu).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                style=10;
                Typeface typeface= Typeface.createFromAsset(context.getAssets(),"fonts/隶书.ttf");
                editText.setTypeface(typeface);
                styleCode="隶书.ttf";
            }
        });

        v.findViewById(R.id.wawati).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                style=11;
                Typeface typeface= Typeface.createFromAsset(context.getAssets(),"fonts/华康娃娃体.TTF");
                editText.setTypeface(typeface);
                styleCode="华康娃娃体.TTF";
            }
        });

        v.findViewById(R.id.pingguolihei).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                style=12;
                Typeface typeface= Typeface.createFromAsset(context.getAssets(),"fonts/苹果丽黑.ttf");
                editText.setTypeface(typeface);
                styleCode="苹果丽黑.ttf";
            }
        });

        v.findViewById(R.id.yahei).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                style=13;
                Typeface typeface= Typeface.createFromAsset(context.getAssets(),"fonts/微软雅黑14M.ttf");
                editText.setTypeface(typeface);
                styleCode="微软雅黑14M.ttf";
            }
        });


//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setTitle("请输入内容:");
        /*final EditText editText = new EditText(context);
        editText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        builder.setView(editText);
        builder.setCancelable(false);*/
//        switch (color){
//            case 0:
//                editText.setTextColor(Color.BLACK);
//            case 1:
//                editText.setTextColor(Color.parseColor("#8E2323"));
//            case 2:
//                editText.setTextColor(Color.parseColor("#9F5F9F"));
//            case 3:
//                editText.setTextColor(Color.parseColor("#B5A642"));
//            case 4:
//                editText.setTextColor(Color.GRAY);
//        }
        /*editText.setText(message);
        builder.setSingleChoiceItems(new String[] {"黑色","红色","紫色","黄色","灰色"}, 0,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
                        switch (which){
                            case 0:
                                color=0;
                                editText.setTextColor(Color.BLACK);
                                break;
                            case 1:
                                color=1;
                                editText.setTextColor(Color.parseColor("#8E2323"));
                                break;
                            case 2:
                                color=2;
                                editText.setTextColor(Color.parseColor("#9F5F9F"));
                                break;
                            case 3:
                                color=3;
                                editText.setTextColor(Color.parseColor("#B5A642"));
                            case 4:
                                color=4;
                                editText.setTextColor(Color.GRAY);

                        }

                    }
                }
        );*/

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String content = editText.getText().toString();
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(context, "您没有任何输入!", Toast.LENGTH_SHORT).show();
                } else {
                    if (isNew) {
                        switch (color){
                            case 0:
                                addTextView(null, 150, 500, content, Color.parseColor("#000000"),0,0);
                                colorCode="#000000";
                                break;
                            case 1:
                                addTextView(null, 150, 500, content, Color.parseColor("#550D01"),0,0);
                                colorCode="#550D01";
                                break;
                            case 2:
                                addTextView(null, 150, 500, content, Color.parseColor("#810110"),0,0);
                                colorCode="#810110";
                                break;
                            case 3:
                                addTextView(null, 150, 500, content, Color.parseColor("#EA6D10"),0,0);
                                colorCode="#EA6D10";
                                break;
                            case 4:
                                addTextView(null, 150, 500, content, Color.parseColor("#001B5E"),0,0);
                                colorCode="#001B5E";
                                break;
                            case 5:
                                addTextView(null, 150, 500, content, Color.parseColor("#033853"),0,0);
                                colorCode="#033853";
                                break;
                            case 6:
                                addTextView(null, 150, 500, content, Color.parseColor("#033510"),0,0);
                                colorCode="#033510";
                                break;
                            case 7:
                                addTextView(null, 150, 500, content, Color.parseColor("#2B055C"),0,0);
                                colorCode="#2B055C";
                                break;
                            case 8:
                                addTextView(null, 150, 500, content, Color.parseColor("#717073"),0,0);
                                colorCode="#717073";
                                break;
                            case 9:
                                addTextView(null, 150, 500, content, Color.parseColor("#FF6A6A"),0,0);
                                colorCode="#FF6A6A";
                                break;
                            case 10:
                                addTextView(null, 150, 500, content, Color.parseColor("#FC4B2B"),0,0);
                                colorCode="#FC4B2B";
                                break;
                            case 11:
                                addTextView(null, 150, 500, content, Color.parseColor("#FFB90F"),0,0);
                                colorCode="#FFB90F";
                                break;
                            case 12:
                                addTextView(null, 150, 500, content, Color.parseColor("#4D97D5"),0,0);
                                colorCode="#4D97D5";
                                break;
                            case 13:
                                addTextView(null, 150, 500, content, Color.parseColor("#196A81"),0,0);
                                colorCode="#196A81";
                                break;
                            case 14:
                                addTextView(null, 150, 500, content, Color.parseColor("#3C7602"),0,0);
                                colorCode="#3C7602";
                                break;
                            case 15:
                                addTextView(null, 150, 500, content, Color.parseColor("#A0509D"),0,0);
                                colorCode="#A0509D";
                                break;
                            case 16:
                                addTextView(null, 150, 500, content, Color.parseColor("#FFFFFF"),0,0);
                                colorCode="#FFFFFF";
                                break;
                            case 17:
                                addTextView(null, 150, 500, content, Color.parseColor("#FFC1C1"),0,0);
                                colorCode="#FFC1C1";
                                break;
                            case 18:
                                addTextView(null, 150, 500, content, Color.parseColor("#FFD39B"),0,0);
                                colorCode="#FFD39B";
                                break;
                            case 19:
                                addTextView(null, 150, 500, content, Color.parseColor("#FFF68F"),0,0);
                                colorCode="#FFF68F";
                                break;
                            case 20:
                                addTextView(null, 150, 500, content, Color.parseColor("#B0E2FF"),0,0);
                                colorCode="#B0E2FF";
                                break;
                            case 21:
                                addTextView(null, 150, 500, content, Color.parseColor("#7AC5CD"),0,0);
                                colorCode="#7AC5CD";
                                break;
                            case 22:
                                addTextView(null, 150, 500, content, Color.parseColor("#C4E48D"),0,0);
                                colorCode="#C4E48D";
                                break;
                            case 23:
                                addTextView(null, 150, 500, content, Color.parseColor("#FFF0F5"),0,0);
                                colorCode="#FFF0F5";
                                break;

                        }
//                        addTextView(null, currentX, currentY, content, Color.BLACK,0,0);
                    } else {
//                        Toast.makeText(context, "改颜色!", Toast.LENGTH_SHORT).show();
                        System.out.println();
                        switch (color){
                            case 0:
                                addTextView(textView, textView.getX(), textView.getY(), content, Color.parseColor("#000000"),textView.getTextSize(),textView.getRotation());
                                colorCode="#000000";
                                break;
                            case 1:
                                addTextView(textView, textView.getX(), textView.getY(), content, Color.parseColor("#550D01"),textView.getTextSize(),textView.getRotation());
                                colorCode="#550D01";
                                break;
                            case 2:
                                addTextView(textView, textView.getX(), textView.getY(), content, Color.parseColor("#810110"),textView.getTextSize(),textView.getRotation());
                                colorCode="#810110";
                                break;
                            case 3:
                                addTextView(textView, textView.getX(), textView.getY(), content, Color.parseColor("#EA6D10"),textView.getTextSize(),textView.getRotation());
                                colorCode="#EA6D10";
                                break;
                            case 4:
                                addTextView(textView, textView.getX(), textView.getY(), content, Color.parseColor("#001B5E"),textView.getTextSize(),textView.getRotation());
                                colorCode="#001B5E";
                                break;
                            case 5:
                                addTextView(textView, textView.getX(), textView.getY(), content, Color.parseColor("#033853"),textView.getTextSize(),textView.getRotation());
                                colorCode="#033853";
                                break;
                            case 6:
                                addTextView(textView, textView.getX(), textView.getY(), content, Color.parseColor("#033510"),textView.getTextSize(),textView.getRotation());
                                colorCode="#033510";
                                break;
                            case 7:
                                addTextView(textView, textView.getX(), textView.getY(), content, Color.parseColor("#2B055C"),textView.getTextSize(),textView.getRotation());
                                colorCode="#2B055C";
                                break;
                            case 8:
                                addTextView(textView, textView.getX(), textView.getY(), content, Color.parseColor("#717073"),textView.getTextSize(),textView.getRotation());
                                colorCode="#717073";
                                break;
                            case 9:
                                addTextView(textView, textView.getX(), textView.getY(), content, Color.parseColor("#FF6A6A"),textView.getTextSize(),textView.getRotation());
                                colorCode="#FF6A6A";
                                break;
                            case 10:
                                addTextView(textView, textView.getX(), textView.getY(), content, Color.parseColor("#FC4B2B"),textView.getTextSize(),textView.getRotation());
                                colorCode="#FC4B2B";
                                break;
                            case 11:
                                addTextView(textView, textView.getX(), textView.getY(), content, Color.parseColor("#FFB90F"),textView.getTextSize(),textView.getRotation());
                                colorCode="#FFB90F";
                                break;
                            case 12:
                                addTextView(textView, textView.getX(), textView.getY(), content, Color.parseColor("#4D97D5"),textView.getTextSize(),textView.getRotation());
                                colorCode="#4D97D5";
                                break;
                            case 13:
                                addTextView(textView, textView.getX(), textView.getY(), content, Color.parseColor("#196A81"),textView.getTextSize(),textView.getRotation());
                                colorCode="#196A81";
                                break;
                            case 14:
                                addTextView(textView, textView.getX(), textView.getY(), content, Color.parseColor("#3C7602"),textView.getTextSize(),textView.getRotation());
                                colorCode="#3C7602";
                                break;
                            case 15:
                                addTextView(textView, textView.getX(), textView.getY(), content, Color.parseColor("#A0509D"),textView.getTextSize(),textView.getRotation());
                                colorCode="#A0509D";
                                break;
                            case 16:
                                addTextView(textView, textView.getX(), textView.getY(), content, Color.parseColor("#FFFFFF"),textView.getTextSize(),textView.getRotation());
                                colorCode="#FFFFFF";
                                break;
                            case 17:
                                addTextView(textView, textView.getX(), textView.getY(), content, Color.parseColor("#FFC1C1"),textView.getTextSize(),textView.getRotation());
                                colorCode="#FFC1C1";
                                break;
                            case 18:
                                addTextView(textView, textView.getX(), textView.getY(), content, Color.parseColor("#FFD39B"),textView.getTextSize(),textView.getRotation());
                                colorCode="#FFD39B";
                                break;
                            case 19:
                                addTextView(textView, textView.getX(), textView.getY(), content, Color.parseColor("#FFF68F"),textView.getTextSize(),textView.getRotation());
                                colorCode="#FFF68F";
                                break;
                            case 20:
                                addTextView(textView, textView.getX(), textView.getY(), content, Color.parseColor("#B0E2FF"),textView.getTextSize(),textView.getRotation());
                                colorCode="#B0E2FF";
                                break;
                            case 21:
                                addTextView(textView, textView.getX(), textView.getY(), content, Color.parseColor("#7AC5CD"),textView.getTextSize(),textView.getRotation());
                                colorCode="#7AC5CD";
                                break;
                            case 22:
                                addTextView(textView, textView.getX(), textView.getY(), content, Color.parseColor("#C4E48D"),textView.getTextSize(),textView.getRotation());
                                colorCode="#C4E48D";
                                break;
                            case 23:
                                addTextView(textView, textView.getX(), textView.getY(), content, Color.parseColor("#FFF0F5"),textView.getTextSize(),textView.getRotation());
                                colorCode="#FFF0F5";
                                break;
                        }
//                        addTextView(textView, textView.getX(), textView.getY(), content, Color.BLACK,textView.getTextSize(),textView.getRotation());
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
    public void updateTextViewParams(TextView tv, float rotation, float scale) {
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
    public void saveTextViewparams(TextView textView) {
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
    public TextViewParams getTextViewParams(TextView tv) {
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
        textView.setTextSize(textSize *= f);
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
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        Log.d("HHHH", "onMeasure-->我被调用了哦" + widthMeasureSpec + " " + heightMeasureSpec);
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
            myStyle=styleCode;
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
    public interface MyRelativeTouchCallBack {
        void touchMoveCallBack(int direction);

        void onTextViewMoving(TextView textView);

        void onTextViewMovingDone();
    }
}

