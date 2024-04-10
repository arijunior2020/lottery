package com.lottery.marketplace.util;

import lombok.experimental.UtilityClass;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class PhoneNumberExtractor {
    private static final Pattern phoneNumberPatternValidation = Pattern.compile("^\\((\\d{2})\\) (\\d{5})-(\\d{4})$");

    private static final String INVALID_PHONE_MESSAGE = "Invalid phone number format: ";

    public static String extractAreaCode(String phoneNumber) {
        Matcher matcher = phoneNumberPatternValidation.matcher(phoneNumber);
        if (!matcher.find()) {
            throw new IllegalArgumentException(INVALID_PHONE_MESSAGE + phoneNumber);
        }

        return matcher.group(1);
    }

    public static String extractNumberWithoutDash(String phoneNumber) {
        Matcher matcher = phoneNumberPatternValidation.matcher(phoneNumber);
        if (!matcher.find()) {
            throw new IllegalArgumentException(INVALID_PHONE_MESSAGE + phoneNumber);
        }

        return matcher.group(2).concat(matcher.group(3));
    }

    public static void isValidPhoneNumber(String phoneNumber) {
        Matcher matcher = phoneNumberPatternValidation.matcher(phoneNumber);
        if (!matcher.find()) {
            throw new IllegalArgumentException(INVALID_PHONE_MESSAGE + phoneNumber);
        }
    }
}