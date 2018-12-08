package cn.jzvd.component;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import cn.jzvd.JZDataSource;
import cn.jzvd.JZVideoPlayer;
import cn.jzvd.R;

import static cn.jzvd.JZVideoPlayer.SCREEN_WINDOW_TINY;

public class BottomContainer extends JZUIControlComponent {

    private ViewGroup bottomContainer;

    public BottomContainer(JZVideoPlayer player) {
        super(player);
    }

    @Override
    public String getName() {
        return BottomContainer.class.getSimpleName();
    }

    @Override
    protected void init(FrameLayout frameLayout) {
        bottomContainer = frameLayout.findViewById(R.id.layout_bottom);
    }

    @Override
    public void setUp(JZDataSource dataSource, int defaultUrlMapIndex, int screen, Object... objects) {
        if(player.currentScreen == SCREEN_WINDOW_TINY) {
            bottomContainer.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onNormal(int currentScreen) {
        if(currentScreen != SCREEN_WINDOW_TINY) {
            bottomContainer.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onPreparing(int currentScreen) {
        if(currentScreen != SCREEN_WINDOW_TINY) {
            bottomContainer.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onPlayingShow(int currentScreen) {
        if(currentScreen != SCREEN_WINDOW_TINY) {
            bottomContainer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPlayingClear(int currentScreen) {
        if(currentScreen != SCREEN_WINDOW_TINY) {
            bottomContainer.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onPauseShow(int currentScreen) {
        if(currentScreen != SCREEN_WINDOW_TINY) {
            bottomContainer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPauseClear(int currentScreen) {
        if(currentScreen != SCREEN_WINDOW_TINY) {
            bottomContainer.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onComplete(int currentScreen) {
        if(currentScreen != SCREEN_WINDOW_TINY) {
            bottomContainer.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onError(int currentScreen) {
        if(currentScreen != SCREEN_WINDOW_TINY) {
            bottomContainer.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onDismissControlView() {
        bottomContainer.setVisibility(View.INVISIBLE);
    }

    public int getVisibility() {
        return bottomContainer.getVisibility();
    }
}
