package org.kk.redpacktools.service;

import android.accessibilityservice.AccessibilityService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.view.accessibility.AccessibilityEvent;

import org.kk.redpacktools.utils.Logger;
import org.kk.redpacktools.utils.Spref;

public class MyRedPackService extends AccessibilityService {

    Spref mSpref = null;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {

    }

    @Override
    public void onInterrupt() {

    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
    }
}
