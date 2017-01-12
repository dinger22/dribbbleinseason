package com.Yinghao.dingy.dribbleinseason.ShotList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.Yinghao.dingy.dribbleinseason.DribbleAPI.DribbleException;
import com.Yinghao.dingy.dribbleinseason.DribbleAPI.DribbleTasks;
import com.Yinghao.dingy.dribbleinseason.DribbleAPI.DribbleUtils;

import com.Yinghao.dingy.dribbleinseason.Model.ModelUtils;
import com.Yinghao.dingy.dribbleinseason.Model.Shot;
import com.Yinghao.dingy.dribbleinseason.R;
import com.Yinghao.dingy.dribbleinseason.Shot.ShotFragment;
import com.Yinghao.dingy.dribbleinseason.views.baseClass.InfiniteAdapter;
import com.Yinghao.dingy.dribbleinseason.views.viewsUtils.SpaceItemDecoration;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.Yinghao.dingy.dribbleinseason.DribbleAPI.DribbleUtils.isLikingShot;

/**
 * Created by shawn on 30/12/16.
 */

public class ShotListFragment extends Fragment {

    public static final int REQ_CODE_SHOT = 100;
    public static final String KEY_LIST_TYPE = "listType";
    public static final String KEY_BUCKET_ID = "bucketId";

    public static final int LIST_TYPE_POPULAR = 1;
    public static final int LIST_TYPE_LIKED = 2;
    public static final int LIST_TYPE_BUCKET = 3;

    private ShotListAdapter adapter;
    private int listType;


    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.swipe_refresh_container) SwipeRefreshLayout swipeRefreshLayout;

    private InfiniteAdapter.LoadMoreListener onLoadMore = new InfiniteAdapter.LoadMoreListener() {
        @Override
        public void onLoadMore() {
            if (DribbleUtils.isLoggedin()) {
                AsyncTaskCompat.executeParallel(new LoadShotTask(false));
            }
        }
    };

    public static ShotListFragment newBucketListInstance(@NonNull String bucketId) {
        Bundle args = new Bundle();
        args.putInt(KEY_LIST_TYPE, LIST_TYPE_BUCKET);
        args.putString(KEY_BUCKET_ID, bucketId);

        ShotListFragment fragment =new ShotListFragment();
        fragment.setArguments(args);
        return fragment;
    }


    public static ShotListFragment newInstance(int listType) {
        Bundle args = new Bundle();
        args.putInt(KEY_LIST_TYPE, listType);

        ShotListFragment fragment =new ShotListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE_SHOT && resultCode == Activity.RESULT_OK) {
            Shot updatedShot = ModelUtils.toObject(data.getStringExtra(ShotFragment.KEY_SHOT),
                    new TypeToken<Shot>(){});
            for (Shot shot : adapter.getData()) {
                if (TextUtils.equals(shot.id, updatedShot.id)) {
                    shot.likes_count = updatedShot.likes_count;
                    shot.buckets_count = updatedShot.buckets_count;
                    shot.liked=updatedShot.liked;
                    adapter.notifyDataSetChanged();
                    return;
                }
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_swipe_recycler_view, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        listType = getArguments().getInt(KEY_LIST_TYPE);


        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                AsyncTaskCompat.executeParallel(new LoadShotTask(true));
                //AsyncTaskCompat.executeParallel(new isLikingShortList());
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new SpaceItemDecoration(
                getResources().getDimensionPixelSize(R.dimen.padding_space_m)));
        adapter = new ShotListAdapter(this, new ArrayList<Shot>(), onLoadMore);
        recyclerView.setAdapter(adapter);
    }

//    private class isLikingShortList extends DribbleTasks<Void,Void,List<Shot>>{
//        List<Shot> shortList;
//        boolean refresh;
//
//        public isLikingShortList(List<Shot> shortList, boolean fresh) {
//            this.shortList = shortList;
//            this.refresh = fresh;
//        }
//
//        @Override
//        protected  List<Shot> doJob(Void... params) throws DribbleException {
//            for (Shot shot : shortList){
//                shot.liked = isLikingShot(shot.id);
//            }
//            return shortList;
//        }
//
//        @Override
//        protected void onSuccess(List<Shot> shots) {
//
//        }
//
//        @Override
//        protected void onFailed(DribbleException e) {
//            Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
//        }
//    }

    private class LoadShotTask extends DribbleTasks<Void, Void, List<Shot>> {

        boolean refresh;

        public LoadShotTask(boolean fresh) {
            this.refresh = fresh;
        }

        @Override
        protected List<Shot> doJob(Void... params) throws DribbleException {
            int page = refresh ? 1 : adapter.getData().size() / DribbleUtils.SHOTS_PER_PAGE + 1;
            List<Shot> shortList;
            switch (listType) {
                case LIST_TYPE_POPULAR:

                    shortList = DribbleUtils.getShots(page);
                    for (Shot shot : shortList){
                        shot.liked = isLikingShot(shot.id);
                    }
                    return shortList;
                case LIST_TYPE_LIKED:
                    shortList = DribbleUtils.getLikedShots(page);
                    for (Shot shot : shortList){
                        shot.liked = true;
                    }
                    return shortList;
                case LIST_TYPE_BUCKET:
                    String bucketId = getArguments().getString(KEY_BUCKET_ID);
                    shortList = DribbleUtils.getBucketShots(bucketId, page);
                    for (Shot shot : shortList){
                        shot.liked = isLikingShot(shot.id);
                    }
                    return shortList;
                default:
                    return DribbleUtils.getShots(page);
            }
        }

        @Override
        protected void onSuccess(List<Shot> shots) {
            adapter.setShowLoading(shots.size() >= DribbleUtils.SHOTS_PER_PAGE);
            if (refresh) {
                swipeRefreshLayout.setRefreshing(false);
                adapter.setData(shots);
            } else {
                swipeRefreshLayout.setEnabled(true);

                adapter.append(shots);
            }
//            AsyncTaskCompat.executeParallel(new isLikingShortList(shots,refresh));
        }

        @Override
        protected void onFailed(DribbleException e) {
            Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
        }

    }
}
