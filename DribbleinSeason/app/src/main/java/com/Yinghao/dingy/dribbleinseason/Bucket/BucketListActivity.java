package com.Yinghao.dingy.dribbleinseason.Bucket;


import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.Yinghao.dingy.dribbleinseason.R;
import com.Yinghao.dingy.dribbleinseason.views.baseClass.SingleFragmentActivity;


import java.util.ArrayList;

/**
 * Created by shawn on 08/01/17.
 */

public class BucketListActivity extends SingleFragmentActivity {

    @NonNull
    @Override
    protected Fragment newFragment() {
        boolean isChoosingMode = getIntent().getExtras().getBoolean(
                BucketListFragment.KEY_CHOOSING_MODE);

        ArrayList<String> chosenBucketIds = getIntent().getExtras().getStringArrayList(
                BucketListFragment.KEY_COLLECTED_BUCKET_IDS);

        return BucketListFragment.newInstance(null, isChoosingMode, chosenBucketIds);
    }

    @NonNull
    @Override
    protected String getActivityTitle() {
        return getString(R.string.choose_bucket);
    }
}
