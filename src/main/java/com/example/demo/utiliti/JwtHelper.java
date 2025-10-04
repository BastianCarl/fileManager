package com.example.demo.utiliti;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JwtHelper {
    private static final String AUTHENTICATION_PREFIX = "Bearer ";

    public  static String getJwtTokenValue(String token) {
        Pattern pattern = Pattern.compile(AUTHENTICATION_PREFIX + "(.*)");
        Matcher matcher = pattern.matcher(token);
        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new IllegalArgumentException("Auth header doesn't match expected pattern");
    }
}
