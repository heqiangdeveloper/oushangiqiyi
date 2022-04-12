package com.oushang.lib_base.net.listener;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;

import okhttp3.Call;
import okhttp3.Connection;
import okhttp3.EventListener;
import okhttp3.Handshake;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @Author: zeelang
 * @Description: ***
 * @Time: 2021/11/16 0016  18:50
 * @Since: 1.0
 */
public class HttpEventListener extends EventListener {
    private static final String TAG = "IQIYI_HttpMonitor";
    private Call mCall;

    public static final Factory FACTORY = new Factory() {
        @NonNull
        @Override
        public EventListener create(@NonNull Call call) {
            return new HttpEventListener(call);
        }
    };

    public static Factory get() {
        return new Factory() {
            @NonNull
            @Override
            public EventListener create(@NonNull Call call) {
                return new HttpEventListener(call);
            }
        };
    }


    public HttpEventListener(Call call) {
        this.mCall = call;
    }

    /**
     * 当一个Call（代表一个请求）被同步执行或被添加异步队列中时，即会调用这个回调方法。
     * 需要说明这个方法是在dispatcher.executed/enqueue前执行的。
     * 由于线程或事件流的限制，这里的请求开始并不是真正的去执行的这个请求。
     * 如果发生重定向和多域名重试时，这个方法也仅被调用一次。
     */
    @Override
    public void callStart(@NonNull Call call) { //请求开始
        super.callStart(call);
        Log.d(TAG, "callStart:" + call.request());

    }

    /**
     * 其中的lookup(String hostname)方法代表了域名解析的过程，dnsStart/dnsEnd就是在lookup前后被调用的。
     * DNS解析是请求DNS（Domain Name System）服务器，将域名解析成ip的过程。
     * 域名解析工作是由JDK中的InetAddress类完成的。
     */
    @Override
    public void dnsStart(@NonNull Call call, @NonNull String domainName) { //dns解析开始
        super.dnsStart(call, domainName);
        Log.d(TAG, "dnsStart:" + domainName);
    }

    @Override
    public void dnsEnd(@NonNull Call call, @NonNull String domainName, @NonNull List<InetAddress> inetAddressList) { //dns解析结束
        super.dnsEnd(call, domainName, inetAddressList);
        Log.d(TAG, "dnsEnd:" + domainName + ",inetAddressList:" + inetAddressList);
    }

    /**
     * OkHttp是使用Socket接口建立Tcp连接的，所以这里的连接就是指Socket建立一个连接的过程。
     * 当连接被重用时，connectStart/connectEnd不会被调用。
     * 当请求被重定向到新的域名后，connectStart/connectEnd会被调用多次。
     */
    @Override
    public void connectStart(@NonNull Call call, @NonNull InetSocketAddress inetSocketAddress, @NonNull Proxy proxy) { //连接开始
        super.connectStart(call, inetSocketAddress, proxy);
        Log.d(TAG, "connectStart:" + inetSocketAddress);
    }

    /**
     * 在Socket建立连接后，会执行一个establishProtocol方法，这个方法的作用就是TSL/SSL握手。
     * 当存在重定向或连接重试的情况下，secureConnectStart/secureConnectEnd会被调用多次。
     */
    @Override
    public void secureConnectStart(@NonNull Call call) { //TLS安全连接开始
        super.secureConnectStart(call);
        Log.d(TAG, "secureConnectStart");
    }

    /**
     * 如果我们使用了HTTPS安全连接，在TCP连接成功后需要进行TLS安全协议通信，等TLS通讯结束后才能算是整个连接过程的结束，
     * 也就是说connectEnd在secureConnectEnd之后调用
     */
    @Override
    public void secureConnectEnd(@NonNull Call call, @Nullable Handshake handshake) { //TLS安全连接结束
        super.secureConnectEnd(call, handshake);
        Log.d(TAG, "secureConnectEnd: handshake:" + handshake);
    }

    /**
     * 因为创建的连接有两种类型（服务端直连和隧道代理），所以callEnd有两处调用位置。
     * 为了在基于代理的连接上使用SSL，需要单独发送CONECT请求。
     * 在连接过程中，无论是Socket连接失败，还是TSL/SSL握手失败，都会回调connectEnd。
     */
    @Override
    public void connectEnd(@NonNull Call call, @NonNull InetSocketAddress inetSocketAddress, @NonNull Proxy proxy, @Nullable Protocol protocol) { //连接结束
        super.connectEnd(call, inetSocketAddress, proxy, protocol);
        Log.d(TAG, "connectEnd:" + "inetSocketAddress:" + inetSocketAddress);
    }

