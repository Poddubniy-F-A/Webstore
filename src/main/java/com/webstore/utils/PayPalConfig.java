package com.webstore.utils;

import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.OAuthTokenCredential;
import com.paypal.base.rest.PayPalRESTException;

import java.util.Map;

import static java.util.Collections.singletonMap;

public class PayPalConfig {
    private static final String MODE = "sandbox",
            CLIENT_ID = "AaooF-25wxpbuWme3LOgP5PdebnKczOYBa7qhuZ39KFhM0P-mlU4LD2a36dYxMALDiF32ilA0lcnRRNv",
            CLIENT_SECRET = "EJTX4Pb8jtaDu1fci-4gxPgmWxmNGqlRket_TQUMtI54GCEFqAy3KE9e5G5yQoaLRhpyoTrDdZwCh2bP";

    public static APIContext getAPIContext() throws PayPalRESTException {
        Map<String, String> configMap = singletonMap("mode", MODE);

        APIContext apiContext =
                new APIContext(new OAuthTokenCredential(CLIENT_ID, CLIENT_SECRET, configMap).getAccessToken());
        apiContext.setConfigurationMap(configMap);
        return apiContext;
    }
}
