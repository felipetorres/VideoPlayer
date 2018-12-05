package cn.jzvd.component;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.jzvd.JZDataSource;
import cn.jzvd.JZVideoPlayerStandard;
import cn.jzvd.R;

import static cn.jzvd.JZVideoPlayer.SCREEN_WINDOW_FULLSCREEN;

public class BatteryComponent {

    private static long LAST_GET_BATTERYLEVEL_TIME = 0;
    private static int LAST_GET_BATTERYLEVEL_PERCENT = 70;

    private Context context;
    private JZVideoPlayerStandard player;

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

    public BatteryComponent(JZVideoPlayerStandard player) {
        this.player = player;
        init(player);
    }

    private void init(FrameLayout frameLayout) {
        this.context = frameLayout.getContext();
        batteryTimeLayout = frameLayout.findViewById(R.id.battery_time_layout);
        videoCurrentTime = frameLayout.findViewById(R.id.video_current_time);
        batteryLevel = frameLayout.findViewById(R.id.battery_level);
    }

    public void setUp(JZDataSource dataSource, int defaultUrlMapIndex, int screen, Object... objects) {
        if(screen == SCREEN_WINDOW_FULLSCREEN) {
            batteryTimeLayout.setVisibility(View.VISIBLE);
        } else {
            batteryTimeLayout.setVisibility(View.GONE);
        }
        setSystemTimeAndBattery();
    }

    public void onClickUiToggle() {
        if (player.bottomContainer.getVisibility() != View.VISIBLE) {
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
