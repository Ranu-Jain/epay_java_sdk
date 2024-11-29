package com.sbi.epay.utils;

import okhttp3.MediaType;

public class Constants {

    // API constants
    public static final String SCHEME = "https";
    public static final String HOSTNAME = "sbi.epay.api.com";
    public static final String TX_SERVICE = "transaction/sdk";
    public static final Integer PORT = 8080;
    public static final String VERSION = "v1";
    public static final String AUTH_HEADER_KEY = "Authorization";
    public static final String USER_AGENT = "User-Agent";
    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

    // API URI
    public static final String ORDER_CREATE = "order";
    public static final String CUSTOMER_CREATE = "customer";

}
