package com.oushang.iqiyi.base;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.chinatsp.ifly.aidlbean.CmdVoiceModel;
import com.chinatsp.ifly.aidlbean.MutualVoiceModel;
import com.chinatsp.ifly.aidlbean.NlpVoiceModel;
import com.chinatsp.ifly.voiceadapter.Business;
import com.chinatsp.ifly.voiceadapter.SpeechServiceAgent;
import com.google.gson.Gson;
import com.oushang.lib_base.R;
import com.oushang.lib_base.base.mvp.presenter.BasePresenter;
import com.oushang.lib_base.base.mvp.view.IBaseView;
import com.oushang.lib_base.event.RefreshStksEvent;
import com.oushang.voicebridge.VisibleData;
import com.oushang.voicebridge.VisibleDataHandler;
import com.oushang.voicebridge.VisibleDataProcessor;
import com.oushang.voicebridge.VoiceEventManager;
import com.oushang.voicebridge.visibledata.VisibleDataCollection;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.annotations.Nullable;

public abstract class BaseStksActivityMVP<P extends BasePresenter> extends AppCompatActivity implements IBaseView, VisibleDataHandler {
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
     * ???????????????????????????????????????????????????View????????????????????????,??????????????????????????????????????????????????????,????????????????????????????????????,????????????true,????????????????????????????????????,??????????????????????????????,??????????????????????????????
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
     * ?????????????????????????????????????????????????????????,text????????????????????????,????????????????????? {@link VisibleDataProcessor#DEFAULT_LIST_OPERATIONS} ?????????????????????????????????,?????????
     * {@link VisibleDataProcessor#setListOperations(List)} ???????????????????????????????????????????????????????????????
     *
     * @param view
     * @param text ???????????????????????????????????????
     * @return ?????????????????????, ??????true????????????false
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
//            visibleDataProcessor.registerStksWords(Business.IQIYI);
            VisibleDataCollection visibleData = visibleDataProcessor.getVisibleData();
            registerStks(visibleData);
            isInited = true;
        }
    }

    @Override
    public void notifyPageTextChanged() {
        VisibleDataCollection visibleData = visibleDataProcessor.getVisibleData();
        if (visibleData == null) {
            return;
        }
        registerStks(visibleData);
    }

    @Override
    public void removeKeywordsByScope(String scope) {
        visibleDataProcessor.removeByScope(scope);
    }

    public VisibleDataCollection getVisibleDataByScope(String scope) {
        return visibleDataProcessor.getVisibleDataOfScope(scope);
    }

    public void registerStks(VisibleDataCollection visibleDataCollection) {
        if (visibleDataCollection == null) {
            return;
        }
        if (visibleDataCollection.visibleData != null) {
            try {
                SpeechServiceAgent.getInstance().registerStksCommand(Business.IQIYI, new Gson().toJson(visibleDataCollection.visibleData));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        if (visibleDataCollection.wvmVisibleData != null) {
            try {
                SpeechServiceAgent.getInstance().registerStksCommandByMvw(Business.IQIYI, new Gson().toJson(visibleDataCollection.wvmVisibleData));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void unRegisterStks() {
        try {
            SpeechServiceAgent.getInstance().unRegisterStksCommand(Business.IQIYI);
            SpeechServiceAgent.getInstance().unRegisterStksCommandByMvw(Business.IQIYI);
        } catch (RemoteException e) {
            e.printStackTrace();
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
        //??????????????????????????????????????????EventBus, ?????????Activity????????????????????????,???????????????????????????
        if(visibleDataProcessor != null){
            visibleDataProcessor.unRegisterVoiceEventListener();
        }

        unRegisterStks();

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
