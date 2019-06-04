package com.studio.gaius.gaiusstudio.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.DisplayMetrics;

import com.studio.gaius.gaiusstudio.global.App;

/**
 * Create by yang on 2018/8/23
 * 相机工具类
 */
public class CameraUtil {

    public static DisplayMetrics getScreenWH(Context context) {
        DisplayMetrics dMetrics = new DisplayMetrics();
        dMetrics = context.getResources().getDisplayMetrics();
        return dMetrics;
    }


    /**
     * 计算焦点及测光区域
     */
    public static Rect calculateTapArea(int focusWidth, int focusHeight, float areaMultiple, float x, float y,
                                        int previewLeft, int previewRight, int previewTop, int previewBottom) {
        int areaWidth = (int) (focusWidth * areaMultiple);
        int areaHeight = (int) (focusHeight * areaMultiple);
        int centerX = (previewLeft + previewRight) / 2;
        int centerY = (previewTop + previewBottom) / 2;
        double unitX = ((double) previewRight - (double) previewLeft) / 2000;
        double unitY = ((double) previewBottom - (double) previewTop) / 2000;
        int left = clamp((int) (((x - areaWidth / 2) - centerX) / unitX), -1000, 1000);
        int top = clamp((int) (((y - areaHeight / 2) - centerY) / unitY), -1000, 1000);
        int right = clamp((int) (left + areaWidth / unitX), -1000, 1000);
        int bottom = clamp((int) (top + areaHeight / unitY), -1000, 1000);

        return new Rect(left, top, right, bottom);
    }

    public static int clamp(int x, int min, int max) {
        if (x > max) {
            return max;
        }
        if (x < min) {
            return min;
        }
        return x;
    }

    /**
     * 检测摄像头设备是否可用
     */
    public static boolean checkCameraHardware(Context context) {
        if (context != null && context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Bitmap旋转
     */
    public static Bitmap rotate(Bitmap b, int degrees) {
        if (degrees != 0 && b != null) {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) b.getWidth() / 2, (float) b.getHeight() / 2);
            try {
                Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);
                if (b != b2) {
                    b.recycle();
                    b = b2;
                }
            } catch (OutOfMemoryError ex) {
                ex.printStackTrace();
            }
        }
        return b;
    }

    public static final int getHeightInPx(Context context) {
        final int height = context.getResources().getDisplayMetrics().heightPixels;
        return height;
    }

    public static final int getWidthInPx(Context context) {
        final int width = context.getResources().getDisplayMetrics().widthPixels;
        return width;
    }

    public static Bitmap getWatermarkImg(Bitmap bitmap) {
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        Bitmap bitmap = BitmapFactory.decodeFile(path);
        String mark = "天一智联";
        Context context = App.getApplication();
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint();
        paint.setDither(true); //获取跟清晰的图像采样
        paint.setFilterBitmap(true);//过滤一些

        paint.setColor(Color.parseColor("#CC0001")); //水印颜色
        paint.setTextSize(150); //水印文字大小
        paint.setAntiAlias(true);

//        paint.setStrokeWidth(3);
//        paint.setAlpha(15);
//        paint.setStyle(Paint.Style.STROKE); //空心
//        paint.setShadowLayer(1f, 0f, 3f, Color.LTGRAY);

        canvas.drawBitmap(bitmap, 0, 0, paint);
        canvas.drawText(mark, width - dp2px(context, 820), height - dp2px(context, 60), paint);
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        return bmp;
    }

    public static int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

}

