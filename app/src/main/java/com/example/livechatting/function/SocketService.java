package com.example.livechatting.function;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.livechatting.data.Constants;
import com.example.livechatting.data.UserInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    // 서비스가 모든 클라이언트로부터 바인딩이 해제되면
    // 시스템이 서비스를 소멸시킵니다.
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class ServerThread extends Thread {

        private final String TAG = getClass().getName();
        private Socket sock;

        void turnOff() {
            try {
                if (sock.isConnected())
                    sock.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                sock = new Socket(Constants.HOST, Constants.PORT);
                BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                PrintWriter out = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()), true);
                out.println(UserInfo.num);

                String line;
                while ((line = in.readLine()) != null) {
                    Log.e(TAG, "Message :: " + line);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
