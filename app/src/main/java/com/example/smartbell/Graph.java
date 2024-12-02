package com.example.smartbell;

import static com.example.smartbell.MainActivity.sleepDuration;
import static com.example.smartbell.MainActivity.timeSleep;
import static com.example.smartbell.MainActivity.timeWoke;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Graph extends View {

    Paint bgColor, lineColor, textColor;

    String[] stages = {"Awake", "Light", "Deep", "Time"};
    int[] start = {120, 80};
    int w = 680;
    int h = 550;

    public static ArrayList<Integer> dots = null;
    int offset, maxValue;


    @SuppressLint("ResourceAsColor")
    public Graph(Context context) {
        super(context);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);

        bgColor = new Paint();
        bgColor.setStyle(Paint.Style.FILL);
        bgColor.setColor(getResources().getColor(R.color.extra));

        lineColor = new Paint();
        lineColor.setStyle(Paint.Style.STROKE);

        textColor = new Paint(Paint.LINEAR_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        textColor.setColor(getResources().getColor(R.color.naming));
        textColor.setTextSize(pxFromDp(context, 12));

        if (dots != null)
            if (dots.size() > 1) {
                offset = (int) (w / (dots.size() - 1));
                maxValue = Collections.max(dots);
            }
    }

    public static float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Static
        canvas.drawRoundRect(0, 0, this.getWidth(), this.getHeight(), 20, 20, bgColor);

        lineColor.setStrokeWidth(4);
        lineColor.setColor(getResources().getColor(R.color.axis));
        for (int i = 0; i < stages.length; i++) {
            int y = start[1] + i * 175;
            canvas.drawText(stages[i], 10, y, textColor);
            canvas.drawLine(start[0], y, start[0]+w, y, lineColor);
        }

        lineColor.setColor(getResources().getColor(R.color.naming));
        canvas.drawLine(start[0], start[1], start[0], start[1]+h, lineColor);
        canvas.drawLine(start[0], start[1]+h, start[0]+w, start[1]+h, lineColor);


        // Dynamic
        int timeHours = Math.abs(timeSleep[0] - timeWoke[0]);
        if (timeHours > 0) {
            int timeGap = (int) (w / timeHours);

            int timeOffsetStart = ( (timeSleep[1] > 30) ? timeGap / 2 : 0);
            int timeOffsetEnd = ( (timeWoke[1] < 30) ? timeGap / 2 : 30);

            int fullLen = w - timeOffsetStart - timeOffsetEnd;
            int timeHour = fullLen / timeHours;

            for (int i = 0; i <= (int)(sleepDuration/3600); i++)
                canvas.drawText(String.format("%02d", (timeSleep[0]+i)), start[0] + (timeHour * i), start[1] + h + 50, textColor);

        } else
            canvas.drawText(String.format("%02d", (timeSleep[0])), start[0] + (w/2), start[1] + h + 50, textColor);


        lineColor.setColor(getResources().getColor(R.color.clock));
        lineColor.setStrokeWidth(7);

        if (dots != null)
            if (dots.size() > 1)
                for (int i = 1; i < dots.size(); i++)
                    canvas.drawLine(start[0] + (i - 1) * offset,
                            getValue(dots.get(i - 1)),
                            start[0] + i * offset,
                            getValue(dots.get(i)),
                            lineColor);
    }

    private int getValue(int value) {
        if (value != maxValue)
            value = (int)((float)h * ((float)value / (float)maxValue));
        else
            value = h;

        return (start[1] + h) - value;
    }
}
