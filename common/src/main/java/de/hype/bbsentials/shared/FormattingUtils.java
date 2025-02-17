package de.hype.bbsentials.shared;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;

public class FormattingUtils {
    /**
     * This shortens the amount to numbers like 10k or 10B etc.
     *
     * @param amount The amount to format
     */
    public static String formatAmountShortened(Double amount) {
        DecimalFormat df = new DecimalFormat("0.0");
        if (amount >= 1_000_000_000_000L) {
            return df.format(amount / 1_000_000_000_000.0) + "T";
        } else if (amount >= 1_000_000_000) {
            return df.format(amount / 1_000_000_000.0) + "B";
        } else if (amount >= 1_000_000) {
            return df.format(amount / 1_000_000.0) + "M";
        } else if (amount >= 1_000) {
            return df.format(amount / 1_000.0) + "k";
        } else {
            return String.valueOf(amount);
        }
    }

    public static String formatAmountShortened(Integer amount) {
        DecimalFormat df = new DecimalFormat("0.0");
        if (amount >= 1_000_000_000) {
            return df.format(amount / 1_000_000_000.0) + "B";
        } else if (amount >= 1_000_000) {
            return df.format(amount / 1_000_000.0) + "M";
        } else if (amount >= 1_000) {
            return df.format(amount / 1_000.0) + "k";
        } else {
            return String.valueOf(amount);
        }
    }

    /**
     * This shortens the amount to numbers like 10k or 10B etc.
     *
     * @param amount The amount to format
     */
    public static String formatAmountShortened(Long amount) {
        DecimalFormat df = new DecimalFormat("0.0");
        if (amount >= 1_000_000_000_000L) {
            return df.format(amount / 1_000_000_000_000.0) + "T";
        } else if (amount >= 1_000_000_000) {
            return df.format(amount / 1_000_000_000.0) + "B";
        } else if (amount >= 1_000_000) {
            return df.format(amount / 1_000_000.0) + "M";
        } else if (amount >= 1_000) {
            return df.format(amount / 1_000.0) + "k";
        } else {
            return String.valueOf(amount);
        }
    }

    /**
     * Returns the number as a String like '1st', '2nd', '3rd', '4th', '5th', etc.
     *
     * @param number The number to convert to an ordinal
     */
    public static String toOrdinal(int number) {
        if (number <= 0) {
            return String.valueOf(number); // Handle non-positive numbers
        }

        String suffix;
        int mod100 = number % 100;
        int mod10 = number % 10;

        if (mod100 >= 11 && mod100 <= 13) {
            suffix = "th"; // Special case for 11th, 12th, 13th
        } else {
            switch (mod10) {
                case 1:
                    suffix = "st";
                    break;
                case 2:
                    suffix = "nd";
                    break;
                case 3:
                    suffix = "rd";
                    break;
                default:
                    suffix = "th";
                    break;
            }
        }

        return number + suffix;
    }

    public static String formatTime(Instant time) {
        Duration duration = Duration.between(Instant.now(), time);
        long seconds = duration.getSeconds();
        String prefix = seconds < 0 ? "in %s" : "%s ago";
        int days = (int) (seconds / 86400);
        int hours = (int) ((seconds % 86400) / 3600);
        int minutes = (int) ((seconds % 3600) / 60);
        int secs = (int) (seconds % 60);
        StringBuilder sb = new StringBuilder();
        if (days != 0) sb.append(days).append("d ");
        if (hours != 0) sb.append(hours).append("h ");
        if (minutes != 0) sb.append(minutes).append("m ");
        if (secs != 0) sb.append(secs).append("s");
        return String.format(prefix, sb.toString());
    }

    public static String formatAmount(Long amount) {
        return NumberFormat.getInstance(Locale.US).format(amount);
    }
}
