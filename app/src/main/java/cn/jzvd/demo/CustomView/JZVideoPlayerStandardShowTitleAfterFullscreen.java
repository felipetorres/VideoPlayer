package cn.jzvd.demo.CustomView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import cn.jzvd.JZDataSource;
import cn.jzvd.JZVideoPlayerStandard;
import cn.jzvd.component.TitleComponent;

import static cn.jzvd.JZVideoPlayer.SCREEN_WINDOW_FULLSCREEN;

/**
 * Created by Nathen
 * On 2016/04/27 10:49
 */
public class JZVideoPlayerStandardShowTitleAfterFullscreen extends JZVideoPlayerStandard {
    public JZVideoPlayerStandardShowTitleAfterFullscreen(Context context) {
        super(context);
    }

    public JZVideoPlayerStandardShowTitleAfterFullscreen(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void init(Context context) {
        super.init(context);
        super.loader.register(new CustomTitleComponent(this));
    }
}

class CustomTitleComponent extends TitleComponent {

    CustomTitleComponent(JZVideoPlayerStandard player) {
        super(player);
    }

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