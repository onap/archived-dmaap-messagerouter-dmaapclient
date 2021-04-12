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
package org.onap.dmaap.mr.tools;

import com.att.nsa.apiClient.http.HttpTracer;
import com.att.nsa.cmdtool.Command;
import com.att.nsa.cmdtool.CommandNotReadyException;

import java.io.PrintStream;
import java.net.URI;
import java.util.List;
import java.util.Map;

public class TraceCommand implements Command<MRCommandContext> {
    @Override
    public void checkReady(MRCommandContext context) throws CommandNotReadyException {
    }

    @Override
    public void execute(String[] parts, MRCommandContext context, final PrintStream out) throws CommandNotReadyException {
        if (parts[0].equalsIgnoreCase("on")) {
            context.useTracer(new HttpTracer() {
                @Override
                public void outbound(URI uri, Map<String, List<String>> headers, String method, byte[] entity) {
                    out.println(K_LINE_BREAK);
                    out.println(">>> " + method + " " + uri.toString());
                    for (Map.Entry<String, List<String>> e : headers.entrySet()) {
                        final StringBuilder vals = new StringBuilder();
                        for (String val : e.getValue()) {
                            if (vals.length() > 0) vals.append(", ");
                            vals.append(val);
                        }
                        out.println(">>> " + e.getKey() + ": " + vals);
                    }
                    if (entity != null) {
                        out.println();
                        out.println(new String(entity));
                    }
                    out.println(K_LINE_BREAK);
                }

                @Override
                public void inbound(Map<String, List<String>> headers, int statusCode, String responseLine, byte[] entity) {
                    out.println(K_LINE_BREAK);
                    out.println("<<< " + responseLine);
                    for (Map.Entry<String, List<String>> e : headers.entrySet()) {
                        final StringBuilder vals = new StringBuilder();
                        for (String val : e.getValue()) {
                            if (vals.length() > 0) vals.append(", ");
                            vals.append(val);
                        }
                        out.println("<<< " + e.getKey() + ": " + vals);
                    }
                    if (entity != null) {
                        out.println();
                        out.println(new String(entity));
                    }
                    out.println(K_LINE_BREAK);
                }
            });
        } else {
            context.noTracer();
        }
    }

    @Override
    public void displayHelp(PrintStream out) {
        out.println("trace on|off");
        out.println("\tWhen trace is on, HTTP interaction is printed to the console.");
    }

    @Override
    public String[] getMatches() {
        return new String[]{
                "trace (on)",
                "trace (off)"
        };
    }

    private static final String K_LINE_BREAK = "======================================================================";
}
