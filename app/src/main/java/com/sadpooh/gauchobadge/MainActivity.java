package com.sadpooh.gauchobadge;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements IActivityOperationListener {

    private TextView textView = null;
    boolean text = false;

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(html);
        }
    }

    public void ShowBadge(View view) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
//        new StudentServiceHandler(this).execute(new StudentAccount("",""));
        StudentServiceRunnable studentServiceRunnable = new StudentServiceRunnable();
        studentServiceRunnable.initRunnable(this);
        new Thread(studentServiceRunnable).start();
    }


    @Override
    public void onBadgeComplete(Bitmap badge) {

    }

    @Override
    public void onBadgeFailure(String error) {

    }

    @Override
    public void onTestShow(String text) {
        textView.setText(text);
//        setHTML(text);
    }

    void setHTML(String html) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textView.setText(Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT));
        } else {
            textView.setText(Html.fromHtml(html));
        }
    }
}