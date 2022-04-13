package com.oushang.iqiyi.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.oushang.iqiyi.R;
import com.oushang.iqiyi.common.Constant;
import com.oushang.iqiyi.events.EventBusHelper;
import com.oushang.iqiyi.events.EventConstant;
import com.oushang.iqiyi.fragments.SearchHistoryFragment;
import com.oushang.iqiyi.fragments.SearchResultFragment;
import com.oushang.iqiyi.fragments.fragmentUtils.FragmentHelper;
import com.oushang.iqiyi.mvp.presenter.SearchPresenter;
import com.oushang.iqiyi.mvp.view.ISearchView;
import com.oushang.iqiyi.statistics.DataStatistics;
import com.oushang.iqiyi.statistics.StatConstant;
import com.oushang.iqiyi.ui.CustomSearchView;
import com.oushang.iqiyi.utils.StatusBarUtil;
import com.oushang.iqiyi.utils.ThemeContentObserver;
import com.oushang.iqiyi.utils.ThemeManager;
import com.oushang.lib_base.base.mvp.view.BaseActivityMVP;
import com.oushang.lib_base.utils.SPUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @Author: zeelang
 * @Description: 搜索界面
 * @Time: 2021/7/14 15:36
 * @Since: 1.0
 */
@Route(path = Constant.PATH_ACTIVITY_SEARCH)
public class SearchActivity extends BaseActivityMVP<SearchPresenter> implements ISearchView, EventBusHelper.EventListener,ThemeManager.OnThemeChangeListener {
    private static final String TAG = "xyj_iqiyi";

    private EventBusHelper mEventBusHelper;

    @BindView(R.id.search_root)
    ConstraintLayout mSearchRoot;

    @BindView(R.id.search_back)
    ImageView mSearchBack; //返回键

    @BindView(R.id.search_fragment_container)
    FrameLayout mFragmentContainer;

    @BindView(R.id.search_custom_view)
    CustomSearchView mSearchView;

    @Autowired(name = Constant.SEARCH_KEYWORD)
    String mSearchKeyword;

    @Autowired(name = Constant.SEARCH_TYPE)
    int mSearchType;

    private static final int KEYWORD_MAX_SIZE = 5;

    private SearchHistoryFragment mSearchHistoryFragment;

    private int mSaveFragmentType;

    private static final String SAVE_FRAGMENT = "save_fragment";
    private static final String SAVE_SEARCH_KEYWORD = "save_key_word";

    public static int sMode = 1;

    private ThemeContentObserver mThemeContentObserver;

    @Override
    protected int setLayout() {
        return R.layout.activity_search;
    }

    @Override
    protected SearchPresenter createPresenter() {
        return new SearchPresenter();
    }

