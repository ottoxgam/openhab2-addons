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
 * The {@link thingspeakSec} Enum defines common constants, which are
 * used across the whole binding.
 *
 * @author Zack Lapinski - Initial contribution
 */
public enum thingspeakSec {

    CHANNEL_PM1VALUE("pm1value", "field1"),
    CHANNEL_PM10VALUE("pm10value", "field3");

    private final String id;
    private final String index;

    thingspeakSec(String id, String index) {
        this.id = id;
        this.index = index;
    }

    public String getId() {
        return id;
    }

    public String getIndex() {
        return index;
    }
}
