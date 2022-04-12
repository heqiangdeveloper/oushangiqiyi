package com.oushang.lib_service.response;

import java.util.Arrays;
import java.util.List;

/**
 * @Author: zeelang
 * @Description: 返回码
 * @Time: 2021/8/26 9:04
 * @Since: 1.0
 */
public class ReturnCode {

    public static class CodeInfo{
        protected Object errCode;
        protected String errMsg;
        protected String detailMsg;

        public CodeInfo(Object code, String detailMsg) {
            this.errCode = code;
            this.detailMsg = detailMsg;
        }

        public Object getCode() {
            return errCode;
        }

        public void setCode(Object code) {
            this.errCode = code;
        }

        public String getMsg() {
            return errMsg;
        }

        public void setMsg(String msg) {
            this.errMsg = msg;
        }

        public String getDetailMsg() {
            return detailMsg;
        }

        public void setDetailMsg(String detailMsg) {
            this.detailMsg = detailMsg;
        }

        @Override
        public String toString() {
            return "CodeInfo{" +
                    "errCode=" + errCode +
                    ", errMsg='" + errMsg + '\'' +
                    ", detailMsg='" + detailMsg + '\'' +
                    '}';
        }
    }

    public static class HttpCode extends CodeInfo{
        public static final int HTTP_200 = 200; //成功
        public static final int HTTP_201 = 201; //创建成功
        public static final int HTTP_204 = 204; //无返回内容
        public static final int HTTP_400 = 400; //该请求是无效的，详细的错误信息会说明原因
        public static final int HTTP_401 = 401; //验证失败，详细的错误信息会说明原因
        public static final int HTTP_403 = 403; //被拒绝调用，详细的错误信息会说明原因
        public static final int HTTP_404 = 404; //资源不存在
        public static final int HTTP_429 = 429; //超出了调用频率限制，详细的错误信息会说明原因
        public static final int HTTP_500 = 500; //服务器内部出错了，请联系我们尽快解决问题
        public static final int HTTP_504 = 504; //服务器在运行，本次请求响应超时，请稍后重试

        public HttpCode(Object code, String detailMsg) {
            super(code, detailMsg);
            setErrorMsg(code);
        }

        public HttpCode(Object code) {
            super(code, "");
            setErrorMsg(code);
        }

        public static boolean isOK(int code) {
            return HTTP_200 == code;
        }

        private void setErrorMsg(Object code) {
            if (code instanceof Integer) {
                setMsg((Integer) code);
            }
        }

        private void setMsg(int code) {
            errCode = code;
            switch (code) {
                case HTTP_200:
                    errMsg = "成功";
                    break;
                case HTTP_201:
                    errMsg = "创建成功";
                    break;
                case HTTP_204:
                    errMsg = "无返回内容";
                    break;
                case HTTP_400:
                    errMsg = "该请求是无效的，详细的错误信息会说明原因";
                    break;
                case HTTP_401:
                    errMsg = "验证失败，详细的错误信息会说明原因";
                    break;
                case HTTP_403:
                    errMsg = "被拒绝调用，详细的错误信息会说明原因";
                    break;
                case HTTP_404:
                    errMsg = "资源不存在";
                    break;
                case HTTP_429:
                    errMsg = "超出了调用频率限制，详细的错误信息会说明原因";
                    break;
                case HTTP_500:
                    errMsg = "服务器内部出错了，请联系我们尽快解决问题";
                    break;
                case HTTP_504:
                    errMsg = "服务器在运行，本次请求响应超时，请稍后重试";
                    break;
                default:
                    errMsg = "其他错误";
                    break;
            }
        }
    }

    public static class ErrorCode extends CodeInfo{

        //响应码
        public static final String E001 = "E001"; // 参数不合法
        public static final String E002 = "E002"; // 程序内部错误
        public static final String E003 = "E003"; // 其它未知错误
        public static final String E004 = "E004"; // 没有符合条件的数据
        public static final String E050 = "E050"; // 注册相关未知错误
        public static final String E051 = "E051"; // 设备型号不合法
        public static final String E052 = "E052"; // 设备型号已经停用
        public static final String E053 = "E053"; // 设备已经停用
        public static final String E054 = "E054"; // 授权过期
        public static final String E100 = "E100"; // 设备鉴权失败
        public static final String E101 = "E101"; // 设备鉴权数据错误
        public static final String E102 = "E102"; // 型号被禁用
        public static final String E103 = "E103"; // 设备被禁用
        public static final String E104 = "E104"; // 型号不存在
        public static final String E105 = "E105"; // 无效会话
        public static final String E150 = "E150"; // 获取epg列表数据失败
        public static final String E300 = "E300"; // 获取epg详情数据失败
        public static final String E301 = "E301"; // 详情不支持的数据类

        public static final List<String> ResponseError = Arrays.asList(
                E001,E002,E003,E004,E050,E051,E052,E053,E054,E100,E101,E102,E103,E104,E105,E150,E300,E301);

        //账号
        public static final String Q00311 = "Q00311"; // 鉴权失败，用户账号被临时封停
        public static final String Q00312 = "Q00312"; // 鉴权失败，用户账号被永久封停
        public static final String Q00313 = "Q00313"; // 用户登录态已失效
        public static final String Q00501 = "Q00501"; // 鉴权失败，并发监控云端控制
        public static final String Q00503 = "Q00503"; // 已登录用户：鉴权失败: 不允许试看
        public static final String Q00504 = "Q00504"; // 已登录用户：鉴权失败: 试看已结束
        public static final String Q00505 = "Q00505"; // 未登录用户：鉴权失败: 不允许试看
        public static final String Q00506 = "Q00506"; // 未登录用户：鉴权失败: 试看结束
        public static final String Q00507 = "Q00507"; // passport vinfo接口调用异常，鉴权失败
        public static final String A10001 = "A10001"; // 并发错误，同一VIP账号权益只能在一台设备生效
        public static final String A00000_501 = "A00000-501"; // 视频已在端上下线

