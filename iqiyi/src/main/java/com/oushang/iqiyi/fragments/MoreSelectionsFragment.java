package com.oushang.iqiyi.fragments;

import android.os.Bundle;
import android.util.Log;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.oushang.iqiyi.R;
import com.oushang.iqiyi.adapter.MoreSelectionsDigitalAdapter;
import com.oushang.iqiyi.base.BasePlayFragment;
import com.oushang.iqiyi.common.Constant;
import com.oushang.iqiyi.entries.SelectionEntry;
import com.oushang.iqiyi.events.EventBusHelper;
import com.oushang.iqiyi.events.EventConstant;
import com.oushang.iqiyi.mvp.presenter.MoreSelectionPresenter;
import com.oushang.iqiyi.mvp.view.IMoreSelectionView;
import com.oushang.iqiyi.ui.GridItemDecoration;
import com.oushang.iqiyi.utils.ThemeManager;
import com.oushang.lib_service.entries.VideoInfo;
import com.oushang.lib_service.interfaces.PlayManager;

import java.util.List;
import java.util.Optional;

import butterknife.BindView;

/**
 * @Author: zeelang
 * @Description: 更多选集
 * @Time: 2021/10/18 0018  10:22
 * @Since: 1.0
 */
public class MoreSelectionsFragment extends BasePlayFragment<MoreSelectionPresenter> implements IMoreSelectionView {
    private static final String TAG = MoreSelectionsFragment.class.getSimpleName();
    private static final String ALBUMID = "albumId";
    private static final String POS = "pos";
    private static final String NUM = "num";
    private static final String SORT = "sort";
    private static final String POSITION = "position"; //在FragmentStateAdapter中的位置

    private static final String ID = "id";

    @BindView(R.id.more_selections_rv)
    RecyclerView mMoreSelections;

    @Autowired(name = com.oushang.lib_service.constant.Constant.PATH_SERVICE_PLAY_MANAGER)
    PlayManager mPlayManager; //播放管理

    private int pos = 0;

    private int num = 0;

    private int position = -1;

    private long id = 0;

    private volatile static int sort = Constant.SORT_ORDER;

    private MoreSelectionsDigitalAdapter mMoreSelectionDigitalAdapter;

    @Override
    protected int setLayout() {
        return R.layout.fragment_more_selections;
    }

    @Override
    protected MoreSelectionPresenter createPresenter() {
        return new MoreSelectionPresenter();
    }

