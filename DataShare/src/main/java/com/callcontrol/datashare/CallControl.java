package com.callcontrol.datashare;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;

import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Call Control application integration contract class.
 * <p>Provides constants for use with {@link android.content.ContentResolver} as well as a set of
 * handy methods to simplify routine operations.
 */
public final class CallControl {
    /**
     * Declared Call Control DataShare content provider authority.
     * <p>The same must be declared in Call Control manifest.
     * <p>Must be worldwide unique, base for all content provide URLs.
     */
    /*pkg*/ static final String AUTHORITY = "com.callcontrol.datashare";
    /**
     * Basic {@link Uri} defining the root provider {@link Uri}.
     * <p>Never used directly, but as a shortcut for making direct {@link Uri}s
     * using {@link Uri#withAppendedPath(Uri, String)}.
     */
    /*pkg*/ static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

    /**
     * Uri for looking up calls
     */
    public static final Uri LOOKUP_CALL_URI = Uri.withAppendedPath(AUTHORITY_URI, "lookup/call");

    /**
     * Uri for looking up text messages
     */
    public static final Uri LOOKUP_TEXT_URI = Uri.withAppendedPath(AUTHORITY_URI, "lookup/text");

    /**
     * Uri for requesting a one time intent token
     */
    /* pkg */ static final Uri REQUEST_TOKEN_URI = Uri.withAppendedPath(AUTHORITY_URI, "token");

    /**
     * Mime type for lookup results in {@link android.database.Cursor}
     */
    public static final String MIME_TYPE_LOOKUP_RESULT = "vnd.android.cursor.item/lookup_result";

    /**
     * Mime type for lookup results in {@link android.database.Cursor}
     */
    /* pkg */ static final String MIME_TYPE_TOKEN_RESULT = "vnd.android.cursor.item/token_result";

    /**
     * Intent action to open Blocked List.
     * <p>Intent can optionally contain {@link #EXTRA_PHONE_NUMBER} to open specific number record, if any.
     */
    public static final String ACTION_BLOCKED_LIST = "com.callcontrol.datashare.intent.action.BLOCKED_LIST";

    /**
     * Intent action to lookup number via Call Control UI.
     * <p>Intent must contain {@link #EXTRA_PHONE_NUMBER}.
     */
    public static final String ACTION_LOOKUP = "com.callcontrol.datashare.intent.action.LOOKUP";

    /**
     * Intent action to add rule for a number via Call Control UI.
     * <p>Intent must contain {@link #EXTRA_PHONE_NUMBER}.
     */
    public static final String ACTION_ADD_RULE = "com.callcontrol.datashare.intent.action.ADD_RULE";

    /**
     * Intent action to lookup number via Call Control UI.
     * <p>Intent must contain {@link #EXTRA_PHONE_NUMBER}.
     */
    public static final String ACTION_REPORT = "com.callcontrol.datashare.intent.action.REPORT";

    /**
     * Intent action to open 3rd party apps access control in Call Control UI.
     */
    public static final String ACTION_3RD_PARTY_ACCESS = "com.callcontrol.datashare.intent.action.3RD_PARTY_ACCESS";

    /**
     * Intent extra key for phone number passed along.
     */
    public static final String EXTRA_PHONE_NUMBER = "com.callcontrol.datashare.intent.extra.PHONE_NUMBER";

    /**
     * Intent extra key for the requesting application access token.
     * <p>Intents without this parameter, or when the token is not valid, will not be honored by Call Control.
     * <p>Token is requested prior to sending each {@link Intent} to Call Control is a singe use value.
     */
    /* pkg */ static final String EXTRA_TOKEN = "com.callcontrol.datashare.intent.extra.TOKEN";

    private CallControl() {
    }

    /**
     * Contract class for Token results.
     */
    /* pkg */ static final class Token {
        private Token () {
        }

        /**
         * One time token that should be used when sending {@link Intent} to Call Control.
         */
        /* pkg */ static final String TOKEN = "token";

        /**
         * Timestamp for requesting token, used in query.
         */
        /* pkg */ static final String TIMESTAMP = "ts";
    }


    /**
     * Contract class for Lookup results.
     */
    public static final class Lookup {
        private Lookup() {
        }

