package com.example.prabh.notifydemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class webview extends AppCompatActivity {


    /* />*/

    WebView webView;
    String link;
    int count=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        webView=findViewById(R.id.w1);
        link=getIntent().getStringExtra("animelink");
        //WebView webView=new WebView(this);
        //setContentView(webView);
        webView.setWebViewClient(new WebViewClient());//forcing to open in app
        WebSettings webSetting=webView.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webView.loadUrl(link);
    }
    public void onBackPressed()
    {

        if(webView.canGoBack()) {
            webView.goBack();
            count=0;
        }
            else {
            count += 1;
            Toast.makeText(this, "Click on back button again to exit", Toast.LENGTH_SHORT).show();
            if (count > 1)
                System.exit(0);
        }
    }
}
