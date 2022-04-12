package com.oushang.iqiyi.adapter;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.alibaba.android.arouter.launcher.ARouter;
import com.oushang.iqiyi.R;
import com.oushang.iqiyi.common.Constant;
import com.oushang.iqiyi.events.EventBusHelper;
import com.oushang.iqiyi.events.EventConstant;
import com.oushang.iqiyi.statistics.DataStatistics;
import com.oushang.iqiyi.statistics.StatConstant;
import com.oushang.iqiyi.utils.AppUtils;
import com.oushang.lib_base.base.rv.BaseAdapter;
import com.oushang.lib_base.base.rv.BaseViewHolder;
import com.oushang.lib_base.image.GlideUtils;
import com.oushang.lib_base.utils.StringUtils;
import com.oushang.lib_service.entries.FavoriteRecord;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @Author: zeelang
 * @Description: 收藏记录适配器
 * @Time: 2021/10/29 0029  14:36
 * @Since: 1.0
 */
public class MyFavoriteAdapter extends BaseAdapter<FavoriteRecord, BaseViewHolder> {
    private static final String TAG = MyFavoriteAdapter.class.getSimpleName();
    private boolean isEditEnable = false;
    private boolean isAllSelected = false;

    public MyFavoriteAdapter(Context context, List<FavoriteRecord> datas) {
        super(context, datas);
    }

    @Override
    public int setItemViewLayout() {
        return R.layout.item_my_record;
    }

    @Override
    public void onBindData(@NonNull BaseViewHolder holder, FavoriteRecord data, int position) {
        if (data == null) {
            Log.e(TAG, "data is null!");
            return;
        }
        ImageView albumPic = holder.getView(R.id.my_record_albumpic); //封面图
        TextView albumName = holder.getView(R.id.my_record_albumName); //专辑/节目名称
        TextView playTime = holder.getView(R.id.my_record_play_time); //播放时长
        TextView playRemark = holder.getView(R.id.my_record_play_remark); //播放时长提示
        ImageView favoriteRecordEdit = holder.getView(R.id.my_history_edit_state); //勾选图标

        playTime.setVisibility(View.GONE);

        String albumPicUrl = data.getAlbumPic(); //获取封面地址
        String name = data.getAlbumName(); //获取视频名称
        String playtime = data.getPlaytime(); //获取播放时间
        long len = data.getLen(); //获取视频时长

        long qipuId = data.getQipuId();
        long albumId = data.getAlbumId();

        albumName.setText(name);
        long l_playTime = 0;
        if (playtime != null && !playtime.isEmpty() && StringUtils.isInteger(playtime)) {
            l_playTime = Long.parseLong(playtime);
        }
        long videoPlayTime = l_playTime;
        playTime.setText(AppUtils.parseMills(videoPlayTime));

        if (videoPlayTime < 60) {
            playRemark.setText("观看不足1分钟");
        } else if (videoPlayTime == len) {
            playRemark.setText("已看完");
        } else {
            long remainTime = Math.abs(len - videoPlayTime);
            Log.d(TAG, "remainTime:" + remainTime);
            String remain = "已观看" + AppUtils.parseSecond(videoPlayTime);
            playRemark.setText(remain);
        }

        if (albumPicUrl != null && !albumPicUrl.isEmpty()) {
            String newAlbumPicUrl = AppUtils.appendImageUrl(albumPicUrl, "_260_360");
            GlideUtils.loadImageWithRoundedCorners(mContext, Uri.parse(newAlbumPicUrl), albumPic, R.drawable.album_pic_place_holder, R.drawable.album_pic_place_holder, 8);
        }

        albumPic.setOnClickListener(new View.OnClickListener() { //点击封面图跳转到播放页
            @Override
            public void onClick(View v) {
                if (isEditEnable) {
                    favoriteRecordEdit.setSelected(!favoriteRecordEdit.isSelected());
                    data.setSelected(!data.isSelected());
                    statisticData(!data.isSelected(), data.getName());
                    return;
                }

                Map<String, String> statsValue = new HashMap<>();
                statsValue.put("item", name);
                statsValue.put("plate", data.getChnName());
                DataStatistics.recordUiEvent(StatConstant.EVENT_ID_5941, statsValue);//埋点数据

                ARouter.getInstance().build(Constant.PATH_ACTIVITY_PLAYER)
                        .withLong(Constant.PLAY_VIDEO_ID, qipuId)
                        .withLong(Constant.PLAY_POSITION, videoPlayTime)
                        .withLong(Constant.PLAY_ALBUM_ID, albumId)
                        .navigation();
            }
        });

        favoriteRecordEdit.setOnClickListener(new View.OnClickListener() { //点击勾选
            @Override
            public void onClick(View v) {
                if (v.isSelected()) { //已勾选
                    v.setSelected(false);//取消勾选
                    data.setSelected(false); //设置未选中
                    statisticData(false, data.getName());
                } else {
                    v.setSelected(true); //勾选
                    data.setSelected(true); //设置选中
                    statisticData(true, data.getName());
                }
            }
        });

        if (isEditEnable) { //启用编辑
            favoriteRecordEdit.setVisibility(View.VISIBLE); //显示勾选
            if (isAllSelected) { //全选
                if (!favoriteRecordEdit.isSelected()) {
                    favoriteRecordEdit.setSelected(true);
                    data.setSelected(true); //设置选中
                }
            } else { //不是全选
                favoriteRecordEdit.setSelected(data.isSelected());
            }

        } else {
            favoriteRecordEdit.setVisibility(View.GONE); //取消编辑，隐藏勾选
            if (favoriteRecordEdit.isSelected()) { //取消选中
                favoriteRecordEdit.setSelected(false);
                data.setSelected(false); //设置未选中
            }
        }

    }

