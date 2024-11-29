package com.sbi.epay.client;

import com.sbi.epay.entity.Order;
import com.sbi.epay.exceptions.SBIEpayException;
import com.sbi.epay.utils.Constants;
import org.json.JSONObject;

public class CustomerClient extends ApiClient {

    public CustomerClient(String auth) {
        super(auth);
    }

    public Order create(JSONObject request) throws SBIEpayException {
        return post(Constants.VERSION, Constants.CUSTOMER_CREATE, Constants.TX_SERVICE, request);
    }

}
