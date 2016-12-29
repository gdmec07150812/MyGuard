package com.gdmec.jacky.myguard.m9advancedtools.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

import com.gdmec.jacky.myguard.R;
import com.gdmec.jacky.myguard.m9advancedtools.utils.DensityUtil;

/**
 * Created by dell-pc on 2016/12/19.
 */
public class MyCircleProgress extends Button {

    private Paint paint;
    //进度
    private int progress;
    private int max;
    //进度为0时候颜色
    private int mCircleColor;
    private int mProgressColor;
    private float roundWidth;
    private int mProgressTextSize;
    private Context context;

    private float mDistance0Fbg;

    public MyCircleProgress(Context context) {
        this(context, null);
    }

    public MyCircleProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyCircleProgress(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        paint = new Paint();
        this.context = context;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyCircleProgress);
        progress = typedArray.getInteger(R.styleable.MyCircleProgress_progress, 0);
        max = typedArray.getInteger(R.styleable.MyCircleProgress_circleColor, Color.RED);
        mProgressColor = typedArray.getColor(R.styleable.MyCircleProgress_progressColor, Color.WHITE);
        roundWidth = DensityUtil.dip2px(context, 5);
        mDistance0Fbg = DensityUtil.dip2px(context, 5);
        mProgressTextSize = DensityUtil.dip2px(context, 16);
        typedArray.recycle();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //得到圆心位置
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int radius = (int) (centerX - mDistance0Fbg);
        paint.setColor(mCircleColor);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(roundWidth);
        canvas.drawCircle(centerX, centerY, radius, paint);
        //画进度百分比
        paint.setColor(mProgressColor);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(roundWidth);
        //用于定义的圆弧的形状和大小的界限
        RectF oval = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
        paint.setStyle(Paint.Style.STROKE);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvas.drawArc(oval, 0, 360 * progress / max, false, paint);

        //写进度文字
        paint.setStrokeWidth(0);
        paint.setColor(mProgressColor);
        paint.setTextSize(mProgressTextSize);
        //设置文字
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        int percent = (int) (((float) progress / (float) max) * 100);
        //中间的进度百分比，先转换成float在进行除法运算，不然都为0
        float textWidth = paint.measureText(percent + "%");
        if (percent > 0) {
            //画出进度百分比
            canvas.drawText(percent + "%", centerX - textWidth / 2, centerY + radius - mDistance0Fbg * 6, paint);
        }
    }

    public synchronized int getProgress() {
        return progress;
    }

    //置进度，此为线程安全控件，由于考虑多线程的问题，需要同步
    public synchronized void setProcess(int process) {
        if (process < 0) {
            throw new IllegalArgumentException("progress not less than 0");
        }
        if (process > max) {
            process = max;
        }
        if (process <= max) {
            this.progress = progress;
            //重绘图片
            postInvalidate();
        }
    }

    public synchronized void setMax(int max) {
        if (max < 0) {
            throw new IllegalArgumentException("max not less than 0");
        }
        this.max = max;
    }
}
