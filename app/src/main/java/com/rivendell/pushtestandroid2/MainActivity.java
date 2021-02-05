package com.rivendell.pushtestandroid2;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Pair;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.techain.ac.Callback;
import com.baidu.techain.ac.TH;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView textView;
    private TextView textViewTop;
    private Button btn;
    private boolean mIsCopy;
    private static String sBindCode;

    protected List<String> list = new ArrayList<String>();

    private static Handler sHandler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // findViewById(R.id.load).setOnClickListener(this);
        findViewById(R.id.get_id).setOnClickListener(this);
        findViewById(R.id.check).setOnClickListener(this);
        findViewById(R.id.clean).setOnClickListener(this);
        textViewTop = (TextView) findViewById(R.id.textview_top);
        textView = (TextView) findViewById(R.id.textview);
        textView.setMovementMethod(ScrollingMovementMethod.getInstance());
        btn = (Button) findViewById(R.id.cp_rt);
        btn.setOnClickListener(this);
        sHandler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                Bundle b = msg.getData();
                String content = b.getString("content");
                switch (msg.what) {
                    case 0:
                        writeText(content);
                        break;
                    case 1:
                        writeText(content);
                        Toast.makeText(MainActivity.this, "Push ID has been copied", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        textView.setText("");
                        break;
                    case 3:
                        callGetBindCode();
                        break;
                    default:
                        break;
                }
            }

        };

        if (!isNetworkAvailable(getApplicationContext())) {
            MainActivity.print("Network issue, please try again later", 0);
            textViewTop.setText(R.string.fail);
            btn.setText(getString(R.string.retry));
            mIsCopy = false;
            btn.setEnabled(true);
            return;
        }
        if (MyReceiver.sShowedInitSuccess) {
            MainActivity.print("init successfully", 0);
            callGetBindCode();
        } else {
            TH.setAgreePolicy(getApplicationContext(), true);
            TH.init(getApplicationContext(), "700000765", "b9db79defea6e84c2b7ea7a98ae8f144", 100019);
            mInit = true;
            TH.tinvoke(100019, "sri", new Class[]{int.class, int.class}, R.layout.a_a, R.drawable.b_b);
            callGetBindCode();
        }

    }

    public static void callGetPushIdRemote() {
        if (sHandler != null) {
            Message msg = new Message();
            msg.what = 3;
            sHandler.sendMessage(msg);
        }
    }

    private void callGetBindCode() {
        if (!TextUtils.isEmpty(sBindCode)) {
            textViewTop.setText(getString(R.string.device_code) + sBindCode);
            btn.setText(getString(R.string.copy));
            mIsCopy = true;
            btn.setEnabled(true);
            return;
        }
        TH.tinvoke(100019, "bindSignCode", new Callback() {

            @Override
            public Object onEnd(Object... arg0) {
                Pair<Integer, String> result = (Pair<Integer, String>) arg0[0];
                final int resultCode = result.first;
                final String resultString = result.second;
                if (!TextUtils.isEmpty(resultString)) {
                    sBindCode = resultString;
                }
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if (resultCode == 0) {
                            textViewTop.setText(getString(R.string.device_code) + sBindCode);
                            btn.setText(getString(R.string.copy));
                            mIsCopy = true;
                            btn.setEnabled(true);
                        } else {
                            textViewTop.setText(R.string.fail);
                            btn.setText(getString(R.string.retry));
                            mIsCopy = false;
                            btn.setEnabled(true);
                            Toast.makeText(MainActivity.this, "get device code failed", Toast.LENGTH_SHORT).show();
                        }
                    }

                });
                return null;
            }

            @Override
            public Object onError(Object... arg0) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        textViewTop.setText(R.string.fail);
                        btn.setText(getString(R.string.retry));
                        mIsCopy = false;
                        btn.setEnabled(true);
                        Toast.makeText(MainActivity.this, "get device code failed", Toast.LENGTH_SHORT).show();
                    }

                });

                return null;
            }
        });
    }

    private void writeText(String input) {
        if (textView != null) {
            String oldText = textView.getText().toString();
            String newText = oldText + "\r\n" + input;
            textView.setText(newText);
        }
    }

    public static void print(String text, int what) {
        if (sHandler != null) {
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("content", text);
            msg.setData(data);
            msg.what = what;
            sHandler.sendMessage(msg);
        }
    }

    private boolean mIsCallingPushId;
    private boolean mInit = false;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.check:
                Pair<Integer, Object> ret = TH.tinvokeSync(100019, "isPushEnabled");
                int status = ret.first;
                if (status == 0) {
                    boolean result = (Boolean) ret.second;
                    print("get push state:" + result, 0);
                } else {
                    print("get push state fail, error code:" + status, 0);
                }
                break;
            case R.id.clean:
                print("", 2);
                break;
            case R.id.get_id:
                if (!mIsCallingPushId) {
                    mIsCallingPushId = true;
                    TH.tinvoke(100019, "getPushUid", new Callback() {
                        @Override
                        public Object onEnd(Object... arg0) {
                            final String uid = (String) arg0[0];
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    ClipboardManager clipManager =
                                            (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                    ClipData clipData = ClipData.newPlainText("push id", uid);
                                    clipManager.setPrimaryClip(clipData);
                                }

                            });
                            print("Push ID:" + uid + " has been copied", 1);
                            mIsCallingPushId = false;
                            return null;
                        }

                        @Override
                        public Object onError(Object... arg0) {
                            print("Get Push ID fail, error code: " + arg0[0], 0);
                            mIsCallingPushId = false;
                            return null;
                        }
                    });
                }
                break;
            case R.id.cp_rt:
                if (mIsCopy) {
                    ClipboardManager clipManager =
                            (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("BindSignId", sBindCode);
                    clipManager.setPrimaryClip(clipData);
                    Toast.makeText(MainActivity.this, "device code is copied", Toast.LENGTH_SHORT).show();
                } else {
                    if (!isNetworkAvailable(getApplicationContext())) {
                        return;
                    }
                    if (!mInit) {
                        TH.setAgreePolicy(getApplicationContext(), true);
                        TH.init(getApplicationContext(), "700000765", "b9db79defea6e84c2b7ea7a98ae8f144", 100019);
                        mInit = true;
                        TH.tinvoke(100019, "sri", new Class[]{int.class, int.class},
                                R.layout.a_a, R.drawable.b_b);
                    }
                    resetUI();
                    callGetBindCode();
                }

                break;
            default:
                break;
        }
    }

    private void resetUI() {
        textViewTop.setText(R.string.device_code_defult);
        btn.setText(R.string.retry);
        mIsCopy = false;
        btn.setEnabled(false);
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager mConnMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = mConnMan.getActiveNetworkInfo();
        if (info == null) {
            return false;
        }

        return info.isConnected();
    }
}
