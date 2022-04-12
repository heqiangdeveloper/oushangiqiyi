package com.oushang.iqiyi.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.facade.callback.NavCallback;
import com.alibaba.android.arouter.facade.callback.NavigationCallback;
import com.alibaba.android.arouter.launcher.ARouter;
import com.oushang.iqiyi.R;
import com.oushang.iqiyi.common.Constant;
import com.oushang.iqiyi.statistics.DataStatistics;
import com.oushang.iqiyi.statistics.StatConstant;
import com.oushang.lib_base.utils.SPUtils;

/**
 * @Author: zeelang
 * @Description: 启动界面
 * @Time: 2022/1/5 15:36
 * @Since: 1.0
 */
@Route(path = Constant.PATH_ACTIVITY_SPLASH)
public class SplashActivity extends AppCompatActivity {
    private static final String TAG = SplashActivity.class.getSimpleName();

    long startTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 避免从桌面启动程序后，会重新实例化入口类的activity
        if (!this.isTaskRoot()) { // 判断当前activity是不是所在任务栈的根
            Log.d(TAG, "is not task root!");
            finish();
            return;
        }


        startTime = System.currentTimeMillis();
        checkIsAgreePrivacy();

        setContentView(R.layout.activity_splash);

    }

    /**
     * 检查是否已同意《爱奇艺隐私政策》
     */
    private void checkIsAgreePrivacy() {
        boolean isAgree = SPUtils.getShareBoolean(Constant.SP_IQIYI_SPACE, Constant.SP_KEY_USER_AGREE, false);
        Log.d(TAG, "isAgree:" + isAgree);
        if(isAgree) { //已同意
            checkIsOpenRecent();
        } else {
            ARouter.getInstance().build(Constant.PATH_ACTIVITY_DISCLAIMERS).navigation();//未同意，转到免责声明界面Activity
            finish();
        }
    }

    /**
     * 检查是否是桌面小部件点击最近播放
     */
    private void checkIsOpenRecent() {
        boolean isOpenRecent = getIntent().getBooleanExtra("isOpenRecent", false);
        Log.d(TAG, "isOpenRecent:" + isOpenRecent);
        if(isOpenRecent) { //点击最近播放，跳转到我的界面
            DataStatistics.recordUiEvent(StatConstant.EVENT_ID_5911);
            startNavMainActivity(Constant.NAV_ME);
        } else { //未点击最近播放，则跳转到首页界面
            startNavMainActivity(Constant.NAV_HOME);
        }
    }

    /**
     * 跳转到主界面
     * @param nav 菜单id
     */
    private void startNavMainActivity(@Constant.NavMenu int nav) {
        Log.d(TAG, "startNavMainActivity:" + nav);
        long endTime = System.currentTimeMillis();
        ARouter.getInstance().build(Constant.PATH_ACTIVITY_NAV)
                .withInt(Constant.NAV_MENU_SELECT, nav)
                .withTransition(R.anim.activity_enter, R.anim.activity_exit)
                .navigation(this, new NavCallback() {
                    @Override
                    public void onArrival(Postcard postcard) {
                        finish();
                    }
                });//转到主导航Activity
        long takeTime = endTime - startTime;
        Log.d(TAG, "start NavMainActivity spend time:" + takeTime + "ms");
    }
}