package com.frank.ycj520.customview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

public class WaveView extends View {

    private Paint paint;
    private Path path;
    private int waveLength=600;
    private int dx;
    private int dy;
    private Bitmap bitmap;
    private int width;
    private int height;

    private Region region;
    private int waveHeigth=100;
    private int waveView_boatBitmap;
    private boolean waveView_rise;
    private int duration;
    private int originY;
    private ValueAnimator animator;


    public WaveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        //获取自定义属性
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.WaveView);
        waveView_boatBitmap=typedArray.getResourceId(R.styleable.WaveView_boatBitmap,0);
        waveView_rise=typedArray.getBoolean(R.styleable.WaveView_rise,false);
        duration=(int)typedArray.getDimension(R.styleable.WaveView_duration,2000);
        originY=(int)typedArray.getDimension(R.styleable.WaveView_originY,500);
        waveHeigth=(int)typedArray.getDimension(R.styleable.WaveView_waveHeight,100);
        waveLength=(int)typedArray.getDimension(R.styleable.WaveView_waveLength,600);
        typedArray.recycle();

        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inSampleSize=1;
        if (waveView_boatBitmap>0){
            bitmap=BitmapFactory.decodeResource(getResources(),waveView_boatBitmap,options);
            bitmap=getCircleBitmap(bitmap);

        }else {
            bitmap=BitmapFactory.decodeResource(getResources(),R.drawable.default_person,options);
        }

        paint=new Paint();
        paint.setColor(getResources().getColor(R.color.waterColor));
        //paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        path=new Path();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize=MeasureSpec.getSize(widthMeasureSpec);
        int heightSize=MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(widthSize,heightSize);
        width=widthSize;
        height=heightSize;
        if (originY==0){
            originY=height;
        }
    }

    private Bitmap getCircleBitmap(Bitmap bitmap) {
        if (bitmap==null){
            return null;
        }
        try {


        Bitmap circleBitmap=Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(circleBitmap);
        Paint paint1=new Paint();
        Rect rect=new Rect(0,0,bitmap.getWidth(),bitmap.getHeight());
        RectF rectF=new RectF(new Rect(0,0,bitmap.getWidth(),bitmap.getHeight()));
        float roundPx=0.0f;
        if (bitmap.getWidth()>bitmap.getHeight()){
            roundPx=bitmap.getHeight()/2.0f;
        }else {
            roundPx=bitmap.getWidth()/2.0f;
        }
        paint1.setAntiAlias(true);
        canvas.drawARGB(0,0,0,0);
        paint1.setColor(Color.WHITE);
        canvas.drawRoundRect(rectF,roundPx,roundPx,paint1);
        paint1.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        Rect src=new Rect(0,0,bitmap.getWidth(),bitmap.getHeight());
        canvas.drawBitmap(bitmap,src,rect,paint1);
        return circleBitmap;
        }catch (Exception e){
            return bitmap;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //定义曲线
        setPathData();
        canvas.drawPath(path,paint);
        //PathMeasure pathMeasure=new PathMeasure(path,false);
        Rect bounds=region.getBounds();
        if (bounds.top>0||bounds.right>0) {
            //从高峰降到基准线
            if (bounds.top < originY) {
                canvas.drawBitmap(bitmap, bounds.right - bitmap.getWidth() / 2, bounds.top - bitmap.getHeight()+waveHeigth/4, paint);

            } else {
                canvas.drawBitmap(bitmap, bounds.right - bitmap.getWidth() / 2, bounds.bottom - bitmap.getHeight()+waveHeigth/4, paint);

            }
        }else {
            float x=width/2-bitmap.getWidth()/2;
            canvas.drawBitmap(bitmap, x, originY-bitmap.getHeight()+waveHeigth/4, paint);
        }


    }

    private void setPathData() {
        path.reset();
        int halfWaveLength=waveLength/2;
        //水一直平行从左向右
        path.moveTo(-waveLength+dx,originY);
        //水不断向上蔓延
        //path.moveTo(-waveLength+dx,originY-dy);
        for (int i=-waveLength;i<width+waveLength;i+=waveLength){
            //path.quadTo();
            path.rQuadTo(halfWaveLength/2,-waveHeigth,halfWaveLength,0);
            path.rQuadTo(halfWaveLength/2,waveHeigth,halfWaveLength,0);

        }
        region=new Region();
        float x=width/2;
        Region clip=new Region((int)(x-0.1),0, (int) x,height*2);
        region.setPath(path,clip);

        //画三条封闭的曲线
        path.lineTo(width,height);
        path.lineTo(0,height);
        path.close();
    }

    public void startAnimation(){
        animator=ValueAnimator.ofFloat(0,1);
        animator.setDuration(duration);
        //线性插值
        animator.setInterpolator(new LinearInterpolator());
        //循环执行
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float fraction=(float)valueAnimator.getAnimatedValue();
                dx= (int) (waveLength*fraction);
                //为了水能够向上面蔓延
                dy+=1;
                postInvalidate();
            }
        });
        animator.start();
    }
}
