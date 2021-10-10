package com.sadpooh.gauchobadge;

import android.os.Handler;
import android.os.Looper;

import com.sadpooh.gauchobadge.network.StudentHealthService;

public class StudentServiceRunnable implements Runnable {
    private Handler mainUiHandler;
    private IActivityOperationListener IActivityOperationListener;

    public void initRunnable(IActivityOperationListener IActivityOperationListener) {
        this.IActivityOperationListener = IActivityOperationListener;
        mainUiHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void run() {

        StudentHealthService studentHealthService = new StudentHealthService();
        studentHealthService.bypassDualAuthPage();
        String output = studentHealthService.lastResponse;

        mainUiHandler.post(new Runnable() {
            @Override
            public void run() {
                IActivityOperationListener.onTestShow(output);
            }
        });

    }
}
