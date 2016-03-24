package com.tsrsscw.feed;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class Detail extends Activity {
	
	TextView title;
	TextView desc;
	TextView startDate;
	TextView endDate;
	TextView gps;
	WebView web;
	Bundle extra;
	
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);
        
        extra = getIntent().getExtras();
        
        title = (TextView) findViewById(R.id.textView1);
        desc = (TextView) findViewById(R.id.textView2);
        startDate = (TextView) findViewById(R.id.textView3);
        endDate = (TextView) findViewById(R.id.textView5);
        gps = (TextView) findViewById(R.id.textView4);
        web = (WebView) findViewById(R.id.webView1);
        web.setWebViewClient(new WebViewClient());
        
        title.setText(extra.getString("title"));
        desc.setText(extra.getString("desc"));
        startDate.setText(extra.getString("sd"));
        endDate.setText(extra.getString("ed"));
        gps.setText(extra.getString("gps"));
        web.loadUrl(extra.getString("link"));
        
	}

}
