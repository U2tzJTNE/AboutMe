package com.u2tzjtne.aboutme.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.u2tzjtne.aboutme.R;
import com.u2tzjtne.aboutme.bean.MomentsBean;
import com.u2tzjtne.aboutme.ui.activity.MomentsDetailActivity;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by JK on 2018.01.13.
 * 动态 适配器
 */
public class MomentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<MomentsBean> mItems;

    public MomentsAdapter(Context context) {
        this.context = context;
        mItems = new ArrayList<>();
        inflater = LayoutInflater.from(context);
    }

    public ArrayList<MomentsBean> getList() {
        return mItems;
    }

    public void setItems(ArrayList<MomentsBean> data) {
        this.mItems.clear();
        this.mItems.addAll(data);
    }

    public void addItems(final ArrayList<MomentsBean> data) {
        mItems.addAll(data);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.list_item_moments, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        ViewHolder recyclerViewHolder = (ViewHolder) holder;
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.anim_recycler_item_show);
        recyclerViewHolder.mView.startAnimation(animation);

        AlphaAnimation aa1 = new AlphaAnimation(1.0f, 0.1f);
        aa1.setDuration(400);
        recyclerViewHolder.imageView.startAnimation(aa1);

        AlphaAnimation aa = new AlphaAnimation(0.1f, 1.0f);
        aa.setDuration(400);
        // 设置数据
        final MomentsBean data = mItems.get(position);
        if (data != null) {
            recyclerViewHolder.name.setText(data.getNickname());
            recyclerViewHolder.date.setText(data.getDate());
            recyclerViewHolder.message.setText(data.getMessage());
            recyclerViewHolder.message.setTextSize(data.getFontSize());
            Glide.with(context).load(data.getAvatarURL()).into(recyclerViewHolder.imageView);
            switch (data.getBgColor()) {
                case 0:
                    recyclerViewHolder.mView.setCardBackgroundColor(context.getResources().getColor(R.color.white));
                    break;
                case 1:
                    recyclerViewHolder.mView.setCardBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDark));
                    break;
                case 2:
                    recyclerViewHolder.mView.setCardBackgroundColor(context.getResources().getColor(R.color.theme1_Primary));
                    break;
                case 3:
                    recyclerViewHolder.mView.setCardBackgroundColor(context.getResources().getColor(R.color.theme2_Primary));
                    break;
                case 4:
                    recyclerViewHolder.mView.setCardBackgroundColor(context.getResources().getColor(R.color.theme3_Primary));
                    break;
                case 5:
                    recyclerViewHolder.mView.setCardBackgroundColor(context.getResources().getColor(R.color.theme4_Primary));
                    break;
                case 6:
                    recyclerViewHolder.mView.setCardBackgroundColor(context.getResources().getColor(R.color.theme5_Primary));
                    break;
            }
            recyclerViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, MomentsDetailActivity.class);
                    intent.putExtra("data", data);
                    context.startActivity(intent);
                }
            });
        }

        recyclerViewHolder.imageView.startAnimation(aa);

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    //普通holder
    private class ViewHolder extends RecyclerView.ViewHolder {
        private CardView mView;
        private CircleImageView imageView;
        private TextView name;
        private TextView date;
        private TextView message;

        private ViewHolder(View itemView) {
            super(itemView);
            mView = itemView.findViewById(R.id.moments_card_view);
            imageView = itemView.findViewById(R.id.moments_image);
            name = itemView.findViewById(R.id.moments_tv_name);
            message = itemView.findViewById(R.id.moments_tv_message);
            date = itemView.findViewById(R.id.moments_tv_date);
        }
    }

}
