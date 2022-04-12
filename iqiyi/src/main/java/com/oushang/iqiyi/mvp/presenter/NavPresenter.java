package com.oushang.iqiyi.mvp.presenter;

import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.oushang.iqiyi.R;
import com.oushang.iqiyi.fragments.fragmentUtils.FragmentHelper;
import com.oushang.iqiyi.mvp.view.INavView;
import com.oushang.lib_base.base.mvp.model.IModel;
import com.oushang.lib_base.base.mvp.presenter.BasePresenter;
import com.oushang.lib_base.base.mvp.view.BaseFragmentMVP;

/**
 * @Author: zeelang
 * @Description: 导航逻辑
 * @Time: 2021/7/6 14:58
 * @Since: 1.0
 */
public class NavPresenter extends BasePresenter<INavView, IModel> {

    @Override
    protected IModel createModel() {
        return null;
    }

    /**
     * 选中的导航
     * @param view 导航textview
     */
    public void navSelected(TextView view) {
        INavView navView = getView();
        if (navView != null) {
            navView.onNavSelected(view);
        }
    }


}
