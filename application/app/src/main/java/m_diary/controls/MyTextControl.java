package m_diary.controls;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

import m_diary.activities.DiaryActivity;

//文本控件
@SuppressLint("AppCompatCustomView")
public class MyTextControl extends TextView {
    private PointF mLastSinglePoint = new PointF();//记录上一次单指触摸屏幕的点坐标
    private PointF mLastDistanceVector = new PointF();//记录上一次双指之间的向量
    private PointF mDistanceVector = new PointF();//记录当前双指之间的向量
    //记录点坐标，减少对象在onTouch中的创建
    private PointF mFirstPoint = new PointF();
    private PointF mSecondPoint = new PointF();
    public float mAngle = 0.0f;   //旋转角度
    public float scale = 1.0f; //缩放比例
    private float mLastDistance;//记录上一次双指之间的距离
    public String typeFaceStr;
    public String colorCode;
    public boolean touched;
    public boolean isClick;
    private Context context;
    private int mMode;//当前模式
    public static final int MODE_NONE = 0;//初始状态
    public static final int MODE_SINGLE = 1;//标志是否可移动
    public static final int MODE_MULTIPLE = 2;//标志是否可缩放，旋转
    @SuppressLint("ClickableViewAccessibility")
    public MyTextControl(Context context) {
        super(context);
        this.context = context;
        reset();
        touched = false;
        isClick = false;
        this.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                touched = true;
                isClick = true;
            }
            return !touched;
        });
    }

    public MyTextControl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyTextControl(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    /**
     * 计算两点之间的距离
     */
    public float calculateDistance(PointF firstPointF, PointF secondPointF) {
        float x = firstPointF.x - secondPointF.x;
        float y = firstPointF.y - secondPointF.y;
        return (float) Math.sqrt(x * x + y * y);
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

    public void reset() {
        mLastSinglePoint.set(0f, 0f);
        mLastDistanceVector.set(0f, 0f);
        mDistanceVector.set(0f, 0f);
        mLastDistance = 0f;
        mMode = MODE_NONE;
    }
    
    public void onTouch(MotionEvent event){
        DiaryActivity.changed = true;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:{
                //有触摸到贴纸
                mMode = Sticker.MODE_SINGLE;
                isClick = true;
                //记录按下的位置
                mLastSinglePoint.set(event.getX(), event.getY());
            }break;
            case MotionEvent.ACTION_POINTER_DOWN:{
                if (event.getPointerCount() == 2) {
                    mMode = Sticker.MODE_MULTIPLE;
                    //记录双指的点位置
                    mFirstPoint.set(event.getX(0), event.getY(0));
                    mSecondPoint.set(event.getX(1), event.getY(1));
                    //计算双指之间向量
                    mLastDistanceVector.set(mFirstPoint.x - mSecondPoint.x, mFirstPoint.y - mSecondPoint.y);
                    //计算双指之间距离
                    mLastDistance = calculateDistance(mFirstPoint, mSecondPoint);
                }
            }break;
            case MotionEvent.ACTION_MOVE:{
                isClick = false;
                if (mMode == MODE_SINGLE) {
                    float dx = event.getX() - mLastSinglePoint.x;
                    float dy = event.getY() - mLastSinglePoint.y;
                    this.setX(this.getX() + dx);
                    this.setY(this.getY() + dy);
                    mLastSinglePoint.set(event.getX(), event.getY());
                }
                if (mMode == MODE_MULTIPLE && event.getPointerCount() == 2) {
                    PointF currentFirst = new PointF(), currentSecond = new PointF();
                    currentFirst.set(event.getX(0), event.getY(0));
                    currentSecond.set(event.getX(1), event.getY(1));
                    //操作自由缩放
                    float distance = calculateDistance(mFirstPoint, mSecondPoint);
                    //根据双指移动的距离获取缩放因子
                    scale += ((distance / mLastDistance) - 1.0);
                    this.setScaleX(scale);
                    this.setScaleY(scale);
                    mLastDistance = distance;
                    mAngle += angleBetweenLines(mFirstPoint.x, mFirstPoint.y, mSecondPoint.x, mSecondPoint.y, currentFirst.x, currentFirst.y, currentSecond.x, currentSecond.y);
                    //操作自由旋转
                    this.setRotation(mAngle);
                    //记录双指的点位置
                    mFirstPoint.set(currentFirst.x, currentFirst.y);
                    mSecondPoint.set(currentSecond.x, currentSecond.y);
                }
            }break;
            case MotionEvent.ACTION_UP:{
                //单击事件，打开窗口
                if((mMode == MODE_SINGLE) && isClick){
                    DiaryActivity diaryActivity = (DiaryActivity)context;
                    diaryActivity.mDiaryItemLayout.showDialog(this.getText().toString(), false, colorCode, typeFaceStr);
                }
                reset();
            }break;
        }
    }
}
