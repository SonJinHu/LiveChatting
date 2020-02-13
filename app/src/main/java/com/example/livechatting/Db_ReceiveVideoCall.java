package com.example.livechatting;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.livechatting.api.AsyncTask;
import com.example.livechatting.data.Constants;
import com.example.livechatting.function.ServiceBind;
import com.example.livechatting.function.SocketService;

import org.appspot.apprtc.ConnectActivity;

public class Db_ReceiveVideoCall extends ServiceBind {

    private String senderNum;
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;

    @Override
    public void receiveMessage(int flag, String message) {
        if (flag == SocketService.DISCONNECT) {
            finish();
            Toast.makeText(getApplicationContext(), "상대방이 영상통화 요청을 취소했습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 전체화면
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 스크린 꺼짐 방지
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.db_receive_webrtc);

        senderNum = getIntent().getStringExtra("num");
        String senderNick = getIntent().getStringExtra("nick");
        String senderImg = getIntent().getStringExtra("img");

        ImageView iv = findViewById(R.id.dc_iv_profile);
        if (senderImg == null || senderImg.equals("") || senderImg.equals("null")) {
            Glide.with(getApplicationContext())
                    .load(R.drawable.profile_default)
                    .apply(RequestOptions.circleCropTransform())
                    .into(iv);
        } else {
            Glide.with(getApplicationContext())
                    .load(Constants.URL + senderImg)
                    .placeholder(R.drawable.profile_default)
                    .error(R.drawable.profile_default)
                    .apply(RequestOptions.circleCropTransform())
                    .into(iv);
        }

        TextView tv_nick = findViewById(R.id.dc_tv_nick);
        tv_nick.setText(senderNick);

        ImageButton ib_receive = findViewById(R.id.dc_ib_receive);
        ib_receive.setOnClickListener(v -> receiveVideoCall());

        ImageButton ib_turnOff = findViewById(R.id.dc_ib_turnOff);
        ib_turnOff.setOnClickListener(v -> finish());
    }

    @Override
    protected void onStart() {
        super.onStart();
        startRing();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopRing();
        turnOffVideoCall();
        finish();
    }

    private void startRing() {
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (am != null) {
            switch (am.getRingerMode()) { // 0: 무음, 1: 진동, 2: 노말
                case AudioManager.RINGER_MODE_NORMAL:
                    mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.kakao_ring);
                    mediaPlayer.start();
                    break;
                case AudioManager.RINGER_MODE_VIBRATE:
                    long[] timings = {1000, 1000};
                    vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    if (vibrator != null) {
                        if (Build.VERSION_CODES.O <= Build.VERSION.SDK_INT) {
                            VibrationEffect effect = VibrationEffect.createWaveform(timings, 0);
                            vibrator.vibrate(effect);
                        } else
                            vibrator.vibrate(timings, 0);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void stopRing() {
        if (mediaPlayer != null) {
            // 정지
            mediaPlayer.stop();
            // 초기화
            mediaPlayer.reset();
            // 해지
            mediaPlayer.release();
            mediaPlayer = null;
        }

        if (vibrator != null) {
            vibrator.cancel();
            vibrator = null;
        }
    }

    private void receiveVideoCall() {
        int random = (int) (Math.random() * 99999999 - 10000000 + 1) + 10000000;
        String roomNameAppRTC = String.valueOf(random);

        String serverURL = Constants.URL + Constants.WEB_RTC_CONNECT;
        String postParameters = "sender_num=" + senderNum
                + "&room_name=" + roomNameAppRTC;

        AsyncTask.RequestServer async = new AsyncTask.RequestServer();
        async.setOnPostListener(result -> {
            //TODO: 마이크 퍼미션 요청
            Intent intent = new Intent(getApplicationContext(), ConnectActivity.class);
            intent.putExtra("roomName", roomNameAppRTC);
            startActivity(intent);
        });
        async.execute(serverURL, postParameters);
    }

    private void turnOffVideoCall() {
        String serverURL = Constants.URL + Constants.WEB_RTC_DISCONNECT;
        String postParameters = "opposite_user_num=" + senderNum;

        AsyncTask.RequestServer async = new AsyncTask.RequestServer();
        async.execute(serverURL, postParameters);
    }
}
