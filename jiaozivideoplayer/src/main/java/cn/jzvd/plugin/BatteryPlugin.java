package cn.jzvd.plugin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.jzvd.JZDataSource;
import cn.jzvd.JZVideoPlayerStandard;
import cn.jzvd.R;
import cn.jzvd.ui.ContainerLocation;
import cn.jzvd.ui.PluginLocation;

import static cn.jzvd.JZVideoPlayer.SCREEN_WINDOW_FULLSCREEN;

public class BatteryPlugin extends JZUiPlugin {

    private static long LAST_GET_BATTERYLEVEL_TIME = 0;
    private static int LAST_GET_BATTERYLEVEL_PERCENT = 70;

    private final JZVideoPlayerStandard player;
    private LinearLayout batteryTimeLayout;
    private TextView videoCurrentTime;
    private ImageView batteryLevel;

    private BroadcastReceiver battertReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
                int level = intent.getIntExtra("level", 0);
                int scale = intent.getIntExtra("scale", 100);
                int percent = level * 100 / scale;
                LAST_GET_BATTERYLEVEL_PERCENT = percent;
                setBatteryLevel();
                context.unregisterReceiver(battertReceiver);
            }
        }
    };

    public BatteryPlugin(JZVideoPlayerStandard player) {
        super(player);
        this.player = player;
        super.container = ContainerLocation.TOP;
        super.location = PluginLocation.RIGHT;
    }

    @Override
    public void init(ViewGroup parent) {
        super.init(parent);
        batteryTimeLayout = parent.findViewById(R.id.battery_time_layout);
        videoCurrentTime = parent.findViewById(R.id.video_current_time);
        batteryLevel = parent.findViewById(R.id.battery_level);
    }

    @Override
    public int getLayoutId() {
        return R.layout.plugin_battery;
    }

    @Override
    public String getName() {
        return BatteryPlugin.class.getSimpleName();
    }

    @Override
    public void setUp(JZDataSource dataSource, int defaultUrlMapIndex, int screen, Object... objects) {
        if(screen == SCREEN_WINDOW_FULLSCREEN) {
            batteryTimeLayout.setVisibility(View.VISIBLE);
        } else {
            batteryTimeLayout.setVisibility(View.GONE);
        }
        setSystemTimeAndBattery();
    }

    @Override
    public void onClickUiToggle() {
        if (player.getBottomContainerVisibility() != View.VISIBLE) {
            setSystemTimeAndBattery();
            if (player.getStateMachine().currentStatePreparing()) {
                setSystemTimeAndBattery();
            }
        }
    }

    private void setSystemTimeAndBattery() {
        SimpleDateFormat dateFormater = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        videoCurrentTime.setText(dateFormater.format(date));
        if ((System.currentTimeMillis() - LAST_GET_BATTERYLEVEL_TIME) > 30000) {
            LAST_GET_BATTERYLEVEL_TIME = System.currentTimeMillis();
            context.registerReceiver(
                    battertReceiver,
                    new IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            );
        } else {
            setBatteryLevel();
        }
    }

    private void setBatteryLevel() {
        int percent = LAST_GET_BATTERYLEVEL_PERCENT;
        if (percent < 15) {
            batteryLevel.setBackgroundResource(R.drawable.jz_battery_level_10);
        } else if (percent < 40) {
            batteryLevel.setBackgroundResource(R.drawable.jz_battery_level_30);
        } else if (percent < 60) {
            batteryLevel.setBackgroundResource(R.drawable.jz_battery_level_50);
        } else if (percent < 80) {
            batteryLevel.setBackgroundResource(R.drawable.jz_battery_level_70);
        } else if (percent < 95) {
            batteryLevel.setBackgroundResource(R.drawable.jz_battery_level_90);
        } else if (percent <= 100) {
            batteryLevel.setBackgroundResource(R.drawable.jz_battery_level_100);
        }
    }
}
