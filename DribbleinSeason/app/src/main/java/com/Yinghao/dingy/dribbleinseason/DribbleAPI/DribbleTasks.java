package com.Yinghao.dingy.dribbleinseason.DribbleAPI;

import android.os.AsyncTask;

/**
 * Created by shawn on 04/01/17.
 */

public abstract class DribbleTasks<Params, Progress, Result>
        extends AsyncTask<Params, Progress, Result> {

    private DribbleException exception;

    protected abstract Result doJob(Params... params) throws DribbleException;

    protected abstract void onSuccess(Result result);

    protected abstract void onFailed(DribbleException e);

    @Override
    protected Result doInBackground(Params... params) {
        try {
            return doJob(params);
        } catch (DribbleException e) {
            e.printStackTrace();
            exception = e;
            return null;
        }
    }

    @Override
    protected void onPostExecute(Result result) {
        if (exception != null) {
            onFailed(exception);
        } else {
            onSuccess(result);
        }
    }
}