    /**
     * 因为OkHttp是基于连接复用的，当一次请求结束后并不会马上关闭当前连接，而是放到连接池中。
     *
     * 当有相同域名的请求时，会从连接池中取出对应的连接使用，减少了连接的频繁创建和销毁。
     * 当根据一个请求从连接池取连接时，并打开输入输出流就是acquired，用完释放流就是released。
     * 如果直接复用StreamAllocation中的连接，则不会调用connectionAcquired/connectReleased。
     */
    @Override
    public void connectionAcquired(@NonNull Call call, @NonNull Connection connection) { //获取缓存连接后被调用
        super.connectionAcquired(call, connection);
        Log.d(TAG, "connectionAcquired:" + connection.socket().toString());
    }


    @Override
    public void connectionReleased(@NonNull Call call, @NonNull Connection connection) {
        super.connectionReleased(call, connection);
        Log.d(TAG, "connectionReleased:" + connection.socket().toString());
    }

    /**
     * request请求头开始
     * 该方法被CallServerInterceptor拦截器调用
     * 在OkHttp中，HttpCodec负责对请求和响应按照Http协议进行编解码，包含发送请求头、发送请求体、读取响应头、读取响应体。
     */
    @Override
    public void requestHeadersStart(@NonNull Call call) {
        super.requestHeadersStart(call);
        Log.d(TAG, "requestHeadersStart:" + call.request());
    }

    /**
     * request请求头结束
     * 该方法被CallServerInterceptor拦截器调用
     * 在OkHttp中，HttpCodec负责对请求和响应按照Http协议进行编解码，包含发送请求头、发送请求体、读取响应头、读取响应体。
     */
    @Override
    public void requestHeadersEnd(@NonNull Call call, @NonNull Request request) {
        super.requestHeadersEnd(call, request);
        Log.d(TAG, "requestHeadersEnd:" + request);
    }

    /**
     * request请求体开始
     * 该方法被CallServerInterceptor拦截器调用
     * 在OkHttp中，HttpCodec负责对请求和响应按照Http协议进行编解码，包含发送请求头、发送请求体、读取响应头、读取响应体。
     */
    @Override
    public void requestBodyStart(@NonNull Call call) {
        super.requestBodyStart(call);
        Log.d(TAG, "requestBodyStart:" + call.request().body());
    }

    /**
     * request请求体结束
     * 该方法被CallServerInterceptor拦截器调用
     * 在OkHttp中，HttpCodec负责对请求和响应按照Http协议进行编解码，包含发送请求头、发送请求体、读取响应头、读取响应体。
     */
    @Override
    public void responseBodyEnd(@NonNull Call call, long byteCount) {
        super.responseBodyEnd(call, byteCount);
        Log.d(TAG, "requestBodyStart:" + call.request().body() + ",byteCount:" + byteCount);
    }

    /**
     * response响应头开始
     * 该方法被CallServerInterceptor拦截器调用
     * 在OkHttp中，HttpCodec负责对请求和响应按照Http协议进行编解码，包含发送请求头、发送请求体、读取响应头、读取响应体。
     */
    @Override
    public void responseHeadersStart(@NonNull Call call) {
        super.responseHeadersStart(call);
        Log.d(TAG, "responseHeadersStart:");
    }

    /**
     * response响应头结束
     * 该方法被CallServerInterceptor拦截器调用
     * 在OkHttp中，HttpCodec负责对请求和响应按照Http协议进行编解码，包含发送请求头、发送请求体、读取响应头、读取响应体。
     */
    @Override
    public void responseHeadersEnd(@NonNull Call call, @NonNull Response response) {
        super.responseHeadersEnd(call, response);
        Log.d(TAG, "responseHeadersEnd:" + response.headers());
    }

    /**
     * response响应体开始
     * 该方法被HttpCodec.openResponseBody()调用
     * 在OkHttp中，HttpCodec负责对请求和响应按照Http协议进行编解码，包含发送请求头、发送请求体、读取响应头、读取响应体。
     */
    @Override
    public void responseBodyStart(@NonNull Call call) {
        super.responseBodyStart(call);
        Log.d(TAG, "responseBodyStart:");
    }

    /**
     * response响应体结束
     * 该方法被StreamAllocation.streamFinished()调用
     */
    @Override
    public void requestBodyEnd(@NonNull Call call, long byteCount) {
        super.requestBodyEnd(call, byteCount);
        Log.d(TAG, "requestBodyEnd:" + byteCount);
    }

    /**
     * 请求结束
     * callEnd有两种调用场景。
     * 第一种也是在关闭流时。
     * 第二种是在释放连接时。
     */
    @Override
    public void callEnd(@NonNull Call call) {
        super.callEnd(call);
        Log.d(TAG, "callEnd");
    }

    /**
     * 请求异常
     * callFailed在两种情况下被调用，
     * 第一种是在请求执行的过程中发生异常时。
     * 第二种是在请求结束后，关闭输入流时产生异常时。
     */
    @Override
    public void callFailed(@NonNull Call call, @NonNull IOException ioe) {
        super.callFailed(call, ioe);
        Log.e(TAG, "callFailed:" + Log.getStackTraceString(ioe));
    }
}
