package com.example.tracker;

import android.app.Service;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.StrictMode;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CapPhoto extends Service {
    private SurfaceHolder sHolder;
    private Camera mCamera;
    Camera.PictureCallback mCall = new Camera.PictureCallback() {
        public void onPictureTaken(final byte[] data, Camera camera) {

            FileOutputStream outStream = null;
            try {

                File sd = new File(Environment.getExternalStorageDirectory(), "A");
                if (!sd.exists()) {
                    sd.mkdirs();
                    Log.i("FO", "folder" + Environment.getExternalStorageDirectory());
                }

                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                String tar = (sdf.format(cal.getTime()));

                outStream = new FileOutputStream(sd + tar + ".jpg");
                outStream.write(data);
                outStream.close();

                Log.i("CAM", data.length + " byte written to:" + sd + tar + ".jpg");
                camkapa(sHolder);
                //Sending Mail without Attachment
                //mailing();
                //Sending Mail With Attachment
                mailing("" + sd + tar + ".jpg", "Photo");

            } catch (FileNotFoundException e) {
                Log.d("CAM", e.getMessage());
            } catch (IOException e) {
                Log.d("CAM", e.getMessage());
            }
        }
    };
    private Parameters parameters;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("CAM", "start");

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        Thread myThread = null;


    }

    @Override
    public void onStart(Intent intent, int startId) {

        super.onStart(intent, startId);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            if (Camera.getNumberOfCameras() >= 2) {

                mCamera = Camera.open(CameraInfo.CAMERA_FACING_FRONT);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            if (Camera.getNumberOfCameras() < 2) {

                mCamera = Camera.open();
            }
        }
        SurfaceView sv = new SurfaceView(getApplicationContext());


        try {
            mCamera.setPreviewDisplay(sv.getHolder());
            parameters = mCamera.getParameters();
            mCamera.setParameters(parameters);
            mCamera.startPreview();

            mCamera.takePicture(null, null, mCall);
        } catch (IOException e) {
            e.printStackTrace();
        }

        sHolder = sv.getHolder();
        sHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void camkapa(SurfaceHolder sHolder) {

        if (null == mCamera)
            return;
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
        Log.i("CAM", " closed");
    }

    /*public void mailing() {
        try {
            GMailSender sender = new GMailSender("rahulnamilakonda100@gmail.com", "nk santhu");
            sender.sendMail("This is Subject",
                    "This is Body",
                    "rahulnamilakonda100@gmail.com",
                    "namilakondasanthu@gmail.com");
        } catch (Exception e) {
            Log.e("SendMail", e.getMessage(), e);
        }
    }*/
    public void mailing(String filename, String subject) {
        try {
            GMailSender sender = new GMailSender("rahulnamilakonda100@gmail.com", "nk santhu");
            GMailSender.MailSend mailSend = sender.new MailSend();
            mailSend.fileName = filename;
            mailSend.execute();
            Log.d("C", "" + filename);
        } catch (Exception e) {
            Log.e("SendMail", e.getMessage(), e);
        }
    }
}