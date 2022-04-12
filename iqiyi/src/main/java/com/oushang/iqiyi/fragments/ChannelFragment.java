package com.oushang.iqiyi.fragments;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.oushang.iqiyi.R;
import com.oushang.iqiyi.adapter.ChannelTagAdapter;
import com.oushang.iqiyi.base.BaseLazyFragment;
import com.oushang.iqiyi.common.Constant;
import com.oushang.iqiyi.entries.ChannelTag;
import com.oushang.iqiyi.events.EventBusHelper;
import com.oushang.iqiyi.events.EventConstant;
import com.oushang.iqiyi.mvp.presenter.ClassifyPresenter;
import com.oushang.iqiyi.mvp.view.IClassifyView;
import com.oushang.iqiyi.ui.GridItemDecoration;
import com.oushang.iqiyi.ui.TagView;
import com.oushang.lib_base.utils.HandlerUtils;
import com.oushang.lib_base.utils.SPUtils;
import com.oushang.voicebridge.Util;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @Author: zeelang
 * @Description: 分类页面
 * @Time: 2021/7/12 10:03
 * @Since: 1.0
 */
@Deprecated
public class ChannelFragment extends BaseLazyFragment<ClassifyPresenter> implements IClassifyView {
    private static final String TAG = ChannelFragment.class.getSimpleName();

    @BindView(R.id.channel_edit_btn)
    Button channelEditEnable;//编辑按钮

    @BindView(R.id.normal_channel_rv)
    RecyclerView commonChannelrv; //常用频道

    @BindView(R.id.all_channel_rv)
    RecyclerView allChannelrv; //所有频道

    @BindView(R.id.channel_tag_empty)
    TextView channelTagEmpty; //常用频道为空

    @BindView(R.id.normal_channel_layout)
    RelativeLayout mNormalChannelLayout;

    //常用频道适配器
    private ChannelTagAdapter commonChannelAdapter;

    //所有频道适配器
    private ChannelTagAdapter allChannelAdapter;

    //待删除的频道
    private List<ChannelTag> mDeleteChannelTag;

    //常用频道编辑监听
    private ChannelTagAdapter.OnItemEditClickListen onCommonChannelTagEditClickListen;

    //常用频道点击监听
    private ChannelTagAdapter.OnItemClickListen onCommonChannelTagItemListen;

    @Override
    protected int setLayout() {
        return R.layout.fragment_classify;
    }

    @Override
    protected ClassifyPresenter createPresenter() {
        return new ClassifyPresenter();
    }

    public static ChannelFragment newInstance() {
        return new ChannelFragment();
    }

    @Override
    public void lazyInit() {
        //加载常用频道
        presenter.loadNormalChannelTag();
        //加载所有频道
        presenter.loadAllChannelTag();
    }

    @Override
    protected void initData() {
        mDeleteChannelTag = new ArrayList<>();
    }

