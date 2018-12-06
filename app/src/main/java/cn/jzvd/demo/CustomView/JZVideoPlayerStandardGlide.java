package cn.jzvd.demo.CustomView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.bumptech.glide.Glide;

import cn.jzvd.JZDataSource;
import cn.jzvd.JZVideoPlayerStandard;
import cn.jzvd.component.JZUIControlComponent;
import cn.jzvd.component.ThumbComponent;

public class JZVideoPlayerStandardGlide extends JZVideoPlayerStandard {

    public JZVideoPlayerStandardGlide(Context context) {
        super(context);
    }

    public JZVideoPlayerStandardGlide(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void init(Context context) {
        super.init(context);
        super.loader.registerControl(new CustomThumbComponent(this));
    }

    @Override
    public void onAutoCompletion() {
        super.onAutoCompletion();
        for (JZUIControlComponent component : super.loader.getRegisteredControlComponents()) {
            component.onAutoCompletion(currentScreen);
        }
    }
}

class CustomThumbComponent extends ThumbComponent {

    public CustomThumbComponent(JZVideoPlayerStandard player) {
        super(player);
    }

    @Override
    public void setUp(JZDataSource dataSource, int defaultUrlMapIndex, int screen, Object... objects) {
        super.setUp(dataSource, defaultUrlMapIndex, screen, objects);
        if(objects.length >= 2) {
            Glide.with(context).load(objects[1]).into(thumbImageView);
        }
    }

    @Override
    public void onAutoCompletion(int currentScreen) {
        super.onAutoCompletion(currentScreen);
        thumbImageView.setVisibility(View.GONE);
    }
}