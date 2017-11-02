/*
 * Copyright (C) 2008-2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.softkeyboard;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.KeyboardView;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public class SoftKeyboard extends InputMethodService
        implements KeyboardView.OnKeyboardActionListener {
    static final boolean DEBUG = false;

    private InputMethodManager mInputMethodManager;

    private LatinKeyboardView mInputView;

    static final int MSG_WRITE_CHINESE_SYMBOL = 200;
    static final int MSG_CLOSE = 300;
    static final int MSG_KBD_GET_REPLY_TO_MESSENGER = 400;
    static final int MSG_KBD_UNSET_REPLY_TO_MESSENGER = 500;
    static final int MSG_KBD_START_INPUT = 600;
    static final int MSG_KBD_STOP_INPUT = 700;

    private Messenger mServiceMessenger;
    private Messenger mReplyToMessenger = new Messenger(new IncomingHandler());

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_WRITE_CHINESE_SYMBOL:
                    if (DEBUG) {
                        Toast.makeText(getApplicationContext(), "KBD: MSG_WRITE_CHINESE_SYMBOL", Toast.LENGTH_SHORT).show();
                    }
                    getCurrentInputConnection().commitText(String.valueOf((char) msg.arg1), 1);
                    break;
                case MSG_CLOSE:
                    if (DEBUG) {
                        Toast.makeText(getApplicationContext(), "KBD: MSG_CLOSE", Toast.LENGTH_SHORT).show();
                    }
                    handleClose();
                    break;
                default:
                    break;
            }
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder binder) {
            mServiceMessenger = new Messenger(binder);
            sendMessage(MSG_KBD_GET_REPLY_TO_MESSENGER);
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            mServiceMessenger = null;
        }
    };

    private void sendMessage(int what) {
        //send message with mReplyToKeyboardMessenger
        Message msg = Message.obtain(null, what, 0, 0);
        msg.replyTo = mReplyToMessenger;
        try {
            mServiceMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Main initialization of the input method component.  Be sure to call
     * to super class.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
    }

    /**
     * This is the point where you can do all of your UI initialization.  It
     * is called after creation and any configuration change.
     */
    @Override
    public void onInitializeInterface() {
    }

    /**
     * Called by the framework when your view for creating input needs to
     * be generated.  This will be called the first time your input method
     * is displayed, and every time it needs to be re-created such as due to
     * a configuration change.
     */
    @Override
    public View onCreateInputView() {
        mInputView = (LatinKeyboardView) getLayoutInflater().inflate(
                R.layout.input, null);
        return mInputView;
        //return new View(this);
    }

    /**
     * This is the main point where we do our initialization of the input method
     * to begin operating on an application.  At this point we have been
     * bound to the client, and are now receiving all of the detailed information
     * about the target of our edits.
     */
    @Override
    public void onStartInput(EditorInfo attribute, boolean restarting) {
        super.onStartInput(attribute, restarting);
        if (!restarting) {
            if (mServiceMessenger != null) {
                sendMessage(MSG_KBD_START_INPUT);
            } else {
                boolean b = bindService(new Intent(SoftKeyboard.this, UnicodeReceiverService.class), mConnection, Context.BIND_AUTO_CREATE);
            }
        }
    }

    /**
     * This is called when the user is done editing a field.  We can use
     * this to reset our state.
     */
    @Override
    public void onFinishInput() {
        super.onFinishInput();
        //send message with mReplyToKeyboardMessenger set to null
        if (mServiceMessenger != null) {
            sendMessage(MSG_KBD_STOP_INPUT);
        } else {
            boolean b = bindService(new Intent(SoftKeyboard.this, UnicodeReceiverService.class), mConnection, Context.BIND_AUTO_CREATE);
        }
        if (mInputView != null) {
            mInputView.closing();
        }
    }

    @Override
    public void onStartInputView(EditorInfo attribute, boolean restarting) {
        super.onStartInputView(attribute, restarting);
        mInputView.closing();
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {

    }

    @Override
    public void onText(CharSequence text) {

    }

    private void handleClose() {
        requestHideSelf(0);
        mInputView.closing();
    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }

    @Override
    public void onPress(int primaryCode) {

    }

    @Override
    public void onRelease(int primaryCode) {

    }
}
