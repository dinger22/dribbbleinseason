package com.Yinghao.dingy.dribbleinseason.Bucket;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.Yinghao.dingy.dribbleinseason.ShotList.ShotListFragment;
import com.Yinghao.dingy.dribbleinseason.views.baseClass.SingleFragmentActivity;

/**
 * Created by shawn on 08/01/17.
 */

public class BucketShotListActivity extends SingleFragmentActivity {

    public static final String KEY_BUCKET_NAME = "bucketName";

    @NonNull
    @Override
    protected String getActivityTitle() {
        return getIntent().getStringExtra(KEY_BUCKET_NAME);
    }

    @NonNull
    @Override
    protected Fragment newFragment() {
        String bucketId = getIntent().getStringExtra(ShotListFragment.KEY_BUCKET_ID);
        return bucketId == null
                ? ShotListFragment.newInstance(ShotListFragment.LIST_TYPE_POPULAR)
                : ShotListFragment.newBucketListInstance(bucketId);
    }
}
