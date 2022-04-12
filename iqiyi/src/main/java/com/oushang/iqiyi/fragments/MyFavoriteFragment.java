package com.oushang.iqiyi.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.alibaba.android.arouter.launcher.ARouter;
import com.oushang.iqiyi.R;
import com.oushang.iqiyi.adapter.MyFavoriteAdapter;
import com.oushang.iqiyi.base.BaseLazyFragment;
import com.oushang.iqiyi.common.Constant;
import com.oushang.iqiyi.events.EventBusHelper;
import com.oushang.iqiyi.events.EventConstant;
import com.oushang.iqiyi.mvp.presenter.MyFavoritePresenter;
import com.oushang.iqiyi.mvp.view.IMyFavoriteView;
import com.oushang.iqiyi.statistics.DataStatistics;
import com.oushang.iqiyi.statistics.StatConstant;
import com.oushang.iqiyi.ui.LineItemDecoration;
import com.oushang.lib_base.utils.SPUtils;
import com.oushang.lib_base.utils.ToastUtils;
import com.oushang.lib_service.entries.FavoriteRecord;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @Author: zeelang
 * @Description: 我的收藏界面
 * @Time: 2021/7/28 19:53
 * @Since: 1.0
 */
public class MyFavoriteFragment extends BaseLazyFragment<MyFavoritePresenter> implements IMyFavoriteView {
    private final static String TAG = MyFavoriteFragment.class.getSimpleName();

    @BindView(R.id.my_favorite_content_rv)
    RecyclerView mMyFavoriteContentRv; //收藏列表

    @BindView(R.id.my_favorite_abnormal_layout)
    LinearLayout mMyFavoriteAbnormalLayout; //暂无收藏布局

    @BindView(R.id.my_favorite_abnormal_tips)
    TextView mMyFavoriteAbnormalTips; //暂无收藏

    @BindView(R.id.my_favorite_abnormal_button)
    Button mMyFavoriteAbnormalBtn; //去登录按钮

    @BindView(R.id.favorite_edit_btn)
    Button mMyFavoriteEditBtn; //编辑按钮

    @BindView(R.id.my_favorite_edit_fun_layout)
    LinearLayout mMyFavoriteEditFunLayout; //编辑功能布局

    @BindView(R.id.my_favorite_record_loading)
    ConstraintLayout mMyFavoriteRecordLoading; //loading

    MyFavoriteAdapter mMyFavoriteAdapter; //收藏记录适配器

    private volatile boolean isEditEnable = false;//是否启用编辑

    private volatile boolean isAllSelected = false; //是否全选

    @Override
    protected int setLayout() {
        return R.layout.fragment_my_favorite;
    }

    @Override
    protected MyFavoritePresenter createPresenter() {
        return new MyFavoritePresenter();
    }

    public static MyFavoriteFragment newInstance() {
        Bundle args = new Bundle();
        MyFavoriteFragment myFavoriteFragment = new MyFavoriteFragment();
        myFavoriteFragment.setArguments(args);
        return myFavoriteFragment;
    }

    @Override
    protected void initView() {
        mMyFavoriteContentRv.addItemDecoration(new LineItemDecoration(20, LineItemDecoration.HORIZONTAL));
    }

    @Override
    public void lazyInit() {
        int status = SPUtils.getShareInteger(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_LOGIN_STATUS,0);
        if (status == 1 && presenter.isLogin()) { //已登录
            Log.d(TAG, "is login, load favorite record!");
            presenter.loadFavoriteRecord();
        } else { //未登录
            Log.d(TAG, "is logout, load empty record!");
            presenter.loadEmptyRecord();
        }
    }

