package com.oushang.iqiyi.base;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.chinatsp.ifly.aidlbean.CmdVoiceModel;
import com.chinatsp.ifly.aidlbean.MutualVoiceModel;
import com.chinatsp.ifly.aidlbean.NlpVoiceModel;
import com.chinatsp.ifly.voiceadapter.Business;
import com.oushang.lib_base.R;
import com.oushang.lib_base.base.mvp.presenter.BasePresenter;
import com.oushang.lib_base.base.mvp.view.IBaseView;
import com.oushang.lib_base.event.RefreshStksEvent;
import com.oushang.voicebridge.VisibleDataProcessor;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.annotations.Nullable;

public abstract class BaseStksActivityMVP<P extends BasePresenter> extends AppCompatActivity implements IBaseView {
    protected P presenter;

    private Unbinder unbinder;

    protected abstract int setLayout();

    protected void initView(){}

    protected void initListener(){}

    protected void initData(){}

    protected abstract P createPresenter();

    public final VisibleDataProcessor visibleDataProcessor = new VisibleDataProcessor();

    private boolean isInited = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        visibleDataProcessor.setExtraMutualHandler(this::onMutualAction);
        visibleDataProcessor.setExtraSrHandler(this::onSrAction);
        visibleDataProcessor.setExtraMvwHandler(this::onMvwAction);
        visibleDataProcessor.setExtraStksHandler(this::onStksEvent);
        visibleDataProcessor.setOnViewNotFound(this::onViewNotFound);
        visibleDataProcessor.setVoiceEventConsumer(this::onCommandMatched);
        visibleDataProcessor.setListOperationConsumer(this::onListOperation);
        visibleDataProcessor.registerVoiceEventListener();

        setContentView(setLayout());
        unbinder = ButterKnife.bind(this);
        presenter = createPresenter();
        if (presenter != null) {
            presenter.attach(this);
        }
        initView();
        initListener();
        initData();

        EventBus.getDefault().register(this);
    }

    @NonNull
    @Override
    public AppCompatDelegate getDelegate() {
        if(visibleDataProcessor != null){
            return visibleDataProcessor.wrapAppCompatDelegate(super.getDelegate(), this);
        }else{
            return super.getDelegate();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RefreshStksEvent event){
        Log.d("VisibleDataProcessor","onEvent");
        if(event.getMessage() == RefreshStksEvent.EVENT_TYPE_REFRESH_STKS_WORDS){
            if(visibleDataProcessor != null) visibleDataProcessor.registerStksWords(Business.IQIYI);
        }else if(event.getMessage() == RefreshStksEvent.EVENT_TYPE_SCOPE_MY_SETTING){
            if(visibleDataProcessor != null) visibleDataProcessor.removeByScope(getString(R.string.scope_my_setting));
        }else if(event.getMessage() == RefreshStksEvent.EVENT_TYPE_SCOPE_CLEAR_CACHES){
            if(visibleDataProcessor != null) visibleDataProcessor.removeByScope(getString(R.string.scope_clear_caches));
        }else if(event.getMessage() == RefreshStksEvent.EVENT_TYPE_SCOPE_MY_SETTING_ITEM){
            if(visibleDataProcessor != null) visibleDataProcessor.removeByScope(getString(R.string.scope_my_setting_item));
        }else if(event.getMessage() == RefreshStksEvent.EVENT_TYPE_SCOPE_SETTING_CHILD){
            if(visibleDataProcessor != null) visibleDataProcessor.removeByScope(getString(R.string.scope_setting_child));
        }
    }

    public boolean onSrAction(NlpVoiceModel nlpVoiceModel) {
        return false;
    }

    public boolean onMutualAction(MutualVoiceModel mutualVoiceModel) {
        return false;
    }

    /**
     * 一般情况下可见即可说的指令会分发到View并触发对应的事件,但是如果有些场景需要对指令作其它处理,则可在这里先一步处理指令,如果返回true,则表示当前指令已经被消耗,模块不会再做后续分发,其它几个回调用法类似
     * @param cmdVoiceModel
     * @return
     */
    public boolean onStksEvent(CmdVoiceModel cmdVoiceModel) {
        return false;
    }


    public boolean onMvwAction(CmdVoiceModel cmdVoiceModel) {
        return false;
    }

    /**
     * 这里可以处理作用于列表的可见即可说操作,text为具体的语音指令,默认的指令可见 {@link VisibleDataProcessor#DEFAULT_LIST_OPERATIONS} 如需使用自己定义的指令,可通过
     * {@link VisibleDataProcessor#setListOperations(List)} 替换成自己定义的作用于列表的可见即可说指令
     *
     * @param view
     * @param text 语音服务分发过来的指令文本
     * @return 如果指令被消耗, 返回true否则返回false
     */
    protected boolean onListOperation(View view, String text) {
        return false;
    }

    protected void onCommandMatched(String command, View target) {
        visibleDataProcessor.performTouch(target, this::dispatchTouchEvent);
    }

    protected boolean onViewNotFound(String text) {
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!isInited){
            Log.d("VisibleDataProcessor","BaseStksActivityMVP refresh");
            visibleDataProcessor.registerStksWords(Business.IQIYI);
            isInited = true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(unbinder != null) {
            unbinder.unbind();
        }
        if (presenter != null) {
            presenter.detach();
            presenter = null;
        }
        //因为可见即可说的事件分发依赖EventBus, 所以在Activity退出时需要反注册,否则会引起内存泄露
        if(visibleDataProcessor != null){
            visibleDataProcessor.unRegisterVoiceEventListener();
        }

        if(EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showDialog() {

    }

    @Override
    public void dismissDialog() {

    }

    @Override
    public void showToast(int resId) {

    }

    @Override
    public void showToast(String msg) {

    }

    @Override
    public void showLoadNetErrorView() {

    }

    @Override
    public void showLoadNetErroView(boolean isTimeOut) {

    }

    @Override
    public void hideLoadNetErrorView() {

    }

    @Override
    public void showLoadEmptyView() {

    }

    @Override
    public void hideLoadEmptyView(String emptyText) {

    }

    @Override
    public void showQrCodeLoadingView() {

    }

    @Override
    public void showLogingQrCode(String code, int expire) {

    }

    @Override
    public void hideLoginQrCode() {

    }

    @Override
    public void showLoginQrCodeFail() {

    }

    @Override
    public void ShowLoginQrCodeInvalid() {

    }

    @Override
    public void AfterlogoutSuccess() {

    }

    @Override
    public void showBindLogingQrCode(Bitmap bitmap,int expire) {

    }
}
