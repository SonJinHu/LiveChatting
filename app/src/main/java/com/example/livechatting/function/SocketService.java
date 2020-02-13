package com.example.livechatting.function;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.livechatting.Db_ReceiveVideoCall;
import com.example.livechatting.data.Constants;
import com.example.livechatting.data.UserInfo;

import org.appspot.apprtc.ConnectActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class SocketService extends Service {

    private final String TAG = getClass().getName();
    public static final int MSG_REGISTER_CLIENT = 0;
    public static final int MSG_UNREGISTER_CLIENT = 1;
    public static final int DISCONNECT = 2;
    public static final int MESSAGE = 3;

    private ServiceThread mThread;
    private ArrayList<Messenger> mClients = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        mThread = new ServiceThread();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // 서비스 시작
        Log.e(TAG, "onBind :: 서비스 시작");
        if (mThread.getState() == Thread.State.NEW)
            mThread.start();
        return new Messenger(new ServiceHandler()).getBinder();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 서비스 종료
        Log.e(TAG, "onDestroy :: 서비스 종료");
        if (mThread.isAlive())
            mThread.turnOff();
    }

    @SuppressLint("HandlerLeak")
    private class ServiceHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case MSG_REGISTER_CLIENT:
                    mClients.add(msg.replyTo);
                    Log.e(TAG, "client add :: size:" + mClients.size());
                    break;
                case MSG_UNREGISTER_CLIENT:
                    mClients.remove(msg.replyTo);
                    Log.e(TAG, "client remove :: size:" + mClients.size());
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    private class ServiceThread extends Thread {

        private Socket sock;

        @Override
        public void run() {
            try {
                sock = new Socket(Constants.HOST, Constants.PORT);
                BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                PrintWriter out = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()), true);
                out.println(UserInfo.num);

                String line;
                while ((line = in.readLine()) != null) {
                    Log.e(TAG, "get message from server :: " + line);
                    String[] parts = line.split(" ", 8);
                    switch (parts[2]) {
                        case "message":
                            sendMessage(SocketService.MESSAGE, line);
                            break;
                        case "webrtc_request":
                            Intent intent = new Intent(getApplicationContext(), Db_ReceiveVideoCall.class);
                            intent.putExtra("num", parts[3]);
                            intent.putExtra("nick", parts[4]);
                            intent.putExtra("img", parts[5]);
                            getApplicationContext().startActivity(intent);
                            break;
                        case "webrtc_connect":
                            intent = new Intent(getApplicationContext(), ConnectActivity.class);
                            intent.putExtra("roomName", parts[3]);
                            getApplicationContext().startActivity(intent);
                            break;
                        case "webrtc_disconnect":
                            sendMessage(SocketService.DISCONNECT, line);
                            break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        void turnOff() {
            try {
                if (sock != null && sock.isConnected())
                    sock.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void sendMessage(int flag, String message) {
            Message msg = Message.obtain(null, flag, message);
            for (int i = 0; i < mClients.size(); i++) {
                try {
                    mClients.get(i).send(msg); // Check exception
                    mClients.remove(i);
                } catch (RemoteException | IllegalStateException e) {
                    mClients.remove(i); // The client is dead. Remove it from the list.
                }
            }
        }
    }
}