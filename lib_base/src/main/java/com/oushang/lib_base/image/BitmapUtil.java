package com.oushang.lib_base.image;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

import androidx.annotation.IntDef;

import com.oushang.lib_base.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import retrofit2.http.PUT;

/**
 * @Author: zeelang
 * @Description: 图片工具类
 * @Time: 2021/8/31 14:14
 * @Since: 1.0
 */
public class BitmapUtil {

    public static final int CORNER_NONE = 0;//无圆角
    public static final int CORNER_LEFT_TOP = 1;//左上角
    public static final int CORNER_RIGHT_TOP = 1<<1;//右上角
    public static final int CORNER_LEFT_BOTTOM = 1<<2;//左下角
    public static final int CORNER_RIGHT_BOTTOM = 1<<3;//右下角
    public static final int CORNER_ALL = CORNER_LEFT_TOP|CORNER_RIGHT_TOP|CORNER_LEFT_BOTTOM|CORNER_RIGHT_BOTTOM; //全部4个角
    public static final int CORNER_TOP = CORNER_LEFT_TOP|CORNER_RIGHT_TOP; //顶部2个角
    public static final int CORNER_BOTTOM = CORNER_LEFT_BOTTOM|CORNER_RIGHT_BOTTOM;//底部2个角
    public static final int CORNER_LEFT = CORNER_LEFT_TOP|CORNER_LEFT_BOTTOM;//左边2个角
    public static final int CORNER_RIGHT = CORNER_RIGHT_TOP|CORNER_RIGHT_BOTTOM;//右边2个角

    @IntDef({CORNER_NONE,CORNER_LEFT_TOP,CORNER_RIGHT_TOP,CORNER_LEFT_BOTTOM,CORNER_RIGHT_BOTTOM,CORNER_ALL,CORNER_TOP,CORNER_BOTTOM,CORNER_LEFT,CORNER_RIGHT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface CornerMode{}

    public static Bitmap cropRoundedCornerBitmap(Bitmap bitmap, float roundPx, @CornerMode int cornerMode) {
        if (bitmap == null) {
            return null;
        }

        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();
        Bitmap output = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        canvas.drawARGB(Color.TRANSPARENT, Color.TRANSPARENT,Color.TRANSPARENT,Color.TRANSPARENT);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);

        RectF rectF = new RectF(0,0,width,height);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        int roundCorner = cornerMode ^ CORNER_ALL;
        if ((roundCorner & CORNER_LEFT_TOP) != CORNER_NONE) {
            Rect block = new Rect(0,0,width,height);
            canvas.drawRect(block, paint);
        }
        if ((roundCorner & CORNER_RIGHT_TOP) != CORNER_NONE) {
            int offset = (int) (width - roundPx);
            Rect block = new Rect(offset, 0, width, (int) roundPx);
            canvas.drawRect(block, paint);
        }
        if ((roundCorner & CORNER_LEFT_BOTTOM) != CORNER_NONE) {
            int offset = (int) (height- roundPx);
            Rect block = new Rect(0, offset, (int) roundPx, height);
            canvas.drawRect(block, paint);
        }
        if ((roundCorner & CORNER_RIGHT_BOTTOM) != CORNER_NONE) {
            int wOffset = (int) (width - roundPx);
            int hOffset = (int) (height- roundPx);
            Rect block = new Rect(wOffset, hOffset, width, height);
            canvas.drawRect(block, paint);
        }

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        final Rect src = new Rect(0,0,width,height);
        final Rect dst = src;
        canvas.drawBitmap(bitmap, src, dst, paint);
        return output;
    }

}
