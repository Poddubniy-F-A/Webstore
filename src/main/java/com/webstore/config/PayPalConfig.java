package com.webstore.config;

import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.OAuthTokenCredential;
import com.paypal.base.rest.PayPalRESTException;

import java.util.Map;

import static java.util.Collections.singletonMap;

public class PayPalConfig {

    private static final String MODE = "sandbox",
            CLIENT_ID = "",
            CLIENT_SECRET = "";

    public static APIContext getAPIContext() throws PayPalRESTException {
        Map<String, String> configMap = singletonMap("mode", MODE);

        APIContext apiContext =
                new APIContext(new OAuthTokenCredential(CLIENT_ID, CLIENT_SECRET, configMap).getAccessToken());
        apiContext.setConfigurationMap(configMap);
        return apiContext;
    }
}
