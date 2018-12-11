package cn.jzvd;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.FrameLayout;

import java.lang.reflect.Constructor;
import java.util.LinkedHashMap;

import cn.jzvd.plugin.JZCorePlugin;
import cn.jzvd.plugin.Loader;
import cn.jzvd.plugin.ProgressPlugin;
import cn.jzvd.plugin.StartButtonPlugin;
import cn.jzvd.task.ProgressTimerTask;

/**
 * Created by Nathen on 16/7/30.
 */
public abstract class JZVideoPlayer extends FrameLayout {

    public static final String TAG = "JiaoZiVideoPlayer";

    private static final int FULL_SCREEN_NORMAL_DELAY = 300;
    private static long CLICK_QUIT_FULLSCREEN_TIME = 0;

    protected static JZUserAction JZ_USER_EVENT;

    public static final int SCREEN_WINDOW_NORMAL = 0;
    public static final int SCREEN_WINDOW_LIST = 1;
    public static final int SCREEN_WINDOW_FULLSCREEN = 2;
    public static final int SCREEN_WINDOW_TINY = 3;

    public static final String URL_KEY_DEFAULT = "URL_KEY_DEFAULT";//Key used when playing one address.
    public static int FULLSCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_SENSOR;
    public static int NORMAL_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    public static boolean SAVE_PROGRESS = true;

    public int currentScreen = -1;
    private Object[] objects = null;

    private int widthRatio = 0;
    private int heightRatio = 0;
    public JZDataSource dataSource;
    public int currentUrlMapIndex = 0;
    public int positionInList = -1;

    boolean tmp_test_back = false;
    private final JZVideoPlayerStateMachine stateMachine = new JZVideoPlayerStateMachine(this);
    protected final Loader loader = new Loader();

    public JZVideoPlayer(Context context) {
        super(context);
        init(context);
    }

