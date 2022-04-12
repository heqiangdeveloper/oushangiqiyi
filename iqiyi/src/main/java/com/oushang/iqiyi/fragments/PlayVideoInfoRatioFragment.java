package com.oushang.iqiyi.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.oushang.iqiyi.R;
import com.oushang.iqiyi.base.BasePlayFragment;
import com.oushang.iqiyi.events.EventBusHelper;
import com.oushang.iqiyi.events.EventConstant;
import com.oushang.iqiyi.fragments.fragmentUtils.FragmentHelper;
import com.oushang.iqiyi.ui.LineItemDecoration;
import com.oushang.iqiyi.ui.SimpleFastAdapter;
import com.oushang.iqiyi.utils.ThemeManager;
import com.oushang.lib_base.base.mvp.presenter.BasePresenter;
import com.oushang.lib_base.base.mvp.view.BaseFragmentMVP;
import com.oushang.lib_base.base.rv.BaseViewHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @Author: zeelang
 * @Description: 画面比例
 * @Time: 2021/8/16 11:36
 * @Since: 1.0
 */
public class PlayVideoInfoRatioFragment extends BasePlayFragment {

    @BindView(R.id.ratio_back)
    LinearLayout mRatioBackLayout; //返回键

    @BindView(R.id.video_scale_type)
    RecyclerView mVideoScaleType; //画面比例

    @BindView(R.id.ratio_back_img)
    ImageView mRatioBackImg; //返回箭头

    @BindView(R.id.ratio_scale_label)
    TextView mRatioScaleLabel; //画面比例标签

    private static final String VIDEO_SCALE_TYPE = "video_scale_type";

    private int mCurrentVideoScaleType; //当前画面比例

    @Override
    protected int setLayout() {
        return R.layout.fragment_play_videoinfo_ratio;
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    public static PlayVideoInfoRatioFragment newInstance(int videoScaleType) {
        PlayVideoInfoRatioFragment fragment = new PlayVideoInfoRatioFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(VIDEO_SCALE_TYPE, videoScaleType);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initView() {
        int id = getId();
        if (id == R.id.right_drawer_contaniner) { //如果是侧边栏显示
            mRatioBackImg.setVisibility(View.GONE); //隐藏返回箭头
            mRatioBackLayout.setClickable(false); //禁止点击返回
        } else {
            mRatioBackImg.setVisibility(View.VISIBLE);
            mRatioBackLayout.setClickable(true);
        }
        Bundle arguments = getArguments();
        if (arguments != null) {
            mCurrentVideoScaleType = arguments.getInt(VIDEO_SCALE_TYPE);
        }
        List<Integer> videoScaleType = Arrays.asList(0,3,101, 100);
        mVideoScaleType.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false));
        mVideoScaleType.addItemDecoration(new LineItemDecoration(35, LineItemDecoration.VERTICAL));
        mVideoScaleType.setAdapter(new SimpleFastAdapter<Integer>(getContext(), R.layout.item_definition_btn, videoScaleType){
            @Override
            public void convert(BaseViewHolder holder, Integer data, int position) {
                Button scaleType = holder.getView(R.id.definition_name_btn);
                String text = "";
                switch (data) {
                    case 0:
                        text = "等比缩放";
                        break;
                    case 3:
                        text = "满屏";
                        break;
                    case 100:
                        text = "50%";
                        break;
                    case 101:
                        text = "75%";
                        break;
                }
                if (!text.isEmpty()) {
                    scaleType.setText(text);
                }
                if (ThemeManager.getThemeMode() == ThemeManager.ThemeMode.NIGHT) {
                    scaleType.setTextColor(mContext.getColor(R.color.color_skin_text));
                    scaleType.setBackground(mContext.getDrawable(R.drawable.play_videoinfo_defination_skin));
                } else {
                    scaleType.setTextColor(mContext.getColor(R.color.color_skin_text_notnight));
                    scaleType.setBackground(mContext.getDrawable(R.drawable.play_videoinfo_defination_skin_notnight));
                }
                if (mCurrentVideoScaleType == data) {
                    scaleType.setSelected(true);
                    scaleType.setTextColor(mContext.getColor(R.color.white));
                } else {
                    scaleType.setSelected(false);
                }
                scaleType.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.setSelected(true);
                        Bundle eventParams = new Bundle();
                        eventParams.putInt(EventConstant.EVENT_PARAMS_PLAY_SELECT_RATIO, data);
                        EventBusHelper.post(EventBusHelper.newEvent(EventConstant.EVENT_TYPE_PLAY_SELECT_RATIO, eventParams));
                        mCurrentVideoScaleType = data;
                        if (mVideoScaleType.getAdapter() != null) {
                            mVideoScaleType.getAdapter().notifyDataSetChanged();
                        }
                    }
                });
            }
        });
    }

    //点击返回键
    @OnClick(R.id.ratio_back)
    public void onClickBack() {
        FragmentHelper.popBackAll(getFragmentManager());
    }

    @Override
    public void onThemeChanged(ThemeManager.ThemeMode themeMode) {
        super.onThemeChanged(themeMode);
        updateSkin();
    }

    /**
     * 主题换肤
     */
    @SuppressLint({"UseCompatLoadingForDrawables", "NotifyDataSetChanged"})
    private void updateSkin() {
        rootView.setBackgroundColor(getResources().getColor(ThemeManager.getThemeResource(getContext(), R.color.color_skin_background), null));
        mRatioBackImg.setBackground(getResources().getDrawable(ThemeManager.getThemeResource(getContext(), R.drawable.ic_skin_back_60), null));
        mRatioScaleLabel.setTextColor(getResources().getColor(ThemeManager.getThemeResource(getContext(), R.color.color_skin_synopsis_text), null));
        if (mVideoScaleType != null && mVideoScaleType.getAdapter() != null) {
            mVideoScaleType.getAdapter().notifyDataSetChanged();
        }
    }
}
