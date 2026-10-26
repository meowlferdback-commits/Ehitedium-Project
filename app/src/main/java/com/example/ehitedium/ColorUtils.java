package com.example.ehitedium;

import android.graphics.Color;
import java.util.Locale;
import java.util.regex.Pattern;

public final class ColorUtils {
    private static final Pattern HEX = Pattern.compile("^#?[0-9a-fA-F]{6}$");

    private ColorUtils() {}

    public static boolean isValidHex(String value) {
        return value != null && HEX.matcher(value.trim()).matches();
    }

    public static int parseHex(String value) {
        if (!isValidHex(value)) {
            throw new IllegalArgumentException("Ожидается HEX-цвет вида #RRGGBB");
        }
        String clean = value.trim();
        if (clean.startsWith("#")) clean = clean.substring(1);
        return Color.parseColor("#" + clean.toUpperCase(Locale.ROOT));
    }

    public static String toHex(int color) {
        return String.format(Locale.ROOT, "#%06X", (0xFFFFFF & color));
    }
}
