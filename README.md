# SBI Epay Java SDK

Official java bindings for the [EPay API]

## Requirements

Java 1.8 or later

Mock Tests Support till Java 1.8

## Installation

### Maven users

Add this dependency to your project's POM:

```xml
<dependency>
 <groupId>com.sbi.epay</groupId>
 <artifactId>epay_java_sdk</artifactId>
 <version>1.0.0</version>
</dependency>
```

### Gradle users

Add this dependency to your project's build file:

```groovy
implementation "com.sbi.epay:epay_java_sdk:1.0.0"
```

## Usage
`SBIEpayClient` can be instantiated like below:

### Using Private Auth
Instantiate `SBIEpayClient` with `key_id` & `key_secret`. You can obtain the keys from the dashboard app <https://sbi.epay.com/merchant/app/keys>

```java
// Initialize client
SBIEpayClient instance = new SBIEpayClient("key_id", "key_secret");
```
* Add custom headers to request (optional)
```java
Map<String, String> headers = new HashMap<String, String>();
instance.addHeaders(headers);
```

## Supported Resources

* Make custom requests

You can make custom API requests using clients. For example, here is how to make custom request to `/order/path` endpoint.

```java
Entity response = instance.Order.post("path", JSONObject requestBody);
```
