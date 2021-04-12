/**
 * ============LICENSE_START=======================================================
 * org.onap.dmaap
 * ================================================================================
 * Copyright © 2017 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 * <p>
 * ECOMP is a trademark and service mark of AT&T Intellectual Property.
 */
/**
 *
 */
package org.onap.dmaap.mr.client;

/**
 * @author author
 *
 */
public enum ProtocolTypeConstants {

    DME2("DME2"),
    AAF_AUTH("HTTPAAF"),
    AUTH_KEY("HTTPAUTH"),
    HTTPNOAUTH("HTTPNOAUTH");

    private String value;

    ProtocolTypeConstants(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
