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

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * The {@link purpleairBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Zack Lapinsk - Initial contribution
 */
@NonNullByDefault
public class purpleairBindingConstants {

    private static final String BINDING_ID = "purpleair";

    // List of all Thing Type UIDs
    public static final ThingTypeUID THING_PURPLEAIR = new ThingTypeUID(BINDING_ID, "Station");

    // List of all Channel ids
    public static final String CHANNEL_ID_PM1VALUE = "pm1value";
    public static final String CHANNEL_ID_PM25VALUE = "pm25value";
    public static final String CHANNEL_ID_PM10VALUE = "pm10value";
    public static final String CHANNEL_ID_RSSI = "rssi";
    public static final String CHANNEL_ID_LASTSEEN = "lastseen";
    public static final String CHANNEL_ID_TEMPF = "temperature";
    public static final String CHANNEL_ID_HUMIDITY = "humidity";
    public static final String CHANNEL_ID_PRESSURE = "pressure";
    public static final String CHANNEL_ID_LABEL = "label";
    public static final String CHANNEL_ID_LAT = "latitude";
    public static final String CHANNEL_ID_LON = "longitude";
    public static final String CHANNEL_ID_AQI = "aqi";
    public static final String CHANNEL_ID_AQIDESCRIPTION = "aqidescription";
    public static final String CHANNEL_ID_AQIMESSAGE = "aqimessage";

    // List of all JSON IDs
    public static final String CHANNEL_PM1VALUE = "field1";
    public static final String CHANNEL_PM25VALUE = "PM2_5Value";
    public static final String CHANNEL_PM10VALUE = "field3";
    public static final String CHANNEL_RSSI = "RSSI";
    public static final String CHANNEL_LASTSEEN = "Lastseen";
    public static final String CHANNEL_TEMPF = "temp_f";
    public static final String CHANNEL_HUMIDITY = "humidity";
    public static final String CHANNEL_PRESSURE = "pressure";
    public static final String CHANNEL_LABEL = "Label";
    public static final String CHANNEL_LAT = "Latitude";
    public static final String CHANNEL_LON = "Longitude";
    public static final String CHANNEL_AQI = "aqi";
    public static final String CHANNEL_AQIDESCRIPTION = "aqidescription";
    public static final String CHANNEL_AQIMESSAGE = "aqimessage";

    // Channel Type
    public static final String CHANNEL_TYPE_PM1VALUE = "number";
    public static final String CHANNEL_TYPE_PM25VALUE = "number";
    public static final String CHANNEL_TYPE_PM10VALUE = "number";
    public static final String CHANNEL_TYPE_RSSI = "number";
    public static final String CHANNEL_TYPE_LASTSEEN = "number";
    public static final String CHANNEL_TYPE_TEMPF = "number";
    public static final String CHANNEL_TYPE_HUMIDITY = "number";
    public static final String CHANNEL_TYPE_PRESSURE = "number";
    public static final String CHANNEL_TYPE_LABEL = "string";
    public static final String CHANNEL_TYPE_LAT = "number";
    public static final String CHANNEL_TYPE_LON = "number";
    public static final String CHANNEL_TYPE_AQI = "NUMBER";
    public static final String CHANNEL_TYPE_AQIDESCRIPTION = "string";
    public static final String CHANNEL_TYPE_AQIMESSAGE = "string";

    // Some basic constants (JSON ID)
    public static final String PURPLEAIR_JSON_ROOT = "ID";
    public static final String PURPLEAIR_JSON_JSONPROPERTIES = "Label";

    public static final String THINGSPEAK_JSON_ROOT = "entry_id";
    public static final String THINGSPEAK_JSON_JSONPROPERTIES = "feeds[0]";

}
