package com.oushang.iqiyi.adapter;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.oushang.iqiyi.R;
import com.oushang.iqiyi.common.Constant;
import com.oushang.iqiyi.events.EventBusHelper;
import com.oushang.iqiyi.events.EventConstant;
import com.oushang.iqiyi.fragments.PlayVideoInfoEpisodeDetailsFragment;
import com.oushang.iqiyi.fragments.fragmentUtils.FragmentHelper;
import com.oushang.iqiyi.utils.AppUtils;
import com.oushang.iqiyi.utils.SteeringWheelControl;
import com.oushang.iqiyi.utils.ThemeManager;
import com.oushang.lib_base.base.rv.BaseAdapter;
import com.oushang.lib_base.base.rv.BaseViewHolder;
import com.oushang.lib_base.image.GlideUtils;
import com.oushang.lib_service.entries.CastInfo;
import com.oushang.lib_service.entries.VideoInfo;

import java.util.Collections;
import java.util.List;

/**
 * @Author: zeelang
 * @Description: 更多选集适配器(时间型）
 * @Time: 2021/8/27 15:44
 * @Since: 1.0
 */
public class MoreSelectionsTimeAdapter extends BaseAdapter<VideoInfo, BaseViewHolder> {
    private static final String TAG = MoreSelectionsTimeAdapter.class.getSimpleName();

    private static int mCurrentSort = Constant.SORT_ORDER; //当前排序, 默认顺序排序, 这里设为static，避免重置
    private OnSortChangedListener mOnSortChangedListener; //排序监听
    private ThemeManager.ThemeMode mThemeMode = ThemeManager.sThemeMode;//黑白模式切换

    public MoreSelectionsTimeAdapter(Context context, List<VideoInfo> datas) {
        super(context, datas);
    }

    public MoreSelectionsTimeAdapter(Context context, List<VideoInfo> datas, OnSortChangedListener sortChangedListener) {
        super(context, datas);
        this.mOnSortChangedListener = sortChangedListener;
    }

    @Override
    public int setItemViewLayout() {
        return R.layout.item_video_info_more_time_selections;
    }

    @Override
    public void onBindData(@NonNull BaseViewHolder holder, VideoInfo data, int position) {
        if (data == null) {
            return;
        }

        //Album图片
        ImageView albumPic = holder.getView(R.id.video_info_selection_pic);
        //发布时间
        TextView albumPublishTime = holder.getView(R.id.video_info_publish_time);
        //剧集名称
        TextView albumName = holder.getView(R.id.video_info_selection_name);
        //剧集数
        TextView albumSeries = holder.getView(R.id.video_info_selection_series);
        //剧集详情
        TextView albumInfo = holder.getView(R.id.video_info_selection_details);

        String picurl = data.getAlbumPic();
        if (picurl != null && !picurl.isEmpty()) {
            String newUrl = AppUtils.appendImageUrl(picurl, "_180_120");
            GlideUtils.loadImageWithRoundedCorners(mContext, Uri.parse(newUrl), albumPic, 10);
        }

        String time = data.getInitIssueTime();
        String[] split = time.split(" ");
        //剧集发布时间
        albumPublishTime.setText(split.length>0 ? split[0]:time);
        //剧集名称
        albumName.setText(data.getName());
        //当前集数
        albumSeries.setText(data.getShortName());

        String desc = data.getDesc();
        CastInfo castInfo = data.getCast();
        mThemeMode = ThemeManager.getThemeMode();
        if(mThemeMode == ThemeManager.ThemeMode.NIGHT) {
            albumName.setTextColor(mContext.getColor(R.color.color_skin_synopsis_text));
            albumSeries.setTextColor(mContext.getColor(R.color.color_skin_synopsis_text));
        } else {
            albumName.setTextColor(mContext.getColor(R.color.color_skin_synopsis_text_notnight));
            albumSeries.setTextColor(mContext.getColor(R.color.color_skin_synopsis_text_notnight));
        }

        //剧集详情
        albumInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle eventParams = new Bundle();
                eventParams.putString(EventConstant.EVENT_PARAMS_VIDEO_INFO_DESC, desc);
                eventParams.putParcelable(EventConstant.EVENT_PARAMS_VIDEO_INFO_CAST_INFO, castInfo);
                EventBusHelper.post(EventBusHelper.newEvent(EventConstant.EVENT_TYPE_EPISODE_DETAIL, eventParams));
            }
        });
        albumPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle eventParams = new Bundle();
                eventParams.putParcelable(EventConstant.EVENT_PARAMS_PLAY_SELECT_SELECTION, data);
                EventBusHelper.post(EventBusHelper.newEvent(EventConstant.EVENT_TYPE_PLAY_SELECT_SELECTION, eventParams));
            }
        });
    }

    public void reverse(int sort) {
        mCurrentSort = sort;
        Collections.reverse(mDatas);
        notifyDataSetChanged();
    }

    public int getCurrentSort() {
        return mCurrentSort;
    }

    public void changeSort(int sort) {
        if(mCurrentSort != sort && mOnSortChangedListener != null) {
            mCurrentSort = sort;
            mOnSortChangedListener.onChanged(mCurrentSort);
        }
    }

    public interface OnSortChangedListener {
        void onChanged(int sort);
    }

    public void updateSkin(ThemeManager.ThemeMode themeMode) {
        this.mThemeMode = themeMode;
        notifyDataSetChanged();
    }

}
