package m_diary.assets;

import android.graphics.Matrix;
import android.graphics.PointF;

public class StickerAttr {
    public Matrix mMatrix;
    public float[] mSrcPoints;
    public float[] mDstPoints;
    public PointF mMidPointF;

    public StickerAttr(Matrix mMatrix, float[] mSrcPoints, float[] mDstPoints, PointF mMidPointF){
        this.mMatrix = mMatrix;
        this.mDstPoints = mDstPoints;
        this.mSrcPoints = mSrcPoints;
        this.mMidPointF = mMidPointF;
    }
}
