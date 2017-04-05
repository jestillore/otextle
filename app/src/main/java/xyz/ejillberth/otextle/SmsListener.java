package xyz.ejillberth.otextle;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class SmsListener extends BroadcastReceiver {

    private static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION)) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                if (pdus != null) {
                    SmsMessage[] messages = new SmsMessage[pdus.length];
                    for (int i = 0; i < pdus.length; i++) {
                        messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    }
                    for (SmsMessage message : messages) {

                        String sender = message.getDisplayOriginatingAddress();
                        String messageBody = message.getDisplayMessageBody();

                        // new message
                        if (MessageUtil.isPhNumber(sender)) {
                            String number = MessageUtil.formatNumber(sender);
                            MessageUtil.newMessage(number, messageBody);
                        }
                    }
                }
            }
        }
    }
}
