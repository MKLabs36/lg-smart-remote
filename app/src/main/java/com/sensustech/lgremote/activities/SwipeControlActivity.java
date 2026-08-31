package com.sensustech.lgremote.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.connectsdk.device.ConnectableDevice;
import com.sensustech.lgremote.R;
import com.sensustech.lgremote.SingletonTV;

public class SwipeControlActivity extends AppCompatActivity {

    private TextView tv_swipe_check;
    private ConnectableDevice mTV;

    private float lastX;
    private float lastY;
    private boolean moved;

    private static final float POINTER_SPEED = 0.5f;
    private static final float TAP_DISTANCE = 20f;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_control);

        View touchpad = findViewById(R.id.constraint);
        tv_swipe_check = findViewById(R.id.textGesture);

        SingletonTV tv = SingletonTV.getInstance();
        mTV = tv.getTV();

        touchpad.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (mTV == null) {
                    mTV = SingletonTV.getInstance().getTV();
                }

                switch (event.getActionMasked()) {

                    case MotionEvent.ACTION_DOWN:
                        lastX = event.getX();
                        lastY = event.getY();
                        moved = false;
                        return true;

                    case MotionEvent.ACTION_MOVE:

                        if (event.getPointerCount() != 1) {
                            return true;
                        }

                        float currentX = event.getX();
                        float currentY = event.getY();

                        float dx = currentX - lastX;
                        float dy = currentY - lastY;

                        if (Math.abs(dx) > 0 || Math.abs(dy) > 0) {
                            moved = true;

                            if (mTV != null) {
                                mTV.getMouseControl().move(
                                        dx * POINTER_SPEED,
                                        dy * POINTER_SPEED
                                );
                            }

                            lastX = currentX;
                            lastY = currentY;
                        }

                        return true;

                    case MotionEvent.ACTION_UP:

                        float totalX = event.getX() - lastX;
                        float totalY = event.getY() - lastY;

                        if (!moved ||
                                (Math.abs(totalX) < TAP_DISTANCE &&
                                 Math.abs(totalY) < TAP_DISTANCE)) {

                            if (mTV != null) {
                                mTV.getMouseControl().click();
                                tv_swipe_check.setText("OK");
                            } else {
                                Toast.makeText(
                                        getApplicationContext(),
                                        "Device is not connected",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }

                        return true;

                    case MotionEvent.ACTION_CANCEL:
                        return true;
                }

                return true;
            }
        });
    }

    public void backClick(View view) {
        finish();
    }

    public void closeClick(View view) {
        finish();
    }

    public void helpClick(View view) {
        Intent intent = new Intent(
                SwipeControlActivity.this,
                GesturesActivity.class
        );
        startActivity(intent);
    }
}
