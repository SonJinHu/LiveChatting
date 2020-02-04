package com.example.livechatting;

import android.content.Intent;
import android.hardware.SensorManager;
import android.media.MediaActionSound;
import android.os.Bundle;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatToggleButton;

import com.example.livechatting.data.Constants;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;

public class BaA_FaceDetection extends AppCompatActivity {

    private final String TAG = getClass().getName();
    //영상에 검출된 얼굴 위치, 눈 위치를 그려넣는 코드와
    //최종 영상을 저장하는 코드간에 동기화를 맞추기 위해서 세마포어를 사용
    private final Semaphore writeLock = new Semaphore(1);

    private OrientationEventListener mOrientationEventListener;
    private BaseLoaderCallback mLoaderCallback;
    private CameraBridgeViewBase mOpenCvCameraView;
    private int orientation;
    private long cascadeClassifier_face = 0;
    private long cascadeClassifier_eye = 0;

    static {
        System.loadLibrary("opencv_java4");
        System.loadLibrary("native-lib");
    }

    //public native String stringFromJNI();
    //public native void ConvertRGBtoGray(long matAddrInput, long matAddrResult);
    public native long loadCascade(String cascadeFileName);

    public native int detect(long cascadeClassifier_face, long cascadeClassifier_eye, long matAddrInput, long matAddrResult);

    public native String saveFaces(long cascadeClassifier_face, long matAddrInput, long matAddrResult);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.baa_face_detection);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        ImageButton mTakePicture = findViewById(R.id.baa_ib_capture);
        AppCompatToggleButton mSwitchCamera = findViewById(R.id.baa_tb_switch);
        mSwitchCamera.setOnCheckedChangeListener((buttonView, isChecked) -> switchCamera(isChecked));

        mOrientationEventListener = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int orientation) {
                if (0 <= orientation && orientation < 45
                        || 315 <= orientation && orientation < 360) // 0˚ (portrait)
                    BaA_FaceDetection.this.orientation = 1;

                if (45 <= orientation && orientation < 135) // 90˚ (reverse landscape)
                    BaA_FaceDetection.this.orientation = 2;

                if (135 <= orientation && orientation < 225) // 180˚ (reverse portrait)
                    BaA_FaceDetection.this.orientation = 3;

                if (225 <= orientation && orientation < 315) // 270˚ (landscape)
                    BaA_FaceDetection.this.orientation = 4;
            }
        };

        mLoaderCallback = new BaseLoaderCallback(this) {
            @Override
            public void onManagerConnected(int status) {
                if (status == LoaderCallbackInterface.SUCCESS) {
                    mOpenCvCameraView.enableView();
                } else {
                    super.onManagerConnected(status);
                }
            }
        };

        mOpenCvCameraView = findViewById(R.id.baa_camera_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCameraIndex(1); // 0: back-camera, 1: front-camera
        mOpenCvCameraView.setCvCameraViewListener(new CameraBridgeViewBase.CvCameraViewListener2() {
            @Override
            public void onCameraViewStarted(int width, int height) {

            }

            @Override
            public void onCameraViewStopped() {

            }

            @Override
            public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
                Mat matResult = null;
                try {
                    writeLock.acquire();

                    Mat matInput = inputFrame.rgba();
                    Point center = new Point(matInput.cols() / 2, matInput.rows() / 2);
                    switch (orientation) {
                        default:
                        case 1: // 0˚ (portrait)
                            matResult = Imgproc.getRotationMatrix2D(center, 90, 1.0);
                            break;
                        case 2: // 90˚ (reverse landscape)
                            matResult = Imgproc.getRotationMatrix2D(center, 180, 1.0);
                            break;
                        case 4: // 270˚ (landscape)
                            matResult = Imgproc.getRotationMatrix2D(center, 0, 1.0);
                            break;
                    }

                    Imgproc.warpAffine(matInput, matInput, matResult, matInput.size());
                    if (!mSwitchCamera.isChecked()) { // front-camera
                        Core.flip(matInput, matInput, 1); // 0: x축, 1: y축, -1: x and y
                    } else { // back-camera
                        if (orientation == 1)
                            Core.flip(matInput, matInput, -1);
                    }

                    // Number of faces detected
                    int countFaceDetected = detect(cascadeClassifier_face, cascadeClassifier_eye,
                            matInput.getNativeObjAddr(), matResult.getNativeObjAddr());

                    // The number of faces detected must be at least one to take a picture
                    if (0 < countFaceDetected) {
                        Mat finalMatResult = matResult;
                        mTakePicture.setBackgroundResource(android.R.color.holo_red_dark);
                        mTakePicture.setOnClickListener(v -> takePicture(matInput, finalMatResult));
                    } else {
                        mTakePicture.setBackgroundResource(android.R.color.darker_gray);
                        mTakePicture.setOnClickListener(null);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                writeLock.release();
                return matResult;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        List<? extends CameraBridgeViewBase> cameraViews = Collections.singletonList(mOpenCvCameraView);
        for (CameraBridgeViewBase cameraBridgeViewBase : cameraViews) {
            if (cameraBridgeViewBase != null) {
                cameraBridgeViewBase.setCameraPermissionGranted();
                readCascadeFile();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mOrientationEventListener.enable();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "onResume :: Internal OpenCV library not found.");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "onResume :: OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mOrientationEventListener.canDetectOrientation())
            mOrientationEventListener.disable();

        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mOrientationEventListener.canDetectOrientation())
            mOrientationEventListener.disable();

        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    private void takePicture(Mat matInput, Mat matResult) {
        AppCompatToggleButton mCameraSound = findViewById(R.id.baa_tb_sound);
        if (mCameraSound.isChecked()) {
            MediaActionSound sound = new MediaActionSound();
            sound.play(MediaActionSound.SHUTTER_CLICK);
        } else {
            Toast.makeText(getApplicationContext(), "사진을 찍었습니다", Toast.LENGTH_SHORT).show();
        }

        String fileTimeStamp = null;
        try {
            writeLock.acquire();
            Imgproc.cvtColor(matInput, matInput, Imgproc.COLOR_BGR2RGB, 4);
            fileTimeStamp = saveFaces(cascadeClassifier_face, matInput.getNativeObjAddr(), matResult.getNativeObjAddr());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        writeLock.release();

        Intent intent = new Intent(this, BaB_PickPicture.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        intent.putExtra("timeStamp", fileTimeStamp);
        startActivity(intent);
        finish();
    }

    private void switchCamera(boolean isChecked) {
        mOpenCvCameraView.disableView();
        if (isChecked) {
            mOpenCvCameraView.setCameraIndex(0); // front-camera(1),  back-camera(0)
            onResume();
        } else {
            mOpenCvCameraView.setCameraIndex(1);
            onResume();
        }
    }

    private void readCascadeFile() {
        copyCascadeFile(Constants.HAAR_EYE_FILE);
        copyCascadeFile(Constants.HAAR_FACE_FILE);
        cascadeClassifier_eye = loadCascade(Constants.DIRECTORY_PATH + File.separator + Constants.HAAR_EYE_FILE);
        cascadeClassifier_face = loadCascade(Constants.DIRECTORY_PATH + File.separator + Constants.HAAR_FACE_FILE);
    }

    private void copyCascadeFile(String fileName) {
        String pathDir = Constants.DIRECTORY_PATH + File.separator + fileName;
        try {
            InputStream inputStream = getAssets().open(fileName);
            OutputStream outputStream = new FileOutputStream(pathDir);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            inputStream.close();
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}