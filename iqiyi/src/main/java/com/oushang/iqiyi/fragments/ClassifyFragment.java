package com.oushang.iqiyi.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.oushang.iqiyi.R;
import com.oushang.iqiyi.adapter.ChannelParentTagAdapter;
import com.oushang.iqiyi.base.BaseLazyFragment;
import com.oushang.iqiyi.common.Constant;
import com.oushang.iqiyi.entries.ChannelParentTag;
import com.oushang.iqiyi.events.EventBusHelper;
import com.oushang.iqiyi.events.EventConstant;
import com.oushang.iqiyi.mvp.presenter.ClassifyPresenter;
import com.oushang.iqiyi.mvp.view.IClassifyView;
import com.oushang.iqiyi.statistics.DataStatistics;
import com.oushang.iqiyi.statistics.StatConstant;
import com.oushang.iqiyi.ui.GridItemDecoration;
import com.oushang.iqiyi.ui.SimpleFastAdapter;
import com.oushang.iqiyi.ui.TagView;
import com.oushang.lib_base.base.rv.BaseViewHolder;
import com.oushang.lib_base.utils.SPUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @Author: zeelang
 * @Description: 分类页面
 * @Time: 2021/12/18 0018  10:31
 * @Since: 1.0
 */
public class ClassifyFragment extends BaseLazyFragment<ClassifyPresenter> implements IClassifyView {
    private static final String TAG = ClassifyFragment.class.getSimpleName();

    @BindView(R.id.channel_edit_btn)
    Button mChanelEditBtn; //频道编辑按钮

    @BindView(R.id.common_channel_rv)
    RecyclerView mCommonChannelRv; //常用频道列表

    @BindView(R.id.common_channel_tag_empty)
    TextView mCommonChannelEmpty; //常用频道列表为空

    @BindView(R.id.all_channel_rv)
    RecyclerView mAllChannelRv; //所有频道列表

    @BindView(R.id.all_channel_layout)
    RelativeLayout mAllChannelLayout; //所有频道布局

    @BindView(R.id.classify2_fragment_root)
    CoordinatorLayout mClassify2FragmentRoot; //根布局

    ChannelParentTagAdapter mCommonChannelAdapter;//常用频道列表的适配器

    ChannelParentTagAdapter mAllChannelAdapter;//所有频道列表的适配器

    private volatile boolean isEditState = false;//是否处理编辑状态

    @Override
    protected int setLayout() {
        return R.layout.fragment_classify2;
    }

    @Override
    protected ClassifyPresenter createPresenter() {
        return new ClassifyPresenter();
    }

    public static ClassifyFragment newInstance() {
        return new ClassifyFragment();
    }

