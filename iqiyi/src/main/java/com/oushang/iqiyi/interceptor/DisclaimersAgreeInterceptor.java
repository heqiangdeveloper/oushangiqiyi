package com.oushang.iqiyi.interceptor;

import android.content.Context;
import android.util.Log;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Interceptor;
import com.alibaba.android.arouter.facade.callback.InterceptorCallback;
import com.alibaba.android.arouter.facade.template.IInterceptor;
import com.alibaba.android.arouter.launcher.ARouter;
import com.oushang.iqiyi.common.Constant;
import com.oushang.lib_base.utils.SPUtils;

/**
 * @Author: zeelang
 * @Description: 免责声明同意拦截器
 * @Time: 2022/3/18 14:34
 * @Since: 1.0
 */
@Interceptor(priority = 3, name = "DisclaimersAgreeInterceptor")
public class DisclaimersAgreeInterceptor implements IInterceptor {
    private static final String TAG = DisclaimersAgreeInterceptor.class.getSimpleName();

    @Override
    public void process(Postcard postcard, InterceptorCallback callback) {
        String path = postcard.getPath();
        if (path.equals(Constant.PATH_ACTIVITY_DISCLAIMERS) || path.equals(Constant.PATH_ACTIVITY_SPLASH)) {
            callback.onContinue(postcard);//继续页面间的跳转
        } else {
            boolean isAgree = SPUtils.getShareBoolean(Constant.SP_IQIYI_SPACE, Constant.SP_KEY_USER_AGREE, false);
            Log.d(TAG, "isAgree:" + isAgree);
            if (!isAgree) {
                ARouter.getInstance().build(Constant.PATH_ACTIVITY_DISCLAIMERS).navigation();//未同意，转到免责声明界面Activity
            } else {
                callback.onContinue(postcard);//继续页面间的跳转
            }
        }
    }

    @Override
    public void init(Context context) {

    }
}
