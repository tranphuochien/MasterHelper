package com.hientp.hcmus.masterhelper.recceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.text.TextUtils;

import com.hientp.hcmus.masterhelper.AndroidApplication;
import com.hientp.hcmus.masterhelper.event.ReceiveSmsEvent;

import org.greenrobot.eventbus.EventBus;

import timber.log.Timber;

/**
 * Process received sms
 */
public class SmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            //---get the SMS message passed in---
            Bundle bundle = intent.getExtras();

            if (bundle == null) {
                return;
            }

            SmsMessage[] msgs = null;
            StringBuilder str = new StringBuilder();

            ReceiveSmsEvent event = new ReceiveSmsEvent();
            //---retrieve the SMS message received---
            Object[] pdus = (Object[]) bundle.get("pdus");
            if (pdus != null) {
                msgs = new SmsMessage[pdus.length];
            }

            if (msgs != null) {
                for (int i = 0; i < msgs.length; i++) {
                    SmsMessage smsMessage = createFromPdu((byte[]) pdus[i], bundle);
                    if (smsMessage == null) {
                        continue;
                    }

                    String origAddress = null;
                    String messageBody = null;

                    try {
                        origAddress = smsMessage.getOriginatingAddress();
                        messageBody = smsMessage.getMessageBody();
                    } catch (NullPointerException ignore) {
                    }

                    if (TextUtils.isEmpty(origAddress) && TextUtils.isEmpty(messageBody)) {
                        continue;
                    }

                    str.append("SMS from ").append(origAddress);
                    str.append(" :");
                    str.append(messageBody);
                    event.addMessage(origAddress, messageBody);
                    str.append("\n");
                }
            }
            EventBus eventBus = AndroidApplication.instance().getAppComponent().eventBus();
            eventBus.removeStickyEvent(ReceiveSmsEvent.class);
            eventBus.postSticky(event);

            //---display the new SMS message---
            Timber.d(str.toString());


        } catch (Throwable t) {
            Timber.w(t, "Got exception while processing received sms");
        }
    }

    private SmsMessage createFromPdu(byte[] pdu, Bundle bundle) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String format = bundle.getString("format");
            return SmsMessage.createFromPdu(pdu, format);
        } else {
            return SmsMessage.createFromPdu(pdu);
        }
    }
}
