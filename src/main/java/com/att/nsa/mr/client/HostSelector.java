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
package com.att.nsa.mr.client;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HostSelector
{
  private final TreeSet<String> fBaseHosts;
  private final DelayQueue<BlacklistEntry> fBlacklist;
  private String fIdealHost;
  private String fCurrentHost;
  private static final Logger log = LoggerFactory.getLogger(HostSelector.class);

  public HostSelector(String hostPart)
  {
    this(makeSet(hostPart), null);
  }

  public HostSelector(Collection<String> baseHosts)
  {
    this(baseHosts, null);
  }

  public HostSelector(Collection<String> baseHosts, String signature)
  {
    if (baseHosts.isEmpty())
    {
      throw new IllegalArgumentException("At least one host must be provided.");
    }

    this.fBaseHosts = new TreeSet(baseHosts);
    this.fBlacklist = new DelayQueue();
    this.fIdealHost = null;

    if (signature == null) {
      return;
    }
    int index = 0 ;
    int value = signature.hashCode();
    if(value!=0) {
    index = Math.abs(value) % baseHosts.size();
    }
    Iterator it = this.fBaseHosts.iterator();
    while (index-- > 0)
    {
      it.next();
    }
    this.fIdealHost = ((String)it.next());
  }

  public String selectBaseHost()
  {
    if (this.fCurrentHost == null)
    {
      makeSelection();
    }
    return this.fCurrentHost;
  }

  public void reportReachabilityProblem(long blacklistUnit, TimeUnit blacklistTimeUnit)
  {
    if (this.fCurrentHost == null)
    {
      log.warn("Reporting reachability problem, but no host is currently selected.");
    }

    if (blacklistUnit > 0L)
    {
      for (BlacklistEntry be : this.fBlacklist)
      {
        if (be.getHost().equals(this.fCurrentHost))
        {
          be.expireNow();
        }
      }

      LinkedList devNull = new LinkedList();
      this.fBlacklist.drainTo(devNull);

      if (this.fCurrentHost != null)
      {
        this.fBlacklist.add(new BlacklistEntry(this.fCurrentHost, TimeUnit.MILLISECONDS.convert(blacklistUnit, blacklistTimeUnit)));
      }
    }
    this.fCurrentHost = null;
  }

  private String makeSelection()
  {
    TreeSet workingSet = new TreeSet(this.fBaseHosts);

    LinkedList devNull = new LinkedList();
    this.fBlacklist.drainTo(devNull);
    for (BlacklistEntry be : this.fBlacklist)
    {
      workingSet.remove(be.getHost());
    }

    if (workingSet.isEmpty())
    {
      log.warn("All hosts were blacklisted; reverting to full set of hosts.");
      workingSet.addAll(this.fBaseHosts);
      this.fCurrentHost = null;
    }

    String selection = null;
    if ((this.fCurrentHost != null) && (workingSet.contains(this.fCurrentHost)))
    {
      selection = this.fCurrentHost;
    }
    else if ((this.fIdealHost != null) && (workingSet.contains(this.fIdealHost)))
    {
      selection = this.fIdealHost;
    }
    else
    {
      int index = 0;
      int value = new Random().nextInt();
      Vector v = new Vector(workingSet);
      if(value!=0) {
      index = Math.abs(value) % workingSet.size();
      }
      selection = (String)v.elementAt(index);
    }

    this.fCurrentHost = selection;
    return this.fCurrentHost;
  }

  private static Set<String> makeSet(String s)
  {
    TreeSet set = new TreeSet();
    set.add(s);
    return set; }

  private static class BlacklistEntry implements Delayed {
    private final String fHost;
    private long fExpireAtMs;

    public BlacklistEntry(String host, long delayMs) {
      this.fHost = host;
      this.fExpireAtMs = (System.currentTimeMillis() + delayMs);
    }

    public void expireNow()
    {
      this.fExpireAtMs = 0L;
    }

    public String getHost()
    {
      return this.fHost;
    }

    public int compareTo(Delayed o)
    {
      Long thisDelay = Long.valueOf(getDelay(TimeUnit.MILLISECONDS));
      return thisDelay.compareTo(Long.valueOf(o.getDelay(TimeUnit.MILLISECONDS)));
    }

    public long getDelay(TimeUnit unit)
    {
      long remainingMs = this.fExpireAtMs - System.currentTimeMillis();
      return unit.convert(remainingMs, TimeUnit.MILLISECONDS);
    }
  }
}
