package com.frank.ycj520.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class BrokenLineView2 extends View {

    private Context context;

    public BrokenLineView2(Context context) {
        this(context, null);
    }

    public BrokenLineView2(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BrokenLineView2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        obtainAttribute(context, attrs);
        init(context);
        this.context = context;
    }

    //画线的笔
    private Paint mPaint;

    //画折线的笔
    private Paint mBrokenLinePaint;

    //写坐标轴上文字的笔
    private TextPaint mTextPaint;

    //顶点文字的笔
    private TextPaint mTextVerticesPaint;

    //画圆点的笔
    private Paint mCirclePaint;

    //坐标的path
    private Path mPath;

    //折线path，与上面用同一个会存在颜色互串的问题
    private Path mBrokenPath;

    //屏幕宽度
    private int screenWidth;
    //屏幕高度
    private int screenHeight;

    //padding系列
    private int paddingTop;
    private int paddingBottom;
    private int paddingRight;
    private int paddingLeft;

    private void init(Context context) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);//抗锯齿
        mPaint.setColor(linePaintColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(lineWidth);

        mBrokenLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBrokenLinePaint.setColor(brokenLinePaintColor);
        mBrokenLinePaint.setStyle(Paint.Style.STROKE);
        mBrokenLinePaint.setStrokeWidth(lineWidth);

        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(textPaintColor);
        mTextPaint.setTextSize(textPaintSize);

        mTextVerticesPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextVerticesPaint.setColor(textVerticesPaintColor);
        mTextVerticesPaint.setTextSize(textVerticesPaintSize);

        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setColor(circlePaintColor);

        mPath = new Path();
        mBrokenPath = new Path();

        //获取屏幕的宽高
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;

        paddingTop = Math.max(dp2px(context, 16), getPaddingTop());
        paddingBottom = Math.max(dp2px(context, 12), getPaddingBottom());
        paddingLeft = Math.max(dp2px(context, 16), getPaddingLeft());
        paddingRight = Math.max(dp2px(context, 24), getPaddingRight());
    }

    /**
     * 得到设置的属性
     */
//线的颜色
    private int linePaintColor;
    //折线的颜色
    private int brokenLinePaintColor;
    //字的颜色
    private int textPaintColor;
    //字的大小
    private int textPaintSize;
    //顶点圆点的颜色
    private int circlePaintColor;
    //圆点半径
    private float circleRadius;
    //顶点文字的颜色
    private int textVerticesPaintColor;
    //顶点文字的大小
    private int textVerticesPaintSize;
    //线宽
    private float lineWidth;

    private void obtainAttribute(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BrokenLineView2);
        try {
            linePaintColor = typedArray.getColor(R.styleable.BrokenLineView2_linePaintColor, Color.BLUE);

            brokenLinePaintColor = typedArray.getColor(R.styleable.BrokenLineView2_brokenLinePaintColor, Color.BLACK);

            textPaintColor = typedArray.getColor(R.styleable.BrokenLineView2_textPaintColor, Color.BLACK);
            textPaintSize = typedArray.getDimensionPixelSize(R.styleable.BrokenLineView2_textPaintSize, sp2px(context, 10));

            circlePaintColor = typedArray.getColor(R.styleable.BrokenLineView2_circlePaintColor, Color.BLACK);
            circleRadius = typedArray.getDimensionPixelSize(R.styleable.BrokenLineView2_circleRadius, dp2px(context, 1));

            textVerticesPaintColor = typedArray.getColor(R.styleable.BrokenLineView2_textVerticesPaintColor, Color.BLACK);
            textVerticesPaintSize = typedArray.getDimensionPixelSize(R.styleable.BrokenLineView2_textVerticesPaintSize, sp2px(context, 10));
            lineWidth = typedArray.getDimensionPixelSize(R.styleable.BrokenLineView2_lineWidth, dp2px(context, 1));
        } finally {
            typedArray.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawTextY(canvas);

        drawTextX(canvas);

        drawCoordinate(canvas);

        drawBrokenLine(canvas);
    }

    //y轴文字的最大宽度
    private int maxTextWidth;
    //y轴每一刻度的高度
    private int singleHeight;
    //文字的高度
    private int textHeight;

    //y轴文字list
    private List<String> textY;

    /**
     * draw Y轴上的文字
     *
     * @param canvas
     */
    private void drawTextY(Canvas canvas) {
        if (textY == null) return;

        // 文字的高度：mTextPaint.descent() - mTextPaint.ascent()
        textHeight = (int) (mTextPaint.descent() - mTextPaint.ascent());

        singleHeight = (getHeight() - paddingBottom - paddingTop - textHeight - spacing) / (textY.size() - 1);


        Log.i("tag", "height: " + getHeight() + "  singleHeight: " + singleHeight
                + "  bottom: " + paddingBottom + " top: " + paddingTop + "  textHeight: " + textHeight);

        int xPos = paddingLeft;
        int yPos;
        for (int i = 0; i < textY.size(); i++) {
            //找出文字所需的最大宽度
            if (mTextPaint.measureText(textY.get(textY.size() - i - 1)) > maxTextWidth) {
                maxTextWidth = (int) mTextPaint.measureText(textY.get(textY.size() - i - 1));
            }

            yPos = paddingTop + singleHeight * i + textHeight / 2;

            canvas.drawText(textY.get(textY.size() - i - 1), xPos, yPos, mTextPaint);
        }
    }

    /**
     * 设置y轴显示的数值
     *
     * @param minY   y轴最小值
     * @param maxY   y轴最大值
     * @param number 坐标轴分为几部分
     * @param unit   数字后面所跟的单位
     */
