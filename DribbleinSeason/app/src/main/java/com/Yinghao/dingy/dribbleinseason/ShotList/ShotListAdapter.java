package com.Yinghao.dingy.dribbleinseason.ShotList;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.Yinghao.dingy.dribbleinseason.Model.ModelUtils;
import com.Yinghao.dingy.dribbleinseason.Shot.ShotActivity;
import com.Yinghao.dingy.dribbleinseason.Shot.ShotFragment;
import com.Yinghao.dingy.dribbleinseason.views.baseClass.SingleFragmentActivity;
import com.Yinghao.dingy.dribbleinseason.views.baseClass.BaseViewHolder;
import com.Yinghao.dingy.dribbleinseason.views.viewsUtils.ImageUtils;
import com.Yinghao.dingy.dribbleinseason.views.baseClass.InfiniteAdapter;
import com.Yinghao.dingy.dribbleinseason.R;
import com.Yinghao.dingy.dribbleinseason.Model.Shot;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by shawn on 30/12/16.
 */
class ShotListAdapter extends InfiniteAdapter<Shot> {

    private final ShotListFragment shotListFragment;

    public ShotListAdapter(@NonNull ShotListFragment shotListFragment,
                           @NonNull List<Shot> data,
                           @NonNull LoadMoreListener loadMoreListener) {
        super(shotListFragment.getContext(), data, loadMoreListener);
        this.shotListFragment = shotListFragment;
    }

    @Override
    protected BaseViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.list_item_shot, parent, false);

        return new ShotViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(BaseViewHolder viewHolder, int position){
        ShotViewHolder shotViewHolder = (ShotViewHolder) viewHolder;

        final Shot shot = getData().get(position);
        shotViewHolder.cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ShotActivity.class);
                intent.putExtra(ShotFragment.KEY_SHOT,
                        ModelUtils.toString(shot, new TypeToken<Shot>(){}));
                intent.putExtra(SingleFragmentActivity.KEY_SHOT_TITLE, shot.title);
                shotListFragment.startActivityForResult(intent, ShotListFragment.REQ_CODE_SHOT);
            }
        });

        Drawable likeDrawable = shot.liked
                ? ContextCompat.getDrawable(getContext(), R.drawable.ic_favorite_black_24dp)
                : ContextCompat.getDrawable(getContext(), R.drawable.ic_favorite_border_black_24dp);

        shotViewHolder.likeCount.setText(String.valueOf(shot.likes_count));
        shotViewHolder.likeCount.setCompoundDrawablesWithIntrinsicBounds(likeDrawable,null,null,null);
        shotViewHolder.bucketCount.setText(String.valueOf(shot.buckets_count));
        shotViewHolder.viewCount.setText(String.valueOf(shot.views_count));
        ImageUtils.loadShotImage(shot, shotViewHolder.image);
    }


//
}
