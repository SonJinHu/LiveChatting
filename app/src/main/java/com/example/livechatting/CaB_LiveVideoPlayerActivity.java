package com.example.livechatting;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.livechatting.broadcast.liveVideoPlayer.DefaultExtractorsFactoryForFLV;
import com.example.livechatting.broadcast.liveVideoPlayer.EventLogger;
import com.example.livechatting.broadcast.liveVideoPlayer.RtmpDataSource;
import com.example.livechatting.data.Constants;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer.DecoderInitializationException;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil.DecoderQueryException;
import com.google.android.exoplayer2.source.BehindLiveWindowException;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector.MappedTrackInfo;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.DebugTextViewHelper;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

public class CaB_LiveVideoPlayerActivity extends AppCompatActivity implements OnClickListener {

    private String url;
    private EventLogger eventLogger;
    private SimpleExoPlayerView simpleExoPlayerView;
    private SimpleExoPlayer player;
    private DefaultTrackSelector trackSelector;
    private DebugTextViewHelper debugViewHelper;
    private boolean needRetrySource;
    private boolean shouldAutoPlay;
    private int resumeWindow;
    private long resumePosition;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.cab_player);

        String nick = getIntent().getStringExtra("nick");
        String img = getIntent().getStringExtra("img");
        url = getIntent().getStringExtra("rtmp");

        ImageView iv_profile = findViewById(R.id.ccb_iv_profile);
        if (img == null || img.equals("") || img.equals("null")) {
            Glide.with(getApplicationContext())
                    .load(R.drawable.profile_default)
                    .apply(RequestOptions.circleCropTransform())
                    .into(iv_profile);
        } else {
            Glide.with(getApplicationContext())
                    .load(Constants.URL + img)
                    .apply(RequestOptions.circleCropTransform())
                    .placeholder(R.drawable.profile_default)
                    .error(R.drawable.profile_default)
                    .into(iv_profile);
        }
        TextView tv_nick = findViewById(R.id.ccb_tv_nick);
        tv_nick.setText(nick);
        View tv_exit = findViewById(R.id.ccb_tv_exit);
        tv_exit.setOnClickListener(v -> finish());

        shouldAutoPlay = true;
        clearResumePosition();

        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
        if (CookieHandler.getDefault() != cookieManager)
            CookieHandler.setDefault(cookieManager);

        Button retryButton = findViewById(R.id.ccb_btn_retry);
        retryButton.setOnClickListener(this);

        simpleExoPlayerView = findViewById(R.id.player_view);
        simpleExoPlayerView.requestFocus();
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        releasePlayer();
        shouldAutoPlay = true;
        clearResumePosition();
        setIntent(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        play();
    }

    @Override
    public void onStop() {
        super.onStop();
        releasePlayer();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            play();
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.storage_permission_denied), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // Show the controls on any key event.
        simpleExoPlayerView.showController();
        // If the event was not handled then see if the player view can handle it as a media key event.
        return super.dispatchKeyEvent(event) || simpleExoPlayerView.dispatchMediaKeyEvent(event);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ccb_btn_retry)
            play();
    }

    private void releasePlayer() {
        if (player != null) {
            debugViewHelper.stop();
            debugViewHelper = null;
            shouldAutoPlay = player.getPlayWhenReady();
            updateResumePosition();
            player.release();

            player = null;
            trackSelector = null;
            eventLogger = null;
        }
    }

    private void updateResumePosition() {
        resumeWindow = player.getCurrentWindowIndex();
        resumePosition = player.isCurrentWindowSeekable()
                ? Math.max(0, player.getCurrentPosition())
                : C.TIME_UNSET;
    }

    private void clearResumePosition() {
        resumeWindow = C.INDEX_UNSET;
        resumePosition = C.TIME_UNSET;
    }

    private void play() {
        boolean needNewPlayer = player == null;
        if (needNewPlayer) {
            TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(new DefaultBandwidthMeter());
            trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
            eventLogger = new EventLogger(trackSelector);

            player = ExoPlayerFactory.newSimpleInstance(this, trackSelector, new DefaultLoadControl(), null, SimpleExoPlayer.EXTENSION_RENDERER_MODE_OFF);
            player.addListener(new ExoPlayer.EventListener() {
                @Override
                public void onTimelineChanged(Timeline timeline, Object manifest) {
                    // Do nothing.
                }

                @Override
                public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                    MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
                    if (mappedTrackInfo != null) {
                        if (mappedTrackInfo.getTrackTypeRendererSupport(C.TRACK_TYPE_VIDEO) == MappedTrackInfo.RENDERER_SUPPORT_UNSUPPORTED_TRACKS)
                            Toast.makeText(getApplicationContext(), getString(R.string.error_unsupported_video), Toast.LENGTH_LONG).show();

                        if (mappedTrackInfo.getTrackTypeRendererSupport(C.TRACK_TYPE_AUDIO) == MappedTrackInfo.RENDERER_SUPPORT_UNSUPPORTED_TRACKS)
                            Toast.makeText(getApplicationContext(), getString(R.string.error_unsupported_audio), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onLoadingChanged(boolean isLoading) {
                    // Do nothing.
                }

                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    // 방송 끝
                    //if (playbackState == ExoPlayer.STATE_ENDED) {
                    //}
                }

                @Override
                public void onPlayerError(ExoPlaybackException error) {
                    String errorString = null;
                    if (error.type == ExoPlaybackException.TYPE_RENDERER) {
                        Exception cause = error.getRendererException();
                        if (cause instanceof DecoderInitializationException) {
                            // Special case for decoder initialization failures.
                            DecoderInitializationException decoderInitializationException = (DecoderInitializationException) cause;
                            if (decoderInitializationException.decoderName == null) {
                                if (decoderInitializationException.getCause() instanceof DecoderQueryException) {
                                    errorString = getString(R.string.error_querying_decoders);
                                } else if (decoderInitializationException.secureDecoderRequired) {
                                    errorString = getString(R.string.error_no_secure_decoder, decoderInitializationException.mimeType);
                                } else {
                                    errorString = getString(R.string.error_no_decoder, decoderInitializationException.mimeType);
                                }
                            } else {
                                errorString = getString(R.string.error_instantiating_decoder, decoderInitializationException.decoderName);
                            }
                        }
                    }

                    if (errorString != null)
                        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_LONG).show();

                    needRetrySource = true;
                    if (isBehindLiveWindow(error)) {
                        clearResumePosition();
                        play();
                    } else {
                        updateResumePosition();
                    }
                }

                @Override
                public void onPositionDiscontinuity() {
                    if (needRetrySource) {
                        // This will only occur if the user has performed a seek whilst in the error state. Update the
                        // resume position so that if the user then retries, playback will resume from the position to
                        // which they seek.
                        updateResumePosition();
                    }
                }
            });
            player.addListener(eventLogger);
            player.setAudioDebugListener(eventLogger);
            player.setVideoDebugListener(eventLogger);
            player.setMetadataOutput(eventLogger);
            simpleExoPlayerView.setPlayer(player);
            player.setPlayWhenReady(shouldAutoPlay);

            TextView debugTextView = findViewById(R.id.ccb_tv_debug);
            debugViewHelper = new DebugTextViewHelper(player, debugTextView);
            debugViewHelper.start();
        }

        if (needNewPlayer || needRetrySource) {
            boolean haveResumePosition = resumeWindow != C.INDEX_UNSET;
            if (haveResumePosition)
                player.seekTo(resumeWindow, resumePosition);

            //Uri uri = Uri.parse(URL);
            Uri uri = Uri.parse(url);
            RtmpDataSource.RtmpDataSourceFactory rtmpDataSourceFactory = new RtmpDataSource.RtmpDataSourceFactory();
            MediaSource mediaSource = new ExtractorMediaSource(uri, rtmpDataSourceFactory, new DefaultExtractorsFactoryForFLV(), new Handler(), eventLogger);
            player.prepare(mediaSource, !haveResumePosition, false);
            needRetrySource = false;
        }
    }

    private boolean isBehindLiveWindow(ExoPlaybackException e) {
        if (e.type != ExoPlaybackException.TYPE_SOURCE)
            return false;

        Throwable cause = e.getSourceException();
        while (cause != null) {
            if (cause instanceof BehindLiveWindowException)
                return true;
            cause = cause.getCause();
        }
        return false;
    }
}