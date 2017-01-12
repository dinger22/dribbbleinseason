package com.Yinghao.dingy.dribbleinseason.Shot;

import android.support.v4.app.Fragment;
import android.support.annotation.NonNull;

import com.Yinghao.dingy.dribbleinseason.views.baseClass.SingleFragmentActivity;

/**
 * Created by shawn on 04/01/17.
 */

public class ShotActivity extends SingleFragmentActivity{

    public static final String KEY_SHOT_TITLE = "shot_title";

    @NonNull
    @Override
    protected Fragment newFragment() {
        return ShotFragment.newInstance(getIntent().getExtras());
    }

    @NonNull
    @Override
    protected String getActivityTitle() {
        return getIntent().getStringExtra(KEY_SHOT_TITLE);
    }

}
