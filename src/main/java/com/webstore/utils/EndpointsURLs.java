package com.webstore.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EndpointsURLs {

    @Value("${app.host}")
    public String HOST;

    @Value("${app.endpoints.main}")
    public String MAIN;

    @Value("${app.endpoints.auth.moderator.main}")
    public String AUTH_MODERATOR_MAIN;
    @Value("${app.endpoints.auth.moderator.login}")
    public String AUTH_MODERATOR_LOGIN;
    @Value("${app.endpoints.auth.moderator.failure}")
    public String AUTH_MODERATOR_FAILURE;

    @Value("${app.endpoints.auth.wh_worker.main}")
    public String AUTH_WH_WORKER_MAIN;
    @Value("${app.endpoints.auth.wh_worker.login}")
    public String AUTH_WH_WORKER_LOGIN;
    @Value("${app.endpoints.auth.wh_worker.failure}")
    public String AUTH_WH_WORKER_FAILURE;

    @Value("${app.endpoints.auth.customer.main}")
    public String AUTH_CUSTOMER_MAIN;
    @Value("${app.endpoints.auth.customer.login}")
    public String AUTH_CUSTOMER_LOGIN;
    @Value("${app.endpoints.auth.customer.failure}")
    public String AUTH_CUSTOMER_FAILURE;
    @Value("${app.endpoints.auth.customer.registration}")
    public String AUTH_CUSTOMER_REGISTRATION;

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

    @Value("${app.endpoints.errors.access_denied.cust_service}")
    public String ERRORS_ACCESS_DENIED_CUST_SERVICE;
    @Value("${app.endpoints.errors.access_denied.management}")
    public String ERRORS_ACCESS_DENIED_MANAGEMENT;
    @Value("${app.endpoints.errors.access_denied.warehouse}")
    public String ERRORS_ACCESS_DENIED_WAREHOUSE;
}
