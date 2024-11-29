package com.sbi.epay.test;

import com.sbi.epay.client.OrderClient;
import com.sbi.epay.entity.Order;
import com.sbi.epay.utils.Constants;
import org.json.JSONObject;
import org.junit.Test;
import org.mockito.InjectMocks;

import java.io.IOException;

import static org.junit.Assert.*;

public class OrderClientTest extends BaseTest{

    @InjectMocks
    protected OrderClient orderClient = new OrderClient(TEST_SECRET_KEY);

    private static final String ORDER_ID = "order_EKwxwAgItmmXdp";

    /**
     * Create order with basic details such as currency and amount details
     * @throws TransactionException
     */
    @Test
    public void create() throws TransactionException {
        JSONObject request = new JSONObject("{" +
                "amount:50000," +
                "currency:\"INR\"," +
                "receipt:\"receipt#1\"," +
                "notes:" +
                "{key1:\"value3\"," +
                "key2:\"value2\"}}");

        String mockedResponseJson = "{" +
                "\"id\":\"order_EKwxwAgItmmXdp\"," +
                "\"entity\":\"order\"," +
                "\"amount\":50000," +
                "\"amount_paid\":0," +
                "\"amount_due\":50000," +
                "\"currency\":\"INR\"," +
                "\"receipt\":\"receipt#1\"," +
                "\"offer_id\":null," +
                "\"status\":\"created\"," +
                "\"attempts\":0," +
                "\"notes\":[]," +
                "\"created_at\":1582628071}";
        try {
            mockResponseFromExternalClient(mockedResponseJson);
            mockResponseHTTPCodeFromExternalClient(200);
            Order fetch = orderClient.create(request);
            assertNotNull(fetch);
            assertEquals(ORDER_ID,fetch.get("id"));
            assertEquals("order",fetch.get("entity"));
            assertTrue(fetch.has("amount"));
            assertTrue(fetch.has("amount_paid"));
            String createRequest = getHost(Constants.ORDER_CREATE);
            verifySentRequest(true, request.toString(), createRequest);
        } catch (IOException e) {
            assertTrue(false);
        }
    }


    /**
     * Retrieve the order details using order id.
     * @throws TransactionException
     */
    @Test
    public void fetch() throws TransactionException{
        String mockedResponseJson = "{\"id\":"+ORDER_ID+"," +
                "\"entity\":\"order\"," +
                "\"amount\":2200," +
                "\"amount_paid\":0," +
                "\"amount_due\":2200," +
                "\"currency\":\"INR\"," +
                "\"receipt\":\"Receipt#211\"," +
                "\"status\":\"attempted\"," +
                "\"attempts\":1," +
                "\"notes\":[]," +
                "\"created_at\":1572505143}";
        try {
            mockResponseFromExternalClient(mockedResponseJson);
            mockResponseHTTPCodeFromExternalClient(200);
            Order fetch = orderClient.fetch(ORDER_ID);
            assertNotNull(fetch);
            assertEquals(true,fetch.has("id"));
            assertTrue(fetch.has("entity"));
            assertTrue(fetch.has("amount"));
            assertTrue(fetch.has("amount_paid"));
            String fetchRequest = getHost(String.format(Constants.ORDER_GET,ORDER_ID));
            verifySentRequest(false, null, fetchRequest);
        } catch (IOException e) {
            assertTrue(false);
        }
    }
}