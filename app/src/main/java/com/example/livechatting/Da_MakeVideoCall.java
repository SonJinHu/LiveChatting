package com.example.livechatting;

import android.media.MediaPlayer;
import android.os.Bundle;
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
import com.example.livechatting.data.UserInfo;
import com.example.livechatting.function.ServiceBind;
import com.example.livechatting.function.SocketService;

public class Da_MakeVideoCall extends ServiceBind {

    private String receiverNum;
    private MediaPlayer mediaPlayer;

    @Override
    public void receiveMessage(int flag, String message) {
        if (flag == SocketService.DISCONNECT) {
            finish();
            Toast.makeText(getApplicationContext(), "상대방이 영상통화 요청을 거절했습니다.", Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.da_make_webrtc);

        receiverNum = getIntent().getStringExtra("num");
        String receiverNick = getIntent().getStringExtra("nick");
        String receiverImg = getIntent().getStringExtra("img");

        ImageView iv = findViewById(R.id.db_iv_profile);
        if (receiverImg == null || receiverImg.equals("") || receiverImg.equals("null")) {
            Glide.with(getApplicationContext())
                    .load(R.drawable.profile_default)
                    .apply(RequestOptions.circleCropTransform())
                    .into(iv);
        } else {
            Glide.with(getApplicationContext())
                    .load(Constants.URL + receiverImg)
                    .apply(RequestOptions.circleCropTransform())
                    .placeholder(R.drawable.profile_default)
                    .error(R.drawable.profile_default)
                    .into(iv);
        }

        TextView tv_nick = findViewById(R.id.db_tv_nick);
        tv_nick.setText(receiverNick);

        ImageButton ib_endCall = findViewById(R.id.db_ib_endCall);
        ib_endCall.setOnClickListener(v -> finish());
    }

    @Override
    protected void onStart() {
        super.onStart();
        startRing();
        makeVideoCall();
    }

    /**
     * onStop()에서 finish() 수행
     * 상대방에게 영상통화를 요청하는 중에 홈, 전원버튼 또는 뒤로가기 버튼 등을 누르거나
     * 다른 여러 상황에 의해 영상통화 요청 화면을 벗어나는 경우가 있을 수 있음
     * 사용자의 의도와 다를 수 있지만, 이러한 경우 영상통화 요청 중지로 간주
     * ∴ 화면을 벗어나는 즉시, 영상통화 요청 화면을 종료합니다.
     */
    @Override
    protected void onStop() {
        super.onStop();
        stopRing();
        endVideoCall();
        finish();
    }

    private void startRing() {
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.kakao_ring);
        mediaPlayer.start();
    }

    private void stopRing() {
        // 정지
        mediaPlayer.stop();
        // 초기화
        mediaPlayer.reset();
        // 해지
        mediaPlayer.release();
        mediaPlayer = null;
    }

    private void makeVideoCall() {
        String serverURL = Constants.URL + Constants.WEB_RTC_REQUEST;
        String postParameters = "receiver_num=" + receiverNum
                + "&sender_num=" + UserInfo.num
                + "&sender_nick=" + UserInfo.nick
                + "&sender_img=" + UserInfo.img;

        AsyncTask.RequestServer async = new AsyncTask.RequestServer();
        async.execute(serverURL, postParameters);
    }

    private void endVideoCall() {
        String serverURL = Constants.URL + Constants.WEB_RTC_DISCONNECT;
        String postParameters = "opposite_user_num=" + receiverNum;

        AsyncTask.RequestServer async = new AsyncTask.RequestServer();
        async.execute(serverURL, postParameters);
    }
}