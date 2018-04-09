package com.u2tzjtne.aboutme.ui.holder;

import android.view.View;
import android.widget.TextView;

import com.u2tzjtne.aboutme.R;
import com.u2tzjtne.aboutme.bean.AppBean;
import com.u2tzjtne.aboutme.util.UIUtils;


/**
 * 详情页-应用其它信息
 *
 * @author Kevin
 */
public class DetailAppOtherHolder extends BaseHolder<AppBean> {

    private TextView tvDeveloper;
    private TextView tvUpdateDate;
    private TextView tvVersion;

    @Override
    public View initView() {
        View view = View.inflate(UIUtils.getContext(),
                R.layout.layout_detail_other_info, null);
        tvDeveloper = view.findViewById(R.id.tv_detail_other_developer_info);
        tvUpdateDate = view.findViewById(R.id.tv_detail_other_update_date_info);
        tvVersion = view.findViewById(R.id.tv_detail_other_update_version_info);
        return view;
    }

    @Override
    public void refreshView(AppBean data) {
        if (data != null) {
            tvDeveloper.setText(data.getAuthor());
            tvUpdateDate.setText(data.getDate());
            tvVersion.setText(data.getVersion());
        }
    }
}
