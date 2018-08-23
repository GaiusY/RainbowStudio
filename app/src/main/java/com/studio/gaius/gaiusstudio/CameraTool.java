package com.studio.gaius.gaiusstudio;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;

/**
 * Create by yang on 2018/8/23
 * 拍照处理类
 */
public class CameraTool {

    private static final String TAG = "CameraTool";
    public static final Uri IMAGE_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    public static final String PATH = Environment.getExternalStorageDirectory().toString() + "/1_IVMCC_Media/picture/";
    private Camera camera;
    private int cameraId;
    //    private OnCameraStatusListener listener;
    private boolean release = false;

    public CameraTool() {
    }

    public void tackPicture(Camera camera) {
        if (null != camera) {
            this.camera = camera;
            camera.startPreview();
            camera.takePicture(null, null, JpegPicCallback);
        }
    }

    public void tackPicOnVideo(Camera camera) {

    }

    /**
     * 获取摄像头实例
     */
    public Camera getCameraInstance() {
        Camera c = null;
        try {
            int cameraCount;
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            cameraCount = Camera.getNumberOfCameras();

            for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
                Camera.getCameraInfo(camIdx, cameraInfo);
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    try {
                        c = Camera.open(camIdx);   //打开后置摄像头
                        cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
                    } catch (RuntimeException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (c == null) {
                c = Camera.open(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        release = false;
        return c;
    }

    /**
     * 生成指定的相机参数
     */
    public Camera.Parameters generateDefaultParams(Camera camera) {
        Camera.Parameters parameters = camera.getParameters();
        if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE); //连续对焦
        }
//        camera.cancelAutoFocus();//自动对焦
        parameters.setPictureFormat(PixelFormat.JPEG);
        parameters.setJpegQuality(100);//照片质量
        if (cameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
            camera.setDisplayOrientation(0);
//            parameters.setRotation(90);
        }
        return parameters;
    }

    /**
     * 处理原始数据，加水印
     */
    private Camera.PictureCallback RawPicCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
//            new SaveImageTask().execute(data);
            int w = 1280;
            int h = 720;
            Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ALPHA_8);
            Buffer buf = ByteBuffer.wrap(data);
            bitmap.copyPixelsFromBuffer(buf);
            Bitmap bitmap2 = CameraUtil.getWatermarkImg(bitmap);

            long dateTaken = System.currentTimeMillis();// 系统时间
            String filename = DateFormat.format("yyyyMMdd_HH.mm.ss", dateTaken).toString() + ".jpg";// 图像名称
            Uri uri = insertImage(App.getApplication().getContentResolver(), filename, dateTaken, PATH, filename, bitmap2, data);
            Log.i(TAG, "====picture uri==== " + uri);
        }
    };

    /**
     * 创建一个PictureCallback对象，处理相片存储
     */
    private Camera.PictureCallback JpegPicCallback = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {// 该方法用于处理拍摄后的照片数据
            try {
                camera.stopPreview(); // 停止照片拍摄
                if (!isRelease()) {
                    releaseCameraPic();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            onCameraStopped(data);
            camera.startPreview();
//            if (null != listener) {
//                listener.onCameraStopped(data); // 调用结束事件
//            }
        }
    };

    /**
     * 拍照结束事件
     */
    private void onCameraStopped(byte[] data) {
        Bitmap bitmap1 = BitmapFactory.decodeByteArray(data, 0, data.length);// 创建图像
        Bitmap bitmap2 = CameraUtil.rotate(bitmap1, 0);
        Bitmap bitmap = CameraUtil.getWatermarkImg(bitmap2); // 加水印

        long dateTaken = System.currentTimeMillis();// 系统时间
        String filename = DateFormat.format("yyyyMMdd_HH.mm.ss", dateTaken).toString() + ".jpg";// 图像名称
        Uri uri = insertImage(App.getApplication().getContentResolver(), filename, dateTaken, PATH, filename, bitmap, data);
        Log.i(TAG, "====picture uri==== " + uri);
    }

//    /**
//     * 相机拍照监听接口
//     */
//    public interface OnCameraStatusListener {
//        // 相机拍照结束事件
//        void onCameraStopped(byte[] data);
//    }

    /**
     * 存储图像并将信息添加入媒体数据库
     */
    private Uri insertImage(ContentResolver cr, String name, long dateTaken,
                            String directory, String filename, Bitmap source, byte[] jpegData) {
//        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//            FileOutputStream fos = null;
//            String filePath = Environment.getExternalStorageDirectory() + File.separator +
//                    DIRNAME + File.separator + filename;
//            File imgFile = new File(filePath);
//            if (!imgFile.getParentFile().exists()) {
//                imgFile.getParentFile().mkdirs();
//            }
//            try {
//                if (!imgFile.exists()) {
//                    imgFile.createNewFile();
//                }
//                fos = new FileOutputStream(imgFile);
//                source.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//                fos.write(jpegData);
//                fos.flush();
//            } catch (IOException e) {
//                e.printStackTrace();
//            } finally {
//                try {
//                    if (null != fos) {
//                        fos.close();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//            Uri uri = Uri.fromFile(imgFile);
//            intent.setData(uri);
//            return uri;
//        } else {
        OutputStream outputStream = null;
        String filePath = directory + filename;
        try {
            File dir = new File(directory);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(directory, filename);
            if (file.createNewFile()) {
                outputStream = new FileOutputStream(file);
                if (source != null) {
                    source.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                } else {
                    outputStream.write(jpegData);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
        ContentValues values = new ContentValues(7);
        values.put(MediaStore.Images.Media.TITLE, name);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, filename);
        values.put(MediaStore.Images.Media.DATE_TAKEN, dateTaken);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.DATA, filePath);
        return cr.insert(IMAGE_URI, values);
//        }
    }


    /**
     * 打开闪光灯
     */
    public void openLight() {
        if (camera != null) {
            Camera.Parameters parameter;
            parameter = camera.getParameters();
            parameter.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(parameter);
        }
    }

    /**
     * 关闭闪光灯
     */
    public void offLight() {
        if (camera != null) {
            Camera.Parameters parameter;
            parameter = camera.getParameters();
            parameter.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(parameter);
        }
    }

    /**
     * 释放相机资源
     */
    public void releaseCameraPic() {
        if (camera != null) {
            camera.release();
        }
        camera = null;
        release = true;
    }

    /**
     * 现在是否处于释放状态
     *
     * @return true释放，false没释放
     */
    public boolean isRelease() {
        return release;
    }


    private class SaveImageTask extends AsyncTask<byte[], Void, Void> {

        @Override
        protected Void doInBackground(byte[]... data) {
            int w = 1280;
            int h = 720;
            Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ALPHA_8);
            Buffer buf = ByteBuffer.wrap(data[0]);
            bitmap.copyPixelsFromBuffer(buf);
            Bitmap bitmap2 = CameraUtil.getWatermarkImg(bitmap);

            long dateTaken = System.currentTimeMillis();// 系统时间
            String filename = DateFormat.format("yyyyMMdd_HH.mm.ss", dateTaken).toString() + ".jpg";// 图像名称
            Uri uri = insertImage(App.getApplication().getContentResolver(), filename, dateTaken, PATH, filename, bitmap2, data[0]);
            Log.i(TAG, "====picture uri==== " + uri);
            return null;
        }

    }

}
