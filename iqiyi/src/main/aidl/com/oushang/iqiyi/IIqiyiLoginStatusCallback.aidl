// IIqiyiLoginStatusCallback.aidl
package com.oushang.iqiyi;
import com.oushang.iqiyi.bean.User;

// Declare any non-default types here with import statements

interface IIqiyiLoginStatusCallback {

    void onLoginStatusChanged(boolean isLogin, inout User user);

}