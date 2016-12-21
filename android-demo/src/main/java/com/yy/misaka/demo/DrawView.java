package com.yy.misaka.demo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.yy.httpproxy.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DrawView extends View {

    public static class Dot {
        private static int ID = (int) (Math.random() * 1000000);

        public float xPercent;
        public float yPercent;
        public String color;
        public int id = ID;
        public long timestamp = System.currentTimeMillis();
        public boolean endline = false;

        @Override
        public String toString() {
            return "Dot{" +
                    "xPercent=" + xPercent +
                    ", yPercent=" + yPercent +
                    ", myColor=" + color +
                    ", timestamp=" + timestamp +
                    ", endline=" + endline +
                    '}';
        }

        public void setIntColor(int intColor) {
            color = String.format("%06X", (0xFFFFFF & intColor));
        }
    }

    private Map<Integer, List<Dot>> lineMap = new HashMap<>();

    public DrawView(Context context) {
        super(context);
    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Log.i("drawview", "ondraw");
        Paint paint = new Paint();

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(6);
        for (List<Dot> dots : lineMap.values()) {
            Path path = new Path();
            boolean first = true;
            for (Dot dot : dots) {
                float x = dot.xPercent * canvas.getWidth();
                float y = dot.yPercent * canvas.getHeight();
                paint.setColor(Color.parseColor("#" + dot.color));
                if (first) {
                    first = false;
                    path.moveTo(x, y);
                } else if (dot.endline) {
                    path.lineTo(x, y);
                    canvas.drawPath(path, paint);
                    first = true;
                } else {
                    path.lineTo(x, y);
                }
            }
            if (!first) {
                canvas.drawPath(path, paint);
            }
        }
    }

    public void addDot(Dot dot) {
        List<Dot> lines = lineMap.get(dot.id);
        if (lines == null) {
            lines = new ArrayList<>();
            lineMap.put(dot.id, lines);
        }
        lines.add(dot);
        invalidate();
    }

    public void clear() {
        lineMap.clear();
        invalidate();
    }
}
