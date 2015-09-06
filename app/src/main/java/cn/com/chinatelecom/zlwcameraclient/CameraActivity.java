package cn.com.chinatelecom.zlwcameraclient;

import android.app.Activity;
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

/**
 * Created by Zouyiran on 2014/11/21.
 *
 */

public class CameraActivity extends Activity implements SurfaceHolder.Callback{
    private SurfaceView surfaceView;// 显示视频的控件
    private SurfaceHolder surfaceHolder;
    private Camera camera;
    private String server;
    private int port;
    private int managePort;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);//SurfaceView
        server = getIntent().getStringExtra("server");
        port = getIntent().getIntExtra("port", 9999);
        managePort = getIntent().getIntExtra("manage_port", 8086);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initView();
    }

    private void initView() {
        surfaceView = (SurfaceView) this.findViewById(R.id.camera_preview);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if(camera == null){
            camera = Camera.open();
        }
        camera.setPreviewCallback(previewCallback);
        //camera.setDisplayOrientation(90);
        try {
            Camera.Parameters parameters = camera.getParameters();
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            camera.setParameters(parameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try{
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
            camera.release();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.setPreviewCallback(null);
        camera.stopPreview();
        camera.release();
        camera = null;

        Runnable requestThread = new Runnable(){
            @Override
            public void run() {
                // TODO Auto-generated method stub
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

    private Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
        if (camera != null) {
            Camera.Size size = camera.getParameters().getPreviewSize();
            try{
                //调用image.compressToJpeg（）将YUV格式图像数据data转为jpg格式
                YuvImage image = new YuvImage(data, ImageFormat.NV21, size.width, size.height, null);
                if(image != null){
                    ByteArrayOutputStream imagestream = new ByteArrayOutputStream();
                    image.compressToJpeg(new Rect(0, 0, size.width, size.height), 100, imagestream);
                    byte[] imageBytes = imagestream.toByteArray();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    Bitmap resized = Bitmap.createScaledBitmap(bitmap, 320, 240, true);
                    ByteArrayOutputStream outstream = new ByteArrayOutputStream();
                    resized.compress(Bitmap.CompressFormat.JPEG, Config.videoQuality, outstream);
                    outstream.flush();
                    //启用线程将图像数据发送出去
                    Thread th = new SendImageThread(outstream, server, port);
                    th.start();
                }
            } catch(Exception ex){
                Log.e("Sys","Error:"+ ex.getMessage());
            }
        }
        }
    };
}
