package com.maniac.luis.series.utilidades;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.ViewGroup;

import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;

public abstract class ViewGroupTarget<Z> extends ViewTarget<ViewGroup, Z> implements GlideAnimation.ViewAdapter {

    public ViewGroupTarget(ViewGroup view) {
        super(view);
    }

    /**
     * Returns the current {@link android.graphics.drawable.Drawable} being displayed in the view using
     * {@link android.widget.ImageView#getDrawable()}.
     */
    @Override
    public Drawable getCurrentDrawable() {
        return view.getBackground();
    }

    /**
     * Sets the given {@link android.graphics.drawable.Drawable} on the view using
     * {@link android.widget.ImageView#setImageDrawable(android.graphics.drawable.Drawable)}.
     *
     * @param drawable {@inheritDoc}
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void setDrawable(Drawable drawable) {
        view.setBackground(drawable);
    }

    /**
     * Sets the given {@link android.graphics.drawable.Drawable} on the view using
     * {@link android.widget.ImageView#setImageDrawable(android.graphics.drawable.Drawable)}.
     *
     * @param placeholder {@inheritDoc}
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onLoadStarted(Drawable placeholder) {
        view.setBackground(placeholder);
    }

    /**
     * Sets the given {@link android.graphics.drawable.Drawable} on the view using
     * {@link android.widget.ImageView#setImageDrawable(android.graphics.drawable.Drawable)}.
     *
     * @param errorDrawable {@inheritDoc}
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onLoadFailed(Exception e, Drawable errorDrawable) {
        view.setBackground(errorDrawable);
    }

    /**
     * Sets the given {@link android.graphics.drawable.Drawable} on the view using
     * {@link android.widget.ImageView#setImageDrawable(android.graphics.drawable.Drawable)}.
     *
     * @param placeholder {@inheritDoc}
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onLoadCleared(Drawable placeholder) {
        view.setBackground(placeholder);
    }

    @Override
    public void onResourceReady(Z resource, GlideAnimation<? super Z> glideAnimation) {

        this.setResource(resource);
    }

    protected abstract void setResource(Z resource);

}
