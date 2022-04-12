package com.oushang.iqiyi.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;

import com.oushang.iqiyi.R;

/**
 * @Author: zeelang
 * @Description: 搜索框
 * @Time: 2021/7/8 19:42
 * @Since: 1.0
 */
public class CustomSearchView extends SearchView {
    private static final String TAG = CustomSearchView.class.getSimpleName();

    private OnSearchClickListener searchClickListener;

    private TextView mSearchText;

    private ImageView mSearchIcon;

    private ImageView mSearchClear;

    public CustomSearchView(Context context) {
        super(context);
    }

    public CustomSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        initParams(context, attrs);
        initListener();
    }

    public CustomSearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressLint("ResourceType")
    public void initParams(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomSearchView);
        mSearchText.setTextColor(typedArray.getColor(R.styleable.CustomSearchView_search_text_color, 0xFFFFFF));
        mSearchText.setHint(typedArray.getString(R.styleable.CustomSearchView_search_text_hint));
        mSearchText.setHintTextColor(typedArray.getColor(R.styleable.CustomSearchView_search_text_hint_color, 0xFFFFFF));
        mSearchText.setTextSize(typedArray.getDimensionPixelSize(R.styleable.CustomSearchView_search_text_size,23));
        mSearchIcon.setImageResource(typedArray.getResourceId(R.styleable.CustomSearchView_search_icon, 0));
        ViewGroup.LayoutParams icon_layoutParams = mSearchIcon.getLayoutParams();
//        int width = mSearchIcon.getWidth();
//        int height = mSearchIcon.getHeight();

//        ViewGroup.LayoutParams icon_layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        Log.d(TAG, "search icon: width:" + width + ",height:" + height);
        icon_layoutParams.width = typedArray.getDimensionPixelSize(R.styleable.CustomSearchView_search_icon_width, 27);
        icon_layoutParams.height = typedArray.getDimensionPixelSize(R.styleable.CustomSearchView_search_icon_height, 27);
        Log.d(TAG, "search icon set:" + "width:" + icon_layoutParams.width + ",height:" + icon_layoutParams.height);
        mSearchIcon.setLayoutParams(icon_layoutParams);
        typedArray.recycle();
    }

    public void initView() {
        //不使用默认的搜索图标
        setIconifiedByDefault(false);
        //展开搜索输入区域
        onActionViewExpanded();
        //不显示下划线背景
        findViewById(androidx.appcompat.R.id.search_plate).setBackground(null);
        //不使用提交按钮
        findViewById(androidx.appcompat.R.id.submit_area).setBackground(null);
        //搜索输入
        mSearchText = findViewById(androidx.appcompat.R.id.search_src_text);
        //搜索图标
        mSearchIcon = findViewById(androidx.appcompat.R.id.search_mag_icon);
        //清空图标
        mSearchClear = findViewById(androidx.appcompat.R.id.search_close_btn);
        //默认不显示光标
        mSearchText.setCursorVisible(true);
        //默认不显示清空图标
        mSearchClear.setVisibility(GONE);
//        hideSoftKeyBoard();
//        clearFocus();
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//    }

    public void showSearchClearButton(boolean show) {
        mSearchClear.setVisibility(show? VISIBLE: GONE);
    }

    public TextView getSearchTextView() {
        return mSearchText;
    }

    public ImageView getSearchIconImg() {
        return mSearchIcon;
    }

    public void initListener() {
        mSearchText.setOnClickListener(searchClickListener);
        setOnClickListener(searchClickListener);

    }

    public void hideSoftKeyBoard() {
        InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mSearchText.getWindowToken(), 0);
    }

    public void setOnSearchClickListener(OnSearchClickListener clickListener) {
        this.searchClickListener = clickListener;
    }

    public interface OnSearchClickListener extends OnClickListener {
        void onClick(View v);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mSearchClear.setVisibility(GONE);
                if (searchClickListener != null) {
                    searchClickListener.onClick(this);
                    hideSoftKeyBoard();
                }
                break;
        }

        return super.onTouchEvent(event);
    }


}