        /**
         * Human readable block reason. The action should be blocked if not null.
         */
        public static final String BLOCK_REASON = "reason";
        /**
         * Known name for the number, if any. Can be null.
         */
        public static final String DISPLAY_NAME = "name";
    }

    /**
     * Reported number. Contain a number and possibly a name to go along with it.
     */
    public static final class Report implements Parcelable {
        /**
         * Number being reported.
         */
        private String number;
        /**
         * Known name for the number.
         * <p>Optional.
         */
        private String name;
        /**
         * Whether or not the number is unwanted.
         */
        private boolean isUnwanted;

        /**
         * Constructs the object assuming the number is unwanted.
         *
         * @param number Number being reported.
         */
        public Report(@NonNull String number) {
            this(number, null, true);
        }

        /**
         * Constructs the object assuming the number is unwanted.
         *
         * @param number Number being reported.
         * @param name   Name known for the number.
         */
        public Report(@NonNull String number, @Nullable String name) {
            this(number, name, true);
        }

        /**
         * Constructs the object.
         *
         * @param number     Number being reported.
         * @param name       Name known for the number.
         * @param isUnwanted Indicates if user doesn't want to be contacted by the number.
         */
        public Report(@NonNull String number, @Nullable String name, boolean isUnwanted) {
            this.number = number;
            this.name = name;
            this.isUnwanted = isUnwanted;
        }

        /**
         * Constructs the object form parcel.
         *
         * @param parcel Parcel.
         */
        Report(Parcel parcel) {
            number = parcel.readString();
            name = parcel.readString();
            isUnwanted = parcel.readInt() == 1;
        }

        /**
         * Returns the associated phone number.
         *
         * @return Associated phone number.
         */
        @NonNull
        public String getNumber() {
            return number;
        }

        /**
         * Returns the associated name, if any.
         *
         * @return Associated name, if any.
         */
        @Nullable
        public String getName() {
            return name;
        }

        /**
         * Returns whether or not user refuses to receive any activity from the phone number.
         *
         * @return Whether or not user refuses to receive any activity from the phone number.
         */
        public boolean isUnwanted() {
            return isUnwanted;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(number);
            dest.writeString(name);
            dest.writeInt(isUnwanted ? 1 : 0);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        /**
         * Parcel interface used to create instances from parcels.
         */
        public static final Parcelable.Creator<Report> CREATOR = new Parcelable.Creator<Report>() {
            @Override
            public Report createFromParcel(Parcel source) {
                return new Report(source);
            }

            @Override
            public Report[] newArray(int size) {
                return new Report[size];
            }
        };
    }

    /**
     * Opens Blocked List in Call Control.
     *
     * @param context Context where {@link Context#startActivity(Intent)} will be executed.
     *
     * @return True if the intent has been sent, false on error.
     */
    public static boolean openBlockedList(@NonNull Context context) {
        return openBlockedList(context, null, 0);
    }

    /**
     * Opens Blocked List in Call Control.
     *
     * @param context     Context where {@link Context#startActivity(Intent)} will be executed.
     * @param intentFlags Flags that should be set to the {@link Intent}. See {@link Intent#setFlags(int)} for details.
     *
     * @return True if the intent has been sent, false on error.
     */
    public static boolean openBlockedList(@NonNull Context context, int intentFlags) {
        return openBlockedList(context, null, intentFlags);
    }

    /**
     * Opens specified number in Blocked List in Call Control if possible.
     * <p>If the number is null, the behavior is identical to {@link #openBlockedList(Context)}.
     *
     * @param context     Context where {@link Context#startActivity(Intent)} will be executed.
     * @param phoneNumber Phone number to present.
     *
     * @return True if the intent has been sent, false on error.
     */
    public static boolean openBlockedList(@NonNull Context context, @Nullable String phoneNumber) {
        return openBlockedList(context, phoneNumber, 0);
    }

