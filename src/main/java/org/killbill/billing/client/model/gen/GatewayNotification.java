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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 *           DO NOT EDIT !!!
 *
 * This code has been generated by the Kill Bill swagger generator.
 *  @See https://github.com/killbill/killbill-swagger-coden
 */
import org.killbill.billing.client.model.KillBillObject;

public class GatewayNotification extends KillBillObject {

    private UUID kbPaymentId = null;

    private Integer status = null;

    private String entity = null;

    private Map<String, List<String>> headers = null;

    private Map<String, Object> properties = null;

    public GatewayNotification kbPaymentId(UUID kbPaymentId) {
        this.kbPaymentId = kbPaymentId;
        return this;
    }

    
    public UUID getKbPaymentId() {
        return kbPaymentId;
    }

    public void setKbPaymentId(UUID kbPaymentId) {
        this.kbPaymentId = kbPaymentId;
    }

    public GatewayNotification status(Integer status) {
        this.status = status;
        return this;
    }

    
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public GatewayNotification entity(String entity) {
        this.entity = entity;
        return this;
    }

    
    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public GatewayNotification headers(Map<String, List<String>> headers) {
        this.headers = headers;
        return this;
    }

    public GatewayNotification putHeadersItem(String key, List<String> headersItem) {
        if (this.headers == null) {
            this.headers = new HashMap<String, List<String>>();
        }
        this.headers.put(key, headersItem);
        return this;
    }

    
    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }

    public GatewayNotification properties(Map<String, Object> properties) {
        this.properties = properties;
        return this;
    }

    public GatewayNotification putPropertiesItem(String key, Object propertiesItem) {
        if (this.properties == null) {
            this.properties = new HashMap<String, Object>();
        }
        this.properties.put(key, propertiesItem);
        return this;
    }

    
    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GatewayNotification gatewayNotification = (GatewayNotification) o;
        return Objects.equals(this.kbPaymentId, gatewayNotification.kbPaymentId) &&
        Objects.equals(this.status, gatewayNotification.status) &&
        Objects.equals(this.entity, gatewayNotification.entity) &&
        Objects.equals(this.headers, gatewayNotification.headers) &&
        Objects.equals(this.properties, gatewayNotification.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kbPaymentId, status, entity, headers, properties);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class GatewayNotification {\n");
        
        sb.append("    kbPaymentId: ").append(toIndentedString(kbPaymentId)).append("\n");
        sb.append("    status: ").append(toIndentedString(status)).append("\n");
        sb.append("    entity: ").append(toIndentedString(entity)).append("\n");
        sb.append("    headers: ").append(toIndentedString(headers)).append("\n");
        sb.append("    properties: ").append(toIndentedString(properties)).append("\n");
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

