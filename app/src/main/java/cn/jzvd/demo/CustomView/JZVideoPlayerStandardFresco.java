package cn.jzvd.demo.CustomView;

import android.content.Context;
import android.util.AttributeSet;

import cn.jzvd.JZVideoPlayerStandard;
import cn.jzvd.demo.R;

//import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Just replace thumb from ImageView to SimpleDraweeView
 * Created by Nathen
 * On 2016/05/01 22:59
 */
public class JZVideoPlayerStandardFresco extends JZVideoPlayerStandard {
    //    public SimpleDraweeView thumbImageView;

    public JZVideoPlayerStandardFresco(Context context) {
        super(context);
    }

    public JZVideoPlayerStandardFresco(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void init(Context context) {
        super.init(context);
//        thumbImageView = findViewById(R.id.thumb);

//        thumbImageView.setOnClickListener(this);
    }

    @Override
    public void setUp(String url, int screen, Object... objects) {
        super.setUp(url, screen, objects);
        if (objects.length == 0) return;
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_standard_fresco;
    }


}
