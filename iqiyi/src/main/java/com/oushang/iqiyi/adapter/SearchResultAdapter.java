package com.oushang.iqiyi.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.oushang.iqiyi.R;
import com.oushang.iqiyi.common.Constant;
import com.oushang.iqiyi.entries.MutiType;
import com.oushang.iqiyi.entries.SearchResultInfo;
import com.oushang.iqiyi.entries.SearchResultNoMore;
import com.oushang.iqiyi.statistics.DataStatistics;
import com.oushang.iqiyi.statistics.StatConstant;
import com.oushang.iqiyi.utils.AppUtils;
import com.oushang.lib_base.base.rv.BaseMultiAdapter;
import com.oushang.lib_base.base.rv.BaseViewHolder;
import com.oushang.lib_base.base.rv.IMultiItem;
import com.oushang.lib_base.image.GlideUtils;
import com.oushang.lib_service.entries.VideoInfo;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @Author: zeelang
 * @Description: 搜索结果信息适配器
 * @Time: 2021/12/16 0016  16:26
 * @Since: 1.0
 */
public class SearchResultAdapter <T extends IMultiItem> extends BaseMultiAdapter<T> {
    private boolean isAddFooter = false;
    private int mode;

    public SearchResultAdapter(Context context, List<T> datas, int mode) {
        super(context, datas);
        this.mode = mode;
        addViewType(MutiType.IMAGE, R.layout.item_search_result_albumpic);
        addViewType(MutiType.TEXT, R.layout.item_search_result_no_more);
    }

    @Override
    public void onBindData(@NonNull BaseViewHolder holder, T data, int position) {
        holder.setIsRecyclable(false);
        if(data instanceof SearchResultInfo) {
            VideoInfo videoInfo = ((SearchResultInfo) data).getVideoInfo();
            TextView searchAlbumName = holder.getView(R.id.search_result_album_name);
            TextView searchAlbumFocus = holder.getView(R.id.search_result_album_focus);
            ImageView exclusiveImg = holder.getView(R.id.search_result_album_exclusive);
            ImageView vipImg = holder.getView(R.id.search_result_album_vip);
            ImageView album = holder.getView(R.id.search_result_album_pic);

            String name ="";
            name = videoInfo.getShortName();
            if (name == null || name.isEmpty()) {
                name = videoInfo.getName();
            }
            searchAlbumName.setText(name);

            String focus = "";

            focus = videoInfo.getFocus();
            if (focus == null || focus.isEmpty()) {
                focus = videoInfo.getSubTitle();
            }
            searchAlbumFocus.setText(focus);

            if (videoInfo.isVip()) {
                exclusiveImg.setVisibility(View.GONE);
                vipImg.setVisibility(View.VISIBLE);
            } else if(videoInfo.isExclusive()) {
                exclusiveImg.setVisibility(View.VISIBLE);
                vipImg.setVisibility(View.GONE);
            }else{
                exclusiveImg.setVisibility(View.GONE);
                vipImg.setVisibility(View.GONE);
            }

            String albumPic = videoInfo.getAlbumPic();
            if (albumPic != null && !albumPic.isEmpty()) {
                String newUrl = AppUtils.appendImageUrl(albumPic, "_260_360");
                GlideUtils.loadImageWithRoundedCorners(mContext, Uri.parse(newUrl), album,R.drawable.album_pic_place_holder,R.drawable.album_pic_place_holder, 8);
            }

            long qipuId = videoInfo.getQipuId();
            long albumId = videoInfo.getAlbumId();

            if (videoInfo.isAlbum()) {
                albumId = qipuId;
                if(videoInfo.getDefaultEpi() != null) {
                    qipuId = videoInfo.getDefaultEpi().getQipuId();
                }
            }

            final long qid = qipuId;
            final long aid = albumId;

            holder.getItemView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Map<String, String> statsValue = new HashMap<>();
                    String type = "";
                    switch (mode) {
                        case 1:
                            type = "相关";
                            break;
                        case 4:
                            type = "最新";
                            break;
                        case 10:
                        case 11:
                            type = "最热";
                            break;
                    }
                    statsValue.put("resultType", type);
                    statsValue.put("item", searchAlbumName.getText().toString());
                    statsValue.put("plate", videoInfo.getChnName());
                    statsValue.put("type", videoInfo.isVip()?"vip":"无");
                    DataStatistics.recordUiEvent(StatConstant.EVENT_ID_5955, statsValue); //埋点数据

                    ARouter.getInstance().build(Constant.PATH_ACTIVITY_PLAYER)
                            .withLong(Constant.PLAY_VIDEO_ID, qid)
                            .withLong(Constant.PLAY_ALBUM_ID, aid)
                            .withLong(Constant.PLAY_POSITION, 0)
                            .navigation();
                }
            });

        } else if(data instanceof SearchResultNoMore) {
            TextView textView = holder.getView(R.id.search_result_no_more_text);
            textView.setText(((SearchResultNoMore) data).getText());

        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if(layoutManager instanceof GridLayoutManager) {
            ((GridLayoutManager) layoutManager).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    T t = mDatas.get(position);
                    if(t instanceof SearchResultInfo) {
                      return  ((SearchResultInfo) t).getSpanSize();
                    } else if(t instanceof SearchResultNoMore) {
                       return  ((SearchResultNoMore) t).getSpanSize();
                    }
                    return ((GridLayoutManager) layoutManager).getSpanCount();
                }
            });
        }
    }

    public void addData(List<T> data) {
        if(mDatas != null && !mDatas.isEmpty()) {
            int size = mDatas.size();
            mDatas.addAll(data);
            notifyItemRangeInserted(size, data.size());
        }
    }

    public void addFooter(T data) {
        if(mDatas != null && !mDatas.isEmpty() && !isAddFooter) {
            int size = mDatas.size();
            mDatas.add(data);
            isAddFooter = true;
            notifyItemChanged(size);
        }
    }

    public boolean isAddFooter() {
        return isAddFooter;
    }

    /**
     * 获取第几个视频
     * @param num 第几个
     * @return 视频信息
     */
    public T getData(int num) {
        if (mDatas != null && !mDatas.isEmpty()) {
            int position = num - 1;
            if (position >= 0 && position < mDatas.size()) {
                return mDatas.get(position);
            }
        }
        return null;
    }
}
