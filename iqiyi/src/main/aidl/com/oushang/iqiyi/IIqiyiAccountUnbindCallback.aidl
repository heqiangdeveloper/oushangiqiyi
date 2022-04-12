// IIqiyiLoginStatusCallback.aidl
package com.oushang.iqiyi;
import com.oushang.iqiyi.bean.User;

// Declare any non-default types here with import statements

interface IIqiyiAccountUnbindCallback {

    void onUnbindStatusChanged(boolean isUnbindSuccess);

}