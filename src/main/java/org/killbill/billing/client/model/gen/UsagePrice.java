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
import java.util.ArrayList;
import java.util.List;
import org.killbill.billing.catalog.api.BillingMode;
import org.killbill.billing.catalog.api.TierBlockPolicy;
import org.killbill.billing.catalog.api.UsageType;
import org.killbill.billing.client.model.gen.TierPrice;

/**
 *           DO NOT EDIT !!!
 *
 * This code has been generated by the Kill Bill swagger generator.
 *  @See https://github.com/killbill/killbill-swagger-coden
 */
import org.killbill.billing.client.model.KillBillObject;

public class UsagePrice {

    private String usageName = null;

    private UsageType usageType = null;

    private BillingMode billingMode = null;

    private TierBlockPolicy tierBlockPolicy = null;

    private List<TierPrice> tierPrices = null;


    public UsagePrice() {
    }

    public UsagePrice(final String usageName,
                     final UsageType usageType,
                     final BillingMode billingMode,
                     final TierBlockPolicy tierBlockPolicy,
                     final List<TierPrice> tierPrices) {
        this.usageName = usageName;
        this.usageType = usageType;
        this.billingMode = billingMode;
        this.tierBlockPolicy = tierBlockPolicy;
        this.tierPrices = tierPrices;

    }


    public UsagePrice setUsageName(final String usageName) {
        this.usageName = usageName;
        return this;
    }

    public String getUsageName() {
        return usageName;
    }

    public UsagePrice setUsageType(final UsageType usageType) {
        this.usageType = usageType;
        return this;
    }

    public UsageType getUsageType() {
        return usageType;
    }

    public UsagePrice setBillingMode(final BillingMode billingMode) {
        this.billingMode = billingMode;
        return this;
    }

    public BillingMode getBillingMode() {
        return billingMode;
    }

    public UsagePrice setTierBlockPolicy(final TierBlockPolicy tierBlockPolicy) {
        this.tierBlockPolicy = tierBlockPolicy;
        return this;
    }

    public TierBlockPolicy getTierBlockPolicy() {
        return tierBlockPolicy;
    }

    public UsagePrice setTierPrices(final List<TierPrice> tierPrices) {
        this.tierPrices = tierPrices;
        return this;
    }

    public UsagePrice addTierPricesItem(final TierPrice tierPricesItem) {
        if (this.tierPrices == null) {
            this.tierPrices = new ArrayList<TierPrice>();
        }
        this.tierPrices.add(tierPricesItem);
        return this;
    }

    public List<TierPrice> getTierPrices() {
        return tierPrices;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UsagePrice usagePrice = (UsagePrice) o;
        return Objects.equals(this.usageName, usagePrice.usageName) &&
        Objects.equals(this.usageType, usagePrice.usageType) &&
        Objects.equals(this.billingMode, usagePrice.billingMode) &&
        Objects.equals(this.tierBlockPolicy, usagePrice.tierBlockPolicy) &&
        Objects.equals(this.tierPrices, usagePrice.tierPrices);

    }

    @Override
    public int hashCode() {
        return Objects.hash(usageName,
                            usageType,
                            billingMode,
                            tierBlockPolicy,
                            tierPrices);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class UsagePrice {\n");
        
        sb.append("    usageName: ").append(toIndentedString(usageName)).append("\n");
        sb.append("    usageType: ").append(toIndentedString(usageType)).append("\n");
        sb.append("    billingMode: ").append(toIndentedString(billingMode)).append("\n");
        sb.append("    tierBlockPolicy: ").append(toIndentedString(tierBlockPolicy)).append("\n");
        sb.append("    tierPrices: ").append(toIndentedString(tierPrices)).append("\n");
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