    private void statisticData(boolean isSelected, String videoName) {
        Map<String, String> statsValue = new HashMap<>();
        statsValue.put("mode", isSelected?"选中":"取消选中");
        statsValue.put("name", videoName);
        DataStatistics.recordUiEvent(StatConstant.EVENT_ID_5942, statsValue);
    }

    /**
     * 设置是否编辑
     *
     * @param editEnable 是否编辑
     */
    public void setEditEnable(boolean editEnable) {
        this.isEditEnable = editEnable;
        notifyItemRangeChanged(0, mDatas != null ? mDatas.size() : 0);
    }

    /**
     * 是否已启动编辑
     *
     * @return true 是，false 否
     */
    public boolean isEditEnable() {
        return this.isEditEnable;
    }

    /**
     * 设置是否全选
     *
     * @param isAllSelected 是否全选
     */
    public void setAllSelect(boolean isAllSelected) {
        this.isAllSelected = isAllSelected;
        notifyItemRangeChanged(0, mDatas != null ? mDatas.size() : 0);
    }

    /**
     * 是否全选
     *
     * @return true 是， false 否
     */
    public boolean isAllSelect() {
        return isAllSelected;
    }

    /**
     * 获取选中的记录
     *
     * @return 选中的记录
     */
    public List<FavoriteRecord> getSelectedRecord() {
        List<FavoriteRecord> favoriteRecords = null;
        if (mDatas != null && !mDatas.isEmpty()) {
            favoriteRecords = mDatas.stream().filter(FavoriteRecord::isSelected).collect(Collectors.toList());
        }
        return favoriteRecords;
    }

    public void setSelectedRecord(List<FavoriteRecord> favoriteRecords, boolean isSelected) {
        if (favoriteRecords != null && !favoriteRecords.isEmpty()) {
            if (mDatas != null && !mDatas.isEmpty()) {
                favoriteRecords.forEach(favoriteRecord -> {
                    mDatas.forEach(favoriteRecord1 -> {
                        if (favoriteRecord1.equals(favoriteRecord)) {
                            favoriteRecord1.setSelected(isSelected);
                        }
                    });
                });
                notifyDataSetChanged();
            }
        }
    }

    /**
     * 删除列表收藏记录
     *
     * @param deleteRecords 列表收藏记录
     */
    public void deleteRecordList(List<FavoriteRecord> deleteRecords) {
        if (deleteRecords != null && !deleteRecords.isEmpty()) {
            deleteRecords.forEach(this::deleteRecord);
        }
    }

    /**
     * 删除所有记录
     */
    public void deleteAllRecord() {
        if (mDatas != null && !mDatas.isEmpty()) {
            mDatas.clear();
            notifyDataSetChanged();
        }
    }

    /**
     * 删除单个收藏记录
     *
     * @param record 收藏记录
     */
    public void deleteRecord(FavoriteRecord record) {
        Log.d(TAG, "deleteRecord");
        int index = mDatas.indexOf(record);
        if (mDatas.remove(record)) {
            notifyItemRemoved(index);
        }
    }

    /**
     * 删除已选中的收藏记录
     */
    public void deleteSelectedRecord() {
        deleteRecordList(getSelectedRecord());
    }

    public boolean isEmpty() {
        return mDatas == null || mDatas.isEmpty();
    }
}
