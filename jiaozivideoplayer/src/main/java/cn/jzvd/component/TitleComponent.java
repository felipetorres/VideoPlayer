package cn.jzvd.component;

import android.widget.FrameLayout;
import android.widget.TextView;

import cn.jzvd.JZDataSource;
import cn.jzvd.JZVideoPlayerStandard;
import cn.jzvd.R;

public class TitleComponent extends JZUIComponent {

    protected TextView titleTextView;

    public TitleComponent(JZVideoPlayerStandard player) {
        super(player);
    }

    @Override
    protected void init(FrameLayout frameLayout) {
        titleTextView = frameLayout.findViewById(R.id.title);
    }

    @Override
    public String getName() {
        return TitleComponent.class.getSimpleName();
    }

    @Override
    public void setUp(JZDataSource dataSource, int defaultUrlMapIndex, int screen, Object... objects) {
        if (objects.length != 0) titleTextView.setText(objects[0].toString());
    }
}
