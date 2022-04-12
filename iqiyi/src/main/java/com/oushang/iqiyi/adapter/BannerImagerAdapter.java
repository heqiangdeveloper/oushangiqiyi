package com.oushang.iqiyi.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.oushang.iqiyi.R;
import com.oushang.iqiyi.entries.BannerEntry;
import com.oushang.iqiyi.utils.AppUtils;
import com.oushang.lib_base.image.Glide2Utils;
import com.youth.banner.adapter.BannerAdapter;

import java.util.List;

/**
 * @Author: zeelang
 * @Description: 轮播图适配器
 * @Time: 2021/7/7 14:55
 * @Since: 1.0
 */
public class BannerImagerAdapter extends BannerAdapter<BannerEntry, BannerImagerAdapter.BannerViewHolder> {
    private static final String TAG = BannerImagerAdapter.class.getSimpleName();

    public BannerImagerAdapter(List<BannerEntry> datas) {
        super(datas);
    }

    @Override
    public BannerViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_banner_image_text, parent, false);
        return new BannerViewHolder(rootView);
    }

    @Override
    public void onBindView(BannerViewHolder holder, BannerEntry data, int position, int size) {
        String url = data.getImgUrl();
        if (url != null && !url.isEmpty()) {
            String newUrl = AppUtils.appendImageUrl(url, "_720_405");
            Glide2Utils.loadRoundedCorners(holder.imageView.getContext(), Uri.parse(newUrl), holder.imageView, R.drawable.ic_banner_place_holder, R.drawable.ic_banner_place_holder, 16);
        }
        holder.textView.setText(data.getTitle());
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {
        View rootView;
        ImageView imageView;
        TextView textView;

        public BannerViewHolder(@NonNull View view) {
            super(view);
            this.rootView = view;
            init();
        }

        private void init() {
            imageView = rootView.findViewById(R.id.hot_recommend_banner_image);
            textView = rootView.findViewById(R.id.hot_recommend_banner_focus);
        }
    }
}
