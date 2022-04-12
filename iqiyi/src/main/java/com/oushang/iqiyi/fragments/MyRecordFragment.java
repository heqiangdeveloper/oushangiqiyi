package com.oushang.iqiyi.fragments;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.alibaba.android.arouter.launcher.ARouter;
import com.oushang.iqiyi.R;
import com.oushang.iqiyi.adapter.MyRecordAdapter;
import com.oushang.iqiyi.base.BaseLazyFragment;
import com.oushang.iqiyi.common.Constant;
import com.oushang.iqiyi.events.EventBusHelper;
import com.oushang.iqiyi.events.EventConstant;
import com.oushang.iqiyi.mvp.presenter.MyHistoryPresenter;
import com.oushang.iqiyi.mvp.view.IMyRecordView;
import com.oushang.iqiyi.statistics.DataStatistics;
import com.oushang.iqiyi.statistics.StatConstant;
import com.oushang.iqiyi.ui.LineItemDecoration;
import com.oushang.lib_base.utils.NetworkUtils;
import com.oushang.lib_base.utils.SPUtils;
import com.oushang.lib_service.entries.HistoryRecord;
import com.oushang.lib_service.entries.VideoInfoRecord;


import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @Author: zeelang
 * @Description: 我的历史/收藏
 * @Time: 2021/7/28 17:41
 * @Since: 1.0
 */
public class MyRecordFragment extends BaseLazyFragment<MyHistoryPresenter> implements IMyRecordView {
    private static final String TAG = MyRecordFragment.class.getSimpleName();

    @BindView(R.id.my_history_content_rv)
    RecyclerView mHistoryCotentrv;

    @BindView(R.id.my_record_abnormal_layout)
    LinearLayout mRecordAbnormalLayout;

    @BindView(R.id.my_record_abnormal_image)
    ImageView mRecordAbnormalImage;

    @BindView(R.id.my_record_abnormal_tips)
    TextView mRecordAbnormalTips;

    @BindView(R.id.my_record_abnormal_button)
    Button mRecordAbnormalBtn; //去首页看看

    @BindView(R.id.record_edit_btn)
    Button mRecordEditBtn;

    @BindView(R.id.my_record_edit_fun_layout)
    LinearLayout mRecordEditFunLayout; //编辑布局

    @BindView(R.id.my_record_edit_all_select)
    Button mRecordEditAllSelectedBtn;

    @BindView(R.id.my_record_edit_delete)
    Button mRecordEditDeleteBtn;

    @BindView(R.id.my_record_edit_cancel)
    Button mRecordEditCancel;

    @BindView(R.id.history_record_loading)
    ConstraintLayout mLoadingLayout;

    @BindView(R.id.history_record_loading_anim)
    ImageView mLoadingAnim; //加载动画

    private MyRecordAdapter myRecordAdapter;

    private int recyclerViewBottom;

    private boolean isObtainBottom;

    @Override
    protected int setLayout() {
        return R.layout.fragment_my_history;
    }

    @Override
    protected MyHistoryPresenter createPresenter() {
        return new MyHistoryPresenter();
    }

