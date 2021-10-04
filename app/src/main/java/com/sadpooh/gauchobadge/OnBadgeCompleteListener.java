package com.sadpooh.gauchobadge;

import android.graphics.Bitmap;

public interface OnBadgeCompleteListener {
    public abstract void onTestShow(String text);
    public abstract void onBadgeComplete(Bitmap badge);
    public abstract void onBadgeFailure(String error);
}
