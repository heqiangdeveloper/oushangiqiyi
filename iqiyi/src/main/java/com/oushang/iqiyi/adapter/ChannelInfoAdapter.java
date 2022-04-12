package com.oushang.iqiyi.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.callback.NavCallback;
import com.alibaba.android.arouter.facade.callback.NavigationCallback;
import com.alibaba.android.arouter.launcher.ARouter;
import com.jakewharton.rxbinding2.view.RxView;
import com.oushang.iqiyi.R;
import com.oushang.iqiyi.common.Constant;
import com.oushang.iqiyi.statistics.DataStatistics;
import com.oushang.iqiyi.statistics.StatConstant;
import com.oushang.iqiyi.ui.OnPreventRepeatedClickListener;
import com.oushang.iqiyi.utils.AppUtils;
import com.oushang.lib_base.base.rv.BaseAdapter;
import com.oushang.lib_base.base.rv.BaseViewHolder;
import com.oushang.lib_base.image.GlideUtils;
import com.oushang.lib_service.entries.VideoInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * @Author: zeelang
 * @Description: 频道详情适配器
 * @Time: 2021/10/18 0018  14:26
 * @Since: 1.0
 */
public class ChannelInfoAdapter extends BaseAdapter<VideoInfo, BaseViewHolder> {
    private static final String TAG = ChannelInfoAdapter.class.getSimpleName();

    public ChannelInfoAdapter(Context context, List<VideoInfo> videoInfoList) {
        super(context, videoInfoList);
    }

    @Override
    public int setItemViewLayout() {
        return R.layout.item_classify_details_albumpic;
    }

    @Override
    public void onBindData(@NonNull BaseViewHolder holder, VideoInfo data, int position) {
        TextView view = holder.getView(R.id.albumpic_tv);
        ImageView albumPic = holder.getView(R.id.albumpic_iv);
        ImageView remark = holder.getView(R.id.albumpic_remark);
        String name = data.getShortName();
        if (name == null || name.isEmpty()) {
            name = data.getName();
        }
        view.setText(name);
        String picUrl = data.getAlbumPic();
        if (picUrl != null && !picUrl.isEmpty()) {
            String newUrl = AppUtils.appendImageUrl(picUrl, "_260_360");
            GlideUtils.loadImageWithRoundedCorners(mContext, Uri.parse(newUrl), albumPic, R.drawable.album_pic_place_holder, R.drawable.album_pic_place_holder, 8);
        }

        holder.setIsRecyclable(false);

        //是否独播
        boolean isExclusive = data.isExclusive();
        //是否是vip
        boolean isVip = data.isVip();

        int publishyear = 0;
        String publishTime = data.getPublishTime();
        if (publishTime != null && !publishTime.equals("0") && !publishTime.isEmpty() && publishTime.length() >= 4) {
            publishyear = Integer.parseInt(publishTime.trim().substring(0, 4));
        }

        final int year = publishyear;

        if (isExclusive) {
            GlideUtils.loadRes(mContext, R.drawable.hot_search_album_exclusive, remark);
        }
        if (isVip) {
            GlideUtils.loadRes(mContext, R.drawable.hot_search_album_vip, remark);
        }

        Disposable disposable = RxView.clicks(holder.getItemView())
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(o -> {

                    Map<String,String> statValue = new HashMap<>();
                    statValue.put("item", data.getName());
                    statValue.put("plate", data.getChnName());
                    DataStatistics.recordUiEvent(StatConstant.EVENT_ID_5929, statValue);//埋点数据

                    ARouter.getInstance().build(Constant.PATH_ACTIVITY_PLAYER)
                            .withLong(Constant.PLAY_VIDEO_ID, data.getQipuId())
                            .withLong(Constant.PLAY_ALBUM_ID, data.getAlbumId())
                            .withInt(Constant.PLAY_PUBLISH_YEAR, year)
                            .navigation();
                });


//        holder.getItemView().setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG, "click:");
//                ARouter.getInstance().build(Constant.PATH_ACTIVITY_PLAYER)
//                        .withLong(Constant.PLAY_VIDEO_ID, data.getQipuId())
//                        .withLong(Constant.PLAY_ALBUM_ID, data.getAlbumId())
//                        .withInt(Constant.PLAY_PUBLISH_YEAR, year)
//                        .navigation();
//            }
//        });

//        holder.getItemView().setOnClickListener(new OnPreventRepeatedClickListener() {
//            @Override
//            public void onNoRepeatedClick(View view) {
//                Log.d(TAG, "click:");
//                ARouter.getInstance().build(Constant.PATH_ACTIVITY_PLAYER)
//                        .withLong(Constant.PLAY_VIDEO_ID, data.getQipuId())
//                        .withLong(Constant.PLAY_ALBUM_ID, data.getAlbumId())
//                        .withInt(Constant.PLAY_PUBLISH_YEAR, year)
//                        .navigation(mContext, new NavigationCallback() {
//                            @Override
//                            public void onFound(Postcard postcard) {
//                                Log.d(TAG, "onFound");
//                            }
//
//                            @Override
//                            public void onLost(Postcard postcard) {
//                                Log.d(TAG, "onLost");
//                            }
//
//                            @Override
//                            public void onArrival(Postcard postcard) {
//                                Log.d(TAG, "onArrival");
//                            }
//
//                            @Override
//                            public void onInterrupt(Postcard postcard) {
//                                Log.d(TAG, "onInterrupt");
//                            }
//                        });
//            }
//        });


    }

    public void addData(List<VideoInfo> videoInfoList) {
        if (videoInfoList != null && !videoInfoList.isEmpty()) {
            int lastPos = mDatas.size();
            mDatas.addAll(videoInfoList);
            notifyItemRangeInserted(lastPos, videoInfoList.size());
        }
    }

    public void updateData(List<VideoInfo> videoInfoList) {
        mDatas = videoInfoList;
        notifyDataSetChanged();
    }
}
