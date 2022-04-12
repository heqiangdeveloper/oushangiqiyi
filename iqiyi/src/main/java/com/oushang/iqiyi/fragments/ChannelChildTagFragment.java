package com.oushang.iqiyi.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.oushang.iqiyi.R;
import com.oushang.iqiyi.base.BaseLazyFragment;
import com.oushang.iqiyi.common.Constant;
import com.oushang.iqiyi.mvp.presenter.ChannelChildTagPresenter;
import com.oushang.iqiyi.mvp.view.IChannelChildTagView;
import com.oushang.iqiyi.ui.GridItemDecoration;
import com.oushang.iqiyi.ui.SimpleFastAdapter;
import com.oushang.lib_base.base.rv.BaseViewHolder;
import com.oushang.lib_service.entries.ChannelTag;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @Author: zeelang
 * @Description: 频道子标签fragment(三级标签)
 * @Time: 2021/7/19 20:26
 * @Since: 1.0
 */
@Deprecated
public class ChannelChildTagFragment extends BaseLazyFragment<ChannelChildTagPresenter> implements IChannelChildTagView {
    private static final String TAG = ChannelChildTagFragment.class.getSimpleName();

    @BindView(R.id.channel_child_third_tag_rv)
    RecyclerView channnelChildTag;

    private List<String> mSelectedChildTag;

    @Override
    protected int setLayout() {
        return R.layout.fragment_channel_childtag;
    }

    @Override
    protected ChannelChildTagPresenter createPresenter() {
        return new ChannelChildTagPresenter();
    }

    public static ChannelChildTagFragment newInstance(Bundle args) {
        ChannelChildTagFragment fragment = new ChannelChildTagFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initView() {
        channnelChildTag.setLayoutManager(new GridLayoutManager(getContext(),5));
        if (channnelChildTag.getItemDecorationCount() == 0) {
            channnelChildTag.addItemDecoration(new GridItemDecoration(15, GridItemDecoration.HORIZONTAL));
            channnelChildTag.addItemDecoration(new GridItemDecoration(15, GridItemDecoration.VERTICAL));
        }
    }

    @Override
    public void lazyInit() {
        Log.d(TAG, "lazyInit");
        mSelectedChildTag = new ArrayList<>();
        Bundle bundle = getArguments();
        if (bundle != null ) {
            ChannelTag tag = bundle.getParcelable(Constant.BUNDLE_KEY_CHANNEL_TAG);
            if (tag != null) {
                Log.d(TAG, "tag:" + tag);
                presenter.loadChannelChildTag(tag.getChildren());
            }
        }
    }

    @Override
    public void onLoadChannelChildTag(List<ChannelTag> tags) {
        Log.d(TAG, "channelTags:" + tags);
        channnelChildTag.setAdapter(new SimpleFastAdapter<ChannelTag>(getContext(), R.layout.item_channel_third_tag, tags){
            @Override
            public void convert(BaseViewHolder holder, ChannelTag data, int position) {
                if (data == null) {
                    return;
                }
                Log.d(TAG, "data:" + data);
                TextView textView = holder.getView(R.id.channel_child_childtag_name);
                textView.setText(data.getName());
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!v.isSelected()) {
                            textView.setTextColor(mContext.getColor(R.color.color_channel_child_tag_selected));
                            v.setSelected(true);
                            if (!mSelectedChildTag.contains(data.getName())) {
                                mSelectedChildTag.add(data.getName());
                            }
                        } else {
                            v.setSelected(false);
                            textView.setTextColor(mContext.getColor(R.color.color_white));
                            mSelectedChildTag.remove(data.getName());
                        }
                    }
                });
//                if (position == 0) {
//                    textView.setSelected(true);
//                    textView.setTextColor(mContext.getColor(R.color.color_channel_child_tag_selected));
//                }
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "selected:" + mSelectedChildTag);
    }
}
