package xyz.ejillberth.otextle;

import android.telephony.SmsManager;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import xyz.ejillberth.otextle.models.Connected;
import xyz.ejillberth.otextle.models.Online;

class MessageUtil {

    private static final String CONNECTED = "You are now connected to a random stranger. Enjoy texting.";
    private static final String YOU_DISCONNECTED = "You have disconnected.";
    private static final String PARTNER_DISCONNECTED = "Stranger has disconnected.";
    private static final String LOOKING_FOR_PARTNER = "We are looking for a random stranger. Please wait.";
    private static final String INSTRUCTIONS = "To connect, please reply with @connect.\n" +
            "To disconnect, please reply with @disconnect.\n" +
            "For help, please reply with @help";

    static void newMessage(String number, String message) {
        if (message.equalsIgnoreCase("@connect")) {
            if (isConnected(number)) {
                disconnect(number);
            }
            else if (isOnline(number)) {
                setOnline(number, false);
            }
            startNewChat(number);
        }
        else if (message.equalsIgnoreCase("@disconnect")) {
            if (isConnected(number)) {
                String partner = getPartner(number);
                disconnect(number);
                sendMessage(number, YOU_DISCONNECTED);
                sendMessage(partner, PARTNER_DISCONNECTED);
            }
            else if (isOnline(number)) {
                setOnline(number, false);
            }
            else {
                sendMessage(number, INSTRUCTIONS);
            }
        }
        else if (message.equals("@help")) {
            sendMessage(number, INSTRUCTIONS);
        }
        else {
            if (isConnected(number)) {
                sendMessage(number, message);
            }
            else {
                sendMessage(number, INSTRUCTIONS);
            }
        }
    }

    private static String getPartner(String number) {
        // get partner number assuming it's connected
        Connected c = new Select()
                .from(Connected.class)
                .where("tom = ? OR jerry = ?", number, number)
                .executeSingle();
        if (c != null) {
            if (c.tom.equals(number)) {
                return c.jerry;
            }
            else {
                return c.tom;
            }
        }
        return null;
    }

    private static boolean isConnected(String number) {
        // check if number is in connected table (tom or jerry)
        int count = new Select()
                .from(Connected.class)
                .where("tom = ? OR jerry = ?", number, number)
                .count();
        return count > 0;
    }

    private static boolean isOnline(String number) {
        // check if number is in online table
        int count = new Select()
                .from(Online.class)
                .where("number = ?", number)
                .count();

        return count > 0;
    }

    private static void disconnect(String number) {
        // remove number from connected table (tom or jerry)
        new Delete()
                .from(Connected.class)
                .where("tom = ? OR jerry = ?", number, number)
                .execute();
    }

    private static void setOnline(String number, boolean online) {
        if (online) {
            // add number to online table
            Online o = new Online();
            o.number = number;
            o.save();
        }
        else {
            // remove number from online table
            new Delete()
                    .from(Online.class)
                    .where("number = ?", number)
                    .execute();
        }
    }

    private static int getOnlineCount() {
        // count number of rows in online table
        return new Select()
                .from(Online.class)
                .count();
    }

    private static String getOnlineNumber() {
        // get first row in online table
        Online online = new Select()
                .from(Online.class)
                .executeSingle();
        if (online != null) {
            return online.number;
        }
        return null;
    }

    private static void connect(String tom, String jerry) {
        // remove numbers to online
        setOnline(tom, false);
        setOnline(jerry, false);
        // save numbers to connected
        Connected connected = new Connected();
        connected.tom = tom;
        connected.jerry = jerry;
        connected.save();
    }

    private static void startNewChat(String number) {
        if (getOnlineCount() > 0) {
            // there are online users
            String partner = getOnlineNumber();
            if (partner != null) {
                connect(number, partner);
                sendMessage(number, CONNECTED);
                sendMessage(partner, CONNECTED);
            }
            else {
                setOnline(number, true);
                sendMessage(number, LOOKING_FOR_PARTNER);
            }
        }
        else {
            setOnline(number, true);
            sendMessage(number, LOOKING_FOR_PARTNER);
        }
    }

    static boolean isPhNumber(String number) {
        // check if number is 10 digits or more
        // ph number is 10 numbers excluding 0
        if (number.length() >= 10) {
            // there are 4 possible starting letter for ph number
            // 0, +, 6, 9
            switch (number.charAt(0)) {
                case '0':
                    // if 0, there must be 11 digits, and the next digit is 9
                    return number.length() == 11 && number.charAt(1) == '9';
                case '+':
                    // if +, there will be 13 digits, and next digits will be 639
                    return number.length() == 13 && number.substring(1, 4).equals("639");
                case '6':
                    // if 6, there will be 12 digits, and next digits will be 39
                    return number.length() == 12 && number.substring(1, 3).equals("39");
                case '9':
                    // if 9, there will be 10 digits
                    return number.length() == 10;
            }
        }
        return false;
    }

    static String formatNumber(String number) {
        return "0" + number.substring(number.length() - 10);
    }

    private static void sendMessage(String number, String message) {
        // check if there is something to send
        if (number != null && number.length() > 0 && message != null && message.length() > 0) {
            // send message
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(number, null, message, null, null);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}
