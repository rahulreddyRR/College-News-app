package com.rahul.newsdroid;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.airbnb.lottie.LottieAnimationView;

public class ActivityScore extends AppCompatActivity {

    LottieAnimationView islLoading;
    WebView superWebView;
    String webUrl = "https://www.google.co.in/search?q=isl&oq=isl&aqs=chrome.0.69i59j69i60j69i59j69i60l2j69i61.663j0j4&sourceid=chrome&ie=UTF-8#sie=lg;/g/11ggs8nw74;2;/m/0ynr_sq;mt;fp;1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        islLoading = findViewById(R.id.isl_loading);
        superWebView = findViewById(R.id.myWebView);

        superWebView.setBackgroundColor(0x00000000);

        superWebView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            superWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            superWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        superWebView.loadUrl(webUrl);
        superWebView.getSettings().setJavaScriptEnabled(true);
        superWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (!url.contains("google")) {
                    superWebView.stopLoading();
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                islLoading.setVisibility(View.GONE);
                superWebView.setVisibility(View.VISIBLE);
            }
        });

        //superWebView.setWebChromeClient(new WebChromeClient());

        superWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                Uri myUri = Uri.parse(url);
                Intent superIntent = new Intent(Intent.ACTION_VIEW);
                superIntent.setData(myUri);
                startActivity(superIntent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
