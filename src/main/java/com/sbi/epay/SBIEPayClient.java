package com.sbi.epay;

import java.util.Map;

import com.sbi.epay.client.CustomerClient;
import com.sbi.epay.client.OrderClient;
import com.sbi.epay.exceptions.SBIEpayException;
import com.sbi.epay.utils.ApiUtils;
import okhttp3.Credentials;

public class SBIEPayClient {
    
    public OrderClient orders;
    public CustomerClient customers;

    public SBIEPayClient(String key, String secret) throws SBIEpayException {
        this(key, secret, false);
    }

    public SBIEPayClient(String key, String secret, Boolean enableLogging) throws SBIEpayException {
        String auth = Credentials.basic(key, secret);
        initializeResources(auth, enableLogging);
    }
    private void initializeResources(String auth, Boolean enableLogging) throws SBIEpayException {
        ApiUtils.createHttpClientInstance(enableLogging);
        orders = new OrderClient(auth);
        customers = new CustomerClient(auth);
    }

    public SBIEPayClient addHeaders(Map<String, String> headers) {
        ApiUtils.addHeaders(headers);
        return this;
    }
}
