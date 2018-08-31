package com.yuanju.bitmapShaderDemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ImageView;


/**
 *
 *
 *
 */
public class RoundImageView extends android.support.v7.widget.AppCompatImageView {



    /**
     * 绘图的Paint
     */
    private Paint mBitmapPaint;
    /**
     * 圆角的半径
     */
    private int mRadius;
    /**
     * 3x3 矩阵，主要用于缩小放大
     */
    private Matrix mMatrix;
    /**
     * 渲染图像，使用图像为绘制图形着色
     */
    private BitmapShader mBitmapShader;
    /**
     * view的宽度
     */
    private int mWidth;
    private RectF mRoundRect;

    int radius;

    boolean isCircle;

    public RoundImageView(Context context, AttributeSet attrs) {

        super(context, attrs);
        mMatrix = new Matrix();
        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.RoundImageView);


        a.recycle();

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundImageView);
        radius = typedArray.getDimensionPixelOffset(R.styleable.RoundImageView_rad, 0);
        isCircle = typedArray.getBoolean(R.styleable.RoundImageView_circle, false);

        typedArray.recycle();
    }

    public RoundImageView(Context context) {
        this(context, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        /**
         * 如果类型是圆形，则强制改变view的宽高一致，以小值为准
         */
        if (isCircle) {
            mWidth = Math.min(getMeasuredWidth(), getMeasuredHeight());
            mRadius = mWidth / 2;
            setMeasuredDimension(mWidth, mWidth);
        }

    }

    /**
     * 初始化BitmapShader
     */
    private void setUpShader() {

        //将drawable转化成bitmap对象
        Bitmap bitmap = ((BitmapDrawable) getDrawable()).getBitmap();
        if (bitmap == null) {
            return;
        }
        // 将bmp作为着色器，就是在指定区域内绘制bmp
        mBitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        float scale = 1.0f;

        if (isCircle) {
            // 拿到bitmap宽或高的小值
            int size = Math.min(bitmap.getWidth(), bitmap.getHeight());
            scale = mWidth * 1.0f / size;
            mMatrix.setScale(scale, scale);
            mBitmapPaint.setShader(mBitmapShader);

        }
        if (radius != 0) {


            int dwidth = bitmap.getWidth();
            int vheight = getMeasuredHeight();
            int vwidth = getMeasuredWidth();
            int dheight = bitmap.getHeight();

            float dx = 0, dy = 0;

            if (dwidth * vheight > vwidth * dheight) {
                scale = (float) vheight / (float) dheight;
                dx = (vwidth - dwidth * scale) * 0.5f;
            } else {
                scale = (float) vwidth / (float) dwidth;
                dy = (vheight - dheight * scale) * 0.5f;
            }

            mMatrix.setScale(scale, scale);
            mMatrix.postTranslate((int) (dx + 0.5f), (int) (dy + 0.5f));
            mBitmapShader.setLocalMatrix(mMatrix);


            // 设置变换矩阵

            // 设置shader
            mBitmapPaint.setShader(mBitmapShader);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (getDrawable() == null) {
            return;
        }
        setUpShader();

        if (radius != 0) {

            canvas.drawRoundRect(mRoundRect, radius, radius,
                    mBitmapPaint);
        } else {

            canvas.drawCircle(mRadius, mRadius, mRadius, mBitmapPaint);

        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // 圆角图片的范围
        if (radius != 0) {
            mRoundRect = new RectF(0, 0, w, h);
        }

    }


}