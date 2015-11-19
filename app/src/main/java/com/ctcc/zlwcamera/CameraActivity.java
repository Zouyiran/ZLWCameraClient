package com.ctcc.zlwcamera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.io.ByteArrayOutputStream;

import com.ctcc.zlwcamera.tools.Config;
import com.ctcc.zlwcamera.tools.Globals;
import com.ctcc.zlwcamera.tools.HttpRequest;
import com.ctcc.zlwcamera.tools.SendImageThread;

/**
 * Created by Zouyiran on 2014/11/21.
 *
 */

public class CameraActivity extends Activity implements SurfaceHolder.Callback{

    private SurfaceHolder surfaceHolder;
    @SuppressWarnings("deprecation")
    private Camera camera;
    private String server;
    private int port;
    private int managePort;

    public static void actionStart(Context context, String server, int port, String managePort){
        Intent intent = new Intent(context,CameraActivity.class);
        intent.putExtra("server", server);
        intent.putExtra("port", port);
        intent.putExtra("manage_port", managePort);
        context.startActivity(intent);
    }

    /** ����豸�Ƿ��ṩ����ͷ */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        server = getIntent().getStringExtra("server");
        port = getIntent().getIntExtra("port", 9999);
        managePort = getIntent().getIntExtra("manage_port", 8086);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Applications.getInstance().addActivity(this);
        initSurfaceView();
    }

//    If you are using {@link android.view.SurfaceView},
//            * you will need to register a {@link SurfaceHolder.Callback} with
//    * {@link SurfaceHolder#addCallback(SurfaceHolder.Callback)} and wait for
//            * {@link SurfaceHolder.Callback#surfaceCreated(SurfaceHolder)} before
//    * calling setPreviewDisplay() or starting preview.

    private void initSurfaceView() {
        //������Ԥ�������ʵʱͼ��
        SurfaceView surfaceView = (SurfaceView) this.findViewById(R.id.camera_preview);
        surfaceHolder = surfaceView.getHolder();
        // ��װһ��SurfaceHolder.Callback�� �������������ٵײ�surfaceʱ�ܹ����֪ͨ��
        surfaceHolder.addCallback(this);
        // �ѹ��ڵ����ã����汾����3.0��Android����Ҫ
//        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

//    ����Ԥ�����沢����һ��Ҫ������ʾ��
// ��Android 2.2 (API Level 8) ��ʼ����������setDisplayOrientation() ��������תԤ�����档
// Ϊ����Ԥ����������ֻ�����ı仯���ı䣬������Ԥ�����surfaceChanged()������ʵ�֣�
// ����Camera.stopPreview()ֹͣԤ�����ı䷽�������Camera.startPreview()����Ԥ����
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if(camera == null){
            try{
                camera = Camera.open();
            }catch(Exception e){
                e.printStackTrace();
                return;
            }
        }

        /**
         * <p>Installs a callback to be invoked for every preview frame in addition
         * to displaying them on the screen.  The callback will be repeatedly called
         * for as long as preview is active.  This method can be called at any time,
         * even while preview is live.  Any other preview callbacks are
         * overridden.</p>
         *
         * <p>If you are using the preview data to create video or still images,
         * strongly consider using {@link android.media.MediaActionSound} to
         * properly indicate image capture or recording start/stop to the user.</p>
         *
         * @param cb a callback object that receives a copy of each preview frame,
         *     or null to stop receiving callbacks.
         * @see android.media.MediaActionSound
         */

        //����Ԥ���ص�
        camera.setPreviewCallback(previewCallback);
        try {
            @SuppressWarnings("deprecation")
            Camera.Parameters parameters = camera.getParameters();
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            camera.setParameters(parameters);
            // >>>>>>>>>>>>>>������ͷ���ӵ�һ��surfaceViewԤ��,׼��ʵʱԤ��
            camera.setPreviewDisplay(surfaceHolder);
            //����Ԥ��
            camera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
            //�ͷ�camera����
            camera.release();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
//        ����Ԥ���ص�ֹͣ
        camera.setPreviewCallback(null);
//        ֹͣԤ��
        camera.stopPreview();
//        �ͷ�����ͷ��Դ
        camera.release();
        camera = null;

        Runnable requestThread = new Runnable(){
            @Override
            public void run() {
                String param = String.format("deviceid=%s", Globals.deviceID);
                try {
                    String api = "http://" + server + ":" + String.valueOf(managePort) + Config.stopRecordAPI;
                    HttpRequest.sendPost(api, param);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        new Thread(requestThread).start();
    }

//    * @param cb a callback object that receives a copy of each preview frame,
//    *     or null to stop receiving callbacks.
    @SuppressWarnings("deprecation")
    private Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {
//    ����ÿһ֡�Ļ���
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
        if (camera != null) {
            Camera.Size size = camera.getParameters().getPreviewSize();
            try{
                //����image.compressToJpeg������YUV��ʽͼ������dataתΪjpg��ʽ
                YuvImage image = new YuvImage(data, ImageFormat.NV21, size.width, size.height, null);
                if(image != null){
                    ByteArrayOutputStream imagestream = new ByteArrayOutputStream();
                    image.compressToJpeg(new Rect(0, 0, size.width, size.height), 100, imagestream);

                    byte[] imageBytes = imagestream.toByteArray();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 320, 240, true);
                    ByteArrayOutputStream outstream = new ByteArrayOutputStream();
                    resizedBitmap.compress(Bitmap.CompressFormat.JPEG, Config.videoQuality, outstream);
                    outstream.flush();
                    //�����߳̽�ͼ�����ݷ��ͳ�ȥ
                    Thread th = new SendImageThread(outstream, server, port);
                    th.start();
                }
            } catch(Exception ex){
                Log.e("Sys", "Error:" + ex.getMessage());
            }
        }
        }
    };
}
