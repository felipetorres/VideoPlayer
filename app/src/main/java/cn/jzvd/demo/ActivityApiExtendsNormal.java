package cn.jzvd.demo;

import android.app.Activity;
import android.os.Bundle;

import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;
import cn.jzvd.demo.CustomView.JZVideoPlayerStandardGlide;

/**
 * Created by Nathen on 2017/9/19.
 */

public class ActivityApiExtendsNormal extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extends_normal);
        JZVideoPlayerStandardGlide jzVideoPlayerStandard = findViewById(R.id.videoplayer);
        jzVideoPlayerStandard.setUp(VideoConstant.videoUrlList[0],
                                    JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL,
                                    "饺子不信", VideoConstant.videoThumbList[0]);
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
}
