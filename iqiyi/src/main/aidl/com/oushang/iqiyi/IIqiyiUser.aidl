// IIqiyiUser.aidl
package com.oushang.iqiyi;
import com.oushang.iqiyi.IIqiyiLoginStatusCallback;
import com.oushang.iqiyi.IIqiyiAccountBindLoginCallback;
import com.oushang.iqiyi.IIqiyiAccountUnbindCallback;
import com.oushang.iqiyi.bean.User;

// Declare any non-default types here with import statements

interface IIqiyiUser {

    void registerBindCallback(IIqiyiAccountBindLoginCallback callback);
    void unregisterBindCallback(IIqiyiAccountBindLoginCallback callback);
    void registerCallback(IIqiyiLoginStatusCallback callback);
    void unregisterCallback(IIqiyiLoginStatusCallback callback);
    void callbackFromSelf();
    User getUser();
    boolean isBindSuccess();
    boolean isLogin();
    void accountBindLogin(String accessToken,String ottVersion,String partnerEnv,IIqiyiAccountBindLoginCallback callback);
    void accountUnbind(String accessToken,String partnerEnv,IIqiyiAccountUnbindCallback callback);

}