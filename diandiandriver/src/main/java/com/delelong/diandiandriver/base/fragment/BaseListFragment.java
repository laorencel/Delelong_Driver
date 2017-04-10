package com.delelong.diandiandriver.base.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.delelong.diandiandriver.R;
import com.delelong.diandiandriver.base.adapter.BaseListAdapter;
import com.delelong.diandiandriver.base.common.utils.SnackbarUtil;
import com.delelong.diandiandriver.base.fragment.ifragment.IBaseListFragment;
import com.delelong.diandiandriver.base.params.BaseParams;
import com.delelong.diandiandriver.base.presenter.BaseListPresenter;
import com.delelong.diandiandriver.base.view.iview.IListView;

import java.util.List;

/**
 * Created by Administrator on 2017/2/25.
 */

public abstract class BaseListFragment extends BaseMvpFragment
        implements IListView, IBaseListFragment {
    public SwipeRefreshLayout baseRefreshLayout;
    private RecyclerView baseRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    public BaseListAdapter adapter;
    private int lastVisibleItem;
    private int[] lastPositions;

    //错误显示View
    private LinearLayout errorLayout;
    private ImageView errorImage;
    private TextView errorText;

    //如果有下一页nextPage大于0
    private int nextPage;

    public BaseListPresenter presenter;

    //需要替换的layoutId
    private int mLayoutId = 0;
    private boolean ifShowError = true;

    public Activity mActivity;
    public Context mContext;

    /**
     * 标志位，标志已经初始化数据完成
     */
    public boolean hasLoad;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (contentView == null) {
            if (mLayoutId != 0) {
                contentView = inflater.inflate(mLayoutId, container, false);
            } else {
                contentView = inflater.inflate(R.layout.base_fragment_list, container, false);
            }
            mActivity = getActivity();
            mContext = mActivity.getApplicationContext();
            presenter = setPresenter();

            addCreateView(contentView);

            //自动刷新
            baseRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue
                    .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                            .getDisplayMetrics()));
        }
        return contentView;

    }

    @Override
    public void addCreateView(View view) {
        initBaseView(view);
    }

    /**
     * 初始化Base View
     */
    private void initBaseView(View view) {
        baseRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.base_refresh_layout);
        baseRecyclerView = (RecyclerView) view.findViewById(R.id.base_recycler_view);

        errorLayout = (LinearLayout) view.findViewById(R.id.base_error_layout);
        errorImage = (ImageView) view.findViewById(R.id.base_error_img);
        errorText = (TextView) view.findViewById(R.id.base_error_text);

        layoutManager = setLayoutManager();
        adapter = setAdapter();
        baseRecyclerView.setLayoutManager(layoutManager);
        baseRecyclerView.setAdapter(adapter);
        baseRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (nextPage > 0 && newState == RecyclerView.SCROLL_STATE_IDLE
                        && lastVisibleItem + 1 == adapter.getItemCount()) {
                    onLoadingNextPage();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (layoutManager instanceof LinearLayoutManager) {
                    lastVisibleItem
                            = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                } else if (layoutManager instanceof GridLayoutManager) {
                    lastVisibleItem = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
                } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                    StaggeredGridLayoutManager staggeredGridLayoutManager
                            = (StaggeredGridLayoutManager) layoutManager;
                    if (lastPositions == null) {
                        lastPositions = new int[staggeredGridLayoutManager.getSpanCount()];
                    }
                    staggeredGridLayoutManager.findLastVisibleItemPositions(lastPositions);
                    lastVisibleItem = findMax(lastPositions);
                }
            }
        });

        baseRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.mainColor));
        baseRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                BaseListFragment.this.onRefresh();
            }
        });
        onFragmentStart();
    }

    @Override
    public void onRefresh() {
        if (presenter != null) {
            presenter.accessServer(setParams());
        }
    }

    @Override
    public void onLoadingNextPage() {
        if (presenter != null) {
//            presenter.loading();
        }
    }

    @Override
    public void onRefreshIndexPage(int index) {
        if (presenter != null) {
//            presenter.refreshIndexPage(index);
        }
    }

    @Override
    public void setData(List data) {
        if (errorLayout.getVisibility() == View.VISIBLE) {
            errorLayout.setVisibility(View.GONE);
        }
        if (adapter != null) {
            if (data.size() == 0) {
                if (adapter.getData().size() > 0) {
                    SnackbarUtil.LongSnackbar(baseRefreshLayout, "没有更多数据啦~").show();
                    return;
                } else {
                    errorText.setText("暂无数据");
                    errorLayout.setVisibility(View.VISIBLE);
                    return;
                }
            }
            adapter.setData(data);
        }
    }

    @Override
    public void reSetContentView(int layoutId) {
        this.mLayoutId = layoutId;
    }

    /**
     * 用于查找int数组中最大的数
     */
    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    @Override
    public void isNextPage(int nextPage) {
        this.nextPage = nextPage;
    }

    @Override
    public void showProgress(boolean show) {
        baseRefreshLayout.setRefreshing(show);
    }

    @Override
    public void showError(boolean show) {
        this.ifShowError = show;
    }

    @Override
    public void showNetworkError() {
        SnackbarUtil.LongSnackbar(baseRefreshLayout, "未连接到网络").show();
    }

    @Override
    public void onFailure(int errorCode, String errorMsg) {
        showProgress(false);
        if (ifShowError) {
            SnackbarUtil.LongSnackbar(baseRefreshLayout, "连接服务器失败").show();
        }
    }

    @Override
    public void responseNoAuth() {
        SnackbarUtil.LongSnackbar(baseRefreshLayout, "未登录").show();
    }

    @Override
    public void responseError(String errorMsg) {
        if (ifShowError) {
            SnackbarUtil.LongSnackbar(baseRefreshLayout, errorMsg).show();
        }
    }

    @Override
    public void responseFailure(String failureMsg) {
        if (ifShowError) {
            SnackbarUtil.LongSnackbar(baseRefreshLayout, failureMsg).show();
        }
    }

    /**
     * 抽象方法，子必须实现setLayoutManager()、setAdapter()、setPresenter()方法
     * setParams()可以返回空
     */
    @NonNull
    public abstract RecyclerView.LayoutManager setLayoutManager();

    public abstract BaseListAdapter setAdapter();

    public abstract BaseListPresenter setPresenter();

    public abstract BaseParams setParams();
}
