package m_diary.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;

import m_diary.controls.DiaryItemLayout;
import m_diary.utils.Protocol;

import com.example.myapplication.R;

public class StickersActivity extends AppCompatActivity {
    private DialogInterface.OnClickListener confirm;
    private DialogInterface.OnClickListener cancel;

    public int stickerSrcNum = 16;
    Bitmap stickerBitmap = null;
    private DiaryItemLayout mDiaryItemLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stickers);

        for (int stickerIndex = 1; stickerIndex <= stickerSrcNum; stickerIndex++) {
            @SuppressLint("DefaultLocale") String ivStickerStr = "iv_sticker_" + String.format("%02d", stickerIndex);
            @SuppressLint("DefaultLocale") String stickerStr = "sticker_" + String.format("%02d", stickerIndex);
            int resIVStickerID = getResources().getIdentifier(ivStickerStr, "id", getPackageName());
            int resStickerID = getResources().getIdentifier(stickerStr, "drawable", getPackageName());
            findViewById(resIVStickerID).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stickerBitmap = BitmapFactory.decodeResource(StickersActivity.this.getResources(), resStickerID);
                    getStickerBitmap(resStickerID);
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        mStickerLayout.removeAllSticker();
    }

    public void getStickerBitmap(int resStickerID) {
        Intent i = new Intent();
        i.putExtra(Protocol.STICKER_ID, resStickerID);
        i.putExtra(Protocol.CHANGED, true);
        this.setResult(Protocol.ADD_STICKER, i);
        finish();
    }

    public void back_sticker(View view) {
        if (stickerBitmap != null) {
            AlertDialog.Builder alert_dialog_builder = new AlertDialog.Builder(this);
            alert_dialog_builder.setMessage("放弃添加贴纸吗?");
            alert_dialog_builder.setPositiveButton("是", confirm);
            alert_dialog_builder.setNegativeButton("否", cancel);
            AlertDialog alert_dialog = alert_dialog_builder.create();
            alert_dialog.show();
        } else {
            Intent i = new Intent();
            i.putExtra(Protocol.CHANGED, false);
            StickersActivity.this.setResult(Protocol.ADD_STICKER, i);
            finish();
        }
    }
}