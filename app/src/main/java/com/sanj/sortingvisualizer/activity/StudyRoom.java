package com.sanj.sortingvisualizer.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.sanj.sortingvisualizer.R;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class StudyRoom extends AppCompatActivity {

    private final String[] links= new String []{
                    "http://geeksquiz.com/insertion-sort/" ,
                    "http://geeksquiz.com/bubble-sort/" ,
                    "http://geeksquiz.com/merge-sort/" ,
                    "http://geeksquiz.com/quick-sort/" ,
                    "http://geeksquiz.com/shellsort/" ,
                    "http://geeksquiz.com/selection-sort/" ,
                    "http://geeksquiz.com/heap-sort/" ,
                    "https://www.geeksforgeeks.org/counting-sort/" ,
                    "https://www.geeksforgeeks.org/bucket-sort-2/" ,
                    "https://www.geeksforgeeks.org/pigeonhole-sort/"
    };

    CharSequence[] mSortingAlgorithms = new CharSequence[]{
                "Insertion Sort Algorithm",
                "Bubble Sort Algorithm",
                "Merge Sort Algorithm",
                "Quick Sort Algorithm",
                "Shell Sort Algorithm",
                "Selection Sort Algorithm",
                "Heap Sort Algorithm",
                "Count Sort Algorithm",
                "Bucket Sort Algorithm",
                "Pigeonhole Sort Algorithm"
    };
    private String url=null;
    WebView webView;
    int INDEX=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_room);
        Bundle bundle=getIntent().getExtras();
        INDEX= bundle != null ? bundle.getInt("index") : 0;
        url=links[INDEX];
        webView=findViewById(R.id.webview);
        webView.setWebViewClient(new MyBrowser());
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onStart() {
        super.onStart();
        isInternetAvailable();
    }

    private static class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return  true;
        }
    }
    
    public void isInternetAvailable() {

        final AlertDialog progress_dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Checking Internet Connection...")
                .setView(R.layout.progress_bar)
                .setCancelable(false);
        progress_dialog = builder.create();
        progress_dialog.show();

        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, Boolean> isInternetAvailable = new AsyncTask<Void, Void, Boolean>() {
            String ERROR_MESSAGE = "";

            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    Socket socket = new Socket();
                    socket.connect(new InetSocketAddress("www.google.com", 80), 3000);
                    return true;
                } catch (IOException e) {
                    ERROR_MESSAGE = e.getLocalizedMessage();
                    System.out.println(e);
                    return false;
                }
            }

            @SuppressLint("SetJavaScriptEnabled")
            @Override
            protected void onPostExecute(Boolean aBoolean) {
                if (aBoolean) {
                    progress_dialog.dismiss();
                    Toast.makeText(StudyRoom.this, "Please wait while loading study room for "+mSortingAlgorithms[INDEX], Toast.LENGTH_LONG).show();
                    webView.getSettings().setLoadsImagesAutomatically(true);
                    webView.getSettings().setJavaScriptEnabled(true);
                    webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
                    webView.loadUrl(url);
                } else {
                    progress_dialog.dismiss();
                    Toast.makeText(StudyRoom.this, "No Internet Connection... Please check your connection and try again", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        };
        isInternetAvailable.execute();
    }
}