    @Override
    protected void initView() {
        ARouter.getInstance().inject(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mSearchView.getSearchTextView().setText("");
        mSearchView.showSearchClearButton(false);
        Log.d(TAG, "SearchKeyword:" + mSearchKeyword + ",SearchType:" + mSearchType);
        if (mSearchKeyword != null && !mSearchKeyword.isEmpty()) {
            mSearchView.getSearchTextView().setText(mSearchKeyword);
            updateHistoryKeyRecord(mSearchKeyword);
        }
        if (mSearchType == Constant.SEARCH_TYPE_INPUT) {
            presenter.loadSearchHistory();
        } else if (mSearchType == Constant.SEARCH_TYPE_VOICE) {
            mSearchView.getSearchTextView().clearFocus();
            presenter.loadSearchResult(mSearchKeyword, mSearchType, 1);
        }

    }

    @Override
    protected void initListener() {
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { //提交监听
                Log.d(TAG, "call onQueryTextSubmit:" + query);
                if (query != null && !query.isEmpty()) {
                    DataStatistics.recordUiEvent(StatConstant.EVENT_ID_5950, query);//埋点数据
                    presenter.loadSearchResult(query, mSearchType, 1);
                    SearchActivity.updateHistoryKeyRecord(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {//输入监听
                Log.d(TAG, "call onQueryTextChange:" + newText);
                mSearchView.showSearchClearButton(false);
                return false;
            }
        });
        ThemeManager.getInstance().registerThemeChangeListener(this);
        mThemeContentObserver = new ThemeContentObserver(getApplicationContext(), new Handler());
        ThemeContentObserver.register(getApplicationContext(), mThemeContentObserver);
    }

    /**
     * 更新搜索列表
     * @param keyWord 搜索关键字
     */
    public static void updateHistoryKeyRecord(String keyWord) {
        if(keyWord == null || keyWord.isEmpty()) {
            return;
        }
        List<String> searchKeyWordList =  SPUtils.getShareValue(Constant.SP_SPACE_SEARCH_HISTORY_KEYWORD_RESULT,
                Constant.SP_KEY_SEARCH_HISTORY_KEYWORD, String.class);
        int size;
        if (searchKeyWordList.isEmpty()) { //如果搜索列表为空，则添加
            searchKeyWordList.add(keyWord);
        }else if (searchKeyWordList.stream().anyMatch(s -> s.equals(keyWord))) { //如果搜索的关键字已在搜索列表中，则更新到第1个位置
            searchKeyWordList.remove(keyWord);
            searchKeyWordList.add(0, keyWord);
        } else if ((size = searchKeyWordList.size()) >= KEYWORD_MAX_SIZE) { //如果搜索列表已达到或超过最大容量，则丢弃最后一个，并把该搜索关键字放在第1个位置
            searchKeyWordList.remove(size-1);
            searchKeyWordList.add(0, keyWord);
        } else { //其他正常添加
            searchKeyWordList.add(0, keyWord);
        }
        SPUtils.putShareValue(Constant.SP_SPACE_SEARCH_HISTORY_KEYWORD_RESULT,
                Constant.SP_KEY_SEARCH_HISTORY_KEYWORD, searchKeyWordList); //更新搜索列表
    }

    @OnClick(R.id.search_back)
    public void onClickBack() {
        FragmentHelper.popBackAll(getSupportFragmentManager());
        finish();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState");
        outState.putInt(SAVE_FRAGMENT, mSaveFragmentType);
        outState.putString(SAVE_SEARCH_KEYWORD, mSearchKeyword);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int saveFragment = savedInstanceState.getInt(SAVE_FRAGMENT, 0);
        String searchKeyWord = savedInstanceState.getString(SAVE_SEARCH_KEYWORD, "");
        int mode = sMode;
        Log.d(TAG, "onRestoreInstanceState saveFragment:" + saveFragment + ",searchKeyWord:" + searchKeyWord + ",mode:" + mode);
        if (saveFragment == 1) {
            presenter.loadSearchResult(searchKeyWord, mSearchType, mode);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mEventBusHelper = new EventBusHelper(this);
        mEventBusHelper.register();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mEventBusHelper != null) {
            mEventBusHelper.unRegister();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatusBarUtil.hideStatusBar(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String keyWord = intent.getStringExtra(Constant.SEARCH_KEYWORD);
        int type = intent.getIntExtra(Constant.SEARCH_TYPE, 0);
        Log.d(TAG, "onNewIntent: keyword:" + keyWord + ",type:" + type);
        setIntent(intent);
        initView();
    }

    @Override
    public void onLoadSearchHistory() {
        if (mSearchHistoryFragment == null) {
            mSearchHistoryFragment = SearchHistoryFragment.newInstance();
        }
        FragmentHelper.loadFragment(R.id.search_fragment_container, getSupportFragmentManager(), mSearchHistoryFragment, false);
    }

    @Override
    public void onLoadSearchResult(String searchKey, int searchType, int mode) {
        Log.d(TAG, "onLoadSearchResult：searchKey:" + searchKey + ",searchType:" + searchType + ",mode:" + mode);
        updateHistoryKeyRecord(searchKey);
        View view = getWindow().peekDecorView();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (view != null) {
            Log.d(TAG, "hideSoftInput");
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        if (searchType == Constant.SEARCH_TYPE_INPUT) {
            FragmentHelper.loadFragment(R.id.search_fragment_container, getSupportFragmentManager(), SearchResultFragment.newInstance(searchKey, mSearchType, mode), true);
        } else if(searchType == Constant.SEARCH_TYPE_VOICE) {
            FragmentHelper.loadFragment(R.id.search_fragment_container, getSupportFragmentManager(), SearchResultFragment.newInstance(searchKey, mSearchType, mode), false);
        }
        //数据保存
        mSaveFragmentType = 1;
        mSearchKeyword = searchKey;
    }


    @Override
    public void onEvent(EventBusHelper.Event event) {
        if (event != null) {
            int eventType = event.getEventType();
            if (eventType == EventConstant.EVENT_TYPE_UPDATE_SEARCH_RESULT) {
                Bundle eventParams = event.getEventParams();
                String searchKey = eventParams.getString(Constant.BUNDLE_KEY_SEARCH_KEY);
                int searchType = eventParams.getInt(Constant.BUNDLE_KEY_SEARCH_Type);
                presenter.loadSearchResult(searchKey, searchType, 1);

                //将点击搜索历史的内容输进搜索框中，bug1091454
                mSearchView.getSearchTextView().setText(searchKey);
                ((EditText)mSearchView.getSearchTextView()).setSelection(mSearchView.getSearchTextView().getText().length());
            }
        }
    }

    // 获取点击事件
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            Log.d(TAG, "view:" + view);
            if (isHideInput(view, ev)) {
                view.clearFocus();
                hideSoftInput(view.getWindowToken());
            }
            if(view instanceof SearchView.SearchAutoComplete) {
                DataStatistics.recordUiEvent(StatConstant.EVENT_ID_5949);//埋点数据
                if(mSearchType != Constant.SEARCH_TYPE_INPUT) {
                    mSearchType = Constant.SEARCH_TYPE_INPUT;//恢复输入搜索
                    Log.d(TAG, "change searchType input");
                    Bundle eventParams = new Bundle();
                    eventParams.putInt(Constant.BUNDLE_KEY_SEARCH_Type, mSearchType);
                    EventBusHelper.postStick(EventBusHelper.newEvent(EventConstant.EVENT_TYPE_SEARCH_TYPE_CHANGED, eventParams));
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    // 判定是否需要隐藏
    private boolean isHideInput(View v, MotionEvent ev) {
        if (v != null) {
            if (v instanceof EditText) {
                int[] l = {0, 0};
                v.getLocationInWindow(l);
                int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                        + v.getWidth();
                if (ev.getX() > left && ev.getX() < right && ev.getY() > top
                        && ev.getY() < bottom) {
                    return false;
                } else {
                    return true;
                }
            }
        }
        return false;
    }

    // 隐藏软键盘
    protected void hideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(token,
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.d(TAG, "onWindowFocusChanged:" + hasFocus);
        if (hasFocus) {
            StatusBarUtil.hideStatusBar(this);
        }
    }

    @Override
    public void onThemeChanged(ThemeManager.ThemeMode themeMode) {
        updateSkin();
    }

    /**
     * 主题换肤
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    private void updateSkin() {
        mSearchRoot.setBackgroundColor(getResources().getColor(ThemeManager.getThemeResource(getApplicationContext(), R.color.color_skin_background), null));
        mSearchBack.setBackground(getResources().getDrawable(ThemeManager.getThemeResource(getApplicationContext(), R.drawable.ic_skin_back_60), null));
        mSearchView.setBackground(getResources().getDrawable(ThemeManager.getThemeResource(getApplicationContext(), R.drawable.search_view_shape_skin), null));
        mSearchView.getSearchTextView().setTextColor(getResources().getColor(ThemeManager.getThemeResource(getApplicationContext(), R.color.color_skin_search_hint_text), null));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ThemeContentObserver.register(getApplicationContext(), mThemeContentObserver);
        ThemeManager.getInstance().unRegisterThemeChangeListener(this);
    }
}
