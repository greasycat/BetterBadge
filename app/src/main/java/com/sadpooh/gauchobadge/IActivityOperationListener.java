package com.sadpooh.gauchobadge;

import android.graphics.Bitmap;

public interface IActivityOperationListener {
    void onTestShow(String text);

    void onBadgeComplete(Bitmap badge);

    void onBadgeFailure(String error);
}
