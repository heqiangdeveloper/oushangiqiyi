package com.oushang.iqiyi.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.oushang.iqiyi.R;


/**
 * @Author: zeelang
 * @Description: 标签view
 * @Time: 2021/7/7 10:07
 * @Since: 1.0
 */
public class TagView extends FrameLayout {
    private static final String TAG = TagView.class.getSimpleName();

    //标签文本
    private TextView tag_tv;

    //标签编辑图标
    private ImageView tag_img;

    //触控热区
    private RelativeLayout tag_touch_layout;

    private OnClickListener onClickListener;

    //编辑点击监听
    private OnEditClickListener onEditClickListener;

    private OnTagEditClickListener onTagEditClickListener;

    //编辑状态变化监听
    private OnStateChangeListener onStateChangeListener;

    //是否启用编辑
    private boolean isEditEnable = false;

    //编辑启用状态监听
    private OnEditableChangeListener editableChangeListener;

    //编辑状态，只有启用编辑才可设置编辑状态,默认无编辑状态
    private EditState state = EditState.NONE;

    public enum EditState {
        NONE,      //无
        DELETABLE, //可添加
        ADDABLE,   //可删除
        COMPLETE   //已完成
    }

    public TagView(Context context) {
        this(context, null);
    }

    public TagView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public TagView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.tagview, this);
        tag_tv = findViewById(R.id.tag_tv);
        tag_img = findViewById(R.id.tag_img);
        tag_touch_layout = findViewById(R.id.tag_touch);
        tag_touch_layout.setVisibility(GONE);//热点触区
        tag_img.setVisibility(GONE);//默认不显示编辑图标
        tag_touch_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick tag touch layout!" + ":isEditEnable:" + isEditEnable + ",state:" + state);
                if (isEditEnable && state != EditState.COMPLETE) {
                    if (state == EditState.DELETABLE) {
                        setImageDrawable(context.getDrawable(R.drawable.channel_tag_delete_shape));
                        v.setVisibility(GONE);
                        tag_img.setVisibility(GONE);
                        tag_tv.setText("");
                    }
                    if (onEditClickListener != null) {
                        onEditClickListener.onClick(v, state);
                    }
                    if(onTagEditClickListener != null) {
                        onTagEditClickListener.onTagEditClick(TagView.this, state);
                    }
                }
            }
        });
    }

    public boolean isEditEnable() {
        return isEditEnable;
    }

    //设置编辑是否启用
    public void setEditEnable(boolean editEnable) {
        if (isEditEnable != editEnable) {
            isEditEnable = editEnable;
            if (editableChangeListener != null) {
                editableChangeListener.onChange(editEnable);
            } else {
                Log.e(TAG, "editableChangeListener is null");
            }
        }
    }

    public void setText(String text) {
        if (tag_tv != null) {
            tag_tv.setText(text);
            requestLayout();
            invalidate();
        }
    }

    public void setTagImageDrawable(Drawable drawable) {
        if (tag_img != null && tag_img.getVisibility() == VISIBLE) {
            tag_img.setImageDrawable(drawable);
            requestLayout();
            invalidate();
        }
    }

    public void setImageDrawable(Drawable drawable) {
        if (tag_tv != null) {
            tag_tv.setBackground(drawable);
        }
    }

    public void setTagImageResource(int resId) {
        if (tag_img != null && tag_img.getVisibility() == VISIBLE) {
            tag_img.setImageResource(resId);
            requestLayout();
            invalidate();
        }
    }

    public void setTagTouchBackRound(int resId) {
        if(tag_touch_layout != null && tag_touch_layout.getVisibility() ==VISIBLE) {
            tag_touch_layout.setBackground(getResources().getDrawable(resId, null));
            requestLayout();
            invalidate();
        }
    }

    public void setEditState(EditState state) {
        //若启用编辑，且编辑状态与设置的状态不同，则更新状态
        if ((isEditEnable && this.state != state) ||(state == EditState.NONE)) {
            if (onStateChangeListener != null ) {
                onStateChangeListener.onChange(state);
            }
            this.state = state;
        }
    }

    public EditState getState() {
        return state;
    }

    public TextView getTagTextView() {
        return tag_tv;
    }

    public ImageView getTagImageView() {
        return tag_img;
    }

    //设置编辑按钮是否显示
    public void setTagImageVisible(boolean visible) {
        if (tag_img != null) {
            if (visible && isEditEnable && tag_img.getVisibility() != VISIBLE) {
                tag_img.setVisibility(VISIBLE);
                tag_touch_layout.setVisibility(VISIBLE);
                tag_touch_layout.setClickable(true);
            } else {
                tag_img.setVisibility(GONE);
                tag_touch_layout.setVisibility(GONE);
                tag_touch_layout.setClickable(false);
            }
        }
    }

    public void setOnTagEditClickListener(OnTagEditClickListener onTagEditClickListener) {
        this.onTagEditClickListener = onTagEditClickListener;
    }

    /**
     * 编辑监听
     */
    public interface OnTagEditClickListener {
        void onTagEditClick(TagView view, EditState state);
    }

    public void setOnClickListener(OnClickListener listener) {
        this.onClickListener = listener;
    }

    public interface OnClickListener {
        void onClick(View v, EditState state);
    }

    public void setOnEditClickListener(OnEditClickListener editClickListener) {
        this.onEditClickListener = editClickListener;
    }

    /**
     * 编辑点击监听
     */
    public interface OnEditClickListener {
        void onClick(View v,EditState state);
    }

    /**
     * 设置编辑状态监听
     * @param listener
     */
    public void setOnStateChangeListener(OnStateChangeListener listener) {
        this.onStateChangeListener = listener;
    }

    /**
     * 编辑状态监听
     */
    public interface OnStateChangeListener {
        void onChange(EditState state);
    }

    /**
     * 编辑启用监听
     */
    public interface OnEditableChangeListener {
        void onChange(boolean isEditable);
    }

    /**
     * 设置编辑启用监听
     * @param changeListener 编辑启用监听
     */
    public void setOnEditableChangeListener (OnEditableChangeListener changeListener) {
        this.editableChangeListener = changeListener;
    }

}
