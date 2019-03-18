/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Samsung Electronics Co., Ltd. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ============LICENSE_END=========================================================
 */

package org.onap.dmaap.mr.client.impl;

public class PostAuthDataObject {

    private String path;
    private byte[] data;
    private String contentType;
    private String authKey;
    private String authDate;
    private String username;
    private String password;
    private String protocolFlag;

    public String getPath() {
        return path;
    }

    public PostAuthDataObject setPath(String path) {
        this.path = path;
        return this;
    }

    public byte[] getData() {
        return data;
    }

    public PostAuthDataObject setData(byte[] data) {
        this.data = data;
        return this;
    }

    public String getContentType() {
        return contentType;
    }

    public PostAuthDataObject setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public String getAuthKey() {
        return authKey;
    }

    public PostAuthDataObject setAuthKey(String authKey) {
        this.authKey = authKey;
        return this;
    }

    public String getAuthDate() {
        return authDate;
    }

    public PostAuthDataObject setAuthDate(String authDate) {
        this.authDate = authDate;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public PostAuthDataObject setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public PostAuthDataObject setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getProtocolFlag() {
        return protocolFlag;
    }

    public PostAuthDataObject setProtocolFlag(String protocolFlag) {
        this.protocolFlag = protocolFlag;
        return this;
    }
}
