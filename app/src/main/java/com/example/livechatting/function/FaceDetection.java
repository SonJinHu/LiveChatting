package com.example.livechatting.function;

import android.annotation.SuppressLint;

import androidx.appcompat.app.AppCompatActivity;

import com.example.livechatting.data.Constant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

@SuppressLint("Registered")
public class FaceDetection extends AppCompatActivity {

    public long cascadeClassifier_face = 0;
    public long cascadeClassifier_eye = 0;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("opencv_java4");
        System.loadLibrary("native-lib");
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    //public native String stringFromJNI();
    //public native void ConvertRGBtoGray(long matAddrInput, long matAddrResult);
    public native long loadCascade(String cascadeFileName);

    public native void detect(long cascadeClassifier_face, long cascadeClassifier_eye, long matAddrInput, long matAddrResult);

    public void read_cascade_file() {
        copyFile(Constant.HAAR_EYE_FILE);
        copyFile(Constant.HAAR_FACE_FILE);
        cascadeClassifier_eye = loadCascade(Constant.DIRECTORY_PATH + File.separator + Constant.HAAR_EYE_FILE);
        cascadeClassifier_face = loadCascade(Constant.DIRECTORY_PATH + File.separator + Constant.HAAR_FACE_FILE);
    }

    private void copyFile(String fileName) {
        String pathDir = Constant.DIRECTORY_PATH + File.separator + fileName;
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
