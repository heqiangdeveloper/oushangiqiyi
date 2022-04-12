package com.oushang.lib_base.image;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * @Author: zeelang
 * @Description: Glide
 * @Time: 2022/1/18 21:01
 * @Since: 1.0
 */
public class Glide2Utils {
    private static final String TAG = Glide2Utils.class.getSimpleName();

    public static void loadRoundedCorners(Context context,Uri uri, ImageView view, int placeHolder, int err, int roundingRadius) {
        Log.d(TAG, "load:" + uri);
        Glide.with(context)
                .load(uri)
                .placeholder(placeHolder)
                .error(err)
                .skipMemoryCache(false)
                .apply(RequestOptions.bitmapTransform(new MultiTransformation<>(new RoundedCornersTransformation(roundingRadius,0), new FitCenter())))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.e(TAG, "onLoadFailed:" + Log.getStackTraceString(e));
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Log.d(TAG, "onResourceReady");
                        return false;
                    }
                })
                .into(new ImageViewTarget<Drawable>(view) {

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        Log.e(TAG, "onLoadFailed: Image load on failed");
                    }

                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        super.onResourceReady(resource, transition);
                        if(view != null) {
                            Log.d(TAG, "set image resource:" + uri.getPath());
                            view.setImageDrawable(resource);
                        }
                    }

                    @Override
                    protected void setResource(@Nullable Drawable resource) {

                    }
                });
    }

    public static void load(Context context, Uri uri, ImageView view, int placeHolder, int err, int radius) {
        Log.d(TAG, "load:" + uri.getPath());
        Glide.with(context)
                .load(uri)
                .placeholder(placeHolder)
                .error(err)
                .skipMemoryCache(false)
                .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(radius, 0, RoundedCornersTransformation.CornerType.ALL)))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.e(TAG, "onLoadFailed:" + Log.getStackTraceString(e));
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Log.d(TAG, "onResourceReady");
                        return false;
                    }
                })
                .into(new ImageViewTarget<Drawable>(view) {

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        Log.e(TAG, "onLoadFailed: Image load on failed");
                    }

                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        super.onResourceReady(resource, transition);
                        if(view != null) {
                            Log.d(TAG, "set image resource:" + uri.getPath());
                            view.setImageDrawable(resource);
                        }
                    }

                    @Override
                    protected void setResource(@Nullable Drawable resource) {

                    }
                });
    }

    public static void loadRes(Context context, int resId, ImageView imageView) {
        Glide.with(context)
                .load(resId)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .format(DecodeFormat.PREFER_RGB_565)
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .into(imageView);
    }

    @WorkerThread
    public static void clearDiskCache(Context context) {
        Glide.get(context).clearDiskCache();
    }

    public static void clearMemory(Context context) {
        Glide.get(context).clearMemory();
    }

    public static void onTrimMemory(Context context, int level) {
        Glide.get(context).onTrimMemory(level);
    }

    public static void onLowMemory(Context context) {
        Glide.get(context).onLowMemory();
    }

}
