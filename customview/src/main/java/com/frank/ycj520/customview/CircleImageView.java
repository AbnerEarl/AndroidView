package com.frank.ycj520.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;

public class CircleImageView extends View{
    private Paint mPaint;
    private Bitmap imageBitmap;
    private float circleRadio;
    private int imageResourceId;
    private int mInSampleSize=1;
    private int mWidth;
    public CircleImageView(Context context, int width) {
        super(context);
        mPaint=new Paint();
        mWidth=width;
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        System.out.println("圆的历程"+"circleImageView");
        mPaint=new Paint();
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CircleImageView, 0, 0);
        System.out.println("imageResourceId="+imageResourceId);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++)
        {
            int attr = a.getIndex(i);
            /*switch (attr)
            {
                //获取图片id
                case R.styleable.circleImageView_imageSrc:
                    imageResourceId=a.getResourceId(attr,R.drawable.default_img);
                    break;
                //获取图片清晰度设置
                case R.styleable.circleImageView_inSampleSize:
                    mInSampleSize=a.getInteger(attr,1);
                    break;
            }*/

            //获取图片id
            if (attr == R.styleable.CircleImageView_imageSrc) {
                imageResourceId = a.getResourceId(attr, R.drawable.default_img);

                //获取图片清晰度设置
            } else if (attr == R.styleable.CircleImageView_inSampleSize) {
                mInSampleSize = a.getInteger(attr, 1);

            }
        }
        a.recycle();
        if(imageResourceId==0)imageResourceId=R.drawable.default_img;
    }
    @Override
    protected void onDraw(Canvas canvas) {
        System.out.println("圆的历程"+"onDraw");
        canvas.drawBitmap(imageBitmap,0,0,mPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        mWidth=widthSize;
        if(imageBitmap==null||imageBitmap.isRecycled()){
            System.out.println("圆的历程"+"imageBitmap");
            imageBitmap=getCircle(getSampleBitmap2(imageResourceId,widthSize));
        }
        setMeasuredDimension(widthSize, heightSize);
        System.out.println("圆的历程"+"onMeasure");
    }

    //动态设置图片
    public void setImageid(int imageid){
        System.out.println("圆的历程"+"setImageid");
        if(mWidth==0)imageResourceId=imageid;
        else{
            imageResourceId=imageid;
            if(!imageBitmap.isRecycled()){
                imageBitmap.recycle();
                imageBitmap=null;
            }
            imageBitmap=getCircle(getSampleBitmap2(imageResourceId,mWidth));
            postInvalidate();
        }
    }

    public void setImageUri(){

    }

    //动态设置图片清晰度
    public void setSimpleSize(int size){
        System.out.println("圆的历程"+"setSimpleSize");
        if(mWidth==0)mInSampleSize=size;
        else{
            mInSampleSize=size;
            if(!imageBitmap.isRecycled()){
                imageBitmap.recycle();
                imageBitmap=null;
            }
            imageBitmap=getCircle(getSampleBitmap2(imageResourceId,mWidth));
            postInvalidate();
        }
    }

    public Bitmap getCircle(Bitmap bitmap){
        //circleRadio圆形图片的半径
        float circleRadio;
        //bitmapSize图片的尺寸，也就是圆的直径，正方形图片的边长
        int bitmapSize;
        if(bitmap.getHeight()>bitmap.getWidth()){
            circleRadio=bitmap.getWidth()/2;
            bitmapSize=bitmap.getWidth();
        }else{
            circleRadio=bitmap.getHeight()/2;
            bitmapSize=bitmap.getHeight();
        }
        //创建一张新的bitmap，跟传入图片一样宽的正方形bitmap，
        Bitmap b=Bitmap.createBitmap(bitmapSize,bitmapSize, Bitmap.Config.ARGB_8888);
        //将图片密度修改为上面那张的图片密度
        b.setDensity(bitmap.getDensity());
        //初始化画布，并将刚才创建的bitmap给这画布，让画布画在这bitmap上面
        Canvas canvas=new Canvas(b);
        //初始化画笔
        Paint p=new Paint();
        //在画布中画一个等宽的圆
        canvas.drawCircle(circleRadio,circleRadio,circleRadio,p);
        //设置画笔属性，让画笔只在哪圆圈中画画，关于画笔属性，可以百度一下，很多，但是非常有用
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap,0,0,p);
        return  b;
    }

    /**
     * 获取特定大小缩略图
     * @param imageid 图片资源id
     * @param size 你想要获取的图片大小尺寸
     * @return
     */
    public Bitmap getSampleBitmap2(int imageid,int size){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds=true;
        BitmapFactory.decodeResource(getResources(),imageid,options);
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inSampleSize= calculateInSampleSize(options,size)*mInSampleSize;
        //设置图片可以缩小
        options.inScaled = true;
        int calsize=options.outHeight>options.outWidth?options.outWidth:options.outHeight;
        /**
         * 计算图片缩小的目标密度，在这里说一下，有一条公式：
         * 输出图片的宽高= (原图片的宽高 * (inTargetDensity / inDensity)) / inSampleSize
         * 一般来说，图片的options.inDensity默认为160
         * 所以inTargetDensity计算公式为：(希望输出的宽高*options.inDensity)/(原来图片的宽高/options.inSampleSize)
         */
        options.inTargetDensity =(size*options.inDensity)/(calsize/options.inSampleSize);
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),imageid,options);
        circleRadio=bitmap.getWidth()/2;
        return bitmap;
    }

    //谷歌源码里面的计算simplesize方法，
    public int calculateInSampleSize(BitmapFactory.Options options,
                                     int size) {
        int reqWidth,reqHeight;
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        if(options.outHeight>options.outWidth){
            reqWidth=size;
            reqHeight=size*options.outHeight/options.outWidth;
        }else{
            reqWidth=size*options.outWidth/options.outHeight;
            reqHeight=size;
        }
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}

