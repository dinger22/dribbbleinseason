package com.Yinghao.dingy.dribbleinseason.ShotList;

import android.view.View;
import android.widget.TextView;

import com.Yinghao.dingy.dribbleinseason.R;
import com.Yinghao.dingy.dribbleinseason.views.baseClass.BaseViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.BindView;

/**
 * Created by shawn on 02/01/17.
 */

public class ShotViewHolder extends BaseViewHolder {

    @BindView(R.id.shot_clickable_cover) View cover;
    @BindView(R.id.shot_like_count) TextView likeCount;
    @BindView(R.id.shot_bucket_count) TextView bucketCount;
    @BindView(R.id.shot_view_count) TextView viewCount;
    @BindView(R.id.shot_image) SimpleDraweeView image;

    public ShotViewHolder(View view){
        super(view);
    }
}
