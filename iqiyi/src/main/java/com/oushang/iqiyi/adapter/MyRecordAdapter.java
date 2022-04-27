package com.oushang.iqiyi.adapter;

import android.content.Context;
import android.net.Uri;
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
import com.oushang.lib_service.entries.HistoryRecord;
import com.oushang.lib_service.entries.VideoInfo;
import com.oushang.lib_service.entries.VideoInfoRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @Author: zeelang
 * @Description: 历史/收藏记录适配器
 * @Time: 2021/8/5 10:56
 * @Since: 1.0
 */
public class MyRecordAdapter extends BaseAdapter<HistoryRecord, BaseViewHolder> {
    private static final String TAG = MyRecordAdapter.class.getSimpleName();

    private boolean isEditChange = false;

    private volatile boolean isAllSelected = false;

    private HashSet<Integer> mSelectPosition = new HashSet<>();

    public MyRecordAdapter(Context context, List<HistoryRecord> datas) {
        super(context, datas);
    }

    @Override
    public int setItemViewLayout() {
        return R.layout.item_my_record;
    }

    @Override
    public void onBindData(@NonNull BaseViewHolder holder, HistoryRecord data, int position) {
        if (data == null) {
            Log.e(TAG, "data is null");
            return;
        }
        ImageView albumPic = holder.getView(R.id.my_record_albumpic); //封面图
        TextView albumName = holder.getView(R.id.my_record_albumName); //专辑/节目名称
        TextView playTime = holder.getView(R.id.my_record_play_time); //播放时长
        TextView playRemark = holder.getView(R.id.my_record_play_remark); //播放时长提示
        ImageView historyRecordEdit = holder.getView(R.id.my_history_edit_state);

        long videoPlayTime = data.getVideoPlayTime();//获取已播放时长
        String tvId = data.getTvId(); //获取qipuId
        String alId = data.getID(); //获取albumId
        long len = data.getVideoDuration(); //获取视频时长
        String name = data.getVideoName(); //获取视频名称
        String albumPicUrl = data.getVideoImageUrl(); //获取视频封面图片地址

        long l_tvId = 0;
        long l_alId = 0;
        if (tvId != null && !tvId.isEmpty()) {
            l_tvId = Long.parseLong(tvId);
        }
        if (alId != null && !alId.isEmpty()) {
            l_alId = Long.parseLong(alId);
        }

        if(videoPlayTime == 0) { //播放完毕
            playRemark.setText("已看完");
        } else if (videoPlayTime == 1) { //试看完毕（试看时间6分钟）
            playRemark.setText("已试看完");
            videoPlayTime = 0;
        } else if (videoPlayTime < 60) { //观看时间小于1分钟
            playRemark.setText("观看不足1分钟");
        } else {
            long remainTime = len - videoPlayTime;
            String remain = "剩余" + AppUtils.parseSecond(remainTime);
            playRemark.setText(remain);
        }
        albumName.setText(name);
        playTime.setText(AppUtils.parseSecond(videoPlayTime));

        long qipuId = l_tvId;
        long albumId = l_alId;

        if (albumPicUrl != null && !albumPicUrl.isEmpty()) {
            String newAlbumPicUrl = AppUtils.appendImageUrl(albumPicUrl, "_260_360");
            GlideUtils.loadImageWithRoundedCorners(mContext, Uri.parse(newAlbumPicUrl), albumPic, R.drawable.album_pic_place_holder, R.drawable.album_pic_place_holder, 10);
        }

        albumPic.setOnClickListener(new View.OnClickListener() { //点击
            @Override
            public void onClick(View v) {
                if (isEditChange) {
                    historyRecordEdit.setSelected(!historyRecordEdit.isSelected());
                    data.setSelected(!data.isSelected());
                    isAllSelected = false;
                    statisticData(!data.isSelected(), data.videoName);
                    return;
                }
                Map<String,String> statsValue = new HashMap<>();
                statsValue.put("item", name);
                statsValue.put("result", playRemark.getText().toString());
                DataStatistics.recordUiEvent(StatConstant.EVENT_ID_5936, statsValue);//埋点数据

                ARouter.getInstance().build(Constant.PATH_ACTIVITY_PLAYER)
                        .withLong(Constant.PLAY_VIDEO_ID, qipuId)
                        .withLong(Constant.PLAY_ALBUM_ID, albumId)
                        .navigation();
//                EventBusHelper.postStick(EventBusHelper.newEvent(EventConstant.EVENT_TYPE_UPDATE_PLAY_RECORD)); //通知更新播放记录
            }
        });


        historyRecordEdit.setOnClickListener(new View.OnClickListener() { //勾选
            @Override
            public void onClick(View v) {
                if (!v.isSelected()) {
                    historyRecordEdit.setSelected(true);
                    data.setSelected(true);
                    statisticData(true, data.videoName);
                } else {
                    historyRecordEdit.setSelected(false);
                    data.setSelected(false);
                    isAllSelected = false;
                    statisticData(false, data.videoName);
                }
            }
        });

        if (isEditChange) { //启用编辑
            historyRecordEdit.setVisibility(View.VISIBLE); //显示勾选

            if (isAllSelected) { //全选
                if (!historyRecordEdit.isSelected()) {
                    historyRecordEdit.setSelected(true);
                    data.setSelected(true);
                }
            } else { //取消全选
                if (historyRecordEdit.isSelected()) {
                    historyRecordEdit.setSelected(false);
                    data.setSelected(false);
                }
            }

        } else { //取消编辑
            historyRecordEdit.setVisibility(View.GONE); //隐藏勾选
            historyRecordEdit.setSelected(false); //取消选中
            data.setSelected(false); //取消选中
        }
    }