    @OnClick({R.id.my_favorite_abnormal_button,
            R.id.favorite_edit_btn,
            R.id.my_favorite_edit_all_select,
            R.id.my_favorite_edit_delete,
            R.id.my_favorite_edit_cancel
    })
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.my_favorite_abnormal_button://点击按钮
                if (view instanceof Button) {
                    CharSequence text = ((Button) view).getText();
                    if (text.equals(getString(R.string.my_favorite_empty_button))) { //去登录
                        DataStatistics.recordUiEvent(StatConstant.EVENT_ID_5960);
                        ARouter.getInstance().build(Constant.PATH_ACTIVITY_ACCOUNT).navigation();
                    } else if (text.equals(getString(R.string.my_favorite_load_fail_button))) { //重新加载
                        presenter.loadFavoriteRecord();
                    } else if (text.equals(getString(R.string.my_history_empty_button))) { //去首页看看
                        DataStatistics.recordUiEvent(StatConstant.EVENT_ID_5938);
                        Bundle eventParams = new Bundle();
                        eventParams.putInt(EventConstant.EVENT_PARAMS_SELECT_TAB_ID, R.id.nav_tab_home_page);
                        eventParams.putSerializable(EventConstant.EVENT_PARAMS_FRAGMENT_CLASS, HomePageFragment.class);
                        Bundle args = new Bundle();
                        eventParams.putBundle(EventConstant.EVENT_PARAMS_FRAGMENT_ARGS, args);
                        EventBusHelper.post(EventBusHelper.newEvent(EventConstant.EVENT_TYPE_LOAD_CHILD_FRAGMENT, eventParams));
                    }
                }
                break;
            case R.id.favorite_edit_btn: //点击编辑
                if (mMyFavoriteEditFunLayout.getVisibility() == View.GONE) { //显示编辑功能
                    mMyFavoriteEditFunLayout.setVisibility(View.VISIBLE);
                }
                if (mMyFavoriteEditBtn.getVisibility() == View.VISIBLE) { //隐藏编辑按钮
                    mMyFavoriteEditBtn.setVisibility(View.GONE);
                }
                if (mMyFavoriteAdapter != null) {
                    RecyclerView.ItemAnimator itemAnimator = mMyFavoriteContentRv.getItemAnimator();
                    if (itemAnimator instanceof SimpleItemAnimator) {
                        ((SimpleItemAnimator) itemAnimator).setSupportsChangeAnimations(false); //取消动画避免闪烁
                    }
                    mMyFavoriteAdapter.setEditEnable(true);
                    isEditEnable = true;
                }
                break;
            case R.id.my_favorite_edit_all_select: //全选
                if (mMyFavoriteAdapter != null) {
                    mMyFavoriteAdapter.setAllSelect(true);
                    isAllSelected = true;
                }
                break;
            case R.id.my_favorite_edit_delete: //删除
                if (mMyFavoriteAdapter != null) {
                    if (mMyFavoriteAdapter.isAllSelect()) { //全选
                        presenter.clearFavoriteRecord(); //全空记录
                        mMyFavoriteAdapter.deleteAllRecord();
                    } else { //未全选
                        List<FavoriteRecord> selectedRecord = mMyFavoriteAdapter.getSelectedRecord();
                        if (selectedRecord != null && !selectedRecord.isEmpty()) {
                            mMyFavoriteAdapter.deleteRecordList(selectedRecord);
                            presenter.deleteFavoriteRecord(selectedRecord.toArray(new FavoriteRecord[0]));
                        }
                    }
                }
                break;
            case R.id.my_favorite_edit_cancel: //取消
                DataStatistics.recordUiEvent(StatConstant.EVENT_ID_5943);
                if (mMyFavoriteEditFunLayout.getVisibility() == View.VISIBLE) { //隐藏编辑功能
                    mMyFavoriteEditFunLayout.setVisibility(View.GONE);
                }
                if (mMyFavoriteEditBtn.getVisibility() == View.GONE) { //显示编辑按钮
                    mMyFavoriteEditBtn.setVisibility(View.VISIBLE);
                }
                if (mMyFavoriteAdapter != null) {
                    mMyFavoriteAdapter.setAllSelect(false);
                    mMyFavoriteAdapter.setEditEnable(false);
                }
                isEditEnable = false;
                isAllSelected = false;
                break;
        }

    }

    //加载收藏记录回调
    @Override
    public void onLoadFavoriteRecord(List<FavoriteRecord> favoriteRecordList) {
        Log.d(TAG, "onLoadFavoriteRecord:" + favoriteRecordList);
        if (mMyFavoriteContentRv.getVisibility() == View.GONE) { //显示收藏记录
            mMyFavoriteContentRv.setVisibility(View.VISIBLE);
        }
        if (mMyFavoriteAbnormalLayout.getVisibility() == View.VISIBLE) { //隐藏空记录
            mMyFavoriteAbnormalLayout.setVisibility(View.GONE);
        }
        if (mMyFavoriteEditBtn.getVisibility() == View.GONE) { //显示编辑按钮
            mMyFavoriteEditBtn.setVisibility(View.VISIBLE);
        }
        mMyFavoriteAdapter = new MyFavoriteAdapter(getContext(), favoriteRecordList);
        mMyFavoriteContentRv.setAdapter(mMyFavoriteAdapter);
        mMyFavoriteAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                super.onItemRangeChanged(positionStart, itemCount);
                Log.d(TAG, "adapter onItemRangeChanged");
                if (mMyFavoriteAdapter != null && mMyFavoriteAdapter.isEmpty()) {
                    Log.d(TAG, "adapter data is empty!");
                    isEditEnable = false; //置为不启用编辑状态
                    isAllSelected = false; //置为不全选
                    presenter.loadEmptyRecord();
                }
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) { //删除监听
                super.onItemRangeRemoved(positionStart, itemCount);
                Log.d(TAG, "adapter onItemRangeRemoved:" + itemCount);
                if (mMyFavoriteAdapter != null && mMyFavoriteAdapter.isEmpty()) { //如果删除完了
                    Log.d(TAG, "itemRemove, adapter data is empty!");
                    isEditEnable = false; //置为不启用编辑状态
                    isAllSelected = false; //置为不全选
                    presenter.loadEmptyRecord();
                }

            }

            @Override
            public void onChanged() {
                super.onChanged();
                Log.d(TAG, "adapter onChanged");
            }
        });
    }

    //空记录回调
    @Override
    public void onLoadEmptyRecord() {
        isEditEnable = false; //置为不启用编辑状态
        isAllSelected = false; //置为不全选

        if (mMyFavoriteContentRv.getVisibility() == View.VISIBLE) { //隐藏收藏记录
            mMyFavoriteContentRv.setVisibility(View.GONE);
        }
        if (mMyFavoriteEditFunLayout.getVisibility() == View.VISIBLE) { //隐藏编辑布局
            mMyFavoriteEditFunLayout.setVisibility(View.GONE);
        }
        if (mMyFavoriteAbnormalLayout.getVisibility() == View.GONE) { //显示空记录
            mMyFavoriteAbnormalLayout.setVisibility(View.VISIBLE);
        }
        if (mMyFavoriteEditBtn.getVisibility() == View.VISIBLE) { //隐藏编辑按钮
            mMyFavoriteEditBtn.setVisibility(View.GONE);
        }
        mMyFavoriteAbnormalTips.setText(R.string.my_favorite_empty);
        int status = SPUtils.getShareInteger(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_LOGIN_STATUS, 0);//登录状态
        if (status == 0 ||!presenter.isLogin()) { //不是登录状态
            mMyFavoriteAbnormalBtn.setVisibility(View.VISIBLE); //显示去登录按钮
            mMyFavoriteAbnormalBtn.setText(R.string.my_favorite_empty_button);
        } else {
            mMyFavoriteAbnormalBtn.setVisibility(View.VISIBLE); //显示去登录按钮
            mMyFavoriteAbnormalBtn.setText(R.string.my_history_empty_button);
        }
    }

    //加载失败回调
    @Override
    public void onLoadFailRecord() {
        Log.d(TAG, "onLoadFailRecord");
        if (mMyFavoriteContentRv.getVisibility() == View.VISIBLE) { //隐藏收藏记录
            mMyFavoriteContentRv.setVisibility(View.GONE);
        }
        if (mMyFavoriteAbnormalLayout.getVisibility() == View.GONE) { //显示空记录
            mMyFavoriteAbnormalLayout.setVisibility(View.VISIBLE);
        }
        if (mMyFavoriteEditBtn.getVisibility() == View.VISIBLE) { //隐藏编辑按钮
            mMyFavoriteEditBtn.setVisibility(View.GONE);
        }
        mMyFavoriteAbnormalTips.setText(R.string.my_favorite_load_fail);
        mMyFavoriteAbnormalBtn.setVisibility(View.VISIBLE);
        mMyFavoriteAbnormalBtn.setText(R.string.my_favorite_load_fail_button);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        if (mMyFavoriteAdapter != null && !mMyFavoriteAdapter.isEmpty()) { //数据不为空时
            Log.d(TAG, "isEditEnable:" + isEditEnable);
            mMyFavoriteAdapter.setEditEnable(isEditEnable);
            if(isAllSelected) {
                mMyFavoriteAdapter.setAllSelect(isAllSelected);
            } else {
                Bundle arguments = getArguments();
                if(arguments != null) {
                    Log.d(TAG, "get bundle data");
                    ArrayList<FavoriteRecord> selectedRecord = arguments.getParcelableArrayList("selectedRecord");
                    Log.d(TAG, "selectedRecord:" + selectedRecord);
                    mMyFavoriteAdapter.setSelectedRecord(selectedRecord, true);

                } else {
                    Log.e(TAG, "argument is null");
                }
            }
            if (isEditEnable) { //处在编辑状态
                if (mMyFavoriteEditFunLayout.getVisibility() == View.GONE) { //显示编辑功能
                    mMyFavoriteEditFunLayout.setVisibility(View.VISIBLE);
                }
                if (mMyFavoriteEditBtn.getVisibility() == View.VISIBLE) { //隐藏编辑按钮
                    mMyFavoriteEditBtn.setVisibility(View.GONE);
                }
            } else {
                if (mMyFavoriteEditFunLayout.getVisibility() == View.VISIBLE) { //隐藏编辑功能
                    mMyFavoriteEditFunLayout.setVisibility(View.GONE);
                }
                if (mMyFavoriteEditBtn.getVisibility() == View.GONE) { //显示编辑按钮
                    mMyFavoriteEditBtn.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        if(mMyFavoriteAdapter != null) {
            Log.d(TAG, "put bundle data");
            ArrayList<FavoriteRecord> selectedRecord = (ArrayList<FavoriteRecord>) mMyFavoriteAdapter.getSelectedRecord();
            if(getArguments() != null) {
                getArguments().putParcelableArrayList("selectedRecord", selectedRecord);
            } else {
                Log.e(TAG, "arguments is null");
            }
        }
    }

    @Override
    public void showLoading() {
        mMyFavoriteRecordLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        mMyFavoriteRecordLoading.setVisibility(View.GONE);
    }

    //网络异常
    @Override
    public void showLoadNetErrorView() {
        if (mMyFavoriteContentRv.getVisibility() == View.VISIBLE) { //隐藏收藏记录
            mMyFavoriteContentRv.setVisibility(View.GONE);
        }
        if (mMyFavoriteAbnormalLayout.getVisibility() == View.GONE) { //显示空记录
            mMyFavoriteAbnormalLayout.setVisibility(View.VISIBLE);
        }
        if (mMyFavoriteEditBtn.getVisibility() == View.VISIBLE) { //隐藏编辑按钮
            mMyFavoriteEditBtn.setVisibility(View.GONE);
        }
        mMyFavoriteAbnormalTips.setText(R.string.network_error);
        mMyFavoriteAbnormalBtn.setText(R.string.refresh_try);
    }

    @Override
    public void onEvent(EventBusHelper.Event event) {
        int eventType = event.getEventType();
        Bundle eventParams = event.getEventParams();
        switch (eventType) {
            case EventConstant.EVENT_TYPE_DELETE_FAVORITE_RECORD: //删除收藏记录
                if (eventParams != null) {
                    FavoriteRecord favoriteRecord = eventParams.getParcelable(EventConstant.EVENT_PARAMS_FAVORITE_RECORD);
                    if (mMyFavoriteAdapter != null) {
                        mMyFavoriteAdapter.deleteRecord(favoriteRecord);
                        presenter.deleteFavoriteRecord(favoriteRecord);
                    }
                }
                break;
            case EventConstant.EVENT_TYPE_LOGIN_CHANGE: //登录事件
                Log.d(TAG, "login change event");
                if (eventParams != null) {
                    int loginStatus = eventParams.getInt(EventConstant.EVENT_PARAMS_IS_LOGIN, 0);
                    if (loginStatus == 1) {
                        Log.d(TAG, "login in");
                        presenter.loadFavoriteRecord();
                    } else {
                        Log.d(TAG, "login out");
                        presenter.loadEmptyRecord();
                    }
                }
                break;
            case EventConstant.EVENT_TYPE_UPDATE_FAVORITE_RECORD: //收藏记录更新事件
                Log.d(TAG, "update favorite record");
                presenter.loadFavoriteRecord(); //重新加载
                break;
        }
    }
}
