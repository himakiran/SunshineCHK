package com.example.android.sunshine.app;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * Created by userhk on 11/03/17.
 * Used code from url http://stackoverflow.com/questions/17954596/how-to-draw-circle-by-canvas-in-android
 * https://www.intertech.com/Blog/android-custom-view-tutorial-part-2-custom-attributes-drawing-and-measuring/
 */

public class MyView extends View {

    public String textBox1;
    public String textBox2;
    public String textBox3;
    private Paint mCirclePaint;
    private Paint mTextPaint;
    private Paint mLinePaint;
    private int size;
    private int circleColor;
    private int line_color;

    public MyView(Context context) {
        super(context);
    }

    public MyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.MyView,
                0, 0);

        try {
            circleColor = a.getInt(R.styleable.MyView_circleColor, Color.RED);
            line_color = a.getInt(R.styleable.MyView_lineColor, Color.WHITE);
            textBox1 = a.getString(R.styleable.MyView_textBox1);
            textBox2 = a.getString(R.styleable.MyView_textBox2);
            textBox3 = a.getString(R.styleable.MyView_textBox3);
        } finally {
            a.recycle();
        }
        mCirclePaint = new Paint();
        mCirclePaint.setColor(circleColor);
        mCirclePaint.setAntiAlias(true);

        mTextPaint = new Paint();
        mTextPaint.setColor(line_color);
        mTextPaint.setTextSize(35);
        mTextPaint.setFakeBoldText(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setAntiAlias(true);

        mLinePaint = new Paint();
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setColor(line_color);
        mLinePaint.setStrokeWidth(4f);

        setOnMeasureCallback();
    }


    private void setOnMeasureCallback() {
        final ViewTreeObserver vto = getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                removeOnGlobalLayoutListener(this);
                size = getMeasuredWidth() / 2;
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void removeOnGlobalLayoutListener(ViewTreeObserver.OnGlobalLayoutListener listener) {
        if (Build.VERSION.SDK_INT < 16) {
            getViewTreeObserver().removeGlobalOnLayoutListener(listener);
        } else {
            getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(size, size, size, mCirclePaint);

        canvas.drawLine(-size - 10, size + 20, size * 2, size + 20, mLinePaint);
        canvas.drawLine(-size - 10, size - 40, size * 2, size - 40, mLinePaint);
        canvas.drawText(textBox1, size, size - 50, mTextPaint);
        canvas.drawText(textBox2, size, size, mTextPaint);
        canvas.drawText(textBox3, size, size + 60, mTextPaint);
    }

    public void setText(String a, String b, String c) {
        textBox1 = "ZIP : " + a;
        textBox2 = "LAT : " + b;
        textBox3 = "LNG : " + c;
        this.refreshDrawableState();
    }
}
