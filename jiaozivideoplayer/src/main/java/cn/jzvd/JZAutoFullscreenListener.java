package cn.jzvd;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import static android.content.Context.SENSOR_SERVICE;
import static cn.jzvd.JZVideoPlayer.SCREEN_WINDOW_FULLSCREEN;
import static cn.jzvd.JZVideoPlayer.backPress;

public class JZAutoFullscreenListener implements SensorEventListener {

    private static long lastAutoFullscreenTime = 0;
    private final Sensor accelerometerSensor;
    private final SensorManager sensorManager;

    private static JZAutoFullscreenListener INSTANCE;

    private JZAutoFullscreenListener(Context context) {
        this.sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        this.accelerometerSensor = this.sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public static void register(Context context) {
        if(INSTANCE == null) {
            INSTANCE = new JZAutoFullscreenListener(context);
        }
        INSTANCE.sensorManager.registerListener(INSTANCE, INSTANCE.accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public static void unregister() {
        if(INSTANCE != null) {
            INSTANCE.sensorManager.unregisterListener(INSTANCE);
        }
    }


    public void autoQuitFullscreen() {
        if ((System.currentTimeMillis() - lastAutoFullscreenTime) > 2000) {
            JZVideoPlayer jzvd = JZVideoPlayerManager.getCurrentJzvd();
            if(jzvd != null
                    && jzvd.isCurrentPlay()
                    && jzvd.getStateMachine().currentStatePlaying()
                    && jzvd.currentScreen == SCREEN_WINDOW_FULLSCREEN) {
                lastAutoFullscreenTime = System.currentTimeMillis();
                backPress();
            }
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {//可以得到传感器实时测量出来的变化值
        final float x = event.values[SensorManager.DATA_X];
        float y = event.values[SensorManager.DATA_Y];
        float z = event.values[SensorManager.DATA_Z];
        //过滤掉用力过猛会有一个反向的大数值
        if (x < -12 || x > 12) {
            if ((System.currentTimeMillis() - lastAutoFullscreenTime) > 2000) {
                JZVideoPlayer jzvd = JZVideoPlayerManager.getCurrentJzvd();
                if (jzvd != null) {
                    jzvd.autoFullscreen(x);
                }
                lastAutoFullscreenTime = System.currentTimeMillis();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}