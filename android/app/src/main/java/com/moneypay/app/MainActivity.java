package com.moneypay.app;

import android.os.Build;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webView);
        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);

        // Configure WebView settings
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);

        // Enable mixed content mode for loading HTTP and HTTPS resources if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // Keep navigation inside the WebView
                view.loadUrl(url);
                return true;
            }
        });

        // Load Default Page
        webView.loadUrl("file:///android_asset/monthly-spend.html");

        // Bottom Navigation logic
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_expenses) {
                webView.loadUrl("file:///android_asset/monthly-spend.html");
                return true;
            } else if (itemId == R.id.nav_payments) {
                webView.loadUrl("file:///android_asset/pay.html/money_pay.html");
                return true;
            } else if (itemId == R.id.nav_delivery) {
                webView.loadUrl("file:///android_asset/delivery.html");
                return true;
            }
            return false;
        });
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
