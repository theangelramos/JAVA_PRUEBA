package com.chakray.usersapi.util;

import java.util.regex.Pattern;

public class ValidatorUtil {

    private static final Pattern RFC_PATTERN = 
        Pattern.compile("^[A-ZÑ&]{3,4}\\d{6}[A-Z0-9]{3}$");

    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^(\\+\\d{1,3}[\\s-]?)?(\\d[\\s-]?){10}$");

    public static boolean isValidTaxId(String taxId) {
        if (taxId == null) return false;
        return RFC_PATTERN.matcher(taxId.trim()).matches();
    }

    public static boolean isValidPhone(String phone) {
        if (phone == null) return false;
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }
}