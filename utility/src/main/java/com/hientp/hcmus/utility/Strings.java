package com.hientp.hcmus.utility;

import android.text.TextUtils;
import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.List;


public final class Strings {

    private Strings() {
    }

    public static Charset charsetUTF8() {
        return Charset.forName("UTF-8");
    }

    public static String joinWithDelimiter(String delimiter, List longList) {
        return joinWithDelimiter(delimiter, longList.toArray());
    }

    @SafeVarargs
    public static <T> String joinWithDelimiter(String delimiter, T... values) {
        StringBuilder sb = new StringBuilder();
        String loopDelimiter = "";
        for (T value : values) {
            if (value != null) {
                sb.append(loopDelimiter);
                sb.append(String.valueOf(value));

                loopDelimiter = delimiter;
            }
        }

        return sb.toString();
    }

    public static boolean hasAnyPrefix(String number, String... prefixes) {
        if (number == null) {
            return false;
        }
        for (String prefix : prefixes) {
            if (number.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    public static String trim(String src) {
        src = src.trim();
        if (src.length() == 0) {
            return src;
        }
        while (src.startsWith("\n")) {
            src = src.substring(1);
        }
        while (src.endsWith("\n")) {
            src = src.substring(0, src.length() - 1);
        }
        return src;
    }

    public static String stripLeadingPath(String input) {
        if (input == null) {
            return "";
        }

        String pattern = "(^\\.\\.)|(^/+)";
        String prevOutput = input;
        String output = input.replaceAll(pattern, "");
        while (!output.equals(prevOutput)) {
            prevOutput = output;
            output = output.replaceAll(pattern, "");
        }
        return output;
    }

    public static String getDomainName(String url) {
        try {
            final String[] DOMAIN = {"com", "org", "net", "edu", "co", "gov", "asia",};

            URI uri = new URI(url);
            String domain = uri.getHost();

            if (domain != null) {
                String dot = ".";
                String[] parts = domain.split("\\.");

                for (String firstDomain : DOMAIN) {
                    if (parts[parts.length - 2].equals(firstDomain) && parts.length >= 3) {
                        return parts[parts.length - 3] + dot + parts[parts.length - 2] + dot + parts[parts.length - 1];
                    }
                }

                return parts[parts.length - 2] + dot + parts[parts.length - 1];
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return "";
        }
        return "";
    }

    public static String pathSegmentsToString(List<String> pathSegments) {
        StringBuilder out = new StringBuilder();
        for (int i = 0, size = pathSegments.size(); i < size; i++) {
            out.append('/');
            out.append(pathSegments.get(i));
        }
        return out.toString();
    }

    /**
     * encode UTF16
     *
     * @param text : source text
     * @return: text encoded
     */
    public static String encodeUTF16(String text) {
        try {
            text = Base64.encodeToString(text.getBytes("UTF-16"), Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
            text = Base64.encodeToString(text.getBytes(), Base64.DEFAULT);
        }
        return text;
    }

    /**
     * decode UTF16
     *
     * @param text : source text
     * @return: text decoded
     */
    public static String decodeUTF16(String text) {
        try {
            try {
                text = new String(Base64.decode(text, Base64.DEFAULT), "UTF-16");
            } catch (UnsupportedEncodingException e) {
                text = new String(Base64.decode(text, Base64.DEFAULT), "UTF-8");
            }
        } catch (UnsupportedEncodingException e) {
            return "";
        }
        return text;
    }

    /**
     * Strip all while spaces within input string
     *
     * @param value: string need to be strip
     * @return: string is removed all white spaces
     */
    public static String stripWhitespace(String value) {
        if (TextUtils.isEmpty(value)) {
            return "";
        }

        String result = value.trim();
        result = result.replaceAll("[^+\\d]", "");
        return result;
    }
}
