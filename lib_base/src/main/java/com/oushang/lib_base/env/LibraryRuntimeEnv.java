package com.oushang.lib_base.env;

import android.app.Application;
import android.content.Context;

/**
 * @Author: zeelang
 * @Description: library运行环境
 * @Time: 2021/6/28 18:01
 * @Since: 1.0
 */
public class LibraryRuntimeEnv {
    private Application mApplication;

    private static class LibraryRuntimeEnvHolder {
        private static LibraryRuntimeEnv HOLDER = new LibraryRuntimeEnv();
    }

    private LibraryRuntimeEnv() {}

    public static LibraryRuntimeEnv get() {
        return LibraryRuntimeEnvHolder.HOLDER;
    }

    /**
     * 初始化
     * @param application application
     */
    public void init(Application application) {
        if (mApplication != null) {
            throw new IllegalStateException("library can be setup only once.");
        }
        this.mApplication = application;
    }

    /**
     * 判断application
     */
    private void ensureApplication() {
        if (this.mApplication == null) {
            throw new IllegalStateException("library has not been setup.");
        }
    }

    /**
     * 获取context
     * @return application context
     */
    public Context getContext() {
        ensureApplication();
        return mApplication.getApplicationContext();
    }

    /**
     * 获取application
     * @return application
     */
    public Application getApplication() {
        ensureApplication();
        return mApplication;
    }

}
