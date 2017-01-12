package com.Yinghao.dingy.dribbleinseason.views;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.Yinghao.dingy.dribbleinseason.DribbleAPI.DribbleUtils;
import com.Yinghao.dingy.dribbleinseason.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AuthWebActivity extends AppCompatActivity {

    @BindView(R.id.webview)
    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_web);
        ButterKnife.bind(this);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith(DribbleUtils.REDIRECT_URI)) {
                    Uri uri = Uri.parse(url);
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("code", uri.getQueryParameter("code"));
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }

                return super.shouldOverrideUrlLoading(view, url);
            }
        });

//        Activity acti = AuthWebActivity.this;
//        Context context = acti.getBaseContext();
//        CookieSyncManager.createInstance(context);
//        CookieManager.getInstance().removeAllCookie();
        String url = getIntent().getStringExtra("url");
        webView.loadUrl(url);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
