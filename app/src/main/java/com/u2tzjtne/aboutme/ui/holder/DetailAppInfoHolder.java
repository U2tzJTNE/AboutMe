package com.u2tzjtne.aboutme.ui.holder;

import android.text.format.Formatter;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.u2tzjtne.aboutme.R;
import com.u2tzjtne.aboutme.bean.AppBean;
import com.u2tzjtne.aboutme.http.HttpHelper;
import com.u2tzjtne.aboutme.util.UIUtils;

/**
 * 详情页-应用信息
 *
 * @author Kevin
 */
public class DetailAppInfoHolder extends BaseHolder<AppBean> {

    private TextView tvName;
    private TextView tvDownloadNum;
    private TextView tvSize;
    private ImageView ivIcon;
    private RatingBar rbStar;

    @Override
    public View initView() {
        View view = View.inflate(UIUtils.getContext(),
                R.layout.layout_detail_app_info, null);
        tvName = view.findViewById(R.id.detail_tv_name);
        tvDownloadNum = view.findViewById(R.id.tv_download_num);
        tvSize = view.findViewById(R.id.detail_tv_size);
        ivIcon = view.findViewById(R.id.detail_iv_icon);
        rbStar = view.findViewById(R.id.detail_rb_star);
        return view;
    }

    @Override
    public void refreshView(AppBean data) {
        if (data != null) {
            tvName.setText(data.getName());
            tvSize.setText(Formatter.formatFileSize(UIUtils.getContext(),
                    data.getSize()));
            tvDownloadNum.setText(data.getDownloadNum());
            rbStar.setRating((float) data.getStars());
            Glide.with(UIUtils.getContext())
                    .load(HttpHelper.URL + "image?name=" + data.getIconUrl()).into(ivIcon);
        }
    }
}