    @Override
    protected void initView() {
        //设置常用频道布局
        mCommonChannelRv.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));
        if (mCommonChannelRv.getItemDecorationCount() == 0) {
            mCommonChannelRv.addItemDecoration(new GridItemDecoration(9, GridItemDecoration.HORIZONTAL));
        }
        //设置所有频道布局
        mAllChannelRv.setLayoutManager(new GridLayoutManager(getContext(), 5));
        if (mAllChannelRv.getItemDecorationCount() == 0) {
            mAllChannelRv.addItemDecoration(new GridItemDecoration(9, GridItemDecoration.HORIZONTAL));
            mAllChannelRv.addItemDecoration(new GridItemDecoration(15, GridItemDecoration.VERTICAL));
        }
    }

    @Override
    public void lazyInit() {
        presenter.loadCommonChannelParentTag(false);
        presenter.loadAllChannelParentTag();
    }

    @OnClick(R.id.channel_edit_btn)
    public void onClick() {
        if (mChanelEditBtn.getText().equals(getString(R.string.edit))) { //编辑
            isEditState = true;
            mChanelEditBtn.setText(R.string.cancel_edit);
            if (mCommonChannelAdapter != null) {
                mCommonChannelAdapter.setEditEnable(true);//常用频道启用编辑
            } else {
                if (mCommonChannelEmpty.getVisibility() == View.VISIBLE) { //隐藏空数据
                    mCommonChannelEmpty.setVisibility(View.GONE);
                }
                if (mCommonChannelRv.getVisibility() == View.GONE) {
                    mCommonChannelRv.setVisibility(View.VISIBLE);
                }
                List<String> channelEmptyList = Arrays.asList("1","2","3","4","5");
                mCommonChannelRv.setAdapter(new SimpleFastAdapter<>(getContext(), R.layout.item_common_channel_empty, channelEmptyList));
            }
            if (mAllChannelAdapter != null) {
                mAllChannelAdapter.setEditEnable(true);//所有频道启用编辑
            }
        } else {
            isEditState = false;
            mChanelEditBtn.setText(R.string.edit); //取消编辑
            DataStatistics.recordUiEvent(StatConstant.EVENT_ID_5926);
            if (mCommonChannelAdapter != null) {
                mCommonChannelAdapter.setEditEnable(false); //常用频道不启用编辑
            } else  {
                if (mCommonChannelRv.getVisibility() == View.VISIBLE) {
                    mCommonChannelRv.setVisibility(View.GONE);
                }
                if (mCommonChannelEmpty.getVisibility() == View.GONE) { //隐藏空数据
                    mCommonChannelEmpty.setVisibility(View.VISIBLE);
                }
                mCommonChannelRv.setAdapter(null);
            }
            if (mAllChannelAdapter != null) {
                mAllChannelAdapter.setEditEnable(false); //所有频道不启用编辑
            }
        }
    }

    /**
     * 常用频道显示为空
     */
    @Override
    public void onLoadEmptyCommonChannelParentTag() {
        Log.d(TAG, "onLoadEmptyCommonChannelParentTag");
        if(isEditState) {
            if (mCommonChannelEmpty.getVisibility() == View.VISIBLE) { //显示空数据
                mCommonChannelEmpty.setVisibility(View.GONE);
            }
            if (mCommonChannelRv.getVisibility() == View.GONE) { //隐藏常用频道列表
                mCommonChannelRv.setVisibility(View.VISIBLE);
            }
            List<String> channelEmptyList = Arrays.asList("1","2","3","4","5");
            mCommonChannelRv.setAdapter(new SimpleFastAdapter(getContext(), R.layout.item_common_channel_empty, channelEmptyList));
        } else {
            if (mCommonChannelEmpty.getVisibility() == View.GONE) { //显示空数据
                mCommonChannelEmpty.setVisibility(View.VISIBLE);
            }
            if (mCommonChannelRv.getVisibility() == View.VISIBLE) { //隐藏常用频道列表
                mCommonChannelRv.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public void onLoadCommonChannelParentTag(List<ChannelParentTag> channelParentTagList) {
        Log.d(TAG, "onLoadCommonChannelParentTag:" + channelParentTagList);
        if (mCommonChannelEmpty.getVisibility() == View.VISIBLE) { //隐藏空数据
            mCommonChannelEmpty.setVisibility(View.GONE);
        }
        if (mCommonChannelRv.getVisibility() == View.GONE) { //显示常用频道列表
            mCommonChannelRv.setVisibility(View.VISIBLE);
        }
        mCommonChannelAdapter = new ChannelParentTagAdapter(getContext(), channelParentTagList);
        if (mCommonChannelRv.getAdapter() != null) {
            mCommonChannelRv.setAdapter(null);
        }
        mCommonChannelRv.setAdapter(mCommonChannelAdapter);

        //点击编辑图标
        mCommonChannelAdapter.setOnItemEditClickListen(new ChannelParentTagAdapter.OnItemEditClickListen() {
            @Override
            public void onClick(TagView view, TagView.EditState state, int position) {
                Log.d(TAG, "commonChannel state:" + state + ",position:" + position);
                if (state == TagView.EditState.DELETABLE) { //点击删除
                    if (mAllChannelAdapter != null) {
                        List<ChannelParentTag> datas = mCommonChannelAdapter.getDatas();
                        mCommonChannelAdapter.removeTag(datas.get(position), true); //设置为删除状态
                        removeToSpFile(datas.get(position)); //sp磁盘文件中删除
                        ChannelParentTag allChannelParentTag = (ChannelParentTag) datas.get(position).clone(); //复制为所有频道标签
                        allChannelParentTag.setCommonChannel(false); //不设置为常用频道标签
                        mAllChannelAdapter.updateState(allChannelParentTag, ChannelParentTag.ADD_STATE); //更新所有频道状态为可添加状态
                        Map<String, String> statValue = new HashMap<>();
                        statValue.put("type", "删除");
                        statValue.put("name", datas.get(position).getChannelTag().getName());
                        DataStatistics.recordUiEvent(StatConstant.EVENT_ID_5925, statValue);//埋点数据
                    }
                }
            }
        });

        //点击频道标签
        mCommonChannelAdapter.setOnItemClickListen(new ChannelParentTagAdapter.OnItemClickListen() {
            @Override
            public void onClick(View view, int position) {
                List<ChannelParentTag> datas = mCommonChannelAdapter.getDatas();
                ChannelParentTag mCurrentTag = datas.get(position);
                Map<String,String> statsValue = new HashMap<>();
                statsValue.put("type", "常用频道");
                statsValue.put("name", mCurrentTag.getChannelTag().getName());
                DataStatistics.recordUiEvent(StatConstant.EVENT_ID_5927, statsValue);//埋点数据
                showClassifyDetailsFragment(mCurrentTag); //跳转到频道详情页面
            }
        });

        //常用频道数据监听
        mCommonChannelAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                Log.d(TAG, "onChanged");
                if (mCommonChannelAdapter != null) {
                    if (mCommonChannelAdapter.getDatas() == null || mCommonChannelAdapter.getDatas().isEmpty()) { //如果数据删除完
                        onLoadEmptyCommonChannelParentTag(); //加载空数据
                    }
                }
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) { //插入数据
                super.onItemRangeInserted(positionStart, itemCount);
                if (mCommonChannelEmpty.getVisibility() == View.VISIBLE) { //隐藏空数据
                    mCommonChannelEmpty.setVisibility(View.GONE);
                }
                if (mCommonChannelRv.getVisibility() == View.GONE) { //显示常用频道列表
                    mCommonChannelRv.setVisibility(View.VISIBLE);
                }
            }
        });


    }

    @Override
    public void onLoadAllChannelParentTag(List<ChannelParentTag> channelParentTagList) {
        Log.d(TAG, "onLoadAllChannelParentTag:" + channelParentTagList);
        mAllChannelAdapter = new ChannelParentTagAdapter(getContext(), channelParentTagList);
        mAllChannelRv.setAdapter(mAllChannelAdapter);

        if (mCommonChannelAdapter != null) {
            mAllChannelAdapter.updateState(mCommonChannelAdapter.getDatas(), ChannelParentTag.COMPLETE_STATE); //更新所有频道状态，如果已添加到常用频道，则置为已完成状态
        }

        //编辑
        mAllChannelAdapter.setOnItemEditClickListen(new ChannelParentTagAdapter.OnItemEditClickListen() {
            @Override
            public void onClick(TagView view, TagView.EditState state, int position) {
                Log.d(TAG, "allChannel state:" + state + ",position:" + position);
                if (state == TagView.EditState.ADDABLE) {
                    ChannelParentTag commonChannelParentTag = (ChannelParentTag) channelParentTagList.get(position).clone(); //复制为一个常用频道
                    addToSpFile(commonChannelParentTag);//添加到sp磁盘文件中
                    mAllChannelAdapter.updateState(channelParentTagList.get(position), ChannelParentTag.COMPLETE_STATE);//已添加到常用频道的频道状态更新为已完成状态
                    Map<String, String> statValue = new HashMap<>();
                    statValue.put("type", "添加");
                    statValue.put("name", channelParentTagList.get(position).getChannelTag().getName());
                    DataStatistics.recordUiEvent(StatConstant.EVENT_ID_5925, statValue);//埋点数据
                    if (mCommonChannelAdapter != null) {
                        commonChannelParentTag.setEditEnable(true);//设置已编辑状态
                        commonChannelParentTag.setEditState(ChannelParentTag.DELETE_STATE);//设置常用频道为可删除状态
                        List<ChannelParentTag> outChannelParentTags = mCommonChannelAdapter.addTag(commonChannelParentTag);
                        mAllChannelAdapter.updateState(outChannelParentTags, ChannelParentTag.ADD_STATE);//添加到常用频道，并把添加挤出的频道置为可添加状态
                        if(mCommonChannelAdapter.getItemCount() > 0) { //如果常用添加了数据，之前是删除完了
                            if(mCommonChannelEmpty.getVisibility() == View.VISIBLE) { //如果显示空数据提示
                                mCommonChannelEmpty.setVisibility(View.GONE); //隐藏空数据提示
                            }
                            if (!(mCommonChannelRv.getAdapter() instanceof ChannelParentTagAdapter)) { //如果当前adapter不是ChannelParentTagAdapter
                                mCommonChannelRv.setAdapter(mCommonChannelAdapter);//设置ChannelParentTagAdapter
                            }
                            if(mCommonChannelRv.getVisibility() == View.GONE) {
                                mCommonChannelRv.setVisibility(View.VISIBLE);//显示列表数据
                            }
                        }

                    } else {
                        Log.d(TAG, "loadCommonChannelParentTag");
                        presenter.loadCommonChannelParentTag(true); //如果常用频道是空的，则重新从sp磁盘文件中加载常用频道
                    }
                }
            }
        });

        //点击
        mAllChannelAdapter.setOnItemClickListen(new ChannelParentTagAdapter.OnItemClickListen() {
            @Override
            public void onClick(View view, int position) {
                ChannelParentTag allChannelParentTag = channelParentTagList.get(position);
                Map<String, String> statValue = new HashMap<>();
                statValue.put("type", "所有频道");
                statValue.put("name", allChannelParentTag.getChannelTag().getName());
                DataStatistics.recordUiEvent(StatConstant.EVENT_ID_5927, statValue); //埋点数据
                ChannelParentTag commonChannelParentTag = (ChannelParentTag) allChannelParentTag.clone();//复制为一个常用频道
                addToSpFile(commonChannelParentTag); //添加到sp磁盘文件中
                if (mCommonChannelAdapter == null || mCommonChannelAdapter.getItemCount() == 0) {
                    presenter.loadCommonChannelParentTag(isEditState); //如果常用频道是空的，则重新从sp磁盘文件中加载常用频道
                }
                if (mCommonChannelAdapter != null) {
                    commonChannelParentTag.setEditEnable(isEditState);//设置编辑状态
                    commonChannelParentTag.setEditState(isEditState?ChannelParentTag.DELETE_STATE:ChannelParentTag.NONE_STATE);
                    mAllChannelAdapter.updateState(mCommonChannelAdapter.addTag(commonChannelParentTag), ChannelParentTag.ADD_STATE);//添加到常用频道，并把添加挤出的频道置为可添加状态
                }
                mAllChannelAdapter.updateState(channelParentTagList.get(position), ChannelParentTag.COMPLETE_STATE);//已添加到常用频道的频道状态更新为已完成状态
                showClassifyDetailsFragment(channelParentTagList.get(position)); //跳转到频道详情页面
            }
        });
    }

    private void removeToSpFile(ChannelParentTag channelParentTag) {
        Log.d(TAG, "removeToSpFile");
        List<ChannelParentTag> channelParentTags = SPUtils.getShareValue(Constant.SP_SPACE_COMMON_CHANNEL_PARENT_TAG, Constant.SP_KEY_COMMON_CHANNEL_PARENT_TAG, ChannelParentTag.class);
        if (!channelParentTags.isEmpty()) {
            channelParentTags.remove(channelParentTag);
        }
        SPUtils.putShareValue(Constant.SP_SPACE_COMMON_CHANNEL_PARENT_TAG, Constant.SP_KEY_COMMON_CHANNEL_PARENT_TAG, channelParentTags);
    }

    private void addToSpFile(ChannelParentTag channelParentTag) {
        Log.d(TAG, "addToSpFile");
        List<ChannelParentTag> channelParentTags = SPUtils.getShareValue(Constant.SP_SPACE_COMMON_CHANNEL_PARENT_TAG, Constant.SP_KEY_COMMON_CHANNEL_PARENT_TAG, ChannelParentTag.class);
        if (channelParentTags.removeIf(next -> next.equals(channelParentTag))) {//如果频道已存在常用频道,删除该频道，后续加到列表后面
            if (mCommonChannelAdapter != null) {
                mCommonChannelAdapter.removeTag(channelParentTag, false);
            }
        }

        if (channelParentTags.size() >= 5) { //如果大于等于5个
            int size = channelParentTags.size();
            channelParentTags.remove(size - 1); //删除最后一个
        }
        channelParentTag.setCommonChannel(true);//设置为常用频道
        channelParentTags.add(0, channelParentTag);//加入到常用频道中，加到第一个位置

        for(ChannelParentTag tag: channelParentTags) {
            tag.setEditEnable(false); //默认不启用编辑
        }
        SPUtils.putShareValue(Constant.SP_SPACE_COMMON_CHANNEL_PARENT_TAG, Constant.SP_KEY_COMMON_CHANNEL_PARENT_TAG, channelParentTags);
    }

    @Override
    public void onNetWorkChanged(boolean isConnected) {
        super.onNetWorkChanged(isConnected);
        if(isConnected) { //网络已连接
            if(mAllChannelAdapter == null) { //没有数据
                presenter.loadAllChannelParentTag(); //再次请求
            }
        }
    }

    private void showClassifyDetailsFragment(ChannelParentTag tag) {
        Bundle bundle = new Bundle();
        bundle.putInt(EventConstant.EVENT_PARAMS_SELECT_TAB_ID, R.id.nav_tab_classify);
        bundle.putSerializable(EventConstant.EVENT_PARAMS_FRAGMENT_CLASS, ClassifyDetailsFragment.class);
        Bundle args = new Bundle();
        args.putParcelable(Constant.BUNDLE_KEY_CHANNEL_PARENT_TAG, tag);
        bundle.putBundle(EventConstant.EVENT_PARAMS_FRAGMENT_ARGS, args);
        EventBusHelper.post(EventBusHelper.newEvent(EventConstant.EVENT_TYPE_LOAD_CHILD_FRAGMENT, bundle));

        updateTitleLayout(tag.getChannelTag().getName());
    }

    private void updateTitleLayout(String title) {
        Bundle eventParams = new Bundle();
        eventParams.putString(EventConstant.EVENT_PARAMS_UPDATE_NESTED_TITLE_LAYOUT_WHO, ClassifyDetailsFragment.class.getName());
        eventParams.putString(EventConstant.EVENT_PARAMS_NESTED_TITLE_SHOW_BACK, title);
        EventBusHelper.post(EventBusHelper.newEvent(EventConstant.EVENT_TYPE_UPDATE_TITLE_LAYOUT, eventParams));
    }
}