    private void statisticData(boolean isSelected, String videoName) {
        Map<String, String> statsValue = new HashMap<>();
        statsValue.put("mode", isSelected?"选中":"取消选中");
        statsValue.put("name", videoName);
        DataStatistics.recordUiEvent(StatConstant.EVENT_ID_5937, statsValue);
    }

    public void setEditChange(boolean editChange) { //是否启用编辑
        this.isEditChange = editChange;
        notifyItemRangeChanged(0, mDatas != null ? mDatas.size() : 0);
    }

    /**
     * 设置全选
     * @param select 是否全选
     */
    public void setAllSelect(boolean select) {
        this.isAllSelected = select;
        if (!select) {
            this.isEditChange = false;
        }
        notifyItemRangeChanged(0, mDatas != null ? mDatas.size() : 0);
    }

    /**
     * 是否全选
     * @return true 是， false 否
     */
    public boolean isAllSelected() {
        return isAllSelected;
    }

    public List<HistoryRecord> getDeleteRecord() {
        Log.d(TAG, "getDeleteRecord");
        List<HistoryRecord> deleteVideoInfoRecord = new ArrayList<>();
        if (mDatas != null && !mDatas.isEmpty()) {
            deleteVideoInfoRecord = mDatas.stream().filter(HistoryRecord::isSelected).collect(Collectors.toList());
        }
        return deleteVideoInfoRecord;
    }

    public void delete(List<HistoryRecord> historyRecordList) {
        Log.d(TAG, "delete historyRecordList:" + historyRecordList);
        if (mDatas != null && !mDatas.isEmpty() && historyRecordList != null && !historyRecordList.isEmpty()) {
            for(HistoryRecord record: historyRecordList) {
                int index = mDatas.indexOf(record);
                mDatas.remove(record);
                notifyItemRemoved(index);
            }
        }
    }

    /**
     * 删除所有记录
     */
    public void deleteAllRecord() {
        Log.d(TAG, "deleteAllRecord");
        if(mDatas != null && !mDatas.isEmpty()) {
            mDatas.clear();
            notifyDataSetChanged();
        }
    }

    public boolean isEmpty() {
        return mDatas == null || mDatas.isEmpty();
    }
}
