package com.oushang.lib_service.impls;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.google.gson.Gson;
import com.iqy.iv.player.ISdkError;
import com.iqy.iv.player.Parameter;
import com.iqy.iv.sdk.PlayerSdk;
import com.oushang.lib_service.MediatorServiceFactory;
import com.oushang.lib_service.callback.ISDKInitializedCallBack;
import com.oushang.lib_service.constant.Constant;
import com.oushang.lib_service.entries.DeviceRegister;
import com.oushang.lib_service.interfaces.IqiyiSdkManager;
import com.oushang.lib_service.iqiyiweb.IqiyiApi;
import com.oushang.lib_service.response.ReturnCode;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

@Route(path = Constant.PATH_SERVICE_SDK_MANAGER)
public class IqiyiSdkManagerService implements IqiyiSdkManager {
    private static final String TAG = IqiyiSdkManagerService.class.getSimpleName();

    private boolean isInit = false;

    //是否初始化
    private static volatile boolean mInitialized = false;

    //是否打印log
    private boolean isDebug = true;

    public IqiyiSdkManagerService() {
        if (!isInit) {
            init(MediatorServiceFactory.getInstance().getContext());
        }
    }

    @Override
    public void init(Context context) {
        isInit = true;
    }

    /**
     * 初始化sdk
     */
    @Override
    public void initSdk(ISDKInitializedCallBack callBack) {
        Log.d(TAG, "initGalaPlayerSdk");
        if (mInitialized) {
            return;
        }
        Parameter parameter = Parameter.createInstance();
        parameter.setEnableResumePlay(false);
        parameter.setDebug(isDebug);
        parameter.setEnableCoreLog(false);
        parameter.setEnableAudioLog(false);
        parameter.setEnablePlayerLog(false);
        parameter.setEnableControlAudioFocus(false);
        long startTime = System.currentTimeMillis();
        PlayerSdk.getInstance().initialize(MediatorServiceFactory.getInstance().getApplication(), null, parameter, new PlayerSdk.OnInitializedListener() {
            @Override
            public void onSuccess() {
                long endTime = System.currentTimeMillis();
                long takeTime = endTime - startTime;
                mInitialized = true;
                Log.d(TAG, "initialize playSdk success:" + takeTime + "ms");
                if (callBack != null) {
                    callBack.onSuccess();
                }
            }

            @Override
            public void onFailed(ISdkError iSdkError) {
                long endTime = System.currentTimeMillis();
                long takeTime = endTime - startTime;
                int errCode = iSdkError.getCode();
                String msg = iSdkError.getMessage();
                Log.e(TAG, "initialize playSdk onFailed {errCode:" + errCode + "" + msg + "}," + "takeTime:" + takeTime + "ms");
                if (ReturnCode.ErrorCode.isSdkError(errCode)) {
                    ReturnCode.ErrorCode errorCode = new ReturnCode.ErrorCode(errCode, iSdkError.getErrorInfo());
                    Log.e(TAG, "initialize playSdk onFailed:" + errorCode);
                }
                if (callBack != null) {
                    callBack.onFailure(errCode, msg);
                }
            }
        });
    }



    /**
     * sdk 是否初始化
     *
     * @return true or false
     */
    @Override
    public boolean isSdkInitialized() {
        return mInitialized;
    }


    /**
     *  注册设备
     * @param deviceUUID 硬件型号uuid，由爱奇艺分配
     * @param sn sn 车架号
     * @return 注册信息
     */
    @Override
    public Observable<DeviceRegister> register(String deviceUUID, String sn) {
        Log.d(TAG, "register");
        return IqiyiApi.register(null, deviceUUID, sn, null, null).map(new Function<String, DeviceRegister>() {
            @Override
            public DeviceRegister apply(@NonNull String s) throws Exception {
                Log.d(TAG, "register response:" + s);
                Gson gson = new Gson();
                return gson.fromJson(s, DeviceRegister.class);
            }
        });
    }
}
