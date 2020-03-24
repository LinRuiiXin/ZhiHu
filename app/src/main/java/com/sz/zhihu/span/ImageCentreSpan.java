package com.sz.zhihu.span;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;

public class ImageCentreSpan extends ImageSpan {
    public  static final int CENTRE = 2;

    public ImageCentreSpan(@NonNull Drawable d, int verticalAlignment) {
        super(d, verticalAlignment);

    }

    public ImageCentreSpan(@NonNull Context context, @NonNull Bitmap bitmap) {
        super(context, bitmap);
    }

    public ImageCentreSpan(@NonNull Context context, @NonNull Bitmap b, int verticalAlignment) {
        super(context, b, verticalAlignment);
    }

/*    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
        Drawable b = getCachedDrawable();
        canvas.save();


        int transY = 0;
        if (mVerticalAlignment == ALIGN_BASELINE) {
            transY -= paint.getFontMetricsInt().descent;
        }else if(mVerticalAlignment==ALIGN_BOTTOM){
            transY=  bottom - b.getBounds().bottom;
        }else {
            Paint.FontMetricsInt fontMetricsInt = paint.getFontMetricsInt();

            // y 是基准线  Ascent是基准线 之上字符最高处的距离   descent 是基准线之下字符最低处的距离
            transY= (y + fontMetricsInt.descent + y + fontMetricsInt.ascent) / 2 - b.getBounds().bottom/2;

        }

        canvas.translate(x+270, transY);
        b.draw(canvas);
        canvas.restore();
    }


    private Drawable getCachedDrawable() {
        WeakReference<Drawable> wr = mDrawableRef;
        Drawable d = null;

        if (wr != null)
            d = wr.get();

        if (d == null) {
            d = getDrawable();
            mDrawableRef = new WeakReference<Drawable>(d);
        }

        return d;
    }*/

    private WeakReference<Drawable> mDrawableRef;
}
