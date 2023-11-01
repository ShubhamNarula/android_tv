package com.mystatus.nachos.ui.player;

import android.app.Activity;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableBoolean;

import android.app.ProgressDialog;
import android.media.MediaFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.SingleSampleMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaLoadRequestData;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.MediaStatus;
import com.google.android.gms.cast.MediaTrack;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.SessionManager;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.google.android.gms.common.images.WebImage;
import com.mystatus.nachos.BR;
import com.mystatus.nachos.Provider.PrefManager;
import com.mystatus.nachos.R;
import com.mystatus.nachos.api.apiClient;
import com.mystatus.nachos.api.apiRest;
import com.mystatus.nachos.entity.ApiResponse;
import com.mystatus.nachos.entity.Subtitle;
import com.mystatus.nachos.ui.activities.PlayerActivity;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by Tamim on 28/09/2019.

 */

public class CustomPlayerViewModel extends BaseObservable implements ExoPlayer.EventListener {
    private ArrayList<Subtitle> subtitlesForCast= new ArrayList<>();

    private Activity mActivity;
    private String mUrl;
    private boolean loadingComplete = false;
    private static final boolean SHOULD_AUTO_PLAY = true;
    private ObservableBoolean controlsVisible = new ObservableBoolean(true);
    private boolean mPausable = true;
    private boolean isInProgress = false;
    private boolean isLoadingNow = false;

    private CastSession mCastSession;
    private SessionManager mSessionManager;

    private SimpleExoPlayerView mSimpleExoPlayerView;
    public SimpleExoPlayer mExoPlayer;
    private ImageView ic_media_stop;
//    private RelativeLayout payer_pause_play;
    private Boolean isLive = false;
    private String videoType;
    private String videoImage;
    private String videoTitle;
    private String videoSubTile;
    private int keyCode;
    private KeyEvent event;
    private PrefManager prf;

    public CustomPlayerViewModel(Activity activity) {
        mActivity = activity;
    }

    public void onStart(SimpleExoPlayerView simpleExoPlayerView, Bundle bundle) {
        mSimpleExoPlayerView = simpleExoPlayerView;
        mUrl = bundle.getString("videoUrl");
        isLive = bundle.getBoolean("isLive");
        videoType = bundle.getString("videoType");
        videoTitle = bundle.getString("videoTitle");
        videoSubTile = bundle.getString("videoSubTile");
        videoImage = bundle.getString("videoImage");
        initPlayer();
        mSimpleExoPlayerView.setPlayer(mExoPlayer);
        prf= new PrefManager(mActivity);

        String  lastTime=bundle.getString("seekTime");
        long seekTime=0;
        if(lastTime!=null&&!lastTime.isEmpty()){
            seekTime= Long.parseLong(lastTime);
        }

        preparePlayer(null,seekTime);
        updateCastSessionAndSessionManager();

    }

//    public void setPayerPausePlay(RelativeLayout payer_pause_play) {
//        this.payer_pause_play =  payer_pause_play;
//    }
    public void setMediaFull() {
        mSimpleExoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
        mExoPlayer.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
    }
    public void setMediaNormal() {
        mSimpleExoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
    }
    private void initPlayer() {

        int HI_BITRATE = 2097152;

        // 1. Create a default TrackSelector
//        Handler mainHandler = new Handler();
//        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
//        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);

        DefaultTrackSelector trackSelector = new DefaultTrackSelector();
        DefaultTrackSelector.Parameters defaultTrackParam = trackSelector.buildUponParameters().build();
        trackSelector.setParameters(defaultTrackParam);

        DefaultTrackSelector.Parameters parameters = trackSelector.buildUponParameters()
                .setMaxVideoBitrate(HI_BITRATE)
                .setForceHighestSupportedBitrate(true)
                .build();
        trackSelector.setParameters(parameters);

        // 2. Create a default LoadControl
        LoadControl loadControl = new DefaultLoadControl();

        // 3. Create the player
        mExoPlayer = ExoPlayerFactory.newSimpleInstance(mActivity,
                trackSelector, loadControl);
    }

