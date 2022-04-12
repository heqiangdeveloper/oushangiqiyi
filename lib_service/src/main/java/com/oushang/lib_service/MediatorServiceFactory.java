package com.oushang.lib_service;

import android.app.Application;
import android.content.Context;

import com.oushang.lib_base.log.LogUtils;
import com.oushang.lib_service.impls.ChannelManagerService;
import com.oushang.lib_service.impls.MyAccountManagerService;
import com.oushang.lib_service.impls.IqiyiSdkManagerService;
import com.oushang.lib_service.impls.PlayListManagerService;
import com.oushang.lib_service.impls.PlayManagerService;
import com.oushang.lib_service.impls.SearchManagerService;
import com.oushang.lib_service.impls.VideoManagerService;
import com.oushang.lib_service.interfaces.ChannelManager;
import com.oushang.lib_service.interfaces.MyAccountManager;
import com.oushang.lib_service.interfaces.IqiyiSdkManager;
import com.oushang.lib_service.interfaces.PlayListManager;
import com.oushang.lib_service.interfaces.PlayManager;
import com.oushang.lib_service.interfaces.SearchManager;
import com.oushang.lib_service.interfaces.VideoManager;

import java.util.HashMap;
import java.util.Objects;

/**
 * 服务工厂
 */
public class MediatorServiceFactory {
    private Application mApplication;
    //<接口类，实现类>
    private HashMap<Class, Class> serviceMap = new HashMap<>();
    //<实现类，实例>
    private HashMap<Class, Object> cacheServiceMap = new HashMap<>();
    private boolean isInit = false;

    private static class MediatorServiceHolder {
        private static MediatorServiceFactory HOLDER = new MediatorServiceFactory();
    }

    private MediatorServiceFactory(){}

    public static MediatorServiceFactory getInstance() {
        return MediatorServiceHolder.HOLDER;
    }

    public void init(Application application) {
        if (isInit) {
            return;
        }
        if (Objects.isNull(application)) {
            throw new IllegalArgumentException("application must be not null");
        }
        this.mApplication = application;
        registerService();
        isInit = true;
    }

    public Application getApplication() {
        return mApplication;
    }

    public Context getContext() {
        if (Objects.isNull(mApplication)) {
            throw new IllegalArgumentException("application must be not null,please init() in Application");
        }
        return mApplication.getApplicationContext();
    }

    /**
     * 注册服务
     */
    private void registerService() {
        LogUtils.d("registerService");
        put(IqiyiSdkManager.class, IqiyiSdkManagerService.class);
        put(MyAccountManager.class, MyAccountManagerService.class);
        put(PlayManager.class, PlayManagerService.class);
        put(PlayListManager.class, PlayListManagerService.class);
        put(ChannelManager.class, ChannelManagerService.class);
        put(SearchManager.class, SearchManagerService.class);
        put(VideoManager.class, VideoManagerService.class);
    }

    private  <T,E extends T> void put(Class<T> interClass, Class<E> implClass) {
        serviceMap.put(interClass, implClass);
    }

    /**
     * 获取服务
     * @param t 服务接口类
     * @param <E> 服务接口实现类
     * @param <T> 服务接口类型
     * @return 服务
     */
    public <E extends T,T> E get(T t) throws InstantiationException, IllegalAccessException {
        Class<?> eClass = serviceMap.get(t);
        E instanceClass = null;
        if (eClass != null && (instanceClass = (E)cacheServiceMap.get(eClass))!= null) {
            return instanceClass;
        }
        if (eClass != null) {
            instanceClass = (E) eClass.newInstance();
            cacheServiceMap.put(eClass, instanceClass);
        }
        return instanceClass;
    }

    public IqiyiSdkManager getIqiyiSdkManager() throws IllegalAccessException, InstantiationException {
        return get(IqiyiSdkManager.class);
    }

    public MyAccountManager getMyAccountManager() throws IllegalAccessException, InstantiationException {
        return get(MyAccountManager.class);
    }

    public PlayListManager getPlayListManager() throws IllegalAccessException, InstantiationException {
        return get(PlayListManager.class);
    }

    public PlayManager getPlayManager() throws IllegalAccessException, InstantiationException {
        return get(PlayManager.class);
    }

    public SearchManager getSearchManager() throws IllegalAccessException, InstantiationException {
        return get(SearchManager.class);
    }

    public VideoManager getVideoManager() throws IllegalAccessException, InstantiationException {
        return get(VideoManager.class);
    }

    public ChannelManager getChannelManager() throws IllegalAccessException, InstantiationException {
        return get(ChannelManager.class);
    }
}
