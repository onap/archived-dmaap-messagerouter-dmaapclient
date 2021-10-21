/*******************************************************************************
 *  ============LICENSE_START=======================================================
 *  org.onap.dmaap
 *  ================================================================================
 *  Copyright © 2017 AT&T Intellectual Property. All rights reserved.
 *  ================================================================================
 *  Modifications Copyright © 2021 Orange.
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

package org.onap.dmaap.mr.tools;

import com.att.nsa.cmdtool.Command;
import com.att.nsa.cmdtool.CommandNotReadyException;

import java.io.PrintStream;

public class AuthCommand implements Command<MRCommandContext> {
    @Override
    public void checkReady(MRCommandContext context) throws CommandNotReadyException {
    }

    @Override
    public void execute(String[] parts, MRCommandContext context, PrintStream out) throws CommandNotReadyException {
        if (parts.length > 0) {
            context.setAuth(parts[0], parts[1]);
            out.println("Now authenticating with " + parts[0]);
        } else {
            context.clearAuth();
            out.println("No longer authenticating.");
        }
    }

    @Override
    public void displayHelp(PrintStream out) {
        out.println("auth <apiKey> <apiSecret>");
        out.println("\tuse these credentials on subsequent transactions");
        out.println("noauth");
        out.println("\tdo not use credentials on subsequent transactions");
    }

    @Override
    public String[] getMatches() {
        return new String[] {
            "auth (\\S*) (\\S*)",
            "noauth"
        };
    }
}
