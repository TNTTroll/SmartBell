package com.example.smartbell;

import static com.example.smartbell.MainActivity.BLUETOOTH_CONNECT_PERMISSION_CODE;
import static com.example.smartbell.MainActivity.TrackerBTConnection;
import static com.example.smartbell.MainActivity.WakerBTConnection;
import static com.example.smartbell.Analyze.checkForMovement;
import static com.example.smartbell.MainActivity.checkPermission;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class Threading {
    public static UUID thisUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public static class ThreadConnectBT extends Thread {
        private BluetoothSocket bluetoothSocket = null;
        private int name;

        public ThreadConnectBT(BluetoothDevice device, int _name) {
            try {
                checkPermission(android.Manifest.permission.BLUETOOTH_CONNECT, BLUETOOTH_CONNECT_PERMISSION_CODE);
                bluetoothSocket = device.createRfcommSocketToServiceRecord(thisUUID);
                name = _name;
            } catch (IOException ignored) {}
        }

        @Override
        public void run() {
            boolean success = false;

            try {
                checkPermission(android.Manifest.permission.BLUETOOTH_CONNECT, BLUETOOTH_CONNECT_PERMISSION_CODE);
                bluetoothSocket.connect();
                success = true;
            }
            catch (IOException ignored0) {
                try {
                    bluetoothSocket.close();
                } catch (IOException ignored1) {}
            }

            if (success) {
                if (name == 0) {
                    TrackerBTConnection = new ThreadConnected(bluetoothSocket, name);
                    TrackerBTConnection.start();
                } else {
                    WakerBTConnection = new ThreadConnected(bluetoothSocket, name);
                    WakerBTConnection.start();
                }

                SendAlarms.setAlarm();
            }
        }

        public void cancel() {
            try {
                bluetoothSocket.close();
            } catch (IOException ignored) {}
        }
    }

    public static class ThreadConnected extends Thread {
        private InputStream connectedInputStream;
        private final OutputStream connectedOutputStream;
        private BluetoothSocket socket = null;
        private int name;

        public ThreadConnected(BluetoothSocket _socket, int _name) {
            name = _name;
            socket = _socket;

            InputStream in = null;
            OutputStream out = null;

            try {
                in = socket.getInputStream();
                out = socket.getOutputStream();
            } catch (IOException ignored) {}

            connectedInputStream = in;
            connectedOutputStream = out;
        }

        @Override
        public void run() {
            if (socket != null && name == 0) {
                byte[] buffer = new byte[5];
                int bytes;

                StringBuilder fullMsg = new StringBuilder();
                int[] gyros = new int[3];

                while (true) {
                    try {
                        connectedInputStream = socket.getInputStream();

                        DataInputStream mmInStream = new DataInputStream(connectedInputStream);
                        bytes = mmInStream.read(buffer);
                        String readMessage = new String(buffer, 0, bytes, StandardCharsets.UTF_8);
                        char[] chars = readMessage.toCharArray();

                        for (char c : chars) {
                            fullMsg.append(c);
                            if (fullMsg.length() == 15) {
                                for (int i = 0; i < 3; i++)
                                    gyros[i] = Integer.parseInt(fullMsg.substring(5*i, 5*(i+1)).split("@")[0]);

                                fullMsg = new StringBuilder();
                                checkForMovement(gyros);
                            }
                        }

                    } catch (IOException ignored) {
                        TrackerBTConnection = null;
                        break;
                    }
                }
            }
        }

        public void write(byte[] buffer) {
            try {
                connectedOutputStream.write(buffer);
            } catch (IOException ignored) {}
        }
    }
}
