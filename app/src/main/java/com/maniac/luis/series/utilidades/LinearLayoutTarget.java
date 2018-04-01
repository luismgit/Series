package com.maniac.luis.series.utilidades;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.LinearLayout;

import com.maniac.luis.series.utilidades.ViewGroupTarget;

public class LinearLayoutTarget extends ViewGroupTarget<Bitmap> {

    private Context context;

    public LinearLayoutTarget(Context context, LinearLayout linearLayout) {

        super(linearLayout);

        this.context = context;
    }

    /**
     * Sets the {@link android.graphics.Bitmap} on the view using
     * {@link android.widget.ImageView#setImageBitmap(android.graphics.Bitmap)}.
     *
     * @param resource The bitmap to display.
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void setResource(Bitmap resource) {

        view.setBackground(new BitmapDrawable(context.getResources(), resource));
    }

}