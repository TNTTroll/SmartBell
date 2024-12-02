package com.example.smartbell;

import static android.app.Activity.RESULT_OK;
import static com.example.smartbell.MainActivity.BLUETOOTH_CONNECT_PERMISSION_CODE;
import static com.example.smartbell.MainActivity.BLUETOOTH_SCAN_PERMISSION_CODE;
import static com.example.smartbell.MainActivity.REQUEST_ENABLE_BT;
import static com.example.smartbell.MainActivity.TrackerBTConnection;
import static com.example.smartbell.MainActivity.WakerBTConnection;
import static com.example.smartbell.MainActivity.checkPermission;
import static com.example.smartbell.MainActivity.getResId;
import static com.example.smartbell.MainActivity.image;
import static com.example.smartbell.MainActivity.mBluetoothAdapter;
import static com.example.smartbell.MainActivity.screen;
import static com.example.smartbell.MainActivity.setTimer;
import static com.example.smartbell.MainActivity.toast;
import static com.example.smartbell.MainActivity.userMode;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;


public class Alarm extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    View view;
    Button on, search, timer;
    ImageView tracker, waker;

    public Alarm() {
    }

    public static Alarm newInstance(String param1, String param2) {
        Alarm fragment = new Alarm();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_alarm, container, false);

        if (!userMode)
            toast("USER mode");
        userMode = true;

        checkPermission(android.Manifest.permission.BLUETOOTH_CONNECT, BLUETOOTH_CONNECT_PERMISSION_CODE);
        checkPermission(android.Manifest.permission.BLUETOOTH_SCAN, BLUETOOTH_SCAN_PERMISSION_CODE);

        // ON Button
        on = view.findViewById(R.id.alarmOn);
        on.setOnClickListener(v -> {
            if (!mBluetoothAdapter.isEnabled()) {
                toast("Turning Bluetooth on...");

                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, REQUEST_ENABLE_BT);
            } else
            toast("Bluetooth is already ON");
        });

        // SEARCH Button
        search = view.findViewById(R.id.alarmSearch);
        search.setOnClickListener(v ->
                getParentFragmentManager().beginTransaction().replace(screen.getId(), new Search()).commit());

        // TIMER Button
        timer = view.findViewById(R.id.alarmTimer);
        timer.setOnClickListener(v -> setTimer());

        // IMAGES
        tracker = view.findViewById(R.id.connectTracker);
        if (TrackerBTConnection != null)
            tracker.setImageResource(getResId("connect", R.drawable.class));

        waker = view.findViewById(R.id.connectWaker);
        if (WakerBTConnection != null)
            waker.setImageResource(getResId("connect", R.drawable.class));

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch(requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK) {
                    image.setImageResource(R.drawable.on);
                    toast("Bluetooth is ON");
                } else
                    toast("Could not turn on Bluetooth");
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}