package com.frank.ycj520.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Xfermode;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;



public class WaveViewForDouble extends View {

    private static final String TAG="WaveViewForDouble";
    private final Paint mPaint=new Paint();
    private final Path mFirstPath=new Path();
    private final Path mSecondPath=new Path();
    private final Path mCenterPath=new Path();
    private static final int SAMPLINT_SIZE=128;
    private float[]mSamplingX;
    private float[]mMapX;
    private int mWidth;
    private int mHeight;
    private int mCenterHeight;
    private int mAmplitude;//振幅
    private long startTime=System.currentTimeMillis();
    /*
    波峰和两条路径交叉点的记录，包括起点和终点，用于绘制渐变
     */
    private final float[][] mCrestAndCrossPints=new float[9][];
    private final RectF rectF=new RectF();
    private final Xfermode xfermode=new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
    private int mBackGroundColor=Color.rgb(24,33,41);
    private final int mCenterPathColor=Color.argb(64,255,255,255);
    public WaveViewForDouble(Context context) {
        //super(context);
        this(context,null);
    }

    public WaveViewForDouble(Context context, @Nullable AttributeSet attrs) {
        //super(context, attrs);
        this(context,attrs,0);
    }

    public WaveViewForDouble(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint.setDither(true);//防抖动
        mPaint.setAntiAlias(true);//抗锯齿
        for (int i=0;i<9;i++){
            mCrestAndCrossPints[i]=new float[2];
        }
    }

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            invalidate();
            handler.sendEmptyMessageDelayed(0,16);
        }
    };

    /*@Override
    protected void onDraw(Canvas canvas) {

        if (mSamplingX==null){
            mWidth=canvas.getWidth();
            mHeight=canvas.getHeight();

            mCenterHeight=mHeight==0?50:mHeight>>1;
            mAmplitude=mWidth==0?30:mWidth>>3; //除以8
            mSamplingX=new float[SAMPLINT_SIZE+1];
            mMapX=new float[SAMPLINT_SIZE+1];
            float gap=mWidth/(float)SAMPLINT_SIZE;
            float x;
            for (int i=0;i<SAMPLINT_SIZE;i++){
                x=i*gap;
                mSamplingX[i]=x;
                mMapX[i]=(x/(float)mWidth)*4-2;//映射到[-2,2]

            }
        }

        canvas.drawColor(Color.rgb(24,33,41));
        mFirstPath.rewind();
        mSecondPath.rewind();
        mCenterPath.rewind();

        mFirstPath.moveTo(0,mCenterHeight);
        mSecondPath.moveTo(0,mCenterHeight);
        mCenterPath.moveTo(0,mCenterHeight);

        float offset=(System.currentTimeMillis()-startTime)/500F;
        float x;
        float curV=0;
        for (int i=0;i<SAMPLINT_SIZE;i++){
            x=mSamplingX[i];
            curV=i<SAMPLINT_SIZE?(float)(mAmplitude*calculate(mMapX[i],offset)):0;
            mFirstPath.lineTo(x,mCenterHeight+curV);
            mSecondPath.lineTo(x,mCenterHeight-curV);
            mCenterPath.lineTo(x,(mCenterHeight+curV)/5);

        }
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(1);
        canvas.drawPath(mFirstPath,mPaint);
        canvas.drawPath(mSecondPath,mPaint);
        canvas.drawPath(mCenterPath,mPaint);
        int saveCount=canvas.saveLayer(0,0,mWidth,mHeight,null,Canvas.ALL_SAVE_FLAG);

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.BLUE);
        mPaint.setXfermode(xfermode);
        mPaint.setShader(new LinearGradient(0,mCenterHeight+mAmplitude,mWidth,mCenterHeight-mAmplitude,Color.BLUE,Color.GREEN,Shader.TileMode.CLAMP));

        rectF.set(0,mCenterHeight+mAmplitude,mWidth,mCenterHeight-mAmplitude);
        canvas.drawRect(rectF,mPaint);
        mPaint.setShader(null);
        mPaint.setXfermode(null);
        canvas.restoreToCount(saveCount);
        mPaint.setStyle(Paint.Style.STROKE);//描边
        mPaint.setColor(Color.BLUE);
        mPaint.setStrokeWidth(3);
        canvas.drawPath(mFirstPath,mPaint);
        canvas.drawPath(mSecondPath,mPaint);
        mPaint.setColor(mCenterPathColor);
        canvas.drawPath(mCenterPath,mPaint);

        handler.sendEmptyMessageDelayed(0,16);
    }*/

    @Override
    protected void onDraw(Canvas canvas) {
        if (mSamplingX==null){
            mWidth=canvas.getWidth();
            mHeight=canvas.getHeight();
            mCenterHeight=mHeight>>1;
            mAmplitude=mWidth>>3;

            mSamplingX=new float[SAMPLINT_SIZE+1];
            mMapX=new float[SAMPLINT_SIZE+1];
            float gap=mWidth/(float)SAMPLINT_SIZE;
            float x;
            for (int i=0;i<SAMPLINT_SIZE;i++){
                x=i*gap;
                mSamplingX[i]=x;
                mMapX[i]=(x/(float)mWidth)*4-2;

            }
        }

        //绘制背景
        canvas.drawColor(mBackGroundColor);

        mFirstPath.rewind();
        mSecondPath.rewind();
        mCenterPath.rewind();
        mFirstPath.moveTo(0,mCenterHeight);
        mSecondPath.moveTo(0,mCenterHeight);
        mCenterPath.moveTo(0,mCenterHeight);

        float offset=(System.currentTimeMillis()-startTime)/500F;
        float x;
        float[]xy;
        float cruV=0,lastV=0;
        float nextV=(float)(mAmplitude*calculate(mMapX[0],offset));
        float absLastV,absCurV,absNextV;
        boolean lastIsCrest=false;
        int cresAndCrossCount=0;
        for (int i=0;i<SAMPLINT_SIZE;i++){
            x=mSamplingX[i];
            lastV=cruV;
            cruV=nextV;
            nextV=i<SAMPLINT_SIZE?(float)(mAmplitude*calculate(mMapX[i+1],offset)):0;

            mFirstPath.lineTo(x,mCenterHeight+cruV);
            mSecondPath.lineTo(x,mCenterHeight-cruV);

            mCenterPath.lineTo(x,mCenterHeight+cruV/5F);

            //记录极值点
            absLastV=Math.abs(lastV);
            absCurV=Math.abs(cruV);
            absNextV=Math.abs(nextV);

            if (i==0||i==SAMPLINT_SIZE||(lastIsCrest&&absCurV<absNextV&&absCurV<absLastV)){
                xy=mCrestAndCrossPints[cresAndCrossCount++];
                xy[0]=x;
                xy[1]=0;
                lastIsCrest=false;
            }else if (!lastIsCrest&&absCurV>absLastV&&absCurV>absNextV){
                xy=mCrestAndCrossPints[cresAndCrossCount++];
                xy[0]=x;
                xy[1]=cruV;
                lastIsCrest=true;
            }

        }

        mFirstPath.lineTo(mWidth,mCenterHeight);
        mSecondPath.lineTo(mWidth,mCenterHeight);
        mCenterPath.lineTo(mWidth,mCenterHeight);

        int saveCount= canvas.saveLayer(0,0,mWidth,mHeight,null,Canvas.ALL_SAVE_FLAG);

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(1);
        canvas.drawPath(mFirstPath,mPaint);
        canvas.drawPath(mSecondPath,mPaint);

        //绘制渐变

        mPaint.setColor(Color.BLUE);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setXfermode(xfermode);
        float startX,crestY,endX;
        for (int i=2;i<cresAndCrossCount;i+=2){
            startX=mCrestAndCrossPints[i-2][0];
            crestY=mCrestAndCrossPints[i-1][1];
            endX=mCrestAndCrossPints[i][0];
            mPaint.setShader(new LinearGradient(0,mCenterHeight+crestY,0,mCenterHeight-crestY,Color.GREEN,Color.BLUE,Shader.TileMode.CLAMP));

            rectF.set(startX,mCenterHeight+crestY,endX,mCenterHeight-crestY);
            canvas.drawRect(rectF,mPaint);
        }

        //清理
        mPaint.setShader(null);
        mPaint.setXfermode(null);
        canvas.restoreToCount(saveCount);

        mPaint.setStrokeWidth(3);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.BLUE);
        canvas.drawPath(mFirstPath,mPaint);

        mPaint.setColor(Color.GREEN);
        canvas.drawPath(mSecondPath,mPaint);

        mPaint.setColor(mCenterPathColor);
        canvas.drawPath(mCenterPath,mPaint);

        handler.sendEmptyMessageDelayed(0,16);


    }

    private double calculate(float mapX, float offset){
        offset%=2;
        double sinFunx=Math.sin(0.75*Math.PI*mapX-offset*Math.PI);
        double recessionFun=Math.pow((4/(4+Math.pow(mapX,4))),2.5);
        return sinFunx*recessionFun;
    }
}