    /**
     * Opens specified number in Blocked List in Call Control if possible.
     * <p>If the number is null, the behavior is identical to {@link #openBlockedList(Context)}.
     *
     * @param context     Context where {@link Context#startActivity(Intent)} will be executed.
     * @param phoneNumber Phone number to present.
     * @param intentFlags Flags that should be set to the {@link Intent}. See {@link Intent#setFlags(int)} for details.
     *
     * @return True if the intent has been sent, false on error.
     */
    public static boolean openBlockedList(@NonNull Context context, @Nullable String phoneNumber, int intentFlags) {
        Intent i = new Intent(ACTION_BLOCKED_LIST);
        if (null != phoneNumber) i.putExtra(EXTRA_PHONE_NUMBER, phoneNumber);
        if (0 != intentFlags) i.setFlags(intentFlags);
        if (!signIntent(context, i)) return false;
        context.startActivity(i);
        return true;
    }

    /**
     * Performs a number lookup in Call Control UI.
     *
     * @param context     Context where {@link Context#startActivity(Intent)} will be executed.
     * @param phoneNumber Phone number to lookup.
     *
     * @return True if the intent has been sent, false on error.
     */
    public static boolean lookupNumber(@NonNull Context context, @NonNull String phoneNumber) {
        return lookupNumber(context, phoneNumber, 0);
    }

    /**
     * Performs a number lookup in Call Control UI.
     *
     * @param context     Context where {@link Context#startActivity(Intent)} will be executed.
     * @param phoneNumber Phone number to lookup.
     * @param intentFlags Flags that should be set to the {@link Intent}. See {@link Intent#setFlags(int)} for details.
     *
     * @return True if the intent has been sent, false on error.
     */
    public static boolean lookupNumber(@NonNull Context context, @NonNull String phoneNumber, int intentFlags) {
        Intent i = new Intent(ACTION_LOOKUP);
        i.putExtra(EXTRA_PHONE_NUMBER, phoneNumber);
        if (0 != intentFlags) i.setFlags(intentFlags);
        if (!signIntent(context, i)) return false;
        context.startActivity(i);
        return true;
    }


    /**
     * <p>Create a rule to the set of numbers via Call Control UI.
     * <p>Will present Call Control UI which interacts with user.
     * <p>After added to Blocked List, suggest user to report blocked number.
     * <p>
     * <p><b>IMPORTANT:</b> Must contain the list with ALL items having the same value of the {@link Report#isUnwanted} flag.
     * I.e. in one run you only report either unwanted or wanted numbers, but you cannot mix them in one call.
     * <p>
     * <p>Safe to execute with empty list, does nothing in this case.
     *
     *
     * @param context      Context where {@link Context#startActivity(Intent)} will be executed.
     * @param report       a numbers for a rule.
     *
     * @return True if the intent has been sent, false on error.
     */
    public static boolean addRule(@NonNull Context context, @NonNull Report report) {
        return addRule(context, report, 0);
    }

    /**
     * <p>Create a rule to the set of numbers via Call Control UI.
     * <p>Will present Call Control UI which interacts with user.
     * <p>After added to Blocked List, suggest user to report blocked number.
     * <p>
     * <p><b>IMPORTANT:</b> Must contain the list with ALL items having the same value of the {@link Report#isUnwanted} flag.
     * I.e. in one run you only report either unwanted or wanted numbers, but you cannot mix them in one call.
     * <p>
     * <p>Safe to execute with empty list, does nothing in this case.
     *
     *
     * @param context      Context where {@link Context#startActivity(Intent)} will be executed.
     * @param report       a numbers for a rule.
     * @param intentFlags Flags that should be set to the {@link Intent}. See {@link Intent#setFlags(int)} for details.
     *
     * @return True if the intent has been sent, false on error.
     */
    public static boolean addRule(@NonNull Context context, @NonNull Report report, int intentFlags) {
        ArrayList<Report> list = new ArrayList<>(1);
        list.add(report);
        return addRule(context, list, intentFlags);
    }

    /**
     * <p>Create a rule to the set of numbers via Call Control UI.
     * <p>Will present Call Control UI which interacts with user.
     * <p>After added to Blocked List, suggest user to report blocked number.
     * <p>
     * <p><b>IMPORTANT:</b> Must contain the list with ALL items having the same value of the {@link Report#isUnwanted} flag.
     * I.e. in one run you only report either unwanted or wanted numbers, but you cannot mix them in one call.
     * <p>
     * <p>Safe to execute with empty list, does nothing in this case.
     *
     *
     * @param context      Context where {@link Context#startActivity(Intent)} will be executed.
     * @param reports     List of numbers.
     *
     * @return True if the intent has been sent, false on error.
     */
    public static boolean addRule(@NonNull Context context, @NonNull ArrayList<Report> reports) {
        return addRule(context, reports, 0);
    }

