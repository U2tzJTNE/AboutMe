package com.u2tzjtne.aboutme.ui.view;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.takwolf.android.hfrecyclerview.HeaderAndFooterRecyclerView;
import com.u2tzjtne.aboutme.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


public class LoadMoreFooter implements View.OnClickListener {

    public static final int STATE_DISABLED = 0;
    public static final int STATE_LOADING = 1;
    public static final int STATE_NO_MORE = 2;
    public static final int STATE_ENDLESS = 3;
    public static final int STATE_FAILED = 4;

    private ProgressBar progressBar;
    private TextView tvFailed;
    private View layoutNoMore;

    @IntDef({STATE_DISABLED, STATE_LOADING, STATE_NO_MORE, STATE_ENDLESS, STATE_FAILED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface State {
    }

    public interface OnLoadMoreListener {

        void onLoadMore();

    }

    @State
    private int state = STATE_DISABLED;

    private final OnLoadMoreListener loadMoreListener;

    public LoadMoreFooter(@NonNull Context context, @NonNull HeaderAndFooterRecyclerView recyclerView, @NonNull OnLoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
        View footerView = LayoutInflater.from(context).inflate(R.layout.footer_load_more, recyclerView.getFooterContainer(), false);
        progressBar = footerView.findViewById(R.id.footer_progress);
        tvFailed = footerView.findViewById(R.id.footer_failed);
        layoutNoMore = footerView.findViewById(R.id.footer_no_more);
        tvFailed.setOnClickListener(this);
        recyclerView.addFooterView(footerView);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (!recyclerView.canScrollVertically(1)) {
                    checkLoadMore();
                }
            }

        });
    }

    @State
    public int getState() {
        return state;
    }

    public void setState(@State int state) {
        if (this.state != state) {
            this.state = state;
            switch (state) {
                case STATE_DISABLED:
                    progressBar.setVisibility(View.GONE);
                    tvFailed.setVisibility(View.GONE);
                    layoutNoMore.setVisibility(View.GONE);
                    break;
                case STATE_LOADING:
                    progressBar.setVisibility(View.VISIBLE);
                    tvFailed.setVisibility(View.GONE);
                    layoutNoMore.setVisibility(View.GONE);
                    break;
                case STATE_NO_MORE:
                    progressBar.setVisibility(View.GONE);
                    tvFailed.setVisibility(View.GONE);
                    layoutNoMore.setVisibility(View.VISIBLE);
                    break;
                case STATE_ENDLESS:
                    progressBar.setVisibility(View.GONE);
                    tvFailed.setVisibility(View.GONE);
                    layoutNoMore.setVisibility(View.GONE);
                    break;
                case STATE_FAILED:
                    progressBar.setVisibility(View.GONE);
                    layoutNoMore.setVisibility(View.GONE);
                    tvFailed.setVisibility(View.VISIBLE);
                    tvFailed.setClickable(true);
                    break;
                default:
                    throw new AssertionError("Unknown load more state.");
            }
        }
    }

    private void checkLoadMore() {
        if (getState() == STATE_ENDLESS || getState() == STATE_FAILED) {
            setState(STATE_LOADING);
            loadMoreListener.onLoadMore();
        }
    }

    @Override
    public void onClick(View view) {
        checkLoadMore();
    }

}
