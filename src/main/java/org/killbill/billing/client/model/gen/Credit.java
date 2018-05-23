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


package org.killbill.billing.client.model.gen;

import java.util.Objects;
import java.util.Arrays;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.joda.time.LocalDate;
import org.killbill.billing.catalog.api.Currency;
import org.killbill.billing.client.model.gen.AuditLog;

/**
 *           DO NOT EDIT !!!
 *
 * This code has been generated by the Kill Bill swagger generator.
 *  @See https://github.com/killbill/killbill-swagger-coden
 */
import org.killbill.billing.client.model.KillBillObject;

public class Credit extends KillBillObject {

    private UUID creditId = null;

    private BigDecimal creditAmount = null;

    private Currency currency = null;

    private UUID invoiceId = null;

    private String invoiceNumber = null;

    private LocalDate effectiveDate = null;

    private UUID accountId = null;

    private String description = null;

    private String itemDetails = null;



    public Credit() {
    }

    public Credit(final UUID creditId,
                     final BigDecimal creditAmount,
                     final Currency currency,
                     final UUID invoiceId,
                     final String invoiceNumber,
                     final LocalDate effectiveDate,
                     final UUID accountId,
                     final String description,
                     final String itemDetails,
                     final List<AuditLog> auditLogs) {
        super(auditLogs);
        this.creditId = creditId;
        this.creditAmount = creditAmount;
        this.currency = currency;
        this.invoiceId = invoiceId;
        this.invoiceNumber = invoiceNumber;
        this.effectiveDate = effectiveDate;
        this.accountId = accountId;
        this.description = description;
        this.itemDetails = itemDetails;

    }


    public Credit setCreditId(final UUID creditId) {
        this.creditId = creditId;
        return this;
    }

    public UUID getCreditId() {
        return creditId;
    }

    public Credit setCreditAmount(final BigDecimal creditAmount) {
        this.creditAmount = creditAmount;
        return this;
    }

    public BigDecimal getCreditAmount() {
        return creditAmount;
    }

    public Credit setCurrency(final Currency currency) {
        this.currency = currency;
        return this;
    }

    public Currency getCurrency() {
        return currency;
    }

    public Credit setInvoiceId(final UUID invoiceId) {
        this.invoiceId = invoiceId;
        return this;
    }

    public UUID getInvoiceId() {
        return invoiceId;
    }

    public Credit setInvoiceNumber(final String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
        return this;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public Credit setEffectiveDate(final LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
        return this;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public Credit setAccountId(final UUID accountId) {
        this.accountId = accountId;
        return this;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public Credit setDescription(final String description) {
        this.description = description;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Credit setItemDetails(final String itemDetails) {
        this.itemDetails = itemDetails;
        return this;
    }

    public String getItemDetails() {
        return itemDetails;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Credit credit = (Credit) o;
        return Objects.equals(this.creditId, credit.creditId) &&
        Objects.equals(this.creditAmount, credit.creditAmount) &&
        Objects.equals(this.currency, credit.currency) &&
        Objects.equals(this.invoiceId, credit.invoiceId) &&
        Objects.equals(this.invoiceNumber, credit.invoiceNumber) &&
        Objects.equals(this.effectiveDate, credit.effectiveDate) &&
        Objects.equals(this.accountId, credit.accountId) &&
        Objects.equals(this.description, credit.description) &&
        Objects.equals(this.itemDetails, credit.itemDetails) &&
        Objects.equals(this.auditLogs, credit.auditLogs);

    }

    @Override
    public int hashCode() {
        return Objects.hash(creditId,
                            creditAmount,
                            currency,
                            invoiceId,
                            invoiceNumber,
                            effectiveDate,
                            accountId,
                            description,
                            itemDetails,
                            auditLogs, super.hashCode());
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Credit {\n");
        sb.append("    ").append(toIndentedString(super.toString())).append("\n");
        sb.append("    creditId: ").append(toIndentedString(creditId)).append("\n");
        sb.append("    creditAmount: ").append(toIndentedString(creditAmount)).append("\n");
        sb.append("    currency: ").append(toIndentedString(currency)).append("\n");
        sb.append("    invoiceId: ").append(toIndentedString(invoiceId)).append("\n");
        sb.append("    invoiceNumber: ").append(toIndentedString(invoiceNumber)).append("\n");
        sb.append("    effectiveDate: ").append(toIndentedString(effectiveDate)).append("\n");
        sb.append("    accountId: ").append(toIndentedString(accountId)).append("\n");
        sb.append("    description: ").append(toIndentedString(description)).append("\n");
        sb.append("    itemDetails: ").append(toIndentedString(itemDetails)).append("\n");
        sb.append("    auditLogs: ").append(toIndentedString(auditLogs)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}