    /**
     * <p>Create a rule to the set of numbers via Call Control UI.
     * <p>Will present Call Control UI which interacts with user.
     * <p>After added to Blocked List, suggest user to report blocked number.
     * <p>
     * <p><b>IMPORTANT:</b> Must contain the list with ALL items having the same value of the {@link Report#isUnwanted} flag.
     * I.e. in one run you only report either unwanted or wanted numbers, but you cannot mix them in one call.
     * <p>
     * <p>Safe to execute with empty list, does nothing in this case.
     *
     *
     * @param context      Context where {@link Context#startActivity(Intent)} will be executed.
     * @param reports     List of numbers.
     * @param intentFlags Flags that should be set to the {@link Intent}. See {@link Intent#setFlags(int)} for details.
     *
     * @return True if the intent has been sent, false on error.
     */
    public static boolean addRule(@NonNull Context context, @NonNull ArrayList<Report> reports, int intentFlags) {
        if (reports.isEmpty()) return false;
        boolean isUnwanted = reports.get(0).isUnwanted;
        for (Report n : reports) {
            if (n.isUnwanted != isUnwanted) throw new IllegalArgumentException("All numbers reported must have identical isUnwanted flag");
        }
        Intent i = new Intent(ACTION_ADD_RULE);
        i.putExtra(EXTRA_PHONE_NUMBER, reports);
        if (0 != intentFlags) i.setFlags(intentFlags);
        if (!signIntent(context, i)) return false;
        context.startActivity(i);
        return true;
    }


    /**
     * Reports a number via Call Control UI.
     * <p>Will present Call Control UI which interacts with user.
     * <p>After user confirms the report, he is asked if he wants to also add the number to Blocked List.
     *
     * @param context    Context where {@link Context#startActivity(Intent)} will be executed.
     * @param report     Report about a number.
     *
     * @return True if the intent has been sent, false on error.
     */
    public static boolean report(@NonNull Context context, @NonNull Report report) {
        return report(context, report, 0);
    }

    /**
     * Reports a number via Call Control UI.
     * <p>Will present Call Control UI which interacts with user.
     * <p>After user confirms the report, he is asked if he wants to also add the number to Blocked List.
     *
     * @param context     Context where {@link Context#startActivity(Intent)} will be executed.
     * @param report      Report about a number.
     * @param intentFlags Flags that should be set to the {@link Intent}. See {@link Intent#setFlags(int)} for details.
     *
     * @return True if the intent has been sent, false on error.
     */
    public static boolean report(@NonNull Context context, @NonNull Report report, int intentFlags) {
        ArrayList<Report> list = new ArrayList<>(1);
        list.add(report);
        return report(context, list, intentFlags);
    }

    /**
     * <p>Reports a set of numbers via Call Control UI.
     * <p>Will present Call Control UI which interacts with user.
     * <p>After user confirms the report, the number is also added to Blocked List.
     * <p>
     * <p><b>IMPORTANT:</b> Must contain the list with ALL items having the same value of the {@link Report#isUnwanted} flag.
     * I.e. in one run you only report either unwanted or wanted numbers, but you cannot mix them in one call.
     * <p>
     * <p>Safe to execute with empty list, does nothing in this case.
     * <p>
     * <p>Useful when user enables the integration and the application wants to put already known
     * blocked numbers to Call Control in one run.
     *
     * @throws IllegalArgumentException When {@link Report#isUnwanted} is not the same for all numbers.
     *
     * @param context    Context where {@link Context#startActivity(Intent)} will be executed.
     * @param reports    List of numbers to report. Must all have identical {@link Report#isUnwanted} flag.
     *
     * @return True if the intent has been sent, false on error.
     */
    public static boolean report(@NonNull Context context, @NonNull ArrayList<Report> reports) {
        return report(context, reports, 0);
    }