    public static MoreSelectionsFragment getInstance(long albumId, int pos, int num, long id, int sort) {
        Log.d(TAG, "albumId:" + albumId + ",pos:" + pos + ",num:" + num + ",id:" + id + ",sort:" + sort);
        MoreSelectionsFragment fragment = new MoreSelectionsFragment();
        Bundle args = new Bundle();
        args.putLong(ALBUMID, albumId);
        args.putInt(POS, pos);
        args.putInt(NUM, num);
        args.putLong(ID, id);
        args.putInt(SORT, sort);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initView() {
        super.initView();
        mMoreSelections.setLayoutManager(new GridLayoutManager(getContext(), 5));
        if (mMoreSelections.getItemDecorationCount() == 0) {
            mMoreSelections.addItemDecoration(new GridItemDecoration(29, GridItemDecoration.HORIZONTAL));
            mMoreSelections.addItemDecoration(new GridItemDecoration(30, GridItemDecoration.VERTICAL));
        }
    }

    @Override
    protected void initData() {
        super.initData();
        Bundle args = getArguments();
        if (args != null) {
            long albumId = args.getLong(ALBUMID);
            pos = args.getInt(POS);
            num = args.getInt(NUM);
            id = args.getLong(ID);
            sort = PlayVideoInfoMoreSelectionsFragment.sSort;
            presenter.loadLocalMoreSelection(pos, num);
        }

    }


    @Override
    public void onLoadSelection(List<SelectionEntry> selectionEntries) {
        Log.d(TAG, "selection Entries:" + selectionEntries);
//        mMoreSelections.setLayoutManager(new GridLayoutManager(getContext(), 5));
//        if(mMoreSelections.getItemDecorationCount() > 0) { //先清空itemDecoration
//            int count = mMoreSelections.getItemDecorationCount();
//            for(int i = 0; i < count; i++) {
//                mMoreSelections.removeItemDecorationAt(i);
//            }
//        }
//        if (mMoreSelections.getItemDecorationCount() == 0) {
//            mMoreSelections.addItemDecoration(new GridItemDecoration(25, GridItemDecoration.HORIZONTAL));
//            mMoreSelections.addItemDecoration(new GridItemDecoration(29, GridItemDecoration.VERTICAL));
//        }
        mMoreSelectionDigitalAdapter = new MoreSelectionsDigitalAdapter(getContext(), selectionEntries);
        mMoreSelections.setAdapter(mMoreSelectionDigitalAdapter);
        VideoInfo currentVideoInfo = mPlayManager.getCurrentVideoInfo();
//        Log.d(TAG, "currentVideoInfo:" + currentVideoInfo);
        if (currentVideoInfo != null) {
            mMoreSelectionDigitalAdapter.setSelected(currentVideoInfo);
        }
        Log.d(TAG, "sort:" + sort);
        if(PlayVideoInfoMoreSelectionsFragment.sSort == Constant.SORT_REVERSE) {
            mMoreSelectionDigitalAdapter.changeSort(sort);
        }
    }

    @Override
    public void onEvent(EventBusHelper.Event event) {
        super.onEvent(event);
        int eventType = event.getEventType();
        Bundle eventParams = event.getEventParams();
        switch (eventType) {
            case EventConstant.EVENT_TYPE_UPDATE_PLAY_VIDEO_INFO:
                Log.d(TAG, "update video info");
                if (mMoreSelectionDigitalAdapter!= null) {
                    VideoInfo videoInfo = eventParams.getParcelable(EventConstant.EVENT_PARAMS_UPDATE_PLAY_VIDEO_INFO);
                    Log.d(TAG, "videoInfo:" + videoInfo);
                    if (mMoreSelectionDigitalAdapter!= null) {
                        mMoreSelectionDigitalAdapter.setSelected(videoInfo);
                    }
                }
                break;
            case EventConstant.EVENT_TYPE_UPDATE_REMEMBER_PLAY_VIDEO_INFO:
                Log.d(TAG, "update remember video info");
                VideoInfo videoInfo2 = eventParams.getParcelable(EventConstant.EVENT_PARAMS_UPDATE_REMEMBER_PLAY_VIDEO_INFO);
                if (mMoreSelectionDigitalAdapter!= null) {
                    mMoreSelectionDigitalAdapter.setSelected(videoInfo2);
                }
                break;

            case EventConstant.EVENT_TYPE_EPISODE_LOAD_COMPLETE: //剧集加载完成
                Log.d(TAG, "event type episode load complete");
                if (presenter != null && mMoreSelectionDigitalAdapter == null) {
                    presenter.loadLocalMoreSelection(pos, num);
                }
                break;
            case EventConstant.EVENT_TYPE_REVERSE_CHANGE:
                Optional.ofNullable(eventParams).ifPresent(bundle -> {
                    int _sort = bundle.getInt(EventConstant.EVENT_PARAMS_SORT_CHANGED, Constant.SORT_ORDER);
                    long _id = bundle.getLong(EventConstant.EVENT_PARAMS_NOTIFY_FRAGMENT_ID, -1);
                    Optional.ofNullable(mMoreSelectionDigitalAdapter).ifPresent(moreSelectionsDigitalAdapter -> {
                        Log.d(TAG, "change sort:" + _sort + ",id:" + _id);
                        if(_id != -1 && id == _id && sort != _sort) {
                            sort = _sort;
                            Log.d(TAG, "changeSort:" + sort);
                            mMoreSelectionDigitalAdapter.changeSort(sort);
                        }
                    });
                });
                break;
        }
    }

    @Override
    public void onThemeChanged(ThemeManager.ThemeMode themeMode) {
        super.onThemeChanged(themeMode);
        if (mMoreSelectionDigitalAdapter!= null) {
            mMoreSelectionDigitalAdapter.updateSkin(themeMode);
        }
    }
}
