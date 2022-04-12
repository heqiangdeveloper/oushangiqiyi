package com.oushang.iqiyi.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.oushang.iqiyi.R;

/**
 * @Author: zeelang
 * @Description: 网络加载显示
 * @Time: 2021/7/14 10:22
 * @Since: 1.0
 */
@Deprecated
public class LoadingFragment extends DialogFragment {

    private View rootView;

    private ImageView loadingImage;

    private Animation animation;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.loading, container, false);
        }
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView() {
//        loadingImage = rootView.findViewById(R.id.loading_img);
//        animation = AnimationUtils.loadAnimation(this.getContext(),R.anim.loading_anim);
//        animation.setInterpolator(new AccelerateDecelerateInterpolator());
//        if (animation != null) {
//            loadingImage.startAnimation(animation);
//        } else {
//            loadingImage.setAnimation(animation);
//            loadingImage.startAnimation(animation);
//        }
    }

    public static LoadingFragment create() {
        return new LoadingFragment();
    }

    @Override
    public void dismiss() {
        super.dismiss();
//        loadingImage.clearAnimation();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
