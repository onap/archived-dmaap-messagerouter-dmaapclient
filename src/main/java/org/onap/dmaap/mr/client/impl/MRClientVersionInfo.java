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
package org.onap.dmaap.mr.client.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MRClientVersionInfo {
    private static final Logger logger = LoggerFactory.getLogger(MRClientVersionInfo.class);

    public static String getVersion() {
        return VERSION;
    }

    private static final Properties PROPS = new Properties();
    private static final String VERSION;

    static {
        String use = null;
        try (InputStream is = MRClientVersionInfo.class.getResourceAsStream("/MRClientVersion.properties")) {
            if (is != null) {
                PROPS.load(is);
                use = PROPS.getProperty("MRClientVersion", null);
            }
        } catch (IOException e) {
            logger.error("exception: ", e);
        }
        VERSION = use;
    }
}
