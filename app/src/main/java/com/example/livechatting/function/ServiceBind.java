package com.example.livechatting.function;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import androidx.appcompat.app.AppCompatActivity;

public abstract class ServiceBind extends AppCompatActivity {

    private Messenger mClientMessenger = new Messenger(new ClientHandler());
    private Messenger mServiceMessenger;
    private boolean mIsBound;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mServiceMessenger = new Messenger(service);
            mIsBound = true;
            registerClient();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mServiceMessenger = null;
            mIsBound = false;
        }
    };

    public abstract void receiveMessage(String message);

    @Override
    protected void onStart() {
        super.onStart();
        // 서비스 시작
        Intent intent = new Intent(getApplicationContext(), SocketService.class);
        bindService(intent, mConnection, BIND_AUTO_CREATE);
    }

    /**
     * Unbind service
     * 클라이언트가 하나라도 서비스에 바인딩되어 있다면 그 서비스는 소멸되지 않음
     * (예) Activity 이동 from C to D : D.onStart().bindService() → C.onStop().unbindService()
     * D가 바인딩된 후, C에서 서비스 종료를 요청하므로 서비스는 소멸되지 않음
     */
    @Override
    protected void onStop() {
        super.onStop();
        if (mIsBound) {
            if (mServiceMessenger != null)
                unregisterClient();
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    @SuppressLint("HandlerLeak")
    private class ClientHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SocketService.MESSAGE:
                    receiveMessage((String) msg.obj);
                    break;
                default:
                    super.handleMessage(msg);
            }
            registerClient();
        }
    }

    /**
     * Register client's messenger address with service
     * 바인딩된 서비스는 서버로부터 채팅 메시지(or 다른 메시지) 수신을 기다리고 있음
     * 서버로부터 메시지를 받으면, 서비스는 클라이언트가 등록한 주소로 메시지를 전달함
     * 서버가 전송한 메시지를 클라이언트가 수신받음
     * ∴ 클라이언트의 메신저 주소를 서비스에 등록해야합니다.
     */
    private void registerClient() {
        try {
            Message msg = Message.obtain(null, SocketService.MSG_REGISTER_CLIENT);
            msg.replyTo = mClientMessenger;
            mServiceMessenger.send(msg);
        } catch (RemoteException ignored) {

        }
    }

    /**
     * Unregister client's messenger address from service
     * service needs only one client's address because there's only one client bound to service.
     * 서비스에 등록된 클라이언트의 주소는 한 번만 사용가능
     * 서비스가 한 주소를 중복 사용할 가능성이 있고 그것은 오류를 불러일으킬 수 있음
     * ∴ 적절한 때에 서비스에 등록된 클라이언트의 주소를 해지해야합니다.
     */
    private void unregisterClient() {
        try {
            Message msg = Message.obtain(null, SocketService.MSG_UNREGISTER_CLIENT);
            msg.replyTo = mClientMessenger;
            mServiceMessenger.send(msg);
        } catch (RemoteException ignored) {

        }
    }
}