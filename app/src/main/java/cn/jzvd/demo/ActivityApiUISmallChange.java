package cn.jzvd.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import cn.jzvd.JZVideoPlayer;
import cn.jzvd.demo.CustomView.JZVideoPlayerStandardAutoCompleteAfterFullscreen;
import cn.jzvd.demo.CustomView.JZVideoPlayerStandardGlide;
import cn.jzvd.demo.CustomView.JZVideoPlayerStandardShowShareButtonAfterFullscreen;
import cn.jzvd.demo.CustomView.JZVideoPlayerStandardShowTextureViewAfterAutoComplete;
import cn.jzvd.demo.CustomView.JZVideoPlayerStandardShowTitleAfterFullscreen;
import cn.jzvd.demo.CustomView.JZVideoPlayerStandardVolumeAfterFullscreen;

/**
 * Created by Nathen on 16/7/31.
 */
public class ActivityApiUISmallChange extends AppCompatActivity {
    JZVideoPlayerStandardShowShareButtonAfterFullscreen jzVideoPlayerStandardWithShareButton;
    JZVideoPlayerStandardShowTitleAfterFullscreen jzVideoPlayerStandardShowTitleAfterFullscreen;
    JZVideoPlayerStandardShowTextureViewAfterAutoComplete jzVideoPlayerStandardShowTextureViewAfterAutoComplete;
    JZVideoPlayerStandardAutoCompleteAfterFullscreen jzVideoPlayerStandardAutoCompleteAfterFullscreen;
    JZVideoPlayerStandardVolumeAfterFullscreen jzVideoPlayerStandardVolumeAfterFullscreen;

    JZVideoPlayerStandardGlide jzVideoPlayerStandard_1_1, jzVideoPlayerStandard_16_9;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(false);
        getSupportActionBar().setTitle("SmallChangeUI");
        setContentView(R.layout.activity_ui_small_change);

        jzVideoPlayerStandardWithShareButton = findViewById(R.id.custom_videoplayer_standard_with_share_button);
        jzVideoPlayerStandardWithShareButton.setUp(VideoConstant.videoUrlList[3], JZVideoPlayer.SCREEN_WINDOW_NORMAL,
                "饺子想呼吸", VideoConstant.videoThumbList[3]);

        jzVideoPlayerStandardShowTitleAfterFullscreen = findViewById(R.id.custom_videoplayer_standard_show_title_after_fullscreen);
        jzVideoPlayerStandardShowTitleAfterFullscreen.setUp(VideoConstant.videoUrlList[4], JZVideoPlayer.SCREEN_WINDOW_NORMAL,
                "饺子想摇头", VideoConstant.videoThumbList[4]);

        jzVideoPlayerStandardShowTextureViewAfterAutoComplete = findViewById(R.id.custom_videoplayer_standard_show_textureview_aoto_complete);
        jzVideoPlayerStandardShowTextureViewAfterAutoComplete.setUp(VideoConstant.videoUrlList[5], JZVideoPlayer.SCREEN_WINDOW_NORMAL,
                "饺子想旅行", VideoConstant.videoThumbList[5]);

        jzVideoPlayerStandardAutoCompleteAfterFullscreen = findViewById(R.id.custom_videoplayer_standard_aoto_complete);
        jzVideoPlayerStandardAutoCompleteAfterFullscreen.setUp(VideoConstant.videoUrls[0][1], JZVideoPlayer.SCREEN_WINDOW_NORMAL,
                "饺子没来", VideoConstant.videoThumbs[0][1]);

        jzVideoPlayerStandard_1_1 = findViewById(R.id.jz_videoplayer_1_1);
        jzVideoPlayerStandard_1_1.setUp(VideoConstant.videoUrls[0][1], JZVideoPlayer.SCREEN_WINDOW_NORMAL,
                "饺子有事吗", VideoConstant.videoThumbs[0][1]);
        jzVideoPlayerStandard_1_1.setScreenRatio(1, 1);

        jzVideoPlayerStandard_16_9 = findViewById(R.id.jz_videoplayer_16_9);
        jzVideoPlayerStandard_16_9.setUp(VideoConstant.videoUrls[0][1], JZVideoPlayer.SCREEN_WINDOW_NORMAL,
                "饺子来不了", VideoConstant.videoThumbs[0][1]);
        jzVideoPlayerStandard_16_9.setScreenRatio(16, 9);

        jzVideoPlayerStandardVolumeAfterFullscreen = findViewById(R.id.jz_videoplayer_volume);
        jzVideoPlayerStandardVolumeAfterFullscreen.setUp(VideoConstant.videoUrls[0][1], JZVideoPlayer.SCREEN_WINDOW_NORMAL,
                "饺子摇摆", VideoConstant.videoThumbs[0][1]);
    }

    @Override
    public void onBackPressed() {
        if (JZVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JZVideoPlayer.releaseAllVideos();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
