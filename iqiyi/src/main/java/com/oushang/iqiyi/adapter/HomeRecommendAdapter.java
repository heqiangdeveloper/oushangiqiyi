package com.oushang.iqiyi.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Outline;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.callback.NavCallback;
import com.alibaba.android.arouter.facade.callback.NavigationCallback;
import com.alibaba.android.arouter.launcher.ARouter;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.jakewharton.rxbinding2.view.RxView;
import com.oushang.iqiyi.R;
import com.oushang.iqiyi.activities.PlayerActivity;
import com.oushang.iqiyi.common.Constant;
import com.oushang.iqiyi.entries.BannerEntry;
import com.oushang.iqiyi.entries.ChannelParentTag;
import com.oushang.iqiyi.entries.HotRecommendAlbumPic;
import com.oushang.iqiyi.entries.HotRecommendBanner;
import com.oushang.iqiyi.entries.MutiType;
import com.oushang.iqiyi.events.EventBusHelper;
import com.oushang.iqiyi.events.EventConstant;
import com.oushang.iqiyi.fragments.ClassifyDetailsFragment;
import com.oushang.iqiyi.fragments.MeFragment;
import com.oushang.iqiyi.statistics.DataStatistics;
import com.oushang.iqiyi.statistics.StatConstant;
import com.oushang.iqiyi.ui.GridItemDecoration;
import com.oushang.iqiyi.ui.IqiyiBanner;
import com.oushang.iqiyi.ui.OnPreventRepeatedClickListener;
import com.oushang.iqiyi.ui.RxCustomView;
import com.oushang.iqiyi.ui.SimpleFastAdapter;
import com.oushang.iqiyi.utils.AppUtils;
import com.oushang.lib_base.base.rv.BaseMultiAdapter;
import com.oushang.lib_base.base.rv.BaseViewHolder;
import com.oushang.lib_base.base.rv.IMultiItem;
import com.oushang.lib_base.image.Glide2Utils;
import com.oushang.lib_base.image.GlideUtils;
import com.oushang.lib_base.utils.SPUtils;
import com.oushang.lib_service.entries.ChannelTag;
import com.youth.banner.Banner;
import com.youth.banner.indicator.RectangleIndicator;
import com.youth.banner.listener.OnBannerListener;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * @Author: zeelang
 * @Description: ?????????????????????
 * @Time: 2021/7/26 18:20
 * @Since: 1.0
 */
public class HomeRecommendAdapter<T extends IMultiItem> extends BaseMultiAdapter<T> {
    private static final String TAG = HomeRecommendAdapter.class.getSimpleName();

    private boolean isInsertChange = false;
    private Disposable clickDisposable;
    private OnBannerScrollListener mOnBannerScrollListener;

    public HomeRecommendAdapter(Context context, List<T> datas) {
        super(context, datas);
        addViewType(MutiType.RECOMMEND_BANNER, R.layout.item_hot_recommend_banner);
        addViewType(MutiType.RECOMMEND_ALBUMPIC, R.layout.item_hot_channel_albumpic);
    }

