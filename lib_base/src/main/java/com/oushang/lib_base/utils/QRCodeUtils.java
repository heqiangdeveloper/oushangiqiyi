package com.oushang.lib_base.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.text.TextUtils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.Hashtable;

/**
 * @Author: zeelang
 * @Description: 二维码工具类
 * @Date: 2021/6/25
 */
public class QRCodeUtils {

    /**
     * 生成二维码图片
     * @param qrcode  二维码信息
     * @param width   宽度
     * @param height  高度
     * @return        bitmap
     */
    public static Bitmap createQRCodeImage(String qrcode, int width, int height) {
        return createQRCodeBitmap(qrcode,width,height,null,null,0.2f);
    }


    /**
     * 生成二维码图片
     * @param qrcode  二维码信息
     * @param width   宽度
     * @param height  高度
     * @param hints   参数
     * @param logo    二维码logo图片
     * @param percent logo图片所占比例
     * @return        二维码图片
     */
    public static Bitmap createQRCodeBitmap(String qrcode, int width, int height, Hashtable<EncodeHintType, String> hints, Bitmap logo, float percent) {
        if (TextUtils.isEmpty(qrcode)) {
            return null;
        }
        if (width < 0 || height < 0) {
            return null;
        }
        Hashtable<EncodeHintType, String> hintHashtable = hints;
        if (hintHashtable == null) {
            hintHashtable = new Hashtable<>();
            hintHashtable.put(EncodeHintType.ERROR_CORRECTION, "H");
            hintHashtable.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hintHashtable.put(EncodeHintType.MARGIN, "1");
        }
        try {
            BitMatrix bitMatrix = new QRCodeWriter().encode(qrcode,
                    BarcodeFormat.QR_CODE, width, height, hintHashtable);
            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * width + x] = Color.BLACK;
                    } else {
                        pixels[y * width + x] = Color.WHITE;
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            if (logo != null) {
                return addLogo(bitmap, logo, percent);
            }
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 添加logo图片
     * @param src 二维码图片
     * @param logo logo图片
     * @param percent logo图片所占比例
     * @return 二维码图片
     */
    private static Bitmap addLogo(Bitmap src, Bitmap logo, float percent) {
        if (src == null || logo == null) return null;
        if (percent < 0F || percent > 1F) {
            percent = 0.2F;
        }
        int sw = src.getWidth();
        int sh = src.getHeight();
        int lw = logo.getWidth();
        int lh = logo.getHeight();

        float scalew = sw * percent / lw;
        float scaleh = sw * percent / lh;

        Bitmap bitmap = Bitmap.createBitmap(sw, sh, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(src, 0, 0, null);
        canvas.scale(scalew, scaleh, sw / 2, sh / 2);
        canvas.drawBitmap(logo, sw / 2 - lw / 2, sh / 2 - lh / 2, null);

        return bitmap;
    }


}
