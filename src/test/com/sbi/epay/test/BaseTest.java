package com.sbi.epay.test;


import com.sbi.epay.utils.Constants;
import okhttp3.*;
import okio.Buffer;
import org.json.JSONObject;
import org.junit.Before;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BaseTest {

    private OkHttpClient okHttpClient;
    Response mockedResponse;

    static final String TEST_SECRET_KEY = "test";

    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);
        mockURL(Collections.emptyList());
    }



    protected void mockResponseHTTPCodeFromExternalClient(int code)
    {
        when(mockedResponse.code()).thenReturn(code);
    }
    protected void mockURL(List<String> urlString)
    {
        HttpUrl url = mock(HttpUrl.class);
        when(url.pathSegments()).thenReturn(urlString);
        Request request = mock(Request.class);
        when(request.url()).thenReturn(url);
        when(mockedResponse.request()).thenReturn(request);
    }
    protected void mockResponseFromExternalClient(String response) throws IOException {
        if(response.equals("[]")){
            ResponseBody rb = mock(ResponseBody.class);
            when(rb.string()).thenReturn(response);
            when(mockedResponse.body()).thenReturn(rb);
        }else{
            JSONObject parse = new JSONObject(response);
            ResponseBody rb = mock(ResponseBody.class);
            when(rb.string()).thenReturn(parse.toString());
            when(mockedResponse.request().url()).thenReturn(
                    new HttpUrl.Builder().scheme("https").host("auth.razorpay.com").addPathSegments("/token").build());
            when(mockedResponse.body()).thenReturn(rb);
        }
    }

    protected OkHttpClient getOkHttpClient()
    {
        return okHttpClient;
    }

    protected String getHost(String url) {
        return Constants.SCHEME + "://" + Constants.HOSTNAME + "/" + Constants.VERSION + "/" + url;
    }

    protected void verifySentRequest(boolean hasBody, String request, String requestPath) {
        ArgumentCaptor<Request> req = ArgumentCaptor.forClass(Request.class);
        Mockito.verify(getOkHttpClient()).newCall(req.capture());
        if(hasBody) {
            assertEquals(new JSONObject(request).toString(), new JSONObject(bodyToString(req.getAllValues().get(0))).toString());
        }
        assertEquals(requestPath, req.getValue().url().toString());
    }

    protected void verifySendRequestAndHeaders(String request, String requestPath, Map<String, String> headers) {
        ArgumentCaptor<Request> req = ArgumentCaptor.forClass(Request.class);
        Mockito.verify(getOkHttpClient()).newCall(req.capture());

        Request capturedRequest = req.getValue();
        headers.forEach((key, value) -> {
            assertEquals(capturedRequest.header(key), value);
        });
    }

    private static String bodyToString(final Request request) {

        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }
}
