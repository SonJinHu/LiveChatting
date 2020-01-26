package com.example.livechatting.data;

import android.os.Environment;

public class Constant {
    public static final String URL = "http://115.68.223.21/";
    public static final String DIRECTORY_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/LiveChatting";
    public static final String HAAR_EYE_FILE = "haar_cascade_eye_tree_eyeglasses.xml";
    public static final String HAAR_FACE_FILE = "haar_cascade_frontalface_alt.xml";

    private static final String PATH_SIGN = "LiveChatting/Sign/";
    public static final String SIGN_IN = PATH_SIGN + "sign_in.php";
    public static final String CHECK_ID = PATH_SIGN + "check_id.php";
    public static final String CHECK_NICK = PATH_SIGN + "check_nick.php";
    public static final String SIGN_UP = PATH_SIGN + "sign_up.php";
}