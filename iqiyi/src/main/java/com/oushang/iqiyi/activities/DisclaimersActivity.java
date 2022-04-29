package com.oushang.iqiyi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.oushang.iqiyi.MainApplication;
import com.oushang.iqiyi.NavMainActivity;
import com.oushang.iqiyi.R;
import com.oushang.iqiyi.base.BaseStksActivityMVP;
import com.oushang.iqiyi.common.Constant;
import com.oushang.iqiyi.dialog.IqiyiTipsDialog;
import com.oushang.iqiyi.dialog.VehicleFlowDialog;
import com.oushang.iqiyi.mvp.presenter.DisclaimersPresenter;
import com.oushang.iqiyi.mvp.view.IDisclaimersView;
import com.oushang.iqiyi.statistics.DataStatistics;
import com.oushang.iqiyi.statistics.StatConstant;
import com.oushang.iqiyi.utils.AppUtils;
import com.oushang.iqiyi.utils.StatusBarUtil;
import com.oushang.lib_base.base.mvp.view.BaseActivityMVP;
import com.oushang.lib_base.utils.SPUtils;
import com.oushang.lib_base.utils.ToastUtils;
import com.oushang.lib_service.entries.HistoryRecord;


import butterknife.BindView;
import butterknife.OnClick;

/**
 * @Author: zeelang
 * @Description: 免责声明界面
 * @Time: 2021/8/5 20:04
 * @Since: 1.0
 */
@Route(path = Constant.PATH_ACTIVITY_DISCLAIMERS)
public class DisclaimersActivity extends BaseStksActivityMVP<DisclaimersPresenter> implements IDisclaimersView {
    private static final String TAG = DisclaimersActivity.class.getSimpleName();

    @BindView(R.id.disclaimer_content)
    TextView mDisclaimerCotent; //声明内容

    @BindView(R.id.disclaimer_agree_btn)
    Button mAgreeAndContinue; //同意按钮

    @BindView(R.id.disclaimer_disagree_btn)
    Button mDisAgree; //不同意按钮

    private boolean isAgree = false;

    @Override
    protected int setLayout() {
        return R.layout.activity_disclaimers;
    }

    @Override
    protected DisclaimersPresenter createPresenter() {
        return new DisclaimersPresenter();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        StatusBarUtil.hideStatusBar(this);
        isAgree = SPUtils.getShareBoolean(Constant.SP_IQIYI_SPACE, Constant.SP_KEY_USER_AGREE, false);
        long expirationTime = SPUtils.getShareLong(Constant.SP_IQIYI_SPACE, Constant.SP_KEY_BALANCE_VALUE, 0L);
        Log.d(TAG, "isAgree:" + isAgree + ",expirationTime:" + AppUtils.getTime(expirationTime));
        if (isAgree) {
            ARouter.getInstance().build(Constant.PATH_ACTIVITY_NAV).navigation();
            finish();
        }
//        long curTime = System.currentTimeMillis();
//        if(expirationTime <= curTime) {
//            Log.e(TAG, "车机流量已到期");
//            if(!VehicleFlowDialog.getInstance(MainApplication.getContext()).isShowing()) {
//                VehicleFlowDialog.getInstance(MainApplication.getContext()).show();
//            }
//            finish();
//        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        if((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            Log.w(TAG, "Is brought to front");
            finish();
            return;
        }

        mAgreeAndContinue.setSelected(true);
        String content = getString(R.string.user_agreement_and_privacy_protection_content);
        String agreement = getString(R.string.user_agreement_name);
        String privacy = getString(R.string.privacy_protection_name);
        String newContent = String.format(content,agreement,privacy);
        int agreementStart  = newContent.indexOf(agreement);
        int privacyStart = newContent.indexOf(privacy);
        int agreementEnd = agreementStart + agreement.length();
        int privacyEnd = privacyStart + privacy.length();
        Log.d(TAG, "agreementStart:" + agreementStart + ",privacyStart:" + privacyStart);
        SpannableStringBuilder style = new SpannableStringBuilder(newContent);
        int color = getColor(R.color.color_user_agreement_and_privacy_protection_text_link);
        style.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Log.d(TAG, "click " + agreement);
                Intent intent = new Intent(DisclaimersActivity.this, WebViewActivity.class);
                intent.putExtra(Constant.WEB_VIEW_URL,Constant.AGREEMENT_URL);
                startActivity(intent);

            }
        }, agreementStart, agreementEnd, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        style.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Log.d(TAG, "click " + privacy);
                Intent intent = new Intent(DisclaimersActivity.this, WebViewActivity.class);
                intent.putExtra(Constant.WEB_VIEW_URL,Constant.PRIVACY_URL);
                startActivity(intent);
            }
        }, privacyStart, privacyEnd, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

        style.setSpan(new UnderlineSpan(){
            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setUnderlineText(false);
            }
        },agreementStart,agreementEnd, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

        style.setSpan(new UnderlineSpan(){
            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setUnderlineText(false);
            }
        },privacyStart,privacyEnd, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

        style.setSpan(new ForegroundColorSpan(color),agreementStart,agreementEnd, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        style.setSpan(new ForegroundColorSpan(color),privacyStart,privacyEnd, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

        mDisclaimerCotent.setText(style);
        mDisclaimerCotent.setMovementMethod(new LinkMovementMethod());
    }

    //点击关闭，退出
    @OnClick(R.id.disclaimer_close)
    public void onClickClose() {
        finish();
    }

    //点击同意
    @OnClick(R.id.disclaimer_agree_btn)
    public void onClickAgree() {
        Log.d(TAG, "onClickAgree");
        DataStatistics.recordUiEvent(StatConstant.EVENT_ID_5901);
        SPUtils.putShareValue(Constant.SP_IQIYI_SPACE,Constant.SP_KEY_USER_AGREE, true);
        Intent intent = new Intent(DisclaimersActivity.this, NavMainActivity.class);
        startActivity(intent);
        finish();
    }

    //点击不同意
    @OnClick(R.id.disclaimer_disagree_btn)
    public void onClickDisAgree() {
        Log.d(TAG ,"onClickDisAgree");
        DataStatistics.recordUiEvent(StatConstant.EVENT_ID_5902);
        new IqiyiTipsDialog().show(getSupportFragmentManager());
    }

    @Override
    public void onLoadRecent(HistoryRecord record) {
        Log.d(TAG, "onLoadRecent:" + record);
        String albumIdStr = record.getAlbumId();
        String qipuIdStr = record.getTvId();
        long playTime = record.getVideoPlayTime();

        ARouter.getInstance().build(Constant.PATH_ACTIVITY_PLAYER)
                .withLong(Constant.PLAY_VIDEO_ID, Long.parseLong(qipuIdStr))
                .withLong(Constant.PLAY_ALBUM_ID, Long.parseLong(albumIdStr))
                .withLong(Constant.PLAY_POSITION, playTime)
                .withBoolean(Constant.PLAY_IS_FULL_SCREEN, true)
                .navigation();
        finish();
    }

    @Override
    public void onLoadEmptyRecent() {
        Log.d(TAG, "onLoadEmptyRecent");
        isAgree = SPUtils.getShareBoolean(Constant.SP_IQIYI_SPACE, Constant.SP_KEY_USER_AGREE, false);
        if (isAgree) {
            ToastUtils.showToast(R.layout.toast_layout, R.id.toast_content, "暂无最近播放，在首页看看吧");
            ARouter.getInstance().build(Constant.PATH_ACTIVITY_NAV).navigation();
            finish();
        }
    }
}
