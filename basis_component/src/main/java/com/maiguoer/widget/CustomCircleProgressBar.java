package com.maiguoer.widget;


import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.maiguoer.component.http.R;

import static android.graphics.Paint.Style.FILL;
import static android.graphics.Paint.Style.STROKE;

/**
 *自定义圆形加载进度条
 */

public class CustomCircleProgressBar extends View {

    /*进度的颜色*/
    private int outsideColor;
    /*外圆半径大小*/
    private float outsideRadius;
    /*背景颜色*/
    private int insideColor;
    /*圆环内文字颜色*/
    private int progressTextColor;
    /*圆环内文字大小*/
    private float progressTextSize;
    /*圆环的宽度*/
    private float progressWidth;
    /*最大进度*/
    private int maxProgress;
    /*当前进度*/
    private float progress;

    private Paint paint;
    /*圆环内文字*/
    private String progressText;
    private Rect rect;

    private ValueAnimator animator;

    public CustomCircleProgressBar(Context context) {
        this(context, null);
    }

    public CustomCircleProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomCircleProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomCircleProgressBar, defStyleAttr, 0);
        outsideColor = a.getColor(R.styleable.CustomCircleProgressBar_outside_color, ContextCompat.getColor(getContext(), R.color.T2));
        outsideRadius = a.getDimension(R.styleable.CustomCircleProgressBar_outside_radius, 120f);
        insideColor = a.getColor(R.styleable.CustomCircleProgressBar_inside_color, ContextCompat.getColor(getContext(), R.color.T2));
        progressTextColor = a.getColor(R.styleable.CustomCircleProgressBar_progress_text_color, ContextCompat.getColor(getContext(), R.color.T12));
        progressTextSize = a.getDimension(R.styleable.CustomCircleProgressBar_progress_text_size,  28f);
        progressWidth = a.getDimension(R.styleable.CustomCircleProgressBar_progress_width,  20f);
        progress = a.getFloat(R.styleable.CustomCircleProgressBar_progress, 50.0f);
        maxProgress = a.getInt(R.styleable.CustomCircleProgressBar_max_progress, 100);

        a.recycle();

        paint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int circlePoint = getWidth() / 2;
        //第一步:画内圈背景
        //设置圆的颜色
        paint.setColor(Color.parseColor("#40000000"));
        //设置空心
        paint.setStyle(FILL);
        //设置圆的宽度
        paint.setStrokeWidth(progressWidth);
        //消除锯齿
        paint.setAntiAlias(true);
        //画圆
        canvas.drawCircle(circlePoint, circlePoint, outsideRadius, paint);

        //第二步:画背景(即内层圆)
        //设置圆的颜色
        paint.setColor(insideColor);
        //设置空心
        paint.setStyle(STROKE);
        //设置圆的宽度
        paint.setStrokeWidth(progressWidth);
        //消除锯齿
        paint.setAntiAlias(true);
        //画圆
        canvas.drawCircle(circlePoint, circlePoint, outsideRadius, paint);

        //第三步:画进度(圆弧)
        //设置进度的颜色
        paint.setColor(outsideColor);
        //用于定义的圆弧的形状和大小的界限
        RectF oval = new RectF(circlePoint - outsideRadius, circlePoint - outsideRadius, circlePoint + outsideRadius, circlePoint + outsideRadius);
        //根据进度画圆弧
        canvas.drawArc(oval, 270, 360 * (progress / maxProgress), false, paint);

        //第四步:画圆环内百分比文字
        rect = new Rect();
        paint.setColor(progressTextColor);
        paint.setTextSize(progressTextSize);
        paint.setStrokeWidth(0);
        progressText = getProgressText();
        paint.getTextBounds(progressText, 0, progressText.length(), rect);
        Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
        //获得文字的基准线
        int baseline = (getMeasuredHeight() - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
        canvas.drawText(progressText, getMeasuredWidth() / 2 - rect.width() / 2, baseline, paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width;
        int height;
        int size = MeasureSpec.getSize(widthMeasureSpec);
        int mode = MeasureSpec.getMode(widthMeasureSpec);

        if (mode == MeasureSpec.EXACTLY) {
            width = size;
        } else {
            width = (int) ((2 * outsideRadius) + progressWidth);
        }
        size = MeasureSpec.getSize(heightMeasureSpec);
        mode = MeasureSpec.getMode(heightMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            height = size;
        } else {
            height = (int) ((2 * outsideRadius) + progressWidth);
        }
        setMeasuredDimension(width, height);
    }

    //中间的进度百分比
    private String getProgressText() {
        return (int) ((progress / maxProgress) * 100) + "%";
    }

    public int getOutsideColor() {
        return outsideColor;
    }

    public void setOutsideColor(int outsideColor) {
        this.outsideColor = outsideColor;
    }

    public float getOutsideRadius() {
        return outsideRadius;
    }

    public void setOutsideRadius(float outsideRadius) {
        this.outsideRadius = outsideRadius;
    }

    public int getInsideColor() {
        return insideColor;
    }

    public void setInsideColor(int insideColor) {
        this.insideColor = insideColor;
    }

    public int getProgressTextColor() {
        return progressTextColor;
    }

    public void setProgressTextColor(int progressTextColor) {
        this.progressTextColor = progressTextColor;
    }

    public float getProgressTextSize() {
        return progressTextSize;
    }

    public void setProgressTextSize(float progressTextSize) {
        this.progressTextSize = progressTextSize;
    }

    public float getProgressWidth() {
        return progressWidth;
    }

    public void setProgressWidth(float progressWidth) {
        this.progressWidth = progressWidth;
    }

    public synchronized int getMaxProgress() {
        return maxProgress;
    }

    public synchronized void setMaxProgress(int maxProgress) {
        if (maxProgress < 0) {
            //此为传递非法参数异常
            throw new IllegalArgumentException("maxProgress should not be less than 0");
        }
        this.maxProgress = maxProgress;
    }

    public synchronized float getProgress() {
        return progress;
    }

    //加锁保证线程安全,能在线程中使用
    /*public synchronized void setProgress(int progress) {
        if (progress < 0) {
            throw new IllegalArgumentException("progress should not be less than 0");
        }
        if (progress > maxProgress) {
            progress = maxProgress;
        }
        startAnim(progress);
    }

    private void startAnim(float startProgress) {
        animator = ObjectAnimator.ofFloat(0, startProgress);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                CustomCircleProgressBar.this.progress = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        animator.setStartDelay(500);
        animator.setDuration(2000);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
    }*/

    public synchronized void setProgress(int progress){
        this.progress = progress;
        postInvalidate();
    }
}
