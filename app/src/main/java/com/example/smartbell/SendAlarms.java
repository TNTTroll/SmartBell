package com.example.smartbell;

import static com.example.smartbell.MainActivity.TrackerBTConnection;
import static com.example.smartbell.MainActivity.WakerBTConnection;

import android.util.Log;

public class SendAlarms {
    public static void stopAlarm() {
        Log.e("BT", "Turning OFF");

        if (WakerBTConnection != null) {
            byte[] bytesToSend = "0".getBytes();
            WakerBTConnection.write(bytesToSend);
        }
    }

    public static void preAlarm() {
        Log.e("BT", "Turning PRE");

        if (WakerBTConnection != null) {
            byte[] bytesToSend = "1".getBytes();
            WakerBTConnection.write(bytesToSend);
        }
    }

    public static void fullAlarm() {
        Log.e("BT", "Turning FULL");

        if (WakerBTConnection != null) {
            byte[] bytesToSend = "2".getBytes();
            WakerBTConnection.write(bytesToSend);
        }
    }

    public static void setAlarm() {
        Log.e("BT", "Set a data");

        if (TrackerBTConnection != null) {
            byte[] bytesToSend = "3".getBytes();
            TrackerBTConnection.write(bytesToSend);
        } else if (WakerBTConnection != null) {
            byte[] bytesToSend = "3".getBytes();
            WakerBTConnection.write(bytesToSend);
        }
    }

}
