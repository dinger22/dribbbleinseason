package com.Yinghao.dingy.dribbleinseason.Bucket;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.Yinghao.dingy.dribbleinseason.R;
import com.Yinghao.dingy.dribbleinseason.views.baseClass.BaseViewHolder;

import butterknife.BindView;

/**
 * Created by shawn on 08/01/17.
 */

public class BucketViewHolder extends BaseViewHolder {
    @BindView(R.id.bucket_layout) View bucketLayout;
    @BindView(R.id.bucket_name) TextView bucketName;
    @BindView(R.id.bucket_shot_count) TextView bucketCount;
    @BindView(R.id.bucket_shot_chosen) ImageView bucketChosen;

    public BucketViewHolder(View itemView) {
        super(itemView);
    }
}
