package com.u2tzjtne.aboutme.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.u2tzjtne.aboutme.R;
import com.u2tzjtne.aboutme.http.HttpHelper;

import java.util.List;

/**
 * Created by JK on 2018/1/9.
 */

public class ViewPageAdapter extends PagerAdapter {
    private Context context;
    private List<String> images;
    private SparseArray<View> cacheView;
    private ViewGroup containerTemp;

    public ViewPageAdapter(Context context, List<String> images) {
        this.context = context;
        this.images = images;
        cacheView = new SparseArray<>(images.size());
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        if (containerTemp == null) containerTemp = container;
        View view = cacheView.get(position);
        if (view == null) {
            view = LayoutInflater.from(context)
                    .inflate(R.layout.vp_app_item_image, container, false);
            view.setTag(position);
            PhotoView photoView = view.findViewById(R.id.app_image);
            //先加载原图的1/10大小的略缩图
            Glide.with(context).load(HttpHelper.URL + "image?name=" + images.get(position))
                    .thumbnail(0.1f).into(photoView);
        }
        cacheView.put(position, view);
        container.addView(view);
        return view;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        View view = (View) object;
        container.removeView(view);
    }
}
