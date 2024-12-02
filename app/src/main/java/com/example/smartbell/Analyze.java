package com.example.smartbell;

import static com.example.smartbell.MainActivity.movements;
import static com.example.smartbell.MainActivity.sleep;
import static com.example.smartbell.MainActivity.sleepDuration;
import static com.example.smartbell.MainActivity.timeSleep;
import static com.example.smartbell.MainActivity.timeWoke;
import static com.example.smartbell.MainActivity.toast;
import static com.example.smartbell.MainActivity.userMode;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;

public class Analyze extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public static View view;
    TextView sleepMode;

    public static RelativeLayout rl;
    public static String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    public static int[] position = new int[3];

    public Analyze() {
    }

    public static Analyze newInstance(String param1, String param2) {
        Analyze fragment = new Analyze();
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

        view = inflater.inflate(R.layout.fragment_analyze, container, false);

        if (!userMode)
            toast("USER mode");
        userMode = true;

        sleepMode = view.findViewById(R.id.analyzeSleepMode);
        if (sleep)
            sleepMode.setText("SLEEPING");
        else
            sleepMode.setText("WAKEFULNESS");

        setInfo();

        return view;
    }

    public static void setInfo() {
        rl = view.findViewById(R.id.analyzeGraph);

        Graph graph = new Graph(view.getContext());
        rl.addView(graph);

        Calendar calendar = Calendar.getInstance();

        TextView day = view.findViewById(R.id.analyzeDay);
        day.setText(calendar.get(Calendar.DAY_OF_MONTH) + "");

        TextView month = view.findViewById(R.id.analyzeMonth);
        month.setText( months[calendar.get(Calendar.MONTH)] );

        TextView sleep = view.findViewById(R.id.analyzeSleepStart);
        sleep.setText(String.format("%02d", timeSleep[0]) + ":" + String.format("%02d", timeSleep[1]));

        TextView woke = view.findViewById(R.id.analyzeSleepEnd);
        woke.setText(String.format("%02d", timeWoke[0]) + ":" + String.format("%02d", timeWoke[1]));

        TextView bed = view.findViewById(R.id.analyzeSleepBed);
        bed.setText((int)(sleepDuration/3600) + "h" + (int)(sleepDuration%3600/60) + "m");

        setTimer();
    }

    private static void setTimer() {
        final Handler handler = new Handler();
        final int delay = 1000;

        handler.postDelayed(new Runnable() {
            public void run() {
                if (view != null) {
                    Graph.dots = movements;
                    Graph graph = new Graph(view.getContext());
                    rl.addView(graph);
                }

                handler.postDelayed(this, delay);
            }
        }, delay);
    }

    // Tracker
    public static void checkForMovement(int[] newPos) {
        int diff = Math.abs(newPos[0] - position[0]) + Math.abs(newPos[1] - position[1]) + Math.abs(newPos[2] - position[2]);
        for (int i = 0; i < newPos.length; i++)
            position[i] = newPos[i];

        Log.e("SEND", position[0] + " " + position[1] + " " + position[2]);
        if (position[0] != 0 && sleep)
            movements.add(diff);
    }
}