    @Override
    public void onBindData(@NonNull BaseViewHolder holder, T data, int position) {
        Log.d(TAG, "onBindData");
        if (data instanceof HotRecommendBanner) { //???????????????????????????
            Log.d(TAG, "onBindData:HotRecommendBanner");
            IqiyiBanner banner = holder.getView(R.id.hot_recommend_banner);//???????????????
            List<BannerEntry> bannerEntryList = new ArrayList<>();//???????????????
            List<HotRecommendBanner.RecommendBanner> recommendBannerList = ((HotRecommendBanner) data).getRecommendBannerList();//???????????????????????????
            for (HotRecommendBanner.RecommendBanner recommendBanner : recommendBannerList) {
                //????????????id
                long qipuId = recommendBanner.getQipuId();
                //????????????id
                long albumId = recommendBanner.getAlbumId();
                //???????????????
                String posterPic = recommendBanner.getPosterPic();
                //?????????????????????
                String focus = recommendBanner.getFocus();
                //???????????????????????????
                bannerEntryList.add(new BannerEntry(qipuId, albumId, posterPic, focus));
            }
            banner.setAdapter(new BannerImagerAdapter(bannerEntryList))//????????????????????????
                    .setIndicator(new RectangleIndicator(mContext))//???????????????
                    .start();//???????????????
            banner.setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), 16);
                }
            });
            banner.setClipToOutline(true);
            banner.setOnNestedScrollListener(new IqiyiBanner.OnNestedScrollListener() {
                @Override
                public void onStartScroll() {
                    if(mOnBannerScrollListener != null) {
                        mOnBannerScrollListener.onScrollStart();
                    }
                }

                @Override
                public void onEndScroll() {
                    if(mOnBannerScrollListener != null) {
                        mOnBannerScrollListener.onScrollEnd();
                    }
                }
            });


            banner.setOnBannerListener(new OnBannerListener() {
                @Override
                public void OnBannerClick(Object data, int position) {
                    if (data instanceof BannerEntry) {
                        long qipuId = ((BannerEntry) data).getQipuId();
                        long albumId = ((BannerEntry) data).getAlbumId();
                        Log.d(TAG, "router player activity!");
                        ARouter.getInstance().build(Constant.PATH_ACTIVITY_PLAYER)
                                .withLong(Constant.PLAY_VIDEO_ID, qipuId)
                                .withLong(Constant.PLAY_ALBUM_ID, albumId)
                                .navigation();
                    }
                }
            });

            //????????????-??????
            RelativeLayout moveChannel = holder.getView(R.id.move_channel);

            //????????????-??????
            RelativeLayout varietyChannel = holder.getView(R.id.variety_channel);

            //????????????-??????
            RelativeLayout memberApply = holder.getView(R.id.member_apply);

            //????????????-??????
            RelativeLayout favoritesAction = holder.getView(R.id.favorites_action);

            //????????????-??????
            RelativeLayout recentAction = holder.getView(R.id.recent_action);

            //??????????????????
            moveChannel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "????????????????????????????????????????????????");
                    DataStatistics.recordUiEvent(StatConstant.EVENT_ID_5906, "??????");
//                    ChannelTag tag = new ChannelTag(1,"??????");
                    com.oushang.lib_service.entries.ChannelTag channelTag = new com.oushang.lib_service.entries.ChannelTag("1", "??????");
                    ChannelParentTag tag = new ChannelParentTag(channelTag);
                    Bundle eventParams = new Bundle();
                    eventParams.putInt(EventConstant.EVENT_PARAMS_SELECT_TAB_ID, R.id.nav_tab_classify);
                    eventParams.putSerializable(EventConstant.EVENT_PARAMS_FRAGMENT_CLASS, ClassifyDetailsFragment.class);
                    Bundle args = new Bundle();
                    args.putParcelable(Constant.BUNDLE_KEY_CHANNEL_PARENT_TAG, tag);
                    eventParams.putBundle(EventConstant.EVENT_PARAMS_FRAGMENT_ARGS, args);
                    EventBusHelper.post(EventBusHelper.newEvent(EventConstant.EVENT_TYPE_LOAD_CHILD_FRAGMENT, eventParams));

                    updateTitleLayout("??????");
                }
            });
            //??????????????????
            varietyChannel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "????????????????????????????????????????????????");
                    DataStatistics.recordUiEvent(StatConstant.EVENT_ID_5906, "??????");
