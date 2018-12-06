package cn.jzvd.component;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import cn.jzvd.JZDataSource;
import cn.jzvd.JZVideoPlayerStandard;
import cn.jzvd.R;

import static cn.jzvd.JZVideoPlayer.SCREEN_WINDOW_FULLSCREEN;
import static cn.jzvd.JZVideoPlayer.SCREEN_WINDOW_LIST;
import static cn.jzvd.JZVideoPlayer.SCREEN_WINDOW_NORMAL;
import static cn.jzvd.JZVideoPlayer.backPress;

public class BackButtonComponent extends JZUIComponent {

    private ImageView backButton;

    public BackButtonComponent(JZVideoPlayerStandard player) {
        super(player);
    }

    @Override
    public String getName() {
        return BackButtonComponent.class.getSimpleName();
    }

    @Override
    protected void init(FrameLayout frameLayout) {
        super.init(frameLayout);
        backButton = frameLayout.findViewById(R.id.back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BackButtonComponent.this.onClick();
            }
        });
    }

    private void onClick() {
        backPress();
    }

    @Override
    public void setUp(JZDataSource dataSource, int defaultUrlMapIndex, int screen, Object... objects) {
        if (player.currentScreen == SCREEN_WINDOW_FULLSCREEN) {
            backButton.setVisibility(View.VISIBLE);
        } else if (player.currentScreen == SCREEN_WINDOW_NORMAL || player.currentScreen == SCREEN_WINDOW_LIST) {
            backButton.setVisibility(View.GONE);
        }
    }
}
