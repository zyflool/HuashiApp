package net.muxi.huashiapp.common.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.view.View;

import net.muxi.huashiapp.common.util.DimensUtil;

/**
 * Created by ybao on 16/9/20.
 * 指引的背景 view
 */
public class GuideBgView extends View {

    private static final int RADIUS = DimensUtil.dp2px(28);

    public GuideBgView(Context context) {
        super(context);
        setBackgroundColor(Color.BLACK);
        setAlpha(0.7f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        p.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawCircle(DimensUtil.getScreenWidth() - RADIUS, RADIUS, RADIUS, p);
    }
}