    @Override
    protected void initView() {
        commonChannelrv.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));
        if (commonChannelrv.getItemDecorationCount() == 0) {
            commonChannelrv.addItemDecoration(new GridItemDecoration(7, GridItemDecoration.HORIZONTAL));
        }
        if (allChannelrv.getItemDecorationCount() == 0) {
            allChannelrv.addItemDecoration(new GridItemDecoration(7, GridItemDecoration.HORIZONTAL));
            allChannelrv.addItemDecoration(new GridItemDecoration(11, GridItemDecoration.VERTICAL));
        }
    }

    @Override
    protected void initListener() {
        //常用频道编辑
        onCommonChannelTagEditClickListen = (view, state, position) -> {
            Log.d(TAG,"state:" + state);
            if (state == TagView.EditState.DELETABLE) { //如果编辑状态为删除状态
                Log.d(TAG, "delete position:" + position);
                List<ChannelTag> channelTags = SPUtils.getShareValue(Constant.SP_SPACE_NORMAL_CHANNEL, Constant.SP_KEY_NORMAL_CHANNEL, ChannelTag.class);
                if (!channelTags.isEmpty()) {
                    ChannelTag channelTag = channelTags.get(position);//获取该选中编辑的频道
                    mDeleteChannelTag.add(channelTag);//添加到待删除的列表中
                }
            }
        };

        //常用频道点击
        onCommonChannelTagItemListen = (view, position) -> {
            Log.d(TAG, "click common channel position:" + position);
            ChannelTag tag = commonChannelAdapter.getData().get(position);
            showClassifyDetailsFragment(tag);
        };

        SPUtils.getSP(Constant.SP_SPACE_NORMAL_CHANNEL).registerOnSharedPreferenceChangeListener(preferenceChangeListener);

    }

    //监听SharePreference变化
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(Constant.SP_KEY_NORMAL_CHANNEL)) {
                List<ChannelTag> tagList = SPUtils.getShareValue(Constant.SP_SPACE_NORMAL_CHANNEL, Constant.SP_KEY_NORMAL_CHANNEL, ChannelTag.class);
                Log.d(TAG, "get common channelTag:" + tagList);
                if (commonChannelAdapter == null) {
                    commonChannelAdapter = new ChannelTagAdapter(getContext(), tagList);
                    commonChannelrv.setAdapter(commonChannelAdapter);
                    commonChannelAdapter.setOnItemEditClickListen(onCommonChannelTagEditClickListen);
                    commonChannelAdapter.setOnItemClickListen(onCommonChannelTagItemListen);
                    channelTagEmpty.setVisibility(View.GONE);
                    commonChannelrv.setVisibility(View.VISIBLE);
                }else {
                    commonChannelAdapter.setData(tagList);
                    if (commonChannelAdapter.getData() != null && commonChannelAdapter.getData().isEmpty()) { //如果没有数据
                        if (channelTagEmpty.getVisibility() == View.GONE) {
                            channelTagEmpty.setVisibility(View.VISIBLE);
                            commonChannelrv.setVisibility(View.GONE);
                        }
                    } else if (commonChannelAdapter.getData() != null && commonChannelAdapter.getData().size() > 0) {
                        if (commonChannelrv.getVisibility() == View.GONE) {
                            channelTagEmpty.setVisibility(View.GONE);
                            commonChannelrv.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        SPUtils.getSP(Constant.SP_SPACE_NORMAL_CHANNEL).unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
    }

    //点击频道编辑
    @OnClick(R.id.channel_edit_btn)
    public void onEditEnable() {
        if(channelEditEnable.getText().equals(getString(R.string.edit))) { //编辑
            channelEditEnable.setText(R.string.cancel_edit);
            if (commonChannelAdapter != null) {
                commonChannelAdapter.setEditEnable(true);//常用频道启用编辑
            }
            if (allChannelAdapter != null) {
                allChannelAdapter.setEditEnable(true);//所有频道启用编辑
                initAllChannelTagEditState();
                compareAndUpdateAllChannel();
            }
        } else {
            channelEditEnable.setText(R.string.edit); //取消编辑
            deleteAndUpdateChannel();
            if (commonChannelAdapter != null) {
                commonChannelAdapter.setEditEnable(false); //常用频道不启用编辑
            }
            if (allChannelAdapter != null) {
                allChannelAdapter.setEditEnable(false); //所有频道不启用编辑
            }
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    private void compareAndUpdateAllChannel() { //比较常用频道，更新所有频道中的状态
        //获取所有频道的数据
        List<ChannelTag> allChannelTagList = allChannelAdapter.getData();
        //获取常用频道的数据
        List<ChannelTag> commonChannelTags = SPUtils.getShareValue(Constant.SP_SPACE_NORMAL_CHANNEL, Constant.SP_KEY_NORMAL_CHANNEL, ChannelTag.class);

        if (commonChannelTags.size() > 0 && channelTagEmpty.getVisibility() == View.VISIBLE) {
            channelTagEmpty.setVisibility(View.VISIBLE);
            commonChannelrv.setVisibility(View.GONE);
        }

        //遍历所有常用频道
        commonChannelTags.forEach(tag -> allChannelTagList.forEach(ctag -> {
                    if (ctag.equals(tag)) {
                        int index = allChannelTagList.indexOf(ctag);
                        ChannelTag ct = allChannelTagList.get(index);
                        ct.setEditState(ChannelTag.COMPLETE_STATE);//设置为已完成状态
                        allChannelAdapter.notifyItemChanged(index);//更新数据
                    }
                }));
    }

    private void initAllChannelTagEditState() { //初始化所有频道状态
        List<ChannelTag> allChannelTagList = allChannelAdapter.getData();
        allChannelTagList.forEach(atag -> {
            if (atag.getEditState() != ChannelTag.ADD_STATE) {
                int index = allChannelTagList.indexOf(atag);
                ChannelTag ct = allChannelTagList.get(index);
                ct.setEditState(ChannelTag.ADD_STATE);//设置可添加状态
                allChannelAdapter.notifyItemChanged(index);//更新数据
            }
        });
    }

    private void deleteAndUpdateChannel() { //删除和更新频道状态
        if (mDeleteChannelTag != null && !mDeleteChannelTag.isEmpty()) { //如果待删除的频道列表不为空
            //获取常用频道
            List<ChannelTag> channelTags = SPUtils.getShareValue(Constant.SP_SPACE_NORMAL_CHANNEL, Constant.SP_KEY_NORMAL_CHANNEL, ChannelTag.class);
            //获取所有频道数据
            List<ChannelTag> allChannelTagList = allChannelAdapter.getData();
            //新的常用频道
            List<ChannelTag> newChannelTags = new ArrayList<>();
            //所有频道列表与删除列表比较
            mDeleteChannelTag.forEach(dtag -> {
                allChannelTagList.forEach(atag -> {
                    if (atag.equals(dtag)) { //如果删除列表中有所有频道的tag
                        int index = allChannelTagList.indexOf(atag);
                        ChannelTag ct = allChannelTagList.get(index);
                        ct.setEditState(ChannelTag.ADD_STATE);//设置为可添加状态
                        allChannelAdapter.notifyItemChanged(index);//更新数据
                    }
                });
            });
            //过滤出新的常用频道
            newChannelTags = channelTags.stream().filter(ctag -> {
                for (ChannelTag tag: mDeleteChannelTag) {
                    if (tag.equals(ctag)) {
                        return false;
                    }
                }
                return true;
            }).collect(Collectors.toList());
            Log.d(TAG, "new channelTag list:" + newChannelTags);

            SPUtils.putShareValue(Constant.SP_SPACE_NORMAL_CHANNEL, Constant.SP_KEY_NORMAL_CHANNEL, newChannelTags);
            mDeleteChannelTag.clear();
        }
    }

    //加载常用频道
    @Override
    public void onLoadCommonChannelTagList(List<ChannelTag> channelTagList) {
        if(channelTagList == null || channelTagList.isEmpty()) { //如果常用频道为空
            channelTagEmpty.setVisibility(View.VISIBLE);
            commonChannelrv.setVisibility(View.GONE);
        } else { //常用频道不为空
            commonChannelAdapter = new ChannelTagAdapter(this.getContext(), channelTagList);
            commonChannelrv.setAdapter(commonChannelAdapter);
            //点击item
            commonChannelAdapter.setOnItemClickListen(onCommonChannelTagItemListen);

            //编辑item
            commonChannelAdapter.setOnItemEditClickListen(onCommonChannelTagEditClickListen);
        }
    }

    //加载所有频道
    @Override
    public void onLoadAllChannelTagList(List<ChannelTag> channelTagList) {
        allChannelAdapter = new ChannelTagAdapter(this.getContext(), channelTagList);
        allChannelrv.setAdapter(allChannelAdapter);
        //点击Item
        allChannelAdapter.setOnItemClickListen(new ChannelTagAdapter.OnItemClickListen() {
            @Override
            public void onClick(View view, int position) {
                addCommonChannel(channelTagList.get(position));
                showClassifyDetailsFragment(channelTagList.get(position));
            }
        });
        //编辑item
        allChannelAdapter.setOnItemEditClickListen(new ChannelTagAdapter.OnItemEditClickListen() { //所有频道编辑
            @Override
            public void onClick(View view, TagView.EditState state, int position) {
                if (state == TagView.EditState.ADDABLE) { //如果编辑状态为添加
                    //获取常用频道
                    List<ChannelTag> channelTags = SPUtils.getShareValue(Constant.SP_SPACE_NORMAL_CHANNEL, Constant.SP_KEY_NORMAL_CHANNEL, ChannelTag.class);

                    ChannelTag channelTag = channelTagList.get(position); //所有选中的频道

                    if (channelTags == null) { //如果常用频道为null
                        channelTags = new ArrayList<>();
                    } else if (channelTags.contains(channelTag)) { //如果频道已存在常用频道
                        channelTags.remove(channelTag); //删除该频道，后续加到列表后面
                    } else if (channelTags.size() >= 5){ //如果大于等于5个
                        int size = channelTags.size();
                        channelTags.remove(size - 1); //删除最后一个
                    }

                    channelTag.setCommonChannel(true);//设置为常用频道
                    channelTag.setEditEnable(true);
                    channelTags.add(0, channelTag);//加入到常用频道中，加到第一个位置
                    SPUtils.putShareValue(Constant.SP_SPACE_NORMAL_CHANNEL, Constant.SP_KEY_NORMAL_CHANNEL, channelTags);

                    //恢复
                    channelTag.setCommonChannel(false);//设置为所有频道
                    channelTag.setEditState(ChannelTag.COMPLETE_STATE);//设置完成状态
                    allChannelAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void addCommonChannel(ChannelTag channelTag) {
        //获取常用频道
        List<ChannelTag> channelTags = SPUtils.getShareValue(Constant.SP_SPACE_NORMAL_CHANNEL, Constant.SP_KEY_NORMAL_CHANNEL, ChannelTag.class);

        if (channelTags == null) { //如果常用频道为null
            channelTags = new ArrayList<>();
        } else if (channelTags.contains(channelTag)) { //如果频道已存在常用频道
            channelTags.remove(channelTag); //删除该频道，后续加到列表后面
        } else if (channelTags.size() >= 5){ //如果大于等于5个
            int size = channelTags.size();
            channelTags.remove(size - 1); //删除最后一个
        }

        channelTag.setCommonChannel(true);//设置为常用频道
        channelTags.add(0, channelTag);//加入到常用频道中，加到第一个位置
        for(ChannelTag tag: channelTags) {
            tag.setEditEnable(false);
        }
        SPUtils.putShareValue(Constant.SP_SPACE_NORMAL_CHANNEL, Constant.SP_KEY_NORMAL_CHANNEL, channelTags);

        //恢复
        channelTag.setCommonChannel(false);//设置为所有频道
        channelTag.setEditState(ChannelTag.COMPLETE_STATE);//设置完成状态
        allChannelAdapter.notifyDataSetChanged();
    }


    private void showClassifyDetailsFragment(ChannelTag tag) {
        Bundle bundle = new Bundle();
        bundle.putInt(EventConstant.EVENT_PARAMS_SELECT_TAB_ID, R.id.nav_tab_classify);
        bundle.putSerializable(EventConstant.EVENT_PARAMS_FRAGMENT_CLASS, ClassifyDetailsFragment.class);
        Bundle args = new Bundle();
        args.putParcelable(Constant.BUNDLE_KEY_CHANNEL_TAG, tag);
        bundle.putBundle(EventConstant.EVENT_PARAMS_FRAGMENT_ARGS, args);
        EventBusHelper.post(EventBusHelper.newEvent(EventConstant.EVENT_TYPE_LOAD_CHILD_FRAGMENT, bundle));

        updateTitleLayout(tag.getChannelName());
    }

    private void updateTitleLayout(String title) {
        Bundle eventParams = new Bundle();
        eventParams.putString(EventConstant.EVENT_PARAMS_UPDATE_NESTED_TITLE_LAYOUT_WHO, ClassifyDetailsFragment.class.getName());
        eventParams.putString(EventConstant.EVENT_PARAMS_NESTED_TITLE_SHOW_BACK, title);
        EventBusHelper.post(EventBusHelper.newEvent(EventConstant.EVENT_TYPE_UPDATE_TITLE_LAYOUT, eventParams));
    }

    class FilingGestureListener extends GestureDetector.SimpleOnGestureListener {
        private final static int minVelocity = 10;
        private final static int minDistance = 100;
        private AlphaAnimation mHideAnimation = null;
        private AlphaAnimation mShowAnimation = null;
        private View mView;

        public FilingGestureListener(View view) {
            this.mView = view;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.d(TAG, "onFling:" + velocityX + "," + velocityY);
            if (e1.getY() - e2.getY() > minDistance && Math.abs(velocityY) > minVelocity) {
                Log.d(TAG, "fling up");
                setHideAnimation(mView, 5);
            } else if (e2.getY() - e1.getY() > minDistance && Math.abs(velocityY) > minVelocity) {
                Log.d(TAG, "fling down");
                setShowAnimation(mView, 5);
            }

            return false;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        private void setHideAnimation(View view, int duration) {
            if (null == view || duration < 0) {
                return;
            }
            if (null != mHideAnimation) {
                mHideAnimation.cancel();
            }
            mHideAnimation = new AlphaAnimation(1.0f, 0.0f);
            mHideAnimation.setDuration(duration);
            mHideAnimation.setFillAfter(true);
            view.startAnimation(mHideAnimation);
        }

        private void setShowAnimation(View view, int duration) {
            if (null == view || duration < 0) {
                return;
            }
            if (null != mShowAnimation) {
                mShowAnimation.cancel();
            }
            mShowAnimation = new AlphaAnimation(0.0f, 1.0f);
            mShowAnimation.setDuration(duration);
            mShowAnimation.setFillAfter(true);
            view.startAnimation(mShowAnimation);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
