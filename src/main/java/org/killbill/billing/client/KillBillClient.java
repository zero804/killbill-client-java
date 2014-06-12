/*
 * Copyright 2010-2013 Ning, Inc.
 * Copyright 2014 Groupon, Inc
 * Copyright 2014 The Billing Project, LLC
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

package org.killbill.billing.client;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.killbill.billing.catalog.api.BillingActionPolicy;
import org.killbill.billing.client.model.Account;
import org.killbill.billing.client.model.AccountEmail;
import org.killbill.billing.client.model.AccountEmails;
import org.killbill.billing.client.model.AccountTimeline;
import org.killbill.billing.client.model.Accounts;
import org.killbill.billing.client.model.Bundle;
import org.killbill.billing.client.model.Bundles;
import org.killbill.billing.client.model.Catalog;
import org.killbill.billing.client.model.Chargeback;
import org.killbill.billing.client.model.Chargebacks;
import org.killbill.billing.client.model.Credit;
import org.killbill.billing.client.model.CustomField;
import org.killbill.billing.client.model.CustomFields;
import org.killbill.billing.client.model.DirectPayment;
import org.killbill.billing.client.model.DirectPayments;
import org.killbill.billing.client.model.DirectTransaction;
import org.killbill.billing.client.model.HostedPaymentPageFields;
import org.killbill.billing.client.model.HostedPaymentPageFormDescriptor;
import org.killbill.billing.client.model.Invoice;
import org.killbill.billing.client.model.InvoiceEmail;
import org.killbill.billing.client.model.InvoiceItem;
import org.killbill.billing.client.model.InvoiceItems;
import org.killbill.billing.client.model.Invoices;
import org.killbill.billing.client.model.OverdueState;
import org.killbill.billing.client.model.Payment;
import org.killbill.billing.client.model.PaymentMethod;
import org.killbill.billing.client.model.PaymentMethods;
import org.killbill.billing.client.model.Payments;
import org.killbill.billing.client.model.Permissions;
import org.killbill.billing.client.model.PlanDetail;
import org.killbill.billing.client.model.PlanDetails;
import org.killbill.billing.client.model.Refund;
import org.killbill.billing.client.model.Refunds;
import org.killbill.billing.client.model.Subscription;
import org.killbill.billing.client.model.TagDefinition;
import org.killbill.billing.client.model.TagDefinitions;
import org.killbill.billing.client.model.Tags;
import org.killbill.billing.client.model.Tenant;
import org.killbill.billing.client.model.TenantKey;
import org.killbill.billing.entitlement.api.Entitlement.EntitlementActionPolicy;
import org.killbill.billing.jaxrs.resources.JaxrsResource;
import org.killbill.billing.util.api.AuditLevel;

import com.ning.http.client.Response;
import com.ning.http.util.UTF8UrlEncoder;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import static org.killbill.billing.client.KillBillHttpClient.DEFAULT_EMPTY_QUERY;
import static org.killbill.billing.client.KillBillHttpClient.DEFAULT_HTTP_TIMEOUT_SEC;
import static org.killbill.billing.jaxrs.resources.JaxrsResource.OVERDUE;
import static org.killbill.billing.jaxrs.resources.JaxrsResource.QUERY_DELETE_DEFAULT_PM_WITH_AUTO_PAY_OFF;

public class KillBillClient {

    private final KillBillHttpClient httpClient;

    public KillBillClient() {
        this(new KillBillHttpClient());
    }

    public KillBillClient(final KillBillHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public void close() {
        httpClient.close();
    }

    // Accounts

    public Accounts getAccounts() throws KillBillClientException {
        return getAccounts(0L, 100L);
    }

    public Accounts getAccounts(final Long offset, final Long limit) throws KillBillClientException {
        return getAccounts(offset, limit, AuditLevel.NONE);
    }

    public Accounts getAccounts(final Long offset, final Long limit, final AuditLevel auditLevel) throws KillBillClientException {
        final String uri = JaxrsResource.ACCOUNTS_PATH + "/" + JaxrsResource.PAGINATION;

        final Multimap<String, String> queryParams = ImmutableMultimap.<String, String>of(JaxrsResource.QUERY_SEARCH_OFFSET, String.valueOf(offset),
                                                                                          JaxrsResource.QUERY_SEARCH_LIMIT, String.valueOf(limit),
                                                                                          JaxrsResource.QUERY_AUDIT, auditLevel.toString());

        return httpClient.doGet(uri, queryParams, Accounts.class);
    }

    public Account getAccount(final UUID accountId) throws KillBillClientException {
        return getAccount(accountId, false, false);
    }

    public Account getAccount(final UUID accountId, final boolean withBalance, final boolean withCBA) throws KillBillClientException {
        final String uri = JaxrsResource.ACCOUNTS_PATH + "/" + accountId;

        final Multimap<String, String> queryParams = ImmutableMultimap.<String, String>of(JaxrsResource.QUERY_ACCOUNT_WITH_BALANCE, withBalance ? "true" : "false",
                                                                                          JaxrsResource.QUERY_ACCOUNT_WITH_BALANCE_AND_CBA, withCBA ? "true" : "false");

        return httpClient.doGet(uri, queryParams, Account.class);
    }

    public Account getAccount(final String externalKey) throws KillBillClientException {
        return getAccount(externalKey, false, false);
    }

    public Account getAccount(final String externalKey, final boolean withBalance, final boolean withCBA) throws KillBillClientException {
        final Multimap<String, String> queryParams = ImmutableMultimap.<String, String>of(JaxrsResource.QUERY_EXTERNAL_KEY, externalKey,
                                                                                          JaxrsResource.QUERY_ACCOUNT_WITH_BALANCE, withBalance ? "true" : "false",
                                                                                          JaxrsResource.QUERY_ACCOUNT_WITH_BALANCE_AND_CBA, withCBA ? "true" : "false");

        return httpClient.doGet(JaxrsResource.ACCOUNTS_PATH, queryParams, Account.class);
    }

    public Accounts searchAccounts(final String key) throws KillBillClientException {
        return searchAccounts(key, 0L, 100L);
    }

    public Accounts searchAccounts(final String key, final Long offset, final Long limit) throws KillBillClientException {
        return searchAccounts(key, offset, limit, AuditLevel.NONE);
    }

    public Accounts searchAccounts(final String key, final Long offset, final Long limit, final AuditLevel auditLevel) throws KillBillClientException {
        final String uri = JaxrsResource.ACCOUNTS_PATH + "/" + JaxrsResource.SEARCH + "/" + UTF8UrlEncoder.encode(key);

        final Multimap<String, String> queryParams = ImmutableMultimap.<String, String>of(JaxrsResource.QUERY_SEARCH_OFFSET, String.valueOf(offset),
                                                                                          JaxrsResource.QUERY_SEARCH_LIMIT, String.valueOf(limit),
                                                                                          JaxrsResource.QUERY_AUDIT, auditLevel.toString());

        return httpClient.doGet(uri, DEFAULT_EMPTY_QUERY, Accounts.class);
    }

    public AccountTimeline getAccountTimeline(final UUID accountId) throws KillBillClientException {
        return getAccountTimeline(accountId, AuditLevel.NONE);
    }

    public AccountTimeline getAccountTimeline(final UUID accountId, final AuditLevel auditLevel) throws KillBillClientException {
        final String uri = JaxrsResource.ACCOUNTS_PATH + "/" + accountId + "/" + JaxrsResource.TIMELINE;

        final Multimap<String, String> queryParams = ImmutableMultimap.<String, String>of(JaxrsResource.QUERY_AUDIT, auditLevel.toString());

        return httpClient.doGet(uri, queryParams, AccountTimeline.class);
    }

    public Account createAccount(final Account account, final String createdBy, final String reason, final String comment) throws KillBillClientException {
        final Multimap<String, String> queryParams = paramsWithAudit(createdBy, reason, comment);

        return httpClient.doPostAndFollowLocation(JaxrsResource.ACCOUNTS_PATH, account, queryParams, Account.class);
    }

    public Account updateAccount(final Account account, final String createdBy, final String reason, final String comment) throws KillBillClientException {
        Preconditions.checkNotNull(account.getAccountId(), "Account#accountId cannot be null");

        final String uri = JaxrsResource.ACCOUNTS_PATH + "/" + account.getAccountId();

        final Multimap<String, String> queryParams = paramsWithAudit(createdBy, reason, comment);

        return httpClient.doPut(uri, account, queryParams, Account.class);
    }

    public AccountEmails getEmailsForAccount(final UUID accountId) throws KillBillClientException {
        final String uri = JaxrsResource.ACCOUNTS_PATH + "/" + accountId + "/" + JaxrsResource.EMAILS;

        return httpClient.doGet(uri, DEFAULT_EMPTY_QUERY, AccountEmails.class);
    }

    public void addEmailToAccount(final AccountEmail email, final String createdBy, final String reason, final String comment) throws KillBillClientException {
        Preconditions.checkNotNull(email.getAccountId(), "AccountEmail#accountId cannot be null");

        final String uri = JaxrsResource.ACCOUNTS_PATH + "/" + email.getAccountId() + "/" + JaxrsResource.EMAILS;

        final Multimap<String, String> queryParams = paramsWithAudit(createdBy, reason, comment);

        httpClient.doPost(uri, email, queryParams);
    }

    public void removeEmailFromAccount(final AccountEmail email, final String createdBy, final String reason, final String comment) throws KillBillClientException {
        Preconditions.checkNotNull(email.getAccountId(), "AccountEmail#accountId cannot be null");
        Preconditions.checkNotNull(email.getEmail(), "AccountEmail#email cannot be null");

        final String uri = JaxrsResource.ACCOUNTS_PATH + "/" + email.getAccountId() + "/" + JaxrsResource.EMAILS + "/" + UTF8UrlEncoder.encode(email.getEmail());

        final Multimap<String, String> queryParams = paramsWithAudit(createdBy, reason, comment);

        httpClient.doDelete(uri, queryParams);
    }

    public InvoiceEmail getEmailNotificationsForAccount(final UUID accountId) throws KillBillClientException {
        final String uri = JaxrsResource.ACCOUNTS_PATH + "/" + accountId + "/" + JaxrsResource.EMAIL_NOTIFICATIONS;

        return httpClient.doGet(uri, DEFAULT_EMPTY_QUERY, InvoiceEmail.class);
    }

    public void updateEmailNotificationsForAccount(final InvoiceEmail invoiceEmail, final String createdBy, final String reason, final String comment) throws KillBillClientException {
        Preconditions.checkNotNull(invoiceEmail.getAccountId(), "InvoiceEmail#accountId cannot be null");

        final String uri = JaxrsResource.ACCOUNTS_PATH + "/" + invoiceEmail.getAccountId() + "/" + JaxrsResource.EMAIL_NOTIFICATIONS;

        final Multimap<String, String> queryParams = paramsWithAudit(createdBy, reason, comment);

        httpClient.doPut(uri, invoiceEmail, queryParams);
    }

    // Bundles

    public Bundle getBundle(final UUID bundleId) throws KillBillClientException {
        final String uri = JaxrsResource.BUNDLES_PATH + "/" + bundleId;

        return httpClient.doGet(uri, DEFAULT_EMPTY_QUERY, Bundle.class);
    }

    public Bundle getBundle(final String externalKey) throws KillBillClientException {
        final String uri = JaxrsResource.BUNDLES_PATH;

        final Multimap<String, String> queryParams = ImmutableMultimap.<String, String>of(JaxrsResource.QUERY_EXTERNAL_KEY, externalKey);

        return httpClient.doGet(uri, queryParams, Bundle.class);
    }

    public Bundles getAccountBundles(final UUID accountId) throws KillBillClientException {
        final String uri = JaxrsResource.ACCOUNTS_PATH + "/" + accountId + "/" + JaxrsResource.BUNDLES;

        return httpClient.doGet(uri, DEFAULT_EMPTY_QUERY, Bundles.class);
    }

    public Bundles getAccountBundles(final UUID accountId, final String externalKey) throws KillBillClientException {
        final String uri = JaxrsResource.ACCOUNTS_PATH + "/" + accountId + "/" + JaxrsResource.BUNDLES;

        final Multimap<String, String> queryParams = ImmutableMultimap.<String, String>of(JaxrsResource.QUERY_EXTERNAL_KEY, externalKey);

        return httpClient.doGet(uri, queryParams, Bundles.class);
    }

    public Bundles getBundles() throws KillBillClientException {
        return getBundles(0L, 100L);
    }

    public Bundles getBundles(final Long offset, final Long limit) throws KillBillClientException {
        return getBundles(offset, limit, AuditLevel.NONE);
    }

    public Bundles getBundles(final Long offset, final Long limit, final AuditLevel auditLevel) throws KillBillClientException {
        final String uri = JaxrsResource.BUNDLES_PATH + "/" + JaxrsResource.PAGINATION;

        final Multimap<String, String> queryParams = ImmutableMultimap.<String, String>of(JaxrsResource.QUERY_SEARCH_OFFSET, String.valueOf(offset),
                                                                                          JaxrsResource.QUERY_SEARCH_LIMIT, String.valueOf(limit),
                                                                                          JaxrsResource.QUERY_AUDIT, auditLevel.toString());

        return httpClient.doGet(uri, queryParams, Bundles.class);
    }

    public Bundles searchBundles(final String key) throws KillBillClientException {
        return searchBundles(key, 0L, 100L);
    }

    public Bundles searchBundles(final String key, final Long offset, final Long limit) throws KillBillClientException {
        return searchBundles(key, offset, limit, AuditLevel.NONE);
    }

    public Bundles searchBundles(final String key, final Long offset, final Long limit, final AuditLevel auditLevel) throws KillBillClientException {
        final String uri = JaxrsResource.BUNDLES_PATH + "/" + JaxrsResource.SEARCH + "/" + UTF8UrlEncoder.encode(key);

        final Multimap<String, String> queryParams = ImmutableMultimap.<String, String>of(JaxrsResource.QUERY_SEARCH_OFFSET, String.valueOf(offset),
                                                                                          JaxrsResource.QUERY_SEARCH_LIMIT, String.valueOf(limit),
                                                                                          JaxrsResource.QUERY_AUDIT, auditLevel.toString());

        return httpClient.doGet(uri, DEFAULT_EMPTY_QUERY, Bundles.class);
    }

    public Bundle transferBundle(final Bundle bundle, final String createdBy, final String reason, final String comment) throws KillBillClientException {
        Preconditions.checkNotNull(bundle.getBundleId(), "Bundle#bundleId cannot be null");
        Preconditions.checkNotNull(bundle.getAccountId(), "Bundle#accountId cannot be null");

        final String uri = JaxrsResource.BUNDLES_PATH + "/" + bundle.getBundleId();

        final Multimap<String, String> queryParams = paramsWithAudit(createdBy, reason, comment);

        return httpClient.doPutAndFollowLocation(uri, bundle, queryParams, Bundle.class);

    }

    // Subscriptions and entitlements

    public Subscription getSubscription(final UUID subscriptionId) throws KillBillClientException {
        final String uri = JaxrsResource.SUBSCRIPTIONS_PATH + "/" + subscriptionId;

        return httpClient.doGet(uri, DEFAULT_EMPTY_QUERY, Subscription.class);
    }

    public Subscription createSubscription(final Subscription subscription, final String createdBy, final String reason, final String comment) throws KillBillClientException {
        return createSubscription(subscription, -1, createdBy, reason, comment);
    }

    public Subscription createSubscription(final Subscription subscription, final int timeoutSec, final String createdBy, final String reason, final String comment) throws KillBillClientException {
        return createSubscription(subscription, null, timeoutSec, createdBy, reason, comment);
    }

    public Subscription createSubscription(final Subscription subscription, final DateTime requestedDate, final int timeoutSec, final String createdBy, final String reason, final String comment) throws KillBillClientException {
        Preconditions.checkNotNull(subscription.getAccountId(), "Subscription#accountId cannot be null");
        Preconditions.checkNotNull(subscription.getExternalKey(), "Subscription#externalKey cannot be null");
        Preconditions.checkNotNull(subscription.getProductName(), "Subscription#productName cannot be null");
        Preconditions.checkNotNull(subscription.getProductCategory(), "Subscription#productCategory cannot be null");
        Preconditions.checkNotNull(subscription.getBillingPeriod(), "Subscription#billingPeriod cannot be null");
        Preconditions.checkNotNull(subscription.getPriceList(), "Subscription#priceList cannot be null");

        final Multimap<String, String> params = HashMultimap.<String, String>create();
        params.put(JaxrsResource.QUERY_CALL_COMPLETION, timeoutSec > 0 ? "true" : "false");
        params.put(JaxrsResource.QUERY_CALL_TIMEOUT, String.valueOf(timeoutSec));
        if (requestedDate != null) {
            params.put(JaxrsResource.QUERY_REQUESTED_DT, requestedDate.toDateTimeISO().toString());
        }
        final Multimap<String, String> queryParams = paramsWithAudit(params, createdBy, reason, comment);

        final int httpTimeout = Math.max(DEFAULT_HTTP_TIMEOUT_SEC, timeoutSec);

        return httpClient.doPostAndFollowLocation(JaxrsResource.SUBSCRIPTIONS_PATH, subscription, queryParams, httpTimeout, Subscription.class);
    }

    public Subscription updateSubscription(final Subscription subscription, final String createdBy, final String reason, final String comment) throws KillBillClientException {
        return updateSubscription(subscription, null, -1, createdBy, reason, comment);
    }

    public Subscription updateSubscription(final Subscription subscription, @Nullable final BillingActionPolicy billingPolicy, final int timeoutSec, final String createdBy, final String reason, final String comment) throws KillBillClientException {
        return updateSubscription(subscription, null, billingPolicy, -1, createdBy, reason, comment);
    }

    public Subscription updateSubscription(final Subscription subscription, @Nullable final DateTime requestedDate, @Nullable final BillingActionPolicy billingPolicy, final int timeoutSec, final String createdBy, final String reason, final String comment) throws KillBillClientException {
        Preconditions.checkNotNull(subscription.getSubscriptionId(), "Subscription#subscriptionId cannot be null");
        Preconditions.checkNotNull(subscription.getProductName(), "Subscription#productName cannot be null");
        Preconditions.checkNotNull(subscription.getBillingPeriod(), "Subscription#billingPeriod cannot be null");
        Preconditions.checkNotNull(subscription.getPriceList(), "Subscription#priceList cannot be null");

        final String uri = JaxrsResource.SUBSCRIPTIONS_PATH + "/" + subscription.getSubscriptionId();

        final Multimap<String, String> params = HashMultimap.<String, String>create();
        params.put(JaxrsResource.QUERY_CALL_COMPLETION, timeoutSec > 0 ? "true" : "false");
        params.put(JaxrsResource.QUERY_CALL_TIMEOUT, String.valueOf(timeoutSec));
        if (requestedDate != null) {
            params.put(JaxrsResource.QUERY_REQUESTED_DT, requestedDate.toDateTimeISO().toString());
        }
        if (billingPolicy != null) {
            params.put(JaxrsResource.QUERY_BILLING_POLICY, billingPolicy.toString());
        }
        final Multimap<String, String> queryParams = paramsWithAudit(params, createdBy, reason, comment);

        return httpClient.doPut(uri, subscription, queryParams, Subscription.class);
    }

    public void cancelSubscription(final UUID subscriptionId, final String createdBy, final String reason, final String comment) throws KillBillClientException {
        cancelSubscription(subscriptionId, -1, createdBy, reason, comment);
    }

    public void cancelSubscription(final UUID subscriptionId, final int timeoutSec, final String createdBy, final String reason, final String comment) throws KillBillClientException {
        cancelSubscription(subscriptionId, null, null, timeoutSec, createdBy, reason, comment);
    }

    public void cancelSubscription(final UUID subscriptionId, @Nullable final EntitlementActionPolicy entitlementPolicy, @Nullable final BillingActionPolicy billingPolicy,
                                   final int timeoutSec, final String createdBy, final String reason, final String comment) throws KillBillClientException {
        cancelSubscription(subscriptionId, null, entitlementPolicy, billingPolicy, timeoutSec, createdBy, reason, comment);
    }

    public void cancelSubscription(final UUID subscriptionId, @Nullable final DateTime requestedDate, @Nullable final EntitlementActionPolicy entitlementPolicy, @Nullable final BillingActionPolicy billingPolicy,
                                   final int timeoutSec, final String createdBy, final String reason, final String comment) throws KillBillClientException {
        final String uri = JaxrsResource.SUBSCRIPTIONS_PATH + "/" + subscriptionId;

        final Multimap<String, String> params = HashMultimap.<String, String>create();
        params.put(JaxrsResource.QUERY_CALL_COMPLETION, timeoutSec > 0 ? "true" : "false");
        params.put(JaxrsResource.QUERY_CALL_TIMEOUT, String.valueOf(timeoutSec));
        if (requestedDate != null) {
            params.put(JaxrsResource.QUERY_REQUESTED_DT, requestedDate.toDateTimeISO().toString());
        }
        if (entitlementPolicy != null) {
            params.put(JaxrsResource.QUERY_ENTITLEMENT_POLICY, entitlementPolicy.toString());
        }
        if (billingPolicy != null) {
            params.put(JaxrsResource.QUERY_BILLING_POLICY, billingPolicy.toString());
        }
        final Multimap<String, String> queryParams = paramsWithAudit(params, createdBy, reason, comment);

        httpClient.doDelete(uri, queryParams);
    }

    public void uncancelSubscription(final UUID subscriptionId, final String createdBy, final String reason, final String comment) throws KillBillClientException {
        final String uri = JaxrsResource.SUBSCRIPTIONS_PATH + "/" + subscriptionId + "/uncancel";

        final Multimap<String, String> queryParams = paramsWithAudit(createdBy, reason, comment);

        httpClient.doPut(uri, null, queryParams);
    }

    // Invoices

    public Invoices getInvoices() throws KillBillClientException {
        return getInvoices(0L, 100L);
    }

    public Invoices getInvoices(final Long offset, final Long limit) throws KillBillClientException {
        return getInvoices(true, offset, limit, AuditLevel.NONE);
    }

    public Invoices getInvoices(final Long offset, final Long limit, final AuditLevel auditLevel) throws KillBillClientException {
        return getInvoices(true, offset, limit, auditLevel);
    }

    public Invoices getInvoices(final boolean withItems, final Long offset, final Long limit, final AuditLevel auditLevel) throws KillBillClientException {
        final String uri = JaxrsResource.INVOICES_PATH + "/" + JaxrsResource.PAGINATION;

        final Multimap<String, String> queryParams = ImmutableMultimap.<String, String>of(JaxrsResource.QUERY_SEARCH_OFFSET, String.valueOf(offset),
                                                                                          JaxrsResource.QUERY_SEARCH_LIMIT, String.valueOf(limit),
                                                                                          JaxrsResource.QUERY_INVOICE_WITH_ITEMS, String.valueOf(withItems),
                                                                                          JaxrsResource.QUERY_AUDIT, auditLevel.toString());

        return httpClient.doGet(uri, queryParams, Invoices.class);
    }

    public Invoice getInvoice(final UUID invoiceId) throws KillBillClientException {
        return getInvoice(invoiceId, true);
    }

    public Invoice getInvoice(final UUID invoiceId, final boolean withItems) throws KillBillClientException {
        return getInvoice(invoiceId, withItems, AuditLevel.NONE);
    }

    public Invoice getInvoice(final UUID invoiceId, final boolean withItems, final AuditLevel auditLevel) throws KillBillClientException {
        return getInvoiceByIdOrNumber(invoiceId.toString(), withItems, auditLevel);
    }

    public Invoice getInvoice(final Integer invoiceNumber) throws KillBillClientException {
        return getInvoice(invoiceNumber, true);
    }

    public Invoice getInvoice(final Integer invoiceNumber, final boolean withItems) throws KillBillClientException {
        return getInvoice(invoiceNumber, withItems, AuditLevel.NONE);
    }

    public Invoice getInvoice(final Integer invoiceNumber, final boolean withItems, final AuditLevel auditLevel) throws KillBillClientException {
        return getInvoiceByIdOrNumber(invoiceNumber.toString(), withItems, auditLevel);
    }

    public Invoice getInvoiceByIdOrNumber(final String invoiceIdOrNumber, final boolean withItems, final AuditLevel auditLevel) throws KillBillClientException {
        final String uri = JaxrsResource.INVOICES_PATH + "/" + invoiceIdOrNumber;

        final Multimap<String, String> queryParams = ImmutableMultimap.<String, String>of(JaxrsResource.QUERY_INVOICE_WITH_ITEMS, String.valueOf(withItems),
                                                                                          JaxrsResource.QUERY_AUDIT, auditLevel.toString());

        return httpClient.doGet(uri, queryParams, Invoice.class);
    }

    public Invoices getInvoicesForAccount(final UUID accountId) throws KillBillClientException {
        return getInvoicesForAccount(accountId, true);
    }

    public Invoices getInvoicesForAccount(final UUID accountId, final boolean withItems) throws KillBillClientException {
        return getInvoicesForAccount(accountId, withItems, false, AuditLevel.NONE);
    }

    public Invoices getInvoicesForAccount(final UUID accountId, final boolean withItems, final boolean unpaidOnly) throws KillBillClientException {
        return getInvoicesForAccount(accountId, withItems, unpaidOnly, AuditLevel.NONE);
    }

    public Invoices getInvoicesForAccount(final UUID accountId, final boolean withItems, final boolean unpaidOnly, final AuditLevel auditLevel) throws KillBillClientException {
        final String uri = JaxrsResource.ACCOUNTS_PATH + "/" + accountId + "/" + JaxrsResource.INVOICES;

        final Multimap<String, String> queryParams = ImmutableMultimap.<String, String>of(JaxrsResource.QUERY_INVOICE_WITH_ITEMS, String.valueOf(withItems),
                                                                                          JaxrsResource.QUERY_UNPAID_INVOICES_ONLY, String.valueOf(unpaidOnly),
                                                                                          JaxrsResource.QUERY_AUDIT, auditLevel.toString());

        return httpClient.doGet(uri, queryParams, Invoices.class);
    }

    public Invoices searchInvoices(final String key) throws KillBillClientException {
        return searchInvoices(key, 0L, 100L);
    }

    public Invoices searchInvoices(final String key, final Long offset, final Long limit) throws KillBillClientException {
        return searchInvoices(key, offset, limit, AuditLevel.NONE);
    }

    public Invoices searchInvoices(final String key, final Long offset, final Long limit, final AuditLevel auditLevel) throws KillBillClientException {
        final String uri = JaxrsResource.INVOICES_PATH + "/" + JaxrsResource.SEARCH + "/" + UTF8UrlEncoder.encode(key);

        final Multimap<String, String> queryParams = ImmutableMultimap.<String, String>of(JaxrsResource.QUERY_SEARCH_OFFSET, String.valueOf(offset),
                                                                                          JaxrsResource.QUERY_SEARCH_LIMIT, String.valueOf(limit),
                                                                                          JaxrsResource.QUERY_AUDIT, auditLevel.toString());

        return httpClient.doGet(uri, queryParams, Invoices.class);
    }

    public Invoice createDryRunInvoice(final UUID accountId, final DateTime futureDate, final String createdBy, final String reason, final String comment) throws KillBillClientException {
        final String uri = JaxrsResource.INVOICES_PATH;

        final Multimap<String, String> queryParams = paramsWithAudit(ImmutableMultimap.<String, String>of(JaxrsResource.QUERY_ACCOUNT_ID, accountId.toString(),
                                                                                                          JaxrsResource.QUERY_TARGET_DATE, futureDate.toString(),
                                                                                                          JaxrsResource.QUERY_DRY_RUN, "true"),
                                                                     createdBy,
                                                                     reason,
                                                                     comment
                                                                    );

        return httpClient.doPost(uri, null, queryParams, Invoice.class);
    }

    public Invoice createInvoice(final UUID accountId, final DateTime futureDate, final String createdBy, final String reason, final String comment) throws KillBillClientException {
        final String uri = JaxrsResource.INVOICES_PATH;

        final Multimap<String, String> queryParams = paramsWithAudit(ImmutableMultimap.<String, String>of(JaxrsResource.QUERY_ACCOUNT_ID, accountId.toString(),
                                                                                                          JaxrsResource.QUERY_TARGET_DATE, futureDate.toString()),
                                                                     createdBy,
                                                                     reason,
                                                                     comment
                                                                    );

        return httpClient.doPostAndFollowLocation(uri, null, queryParams, Invoice.class);
    }

    public Invoice adjustInvoiceItem(final InvoiceItem invoiceItem, final String createdBy, final String reason, final String comment) throws KillBillClientException {
        return adjustInvoiceItem(invoiceItem, new DateTime(DateTimeZone.UTC), createdBy, reason, comment);
    }

    public Invoice adjustInvoiceItem(final InvoiceItem invoiceItem, final DateTime requestedDate, final String createdBy, final String reason, final String comment) throws KillBillClientException {
        Preconditions.checkNotNull(invoiceItem.getAccountId(), "InvoiceItem#accountId cannot be null");
        Preconditions.checkNotNull(invoiceItem.getInvoiceId(), "InvoiceItem#invoiceId cannot be null");
        Preconditions.checkNotNull(invoiceItem.getInvoiceItemId(), "InvoiceItem#invoiceItemId cannot be null");

        final String uri = JaxrsResource.INVOICES_PATH + "/" + invoiceItem.getInvoiceId();

        final Multimap<String, String> queryParams = paramsWithAudit(ImmutableMultimap.<String, String>of(JaxrsResource.QUERY_REQUESTED_DT, requestedDate.toDateTimeISO().toString()),
                                                                     createdBy,
                                                                     reason,
                                                                     comment);

        return httpClient.doPostAndFollowLocation(uri, invoiceItem, queryParams, Invoice.class);
    }

    public InvoiceItem createExternalCharge(final InvoiceItem externalCharge, final DateTime requestedDate, final Boolean autoPay, final String createdBy, final String reason, final String comment) throws KillBillClientException {
        final List<InvoiceItem> externalCharges = createExternalCharges(ImmutableList.<InvoiceItem>of(externalCharge), requestedDate, autoPay, createdBy, reason, comment);
        return externalCharges.isEmpty() ? null : externalCharges.get(0);
    }

    public List<InvoiceItem> createExternalCharges(final Iterable<InvoiceItem> externalCharges, final DateTime requestedDate, final Boolean autoPay, final String createdBy, final String reason, final String comment) throws KillBillClientException {
        final Map<UUID, Collection<InvoiceItem>> externalChargesPerAccount = new HashMap<UUID, Collection<InvoiceItem>>();
        for (final InvoiceItem externalCharge : externalCharges) {
            Preconditions.checkNotNull(externalCharge.getAccountId(), "InvoiceItem#accountId cannot be null");
            Preconditions.checkNotNull(externalCharge.getAmount(), "InvoiceItem#amount cannot be null");
            Preconditions.checkNotNull(externalCharge.getCurrency(), "InvoiceItem#currency cannot be null");

            if (externalChargesPerAccount.get(externalCharge.getAccountId()) == null) {
                externalChargesPerAccount.put(externalCharge.getAccountId(), new LinkedList<InvoiceItem>());
            }
            externalChargesPerAccount.get(externalCharge.getAccountId()).add(externalCharge);
        }

        final List<InvoiceItem> createdExternalCharges = new LinkedList<InvoiceItem>();
        for (final UUID accountId : externalChargesPerAccount.keySet()) {
            final List<InvoiceItem> invoiceItems = createExternalCharges(accountId, externalChargesPerAccount.get(accountId), requestedDate, autoPay, createdBy, reason, comment);
            createdExternalCharges.addAll(invoiceItems);
        }

        return createdExternalCharges;
    }

    private List<InvoiceItem> createExternalCharges(final UUID accountId, final Iterable<InvoiceItem> externalCharges, final DateTime requestedDate, final Boolean autoPay, final String createdBy, final String reason, final String comment) throws KillBillClientException {
        final String uri = JaxrsResource.INVOICES_PATH + "/" + JaxrsResource.CHARGES + "/" + accountId;

        final Multimap<String, String> queryParams = paramsWithAudit(ImmutableMultimap.<String, String>of(JaxrsResource.QUERY_REQUESTED_DT, requestedDate.toDateTimeISO().toString(),
                                                                                                          JaxrsResource.QUERY_PAY_INVOICE, autoPay.toString()),
                                                                     createdBy,
                                                                     reason,
                                                                     comment
                                                                    );

        return httpClient.doPost(uri, externalCharges, queryParams, InvoiceItems.class);
    }

    public void triggerInvoiceNotification(final UUID invoiceId, final String createdBy, final String reason, final String comment) throws KillBillClientException {
        final String uri = JaxrsResource.INVOICES_PATH + "/" + invoiceId.toString() + "/" + JaxrsResource.EMAIL_NOTIFICATIONS;

        final Multimap<String, String> queryParams = paramsWithAudit(createdBy, reason, comment);

        httpClient.doPost(uri, null, queryParams);
    }

    // Credits

    public Credit getCredit(final UUID creditId) throws KillBillClientException {
        return getCredit(creditId, AuditLevel.NONE);
    }

    public Credit getCredit(final UUID creditId, final AuditLevel auditLevel) throws KillBillClientException {
        final String uri = JaxrsResource.CREDITS_PATH + "/" + creditId;

        final Multimap<String, String> queryParams = ImmutableMultimap.<String, String>of(JaxrsResource.QUERY_AUDIT, auditLevel.toString());

        return httpClient.doGet(uri, queryParams, Credit.class);
    }

    public Credit createCredit(final Credit credit, final String createdBy, final String reason, final String comment) throws KillBillClientException {
        Preconditions.checkNotNull(credit.getAccountId(), "Credt#accountId cannot be null");
        Preconditions.checkNotNull(credit.getCreditAmount(), "Credt#creditAmount cannot be null");

        final Multimap<String, String> queryParams = paramsWithAudit(createdBy, reason, comment);

        return httpClient.doPostAndFollowLocation(JaxrsResource.CREDITS_PATH, credit, queryParams, Credit.class);
    }

    // Payments

    public Payments getPayments() throws KillBillClientException {
        return getPayments(0L, 100L);
    }

    public Payments getPayments(final Long offset, final Long limit) throws KillBillClientException {
        return getPayments(offset, limit, AuditLevel.NONE);
    }

    public Payments getPayments(final Long offset, final Long limit, final AuditLevel auditLevel) throws KillBillClientException {
        final String uri = JaxrsResource.PAYMENTS_PATH + "/" + JaxrsResource.PAGINATION;

        final Multimap<String, String> queryParams = ImmutableMultimap.<String, String>of(JaxrsResource.QUERY_SEARCH_OFFSET, String.valueOf(offset),
                                                                                          JaxrsResource.QUERY_SEARCH_LIMIT, String.valueOf(limit),
                                                                                          JaxrsResource.QUERY_AUDIT, auditLevel.toString());

        return httpClient.doGet(uri, queryParams, Payments.class);
    }

    public Payments searchPayments(final String key) throws KillBillClientException {
        return searchPayments(key, 0L, 100L);
    }

    public Payments searchPayments(final String key, final Long offset, final Long limit) throws KillBillClientException {
        return searchPayments(key, offset, limit, AuditLevel.NONE);
    }

    public Payments searchPayments(final String key, final Long offset, final Long limit, final AuditLevel auditLevel) throws KillBillClientException {
        final String uri = JaxrsResource.PAYMENTS_PATH + "/" + JaxrsResource.SEARCH + "/" + UTF8UrlEncoder.encode(key);

        final Multimap<String, String> queryParams = ImmutableMultimap.<String, String>of(JaxrsResource.QUERY_SEARCH_OFFSET, String.valueOf(offset),
                                                                                          JaxrsResource.QUERY_SEARCH_LIMIT, String.valueOf(limit),
                                                                                          JaxrsResource.QUERY_AUDIT, auditLevel.toString());

        return httpClient.doGet(uri, queryParams, Payments.class);
    }

    public Payment getPayment(final UUID paymentId) throws KillBillClientException {
        return getPayment(paymentId, true);
    }

    public Payment getPayment(final UUID paymentId, final boolean withRefundsAndChargebacks) throws KillBillClientException {
        return getPayment(paymentId, withRefundsAndChargebacks, AuditLevel.NONE);
    }

    public Payment getPayment(final UUID paymentId, final boolean withRefundsAndChargebacks, final AuditLevel auditLevel) throws KillBillClientException {
        final String uri = JaxrsResource.PAYMENTS_PATH + "/" + paymentId;

        final Multimap<String, String> queryParams = ImmutableMultimap.<String, String>of(JaxrsResource.QUERY_PAYMENT_WITH_REFUNDS_AND_CHARGEBACKS, String.valueOf(withRefundsAndChargebacks),
                                                                                          JaxrsResource.QUERY_AUDIT, auditLevel.toString());

        return httpClient.doGet(uri, queryParams, Payment.class);
    }

    public Payments getPaymentsForAccount(final UUID accountId) throws KillBillClientException {
        final String uri = JaxrsResource.ACCOUNTS_PATH + "/" + accountId + "/" + JaxrsResource.PAYMENTS;

        return httpClient.doGet(uri, DEFAULT_EMPTY_QUERY, Payments.class);
    }

    public Payments getPaymentsForInvoice(final UUID invoiceId) throws KillBillClientException {
        final String uri = JaxrsResource.INVOICES_PATH + "/" + invoiceId + "/" + JaxrsResource.PAYMENTS;

        return httpClient.doGet(uri, DEFAULT_EMPTY_QUERY, Payments.class);
    }

    public void payAllInvoices(final UUID accountId, final boolean externalPayment, final BigDecimal paymentAmount, final String createdBy, final String reason, final String comment) throws KillBillClientException {
        final String uri = JaxrsResource.ACCOUNTS_PATH + "/" + accountId + "/" + JaxrsResource.PAYMENTS;

        final Multimap<String, String> params = HashMultimap.<String, String>create();
        params.put(JaxrsResource.QUERY_PAYMENT_EXTERNAL, String.valueOf(externalPayment));
        if (paymentAmount != null) {
            params.put("paymentAmount", String.valueOf(paymentAmount));
        }

        final Multimap<String, String> queryParams = paramsWithAudit(params,
                                                                     createdBy,
                                                                     reason,
                                                                     comment
                                                                    );

        httpClient.doPost(uri, null, queryParams);
    }

    public Payments createPayment(final Payment payment, final boolean isExternal, final String createdBy, final String reason, final String comment) throws KillBillClientException {
        Preconditions.checkNotNull(payment.getAccountId(), "InvoiceItem#accountId cannot be null");
        Preconditions.checkNotNull(payment.getInvoiceId(), "InvoiceItem#invoiceId cannot be null");
        Preconditions.checkNotNull(payment.getAmount(), "InvoiceItem#amount cannot be null");

        final String uri = JaxrsResource.INVOICES_PATH + "/" + payment.getInvoiceId() + "/" + JaxrsResource.PAYMENTS;

        final Multimap<String, String> queryParams = paramsWithAudit(ImmutableMultimap.<String, String>of("externalPayment", String.valueOf(isExternal)),
                                                                     createdBy,
                                                                     reason,
                                                                     comment);

        return httpClient.doPostAndFollowLocation(uri, payment, queryParams, Payments.class);
    }

    // Direct Payments

    public DirectPayments getDirectPayments() throws KillBillClientException {
        return getDirectPayments(0L, 100L);
    }

    public DirectPayments getDirectPayments(final Long offset, final Long limit) throws KillBillClientException {
        return getDirectPayments(offset, limit, AuditLevel.NONE);
    }

    public DirectPayments getDirectPayments(final Long offset, final Long limit, final AuditLevel auditLevel) throws KillBillClientException {
        return getDirectPayments(offset, limit, null, ImmutableMap.<String, String>of(), auditLevel);
    }

    public DirectPayments getDirectPayments(final Long offset, final Long limit, @Nullable final String pluginName, final Map<String, String> pluginProperties, final AuditLevel auditLevel) throws KillBillClientException {
        final String uri = JaxrsResource.DIRECT_PAYMENTS_PATH + "/" + JaxrsResource.PAGINATION;

        final Multimap<String, String> queryParams = HashMultimap.<String, String>create();
        if (pluginName != null) {
            queryParams.put(JaxrsResource.QUERY_PAYMENT_PLUGIN_NAME, pluginName);
        }
        queryParams.put(JaxrsResource.QUERY_SEARCH_OFFSET, String.valueOf(offset));
        queryParams.put(JaxrsResource.QUERY_SEARCH_LIMIT, String.valueOf(limit));
        queryParams.put(JaxrsResource.QUERY_AUDIT, auditLevel.toString());
        storePluginPropertiesAsParams(pluginProperties, queryParams);

        return httpClient.doGet(uri, queryParams, DirectPayments.class);
    }

    public DirectPayments searchDirectPayments(final String key) throws KillBillClientException {
        return searchDirectPayments(key, 0L, 100L);
    }

    public DirectPayments searchDirectPayments(final String key, final Long offset, final Long limit) throws KillBillClientException {
        return searchDirectPayments(key, offset, limit, AuditLevel.NONE);
    }

    public DirectPayments searchDirectPayments(final String key, final Long offset, final Long limit, final AuditLevel auditLevel) throws KillBillClientException {
        return searchDirectPayments(key, offset, limit, null, ImmutableMap.<String, String>of(), auditLevel);
    }

    public DirectPayments searchDirectPayments(final String key, final Long offset, final Long limit, @Nullable final String pluginName, final Map<String, String> pluginProperties, final AuditLevel auditLevel) throws KillBillClientException {
        final String uri = JaxrsResource.DIRECT_PAYMENTS_PATH + "/" + JaxrsResource.SEARCH + "/" + UTF8UrlEncoder.encode(key);

        final Multimap<String, String> queryParams = HashMultimap.<String, String>create();
        if (pluginName != null) {
            queryParams.put(JaxrsResource.QUERY_PAYMENT_PLUGIN_NAME, pluginName);
        }
        queryParams.put(JaxrsResource.QUERY_SEARCH_OFFSET, String.valueOf(offset));
        queryParams.put(JaxrsResource.QUERY_SEARCH_LIMIT, String.valueOf(limit));
        queryParams.put(JaxrsResource.QUERY_AUDIT, auditLevel.toString());
        storePluginPropertiesAsParams(pluginProperties, queryParams);

        return httpClient.doGet(uri, queryParams, DirectPayments.class);
    }

    public DirectPayment getDirectPayment(final UUID directPaymentId) throws KillBillClientException {
        return getDirectPayment(directPaymentId, true);
    }

    public DirectPayment getDirectPayment(final UUID directPaymentId, final boolean withPluginInfo) throws KillBillClientException {
        return getDirectPayment(directPaymentId, withPluginInfo, AuditLevel.NONE);
    }

    public DirectPayment getDirectPayment(final UUID directPaymentId, final boolean withPluginInfo, final AuditLevel auditLevel) throws KillBillClientException {
        return getDirectPayment(directPaymentId, withPluginInfo, ImmutableMap.<String, String>of(), auditLevel);
    }

    public DirectPayment getDirectPayment(final UUID directPaymentId, final boolean withPluginInfo, final Map<String, String> pluginProperties, final AuditLevel auditLevel) throws KillBillClientException {
        final String uri = JaxrsResource.DIRECT_PAYMENTS_PATH + "/" + directPaymentId;

        final Multimap<String, String> queryParams = HashMultimap.<String, String>create();
        queryParams.put(JaxrsResource.QUERY_PAYMENT_METHOD_PLUGIN_INFO, String.valueOf(withPluginInfo));
        queryParams.put(JaxrsResource.QUERY_AUDIT, auditLevel.toString());
        storePluginPropertiesAsParams(pluginProperties, queryParams);

        return httpClient.doGet(uri, queryParams, DirectPayment.class);
    }

    public DirectPayments getDirectPaymentsForAccount(final UUID accountId) throws KillBillClientException {
        return getDirectPaymentsForAccount(accountId, AuditLevel.NONE);
    }

    public DirectPayments getDirectPaymentsForAccount(final UUID accountId, final AuditLevel auditLevel) throws KillBillClientException {
        final String uri = JaxrsResource.ACCOUNTS_PATH + "/" + accountId + "/" + JaxrsResource.DIRECT_PAYMENTS;

        final Multimap<String, String> queryParams = ImmutableMultimap.<String, String>of(JaxrsResource.QUERY_AUDIT, auditLevel.toString());

        return httpClient.doGet(uri, queryParams, DirectPayments.class);
    }

    public DirectPayment createDirectPayment(final UUID accountId, final DirectTransaction transaction, final String createdBy, final String reason, final String comment) throws KillBillClientException {
        return createDirectPayment(accountId, transaction, ImmutableMap.<String, String>of(), createdBy, reason, comment);
    }

    public DirectPayment createDirectPayment(final UUID accountId, final DirectTransaction transaction, final Map<String, String> pluginProperties, final String createdBy, final String reason, final String comment) throws KillBillClientException {
        return createDirectPayment(accountId, null, transaction, pluginProperties, createdBy, reason, comment);
    }

    public DirectPayment createDirectPayment(final UUID accountId, @Nullable final UUID paymentMethodId, final DirectTransaction transaction, final String createdBy, final String reason, final String comment) throws KillBillClientException {
        return createDirectPayment(accountId, paymentMethodId, transaction, ImmutableMap.<String, String>of(), createdBy, reason, comment);
    }

    public DirectPayment createDirectPayment(final UUID accountId, @Nullable final UUID paymentMethodId, final DirectTransaction transaction, final Map<String, String> pluginProperties, final String createdBy, final String reason, final String comment) throws KillBillClientException {
        Preconditions.checkNotNull(accountId, "accountId cannot be null");
        Preconditions.checkNotNull(transaction.getTransactionType(), "DirectTransaction#transactionId cannot be null");
        Preconditions.checkArgument("AUTHORIZE".equals(transaction.getTransactionType()) ||
                                    "CREDIT".equals(transaction.getTransactionType()) ||
                                    "PURCHASE".equals(transaction.getTransactionType()),
                                    "Invalid transaction type " + transaction.getTransactionType()
                                   );
        Preconditions.checkNotNull(transaction.getAmount(), "DirectTransaction#amount cannot be null");
        Preconditions.checkNotNull(transaction.getCurrency(), "DirectTransaction#currency cannot be null");

        final String uri = JaxrsResource.ACCOUNTS_PATH + "/" + accountId + "/" + JaxrsResource.DIRECT_PAYMENTS;

        final Multimap<String, String> params = HashMultimap.<String, String>create();
        if (paymentMethodId != null) {
            params.put("paymentMethodId", paymentMethodId.toString());
        }
        storePluginPropertiesAsParams(pluginProperties, params);

        final Multimap<String, String> queryParams = paramsWithAudit(params,
                                                                     createdBy,
                                                                     reason,
                                                                     comment);

        return httpClient.doPostAndFollowLocation(uri, transaction, queryParams, DirectPayment.class);
    }

    public DirectPayment captureAuthorization(final DirectTransaction transaction, final String createdBy, final String reason, final String comment) throws KillBillClientException {
        return captureAuthorization(transaction, ImmutableMap.<String, String>of(), createdBy, reason, comment);
    }

    public DirectPayment captureAuthorization(final DirectTransaction transaction, final Map<String, String> pluginProperties, final String createdBy, final String reason, final String comment) throws KillBillClientException {
        Preconditions.checkNotNull(transaction.getDirectPaymentId(), "DirectTransaction#directPaymentId cannot be null");
        Preconditions.checkNotNull(transaction.getAmount(), "DirectTransaction#amount cannot be null");

        final String uri = JaxrsResource.DIRECT_PAYMENTS_PATH + "/" + transaction.getDirectPaymentId();

        final Multimap<String, String> params = HashMultimap.<String, String>create();
        storePluginPropertiesAsParams(pluginProperties, params);

        final Multimap<String, String> queryParams = paramsWithAudit(params,
                                                                     createdBy,
                                                                     reason,
                                                                     comment);

        return httpClient.doPostAndFollowLocation(uri, transaction, queryParams, DirectPayment.class);
    }

    public DirectPayment refundPayment(final DirectTransaction transaction, final String createdBy, final String reason, final String comment) throws KillBillClientException {
        return refundPayment(transaction, ImmutableMap.<String, String>of(), createdBy, reason, comment);
    }

    public DirectPayment refundPayment(final DirectTransaction transaction, final Map<String, String> pluginProperties, final String createdBy, final String reason, final String comment) throws KillBillClientException {
        Preconditions.checkNotNull(transaction.getDirectPaymentId(), "DirectTransaction#directPaymentId cannot be null");
        Preconditions.checkNotNull(transaction.getAmount(), "DirectTransaction#amount cannot be null");

        final String uri = JaxrsResource.DIRECT_PAYMENTS_PATH + "/" + transaction.getDirectPaymentId() + "/" + JaxrsResource.REFUNDS;

        final Multimap<String, String> params = HashMultimap.<String, String>create();
        storePluginPropertiesAsParams(pluginProperties, params);

        final Multimap<String, String> queryParams = paramsWithAudit(params,
                                                                     createdBy,
                                                                     reason,
                                                                     comment);

        return httpClient.doPostAndFollowLocation(uri, transaction, queryParams, DirectPayment.class);
    }

    public DirectPayment voidPayment(final UUID directPaymentId, final String directTransactionKey, final String createdBy, final String reason, final String comment) throws KillBillClientException {
        return voidPayment(directPaymentId, directTransactionKey, ImmutableMap.<String, String>of(), createdBy, reason, comment);
    }

    public DirectPayment voidPayment(final UUID directPaymentId, final String directTransactionExternalKey, final Map<String, String> pluginProperties, final String createdBy, final String reason, final String comment) throws KillBillClientException {
        final String uri = JaxrsResource.DIRECT_PAYMENTS_PATH + "/" + directPaymentId;

        final DirectTransaction transaction = new DirectTransaction();
        transaction.setDirectTransactionExternalKey(directTransactionExternalKey);

        final Multimap<String, String> params = HashMultimap.<String, String>create();
        storePluginPropertiesAsParams(pluginProperties, params);

        final Multimap<String, String> queryParams = paramsWithAudit(params,
                                                                     createdBy,
                                                                     reason,
                                                                     comment);

        return httpClient.doDeleteAndFollowLocation(uri, transaction, queryParams, DirectPayment.class);
    }

    // Hosted payment pages

    public HostedPaymentPageFormDescriptor buildFormDescriptor(final HostedPaymentPageFields fields, final UUID kbAccountId, final Map<String, String> pluginProperties, final String createdBy, final String reason, final String comment) throws KillBillClientException {
        final String uri = JaxrsResource.PAYMENT_GATEWAYS_PATH + "/" + JaxrsResource.HOSTED + "/" + JaxrsResource.FORM + "/" + kbAccountId;

        final Multimap<String, String> params = HashMultimap.<String, String>create();
        storePluginPropertiesAsParams(pluginProperties, params);

        final Multimap<String, String> queryParams = paramsWithAudit(params,
                                                                     createdBy,
                                                                     reason,
                                                                     comment);

        return httpClient.doPost(uri, fields, queryParams, HostedPaymentPageFormDescriptor.class);
    }

    public Response processNotification(final String notification, final String pluginName, final Map<String, String> pluginProperties, final String createdBy, final String reason, final String comment) throws KillBillClientException {
        final String uri = JaxrsResource.PAYMENT_GATEWAYS_PATH + "/" + JaxrsResource.NOTIFICATION + "/" + pluginName;

        final Multimap<String, String> params = HashMultimap.<String, String>create();
        storePluginPropertiesAsParams(pluginProperties, params);

        final Multimap<String, String> queryParams = paramsWithAudit(params,
                                                                     createdBy,
                                                                     reason,
                                                                     comment);

        return httpClient.doPost(uri, notification, queryParams);
    }

    // Refunds

    public Refunds getRefunds() throws KillBillClientException {
        return getRefunds(0L, 100L);
    }

    public Refunds getRefunds(final Long offset, final Long limit) throws KillBillClientException {
        return getRefunds(offset, limit, AuditLevel.NONE);
    }

    public Refunds getRefunds(final Long offset, final Long limit, final AuditLevel auditLevel) throws KillBillClientException {
        final String uri = JaxrsResource.REFUNDS_PATH + "/" + JaxrsResource.PAGINATION;

        final Multimap<String, String> queryParams = ImmutableMultimap.<String, String>of(JaxrsResource.QUERY_SEARCH_OFFSET, String.valueOf(offset),
                                                                                          JaxrsResource.QUERY_SEARCH_LIMIT, String.valueOf(limit),
                                                                                          JaxrsResource.QUERY_AUDIT, auditLevel.toString());

        return httpClient.doGet(uri, queryParams, Refunds.class);
    }

    public Refunds searchRefunds(final String key) throws KillBillClientException {
        return searchRefunds(key, 0L, 100L);
    }

    public Refunds searchRefunds(final String key, final Long offset, final Long limit) throws KillBillClientException {
        return searchRefunds(key, offset, limit, AuditLevel.NONE);
    }

    public Refunds searchRefunds(final String key, final Long offset, final Long limit, final AuditLevel auditLevel) throws KillBillClientException {
        final String uri = JaxrsResource.REFUNDS_PATH + "/" + JaxrsResource.SEARCH + "/" + UTF8UrlEncoder.encode(key);

        final Multimap<String, String> queryParams = ImmutableMultimap.<String, String>of(JaxrsResource.QUERY_SEARCH_OFFSET, String.valueOf(offset),
                                                                                          JaxrsResource.QUERY_SEARCH_LIMIT, String.valueOf(limit),
                                                                                          JaxrsResource.QUERY_AUDIT, auditLevel.toString());

        return httpClient.doGet(uri, DEFAULT_EMPTY_QUERY, Refunds.class);
    }

    public Refunds getRefundsForAccount(final UUID accountId) throws KillBillClientException {
        final String uri = JaxrsResource.ACCOUNTS_PATH + "/" + accountId + "/" + JaxrsResource.REFUNDS;

        return httpClient.doGet(uri, DEFAULT_EMPTY_QUERY, Refunds.class);
    }

    public Refunds getRefundsForPayment(final UUID paymentId) throws KillBillClientException {
        final String uri = JaxrsResource.PAYMENTS_PATH + "/" + paymentId + "/" + JaxrsResource.REFUNDS;

        return httpClient.doGet(uri, DEFAULT_EMPTY_QUERY, Refunds.class);
    }

    public Refund createRefund(final Refund refund, final String createdBy, final String reason, final String comment) throws KillBillClientException {
        Preconditions.checkNotNull(refund.getPaymentId(), "Refund#paymentId cannot be null");

        // Specify isAdjusted for invoice adjustment and invoice item adjustment
        // Specify adjustments for invoice item adjustments only
        if (refund.getAdjustments() != null) {
            for (final InvoiceItem invoiceItem : refund.getAdjustments()) {
                Preconditions.checkNotNull(invoiceItem.getInvoiceItemId(), "InvoiceItem#invoiceItemId cannot be null");
            }
        }

        final String uri = JaxrsResource.PAYMENTS_PATH + "/" + refund.getPaymentId() + "/" + JaxrsResource.REFUNDS;

        final Multimap<String, String> queryParams = paramsWithAudit(createdBy, reason, comment);

        return httpClient.doPostAndFollowLocation(uri, refund, queryParams, Refund.class);
    }

    // Chargebacks

    public Chargebacks getChargebacksForAccount(final UUID accountId) throws KillBillClientException {
        return getChargebacksForAccount(accountId, AuditLevel.NONE);
    }

    public Chargebacks getChargebacksForAccount(final UUID accountId, final AuditLevel auditLevel) throws KillBillClientException {
        final String uri = JaxrsResource.ACCOUNTS_PATH + "/" + accountId + "/" + JaxrsResource.CHARGEBACKS;

        final Multimap<String, String> queryParams = ImmutableMultimap.<String, String>of(JaxrsResource.QUERY_AUDIT, auditLevel.toString());

        return httpClient.doGet(uri, queryParams, Chargebacks.class);
    }

    public Chargebacks getChargebacksForPayment(final UUID paymentId) throws KillBillClientException {
        return getChargebacksForPayment(paymentId, AuditLevel.NONE);
    }

    public Chargebacks getChargebacksForPayment(final UUID paymentId, final AuditLevel auditLevel) throws KillBillClientException {
        final String uri = JaxrsResource.PAYMENTS_PATH + "/" + paymentId + "/" + JaxrsResource.CHARGEBACKS;

        final Multimap<String, String> queryParams = ImmutableMultimap.<String, String>of(JaxrsResource.QUERY_AUDIT, auditLevel.toString());

        return httpClient.doGet(uri, queryParams, Chargebacks.class);
    }

    public Chargeback createChargeBack(final Chargeback chargeback, final String createdBy, final String reason, final String comment) throws KillBillClientException {
        Preconditions.checkNotNull(chargeback.getAmount(), "Chargeback#amount cannot be null");
        Preconditions.checkNotNull(chargeback.getPaymentId(), "Chargeback#paymentId cannot be null");

        final Multimap<String, String> queryParams = paramsWithAudit(createdBy, reason, comment);

        return httpClient.doPostAndFollowLocation(JaxrsResource.CHARGEBACKS_PATH, chargeback, queryParams, Chargeback.class);
    }

    // Payment methods

    public PaymentMethods getPaymentMethods() throws KillBillClientException {
        return getPaymentMethods(0L, 100L);
    }

    public PaymentMethods getPaymentMethods(final Long offset, final Long limit) throws KillBillClientException {
        return getPaymentMethods(offset, limit, AuditLevel.NONE);
    }

    public PaymentMethods getPaymentMethods(final Long offset, final Long limit, final AuditLevel auditLevel) throws KillBillClientException {
        final String uri = JaxrsResource.PAYMENT_METHODS_PATH + "/" + JaxrsResource.PAGINATION;

        final Multimap<String, String> queryParams = ImmutableMultimap.<String, String>of(JaxrsResource.QUERY_SEARCH_OFFSET, String.valueOf(offset),
                                                                                          JaxrsResource.QUERY_SEARCH_LIMIT, String.valueOf(limit),
                                                                                          JaxrsResource.QUERY_AUDIT, auditLevel.toString());

        return httpClient.doGet(uri, queryParams, PaymentMethods.class);
    }

    public PaymentMethods searchPaymentMethods(final String key) throws KillBillClientException {
        return searchPaymentMethods(key, 0L, 100L);
    }

    public PaymentMethods searchPaymentMethods(final String key, final Long offset, final Long limit) throws KillBillClientException {
        return searchPaymentMethods(key, offset, limit, AuditLevel.NONE);
    }

    public PaymentMethods searchPaymentMethods(final String key, final Long offset, final Long limit, final AuditLevel auditLevel) throws KillBillClientException {
        final String uri = JaxrsResource.PAYMENT_METHODS_PATH + "/" + JaxrsResource.SEARCH + "/" + UTF8UrlEncoder.encode(key);

        final Multimap<String, String> queryParams = ImmutableMultimap.<String, String>of(JaxrsResource.QUERY_SEARCH_OFFSET, String.valueOf(offset),
                                                                                          JaxrsResource.QUERY_SEARCH_LIMIT, String.valueOf(limit),
                                                                                          JaxrsResource.QUERY_AUDIT, auditLevel.toString());

        return httpClient.doGet(uri, DEFAULT_EMPTY_QUERY, PaymentMethods.class);
    }

    public PaymentMethod getPaymentMethod(final UUID paymentMethodId) throws KillBillClientException {
        return getPaymentMethod(paymentMethodId, false);
    }

    public PaymentMethod getPaymentMethod(final UUID paymentMethodId, final boolean withPluginInfo) throws KillBillClientException {
        return getPaymentMethod(paymentMethodId, withPluginInfo, AuditLevel.NONE);
    }

    public PaymentMethod getPaymentMethod(final UUID paymentMethodId, final boolean withPluginInfo, final AuditLevel auditLevel) throws KillBillClientException {
        final String uri = JaxrsResource.PAYMENT_METHODS_PATH + "/" + paymentMethodId;

        final Multimap<String, String> queryParams = ImmutableMultimap.<String, String>of(JaxrsResource.QUERY_PAYMENT_METHOD_PLUGIN_INFO, String.valueOf(withPluginInfo),
                                                                                          JaxrsResource.QUERY_AUDIT, auditLevel.toString());

        return httpClient.doGet(uri, queryParams, PaymentMethod.class);
    }

    public PaymentMethods getPaymentMethodsForAccount(final UUID accountId) throws KillBillClientException {
        final String uri = JaxrsResource.ACCOUNTS_PATH + "/" + accountId + "/" + JaxrsResource.PAYMENT_METHODS;

        return httpClient.doGet(uri, DEFAULT_EMPTY_QUERY, PaymentMethods.class);
    }

    public PaymentMethods searchPaymentMethodsByKey(final String key) throws KillBillClientException {
        return searchPaymentMethodsByKeyAndPlugin(key, null);
    }

    public PaymentMethods searchPaymentMethodsByKeyAndPlugin(final String key, @Nullable final String pluginName) throws KillBillClientException {
        return searchPaymentMethodsByKeyAndPlugin(key, pluginName, AuditLevel.NONE);
    }

    public PaymentMethods searchPaymentMethodsByKeyAndPlugin(final String key, @Nullable final String pluginName, final AuditLevel auditLevel) throws KillBillClientException {
        final String uri = JaxrsResource.PAYMENT_METHODS_PATH + "/" + JaxrsResource.SEARCH + "/" + UTF8UrlEncoder.encode(key);

        final Multimap<String, String> queryParams = ImmutableMultimap.<String, String>of(JaxrsResource.QUERY_PAYMENT_METHOD_PLUGIN_INFO, Strings.nullToEmpty(pluginName),
                                                                                          JaxrsResource.QUERY_AUDIT, auditLevel.toString());

        return httpClient.doGet(uri, queryParams, PaymentMethods.class);
    }

    public PaymentMethod createPaymentMethod(final PaymentMethod paymentMethod, final String createdBy, final String reason, final String comment) throws KillBillClientException {
        Preconditions.checkNotNull(paymentMethod.getAccountId(), "PaymentMethod#accountId cannot be null");
        Preconditions.checkNotNull(paymentMethod.getPluginName(), "PaymentMethod#pluginName cannot be null");

        final String uri = JaxrsResource.ACCOUNTS_PATH + "/" + paymentMethod.getAccountId() + "/" + JaxrsResource.PAYMENT_METHODS;

        final Multimap<String, String> queryParams = paramsWithAudit(ImmutableMultimap.<String, String>of(JaxrsResource.QUERY_PAYMENT_METHOD_IS_DEFAULT, paymentMethod.getIsDefault() ? "true" : "false"),
                                                                     createdBy,
                                                                     reason,
                                                                     comment);

        return httpClient.doPostAndFollowLocation(uri, paymentMethod, queryParams, PaymentMethod.class);
    }

    public void updateDefaultPaymentMethod(final UUID accountId, final UUID paymentMethodId, final String createdBy, final String reason, final String comment) throws KillBillClientException {
        final String uri = JaxrsResource.ACCOUNTS_PATH + "/" + accountId + "/" + JaxrsResource.PAYMENT_METHODS + "/" + paymentMethodId + "/" + JaxrsResource.PAYMENT_METHODS_DEFAULT_PATH_POSTFIX;

        final Multimap<String, String> queryParams = paramsWithAudit(createdBy, reason, comment);

        httpClient.doPut(uri, null, queryParams);
    }

    public void deletePaymentMethod(final UUID paymentMethodId, final Boolean deleteDefault, final String createdBy, final String reason, final String comment) throws KillBillClientException {
        final String uri = JaxrsResource.PAYMENT_METHODS_PATH + "/" + paymentMethodId;

        final Multimap<String, String> queryParams = paramsWithAudit(ImmutableMultimap.<String, String>of(QUERY_DELETE_DEFAULT_PM_WITH_AUTO_PAY_OFF, deleteDefault.toString()),
                                                                     createdBy,
                                                                     reason,
                                                                     comment);

        httpClient.doDelete(uri, queryParams);
    }

    // Overdue

    public OverdueState getOverdueStateForAccount(final UUID accountId) throws KillBillClientException {
        final String uri = JaxrsResource.ACCOUNTS_PATH + "/" + accountId + "/" + OVERDUE;

        return httpClient.doGet(uri, DEFAULT_EMPTY_QUERY, OverdueState.class);
    }

    // Tag definitions

    public TagDefinitions getTagDefinitions() throws KillBillClientException {
        return httpClient.doGet(JaxrsResource.TAG_DEFINITIONS_PATH, DEFAULT_EMPTY_QUERY, TagDefinitions.class);
    }

    public TagDefinition getTagDefinition(final UUID tagDefinitionId) throws KillBillClientException {
        final String uri = JaxrsResource.TAG_DEFINITIONS_PATH + "/" + tagDefinitionId;

        return httpClient.doGet(uri, DEFAULT_EMPTY_QUERY, TagDefinition.class);
    }

    public TagDefinition createTagDefinition(final TagDefinition tagDefinition, final String createdBy, final String reason, final String comment) throws KillBillClientException {
        final Multimap<String, String> queryParams = paramsWithAudit(createdBy, reason, comment);

        return httpClient.doPostAndFollowLocation(JaxrsResource.TAG_DEFINITIONS_PATH, tagDefinition, queryParams, TagDefinition.class);
    }

    public void deleteTagDefinition(final UUID tagDefinitionId, final String createdBy, final String reason, final String comment) throws KillBillClientException {
        final String uri = JaxrsResource.TAG_DEFINITIONS_PATH + "/" + tagDefinitionId;

        final Multimap<String, String> queryParams = paramsWithAudit(createdBy, reason, comment);

        httpClient.doDelete(uri, queryParams);
    }

    // Tags

    public Tags getTags() throws KillBillClientException {
        return getTags(0L, 100L);
    }

    public Tags getTags(final Long offset, final Long limit) throws KillBillClientException {
        return getTags(offset, limit, AuditLevel.NONE);
    }

    public Tags getTags(final Long offset, final Long limit, final AuditLevel auditLevel) throws KillBillClientException {
        final String uri = JaxrsResource.TAGS_PATH + "/" + JaxrsResource.PAGINATION;

        final Multimap<String, String> queryParams = ImmutableMultimap.<String, String>of(JaxrsResource.QUERY_SEARCH_OFFSET, String.valueOf(offset),
                                                                                          JaxrsResource.QUERY_SEARCH_LIMIT, String.valueOf(limit),
                                                                                          JaxrsResource.QUERY_AUDIT, auditLevel.toString());

        return httpClient.doGet(uri, queryParams, Tags.class);
    }

    public Tags searchTags(final String key) throws KillBillClientException {
        return searchTags(key, 0L, 100L);
    }

    public Tags searchTags(final String key, final Long offset, final Long limit) throws KillBillClientException {
        return searchTags(key, offset, limit, AuditLevel.NONE);
    }

    public Tags searchTags(final String key, final Long offset, final Long limit, final AuditLevel auditLevel) throws KillBillClientException {
        final String uri = JaxrsResource.TAGS_PATH + "/" + JaxrsResource.SEARCH + "/" + UTF8UrlEncoder.encode(key);

        final Multimap<String, String> queryParams = ImmutableMultimap.<String, String>of(JaxrsResource.QUERY_SEARCH_OFFSET, String.valueOf(offset),
                                                                                          JaxrsResource.QUERY_SEARCH_LIMIT, String.valueOf(limit),
                                                                                          JaxrsResource.QUERY_AUDIT, auditLevel.toString());

        return httpClient.doGet(uri, DEFAULT_EMPTY_QUERY, Tags.class);
    }

    public Tags getAccountTags(final UUID accountId) throws KillBillClientException {
        return getAccountTags(accountId, AuditLevel.NONE);
    }

    public Tags getAccountTags(final UUID accountId, final AuditLevel auditLevel) throws KillBillClientException {
        final String uri = JaxrsResource.ACCOUNTS_PATH + "/" + accountId + "/" + JaxrsResource.TAGS;

        final Multimap<String, String> queryParams = ImmutableMultimap.<String, String>of(JaxrsResource.QUERY_AUDIT, auditLevel.toString());

        return httpClient.doGet(uri, queryParams, Tags.class);
    }

    public Tags createAccountTag(final UUID accountId, final UUID tagDefinitionId, final String createdBy, final String reason, final String comment) throws KillBillClientException {
        final String uri = JaxrsResource.ACCOUNTS_PATH + "/" + accountId + "/" + JaxrsResource.TAGS;

        final Multimap<String, String> queryParams = paramsWithAudit(ImmutableMultimap.<String, String>of(JaxrsResource.QUERY_TAGS, tagDefinitionId.toString()),
                                                                     createdBy,
                                                                     reason,
                                                                     comment);

        return httpClient.doPostAndFollowLocation(uri, null, queryParams, Tags.class);
    }

    public void deleteAccountTag(final UUID accountId, final UUID tagDefinitionId, final String createdBy, final String reason, final String comment) throws KillBillClientException {
        final String uri = JaxrsResource.ACCOUNTS_PATH + "/" + accountId + "/" + JaxrsResource.TAGS;

        final Multimap<String, String> queryParams = paramsWithAudit(ImmutableMultimap.<String, String>of(JaxrsResource.QUERY_TAGS, tagDefinitionId.toString()),
                                                                     createdBy,
                                                                     reason,
                                                                     comment);

        httpClient.doDelete(uri, queryParams);
    }

    // Custom fields

    public CustomFields getCustomFields() throws KillBillClientException {
        return getCustomFields(0L, 100L);
    }

    public CustomFields getCustomFields(final Long offset, final Long limit) throws KillBillClientException {
        return getCustomFields(offset, limit, AuditLevel.NONE);
    }

    public CustomFields getCustomFields(final Long offset, final Long limit, final AuditLevel auditLevel) throws KillBillClientException {
        final String uri = JaxrsResource.CUSTOM_FIELDS_PATH + "/" + JaxrsResource.PAGINATION;

        final Multimap<String, String> queryParams = ImmutableMultimap.<String, String>of(JaxrsResource.QUERY_SEARCH_OFFSET, String.valueOf(offset),
                                                                                          JaxrsResource.QUERY_SEARCH_LIMIT, String.valueOf(limit),
                                                                                          JaxrsResource.QUERY_AUDIT, auditLevel.toString());

        return httpClient.doGet(uri, queryParams, CustomFields.class);
    }

    public CustomFields searchCustomFields(final String key) throws KillBillClientException {
        return searchCustomFields(key, 0L, 100L);
    }

    public CustomFields searchCustomFields(final String key, final Long offset, final Long limit) throws KillBillClientException {
        return searchCustomFields(key, offset, limit, AuditLevel.NONE);
    }

    public CustomFields searchCustomFields(final String key, final Long offset, final Long limit, final AuditLevel auditLevel) throws KillBillClientException {
        final String uri = JaxrsResource.CUSTOM_FIELDS_PATH + "/" + JaxrsResource.SEARCH + "/" + UTF8UrlEncoder.encode(key);

        final Multimap<String, String> queryParams = ImmutableMultimap.<String, String>of(JaxrsResource.QUERY_SEARCH_OFFSET, String.valueOf(offset),
                                                                                          JaxrsResource.QUERY_SEARCH_LIMIT, String.valueOf(limit),
                                                                                          JaxrsResource.QUERY_AUDIT, auditLevel.toString());

        return httpClient.doGet(uri, DEFAULT_EMPTY_QUERY, CustomFields.class);
    }

    public CustomFields getAccountCustomFields(final UUID accountId) throws KillBillClientException {
        return getAccountCustomFields(accountId, AuditLevel.NONE);
    }

    public CustomFields getAccountCustomFields(final UUID accountId, final AuditLevel auditLevel) throws KillBillClientException {
        final String uri = JaxrsResource.ACCOUNTS_PATH + "/" + accountId + "/" + JaxrsResource.CUSTOM_FIELDS;

        final Multimap<String, String> queryParams = ImmutableMultimap.<String, String>of(JaxrsResource.QUERY_AUDIT, auditLevel.toString());

        return httpClient.doGet(uri, queryParams, CustomFields.class);
    }

    public CustomFields createAccountCustomField(final UUID accountId, final CustomField customField, final String createdBy, final String reason, final String comment) throws KillBillClientException {
        return createAccountCustomFields(accountId, ImmutableList.<CustomField>of(customField), createdBy, reason, comment);
    }

    public CustomFields createAccountCustomFields(final UUID accountId, final Iterable<CustomField> customFields, final String createdBy, final String reason, final String comment) throws KillBillClientException {
        final String uri = JaxrsResource.ACCOUNTS_PATH + "/" + accountId + "/" + JaxrsResource.CUSTOM_FIELDS;

        final Multimap<String, String> queryParams = paramsWithAudit(createdBy, reason, comment);

        return httpClient.doPostAndFollowLocation(uri, customFields, queryParams, CustomFields.class);
    }

    public void deleteAccountCustomField(final UUID accountId, final UUID customFieldId, final String createdBy, final String reason, final String comment) throws KillBillClientException {
        deleteAccountCustomFields(accountId, ImmutableList.<UUID>of(customFieldId), createdBy, reason, comment);
    }

    public void deleteAccountCustomFields(final UUID accountId, final String createdBy, final String reason, final String comment) throws KillBillClientException {
        deleteAccountCustomFields(accountId, null, createdBy, reason, comment);
    }

    public void deleteAccountCustomFields(final UUID accountId, @Nullable final Iterable<UUID> customFieldIds, final String createdBy, final String reason, final String comment) throws KillBillClientException {
        final String uri = JaxrsResource.ACCOUNTS_PATH + "/" + accountId + "/" + JaxrsResource.CUSTOM_FIELDS;

        final Multimap<String, String> paramCustomFields = customFieldIds == null ?
                                                           ImmutableMultimap.<String, String>of() :
                                                           ImmutableMultimap.<String, String>of(JaxrsResource.QUERY_CUSTOM_FIELDS, Joiner.on(",").join(customFieldIds));

        final Multimap<String, String> queryParams = paramsWithAudit(paramCustomFields,
                                                                     createdBy,
                                                                     reason,
                                                                     comment);

        httpClient.doDelete(uri, queryParams);
    }

    // Catalog

    public Catalog getSimpleCatalog() throws KillBillClientException {
        final String uri = JaxrsResource.CATALOG_PATH + "/simpleCatalog";

        return httpClient.doGet(uri, DEFAULT_EMPTY_QUERY, Catalog.class);
    }

    public List<PlanDetail> getAvailableAddons(final String baseProductName) throws KillBillClientException {
        final String uri = JaxrsResource.CATALOG_PATH + "/availableAddons";

        final Multimap<String, String> queryParams = ImmutableMultimap.<String, String>of("baseProductName", baseProductName);

        return httpClient.doGet(uri, queryParams, PlanDetails.class);
    }

    public List<PlanDetail> getBasePlans() throws KillBillClientException {
        final String uri = JaxrsResource.CATALOG_PATH + "/availableBasePlans";

        return httpClient.doGet(uri, DEFAULT_EMPTY_QUERY, PlanDetails.class);
    }

    // Tenants

    public Tenant createTenant(final Tenant tenant, final String createdBy, final String reason, final String comment) throws KillBillClientException {
        Preconditions.checkNotNull(tenant.getApiKey(), "Tenant#apiKey cannot be null");
        Preconditions.checkNotNull(tenant.getApiSecret(), "Tenant#apiSecret cannot be null");

        final Multimap<String, String> queryParams = paramsWithAudit(createdBy, reason, comment);

        return httpClient.doPostAndFollowLocation(JaxrsResource.TENANTS_PATH, tenant, queryParams, Tenant.class);
    }

    public TenantKey registerCallbackNotificationForTenant(final String callback, final String createdBy, final String reason, final String comment) throws KillBillClientException {
        final String uri = JaxrsResource.TENANTS_PATH + "/" + JaxrsResource.REGISTER_NOTIFICATION_CALLBACK;

        final Multimap<String, String> queryParams = paramsWithAudit(ImmutableMultimap.<String, String>of(JaxrsResource.QUERY_NOTIFICATION_CALLBACK, callback),
                                                                     createdBy,
                                                                     reason,
                                                                     comment);

        return httpClient.doPostAndFollowLocation(uri, null, queryParams, TenantKey.class);
    }

    public Permissions getPermissions() throws KillBillClientException {
        return httpClient.doGet(JaxrsResource.SECURITY_PATH + "/permissions", DEFAULT_EMPTY_QUERY, Permissions.class);
    }

    // Plugin endpoints

    public Response pluginGET(final String uri) throws Exception {
        return pluginGET(uri, DEFAULT_EMPTY_QUERY);
    }

    public Response pluginGET(final String uri, final Multimap<String, String> queryParams) throws Exception {
        return httpClient.doGet(JaxrsResource.PLUGINS_PATH + "/" + uri, queryParams);
    }

    public Response pluginHEAD(final String uri) throws Exception {
        return pluginHEAD(uri, DEFAULT_EMPTY_QUERY);
    }

    public Response pluginHEAD(final String uri, final Multimap<String, String> queryParams) throws Exception {
        return httpClient.doHead(JaxrsResource.PLUGINS_PATH + "/" + uri, queryParams);
    }

    public Response pluginPOST(final String uri, @Nullable final String body) throws Exception {
        return pluginPOST(uri, body, DEFAULT_EMPTY_QUERY);
    }

    public Response pluginPOST(final String uri, @Nullable final String body, final Multimap<String, String> queryParams) throws Exception {
        return httpClient.doPost(JaxrsResource.PLUGINS_PATH + "/" + uri, body, queryParams);
    }

    public Response pluginPUT(final String uri, @Nullable final String body) throws Exception {
        return pluginPUT(uri, body, DEFAULT_EMPTY_QUERY);
    }

    public Response pluginPUT(final String uri, @Nullable final String body, final Multimap<String, String> queryParams) throws Exception {
        return httpClient.doPut(JaxrsResource.PLUGINS_PATH + "/" + uri, body, queryParams);
    }

    public Response pluginDELETE(final String uri) throws Exception {
        return pluginDELETE(uri, DEFAULT_EMPTY_QUERY);
    }

    public Response pluginDELETE(final String uri, final Multimap<String, String> queryParams) throws Exception {
        return httpClient.doDelete(JaxrsResource.PLUGINS_PATH + "/" + uri, queryParams);
    }

    public Response pluginOPTIONS(final String uri) throws Exception {
        return pluginOPTIONS(uri, DEFAULT_EMPTY_QUERY);
    }

    public Response pluginOPTIONS(final String uri, final Multimap<String, String> queryParams) throws Exception {
        return httpClient.doOptions(JaxrsResource.PLUGINS_PATH + "/" + uri, queryParams);
    }

    // Utilities

    private Multimap<String, String> paramsWithAudit(final Multimap<String, String> queryParams, final String createdBy, final String reason, final String comment) {
        final Multimap<String, String> queryParamsWithAudit = HashMultimap.<String, String>create();
        queryParamsWithAudit.putAll(queryParams);
        queryParamsWithAudit.putAll(paramsWithAudit(createdBy, reason, comment));
        return queryParamsWithAudit;
    }

    private Multimap<String, String> paramsWithAudit(final String createdBy, final String reason, final String comment) {
        return ImmutableMultimap.<String, String>of(KillBillHttpClient.AUDIT_OPTION_CREATED_BY, createdBy,
                                                    KillBillHttpClient.AUDIT_OPTION_REASON, reason,
                                                    KillBillHttpClient.AUDIT_OPTION_COMMENT, comment);
    }

    private void storePluginPropertiesAsParams(final Map<String, String> pluginProperties, final Multimap<String, String> params) {
        for (final String key : pluginProperties.keySet()) {
            params.put(JaxrsResource.QUERY_PLUGIN_PROPERTY, String.format("%s=%s", UTF8UrlEncoder.encode(key), UTF8UrlEncoder.encode(pluginProperties.get(key))));
        }
    }
}
