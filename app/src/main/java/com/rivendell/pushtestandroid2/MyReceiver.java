package com.rivendell.pushtestandroid2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

public class MyReceiver extends BroadcastReceiver {

    private static final String PUSH_ACTION = "com.baidu.techain.push.action.PUSH_EVENT";
    private static final int TYPE_REGISTRATION = 1;
    private static final int TYPE_CONNECTION = 2;
    private static final int TYPE_MESSAGE_RECEIVED = 3;
    private static final int TYPE_NOTIFICATION_RECEIVED = 4;
    private static final int TYPE_NOTIFICATION_OPENED = 5;

    public static boolean sShowedInitSuccess = false;

    @Override
    public void onReceive(Context arg0, Intent arg1) {
        String action = arg1.getAction();
        if (!PUSH_ACTION.equals(action)) {
            return;
        }
        Bundle bundle = arg1.getExtras();
        int type = bundle.getInt("event_type", -1);
        switch (type) {
            case TYPE_REGISTRATION:
                String uid = bundle.getString("push_uid");
                Log.d("Push_Demo", "receive TYPE_REGISTRATION, uid=" + uid);
                if (!sShowedInitSuccess) {
                    MainActivity.print("init successfully", 0);
                    sShowedInitSuccess = true;
                    // MainActivity.callGetPushIdRemote();
                }
                break;
            case TYPE_CONNECTION:
                boolean status = bundle.getBoolean("conn_status");
                Log.d("Push_Demo", "receive TYPE_CONNECTION, status=" + status);
                MainActivity.print("connection state changed, current state is:" + status, 0);
                if (status) {
                    MainActivity.callGetPushIdRemote();
                }
                break;
            case TYPE_MESSAGE_RECEIVED:
                String id1 = bundle.getString("id");
                String description = bundle.getString("description");
                String content = bundle.getString("content");
                Log.d("Push_Demo", "receive TYPE_MESSAGE_RECEIVED, id=" + id1 + ", des=" + description + ", content="
                        + content);
                MainActivity.print("received data message, description=" + description + ", content="
                        + content, 0);
                break;
            case TYPE_NOTIFICATION_RECEIVED:
                String id2 = bundle.getString("id");
                String title2 = bundle.getString("title");
                String content2 = bundle.getString("content");
                Log.d("Push_Demo", "receive TYPE_NOTIFICATION_RECEIVED, id=" + id2 + ", title=" + title2
                        + ", content=" + content2);
                /*
                 * MainActivity.print("receive TYPE_NOTIFICATION_RECEIVED, id=" + id2 + ", title=" + title2 +
                 * ", content=" + content2, 0);
                 */
                String extra = bundle.getString("extra");
                if (!TextUtils.isEmpty(extra)) {
                    Log.d("Push_Demo", "TYPE_NOTIFICATION_RECEIVED extra=" + extra);
                    // MainActivity.print("TYPE_NOTIFICATION_RECEIVED extra=" + extra, 0);
                }
                break;
            case TYPE_NOTIFICATION_OPENED:
                String id3 = bundle.getString("id");
                String title3 = bundle.getString("title");
                String content3 = bundle.getString("content");
                Log.d("Push_Demo", "receive TYPE_NOTIFICATION_OPENED, id=" + id3 + ", title=" + title3
                        + ", content=" + content3);
                /*
                 * MainActivity.print("receive TYPE_NOTIFICATION_OPENED, id=" + id3 + ", title=" + title3 + ", content="
                 * + content3, 0);
                 */
                String extra2 = bundle.getString("extra");
                if (!TextUtils.isEmpty(extra2)) {
                    Log.d("Push_Demo", "TYPE_NOTIFICATION_RECEIVED extra=" + extra2);
                    // MainActivity.print("TYPE_NOTIFICATION_RECEIVED extra=" + extra2, 0);
                }
                break;

            default:
                break;
        }
    }

}
