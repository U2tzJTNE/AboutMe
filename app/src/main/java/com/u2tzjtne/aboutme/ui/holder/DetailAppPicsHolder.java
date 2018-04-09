package com.u2tzjtne.aboutme.ui.holder;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.u2tzjtne.aboutme.R;
import com.u2tzjtne.aboutme.bean.AppBean;
import com.u2tzjtne.aboutme.http.HttpHelper;
import com.u2tzjtne.aboutme.ui.activity.AppPictureDetailActivity;
import com.u2tzjtne.aboutme.util.UIUtils;

/**
 * 详情页-应用图片
 *
 * @author Kevin
 */
public class DetailAppPicsHolder extends BaseHolder<AppBean> {

    private ImageView[] mImageViews;
    private Context context;

    @Override
    public View initView() {
        context = UIUtils.getContext();
        View view = View.inflate(UIUtils.getContext(),
                R.layout.layout_detail_picture_info, null);

        mImageViews = new ImageView[5];
        mImageViews[0] = view.findViewById(R.id.iv_pic1);
        mImageViews[1] = view.findViewById(R.id.iv_pic2);
        mImageViews[2] = view.findViewById(R.id.iv_pic3);
        mImageViews[3] = view.findViewById(R.id.iv_pic4);
        mImageViews[4] = view.findViewById(R.id.iv_pic5);
        return view;
    }

    @Override
    public void refreshView(AppBean data) {
        if (data != null) {
            final ArrayList<String> list = data.getScreen();
            for (int i = 0; i < 5; i++) {
                if (i < list.size()) {
                    mImageViews[i].setVisibility(View.VISIBLE);
                    Glide.with(UIUtils.getContext())
                            .load(HttpHelper.URL + "image?name=" + list.get(i)).into(mImageViews[i]);
                    final int finalI = i;
                    mImageViews[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(UIUtils.getContext(), list, finalI);
                        }
                    });
                } else {
                    mImageViews[i].setVisibility(View.GONE);
                }
            }
        }
    }

    private void startActivity(Context context, ArrayList<String> images, int position) {
        Intent intent = new Intent(context, AppPictureDetailActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putStringArrayListExtra("images", images);
        intent.putExtra("position", position);
        context.startActivity(intent);
    }

}
