package com.example.smartbell;

import static com.example.smartbell.MainActivity.BLUETOOTH_CONNECT_PERMISSION_CODE;
import static com.example.smartbell.MainActivity.checkPermission;
import static com.example.smartbell.MainActivity.toast;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class Plate extends Fragment implements View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static BluetoothDevice innerInfo;

    private String mParam1;
    private String mParam2;

    View view;
    BluetoothDevice info;

    public Plate(BluetoothDevice _info) {
        info = _info;
    }

    public static Plate newInstance(String param1, String param2) {
        Plate fragment = new Plate(innerInfo);
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

    public void onClick(View v) {
        checkPermission(android.Manifest.permission.BLUETOOTH_CONNECT, BLUETOOTH_CONNECT_PERMISSION_CODE);

        toast("Connect to " + info.getName());

        if (info.getName().equals("BT_Tracker")) {
            if (MainActivity.BTTracker != null)
                MainActivity.BTTracker.cancel();
            if (MainActivity.TrackerBTConnection != null)
                MainActivity.TrackerBTConnection.interrupt();

            MainActivity.BTTracker = new Threading.ThreadConnectBT(info, 0);
            MainActivity.BTTracker.start();

        } else if (info.getName().equals("BT_Waker")) {
            if (MainActivity.BTWaker != null)
                MainActivity.BTWaker.cancel();
            if (MainActivity.WakerBTConnection != null)
                MainActivity.WakerBTConnection.interrupt();

            MainActivity.BTWaker = new Threading.ThreadConnectBT(info, 1);
            MainActivity.BTWaker.start();
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_plate, container, false);

        TextView name = view.findViewById(R.id.plateName);
        name.setText(info.getName());

        Button btn = view.findViewById(R.id.plateButton);
        btn.setOnClickListener(this);

        Button bg = view.findViewById(R.id.plateBG);
        bg.setEnabled(false);

        return view;
    }
}