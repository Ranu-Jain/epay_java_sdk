package com.sbi.epay.utils;

import com.sbi.epay.exceptions.SBIEpayException;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;
import org.json.JSONObject;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class ApiUtils {

    private static final Map<String, String> headers = new HashMap<>();
    private static OkHttpClient client;
    private static String version = null;

    public static void createHttpClientInstance(boolean enableLogging) throws SBIEpayException {
        if (client == null) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            if (enableLogging) {
                loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
            } else {
                loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
            }

            try {
                client = new OkHttpClient.Builder().readTimeout(60, TimeUnit.SECONDS).writeTimeout(60, TimeUnit.SECONDS).addInterceptor(loggingInterceptor).sslSocketFactory(new CustomTLSSocketFactory(), createDefaultTrustManager()).build();
            } catch (Exception e) {
                throw new SBIEpayException(e);
            }
        }

        Properties properties = new Properties();
        try {
            properties.load(ApiUtils.class.getResourceAsStream("/project.properties"));
            version = (String) properties.get("version");
        } catch (IOException e) {
            throw new SBIEpayException(e.getMessage());
        }
    }

    public static Response postRequest(String version, String path, String service, JSONObject requestObject, String auth) throws SBIEpayException {

        HttpUrl.Builder builder = getBuilder(version, path, service);

        RequestBody requestBody;

        if (requestObject != null && requestObject.has("file")) {
            requestBody = fileRequestBody(requestObject);
        } else {
            String requestContent = requestObject == null ? "" : requestObject.toString();
            requestBody = RequestBody.create(Constants.MEDIA_TYPE_JSON, requestContent);
        }

        Request request = createRequest(Method.POST.name(), builder.build().toString(), requestBody, auth);
        return processRequest(request);
    }

    public static Response getRequest(String version, String path, String service, JSONObject requestObject, String auth) throws SBIEpayException {
        HttpUrl.Builder builder = getBuilder(version, path, service);
        addQueryParams(builder, requestObject);
        Request request = createRequest(Method.GET.name(), builder.build().toString(), null, auth);
        return processRequest(request);
    }

    private static HttpUrl.Builder getBuilder(String version, String path, String service) {
        return getAPIBuilder(version, path, service);
    }

    private static HttpUrl.Builder getAPIBuilder(String version, String path, String service) {
        return new HttpUrl.Builder().scheme(Constants.SCHEME).host(Constants.HOSTNAME).port(Constants.PORT).addPathSegment(service).addPathSegment(version).addPathSegments(path);
    }

    private static Request createRequest(String method, String url, RequestBody requestBody, String auth) {
        Request.Builder builder = new Request.Builder().url(url);

        if (auth != null) {
            builder.addHeader(Constants.AUTH_HEADER_KEY, auth);
        }

        builder.addHeader(Constants.USER_AGENT, "SBIEpay/v1 JAVASDK/" + version + " Java/" + System.getProperty("java.version"));

        for (Map.Entry<String, String> header : headers.entrySet()) {
            builder.addHeader(header.getKey(), header.getValue());
        }

        return builder.method(method, requestBody).build();
    }

    private static void addQueryParams(HttpUrl.Builder builder, JSONObject request) {
        if (request == null) return;

        Iterator<?> keys = request.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            builder.addQueryParameter(key, request.get(key).toString());
        }
    }

    private static Response processRequest(Request request) throws SBIEpayException {
        try {
            return client.newCall(request).execute();
        } catch (IOException e) {
            throw new SBIEpayException(e.getMessage());
        }
    }

    public static void addHeaders(Map<String, String> header) {
        headers.putAll(header);
    }

    private static X509TrustManager createDefaultTrustManager() throws NoSuchAlgorithmException, KeyStoreException {
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init((KeyStore) null);
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
        if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
            throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
        }
        return (X509TrustManager) trustManagers[0];
    }

    private static String getMediaType(String fileName) {
        int extensionIndex = fileName.lastIndexOf('.');
        String extenionName = fileName.substring(extensionIndex + 1);
        if (extenionName.equals("jpg") | extenionName.equals("jpeg") | extenionName.equals("png") | extenionName.equals("jfif")) {
            return "image/jpg";
        }
        return "image/pdf";
    }

    private static RequestBody fileRequestBody(JSONObject requestObject) {
        File fileToUpload = new File((String) requestObject.get("file"));
        String fileName = fileToUpload.getName();

        MediaType mediaType = MediaType.parse(getMediaType(fileName));
        RequestBody fileBody = RequestBody.create(mediaType, fileToUpload);

        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        multipartBodyBuilder.addFormDataPart("file", fileName, fileBody);

        Iterator<?> iterator = requestObject.keys();
        while (iterator.hasNext()) {
            Object key = iterator.next();
            Object value = requestObject.get(key.toString());
            multipartBodyBuilder.addFormDataPart((String) key, (String) value);
        }

        return multipartBodyBuilder.build();
    }

    private enum Method {
        GET, POST
    }
}
