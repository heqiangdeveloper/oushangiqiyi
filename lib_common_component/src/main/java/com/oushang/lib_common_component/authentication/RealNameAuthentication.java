package com.oushang.lib_common_component.authentication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.chinatsp.proxy.VehicleNetworkManager;
import com.oushang.lib_common_component.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @Author: zeelang
 * @Description: 实名认证
 * @Time: 2021/9/22 17:24
 * @Since: 1.0
 */
public class RealNameAuthentication extends AppCompatActivity {
    private static final String TAG = RealNameAuthentication.class.getSimpleName();

    private TextView mActivationLater; //稍后激活

    private ImageView mClose; //退出

    private View mActivationPage; //激活提示页

    private View mQRCodePage; //扫码添加微信页

    private TextView mQrcodeTips;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_name_authentication);
        mActivationLater = findViewById(R.id.authentication_activation_later);
        mClose = findViewById(R.id.onestyle_wx_qrcode_close);
        mActivationPage = findViewById(R.id.authentication_start_page);
        mQRCodePage = findViewById(R.id.onestyle_wx_qrcode_page);
        mQrcodeTips = findViewById(R.id.authentication_oushang_qrcode_tips);
        init();
    }

    private void init() {
        if (mQrcodeTips != null) {
            String tips = getString(R.string.onstyle_activation_method2_content1);
            int start = tips.indexOf("欧");
            int end = tips.indexOf("A");
            Log.d(TAG, "start:" + start + ",end:" + end);
            SpannableStringBuilder span = new SpannableStringBuilder(tips);
            //设置文本颜色
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(getColor(R.color.color_onstyle_text)) {
                @Override
                public void updateDrawState(@NonNull TextPaint textPaint) {
                    textPaint.setColor(getColor(R.color.color_onstyle_text));
                }
            };
            span.setSpan(colorSpan, start-1, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            //设置文本的span
            mQrcodeTips.setMovementMethod(new LinkMovementMethod());
            mQrcodeTips.setText(span);

        }


        if (mActivationPage != null) {
            mActivationPage.setVisibility(View.VISIBLE);
        }
        if (mQRCodePage != null) {
            mQRCodePage.setVisibility(View.GONE);
        }

        if (mActivationLater != null) {
            mActivationLater.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mActivationPage != null) {
                        mActivationPage.setVisibility(View.GONE);
                    }
                    if (mQRCodePage != null) {
                        mQRCodePage.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
        if (mClose != null) {
            mClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    System.exit(0);
                }
            });
        }
    }

    public static void startAuthentication(Activity activity) {
        if (activity != null) {
            Intent intent = new Intent(activity, RealNameAuthentication.class);
            activity.startActivity(intent);
            activity.finish();
        } else {
            throw new RuntimeException("activity must be not null!");
        }
    }

    public static final String VEHICLE_STATUS_PATH = "/data/navi/chinatsp/vehicle_status.txt";

    public static boolean isAuthenticationed(Context context) {
        String vehicle_status = Settings.System.getString(context.getContentResolver(), "vehicle_status");
        if (TextUtils.isEmpty(vehicle_status)) {
            //读取文件内容
            String raw = readStringFromFile(VEHICLE_STATUS_PATH);
            if ("1".equals(raw)) {
                vehicle_status = "active";
            } else {
                Log.e(TAG, "has not authenticationed!");
            }
        }
        return "active".equals(vehicle_status);
    }

    public static String readStringFromFile(String filePath) {
        File file = new File(filePath);
        StringBuilder builder = new StringBuilder();
        char[] result= new char[1];
        if (file.exists()) {
            try (FileReader fileReader = new FileReader(file)) {
                while(fileReader.read(result) > 0) {
                    builder.append(result);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "file is not exsits");
        }
        return builder.toString();
    }
}
