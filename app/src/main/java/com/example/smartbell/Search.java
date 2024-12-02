package com.example.smartbell;

import static com.example.smartbell.MainActivity.BLUETOOTH_CONNECT_PERMISSION_CODE;
import static com.example.smartbell.MainActivity.BLUETOOTH_SCAN_PERMISSION_CODE;
import static com.example.smartbell.MainActivity.checkPermission;
import static com.example.smartbell.MainActivity.mBluetoothAdapter;
import static com.example.smartbell.MainActivity.screen;
import static com.example.smartbell.MainActivity.toast;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.Set;

public class Search extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    View view;

    public Search() {
    }

    public static Search newInstance(String param1, String param2) {
        Search fragment = new Search();
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
        view = inflater.inflate(R.layout.fragment_search, container, false);

        Button exit = view.findViewById(R.id.searchExit);
        exit.setOnClickListener(v ->
                getParentFragmentManager().beginTransaction().replace(screen.getId(), new Alarm()).commit());

        if (mBluetoothAdapter.isEnabled()) {
            checkPermission(android.Manifest.permission.BLUETOOTH_CONNECT, BLUETOOTH_CONNECT_PERMISSION_CODE);
            checkPermission(android.Manifest.permission.BLUETOOTH_SCAN, BLUETOOTH_SCAN_PERMISSION_CODE);

            Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();

            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            for (BluetoothDevice device : devices)
                if (device.getName().equals("BT_Tracker") || device.getName().equals("BT_Waker"))
                    transaction.add(R.id.searchList, new Plate( device ));

            transaction.addToBackStack(null);
            transaction.commit();

        } else
            toast("Turn Bluetooth for this function");

        return view;
    }
}