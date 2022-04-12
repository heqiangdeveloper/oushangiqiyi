package com.oushang.lib_base.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;
import java.security.MessageDigest;
import java.util.function.Consumer;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * @Author: DELL
 * @Description: Glide工具类
 * @Time: 2021/6/28 18:01
 * @Since:
 */
public class GlideUtils {
    private static final String TAG = GlideUtils.class.getSimpleName();

    private static RequestOptions defaultOption = getDefaultOptions();

    public static RequestOptions getDefaultOptions() {
        return new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .skipMemoryCache(true)
                .format(DecodeFormat.PREFER_RGB_565);
    }

    /**
     * 图片加载
     *
     * @param context context
     * @param uri     uri
     * @param view    view
     */
    public static void load(Context context, Uri uri, ImageView view) {
        Log.d(TAG, "load image");
        load(context, uri, view, defaultOption);
    }


    public static void load(Context context, String url, ImageView view, Consumer<RequestOptions> optConfigure) {
        Log.d(TAG, "load image");
        RequestOptions opt = getDefaultOptions();
        if (optConfigure != null) optConfigure.accept(opt);
        loadUrl(context, url, view, opt);
    }

    public static void load(Context context, Uri uri, ImageView view, RequestOptions options) {
        options = options.diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(true)
                .format(DecodeFormat.PREFER_RGB_565);
        Glide.with(context)
                .setDefaultRequestOptions(options)
                .load(uri)
                .into(view);
    }

    public static void loadUrl(Context context, String url, ImageView view, RequestOptions options) {
        options = options.diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(true)
                .format(DecodeFormat.PREFER_RGB_565);
        Glide.with(context)
                .setDefaultRequestOptions(options)
                .load(url)
                .into(view);
    }

    /**
     * 加载本地文件图片资源
     *
     * @param context
     * @param file
     * @param view
     */
    public static void loadFile(Context context, File file, ImageView view) {
        Glide.with(context)
                .load(file)
                .into(view);
    }


    /**
     * 加载资源图片
     *
     * @param context
     * @param resId
     * @param imageView
     */
    public static void loadRes(Context context, int resId, ImageView imageView) {
        RequestOptions options = defaultOption.diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(true)
                .format(DecodeFormat.PREFER_RGB_565);
        Glide.with(context)
                .setDefaultRequestOptions(options)
                .load(resId)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.d(TAG, "loadRes onLoadFailed: " + "," + e);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        //Log.d(TAG, "onResourceReady");
                        return false;
                    }
                })
                .into(imageView);
    }

    public static void loadImageWithRoundedCorners(Context context, Uri uri, ImageView view, float radius) {
        Glide.with(context)
                .load(uri)
                .transform(new GlideRoundTransformation(radius))
                .into(view);


    }

    /**
     * 图片加载
     *
     * @param context context
     * @param uri     uri
     * @param view    view
     */
    public static void load(Context context, Uri uri, ImageView view, int placeHolder, int error) {
        RequestOptions options = defaultOption.diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(false)
                .format(DecodeFormat.PREFER_RGB_565);
        Glide.with(context)
                .setDefaultRequestOptions(options)
                .load(uri)
                .placeholder(placeHolder)
                .error(error)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.d(TAG, "onLoadFailed: " + uri + "," + e);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        //Log.d(TAG, "onResourceReady");
                        return false;
                    }
                })
                .into(view);
    }

    public static void loadImageWithRoundedCorners(Context context, Uri uri, ImageView view, int radius) {
        Glide.with(context)
                .asBitmap()
                .load(uri)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(true)
                .format(DecodeFormat.PREFER_RGB_565)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        Log.d(TAG, "loadImageWithRoundedCorners onLoadFailed: " + uri + "," + e);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        //Log.d(TAG, "loadImageWithRoundedCorners onResourceReady");
                        return false;
                    }
                })
                .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(radius, 0, RoundedCornersTransformation.CornerType.ALL)))
                .into(view);
    }

    public static void loadImageWithRoundedCorners(Context context, Uri uri, ImageView view, int placeHolder, int err, int radius) {
        Glide.with(context)
                .asBitmap()
                .load(uri)
                .placeholder(placeHolder)
                .skipMemoryCache(false)
                .error(err)
                .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(radius, 0, RoundedCornersTransformation.CornerType.ALL)))
                .into(view);
    }


    static class GlideRoundTransformation extends BitmapTransformation {

        private float radius = 0f;

        public GlideRoundTransformation(float radius) {
            this.radius = radius;
        }

        @Override
        protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
            return roundCrop(pool, toTransform);
        }

        @Override
        public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {

        }

        private Bitmap roundCrop(BitmapPool pool, Bitmap resource) {
            if (resource == null) return null;
            Bitmap result = pool.get(resource.getWidth(), resource.getHeight(), Bitmap.Config.ARGB_8888);
            if (result == null) {
                result = Bitmap.createBitmap(resource.getWidth(), resource.getHeight(), Bitmap.Config.ARGB_8888);
            }
            Canvas canvas = new Canvas(result);
            Paint paint = new Paint();
            paint.setShader(new BitmapShader(resource, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
            paint.setAntiAlias(true);
            RectF rectF = new RectF(0f, 0f, resource.getWidth(), resource.getHeight());
            canvas.drawRoundRect(rectF, radius, radius, paint);
            return result;
        }
    }

    public static class TransformationUtils extends ImageViewTarget<Bitmap> {
        private ImageView target;

        public TransformationUtils(ImageView view) {
            super(view);
            this.target = view;
        }

        @Override
        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
            super.onResourceReady(resource, transition);
            setResource(resource);
        }

        @Override
        protected void setResource(@Nullable Bitmap resource) {
            if (resource == null) return;
            view.setImageBitmap(resource);

            //获取原图的宽高
            int width = resource.getWidth();
            int height = resource.getHeight();

            Log.d(TAG, "source: width:" + width + ",height:" + height);

            //获取imageView的宽
            int imageViewWidth = target.getWidth();

            //计算缩放比例
            float sy = (float) (imageViewWidth * 0.1) / (float) (width * 0.1);

            //计算图片等比例放大后的高
            int imageViewHeight = (int) (height * sy);

            Log.d(TAG, "target: width:" + imageViewWidth + ",height:" + imageViewHeight);
            ViewGroup.LayoutParams params = target.getLayoutParams();
            params.height = imageViewHeight;
            target.setLayoutParams(params);
        }
    }

}
