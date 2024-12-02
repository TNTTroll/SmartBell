package com.example.smartbell;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    // Variables
    public static Context context;

    public static ImageView image;
    public static BluetoothAdapter mBluetoothAdapter;

    Button alarm, analyze;
    public static View screen;

    public static Dialog clock;
    public static CountDownTimer timer = null;

    public static int[] timeSleep = new int[2];
    public static int[] timeWoke = new int[2];
    public static int sleepDuration;

    // Bluetooth
    public static final int REQUEST_ENABLE_BT = 0;
    public static final int BLUETOOTH_CONNECT_PERMISSION_CODE = 100;
    public static final int BLUETOOTH_SCAN_PERMISSION_CODE = 101;

    public static Threading.ThreadConnectBT BTTracker = null;
    public static Threading.ThreadConnected TrackerBTConnection = null;
    public static ArrayList<Integer> movements = new ArrayList<>();

    public static Threading.ThreadConnectBT BTWaker = null;
    public static Threading.ThreadConnected WakerBTConnection = null;
    public static boolean sleep = false;

    // Slide menu
    static final int MIN_DISTANCE = 400;
    public static boolean userMode = true;
    private float x1;


    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        clock = new Dialog(this);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        screen = (View) findViewById(R.id.menuScreen);
        image = (ImageView) findViewById(R.id.image);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Check if Bluetooth is on
        if (mBluetoothAdapter.isEnabled())
            image.setImageResource(R.drawable.on);
        else
            image.setImageResource(R.drawable.off);

        alarm = (Button) findViewById(R.id.menuAlarm);
        alarm.setOnClickListener(v ->
                getSupportFragmentManager().beginTransaction().replace(screen.getId(), new Alarm()).commit());

        analyze = (Button) findViewById(R.id.menuAnalyze);
        analyze.setOnClickListener(v ->
                getSupportFragmentManager().beginTransaction().replace(screen.getId(), new Analyze()).commit());

        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) screen.getLayoutParams();
        layoutParams.height = (int)(displayMetrics.heightPixels*.75);
        screen.setLayoutParams(layoutParams);
    }


    // Slide menu
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                break;

            case MotionEvent.ACTION_UP:
                float x2 = event.getX();
                float deltaX = x2 - x1;

                if ( Math.abs(deltaX) > MIN_DISTANCE )
                    if (x1 > x2 && !userMode)
                        getSupportFragmentManager().beginTransaction().replace(screen.getId(), new Alarm()).commit();
                    else if (x1 < x2 && userMode)
                        getSupportFragmentManager().beginTransaction().replace(screen.getId(), new Debug()).commit();
                break;
        }

        return super.onTouchEvent(event);
    }


    // Timer
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void setTimer() {
        clock.setContentView(R.layout.fragment_clock);

        Button exit = (Button) clock.findViewById(R.id.clockClose);
        exit.setOnClickListener(v -> clock.dismiss());

        Button done = (Button) clock.findViewById(R.id.clockDone);
        done.setOnClickListener(v -> {
            movements = new ArrayList<>();
            sleep = true;
            Graph.dots = null;

            TimePicker timePicker = (TimePicker) clock.findViewById(R.id.clockTimePicker);
            timeWoke[0] = timePicker.getHour();
            timeWoke[1] = timePicker.getMinute();

            Calendar calendar = Calendar.getInstance();
            timeSleep[0] = calendar.get(Calendar.HOUR_OF_DAY);
            timeSleep[1] = calendar.get(Calendar.MINUTE);

            sleepDuration = ((Math.abs(timeWoke[0] - timeSleep[0]) * 60) + Math.abs(timeWoke[1] - timeSleep[1])) * 60;

            if (timer != null)
                timer.cancel();
            timer = null;
            timer = new CountDownTimer(sleepDuration*1000, 1000) {
                private boolean preStarted = false;

                public void onTick(long millisUntilFinished) {
                    if ((int)(millisUntilFinished/1000) < 60 && !preStarted) {
                        SendAlarms.preAlarm();
                        preStarted = true;
                    }
                }

                public void onFinish() {
                    goodMorning();
                }
            }.start();

            clock.dismiss();
        });

        clock.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        clock.show();
    }


    // Alarm
    private static void goodMorning() {
        clock.setContentView(R.layout.fragment_morning);
        sleep = false;

        SendAlarms.fullAlarm();

        Button stop = clock.findViewById(R.id.morningStop);
        stop.setOnClickListener(v -> {
            SendAlarms.stopAlarm();

            clock.dismiss();
        });

        clock.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        clock.show();
    }


    // Bluetooth functions
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                image.setImageResource(R.drawable.on);
                toast("Bluetooth is ON");
            } else
                toast("Could not turn on Bluetooth");
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public static void checkPermission(String permission, int requestCode)
    {
        if (ContextCompat.checkSelfPermission(MainActivity.context, permission) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions((Activity) MainActivity.context, new String[]{permission}, requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    // Helpers
    public static void toast(String text) {
        Toast.makeText(MainActivity.context, text, Toast.LENGTH_SHORT).show();
    }

    public static int getResId(String resName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception ignored) {
            return -1;
        }
    }
}


/* LINKS
    Arduino + Phone: https://www.youtube.com/watch?v=d8qQ3d_g9YY
    Bluetooth search: https://rutube.ru/video/e4cbefc54557fb8668f92ce90cf70cfc/
    Bluetooth connection: https://istarik.ru/blog/android/50.html

    Canvas: https://www.geeksforgeeks.org/how-to-use-canvas-api-in-android-apps/
*/