    public void preparePlayer(Subtitle subtitle,long seekTo) {

        // Produces Extractor instances for parsing the media data.
//    ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
//    Handler mainHandler = new Handler();
//    MediaSource mediaSource = new HlsMediaSource(Uri.parse(
//    "https://live3-mediaset-it.akamaized.net/content/hls_clr_xo/live/channel(ch09)/Stream(02)/index.m3u8"),
//        dataSourceFactory, null, null);

        // Measures bandwidth during playback. Can be null if not required.
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(mActivity,
                Util.getUserAgent(mActivity, "ExoPlayer2"), bandwidthMeter);
        // Produces Extractor instances for parsing the media data.
        // This is the MediaSource representing the media to be played.
        Uri videoUri = Uri.parse(mUrl);
        MediaSource mediaSource1;
        int sourceSize = 0;

        if (videoType.equals("mp4")){
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            //  mediaSource1 = new ExtractorMediaSource(videoUri, dataSourceFactory, extractorsFactory, null, null);
            mediaSource1 =  new ProgressiveMediaSource.Factory(dataSourceFactory)
                    .setExtractorsFactory(extractorsFactory)
                    .createMediaSource(videoUri);
            sourceSize++;
        }else if (videoType.equals("dash")){
            // mediaSource1 = new DashMediaSource(videoUri,dataSourceFactory, new DefaultDashChunkSource.Factory(dataSourceFactory),null,null);
            mediaSource1=  new DashMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(videoUri);
            sourceSize++;
        }else if (videoType.equals("m3u8")){

            mediaSource1 = new HlsMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(videoUri);
            // mediaSource1 = new HlsMediaSource(videoUri, dataSourceFactory,  new DefaultDashChunkSource.Factory(dataSourceFactory), null,null);
            sourceSize++;
        }else{
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            //  mediaSource1 = new ExtractorMediaSource(videoUri, dataSourceFactory, extractorsFactory, null, null);
            mediaSource1 =  new ProgressiveMediaSource.Factory(dataSourceFactory)
                    .setExtractorsFactory(extractorsFactory)
                    .createMediaSource(videoUri);
            sourceSize++;
        }
        SingleSampleMediaSource subtitleSource = null;
        if (subtitle!=null){
            if (subtitle.getType().equals("srt")) {
                Format textFormat = Format.createTextSampleFormat(null, MimeTypes.APPLICATION_SUBRIP,
                        null, Format.NO_VALUE, Format.NO_VALUE, "ar", null, Format.OFFSET_SAMPLE_RELATIVE);
                subtitleSource = new SingleSampleMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(Uri.parse(subtitle.getUrl()), textFormat, C.TIME_UNSET);
            }else if (subtitle.getType().equals("vtt")){
                subtitleSource = new SingleSampleMediaSource(Uri.parse(subtitle.getUrl()), dataSourceFactory, Format.createTextSampleFormat(null, MimeTypes.TEXT_VTT, Format.NO_VALUE, "en", null), C.TIME_UNSET);
            }else if (subtitle.getType().equals("ass")){
                subtitleSource = new SingleSampleMediaSource(Uri.parse(subtitle.getUrl()), dataSourceFactory, Format.createTextSampleFormat(null, MimeTypes.TEXT_SSA, Format.NO_VALUE, "en", null), C.TIME_UNSET);
            }
            sourceSize++;
        }


        MediaSource[] mediaSources = new MediaSource[sourceSize]; //The Size must change depending on the Uris
        mediaSources[0]=mediaSource1;

        if (subtitleSource!=null){
            mediaSources[1]=subtitleSource;
        }

        MediaSource mediaSource = new MergingMediaSource(mediaSources);



        // Prepare the player with the source.
        mExoPlayer.prepare(mediaSource,false,false);
        mExoPlayer.seekTo(seekTo);
        mExoPlayer.addListener(this);
        mExoPlayer.setPlayWhenReady(SHOULD_AUTO_PLAY);
        mExoPlayer.setPlayWhenReady(true);

    }



    public void onPlayerClicked(final View view) {
        controlsVisible.set(true);
    }

    @Bindable
    public int getPlaybackImageRes() {
        if (!isPlaying()) {
            return R.drawable.ic_media_play;
        }
        if (!isPausable()) {
            return R.drawable.ic_media_stop;
        } else {
            return R.drawable.ic_media_pause;
        }
    }


    public void onPlayPauseClicked(final View view) {
        if (mExoPlayer == null) {
            return;
        }
        if (isPlaying()) {
            pause();
        } else {
            play();
        }
    }
    public void play() {
        if (mExoPlayer == null) {
            return;
        }
        mExoPlayer.setPlayWhenReady(true);
    }

    public void pause() {
        if (mExoPlayer == null) {
            return;
        }
        mExoPlayer.setPlayWhenReady(false);
    }

    private void updateCastSessionAndSessionManager() {
        if (mActivity instanceof PlayerActivity) {
            mCastSession = ((PlayerActivity) mActivity).getCastSession();
            mSessionManager = ((PlayerActivity) mActivity).getSessionManager();
        }
    }

