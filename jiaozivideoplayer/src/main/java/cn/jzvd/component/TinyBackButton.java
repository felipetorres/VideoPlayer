package cn.jzvd.component;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import cn.jzvd.JZDataSource;
import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerManager;
import cn.jzvd.JZVideoPlayerStandard;
import cn.jzvd.R;

import static cn.jzvd.JZVideoPlayer.SCREEN_WINDOW_TINY;
import static cn.jzvd.JZVideoPlayer.backPress;
import static cn.jzvd.JZVideoPlayer.quitFullscreenOrTinyWindow;

public class TinyBackButton extends JZUIComponent {

    private ImageView tinyBackImageView;

    public TinyBackButton(JZVideoPlayerStandard player) {
        super(player);
    }

    @Override
    public String getName() {
        return TinyBackButton.class.getSimpleName();
    }

    @Override
    protected void init(FrameLayout frameLayout) {
        tinyBackImageView = frameLayout.findViewById(R.id.back_tiny);
        tinyBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TinyBackButton.this.onClick();
            }
        });
    }

    private void onClick() {
        if (JZVideoPlayerManager.getFirstFloor().currentScreen == JZVideoPlayer.SCREEN_WINDOW_LIST) {
            quitFullscreenOrTinyWindow();
        } else {
            backPress();
        }
    }

    @Override
    public void setUp(JZDataSource dataSource, int defaultUrlMapIndex, int screen, Object... objects) {
        if (player.currentScreen == SCREEN_WINDOW_TINY) {
            tinyBackImageView.setVisibility(View.VISIBLE);
        } else {
            tinyBackImageView.setVisibility(View.INVISIBLE);
        }
    }
}
