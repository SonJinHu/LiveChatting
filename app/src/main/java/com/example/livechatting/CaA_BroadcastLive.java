package com.example.livechatting;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.livechatting.broadcast.liveVideoBroadcaster.CameraResolutionsFragment;
import com.example.livechatting.data.Constants;
import com.example.livechatting.data.UserInfo;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import io.antmedia.android.broadcaster.ILiveVideoBroadcaster;
import io.antmedia.android.broadcaster.LiveVideoBroadcaster;
import io.antmedia.android.broadcaster.utils.Resolution;

public class CaA_BroadcastLive extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = CaA_BroadcastLive.class.getSimpleName();

    private String roomName;
    private Intent mLiveVideoBroadcasterServiceIntent;
    private ViewGroup mRootView;
    private View view_before;
    private View view_start;
    private TextView mStreamLiveStatus;
    private Timer mTimer;
    private long mElapsedTime;
    private TimerHandler mTimerHandler;
    private CameraResolutionsFragment mCameraResolutionsDialog;
    private GLSurfaceView mGLView;
    private ILiveVideoBroadcaster mLiveVideoBroadcaster;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            LiveVideoBroadcaster.LocalBinder binder = (LiveVideoBroadcaster.LocalBinder) service;
            Log.e(TAG, "onServiceConnected");
            if (mLiveVideoBroadcaster == null) {
                mLiveVideoBroadcaster = binder.getService();
                mLiveVideoBroadcaster.init(CaA_BroadcastLive.this, mGLView);
                mLiveVideoBroadcaster.setAdaptiveStreaming(true);
            }
            // Camera.CameraInfo.CAMERA_FACING_FRONT: 1
            mLiveVideoBroadcaster.openCamera(1);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mLiveVideoBroadcaster = null;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.caa_broadcast);

        // binding on resume not to having leaked service connection
        mLiveVideoBroadcasterServiceIntent = new Intent(this, LiveVideoBroadcaster.class);
        startService(mLiveVideoBroadcasterServiceIntent); // this makes service do its job until done

        mRootView = findViewById(R.id.cca_view_root);
        view_before = findViewById(R.id.cca_view_ready);
        view_before.setVisibility(View.VISIBLE);
        view_start = findViewById(R.id.cca_view_broadcasting);
        view_start.setVisibility(View.GONE);

        // Configure the GLSurfaceView. This will start the Renderer thread, with an appropriate EGL activity.
        mGLView = findViewById(R.id.cca_glSurfaceView);
        mGLView.setEGLContextClientVersion(2);

        // 방송 시작 전 화면
        findViewById(R.id.cca_ib_exit).setOnClickListener(this);
        findViewById(R.id.cca_ib_cameraSwitch_ready).setOnClickListener(this);
        findViewById(R.id.cca_ib_resolutions).setOnClickListener(this);
        findViewById(R.id.cca_btn_startBroadcast).setOnClickListener(this);

        // 방송 시작 후 화면
        mStreamLiveStatus = findViewById(R.id.cca_tv_time);
        //TextView tv_playerCount = findViewById(R.id.player_tv_count);
        findViewById(R.id.cca_ib_cameraSwitch_broadcasting).setOnClickListener(this);
        findViewById(R.id.cca_tv_endBroadcast).setOnClickListener(this);

        //RecyclerView recycler = findViewById(R.id.broadcaster_recyclerView);
        //EditText edit = findViewById(R.id.broadcaster_et_input);
        //ImageView send = findViewById(R.id.broadcaster_iv_send);
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(mLiveVideoBroadcasterServiceIntent, mConnection, 0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLiveVideoBroadcaster.pause();
        if (mCameraResolutionsDialog != null && mCameraResolutionsDialog.isVisible())
            mCameraResolutionsDialog.dismiss();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(mConnection);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE || newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
            mLiveVideoBroadcaster.setDisplayOrientation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LiveVideoBroadcaster.PERMISSIONS_REQUEST: {
                if (mLiveVideoBroadcaster.isPermissionGranted()) {
                    // Camera.CameraInfo.CAMERA_FACING_BACK: 0
                    mLiveVideoBroadcaster.openCamera(0);
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) ||
                            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
                        mLiveVideoBroadcaster.requestPermission();
                    } else {
                        new AlertDialog.Builder(CaA_BroadcastLive.this)
                                .setTitle(R.string.permission)
                                .setMessage(getString(R.string.app_doesnot_work_without_permissions))
                                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                                    try {
                                        //Open the specific App Info page:
                                        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                                        startActivity(intent);
                                    } catch (ActivityNotFoundException e) {
                                        //e.printStackTrace();
                                        //Open the generic Apps page:
                                        Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                                        startActivity(intent);
                                    }
                                })
                                .show();
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cca_ib_exit:
                finish();
                break;
            case R.id.cca_ib_cameraSwitch_ready:
            case R.id.cca_ib_cameraSwitch_broadcasting:
                if (mLiveVideoBroadcaster != null)
                    mLiveVideoBroadcaster.changeCamera();
                break;
            case R.id.cca_ib_resolutions:
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                Fragment fragmentDialog = getSupportFragmentManager().findFragmentByTag("dialog");
                if (fragmentDialog != null)
                    ft.remove(fragmentDialog);

                ArrayList<Resolution> sizeList = mLiveVideoBroadcaster.getPreviewSizeList();
                if (sizeList != null && sizeList.size() > 0) {
                    mCameraResolutionsDialog = new CameraResolutionsFragment();
                    mCameraResolutionsDialog.setCameraResolutions(sizeList, mLiveVideoBroadcaster.getPreviewSize());
                    mCameraResolutionsDialog.show(ft, "resolution_dialog");
                } else
                    Snackbar.make(mRootView, "No resolution available", Snackbar.LENGTH_LONG).show();
                break;
            case R.id.cca_btn_startBroadcast:
                if (mLiveVideoBroadcaster != null) {
                    if (!mLiveVideoBroadcaster.isConnected()) {
                        roomName = UserInfo.num + "_" + System.currentTimeMillis();
                        new StartBroadcasting().execute(Constants.RTMP_BASE_URL + roomName);
                    } else
                        Snackbar.make(mRootView, R.string.streaming_not_finished, Snackbar.LENGTH_LONG).show();
                } else
                    Snackbar.make(mRootView, R.string.oopps_shouldnt_happen, Snackbar.LENGTH_LONG).show();
                break;
            case R.id.cca_tv_endBroadcast:
                stopBroadcasting();
                break;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class StartBroadcasting extends AsyncTask<String, String, Boolean> {

        ContentLoadingProgressBar progressBar;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar = new ContentLoadingProgressBar(CaA_BroadcastLive.this);
            progressBar.show();
        }

        @Override
        protected Boolean doInBackground(String... urls) {
            return mLiveVideoBroadcaster.startBroadcasting(urls[0]);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            progressBar.hide();
            if (result) {
                // 방송이 성공적으로 시작됨
                view_before.setVisibility(View.GONE);
                view_start.setVisibility(View.VISIBLE);
                startTimer();

                String url = Constants.URL + Constants.BROADCAST_START;
                String param = "broadcasterNum=" + UserInfo.num + "&roomName=" + roomName;
                com.example.livechatting.api.AsyncTask.RequestServer async = new com.example.livechatting.api.AsyncTask.RequestServer();
                async.execute(url, param);
            } else
                Snackbar.make(mRootView, R.string.stream_not_started, Snackbar.LENGTH_LONG).show();
        }
    }

    public void stopBroadcasting() {
        view_before.setVisibility(View.VISIBLE);
        view_start.setVisibility(View.GONE);
        stopTimer();
        mLiveVideoBroadcaster.stopBroadcasting();

        String url = Constants.URL + Constants.BROADCAST_END;
        String param = "broadcasterNum=" + UserInfo.num;
        com.example.livechatting.api.AsyncTask.RequestServer async = new com.example.livechatting.api.AsyncTask.RequestServer();
        async.execute(url, param);
    }

    public void startTimer() {
        if (mTimer == null)
            mTimer = new Timer();

        mTimerHandler = new TimerHandler();
        mElapsedTime = 0;
        mTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                mElapsedTime += 1; // increase every sec
                mTimerHandler.obtainMessage(TimerHandler.INCREASE_TIMER).sendToTarget();
                if (mLiveVideoBroadcaster == null || !mLiveVideoBroadcaster.isConnected()) {
                    mTimerHandler.obtainMessage(TimerHandler.CONNECTION_LOST).sendToTarget();
                }
            }
        }, 0, 1000);
    }

    public void stopTimer() {
        if (mTimer != null)
            mTimer.cancel();
        mTimer = null;
        mElapsedTime = 0;
    }

    public void setResolution(Resolution size) {
        mLiveVideoBroadcaster.setResolution(size);
    }

    @SuppressLint("HandlerLeak")
    private class TimerHandler extends Handler {

        static final int INCREASE_TIMER = 1;
        static final int CONNECTION_LOST = 2;

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case INCREASE_TIMER:
                    String timerStr = getString(R.string.live_indicator) + " - " + getDurationString((int) mElapsedTime);
                    mStreamLiveStatus.setText(timerStr);
                    break;
                case CONNECTION_LOST:
                    stopBroadcasting();
                    new AlertDialog.Builder(CaA_BroadcastLive.this)
                            .setMessage(R.string.broadcast_connection_lost)
                            .setPositiveButton(android.R.string.yes, null)
                            .show();
                    break;
            }
        }

        private String getDurationString(int seconds) {
            // there is an codec problem and duration is not set correctly, so display meaningful string
            if (seconds < 0 || seconds > 2000000)
                seconds = 0;
            int hours = seconds / 3600;
            int minutes = (seconds % 3600) / 60;
            seconds = seconds % 60;

            if (hours == 0)
                return twoDigitString(minutes) + " : " + twoDigitString(seconds);
            else
                return twoDigitString(hours) + " : " + twoDigitString(minutes) + " : " + twoDigitString(seconds);
        }

        private String twoDigitString(int number) {
            if (number == 0)
                return "00";

            if (number / 10 == 0)
                return "0" + number;

            return String.valueOf(number);
        }
    }
}