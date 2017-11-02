package com.example.android.softkeyboard;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.widget.Toast;

public class UnicodeReceiverService extends Service {
    static final int MSG_GET_CHINESE_SYMBOL = 6;
    static final int MSG_GET_REPLY_TO_MESSENGER = 7;
    static final int MSG_UNSET_REPLY_TO_MESSENGER = 8;
    static final int MSG_START_INPUT = 9;
    static final int MSG_STOP_INPUT = 10;
    private static final boolean DEBUG = false;

    Messenger mReplyToKeyboardMessenger = null;
    Messenger mReplyToServiceMessenger = null;

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SoftKeyboard.MSG_KBD_GET_REPLY_TO_MESSENGER:
                    if (DEBUG) {
                        Toast.makeText(getApplicationContext(), "B: MSG_KBD_GET_REPLY_TO_MESSENGER", Toast.LENGTH_SHORT).show();
                    }
                    mReplyToKeyboardMessenger = msg.replyTo;
                    break;
                case SoftKeyboard.MSG_KBD_UNSET_REPLY_TO_MESSENGER:
                    if (DEBUG) {
                        Toast.makeText(getApplicationContext(), "B: MSG_KBD_UNSET_REPLY_TO_MESSENGER", Toast.LENGTH_SHORT).show();
                    }
                    mReplyToKeyboardMessenger = null;
                    break;
                case MSG_GET_REPLY_TO_MESSENGER:
                    if (DEBUG) {
                        Toast.makeText(getApplicationContext(), "B: MSG_GET_REPLY_TO_MESSENGER", Toast.LENGTH_SHORT).show();
                    }
                    mReplyToServiceMessenger = msg.replyTo;
                    break;
                case MSG_UNSET_REPLY_TO_MESSENGER:
                    if (DEBUG) {
                        Toast.makeText(getApplicationContext(), "B: MSG_UNSET_REPLY_TO_MESSENGER", Toast.LENGTH_SHORT).show();
                    }
                    mReplyToServiceMessenger = null;
                    break;
                case SoftKeyboard.MSG_KBD_START_INPUT:
                    if (DEBUG) {
                        Toast.makeText(getApplicationContext(), "B: MSG_KBD_START_INPUT", Toast.LENGTH_SHORT).show();
                    }
                    if (mReplyToServiceMessenger != null) {
                        Message replyMessage = Message.obtain(null, MSG_START_INPUT, 0, 0);
                        try {
                            mReplyToServiceMessenger.send(replyMessage);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case SoftKeyboard.MSG_KBD_STOP_INPUT:
                    if (DEBUG) {
                        Toast.makeText(getApplicationContext(), "B: MSG_KBD_STOP_INPUT", Toast.LENGTH_SHORT).show();
                    }
                    if (mReplyToServiceMessenger != null) {
                        Message replyMessage = Message.obtain(null, MSG_STOP_INPUT, 0, 0);
                        try {
                            mReplyToServiceMessenger.send(replyMessage);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case MSG_GET_CHINESE_SYMBOL:
                    if (DEBUG) {
                        Toast.makeText(getApplicationContext(), "B: MSG_GET_CHINESE_SYMBOL", Toast.LENGTH_SHORT).show();
                    }
                    if (mReplyToKeyboardMessenger != null) {
                        Message replyMessage = Message.obtain(null, SoftKeyboard.MSG_WRITE_CHINESE_SYMBOL, msg.arg1, 0);
                        try {
                            mReplyToKeyboardMessenger.send(replyMessage);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                default:
                    break;
//                    super.handleMessage(msg);
            }
        }
    }

    final Messenger mMessenger = new Messenger(new IncomingHandler());

    @Override
    public IBinder onBind(Intent intent) {
        if (DEBUG) {
            Toast.makeText(getApplicationContext(), "B: binding to UnicodeReceiverService", Toast.LENGTH_SHORT).show();
        }
        return mMessenger.getBinder();
    }
}