    private void loadRemoteMedia(int position, boolean autoPlay) {
        final RemoteMediaClient remoteMediaClient = mCastSession.getRemoteMediaClient();
        if (remoteMediaClient == null) {
            Log.d("MYAPP","remoteMediaClient == null");
            return;
        }

        remoteMediaClient.registerCallback(new RemoteMediaClient.Callback() {
            @Override
            public void onStatusUpdated() {
                Log.d("MYAPP","onStatusUpdated");
                if (remoteMediaClient.getMediaStatus() != null) {
                    if (remoteMediaClient.getMediaStatus().getPlayerState() == MediaStatus.PLAYER_STATE_PLAYING || remoteMediaClient.getMediaStatus().getPlayerState() == MediaStatus.PLAYER_STATE_BUFFERING) {
                        mSimpleExoPlayerView.setUseController(false);
                    } else {
                        mSimpleExoPlayerView.setUseController(true);
                    }
                    if (remoteMediaClient.getMediaStatus().getIdleReason() == MediaStatus.IDLE_REASON_FINISHED) {
                        mExoPlayer.seekToDefaultPosition();
                        mExoPlayer.setPlayWhenReady(false);
                    }
                }
            }
        });
        remoteMediaClient.load(new MediaLoadRequestData.Builder()
                .setMediaInfo(getMediaInfo())
                .setAutoplay(autoPlay)
                .setCurrentTime(position).build());
       /* remoteMediaClient.addListener(new RemoteMediaClient.Listener() {
            @Override
            public void onStatusUpdated() {
                Log.d("MYAPP","onStatusUpdated");
                if (remoteMediaClient.getMediaStatus() != null) {
                    if (remoteMediaClient.getMediaStatus().getPlayerState() == MediaStatus.PLAYER_STATE_PLAYING ||
                            remoteMediaClient.getMediaStatus().getPlayerState() == MediaStatus.PLAYER_STATE_BUFFERING) {
                        mSimpleExoPlayerView.setUseController(false);
                    } else {
                        mSimpleExoPlayerView.setUseController(true);
                    }
                    if (remoteMediaClient.getMediaStatus().getIdleReason() == MediaStatus.IDLE_REASON_FINISHED) {
                        mExoPlayer.seekToDefaultPosition();
                        mExoPlayer.setPlayWhenReady(false);
                    }
                }
            }

            @Override
            public void onMetadataUpdated() {
                Log.d("MYAPP","onMetadataUpdated");
            }

            @Override
            public void onQueueStatusUpdated() {
                Log.d("MYAPP","onQueueStatusUpdated");
            }

            @Override
            public void onPreloadStatusUpdated() {
                Log.d("MYAPP","onPreloadStatusUpdated");
            }

            @Override
            public void onSendingRemoteMediaRequest() {
                Log.d("MYAPP","onSendingRemoteMediaRequest");
            }

            @Override
            public void onAdBreakStatusUpdated() {

            }
        });
        remoteMediaClient.load(getMediaInfo(), autoPlay, position);*/
    }

    public void loadMedia(int position, boolean autoPlay) {
        updateCastSessionAndSessionManager();

        if (mCastSession == null) {
//            mCastSession = mSessionManager.getCurrentCastSession();
        }
        if (mCastSession != null) {
            mSimpleExoPlayerView.setUseController(false);
            mExoPlayer.setPlayWhenReady(false);
            loadRemoteMedia(position, autoPlay);
        } else {
            mExoPlayer.setPlayWhenReady(true);
        }
    }

    private MediaInfo getMediaInfo() {
        MediaMetadata movieMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
        movieMetadata.putString(MediaMetadata.KEY_TITLE, videoTitle);
        movieMetadata.putString(MediaMetadata.KEY_SUBTITLE, videoSubTile);
        movieMetadata.addImage(new WebImage(Uri.parse(videoImage)));
        movieMetadata.addImage(new WebImage(Uri.parse(videoImage)));
        List<MediaTrack> tracks =  new ArrayList<>();

        for (int i = 0; i < subtitlesForCast.size(); i++) {
            tracks.add(buildTrack(i+1,"text","captions",subtitlesForCast.get(i).getUrl(),subtitlesForCast.get(i).getLanguage(),"en-US"));
        }
        MediaInfo mediaInfo = new MediaInfo.Builder(mUrl)
                .setStreamType(getStramType())
                // .setContentType("application/x-mpegurl")
                .setStreamDuration(getStramType())
                .setMetadata(movieMetadata)
                .setMediaTracks(tracks)
                .build();
        Log.v("URLVIDEO",mUrl);
        return mediaInfo;
    }

