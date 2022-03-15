package com.example.test_01;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import java.net.URLEncoder;

public class viewPDF extends AppCompatActivity {

    WebView pdfViewer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pdf);

        pdfViewer = (WebView) findViewById(R.id.PDFViews);

        Intent intent = getIntent();
        String FileUrl = intent.getStringExtra("fileUrl");

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Opening...");

        pdfViewer.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressDialog.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressDialog.dismiss();
            }
        });
        String url = "";
        try{
            url = URLEncoder.encode(FileUrl,"UTF-8");
        }catch(Exception e){

        }
        pdfViewer.getSettings().setJavaScriptEnabled(true);
        pdfViewer.getSettings().setPluginState(WebSettings.PluginState.ON);
        pdfViewer.loadUrl("http://docs.google.com/gview?embedded=true&url=" + url);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}