//每一部分y轴的取值
    private int singleY;
    //y轴最大值
    private int maxY;
    //y轴最小值
    private int minY;

    public void setTextY(int minY, int maxY, int number, String unit) {
        if (maxY - minY <= 0 || number < 1) {
            return;
        }
        this.maxY = maxY;
        this.minY = minY;
        textY = new ArrayList<>();
        singleY = (maxY - minY) / number;
        for (int i = 0; i < number + 1; i++) {
            textY.add(String.valueOf(i * singleY + minY) + unit);
        }
        invalidate();
    }

    //x轴每一刻度的宽度
    private int singleWidth;
    //x轴文字list
    private List<String> textX;

    /**
     * draw X轴上的文字
     *
     * @param canvas
     */
    private void drawTextX(Canvas canvas) {
        if (textX == null) return;

        singleWidth = (getWidth() - paddingLeft - paddingRight - spacing - maxTextWidth) / (textX.size() - 1);

        int xPos;
        int yPos = getHeight() - paddingBottom;
        Log.i("tag", "canvasHeight: " + canvas.getHeight() + "  height: " + getHeight());
        Log.i("tag", "textHeight: " + textHeight);
        for (int i = 0; i < textX.size(); i++) {
            xPos = paddingLeft + maxTextWidth + spacing + singleWidth * i - (int) mTextPaint.measureText(textX.get(i)) / 2;
            canvas.drawText(textX.get(i), xPos, yPos, mTextPaint);
        }
    }

    /**
     * 设置x轴显示的文字
     *
     * @param textX x轴文字集合
     */
    public void setTextX(List<String> textX) {
        if (textX == null) return;
        this.textX = textX;
        invalidate();
    }

    /**
     * 画坐标轴
     *
     * @param canvas 画布
     */
    int spacing = 6;//文字与坐标轴的间距

    private void drawCoordinate(Canvas canvas) {
        //移到画线的起始点（左上角）
        mPath.moveTo(paddingLeft + maxTextWidth + spacing, paddingTop / 2);

        mPath.lineTo(paddingLeft + maxTextWidth + spacing, getHeight() - paddingBottom - textHeight - spacing);

        mPath.lineTo(getWidth() - paddingRight / 2, getHeight() - paddingBottom - textHeight - spacing);
        canvas.drawPath(mPath, mPaint);

        if (textY == null) {
            return;
        }
        int x0 = paddingLeft + maxTextWidth + spacing;
        int y0 = getHeight() - paddingBottom - textHeight - spacing;
        //画y轴坐标点
        for (int i = 1; i < textY.size(); i++) {
            canvas.drawLine(x0, y0 - singleHeight * i, x0 + dp2px(context, 4), y0 - singleHeight * i, mPaint);
        }

        if (textX == null) {
            return;
        }
        //画x轴坐标点
        for (int i = 1; i < textX.size(); i++) {
            canvas.drawLine(x0 + singleWidth * i, y0, x0 + singleWidth * i, y0 - dp2px(context, 4), mPaint);
        }
    }

    /**
     * 画折线、圆点、顶点文字
     *
     * @param canvas
     */
    private void drawBrokenLine(Canvas canvas) {
        int startX = paddingLeft + maxTextWidth + spacing;
        int startY = getHeight() - paddingBottom - textHeight - spacing;


        Log.i("tag", "height: " + getHeight() + "  singleHeight: " + singleHeight
                + "  bottom: " + paddingBottom + " top: " + paddingTop + "  textHeight: " + textHeight);

        for (int i = 0; i < brokenData.length; i++) {
            if (i == 0) {
                if (brokenData[i] > maxY) {
                    mBrokenPath.moveTo(startX, startY - singleHeight * (maxY - minY) / singleY);
                } else {
                    mBrokenPath.moveTo(startX, startY - singleHeight * (brokenData[i] - minY) / singleY);
                }
            } else {
                if (brokenData[i] > maxY) {
                    mBrokenPath.lineTo(startX + singleWidth * i, startY - singleHeight * (maxY - minY) / singleY);
                } else {
                    mBrokenPath.lineTo(startX + singleWidth * i, startY - singleHeight * (brokenData[i] - minY) / singleY);
                }
            }
        }
        canvas.drawPath(mBrokenPath, mBrokenLinePaint);

        //在画线之后调用，不然会存在线覆盖点的问题（看着怪）
        for (int i = 0; i < brokenData.length; i++) {
            canvas.drawCircle(startX + singleWidth * i, startY - singleHeight * (brokenData[i] - minY) / singleY, circleRadius, mCirclePaint);
            canvas.drawText(String.valueOf(brokenData[i]), startX + singleWidth * i + spacing, startY - singleHeight * (brokenData[i] - minY) / singleY - spacing, mTextVerticesPaint);
        }

        invalidate();
    }

    //折线图的值
    private float[] brokenData;

    /**
     * 设置折线图顶点的值
     *
     * @param data 数值数组
     */
    public void setBrokenData(float[] data) {
        if (data == null) return;
        this.brokenData = data;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int resultWidth;
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {
            resultWidth = sizeWidth;
        } else {
            if (widthMode == MeasureSpec.AT_MOST) {
                resultWidth = Math.min(screenWidth, sizeWidth);
            } else {
                resultWidth = screenWidth;
            }
        }
        int resultHeight;

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);

        if (heightMode == MeasureSpec.EXACTLY) {
            resultHeight = sizeHeight;
        } else {
            if (heightMode == MeasureSpec.AT_MOST) {
                resultHeight = Math.min(screenHeight, sizeHeight);
            } else {
                resultHeight = screenHeight;
            }
        }

        setMeasuredDimension(resultWidth, resultHeight);
    }

    /**
     * dp转px
     *
     * @param context
     * @return
     */
    private int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }

    /**
     * sp转px
     *
     * @param context
     * @return
     */
    private int sp2px(Context context, float spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, context.getResources().getDisplayMetrics());
    }
}

