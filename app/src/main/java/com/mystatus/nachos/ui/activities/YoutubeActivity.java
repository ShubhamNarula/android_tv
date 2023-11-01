package com.mystatus.nachos.ui.activities;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.mystatus.nachos.R;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.PlayerUiController;

import org.jetbrains.annotations.NotNull;

public class YoutubeActivity extends YouTubeBaseActivity implements View.OnKeyListener {

    private String youtubeUrl;
    private YouTubePlayerView youTubePlayerView;
    private YouTubePlayer youTubePlayer;
    private float currentSecond = 0.0f;
    private float totalDuration = 0.0f;
    private boolean isPaused = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_new);
        youTubePlayerView = findViewById(R.id.ytPlayerView);
        Bundle bundle = getIntent().getExtras();
        youtubeUrl = bundle.getString("url");

//        this.video_youtube_player =(YouTubePlayerView) findViewById(R.id.video_youtube_player);

//        youTubePlayerView.setOnKeyListener(this);

        String myYouTubeVideoUrl = "https://www.youtube.com/embed/j8cKdDkkIYY";
        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NotNull YouTubePlayer youTubePlayer) {
                super.onReady(youTubePlayer);
                YoutubeActivity.this.youTubePlayer = youTubePlayer;
                youTubePlayer.loadVideo(YouTubeHelper.extractVideoIdFromUrl(youtubeUrl), 0.0f);
            }

            @Override
            public void onCurrentSecond(@NotNull YouTubePlayer youTubePlayer, float second) {
                super.onCurrentSecond(youTubePlayer, second);
                currentSecond = second;
            }

            @Override
            public void onVideoDuration(@NotNull YouTubePlayer youTubePlayer, float duration) {
                super.onVideoDuration(youTubePlayer, duration);
                totalDuration = duration;
            }

            @Override
            public void onStateChange(@NotNull YouTubePlayer youTubePlayer, PlayerConstants.@NotNull PlayerState state) {
                super.onStateChange(youTubePlayer, state);
                isPaused = state == PlayerConstants.PlayerState.PAUSED;
            }
        });

        PlayerUiController uiController = youTubePlayerView.getPlayerUiController();
        uiController.showVideoTitle(false);
        uiController.showYouTubeButton(false);
        uiController.showMenuButton(false);
        uiController.showFullscreenButton(false);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Toast.makeText(this, keyCode + "", Toast.LENGTH_SHORT).show();
        if (youTubePlayer != null) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                youTubePlayer.seekTo(Math.min(currentSecond + 10, totalDuration));
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                youTubePlayer.seekTo(Math.max(0.0f, currentSecond - 10));
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                /*onDpadCenter();*/
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (youTubePlayer != null) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                youTubePlayer.seekTo(Math.min(currentSecond + 10, totalDuration));
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                youTubePlayer.seekTo(Math.max(0.0f, currentSecond - 10));
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                /*onDpadCenter();*/
                return true;
            }
        }
        return false;
    }

    private void onDpadCenter() {
        if (isPaused) {
            youTubePlayer.play();
        } else {
            youTubePlayer.pause();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER) {
            onDpadCenter();
        }
        return super.dispatchKeyEvent(event);
    }
}
