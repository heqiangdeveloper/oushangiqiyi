package com.oushang.iqiyi.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentHostCallback;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.oushang.iqiyi.R;
import com.oushang.iqiyi.base.BasePlayFragment;
import com.oushang.iqiyi.fragments.fragmentUtils.FragmentHelper;
import com.oushang.iqiyi.ui.LineItemDecoration;
import com.oushang.iqiyi.ui.SimpleFastAdapter;
import com.oushang.lib_base.base.mvp.presenter.BasePresenter;
import com.oushang.lib_base.base.mvp.view.BaseFragmentMVP;
import com.oushang.lib_base.base.rv.BaseViewHolder;
import com.oushang.lib_base.image.GlideUtils;
import com.oushang.lib_service.constant.Constant;
import com.oushang.lib_service.entries.CastInfo;
import com.oushang.lib_service.entries.PersonInfo;
import com.oushang.lib_service.interfaces.PlayManager;

import java.util.List;
import java.util.stream.Collectors;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @Author: Administrator
 * @Description: ***
 * @Time: 2021/12/1 0001  9:50
 * @Since: 1.0
 */
public class TimeSelectionsDetailsFragment extends BasePlayFragment {
    private static final String TAG = TimeSelectionsDetailsFragment.class.getSimpleName();

    private static final String DESC = "desc";

    private static final String CAST_INFO = "cast_info";

    @BindView(R.id.episode_details_desc)
    TextView mVideoInfoDesc;

    @BindView(R.id.episode_details_main_actor)
    RecyclerView mVideoInfoMainActor;

    @Autowired(name = Constant.PATH_SERVICE_PLAY_MANAGER)
    PlayManager mPlayManager;

    @Override
    protected int setLayout() {
        return R.layout.fragment_play_videoinfo_episodedetails;
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    public static TimeSelectionsDetailsFragment newInstance(String desc, CastInfo castInfo) {
        Bundle bundle = new Bundle();
        bundle.putString(DESC, desc);
        bundle.putParcelable(CAST_INFO, castInfo);
        TimeSelectionsDetailsFragment fragment = new TimeSelectionsDetailsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @OnClick(R.id.episode_details_back_layout)
    public void onBack() {
        if (mPlayManager.isFullScreen()) {
            if (getActivity() != null) {
                FragmentHelper.handlerBack(getActivity().getSupportFragmentManager());
            }
        } else {
            FragmentHelper.handlerBack(getFragmentManager());
        }
    }

    @Override
    protected void initView() {
        super.initView();
        mVideoInfoDesc.setMovementMethod(ScrollingMovementMethod.getInstance());
        if (mVideoInfoMainActor.getItemDecorationCount() == 0) {
            mVideoInfoMainActor.addItemDecoration(new LineItemDecoration(24, LineItemDecoration.HORIZONTAL));
        }
        Bundle arguments = getArguments();
        if (arguments != null) {
            String desc = arguments.getString(DESC);
            CastInfo castInfo = arguments.getParcelable(CAST_INFO);
            mVideoInfoDesc.setText(desc == null || desc.isEmpty() ? "无" : desc);
            if (castInfo != null) {
                //获取主演信息
                List<PersonInfo> mainActorList = castInfo.getMainActor().stream().limit(8).collect(Collectors.toList());
                mVideoInfoMainActor.setAdapter(new SimpleFastAdapter<PersonInfo>(getContext(), R.layout.item_main_actor, mainActorList) {
                    @Override
                    public void convert(BaseViewHolder holder, PersonInfo data, int position) {
                        if (data == null) {
                            return;
                        }
                        ImageView actorPic = holder.getView(R.id.actor_pic);
                        String cover = data.getCover();
                        if (cover != null && !cover.isEmpty()) {
                            GlideUtils.load(getContext(), Uri.parse(cover),actorPic, R.drawable.ic_albumpic_place_holder, R.drawable.ic_albumpic_place_holder);
                        }else{
                            actorPic.setImageResource(R.drawable.ic_albumpic_place_holder);
                        }
                        TextView textView = holder.getView(R.id.actor_name);
                        textView.setText(data.getName());
                    }
                });
            }

        }
    }
}
