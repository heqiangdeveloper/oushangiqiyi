package com.oushang.lib_common_component.authentication;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.chinatsp.proxy.VehicleNetworkManager;

import java.io.File;

/**
 * @Author: zeelang
 * @Description: 实名认证
 * @Time: 2021/11/24 0024  15:57
 * @Since: 1.0
 */
public class RealNameAuthentication2 {
    private static final String TAG = RealNameAuthentication2.class.getSimpleName();

    /**
     * 是否认证
     * @param context context
     * @return
     */
    public static boolean isAuthenticationed(Context context) {
        Log.d(TAG, "isAuthenticationed");
//        File vehicle_file_new = new File("/data/navi/chinatsp/vehicle_status.txt");
//        if (!vehicle_file_new.exists()) {
//            Log.e(TAG, vehicle_file_new.getName() +" is not found");
//            return false;
//        }

        return VehicleNetworkManager.getInstance().isAuth(context);
    }

    public static void launchCertifyGuidePage(Context context) {
        Log.d(TAG, "launchCertifyGuidePage");
        try {
            Intent intent = new Intent();
            intent.putExtra("package", context.getPackageName());
            intent.setPackage("com.chinatsp.drivercenter");
            intent.setAction("com.chinatsp.drivercenter.CertifyGuideActivity");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
