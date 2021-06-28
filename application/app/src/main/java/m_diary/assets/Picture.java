package m_diary.assets;

import android.graphics.Matrix;

public class Picture extends Items{
    public String type;
    public String path;
    public int id;
    public float[] matrix = new float[9];
    public float[] mSrcPoints;
    public Picture(int id, String path, String type, float[] mSrcPoints){
        this.id = id;
        this.path = path;
        this.type = type;
        this.mSrcPoints = mSrcPoints;
    }
}
