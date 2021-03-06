package cn.jzvd.demo.CustomView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import cn.jzvd.JZDataSource;
import cn.jzvd.plugin.TitlePlugin;

import static cn.jzvd.JZVideoPlayer.SCREEN_WINDOW_FULLSCREEN;

/**
 * Created by Nathen
 * On 2016/04/27 10:49
 */
public class JZVideoPlayerStandardShowTitleAfterFullscreen extends JZVideoPlayerStandardGlide {
    public JZVideoPlayerStandardShowTitleAfterFullscreen(Context context) {
        super(context);
    }

    public JZVideoPlayerStandardShowTitleAfterFullscreen(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void init(Context context) {
        super.loader.register(this, new CustomTitlePlugin());
        super.init(context);
    }
}

class CustomTitlePlugin extends TitlePlugin {

    @Override
    public void setUp(JZDataSource dataSource, int defaultUrlMapIndex, int screen, Object... objects) {
        super.setUp(dataSource, defaultUrlMapIndex, screen, objects);
        if (player.currentScreen == SCREEN_WINDOW_FULLSCREEN) {
            titleTextView.setVisibility(View.VISIBLE);
        } else {
            titleTextView.setVisibility(View.INVISIBLE);
        }
    }
}