//                    ChannelTag tag = new ChannelTag(6,"??????");
                    com.oushang.lib_service.entries.ChannelTag channelTag = new com.oushang.lib_service.entries.ChannelTag("6", "??????");
                    ChannelParentTag tag = new ChannelParentTag(channelTag);
                    Bundle eventParams = new Bundle();
                    eventParams.putInt(EventConstant.EVENT_PARAMS_SELECT_TAB_ID, R.id.nav_tab_classify);
                    eventParams.putSerializable(EventConstant.EVENT_PARAMS_FRAGMENT_CLASS, ClassifyDetailsFragment.class);
                    Bundle args = new Bundle();
                    args.putParcelable(Constant.BUNDLE_KEY_CHANNEL_PARENT_TAG, tag);
                    eventParams.putBundle(EventConstant.EVENT_PARAMS_FRAGMENT_ARGS, args);
                    EventBusHelper.post(EventBusHelper.newEvent(EventConstant.EVENT_TYPE_LOAD_CHILD_FRAGMENT, eventParams));

                    updateTitleLayout("??????");
                }
            });
            //????????????
            memberApply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DataStatistics.recordUiEvent(StatConstant.EVENT_ID_5906, "??????");
                    int status = SPUtils.getShareInteger(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_LOGIN_STATUS, 0);
                    if (status == 1) {
                        ARouter.getInstance().build(Constant.PATH_ACTIVITY_MEMBER_QRCODE).navigation();
                    } else {
                        String targetCls = "com.oushang.iqiyi" + ".activities.AccountActivity";
                        Intent intent = new Intent();
                        ComponentName componentName = new ComponentName("com.oushang.iqiyi", targetCls);
                        intent.setComponent(componentName);
                        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("driveCenterAction", Constant.ACTION_VIP);
                        mContext.startActivity(intent);
                    }
                }
            });
            //????????????
            favoritesAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "????????????????????????????????????");
                    DataStatistics.recordUiEvent(StatConstant.EVENT_ID_5906, "??????");
                    Bundle eventParams = new Bundle();
                    eventParams.putInt(EventConstant.EVENT_PARAMS_SELECT_TAB_ID, R.id.nav_tab_my);
                    eventParams.putSerializable(EventConstant.EVENT_PARAMS_FRAGMENT_CLASS, MeFragment.class);
                    Bundle args = new Bundle();
                    args.putInt(Constant.BUNDLE_KEY_RECORD_TYPE, Constant.BUNDLE_VALUE_RECORD_TYPE_FAVORITE);
                    eventParams.putBundle(EventConstant.EVENT_PARAMS_FRAGMENT_ARGS, args);
                    EventBusHelper.post(EventBusHelper.newEvent(EventConstant.EVENT_TYPE_LOAD_CHILD_FRAGMENT, eventParams));
                }
            });
            //????????????
            recentAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "????????????????????????????????????");
                    DataStatistics.recordUiEvent(StatConstant.EVENT_ID_5906, "??????");
                    Bundle eventParams = new Bundle();
                    eventParams.putInt(EventConstant.EVENT_PARAMS_SELECT_TAB_ID, R.id.nav_tab_my);
                    eventParams.putSerializable(EventConstant.EVENT_PARAMS_FRAGMENT_CLASS, MeFragment.class);
                    Bundle args = new Bundle();
                    args.putInt(Constant.BUNDLE_KEY_RECORD_TYPE, Constant.BUNDLE_VALUE_RECORD_TYPE_HISTORY);
                    eventParams.putBundle(EventConstant.EVENT_PARAMS_FRAGMENT_ARGS, args);
                    EventBusHelper.post(EventBusHelper.newEvent(EventConstant.EVENT_TYPE_LOAD_CHILD_FRAGMENT, eventParams));
                }
            });

        } else if (data instanceof HotRecommendAlbumPic) { //???????????????????????????
            Log.d(TAG, "onBindData:HotRecommendAlbumPic");
            //????????????????????????
            List<HotRecommendAlbumPic.RecommendAlbumPic> recommendAlbumPics = ((HotRecommendAlbumPic) data).getRecommendAlbumPic();
            if (recommendAlbumPics == null || recommendAlbumPics.isEmpty()) {
                Log.e(TAG, "recommendAlbumPics is null!");
                return;
            }
            //?????????????????????????????????
            HotRecommendAlbumPic.RecommendAlbumPic recommendAlbumPic = recommendAlbumPics.get(0);
            //??????????????????
            String chnName = recommendAlbumPic.getChnName();
            Log.d(TAG, "chnName:" + chnName);
            //????????????title
            TextView hotChannelTitleName = holder.getView(R.id.hot_channel_title);
            //??????????????????
            LinearLayout hotChannelMore = holder.getView(R.id.hot_channel_more_layout);
            //????????????????????????
            hotChannelTitleName.setText(chnName);
            //????????????????????????
            hotChannelMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "????????????????????????????????????");
