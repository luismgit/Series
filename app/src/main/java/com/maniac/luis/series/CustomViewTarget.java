package com.maniac.luis.series;

import android.app.Activity;
import android.graphics.Point;
import android.view.View;

import com.github.amlcurran.showcaseview.targets.Target;

public class CustomViewTarget implements Target {

    private final View mView;
    private int offsetX;
    private int offsetY;

    public CustomViewTarget(View view) {
        mView = view;
    }

    public CustomViewTarget(int viewId, int offsetX, int offsetY, Activity activity) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        mView = activity.findViewById(viewId);
    }

    @Override
    public Point getPoint() {
        int[] location = new int[2];
        mView.getLocationInWindow(location);
        int x = location[0] + mView.getWidth() / 2 + offsetX;
        int y = location[1] + mView.getHeight() / 2 + offsetY;
        return new Point(x, y);
    }
}