    public JZVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void init(Context context) {
        View.inflate(context, R.layout.jz_layout, this);
        loader.registerControlPlugins(this);

        try {
            if (isCurrentPlay()) {
                NORMAL_ORIENTATION = ((AppCompatActivity) context).getRequestedOrientation();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JZVideoPlayerStateMachine getStateMachine() {
        return stateMachine;
    }

    public void setProgressAndText() { }

    public void setBufferProgress(int bufferProgress) { }

    public void dismissRegisteredPlugins() {
        for (JZCorePlugin plugin : loader.getAllRegisteredPlugins()) {
            plugin.onDismissControlView();
        }
    }

    public boolean isCurrentPlay() {
        return isCurrentJZVD()
                && dataSource.containsUri(JZMediaManager.getCurrentPath());//不仅正在播放的url不能一样，并且各个清晰度也不能一样
    }

    public boolean isCurrentJZVD() {
        return JZVideoPlayerManager.getCurrentJzvd() != null
                && JZVideoPlayerManager.getCurrentJzvd() == this;
    }

    public static void releaseAllVideos() {
        if ((System.currentTimeMillis() - CLICK_QUIT_FULLSCREEN_TIME) > FULL_SCREEN_NORMAL_DELAY) {
            Log.d(TAG, "releaseAllVideos");
            JZVideoPlayerManager.completeAll();
            JZMediaManager.instance().positionInList = -1;
            JZMediaManager.instance().releaseMediaPlayer();
        }
    }

    public static void startFullscreen(Context context, Class _class, String url, Object... objects) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put(URL_KEY_DEFAULT, url);
        startFullscreen(context, _class, new JZDataSource(map), 0, objects);
    }

    public static void startFullscreen(Context context, Class _class, JZDataSource dataSource, int defaultUrlMapIndex, Object... objects) {
        JZActionBarManager.hideSupportActionBar(context);
        JZUtils.setRequestedOrientation(context, FULLSCREEN_ORIENTATION);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        JZVideoPlayer jzVideoPlayer = newInstanceFrom(context, _class, R.id.jz_fullscreen_id, params);

        if(jzVideoPlayer != null) {
//            final Animation ra = AnimationUtils.loadAnimation(context, R.anim.start_fullscreen);
//            jzVideoPlayer.setAnimation(ra);
            jzVideoPlayer.initTextureView();
            jzVideoPlayer.setUp(dataSource, defaultUrlMapIndex, JZVideoPlayerStandard.SCREEN_WINDOW_FULLSCREEN, objects);
            CLICK_QUIT_FULLSCREEN_TIME = System.currentTimeMillis();

            StartButtonPlugin startButton = jzVideoPlayer.loader.getControlPluginNamed(StartButtonPlugin.class);
            if (startButton != null) startButton.performClick();
        }
    }

    public abstract void initTextureView();

    private static JZVideoPlayer newInstanceFrom(Context context, Class _class, @IdRes int id, FrameLayout.LayoutParams lp) {
        ViewGroup vp = (JZUtils.scanForActivity(context)).findViewById(Window.ID_ANDROID_CONTENT);
        View old = vp.findViewById(id);
        if (old != null) {
            vp.removeView(old);
        }

        try {
            Constructor<JZVideoPlayer> constructor = _class.getConstructor(Context.class);
            JZVideoPlayer jzVideoPlayer = constructor.newInstance(context);
            jzVideoPlayer.setId(id);
            vp.addView(jzVideoPlayer, lp);
            return jzVideoPlayer;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean backPress() {
        Log.i(TAG, "backPress");
        if ((System.currentTimeMillis() - CLICK_QUIT_FULLSCREEN_TIME) < FULL_SCREEN_NORMAL_DELAY)
            return false;

        if (JZVideoPlayerManager.getSecondFloor() != null) {
            CLICK_QUIT_FULLSCREEN_TIME = System.currentTimeMillis();
            if (JZVideoPlayerManager.getFirstFloor().dataSource.containsUri(JZMediaManager.getCurrentPath())) {
                JZVideoPlayer jzVideoPlayer = JZVideoPlayerManager.getSecondFloor();
                jzVideoPlayer.onEvent(jzVideoPlayer.currentScreen == JZVideoPlayerStandard.SCREEN_WINDOW_FULLSCREEN ?
                        JZUserAction.ON_QUIT_FULLSCREEN :
                        JZUserAction.ON_QUIT_TINYSCREEN);
                JZVideoPlayerManager.getFirstFloor().playOnThisJzvd();
            } else {
                quitFullscreenOrTinyWindow();
            }
            return true;
        } else if (JZVideoPlayerManager.getFirstFloor() != null &&
                (JZVideoPlayerManager.getFirstFloor().currentScreen == SCREEN_WINDOW_FULLSCREEN ||
                        JZVideoPlayerManager.getFirstFloor().currentScreen == SCREEN_WINDOW_TINY)) {//以前我总想把这两个判断写到一起，这分明是两个独立是逻辑
            CLICK_QUIT_FULLSCREEN_TIME = System.currentTimeMillis();
            quitFullscreenOrTinyWindow();
            return true;
        }
        return false;
    }

    public static void quitFullscreenOrTinyWindow() {
        //直接退出全屏和小窗
        JZVideoPlayerManager.getFirstFloor().onQuitFullscreenOrTinyWindow();
        JZMediaManager.instance().releaseMediaPlayer();
        JZVideoPlayerManager.completeAll();
    }

    public void onQuitFullscreenOrTinyWindow() {
        JZVideoPlayerManager.setSecondFloor(null);
    }

    public static void clearSavedProgress(Context context, String url) {
        JZUtils.clearSavedProgress(context, url);
    }

    public static void setJzUserAction(JZUserAction jzUserEvent) {
        JZ_USER_EVENT = jzUserEvent;
    }

    public static void goOnPlayOnResume() {
        if (JZVideoPlayerManager.getCurrentJzvd() != null) {
            JZVideoPlayer jzvd = JZVideoPlayerManager.getCurrentJzvd();
            if (jzvd.stateMachine.currentStatePause()) {
                jzvd.stateMachine.setPlaying();
            }
        }
    }

    public static void goOnPlayOnPause() {
        if (JZVideoPlayerManager.getCurrentJzvd() != null) {
            JZVideoPlayer jzvd = JZVideoPlayerManager.getCurrentJzvd();
            JZVideoPlayerStateMachine stateMachine = jzvd.stateMachine;
            if (stateMachine.currentStateAutoComplete()
                    || stateMachine.currentStateNormal()
                    || stateMachine.currentStateError()) {
//                JZVideoPlayer.releaseAllVideos();
            } else {
                stateMachine.setPause();
            }
        }
    }

    public static void onScrollAutoTiny(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int lastVisibleItem = firstVisibleItem + visibleItemCount;
        int currentPlayPosition = JZMediaManager.instance().positionInList;
        if (currentPlayPosition >= 0) {
            if ((currentPlayPosition < firstVisibleItem || currentPlayPosition > (lastVisibleItem - 1))) {
                if (JZVideoPlayerManager.getCurrentJzvd() != null &&
                        JZVideoPlayerManager.getCurrentJzvd().currentScreen != JZVideoPlayer.SCREEN_WINDOW_TINY &&
                        JZVideoPlayerManager.getCurrentJzvd().currentScreen != JZVideoPlayer.SCREEN_WINDOW_FULLSCREEN) {
                    if (JZVideoPlayerManager.getCurrentJzvd().stateMachine.currentStatePause()) {
                        JZVideoPlayer.releaseAllVideos();
                    } else {
                        Log.e(TAG, "onScroll: out screen");
                        JZVideoPlayerManager.getCurrentJzvd().startWindowTiny();
                    }
                }
            } else {
                if (JZVideoPlayerManager.getCurrentJzvd() != null &&
                        JZVideoPlayerManager.getCurrentJzvd().currentScreen == JZVideoPlayer.SCREEN_WINDOW_TINY) {
                    Log.e(TAG, "onScroll: into screen");
                    JZVideoPlayer.backPress();
                }
            }
        }
    }

    public static void onScrollReleaseAllVideos(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int lastVisibleItem = firstVisibleItem + visibleItemCount;
        int currentPlayPosition = JZMediaManager.instance().positionInList;
        Log.e(TAG, "onScrollReleaseAllVideos: " +
                currentPlayPosition + " " + firstVisibleItem + " " + currentPlayPosition + " " + lastVisibleItem);
        if (currentPlayPosition >= 0) {
            if ((currentPlayPosition < firstVisibleItem || currentPlayPosition > (lastVisibleItem - 1))) {
                if (JZVideoPlayerManager.getCurrentJzvd().currentScreen != JZVideoPlayer.SCREEN_WINDOW_FULLSCREEN) {
                    JZVideoPlayer.releaseAllVideos();//为什么最后一个视频横屏会调用这个，其他地方不会
                }
            }
        }
    }

    public static void onChildViewAttachedToWindow(View view, int jzvdId) {
        if (JZVideoPlayerManager.getCurrentJzvd() != null && JZVideoPlayerManager.getCurrentJzvd().currentScreen == JZVideoPlayer.SCREEN_WINDOW_TINY) {
            JZVideoPlayer videoPlayer = view.findViewById(jzvdId);
            if (videoPlayer != null && videoPlayer.dataSource.getCurrentPath(videoPlayer.currentUrlMapIndex).equals(JZMediaManager.getCurrentPath())) {
                JZVideoPlayer.backPress();
            }
        }
    }

    public static void onChildViewDetachedFromWindow(View view) {
        if (JZVideoPlayerManager.getCurrentJzvd() != null && JZVideoPlayerManager.getCurrentJzvd().currentScreen != JZVideoPlayer.SCREEN_WINDOW_TINY) {
            JZVideoPlayer videoPlayer = JZVideoPlayerManager.getCurrentJzvd();
            if (((ViewGroup) view).indexOfChild(videoPlayer) != -1) {
                if (videoPlayer.stateMachine.currentStatePause()) {
                    JZVideoPlayer.releaseAllVideos();
                } else {
                    videoPlayer.startWindowTiny();
                }
            }
        }
    }

    public Object getCurrentUrl() {
        return dataSource.getCurrentPath(currentUrlMapIndex);
    }

    public void onClickUiToggle() { }

    public void setUp(String url, int screen, Object... objects) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put(URL_KEY_DEFAULT, url);
        setUp(new JZDataSource(map), 0, screen, objects);
    }

    public void setUp(JZDataSource dataSource, int defaultUrlMapIndex, int screen, Object... objects) {
        if (this.dataSource != null && dataSource.getCurrentPath(currentUrlMapIndex) != null &&
                this.dataSource.getCurrentPath(currentUrlMapIndex).equals(dataSource.getCurrentPath(currentUrlMapIndex))) {
            return;
        }
        if (isCurrentJZVD() && dataSource.containsUri(JZMediaManager.getCurrentPath())) {
            long position = 0;
            try {
                position = JZMediaManager.getCurrentPosition();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            if (position != 0) {
                JZUtils.saveProgress(getContext(), JZMediaManager.getCurrentPath(), position);
            }
            JZMediaManager.instance().releaseMediaPlayer();
        } else if (isCurrentJZVD() && !dataSource.containsUri(JZMediaManager.getCurrentPath())) {
            startWindowTiny();
        } else if (!isCurrentJZVD() && dataSource.containsUri(JZMediaManager.getCurrentPath())) {
            if (JZVideoPlayerManager.getCurrentJzvd() != null &&
                    JZVideoPlayerManager.getCurrentJzvd().currentScreen == JZVideoPlayer.SCREEN_WINDOW_TINY) {
                //需要退出小窗退到我这里，我这里是第一层级
                tmp_test_back = true;
            }
        } else if (!isCurrentJZVD() && !dataSource.containsUri(JZMediaManager.getCurrentPath())) {
        }
        this.dataSource = dataSource;
        this.currentUrlMapIndex = defaultUrlMapIndex;
        this.currentScreen = screen;
        this.objects = objects;
        getStateMachine().setNormal();
    }

    public void startVideo() {
        JZUtils.scanForActivity(getContext()).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        JZMediaManager.setDataSource(dataSource);
        JZMediaManager.setCurrentPath(dataSource.getCurrentPath(currentUrlMapIndex));
        JZMediaManager.instance().positionInList = positionInList;
        getStateMachine().setPreparing();
        JZVideoPlayerManager.setFirstFloor(this);
    }

    public void onPrepared() {
        stateMachine.setPrepared(dataSource);
    }

    public void onStateNormal() { }

    public void onStatePreparing() { }

    public void onStatePreparingChangingUrl(int urlMapIndex) {
        this.currentUrlMapIndex = urlMapIndex;
        JZMediaManager.setDataSource(dataSource);
        JZMediaManager.setCurrentPath(dataSource.getCurrentPath(currentUrlMapIndex));
        JZMediaManager.instance().prepare();
    }

    public void onStatePlaying() {
        JZMediaManager.start();
    }

    public void onStatePause() {
        JZMediaManager.pause();
    }

    public void onStateError() { }

    public void onStateAutoComplete() { }

    public void onInfo(int what, int extra) {
        Log.d(TAG, "onInfo what - " + what + " extra - " + extra);
    }

    public void onError(int what, int extra) {
        Log.e(TAG, "onError " + what + " - " + extra + " [" + this.hashCode() + "] ");
        if (what != 38 && extra != -38 && what != -38 && extra != 38 && extra != -19) {
            stateMachine.setError();
            if (isCurrentPlay()) {
                JZMediaManager.instance().releaseMediaPlayer();
            }
        }
    }

    public void setScreenRatio(int widthRatio, int heightRatio) {
        this.widthRatio = widthRatio;
        this.heightRatio = heightRatio;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (currentScreen == SCREEN_WINDOW_FULLSCREEN || currentScreen == SCREEN_WINDOW_TINY) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        if (widthRatio != 0 && heightRatio != 0) {
            int specWidth = MeasureSpec.getSize(widthMeasureSpec);
            int specHeight = (int) ((specWidth * (float) heightRatio) / widthRatio);
            setMeasuredDimension(specWidth, specHeight);

            int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(specWidth, MeasureSpec.EXACTLY);
            int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(specHeight, MeasureSpec.EXACTLY);
            getChildAt(0).measure(childWidthMeasureSpec, childHeightMeasureSpec);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    public void onAutoCompletion() {
        Runtime.getRuntime().gc();
        Log.i(TAG, "onAutoCompletion " + " [" + this.hashCode() + "] ");
        onEvent(JZUserAction.ON_AUTO_COMPLETE);

        stateMachine.setAutoComplete();

        if (currentScreen == SCREEN_WINDOW_FULLSCREEN || currentScreen == SCREEN_WINDOW_TINY) {
            backPress();
        }
        JZMediaManager.instance().releaseMediaPlayer();
        JZUtils.saveProgress(getContext(), dataSource.getCurrentPath(currentUrlMapIndex), 0);
    }

    public void onCompletion() {
        Log.i(TAG, "onCompletion " + " [" + this.hashCode() + "] ");
        if (stateMachine.currentStatePlaying() || stateMachine.currentStatePause()) {
            long position = getCurrentPositionWhenPlaying();
            JZUtils.saveProgress(getContext(), dataSource.getCurrentPath(currentUrlMapIndex), position);
        }
        getStateMachine().setNormal();

        JZMediaManager.instance().currentVideoWidth = 0;
        JZMediaManager.instance().currentVideoHeight = 0;

        JZUtils.scanForActivity(getContext()).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        clearFullscreenLayout();
        JZUtils.setRequestedOrientation(getContext(), NORMAL_ORIENTATION);

        if (JZMediaManager.surface != null) JZMediaManager.surface.release();
        if (JZMediaManager.savedSurfaceTexture != null)
            JZMediaManager.savedSurfaceTexture.release();
        JZMediaManager.textureView = null;
        JZMediaManager.savedSurfaceTexture = null;
    }

    public void release() {
        if (dataSource.getCurrentPath(currentUrlMapIndex).equals(JZMediaManager.getCurrentPath()) &&
                (System.currentTimeMillis() - CLICK_QUIT_FULLSCREEN_TIME) > FULL_SCREEN_NORMAL_DELAY) {
            //在非全屏的情况下只能backPress()
            if (JZVideoPlayerManager.getSecondFloor() != null &&
                    JZVideoPlayerManager.getSecondFloor().currentScreen == SCREEN_WINDOW_FULLSCREEN) {//点击全屏
            } else if (JZVideoPlayerManager.getSecondFloor() == null && JZVideoPlayerManager.getFirstFloor() != null &&
                    JZVideoPlayerManager.getFirstFloor().currentScreen == SCREEN_WINDOW_FULLSCREEN) {//直接全屏
            } else {
                Log.d(TAG, "releaseMediaPlayer [" + this.hashCode() + "]");
                releaseAllVideos();
            }
        }
    }

    public void clearFullscreenLayout() {
        ViewGroup vp = (JZUtils.scanForActivity(getContext()))//.getWindow().getDecorView();
                .findViewById(Window.ID_ANDROID_CONTENT);
        View oldF = vp.findViewById(R.id.jz_fullscreen_id);
        View oldT = vp.findViewById(R.id.jz_tiny_id);
        if (oldF != null) {
            vp.removeView(oldF);
        }
        if (oldT != null) {
            vp.removeView(oldT);
        }
        JZActionBarManager.showSupportActionBar(getContext());
    }

    public long getCurrentPositionWhenPlaying() {
        long position = 0;
        //TODO 这块的判断应该根据MediaPlayer来
        if (stateMachine.currentStatePlaying() || stateMachine.currentStatePause()) {
            try {
                position = JZMediaManager.getCurrentPosition();
            } catch (IllegalStateException e) {
                e.printStackTrace();
                return position;
            }
        }
        return position;
    }

    public long getDuration() {
        long duration = 0;
        //TODO MediaPlayer 判空的问题
//        if (JZMediaManager.instance().mediaPlayer == null) return duration;
        try {
            duration = JZMediaManager.getDuration();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return duration;
        }
        return duration;
    }

    public void startWindowFullscreen() {
        Log.i(TAG, "startWindowFullscreen " + " [" + this.hashCode() + "] ");
        JZActionBarManager.hideSupportActionBar(getContext());

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        JZVideoPlayer jzVideoPlayer = newInstanceFrom(getContext(), JZVideoPlayer.this.getClass(), R.id.jz_fullscreen_id, params);

        if (jzVideoPlayer != null) {
            jzVideoPlayer.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN);
            jzVideoPlayer.setUp(dataSource, currentUrlMapIndex, JZVideoPlayerStandard.SCREEN_WINDOW_FULLSCREEN, objects);
            jzVideoPlayer.stateMachine.copyStateFrom(this);
            JZVideoPlayerManager.setSecondFloor(jzVideoPlayer);
//            final Animation ra = AnimationUtils.loadAnimation(getContext(), R.anim.start_fullscreen);
//            jzVideoPlayer.setAnimation(ra);
            JZUtils.setRequestedOrientation(getContext(), FULLSCREEN_ORIENTATION);

            getStateMachine().setNormal();

            ProgressPlugin progressPlugin = jzVideoPlayer.loader.getControlPluginNamed(ProgressPlugin.class);
            if (progressPlugin != null) progressPlugin.copySecondaryProgressFrom(this.loader);

            ProgressTimerTask.start(jzVideoPlayer);
            CLICK_QUIT_FULLSCREEN_TIME = System.currentTimeMillis();
        }
    }

    public void startWindowTiny() {
        Log.i(TAG, "startWindowTiny " + " [" + this.hashCode() + "] ");
        onEvent(JZUserAction.ON_ENTER_TINYSCREEN);
        if (stateMachine.currentStateNormal()
                || stateMachine.currentStateError()
                || stateMachine.currentStateAutoComplete())
            return;

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(400, 400);
        params.gravity = Gravity.RIGHT | Gravity.BOTTOM;
        JZVideoPlayer jzVideoPlayer = newInstanceFrom(getContext(), JZVideoPlayer.this.getClass(), R.id.jz_tiny_id, params);

        if (jzVideoPlayer != null) {
            jzVideoPlayer.setUp(dataSource, currentUrlMapIndex, JZVideoPlayerStandard.SCREEN_WINDOW_TINY, objects);
            jzVideoPlayer.stateMachine.copyStateFrom(this);
            JZVideoPlayerManager.setSecondFloor(jzVideoPlayer);
            getStateMachine().setNormal();
        }
    }

    //退出全屏和小窗的方法
    public void playOnThisJzvd() {
        Log.i(TAG, "playOnThisJzvd " + " [" + this.hashCode() + "] ");
        //1.清空全屏和小窗的jzvd
        currentUrlMapIndex = JZVideoPlayerManager.getSecondFloor().currentUrlMapIndex;
        //2.在本jzvd上播放
        stateMachine.copyStateFrom(JZVideoPlayerManager.getSecondFloor());
//        removeTextureView();
    }

    //重力感应的时候调用的函数，
    public void autoFullscreen(float x) {
        if (isCurrentPlay()
                && (stateMachine.currentStatePlaying() || stateMachine.currentStatePause())
                && currentScreen != SCREEN_WINDOW_FULLSCREEN
                && currentScreen != SCREEN_WINDOW_TINY) {
            if (x > 0) {
                JZUtils.setRequestedOrientation(getContext(), ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else {
                JZUtils.setRequestedOrientation(getContext(), ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
            }
            onEvent(JZUserAction.ON_ENTER_FULLSCREEN);
            startWindowFullscreen();
        }
    }

    public void onEvent(int type) {
        if (JZ_USER_EVENT != null && isCurrentPlay() && dataSource != null) {
            JZ_USER_EVENT.onEvent(type, dataSource.getCurrentPath(currentUrlMapIndex), currentScreen, objects);
        }
    }

    public static void setMediaInterface(JZMediaInterface mediaInterface) {
        JZMediaManager.instance().jzMediaInterface = mediaInterface;
    }

    //TODO 是否有用
    public void onSeekComplete() {

    }

    public abstract void onVideoSizeChanged();
}
