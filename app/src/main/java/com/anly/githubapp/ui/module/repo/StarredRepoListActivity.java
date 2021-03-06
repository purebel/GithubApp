package com.anly.githubapp.ui.module.repo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.anly.githubapp.GithubApplication;
import com.anly.githubapp.R;
import com.anly.githubapp.common.wrapper.AppLog;
import com.anly.githubapp.data.model.Repo;
import com.anly.githubapp.di.HasComponent;
import com.anly.githubapp.di.component.DaggerRepoComponent;
import com.anly.githubapp.di.component.RepoComponent;
import com.anly.githubapp.di.module.ActivityModule;
import com.anly.githubapp.di.module.RepoModule;
import com.anly.githubapp.presenter.repo.StarredRepoListPresenter;
import com.anly.githubapp.ui.base.BaseLoadingActivity;
import com.anly.githubapp.ui.module.repo.adapter.RepoListRecyclerAdapter;
import com.anly.mvp.lce.LceView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mingjun on 16/8/10.
 */
public class StarredRepoListActivity extends BaseLoadingActivity implements LceView<ArrayList<Repo>>, HasComponent<RepoComponent> {


    @BindView(R.id.repo_list)
    RecyclerView mRepoListView;

    @Inject
    StarredRepoListPresenter mPresenter;

    private RepoListRecyclerAdapter mAdapter;

    public static void launch(Context context) {
        context.startActivity(new Intent(context, StarredRepoListActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getComponent().inject(this);
        setContentView(R.layout.activity_repo_list);
        ButterKnife.bind(this);

        initViews();

        setTitle(R.string.your_stars);

        mPresenter.attachView(this);
        mPresenter.loadMyStarredRepos();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    private void initViews() {
        mAdapter = new RepoListRecyclerAdapter(null);
        mAdapter.setOnRecyclerViewItemClickListener(mItemClickListener);

        mRepoListView.setLayoutManager(new LinearLayoutManager(this));
        mRepoListView.addItemDecoration(new HorizontalDividerItemDecoration
                .Builder(this)
                .color(Color.TRANSPARENT)
                .size(getResources().getDimensionPixelSize(R.dimen.divider_height))
                .build());

        mRepoListView.setAdapter(mAdapter);
    }

    private BaseQuickAdapter.OnRecyclerViewItemClickListener mItemClickListener = new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            Repo repo = mAdapter.getItem(position);
            RepoDetailActivity.launch(StarredRepoListActivity.this, repo.getOwner().getLogin(), repo.getName());
        }
    };

    @Override
    public String getLoadingMessage() {
        return null;
    }

    @Override
    public void showContent(ArrayList<Repo> data) {
        AppLog.d("data:" + data.size());
        mAdapter.setNewData(data);
    }

    @Override
    public void showError(Throwable e) {
        AppLog.e(e);
    }

    @Override
    public void showEmpty() {

    }

    @Override
    public RepoComponent getComponent() {
        return DaggerRepoComponent.builder()
                .applicationComponent(GithubApplication.get(this).getComponent())
                .activityModule(new ActivityModule(this))
                .repoModule(new RepoModule())
                .build();
    }
}
