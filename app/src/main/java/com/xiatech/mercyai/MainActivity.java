package com.xiatech.mercyai;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView message = new TextView(this);
        message.setText("Mercy AI\n\nYour app is ready to begin.");
        message.setTextSize(22);
        message.setGravity(Gravity.CENTER);
        message.setPadding(24, 24, 24, 24);

        setContentView(message);
    }
}