//                    EventBus.getDefault().post(new LoadFragmentEvent(R.id.nav_tab_classify, ClassifyDetailsFragment.class, null));

//                    ChannelTag tag = new ChannelTag(recommendAlbumPic.getChnId(),recommendAlbumPic.getChnName());

                    ChannelParentTag tag = new ChannelParentTag(new ChannelTag(String.valueOf(recommendAlbumPic.getChnId()), recommendAlbumPic.getChnName()));
                    Bundle eventParams = new Bundle();
                    eventParams.putInt(EventConstant.EVENT_PARAMS_SELECT_TAB_ID, R.id.nav_tab_classify);
                    eventParams.putSerializable(EventConstant.EVENT_PARAMS_FRAGMENT_CLASS, ClassifyDetailsFragment.class);
                    Bundle args = new Bundle();
                    args.putParcelable(Constant.BUNDLE_KEY_CHANNEL_PARENT_TAG, tag);
                    eventParams.putBundle(EventConstant.EVENT_PARAMS_FRAGMENT_ARGS, args);
                    EventBusHelper.post(EventBusHelper.newEvent(EventConstant.EVENT_TYPE_LOAD_CHILD_FRAGMENT, eventParams));

                    updateTitleLayout(recommendAlbumPic.getChnName());
                }
            });
            //??????????????????????????????
            RecyclerView albumPicRV = holder.getView(R.id.hot_channel_albumPic);
            GridLayoutManager layoutManager = new GridLayoutManager(mContext, 3) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            };//????????????
            albumPicRV.setLayoutManager(layoutManager);//????????????

            if (albumPicRV.getItemDecorationCount() == 0) {//???????????????
                albumPicRV.addItemDecoration(new GridItemDecoration(20, 3, GridItemDecoration.HORIZONTAL));//????????????
                albumPicRV.addItemDecoration(new GridItemDecoration(27, GridItemDecoration.VERTICAL));//????????????
            }
            layoutManager.setSmoothScrollbarEnabled(false);//????????????

            SimpleFastAdapter<HotRecommendAlbumPic.RecommendAlbumPic> albumPicAdapter = new SimpleFastAdapter<HotRecommendAlbumPic.RecommendAlbumPic>(mContext, R.layout.item_image, recommendAlbumPics) {
                @Override
                public void convert(BaseViewHolder holder, HotRecommendAlbumPic.RecommendAlbumPic recommendAlbumPic, int position) {
                    //?????????
                    String albumPic = recommendAlbumPic.getAlbumPic();
                    //????????????
                    String name = recommendAlbumPic.getName();
                    //????????????
                    String albumName = recommendAlbumPic.getAlbumName();
                    //???????????????
                    String focus = recommendAlbumPic.getFocus();
                    //????????????
                    boolean isExclusive = recommendAlbumPic.isExclusive();
                    //?????????vip
                    boolean isVip = recommendAlbumPic.isVip();
                    //????????????view
                    ImageView ablumPicView = holder.getView(R.id.hot_channel_albumPic);
                    //????????????view
                    TextView nameView = holder.getView(R.id.hot_channel_video_name);
                    //???????????????view
                    TextView focusView = holder.getView(R.id.hot_channel_video_focus);

                    if (albumPic != null && !albumPic.isEmpty()) {
                        String albumPicUrl = AppUtils.appendImageUrl(albumPic, "_284_160");
//                        GlideUtils.loadImageWithRoundedCorners(mContext, Uri.parse(albumPicUrl), ablumPicView, R.drawable.ic_albumpic_place_holder, R.drawable.ic_albumpic_place_holder, 10);
                        Glide2Utils.load(mContext, Uri.parse(albumPicUrl), ablumPicView, R.drawable.hot_channel_album_pic_place_holder, R.drawable.hot_channel_album_pic_place_holder, 10);
//                        Glide2Utils.load(mContext, Uri.parse(albumPicUrl), ablumPicView, R.layout.item_hot_channel_albumpic_place_holder, R.layout.item_hot_channel_albumpic_place_holder, 10);
                    }

                    //????????????
                    ImageView remark = holder.getView(R.id.hot_channel_remark);

                    if (isExclusive) {
//                        GlideUtils.loadRes(mContext, R.drawable.hot_search_album_exclusive, remark);
                        Glide2Utils.loadRes(mContext, R.drawable.hot_search_album_exclusive, remark);
                    }
                    if (isVip) {
//                        GlideUtils.loadRes(mContext, R.drawable.hot_search_album_vip, remark);
                        Glide2Utils.loadRes(mContext, R.drawable.hot_search_album_vip, remark);
                    }

                    if (name.isEmpty()) {
                        nameView.setText(albumName);
                    } else {
                        nameView.setText(name);
                    }
                    focusView.setText(focus);

                    //????????????
                    clickDisposable = RxView.clicks(holder.getItemView())
                            .throttleFirst(2, TimeUnit.SECONDS)
                            .subscribe(o -> {
                                Log.d(TAG, "router player activity!");
                                Map<String,String> statsValue = new HashMap<>();
                                statsValue.put("item", name);
                                statsValue.put("plate", recommendAlbumPic.getChnName());
                                DataStatistics.recordUiEvent(StatConstant.EVENT_ID_5956, statsValue);//????????????

                                ARouter.getInstance().build(Constant.PATH_ACTIVITY_PLAYER)
                                        .withLong(Constant.PLAY_VIDEO_ID, recommendAlbumPic.getQipuId())
                                        .withLong(Constant.PLAY_ALBUM_ID, recommendAlbumPic.getAlbumId())
                                        .navigation();
                            });
                }
            };

            if (isInsertChange) {
                //?????????
                SkeletonScreen skeletonScreen = Skeleton.bind(albumPicRV)
                        .adapter(albumPicAdapter)
                        .load(R.layout.item_hot_channel_albumpic_skeleton)
                        .shimmer(false)
                        .show();
                albumPicRV.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        skeletonScreen.hide();

                    }
                }, 200);


            } else {
                albumPicRV.setAdapter(albumPicAdapter);
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addData(List<T> data) {
        int oldPosition = mDatas.size();
        mDatas.addAll(data);
        notifyItemRangeInserted(oldPosition, data.size());
        isInsertChange = true;
    }

    private void updateTitleLayout(String title) {
        Bundle eventParams = new Bundle();
        eventParams.putString(EventConstant.EVENT_PARAMS_UPDATE_NESTED_TITLE_LAYOUT_WHO, ClassifyDetailsFragment.class.getName());
        eventParams.putString(EventConstant.EVENT_PARAMS_NESTED_TITLE_SHOW_BACK, title);
        EventBusHelper.post(EventBusHelper.newEvent(EventConstant.EVENT_TYPE_UPDATE_TITLE_LAYOUT, eventParams));
    }

    public void setOnBannerScrollListener(OnBannerScrollListener listener) {
        this.mOnBannerScrollListener = listener;
    }

    public interface OnBannerScrollListener{
        void onScrollStart();
        void onScrollEnd();
    }
}
