package code.main;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.kmcapp.android.R;

import code.common.LocaleHelper;
import code.utils.AppUtils;
import code.view.BaseActivity;

public class VideoActivity extends BaseActivity implements View.OnClickListener{

    /*VideoView videoView;

    ImageView ivClose;*/

    PlayerView exoPlayerView;
    SimpleExoPlayer exoPlayer;
    public static String videoURL = "";

    public static String url="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        exoPlayerView = findViewById(R.id.exo_player_view);
        Uri videoURI = Uri.parse(videoURL);
        prepareExoPlayerFromFileUri(videoURI);

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    private void prepareExoPlayerFromFileUri(Uri uri){

        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
        exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector);

        DataSpec dataSpec = new DataSpec(uri);
        final FileDataSource fileDataSource = new FileDataSource();
        try {
            fileDataSource.open(dataSpec);
        } catch (FileDataSource.FileDataSourceException e) {
            e.printStackTrace();
        }

        DataSource.Factory factory = new DataSource.Factory() {
            @Override
            public DataSource createDataSource() {
                return fileDataSource;
            }
        };

        MediaSource audioSource = new ExtractorMediaSource(fileDataSource.getUri(),
                factory, new DefaultExtractorsFactory(), null, null);

        exoPlayerView.setPlayer(exoPlayer);
        exoPlayer.prepare(audioSource);
        exoPlayer.setPlayWhenReady(true);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        exoPlayer.stop();
        exoPlayer.release();
    }
}
