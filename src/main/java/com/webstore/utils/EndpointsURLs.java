package com.webstore.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EndpointsURLs {

    @Value("${app.endpoints.main}")
    public String MAIN;

    @Value("${app.endpoints.auth.main}")
    public String AUTH_MAIN;
    @Value("${app.endpoints.auth.login}")
    public String AUTH_LOGIN;
    @Value("${app.endpoints.auth.failure}")
    public String AUTH_FAILURE;
    @Value("${app.endpoints.auth.registration}")
    public String AUTH_REGISTRATION;
    @Value("${app.endpoints.auth.logout}")
    public String AUTH_LOGOUT;

    @Value("${app.endpoints.management.main}")
    public String MANAGEMENT_MAIN;
    @Value("${app.endpoints.management.creating}")
    public String MANAGEMENT_CREATING;
    @Value("${app.endpoints.management.editing}")
    public String MANAGEMENT_EDITING;

    @Value("${app.endpoints.warehouse.main}")
    public String WAREHOUSE_MAIN;

    @Value("${app.endpoints.catalog.main}")
    public String CATALOG_MAIN;

    @Value("${app.endpoints.cart.main}")
    public String CART_MAIN;
    @Value("${app.endpoints.cart.validating}")
    public String CART_VALIDATING;
    @Value("${app.endpoints.cart.finished_payment}")
    public String CART_FINISHED_PAYMENT;
    @Value("${app.endpoints.cart.canceled_payment}")
    public String CART_CANCELED_PAYMENT;

    @Value("${app.endpoints.feedbacks.main}")
    public String FEEDBACKS_MAIN;
    @Value("${app.endpoints.feedbacks.creating}")
    public String FEEDBACKS_CREATING;
    @Value("${app.endpoints.feedbacks.editing}")
    public String FEEDBACKS_EDITING;

    @Value("${app.endpoints.access_denied}")
    public String ACCESS_DENIED;
}
