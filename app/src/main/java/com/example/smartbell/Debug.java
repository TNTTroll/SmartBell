package com.example.smartbell;

import static com.example.smartbell.MainActivity.BLUETOOTH_CONNECT_PERMISSION_CODE;
import static com.example.smartbell.MainActivity.BLUETOOTH_SCAN_PERMISSION_CODE;
import static com.example.smartbell.MainActivity.REQUEST_ENABLE_BT;
import static com.example.smartbell.MainActivity.TrackerBTConnection;
import static com.example.smartbell.MainActivity.WakerBTConnection;
import static com.example.smartbell.MainActivity.checkPermission;
import static com.example.smartbell.MainActivity.getResId;
import static com.example.smartbell.MainActivity.mBluetoothAdapter;
import static com.example.smartbell.MainActivity.screen;
import static com.example.smartbell.MainActivity.setTimer;
import static com.example.smartbell.MainActivity.toast;
import static com.example.smartbell.MainActivity.userMode;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

public class Debug extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    View view;
    Button d0, d1, d2;
    ImageView tracker, waker;

    public Debug() {
    }

    public static Debug newInstance(String param1, String param2) {
        Debug fragment = new Debug();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_debug, container, false);

        if (userMode)
            toast("DEBUG mode");
        userMode = false;

        checkPermission(android.Manifest.permission.BLUETOOTH_CONNECT, BLUETOOTH_CONNECT_PERMISSION_CODE);
        checkPermission(android.Manifest.permission.BLUETOOTH_SCAN, BLUETOOTH_SCAN_PERMISSION_CODE);

        // Debug '0' Button
        d0 = view.findViewById(R.id.debug0);
        d0.setOnClickListener(v -> SendAlarms.stopAlarm());

        // Debug '1' Button
        d1 = view.findViewById(R.id.debug1);
        d1.setOnClickListener(v -> SendAlarms.preAlarm());

        // Debug '2' Button
        d2 = view.findViewById(R.id.debug2);
        d2.setOnClickListener(v -> SendAlarms.fullAlarm());

        // IMAGES
        tracker = view.findViewById(R.id.connectTracker);
        if (TrackerBTConnection != null)
            tracker.setImageResource(getResId("connect", R.drawable.class));

        waker = view.findViewById(R.id.connectWaker);
        if (WakerBTConnection != null)
            waker.setImageResource(getResId("connect", R.drawable.class));

        return view;
    }
}