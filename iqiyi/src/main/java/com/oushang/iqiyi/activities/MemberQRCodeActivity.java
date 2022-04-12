package com.oushang.iqiyi.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.WindowManager;
import android.widget.ImageView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.oushang.iqiyi.R;
import com.oushang.iqiyi.common.Constant;
import com.oushang.iqiyi.events.EventBusHelper;
import com.oushang.iqiyi.events.EventConstant;
import com.oushang.lib_base.base.mvp.presenter.BasePresenter;
import com.oushang.lib_base.base.mvp.view.BaseActivityMVP;
import com.oushang.lib_base.image.BitmapUtil;
import com.oushang.lib_base.utils.QRCodeUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @Author: zeelang
 * @Description: 会员扫码
 * @Time: 2021/8/2 19:20
 * @Since: 1.0
 */
@Route(path = Constant.PATH_ACTIVITY_MEMBER_QRCODE)
public class MemberQRCodeActivity extends BaseActivityMVP {

    @BindView(R.id.member_qrcode_image)
    ImageView mQRCode; //二维码

    @Override
    protected int setLayout() {
        return R.layout.activity_member_qrcode;
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @OnClick(R.id.member_back)
    public void onClickBack() {
        finish();
    }

    @Override
    protected void initView() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        String code = "请用手机微信、支付宝扫码开通会员";
        Bitmap qrLogo = BitmapFactory.decodeResource(getResources(), R.drawable.qr_code_logo);
        Bitmap qrImg = QRCodeUtils.createQRCodeBitmap(code,241,239,null, qrLogo,0.2f);
        mQRCode.setImageBitmap(BitmapUtil.cropRoundedCornerBitmap(qrImg,15, BitmapUtil.CORNER_ALL));
        mQRCode.setSelected(true);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            EventBusHelper.post(EventBusHelper.newEvent(EventConstant.EVENT_TYPE_FINISH_ACCOUNTACTIVITY)); //通知登录页关闭
        }
    }
}
