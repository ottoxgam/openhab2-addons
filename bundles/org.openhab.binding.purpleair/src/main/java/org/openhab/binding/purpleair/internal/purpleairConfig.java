/**
 * Copyright (c) 2010-2019 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.purpleair.internal;

/**
 * The {@link purpleairConfig} class contains fields mapping thing configuration parameters.
 *
 * @author Zack Lapinski - Initial contribution
 */
public class purpleairConfig {
    // public String channel_id; // from purpleair json page
    // public String api_key;
    public int refreshInterval;
    public String stationId;
}
