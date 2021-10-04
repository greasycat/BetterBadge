package com.sadpooh.gauchobadge;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements OnBadgeCompleteListener {

    private TextView textView = null;
    boolean text = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.textView);
//        new StudentHealthHandler(this).execute(new StudentAccount("1","2"));
    }

    public String getTestHTML()
    {
            return StudentHealthHandler.readFromInputStream(getResources().openRawResource(R.raw.dualauth));
    }


    public void ShowBadge(View view) {
    }


    @Override
    public void onTestShow(String text) {
        textView.setText(text);
    }

    @Override
    public void onBadgeComplete(Bitmap badge) {

    }

    @Override
    public void onBadgeFailure(String error) {

    }
}