package cn.jzvd.component;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import cn.jzvd.JZDataSource;
import cn.jzvd.JZVideoPlayer;
import cn.jzvd.R;

import static cn.jzvd.JZVideoPlayer.SCREEN_WINDOW_FULLSCREEN;
import static cn.jzvd.JZVideoPlayer.SCREEN_WINDOW_LIST;
import static cn.jzvd.JZVideoPlayer.SCREEN_WINDOW_NORMAL;
import static cn.jzvd.JZVideoPlayer.SCREEN_WINDOW_TINY;

public class TopContainer extends JZUIControlComponent {

    private ViewGroup topContainer;

    public TopContainer(JZVideoPlayer player) {
        super(player);
    }

    @Override
    protected void init(FrameLayout frameLayout) {
        topContainer = frameLayout.findViewById(R.id.layout_top);
    }

    @Override
    public String getName() {
        return TopContainer.class.getSimpleName();
    }

    @Override
    public void setUp(JZDataSource dataSource, int defaultUrlMapIndex, int screen, Object... objects) {
        if(player.currentScreen == SCREEN_WINDOW_TINY) {
            topContainer.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onNormal(int currentScreen) {
        if(currentScreen != SCREEN_WINDOW_TINY) {
            topContainer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPreparing(int currentScreen) {
        if(currentScreen != SCREEN_WINDOW_TINY) {
            topContainer.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onPlayingShow(int currentScreen) {
        if(currentScreen != SCREEN_WINDOW_TINY) {
            topContainer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPlayingClear(int currentScreen) {
        if(currentScreen != SCREEN_WINDOW_TINY) {
            topContainer.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onPauseShow(int currentScreen) {
        if(currentScreen != SCREEN_WINDOW_TINY) {
            topContainer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPauseClear(int currentScreen) {
        if(currentScreen != SCREEN_WINDOW_TINY) {
            topContainer.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onComplete(int currentScreen) {
        if(currentScreen != SCREEN_WINDOW_TINY) {
            topContainer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onError(int currentScreen) {
        if(currentScreen == SCREEN_WINDOW_NORMAL || currentScreen == SCREEN_WINDOW_LIST) {
            topContainer.setVisibility(View.INVISIBLE);
        } else if(currentScreen == SCREEN_WINDOW_FULLSCREEN) {
            topContainer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDismissControlView() {
        topContainer.setVisibility(View.INVISIBLE);
    }
}
