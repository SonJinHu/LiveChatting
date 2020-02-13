package com.example.livechatting.data;

import android.os.Environment;

public class Constants {
    //public static final int PORT = 5555;
    public static final int PORT = 5555;
    public static final String HOST = "115.68.223.21";
    public static final String URL = "http://115.68.223.21/";
    public static final String RTMP_BASE_URL = "rtmp://175.119.180.99:1935/LiveApp/";

    public static final String DIRECTORY_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/LiveChatting";
    public static final String HAAR_EYE_FILE = "haar_cascade_eye_tree_eyeglasses.xml";
    public static final String HAAR_FACE_FILE = "haar_cascade_frontalface_alt.xml";

    private static final String PATH_USER = "livechatting/user/";
    public static final String SIGN_IN = PATH_USER + "sign_in.php";
    public static final String CHECK_ID = PATH_USER + "check_id.php";
    public static final String CHECK_NICK = PATH_USER + "check_nick.php";
    public static final String SIGN_UP = PATH_USER + "sign_up.php";
    public static final String FRIENDS = PATH_USER + "friends.php";

    private static final String PATH_CHAT = "livechatting/chat/";
    public static final String ROOMS = PATH_CHAT + "rooms.php";
    public static final String MESSAGES = PATH_CHAT + "messages.php";
    public static final String MESSAGES_SAVE = PATH_CHAT + "messages_save.php";
    public static final String WEB_RTC_REQUEST = PATH_CHAT + "webrtc_request.php";
    public static final String WEB_RTC_CONNECT = PATH_CHAT + "webrtc_connect.php";
    public static final String WEB_RTC_DISCONNECT = PATH_CHAT + "webrtc_disconnect.php";
    public static final String BROADCAST_LIST = PATH_CHAT + "broadcast_list.php";
    public static final String BROADCAST_START = PATH_CHAT + "broadcast_start.php";
    public static final String BROADCAST_END = PATH_CHAT + "broadcast_end.php";
}