    /**
     * <p>Reports a set of numbers via Call Control UI.
     * <p>Will present Call Control UI which interacts with user.
     * <p>After user confirms the report, the number is also added to Blocked List.
     * <p>
     * <p><b>IMPORTANT:</b> Must contain the list with ALL items having the same value of the {@link Report#isUnwanted} flag.
     * I.e. in one run you only report either unwanted or wanted numbers, but you cannot mix them in one call.
     * <p>
     * <p>Safe to execute with empty list, does nothing in this case.
     * <p>
     * <p>Useful when user enables the integration and the application wants to put already known
     * blocked numbers to Call Control in one run.
     *
     * @throws IllegalArgumentException When {@link Report#isUnwanted} is not the same for all numbers.
     *
     * @param context     Context where {@link Context#startActivity(Intent)} will be executed.
     * @param reports     List of numbers to report. Must all have identical {@link Report#isUnwanted} flag.
     * @param intentFlags Flags that should be set to the {@link Intent}. See {@link Intent#setFlags(int)} for details.
     *
     * @return True if the intent has been sent, false on error.
     */
    public static boolean report(@NonNull Context context, @NonNull ArrayList<Report> reports, int intentFlags) {
        if (reports.isEmpty()) return false;
        boolean isUnwanted = reports.get(0).isUnwanted;
        for (Report n : reports) {
            if (n.isUnwanted != isUnwanted) throw new IllegalArgumentException("All numbers reported must have identical isUnwanted flag");
        }
        Intent i = new Intent(ACTION_REPORT);
        i.putExtra(EXTRA_PHONE_NUMBER, reports);
        if (0 != intentFlags) i.setFlags(intentFlags);
        if (!signIntent(context, i)) return false;
        context.startActivity(i);
        return true;
    }

    /**
     * Opens access settings for 3rd party apps within Call Control UI.
     *
     * @param context Context where {@link Context#startActivity(Intent)} will be executed.
     *
     * @return True if the intent has been sent, false on error.
     */
    public static boolean openAccessSettings(@NonNull Context context) {
        return openAccessSettings(context, 0);
    }

    /**
     * Opens access settings for 3rd party apps within Call Control UI.
     *
     * @param context Context where {@link Context#startActivity(Intent)} will be executed.
     * @param intentFlags Flags that should be set to the {@link Intent}. See {@link Intent#setFlags(int)} for details.
     *
     * @return True if the intent has been sent, false on error.
     */
    public static boolean openAccessSettings(@NonNull Context context, int intentFlags) {
        Intent i = new Intent(ACTION_3RD_PARTY_ACCESS);
        if (0 != intentFlags) i.setFlags(intentFlags);
        if (!signIntent(context, i)) return false;
        context.startActivity(i);
        return true;
    }

    /**
     * Performs intent "signing", i.e. requesting and adding {@link #EXTRA_TOKEN} extra to the intent.
     * <p>Intents that do not contain a valid {@link #EXTRA_TOKEN} will not be honored by Call Control.
     *
     * @param context Context where the {@link ContentResolver#query(Uri, String[], String, String[], String)}
     *                will be executed.
     * @param intent  Intent to "sign".
     * @return True if the sign succeeded, false otherwise.
     */
    private static boolean signIntent(@NonNull Context context, @NonNull Intent intent) {
        try {
            long currentTime = System.currentTimeMillis();
            String sender = context.getPackageName();
            ContentResolver resolver = context.getContentResolver();
            Cursor c = resolver.query(REQUEST_TOKEN_URI, null, Token.TIMESTAMP+" = ?", new String[]{ String.valueOf(currentTime) }, null);
            if (null == c) return false;
            String token = "";
            if (c.moveToFirst()) token = c.getString(c.getColumnIndex(Token.TOKEN));
            c.close();

            ByteBuffer buf = ByteBuffer.allocate((Long.SIZE / Byte.SIZE) + token.getBytes().length + sender.getBytes().length);
            buf.putLong(currentTime);
            buf.put(token.getBytes());
            buf.put(sender.getBytes());
            intent.putExtra(EXTRA_TOKEN, new String(Base64.encode(buf.array(), Base64.DEFAULT)));
            return true;
        } catch (Throwable ignore) {}
        return false;
    }
}
