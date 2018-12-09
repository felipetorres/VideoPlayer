package cn.jzvd.component;

import android.view.ViewGroup;
import android.widget.TextView;

import cn.jzvd.JZDataSource;
import cn.jzvd.JZVideoPlayerStandard;
import cn.jzvd.R;
import cn.jzvd.ui.ContainerLocation;
import cn.jzvd.ui.PluginLocation;

public class TitleComponent extends JZUIComponent {

    protected TextView titleTextView;

    public TitleComponent(JZVideoPlayerStandard player) {
        super(player);
        super.container = ContainerLocation.TOP;
        super.location = PluginLocation.CENTER;
    }

    @Override
    public void init(ViewGroup parent) {
        super.init(parent);
        titleTextView = parent.findViewById(R.id.title);
    }

    @Override
    public int getLayoutId() {
        return R.layout.plugin_title;
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