        public static final List<String> AccountError = Arrays.asList(
                Q00311,Q00312,Q00313,Q00501,Q00503,Q00504,Q00505,Q00506,Q00507,A10001,A00000_501);

        //SDK初始化
        public static final int SDK_FAIL_ERROR = 3; //网络异常或者设备鉴权失败
        public static final int SDK_PARAMETER_ERROR = 4; //初始化参数不全，请检查参数设置
        public static final int SDK_PLUGIN_ERROR = -302; //插件加载失败

        public static final List<Integer> SDKError = Arrays.asList(SDK_FAIL_ERROR,SDK_PARAMETER_ERROR,SDK_PLUGIN_ERROR);

        public ErrorCode(Object code, String detailMsg) {
            super(code, detailMsg);
            setErrorMsg(code);
        }

        public ErrorCode(Object code) {
            super(code, "");
            setErrorMsg(code);
        }

        private void setErrorMsg(Object code) {
            if (code instanceof String) {
                if (!(setResponseErrorMsg((String) code) || setAccountErrorMsg((String) code))) {
                    errMsg = "code:" + code + ",其他的错误";
                }
            } else if (code instanceof Integer) {
                if (!setSDKErrorMsg((Integer) code)) {
                    errMsg = "code:" + code + ",其他的错误";
                }
            }

        }

        private boolean setResponseErrorMsg(String code) {
            if (code != null && !code.isEmpty()) {
                switch (code) {
                    case E001:
                        errMsg = "参数不合法";
                        break;
                    case E002:
                        errMsg = "程序内部错误";
                        break;
                    case E003:
                        errMsg = "其它未知错误";
                        break;
                    case E004:
                        errMsg = "没有符合条件的数据";
                        break;
                    case E050:
                        errMsg = "注册相关未知错误";
                        break;
                    case E051:
                        errMsg = "设备型号不合法";
                        break;
                    case E052:
                        errMsg = "设备型号已经停用";
                        break;
                    case E053:
                        errMsg = "设备已经停用";
                        break;
                    case E054:
                        errMsg = "授权过期";
                        break;
                    case E100:
                        errMsg = "设备鉴权失败";
                        break;
                    case E101:
                        errMsg = "设备鉴权数据错误";
                        break;
                    case E102:
                        errMsg = "型号被禁用";
                        break;
                    case E103:
                        errMsg = "设备被禁用";
                        break;
                    case E104:
                        errMsg = "型号不存在";
                        break;
                    case E105:
                        errMsg = "无效会话";
                        break;
                    case E150:
                        errMsg = "获取epg列表数据失败";
                        break;
                    case E300:
                        errMsg = "获取epg详情数据失败";
                        break;
                    case E301:
                        errMsg = "详情不支持的数据类型";
                        break;
                }
            }
            return errMsg != null && !errMsg.isEmpty();
        }

        private boolean setAccountErrorMsg(String code) {
            if (code != null && !code.isEmpty()) {
                switch (code) {
                    case Q00311:
                        errMsg = "鉴权失败，用户账号被临时封停";
                        break;
                    case Q00312:
                        errMsg = "鉴权失败，用户账号被永久封停";
                        break;
                    case Q00313:
                        errMsg = "用户登录态已失效";
                        break;
                    case Q00501:
                        errMsg = "鉴权失败，并发监控云端控制";
                        break;
                    case Q00503:
                        errMsg = "已登录用户：鉴权失败: 不允许试看";
                        break;
                    case Q00504:
                        errMsg = "已登录用户：鉴权失败: 试看已结束";
                        break;
                    case Q00505:
                        errMsg = "未登录用户：鉴权失败: 不允许试看";
                        break;
                    case Q00506:
                        errMsg = "未登录用户：鉴权失败: 试看结束";
                        break;
                    case Q00507:
                        errMsg = "passport vinfo接口调用异常，鉴权失败";
                        break;
                    case A10001:
                        errMsg = "并发错误，同一VIP账号权益只能在一台设备生效";
                        break;
                    case A00000_501:
                        errMsg = "视频已在端上下线";
                        break;
                }
            }
            return errMsg != null && !errMsg.isEmpty();
        }

        private boolean setSDKErrorMsg(int code) {
            switch (code) {
                case SDK_FAIL_ERROR:
                    errMsg = "网络异常或者设备鉴权失败";
                    break;
                case SDK_PARAMETER_ERROR:
                    errMsg = "初始化参数不全，请检查参数设置";
                    break;
                case SDK_PLUGIN_ERROR:
                    errMsg = "插件加载失败";
                    break;
            }
            return errMsg != null && !errMsg.isEmpty();
        }

        public static boolean isResponseError(Object errorCode) {
            if (errorCode instanceof String) {
                return ResponseError.contains(errorCode);
            }
            return false;
        }

        public static boolean isAccountError(Object errorCode) {
            if (errorCode instanceof String) {
                return AccountError.contains(errorCode);
            }
            return false;
        }

        public static boolean isSdkError(int errorCode) {
            return SDKError.contains(errorCode);
        }

    }
}
