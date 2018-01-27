package com.hientp.hcmus.masterhelper.event;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Hold list of received SMS
 */
public class ReceiveSmsEvent {
    public static class SmsMessage {
        public final String from;
        public final String body;


        SmsMessage(String from, String body) {
            this.from = from;
            this.body = body;
        }
    }

    public final List<SmsMessage> messages = new ArrayList<>();

    public void addMessage(String from, String body) {
        if (TextUtils.isEmpty(from) || TextUtils.isEmpty(body)) {
            return;
        }

        messages.add(new SmsMessage(from, body));
    }
}