    public static MyRecordFragment newInstance(int recordType) {
        Log.d(TAG, "newInstance");
        Bundle bundle = new Bundle();
        bundle.putInt(Constant.BUNDLE_KEY_RECORD_TYPE, recordType);
        MyRecordFragment fragment = new MyRecordFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @OnClick({R.id.record_edit_btn,
            R.id.my_record_edit_all_select,
            R.id.my_record_edit_delete,
            R.id.my_record_edit_cancel,
            R.id.my_record_abnormal_button})
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.record_edit_btn: //启用编辑
                Log.d(TAG, "click record edit button");
                if (!view.isSelected()) {
                    view.setSelected(true);
                    mRecordEditFunLayout.setVisibility(View.VISIBLE); //显示编辑菜单
                    mRecordEditBtn.setVisibility(View.GONE);
                    if (myRecordAdapter != null) {
                        RecyclerView.ItemAnimator itemAnimator = mHistoryCotentrv.getItemAnimator();
                        if (itemAnimator instanceof SimpleItemAnimator) {
                            ((SimpleItemAnimator) itemAnimator).setSupportsChangeAnimations(false);
                        }
                        myRecordAdapter.setEditChange(true);
                    }
                } else {
                    view.setSelected(false);
                    mRecordEditFunLayout.setVisibility(View.GONE);
                    mRecordEditBtn.setVisibility(View.VISIBLE);
                    if (myRecordAdapter != null) {
                        RecyclerView.ItemAnimator itemAnimator = mHistoryCotentrv.getItemAnimator();
                        if (itemAnimator instanceof SimpleItemAnimator) {
                            ((SimpleItemAnimator) itemAnimator).setSupportsChangeAnimations(false);
                        }
                        myRecordAdapter.setEditChange(false);
                    }
                }
                break;
            case R.id.my_record_edit_all_select: //全选
                Log.d(TAG, "click record edit all select button");
                if (myRecordAdapter != null) {
                    myRecordAdapter.setAllSelect(true);
                }
                break;
            case R.id.my_record_edit_delete: //删除
                Log.d(TAG, "click record edit delete button");
                if (myRecordAdapter != null) {
                    if(myRecordAdapter.isAllSelected()) { //是否全选
                        presenter.clearHistoryRecord();
                        myRecordAdapter.deleteAllRecord();
                    } else {
                        List<HistoryRecord> deleteRecord = myRecordAdapter.getDeleteRecord();
                        Log.d(TAG, "delete:" + deleteRecord);
                        if (deleteRecord.size() == myRecordAdapter.getItemCount()) {
                            mRecordEditFunLayout.setVisibility(View.GONE);
                        }
                        if (!deleteRecord.isEmpty()) {
                            presenter.deleteHistoryRecord(deleteRecord);
                            myRecordAdapter.delete(deleteRecord);
                        }
                    }
                }
                break;
            case R.id.my_record_edit_cancel: //取消
                Log.d(TAG, "click record edit cancel button");
                if (myRecordAdapter != null) {
                    myRecordAdapter.setAllSelect(false);
                }
                mRecordEditBtn.setSelected(false);
                mRecordEditFunLayout.setVisibility(View.GONE);
                mRecordEditBtn.setVisibility(View.VISIBLE);
                break;
            case R.id.my_record_abnormal_button:
                Log.d(TAG, "click abnormal button");
                String text = ((Button) view).getText().toString();
                if (getString(R.string.my_history_empty_button).equals(text)) {
                    DataStatistics.recordUiEvent(StatConstant.EVENT_ID_5938);
                    Bundle eventParams = new Bundle();
                    eventParams.putInt(EventConstant.EVENT_PARAMS_SELECT_TAB_ID, R.id.nav_tab_home_page);
                    EventBusHelper.post(EventBusHelper.newEvent(EventConstant.EVENT_TYPE_LOAD_CHILD_FRAGMENT, eventParams));
                } else if (getString(R.string.my_favorite_empty_button).equals(text)) {
                    ARouter.getInstance().build(Constant.PATH_ACTIVITY_ACCOUNT).navigation();
                } else if (getString(R.string.my_record_reload_button).equals(text)) { //重新加载
                    DataStatistics.recordUiEvent(StatConstant.EVENT_ID_5939);//埋点数据
                    int status = SPUtils.getShareInteger(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_LOGIN_STATUS, 0);//登录状态
                    if (status == 1 && presenter.isLogin()) {
                        presenter.loadRemoteHistoryRecord(1);
                    } else {
                        presenter.loadLocalHistoryRecord();
                    }

//                    Bundle bundle = getArguments();
//                    if (bundle != null) {
//                        int recordType = bundle.getInt(Constant.BUNDLE_KEY_RECORD_TYPE);
//                        if (recordType == Constant.BUNDLE_VALUE_RECORD_TYPE_HISTORY && status == 1 && presenter.isLogin()) {
//                            presenter.loadRemoteHistoryRecord(1);
//                        } else {
//                            presenter.loadStoreRecord();
//                        }
//                    }
                }
                break;
        }
    }

    @Override
    protected void initView() {
        super.initView();
        mHistoryCotentrv.addItemDecoration(new LineItemDecoration(20, LineItemDecoration.HORIZONTAL));
    }

    @Override
    public void lazyInit() {
        Log.d(TAG, "lazyInit");
        Bundle bundle = getArguments();
        if (bundle == null) {
            Log.e(TAG, "bundle is null");
            bundle = new Bundle();
            bundle.putInt(Constant.BUNDLE_KEY_RECORD_TYPE, 1); //默认历史记录
        }
        int recordType = bundle.getInt(Constant.BUNDLE_KEY_RECORD_TYPE);
        int status = SPUtils.getShareInteger(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_LOGIN_STATUS, 0);//登录状态
        if (recordType == Constant.BUNDLE_VALUE_RECORD_TYPE_HISTORY) { //如果是历史记录
            Log.d(TAG, "load history record:" + recordType);
            if (status == 1 && presenter.isLogin()) {
                presenter.loadRemoteHistoryRecord(1); //如果已登录，则合并服务器上的历史记录
            } else if(!NetworkUtils.isNetworkAvailable()){
                showLoadNetErrorView();
            } else {
                presenter.loadLocalHistoryRecord(); //如果未登录，则获取本地历史记录
            }
        } else if (recordType == Constant.BUNDLE_VALUE_RECORD_TYPE_FAVORITE) {
            Log.d(TAG, "load favorite record:" + recordType);
            if (presenter.isLogin()) {
                presenter.loadStoreRecord(); //如果已登录，则加载收藏记录
            } else {
                presenter.loadEmptyRecord(Constant.BUNDLE_VALUE_RECORD_TYPE_FAVORITE); //如果未登录，则提示用户去登录
            }
        }
    }

    @Override
    public void onLoadVideoInfoRecord(List<VideoInfoRecord> videoInfoRecordList, int recordType) {
        Log.d(TAG, "onLoadVideoInfoRecord");
        if (videoInfoRecordList == null || videoInfoRecordList.isEmpty()) {
            if (recordType == Constant.BUNDLE_VALUE_RECORD_TYPE_HISTORY) {
                presenter.loadEmptyRecord(Constant.BUNDLE_VALUE_RECORD_TYPE_HISTORY);
            } else {
                presenter.loadEmptyRecord(Constant.BUNDLE_VALUE_RECORD_TYPE_FAVORITE);
            }
            return;
        }
        if (mRecordAbnormalLayout.getVisibility() == View.VISIBLE) {
            mRecordAbnormalLayout.setVisibility(View.GONE);
        }
        mHistoryCotentrv.setVisibility(View.VISIBLE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mHistoryCotentrv.setLayoutManager(layoutManager);
    }

    @Override
    public void onLoadEmptyRecord(int recordType) {
        if (recordType == Constant.BUNDLE_VALUE_RECORD_TYPE_HISTORY) {
            mRecordEditBtn.setVisibility(View.GONE);
            mHistoryCotentrv.setVisibility(View.GONE);
            mRecordEditFunLayout.setVisibility(View.GONE);
            mRecordAbnormalLayout.setVisibility(View.VISIBLE);
            mRecordAbnormalImage.setImageResource(R.drawable.my_history_empty);
            mLoadingLayout.setVisibility(View.GONE);
            mRecordAbnormalTips.setText(getResources().getText(R.string.my_history_empty));
            mRecordAbnormalBtn.setText(getResources().getText(R.string.my_history_empty_button));
        } else if (recordType == Constant.BUNDLE_VALUE_RECORD_TYPE_FAVORITE) {
            mHistoryCotentrv.setVisibility(View.GONE);
            mRecordAbnormalLayout.setVisibility(View.VISIBLE);
            mRecordAbnormalImage.setImageResource(R.drawable.my_history_empty);
            mRecordAbnormalTips.setText(getResources().getText(R.string.my_favorite_empty));
            mRecordAbnormalBtn.setText(getResources().getText(R.string.my_favorite_empty_button));
        }

    }

    @Override
    public void onLoadFail(int recordType) {
        if (recordType == Constant.BUNDLE_VALUE_RECORD_TYPE_HISTORY) {
            mRecordEditBtn.setVisibility(View.GONE);
            mHistoryCotentrv.setVisibility(View.GONE);
            mRecordAbnormalLayout.setVisibility(View.VISIBLE);
            mRecordAbnormalImage.setImageResource(R.drawable.my_history_empty);
            mLoadingLayout.setVisibility(View.GONE);
            mRecordAbnormalTips.setText(getResources().getText(R.string.my_history_load_fail));
            mRecordAbnormalBtn.setText(getResources().getText(R.string.my_record_reload_button));
        } else {
            mHistoryCotentrv.setVisibility(View.GONE);
            mRecordAbnormalLayout.setVisibility(View.VISIBLE);
            mRecordAbnormalImage.setImageResource(R.drawable.my_history_empty);
            mRecordAbnormalTips.setText(getResources().getText(R.string.my_favorite_load_fail));
            mRecordAbnormalBtn.setText(getResources().getText(R.string.my_record_reload_button));
        }
    }

    @Override
    public void onLoadHistoryRecord(List<HistoryRecord> historyRecordList) {
        if (historyRecordList == null || historyRecordList.isEmpty()) {
            presenter.loadEmptyRecord(Constant.BUNDLE_VALUE_RECORD_TYPE_HISTORY);
        }
        if (mRecordAbnormalLayout.getVisibility() == View.VISIBLE) { //隐藏暂无播放历史记录
            mRecordAbnormalLayout.setVisibility(View.GONE);
        }
        if (mHistoryCotentrv.getVisibility() == View.GONE) { //显示历史记录
            mHistoryCotentrv.setVisibility(View.VISIBLE);
        }
        if (mRecordEditBtn.getVisibility() == View.GONE) { //显示编辑按钮
            mRecordEditBtn.setVisibility(View.VISIBLE);
        }
        myRecordAdapter = new MyRecordAdapter(getContext(), historyRecordList);
        mHistoryCotentrv.setAdapter(myRecordAdapter);
        myRecordAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) { //删除监听
                super.onItemRangeRemoved(positionStart, itemCount);
                if(myRecordAdapter != null && myRecordAdapter.isEmpty()) { //如果删除完所有数据
                    presenter.loadEmptyRecord(Constant.BUNDLE_VALUE_RECORD_TYPE_HISTORY); //加载空提示
                }
            }
        });

        //处理进入搜索后，搜索键盘遮挡部分未显示
        mHistoryCotentrv.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (!isObtainBottom) {
                    recyclerViewBottom = bottom;
                    isObtainBottom = true;
                }
                if (bottom < recyclerViewBottom) {
                    myRecordAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void showLoading() {
        if(mRecordAbnormalLayout.getVisibility() == View.VISIBLE) {
            mRecordAbnormalLayout.setVisibility(View.GONE);
        }
//        mLoadingLayout.setVisibility(View.VISIBLE);
        if (mLoadingAnim.getVisibility() == View.GONE) {
            mLoadingAnim.setVisibility(View.VISIBLE);
        }
        mLoadingAnim.setImageResource(R.drawable.data_loading_anim_list);
        AnimationDrawable animationDrawable = (AnimationDrawable) mLoadingAnim.getDrawable();
        animationDrawable.start();
    }

    @Override
    public void hideLoading() {
//        mLoadingLayout.setVisibility(View.GONE);
        AnimationDrawable animationDrawable = (AnimationDrawable) mLoadingAnim.getDrawable();
        if (animationDrawable != null) {
            animationDrawable.stop();
        }
        if (mLoadingAnim.getVisibility() == View.VISIBLE) {
            mLoadingAnim.setVisibility(View.GONE);
        }
    }

    @Override
    public void showLoadNetErrorView() {
        mHistoryCotentrv.setVisibility(View.GONE);
        mRecordAbnormalLayout.setVisibility(View.VISIBLE);
        mRecordAbnormalImage.setImageResource(R.drawable.network_error);
        mRecordAbnormalTips.setText(getResources().getText(R.string.network_error));
        mRecordAbnormalBtn.setText(getResources().getText(R.string.my_record_reload_button));
    }

    @Override
    public void onEvent(EventBusHelper.Event event) {
        super.onEvent(event);
        int eventType = event.getEventType();
        Bundle eventParams = event.getEventParams();
        if (eventType == EventConstant.EVENT_TYPE_EDIT_ENABLE) { //启用编辑事件
            Log.d(TAG, "record edit event");
            int enable = eventParams.getInt(EventConstant.EVENT_PARAMS_MY_RECORD_EDIT_ENABLE);
            if (enable == EventConstant.MY_RECORD_EDITED) { //启用编辑
                if (myRecordAdapter != null) {
                    myRecordAdapter.setEditChange(true);
                }
            } else if (enable == EventConstant.MY_RECORD_UNEDITED) { //不启用编辑
                if (myRecordAdapter != null) {
                    myRecordAdapter.setEditChange(false);
                }
            }
        } else if (eventType == EventConstant.EVENT_TYPE_EDIT) { //编辑事件
            int select = eventParams.getInt(EventConstant.EVENT_PARAMS_MY_RECORD_EDIT);
            switch (select) {
                case EventConstant.MY_RECORD_ALL_SELECT:
                    Log.d(TAG, "my record all select");
                    if (myRecordAdapter != null) {
                        myRecordAdapter.setAllSelect(true);
                    }
                    break;
                case EventConstant.MY_RECORD_DELETE_SELECT:
                    Log.d(TAG, "my record delete select");
                    if (myRecordAdapter != null) {
                        List<HistoryRecord> deleteRecord = myRecordAdapter.getDeleteRecord();
                        Log.d(TAG, "delete:" + deleteRecord);
                        presenter.deleteHistoryRecord(deleteRecord);
                        myRecordAdapter.delete(deleteRecord);
                    }
                    break;
                case EventConstant.MY_RECORD_CANCEL_SELECT:
                    Log.d(TAG, "my record cancel select");
                    if (myRecordAdapter != null) {
                        myRecordAdapter.setAllSelect(false);
                    }
                    break;
            }

        } else if (eventType == EventConstant.EVENT_TYPE_UPDATE_PLAY_RECORD) {
            updateRecord();
        }

    }

    private void updateRecord(){
        Log.d(TAG, "update play record");
        //重置编辑区域
        if (mRecordEditFunLayout.getVisibility() == View.VISIBLE) {
            mRecordEditFunLayout.setVisibility(View.GONE);
        }
        mRecordEditBtn.setSelected(false);
        int status = SPUtils.getShareInteger(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_LOGIN_STATUS, 0);//登录状态
        if (status == 1 && presenter.isLogin()) { //是否已登录
            presenter.loadRemoteHistoryRecord(1); //如果已登录，则合并服务器上的历史记录
        } else {
            presenter.loadLocalHistoryRecord(); //如果未登录，则获取本地历史记录
        }
    }

    @Override
    public void onUpdateData() {
        updateRecord();
    }

}
