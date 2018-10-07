package com.frank.ycj520.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class BrokenLineView1 extends View {
    public int mXPoint=10;    //原点的X坐标
    public int mYPoint=460;     //原点的Y坐标
    public int mXScale=60;     //X的刻度长度
    public int mYScale=40;     //Y的刻度长度
    public int XLength=480;        //X轴的长度
    public int YLength=340;        //Y轴的长度
    public String[] XLabel;    //X的刻度
    public String[] YLabel;    //Y的刻度
    public String[] mData;      //数据
    public String Title;    //显示的标题
    public BrokenLineView1(Context context) {
        super(context);
    }
    public BrokenLineView1(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public BrokenLineView1(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public void SetInfo(String[] XLabel, String[] YLable, String[] mData, String Title) {
        this.XLabel = XLabel;
        this.YLabel = YLable;
        this.mData = mData;
        this.Title = Title;
        mXPoint=100;
        mYPoint=460;
    }
    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);//重写onDraw方法

        canvas.drawColor(Color.parseColor("#ffffff"));//设置背景颜色
        Paint paint= new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);//去锯齿
        paint.setColor(Color.parseColor("#ff02f2"));//颜色
        Paint paint1=new Paint();
        paint1.setStyle(Paint.Style.STROKE);
        paint1.setAntiAlias(true);//去锯齿
        paint1.setColor(Color.DKGRAY);
        paint.setTextSize(12);  //设置轴文字大小
        //设置Y轴
        canvas.drawLine(mXPoint, mYPoint,mXPoint, mYPoint-YLength,  paint);   //轴线
        for(int i=0;i*mYScale<YLength ;i++)
        {
            canvas.drawLine(mXPoint,mYPoint-i*mYScale, mXPoint+5, mYPoint-i*mYScale, paint);  //刻度
            try
            {
                canvas.drawText(YLabel[i] , mXPoint-22, mYPoint-i*mYScale+5, paint);  //文字
            }
            catch(Exception e)
            {
            }
        }
        canvas.drawLine(mXPoint,mYPoint-YLength,mXPoint-3,mYPoint-YLength+6,paint);  //箭头
        canvas.drawLine(mXPoint,mYPoint-YLength,mXPoint+3,mYPoint-YLength+6,paint);
        //设置X轴
        canvas.drawLine(mXPoint,mYPoint,mXPoint+XLength,mYPoint,paint);   //轴线
        for(int i=0;i*mXScale<XLength;i++)
        {
            canvas.drawLine(mXPoint+i*mXScale, mYPoint, mXPoint+i*mXScale, mYPoint-5, paint);  //刻度
            try
            {
                canvas.drawText(XLabel[i] , mXPoint+i*mXScale-10, mYPoint+20, paint);  //文字
                //数据值
                if(i>0&&getYPoint(mData[i-1])!=-999&&getYPoint(mData[i])!=-999)  //保证有效数据
                    canvas.drawLine(mXPoint+(i-1)*mXScale, getYPoint(mData[i-1]), mXPoint+i*mXScale, getYPoint(mData[i]), paint);
                canvas.drawCircle(mXPoint+i*mXScale,getYPoint(mData[i]), 2, paint);
            }
            catch(Exception e)
            {
            }
        }
        canvas.drawLine(mXPoint+XLength,mYPoint,mXPoint+XLength-6,mYPoint-3,paint);    //箭头
        canvas.drawLine(mXPoint+XLength,mYPoint,mXPoint+XLength-6,mYPoint+3,paint);
        paint.setTextSize(16);
        canvas.drawText(Title, 250, 150, paint);
    }
    private int getYPoint(String y0)  //计算绘制时的Y坐标，无数据时返回-999
    {
        int y;
        try
        {
            y=Integer.parseInt(y0);
        }
        catch(Exception e)
        {
            return -999;    //出错则返回-999
        }
        try
        {
            return mYPoint-y*mYScale/Integer.parseInt(YLabel[1]);
        }
        catch(Exception e)
        {
        }
        return y;
    }
}
