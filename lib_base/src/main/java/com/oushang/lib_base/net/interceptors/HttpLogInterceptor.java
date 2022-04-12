package com.oushang.lib_base.net.interceptors;

import android.text.TextUtils;
import android.util.Log;

import com.oushang.lib_base.log.LogUtils;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

public class HttpLogInterceptor implements Interceptor {
    private static final String TAG = "HttpLogInterceptor";
    private final Charset UTF8 = Charset.forName("UTF-8");

    @Override
    public Response intercept(Chain chain) throws IOException {
        StringBuilder strBuilder = new StringBuilder();

        Request request = chain.request();
        RequestBody requestBody = request.body();
        String body = "";
        if (requestBody != null) {
            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);
            Charset charset = UTF8;
            MediaType contentType = requestBody.contentType();
            if (contentType != null) {
                charset = contentType.charset(UTF8);
            }
            body = buffer.clone().readString(charset);
            if (TextUtils.isEmpty(body)) {
                body = URLDecoder.decode(body,"utf-8");
            }
        }
        strBuilder.append("\n请求方式:").append(request.method())
                .append("\n请求Url:").append(request.url())
                .append("\n请求头:").append(request.headers())
                .append("\n请求参数:").append(body);

        Log.d(TAG, strBuilder.toString());

        StringBuilder responseBuilder = new StringBuilder();
        try {
            Response response = chain.proceed(request);
            responseBuilder.append("\n请求Url:")
                    .append(response.request().url())
                    .append("\n响应码:")
                    .append(response.code());
            Log.d(TAG, responseBuilder.toString());
            response.close();
        } catch (IOException e) {
            Log.e(TAG, "response exception:" + Log.getStackTraceString(e));
        }
        return chain.proceed(request);
    }


    /**
     * Uicode 转
     * @param str
     * @return
     */
    private static String decodeUnicode(String str) {
        char ch;
        int len = str.length();
        StringBuffer outBuffer = new StringBuffer();
        for (int i = 0; i < len;) {
            ch = str.charAt(i++);
            if (ch == '\\') {
                ch = str.charAt(i++);
                if (ch == 'u') {
                    int value = 0;
                    for (int j = 0; j < 4; j++) {
                        ch = str.charAt(i++);
                        switch (ch) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                                value = (value << 4) + ch - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + ch - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + ch - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException("missing decode：" + str);
                        }
                    }
                    outBuffer.append((char)value);
                } else {
                    if (ch == 't') {
                        ch = '\t';
                    } else if (ch == 'r') {
                        ch = '\r';
                    } else if (ch == 'n') {
                        ch = '\n';
                    } else if (ch == 'f') {
                        ch = '\f';
                    }
                    outBuffer.append(ch);
                }
            } else {
                outBuffer.append(ch);
            }
        }

        return outBuffer.toString();
    }



}
