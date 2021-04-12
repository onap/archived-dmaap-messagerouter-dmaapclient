/*******************************************************************************
 *  ============LICENSE_START=======================================================
 *  org.onap.dmaap
 *  ================================================================================
 *  Copyright © 2017 AT&T Intellectual Property. All rights reserved.
 *  ================================================================================
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  ============LICENSE_END=========================================================
 *
 *  ECOMP is a trademark and service mark of AT&T Intellectual Property.
 *
 *******************************************************************************/
package org.onap.dmaap.mr.client.response;

/**
 * Response for Publisher
 *
 * @author author
 */
public class MRPublisherResponse {
    private String responseCode;

    private String responseMessage;

    private int pendingMsgs;

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public int getPendingMsgs() {
        return pendingMsgs;
    }

    public void setPendingMsgs(int pendingMsgs) {
        this.pendingMsgs = pendingMsgs;
    }

    @Override
    public String toString() {
        return "Response Code:" + this.responseCode + ","
                + "Response Message:" + this.responseMessage + "," + "Pending Messages Count"
                + this.pendingMsgs;
    }

}
