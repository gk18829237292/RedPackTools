package org.kk.redpacktools.service;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.PendingIntent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import org.kk.redpacktools.App;
import org.kk.redpacktools.db.dao.RedPackDao;
import org.kk.redpacktools.db.entities.RedPackLog;
import org.kk.redpacktools.utils.Logger;
import org.kk.redpacktools.utils.Spref;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

public class MyRedPackService extends AccessibilityService {

    Spref mSpref = null;

    private LinkedList<PendingIntent> pendingIntents = new LinkedList<>();
    private boolean mIsInRedPack = false; //是否在处理红包

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Logger.d("receive msg");
            if (!mIsInRedPack) {
                doBack();
                removeCallbacks(null);
            }
        }

    };


    /**
     * 监听四种类型
     * 1.typeWindowStateChanged
     * 2.typeWindowContentChanged
     * 3.typeWindowsChanged TO判断这个类型是否需要 2018年01月05日21:24:44 验证不需要这个类型
     * 4.typeNotificationStateChanged
     *
     * @param event
     */
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int type = event.getEventType();
        Logger.d("receiced type %08x", type);

        switch (type) {
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                handleWindowContentChanged(event);
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                handleWindowStateChanged(event);
                break;
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                handleNotificationStateChanged(event);
                break;
        }
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
    }

    /**
     * 处理屏幕状态改变
     * 1.目前分析只有在弹出红包的时候这个会触发
     *
     * @param event
     */
    private void handleWindowStateChanged(AccessibilityEvent event) {
        String btnViewId = "com.tencent.mm:id/c2i";
        AccessibilityNodeInfo rootNodeInfo = event.getSource();
        AccessibilityNodeInfo node = findNodeInfoByViewId(rootNodeInfo, btnViewId, null);
        if (node != null) {
            Logger.d("点开红包");
            doClick(node);
            handler.removeCallbacks(null);
            return;
        }

        //判断是不是已经点开了红包
        String moneyViewId = "com.tencent.mm:id/byw";
        node = findNodeInfoByViewId(rootNodeInfo, moneyViewId, null);
        if (node != null) {

            Logger.d("领到红包 %d", event.getWindowId());
            mIsInRedPack = false;
            //如果是在聊天界面
            double money = Double.parseDouble(node.getText().toString());
            String name = "未知";
            String nameViewId = "com.tencent.mm:id/bys";
            node = findNodeInfoByViewId(rootNodeInfo, nameViewId, null);
            if (node != null) {
                name = node.getText().toString();
                write2Log(money, name);
            }
            handler.sendEmptyMessage(1);
            return;
        }

        //判断是不是手慢了，没有领到
        String textViewId = "com.tencent.mm:id/c2h";
        node = findNodeInfoByViewId(rootNodeInfo, textViewId, "手慢了，红包派完了", "该红包已超过24小时。如已领取，可在“我的红包”中查看");
        if (node != null) {
            Logger.d("手慢了，没领到");
            mIsInRedPack = false;
            handler.sendEmptyMessage(1);
            return;
        }

    }

    private void handleWindowContentChanged(AccessibilityEvent event) {
        AccessibilityNodeInfo rootNodeInfo = event.getSource();
        String redPackViewId = "com.tencent.mm:id/aeb";
        AccessibilityNodeInfo tempNodeInfo = rootNodeInfo;
        while (rootNodeInfo.getParent() != null) {
            rootNodeInfo = rootNodeInfo.getParent();
        }
        // 判断聊天消息里是不是收到了红包
        AccessibilityNodeInfo node = findNodeInfoByViewId(rootNodeInfo, redPackViewId, "领取红包");
        if (node != null) {
            mIsInRedPack = true;//正在处理红包
            doClick(node);
            return;
        }


        //判断是不是在聊天列表界面，是的话 查找是否收到红包

        String redTextViewId = "com.tencent.mm:id/apv";
        node = findNodeInfoByViewId(rootNodeInfo, redTextViewId, "[微信红包]");
        if (node != null) {
            doClick(node);
            return;
        }

        clickNotification();
    }

    private void handleNotificationStateChanged(AccessibilityEvent event) {
        String content = event.getText().toString();
        Logger.d("content is " + content);
        //不是微信红包，不处理
        if (!content.contains("[微信红包]")) {
            return;
        }
        Parcelable parcelable = event.getParcelableData();
        if (parcelable instanceof Notification) {
            PendingIntent intent = ((Notification) parcelable).contentIntent;
            if (intent != null) {
                pendingIntents.push(intent);
            }
        }

        clickNotification();
    }

    private void clickNotification() {
        if (mIsInRedPack) return;
        while (!pendingIntents.isEmpty()) {
            try {
                pendingIntents.pop().send();
                return;
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据viewId 和 节点内容查找都应的节点
     *
     * @param rootNodeInfo
     * @param viewId
     * @param textList     节点内容，如果为null，则认为不过滤内容
     * @return 找到的节点
     */
    private AccessibilityNodeInfo findNodeInfoByViewId(AccessibilityNodeInfo rootNodeInfo, String viewId, String... textList) {
        List<AccessibilityNodeInfo> nodeInfoList = rootNodeInfo.findAccessibilityNodeInfosByViewId(viewId);
        if (!nodeInfoList.isEmpty()) {
            if (textList == null) {
                return nodeInfoList.get(0);
            } else {
                for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                    for (String text : textList) {
                        if (nodeInfo.getText().toString().contains(text)) {
                            return nodeInfo;
                        }
                    }
                }
            }
        }
        return null;
    }

    private void doClick(AccessibilityNodeInfo node) {
        while (node != null && !node.isClickable()) {
            node = node.getParent();
        }
        if (node != null) {
            node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
    }

    private void doBack() {
        performGlobalAction(GLOBAL_ACTION_BACK);
    }

    private void write2Log(double money, String name) {
        RedPackLog redPackLog = new RedPackLog(name, money, new Date().getTime());
        Flowable.just(redPackLog).subscribeOn(Schedulers.io()).observeOn(Schedulers.single()).subscribe((log)->{
            App.getDB().redPackDao().insert(log);
        });
    }




}