    public int getStramType(){
        if (isLive)
            return MediaInfo.STREAM_TYPE_LIVE;
        else
            return MediaInfo.STREAM_TYPE_BUFFERED;

    }
    private static MediaTrack buildTrack(long id, String type, String subType, String contentId,
                                         String name, String language) {

        int trackType = MediaTrack.TYPE_UNKNOWN;
        if ("text".equals(type)) {
            trackType = MediaTrack.TYPE_TEXT;
        } else if ("video".equals(type)) {
            trackType = MediaTrack.TYPE_VIDEO;
        } else if ("audio".equals(type)) {
            trackType = MediaTrack.TYPE_AUDIO;
        }

        int trackSubType = MediaTrack.SUBTYPE_NONE;
        if (subType != null) {
            if ("captions".equals(type)) {
                trackSubType = MediaTrack.SUBTYPE_CAPTIONS;
            } else if ("subtitle".equals(type)) {
                trackSubType = MediaTrack.SUBTYPE_SUBTITLES;
            }
        }

        return new MediaTrack.Builder(id, trackType)
                .setContentType(MediaFormat.MIMETYPE_TEXT_VTT)
                .setName(name)
                .setSubtype(trackSubType)
                .setContentId(contentId)
                .setLanguage(language).build();
    }
    private boolean isPlaying() {
        return mExoPlayer != null && mExoPlayer.getPlayWhenReady();
    }

    public boolean isPlaying_custom() {
        return mExoPlayer != null && mExoPlayer.getPlayWhenReady();
    }

    private boolean isPausable() {
        return mPausable;
    }

    public void setPausable(boolean pausable) {
        mPausable = pausable;
//        notifyPropertyChanged(BR.pausable);
    }

    public SimpleExoPlayer getExoPlayer() {
        return mExoPlayer;
    }

    public SimpleExoPlayerView getSimpleExoPlayerView() {
        return mSimpleExoPlayerView;
    }

    public void setIsInProgress(boolean isInProgress) {
        this.isInProgress = isInProgress;
    }

    @Bindable
    public boolean getLoadingComplete() {
        return loadingComplete;
    }
    @Bindable
    public boolean isLoaidingNow() {
        Log.i("TEST", "ExoPlayer Changed ");
//        if (isLoadingNow)
//            payer_pause_play.setVisibility(View.GONE);
//        else
//            payer_pause_play.setVisibility(View.VISIBLE);

        return isLoadingNow;
    }
    private void setLoadingComplete(boolean complete) {
        loadingComplete = complete;
        notifyPropertyChanged(BR.loadingComplete);
    }

    @Bindable
    public boolean getControlsVisible() {
        return controlsVisible.get();
    }




    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
        setLoadingComplete(!isLoading);
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == ExoPlayer.STATE_READY && playWhenReady && !isInProgress) {
            loadMedia(0, true);
        } else if (playbackState == ExoPlayer.STATE_ENDED) {
            PrefManager prf= new PrefManager(mActivity);
            long current=mExoPlayer.getCurrentPosition();
            long duration =mExoPlayer.getDuration();
            int percent= (int)((double)((float)current/(float)duration)*100);
            updateWatchTime(String.valueOf(mExoPlayer.getCurrentPosition()),percent);
//            mExoPlayer.seekToDefaultPosition();
//            mExoPlayer.setPlayWhenReady(false);
            isInProgress = false;
        }

        if(playbackState == ExoPlayer.STATE_READY &&!playWhenReady){
            PrefManager prf= new PrefManager(mActivity);
            long current=mExoPlayer.getCurrentPosition();
            long duration =mExoPlayer.getDuration();
            int percent= (int)((double)((float)current/(float)duration)*100);
            updateWatchTime(String.valueOf(mExoPlayer.getCurrentPosition()),percent);
        }


        if (playbackState ==  Player.STATE_BUFFERING ){
            isLoadingNow =  true;
            notifyPropertyChanged(BR.loaidingNow);
        }
        if (playbackState ==  Player.STATE_READY){
            isLoadingNow =  false;
            notifyPropertyChanged(BR.loaidingNow);
        }


    }

    public void updateWatchTime(String milis,Integer percentage){
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        String email=prf.getString("USERN_USER");

        int poster_id = 0;
        int episode_id = 0;

        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat last_update = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String final_last_update = last_update.format(date.getTime());

        Call<ApiResponse> call;
        if(mActivity.getIntent().getBooleanExtra("isEpisode",false)){
            episode_id = mActivity.getIntent().getIntExtra("id",0);
            call = service.updateWatchTime(email,null,episode_id,milis,percentage,final_last_update,0);
        }else{
            poster_id = mActivity.getIntent().getIntExtra("id",0);
            call = service.updateWatchTime(email,poster_id,null,milis,percentage,null,0);
        }
        ((PlayerActivity)mActivity).setLastVideoSeekTime(milis);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()){
                  //  Toasty.success(mActivity,response.body().getMessage(), Toast.LENGTH_SHORT, true).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
              //  Toasty.error(mActivity, "Operation has been cancelled ! ", Toast.LENGTH_SHORT, true).show();
            }
        });
    }


    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }



    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }


    public void setSubtitilesList(ArrayList<Subtitle> _subtitlesForCast) {
        subtitlesForCast =  _subtitlesForCast;
    }
}
