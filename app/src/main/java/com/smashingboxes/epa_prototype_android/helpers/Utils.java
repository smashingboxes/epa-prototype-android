package com.smashingboxes.epa_prototype_android.helpers;

import java.io.Closeable;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Austin Lanier on 12/28/15.
 * Updated by
 */
public class Utils {

    public static final String DISPLAY_FORMAT = "MMM dd";
    public static final SimpleDateFormat displayFormatter = new SimpleDateFormat(
            DISPLAY_FORMAT, Locale.getDefault());
    public static final SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ",
            Locale.getDefault());

    private static final DecimalFormat df = new DecimalFormat("###.##");
    static {
        df.setRoundingMode(RoundingMode.CEILING);
    }

    public static String formatDate(String date){
        try {
            return displayFormatter.format(parser.parse(date));
        } catch(java.text.ParseException e){
            e.printStackTrace();
            return date;
        }
    }

    public static String formatLong(long time){
        return parser.format(new Date(time));
    }

    public static String formatDistance(float distance){
        return df.format(distance);
    }

    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}