// IIqiyiLoginStatusCallback.aidl
package com.oushang.iqiyi;
import com.oushang.iqiyi.bean.User;

// Declare any non-default types here with import statements

interface IIqiyiAccountBindLoginCallback {

    void onBindLoginStatusChanged(boolean isLogin, inout User user);

}