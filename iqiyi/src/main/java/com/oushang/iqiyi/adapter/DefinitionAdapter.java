package com.oushang.iqiyi.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.oushang.iqiyi.R;
import com.oushang.iqiyi.common.Constant;
import com.oushang.iqiyi.events.EventBusHelper;
import com.oushang.iqiyi.events.EventConstant;
import com.oushang.iqiyi.statistics.DataStatistics;
import com.oushang.iqiyi.statistics.StatConstant;
import com.oushang.iqiyi.ui.OnPreventRepeatedClickListener;
import com.oushang.iqiyi.ui.ProgressButton;
import com.oushang.iqiyi.utils.ThemeManager;
import com.oushang.lib_base.base.rv.BaseAdapter;
import com.oushang.lib_base.base.rv.BaseViewHolder;
import com.oushang.lib_base.utils.SPUtils;
import com.oushang.lib_service.entries.VideoRate;
import com.oushang.lib_service.utils.VideoRateUtil;

import java.util.List;

/**
 * @Author: zeelang
 * @Description: 清晰度列表适配器
 * @Time: 2021/11/9 0009  11:43
 * @Since: 1.0
 */
public class DefinitionAdapter extends BaseAdapter<VideoRate, BaseViewHolder> {
    private static final String TAG = DefinitionAdapter.class.getSimpleName();

    public DefinitionAdapter(Context context, List<VideoRate> videoRateList) {
        super(context, videoRateList);
    }

    @Override
    public int setItemViewLayout() {
        return R.layout.item_definition_btn;
    }

    @Override
    public void onBindData(@NonNull BaseViewHolder holder, VideoRate data, int position) {
        Log.d(TAG, "VideoRate：" + data);
        int rt = data.getRt();
        String rateName = VideoRateUtil.getRateName(rt);
        boolean vip = data.isVip();
        Button definitionName = holder.getView(R.id.definition_name_btn);
        definitionName.setText(rateName);
        if (vip) {
            Drawable drawable = mContext.getDrawable(R.drawable.account_vip);
            definitionName.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
        }
        if (ThemeManager.getThemeMode() == ThemeManager.ThemeMode.NIGHT) {
            definitionName.setTextColor(mContext.getColor(R.color.color_skin_text));
            definitionName.setBackground(mContext.getDrawable(R.drawable.play_videoinfo_defination_skin));
        } else {
            definitionName.setTextColor(mContext.getColor(R.color.color_skin_text_notnight));
            definitionName.setBackground(mContext.getDrawable(R.drawable.play_videoinfo_defination_skin_notnight));
        }
        if (data.isSelected()) {
            Log.d(TAG, "selected:" + data);
            definitionName.setSelected(true);
            definitionName.setTextColor(mContext.getColor(R.color.white));
        } else {
            definitionName.setSelected(false);
        }

        definitionName.setOnClickListener(new OnPreventRepeatedClickListener() {
            @Override
            public void onNoRepeatedClick(View view) {
                setSelect(data.getRt());
                DataStatistics.recordUiEvent(StatConstant.EVENT_ID_5920, rateName);
                Bundle eventParams = new Bundle();
                eventParams.putInt(EventConstant.EVENT_PARAMS_PLAY_SELECT_DEFINITION, rt);
                EventBusHelper.post(EventBusHelper.newEvent(EventConstant.EVENT_TYPE_PLAY_SELECT_DEFINITION, eventParams));
            }
        });
    }

    private void updateVipDefinitionBtn(Button view, String definition) {
        Html.ImageGetter imageGetter = source -> {
            Drawable drawable = null;
            drawable = view.getResources().getDrawable(Integer.parseInt(source));
            int right = drawable.getIntrinsicWidth();
            int bottom = drawable.getIntrinsicHeight();
            drawable.setBounds(19, -10, right + 19, bottom + 10);
            return drawable;
        };
        StringBuffer sb = new StringBuffer();
        int status = SPUtils.getShareInteger(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_LOGIN_STATUS, 0);
        boolean isVip = SPUtils.getShareBoolean(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_LOGIN_ISVIP, false);
        if (status == 1 && isVip) {
            sb.append("<font color=\"#FFFFFF\">");
            view.setEnabled(true);
        } else {
            sb.append("<font color=\"#3E3E3E\">");
            view.setEnabled(false);
        }
        sb.append(definition).append("</font>").append("<img vertical-align:middle src=\"").append(R.drawable.account_vip).append("\"/>");
        Spanned span = Html.fromHtml(sb.toString(), Html.FROM_HTML_MODE_COMPACT, imageGetter, null);
        view.setText(span);
        sb = null;
    }

    public void setSelect(int rt) {
        Log.d(TAG, "select:" + rt);
        if (mDatas != null && !mDatas.isEmpty()) {
            for (VideoRate rate : mDatas) {
                rate.setSelected(rate.getRt() == rt);
            }
            notifyDataSetChanged();
        }
    }


}
