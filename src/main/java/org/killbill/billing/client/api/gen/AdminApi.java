/*
 * Copyright 2014-2018 Groupon, Inc
 * Copyright 2014-2018 The Billing Project, LLC
 *
 * The Billing Project licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */


package org.killbill.billing.client.api.gen;


import org.killbill.billing.client.model.gen.AdminPayment;
import java.util.UUID;

import com.google.common.collect.Multimap;
import com.google.common.base.Preconditions;
import com.google.common.base.MoreObjects;
import com.google.common.collect.HashMultimap;

import org.killbill.billing.client.KillBillClientException;
import org.killbill.billing.client.KillBillHttpClient;
import org.killbill.billing.client.RequestOptions;
import org.killbill.billing.client.RequestOptions.RequestOptionsBuilder;


/**
 *           DO NOT EDIT !!!
 *
 * This code has been generated by the Kill Bill swagger generator.
 *  @See https://github.com/killbill/killbill-swagger-coden
 */
public class AdminApi {

    private final KillBillHttpClient httpClient;

    public AdminApi() {
        this(new KillBillHttpClient());
    }

    public AdminApi(final KillBillHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public void invalidatesCache(final String cacheName,  final RequestOptions inputOptions) throws KillBillClientException {

        final String uri = "/1.0/kb/admin/cache";

        final Multimap<String, String> queryParams = HashMultimap.<String, String>create(inputOptions.getQueryParams());
        queryParams.put("cacheName", String.valueOf(cacheName));

        final RequestOptionsBuilder inputOptionsBuilder = inputOptions.extend();
        inputOptionsBuilder.withQueryParams(queryParams);
        final RequestOptions requestOptions = inputOptionsBuilder.build();

        httpClient.doDelete(uri, requestOptions);
    }

    public void invalidatesCacheByAccount(final UUID accountId,  final RequestOptions inputOptions) throws KillBillClientException {
        Preconditions.checkNotNull(accountId, "Missing the required parameter 'accountId' when calling invalidatesCacheByAccount");

        final String uri = "/1.0/kb/admin/cache/accounts/{accountId}"
          .replaceAll("\\{" + "accountId" + "\\}", accountId.toString());


        final RequestOptionsBuilder inputOptionsBuilder = inputOptions.extend();
        final RequestOptions requestOptions = inputOptionsBuilder.build();

        httpClient.doDelete(uri, requestOptions);
    }

    public void invalidatesCacheByTenant(final String tenantApiKey,  final RequestOptions inputOptions) throws KillBillClientException {

        final String uri = "/1.0/kb/admin/cache/tenants";

        final Multimap<String, String> queryParams = HashMultimap.<String, String>create(inputOptions.getQueryParams());
        queryParams.put("tenantApiKey", String.valueOf(tenantApiKey));

        final RequestOptionsBuilder inputOptionsBuilder = inputOptions.extend();
        inputOptionsBuilder.withQueryParams(queryParams);
        final RequestOptions requestOptions = inputOptionsBuilder.build();

        httpClient.doDelete(uri, requestOptions);
    }

    public void putInRotation( final RequestOptions inputOptions) throws KillBillClientException {

        final String uri = "/1.0/kb/admin/healthcheck";


        final RequestOptionsBuilder inputOptionsBuilder = inputOptions.extend();
        final Boolean followLocation = MoreObjects.firstNonNull(inputOptions.getFollowLocation(), Boolean.TRUE);
        inputOptionsBuilder.withFollowLocation(followLocation);
        inputOptionsBuilder.withHeader(KillBillHttpClient.HTTP_HEADER_CONTENT_TYPE, "application/json");
        final RequestOptions requestOptions = inputOptionsBuilder.build();

        httpClient.doPost(uri, null, requestOptions);
    }

    public void putOutOfRotation( final RequestOptions inputOptions) throws KillBillClientException {

        final String uri = "/1.0/kb/admin/healthcheck";


        final RequestOptionsBuilder inputOptionsBuilder = inputOptions.extend();
        final RequestOptions requestOptions = inputOptionsBuilder.build();

        httpClient.doDelete(uri, requestOptions);
    }

    public void triggerInvoiceGenerationForParkedAccounts(final Long offset, final Long limit,  final RequestOptions inputOptions) throws KillBillClientException {

        final String uri = "/1.0/kb/admin/invoices";

        final Multimap<String, String> queryParams = HashMultimap.<String, String>create(inputOptions.getQueryParams());
        queryParams.put("offset", String.valueOf(offset));
        queryParams.put("limit", String.valueOf(limit));

        final RequestOptionsBuilder inputOptionsBuilder = inputOptions.extend();
        final Boolean followLocation = MoreObjects.firstNonNull(inputOptions.getFollowLocation(), Boolean.TRUE);
        inputOptionsBuilder.withFollowLocation(followLocation);
        inputOptionsBuilder.withQueryParams(queryParams);
        inputOptionsBuilder.withHeader(KillBillHttpClient.HTTP_HEADER_CONTENT_TYPE, "application/json");
        inputOptionsBuilder.withHeader(KillBillHttpClient.HTTP_HEADER_ACCEPT, "application/json");
        final RequestOptions requestOptions = inputOptionsBuilder.build();

        httpClient.doPost(uri, null, requestOptions);
    }

    public void updatePaymentTransactionState(final AdminPayment body, final UUID paymentId, final UUID paymentTransactionId,  final RequestOptions inputOptions) throws KillBillClientException {
        Preconditions.checkNotNull(body, "Missing the required parameter 'body' when calling updatePaymentTransactionState");
        Preconditions.checkNotNull(paymentId, "Missing the required parameter 'paymentId' when calling updatePaymentTransactionState");
        Preconditions.checkNotNull(paymentTransactionId, "Missing the required parameter 'paymentTransactionId' when calling updatePaymentTransactionState");

        final String uri = "/1.0/kb/admin/payments/{paymentId}/transactions/{paymentTransactionId}"
          .replaceAll("\\{" + "paymentId" + "\\}", paymentId.toString())
          .replaceAll("\\{" + "paymentTransactionId" + "\\}", paymentTransactionId.toString());


        final RequestOptionsBuilder inputOptionsBuilder = inputOptions.extend();
        inputOptionsBuilder.withHeader(KillBillHttpClient.HTTP_HEADER_CONTENT_TYPE, "application/json");
        inputOptionsBuilder.withHeader(KillBillHttpClient.HTTP_HEADER_ACCEPT, "application/json");
        final RequestOptions requestOptions = inputOptionsBuilder.build();

        httpClient.doPut(uri, body, requestOptions);
    }

}
