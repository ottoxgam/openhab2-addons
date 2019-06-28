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
package org.openhab.binding.purpleair.internal.handler;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.eclipse.smarthome.core.library.types.DateTimeType;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.UnDefType;
import org.eclipse.smarthome.io.net.http.HttpUtil;
import org.openhab.binding.purpleair.internal.purpleairBindingConstants;
import org.openhab.binding.purpleair.internal.purpleairChannel;
import org.openhab.binding.purpleair.internal.purpleairConfig;
import org.openhab.binding.purpleair.internal.purpleairFunctions;
import org.openhab.binding.purpleair.internal.thingspeakPri;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

/**
 * The {@link purpleairHandler} is responsible for handling commands, which are
 * sent to one of the channels. It does the "heavy lifting" of connecting to the
 * Solar-Log, getting the data, parsing it and updating the channels.
 *
 * @author Zack Lapinski - Initial contribution
 */
public class purpleairHandler extends BaseThingHandler {

    private static Object THINGSPEAKPRIMARYID = null;
    private static Object THINGSPEAK_PRIMARY_ID_READ_KEY = null;
    private static Object PM25VAL = null;
    private Logger logger = LoggerFactory.getLogger(purpleairHandler.class);
    private purpleairConfig config;

    private final int timeout = 5000;

    public purpleairHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        // Read only
    }

    @Override
    public void initialize() {
        logger.debug("Initializing Purpleair Connection");
        config = getConfigAs(purpleairConfig.class);
        scheduler.scheduleWithFixedDelay(() -> {
            logger.debug("Running purpleair refresh cycle");
            try {
                refreshpurpleair();
                updateStatus(ThingStatus.ONLINE);
                // Very rudimentary Exception differentiation
            } catch (IOException e) {
                logger.debug("Error reading response from Purlpeair: {}", e);
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                        "Communication error. Please retry later.");
            } catch (JsonSyntaxException je) {
                logger.warn("Invalid JSON when refreshing source {}: {}", getThing().getUID(), je);
            } catch (Exception e) {
                logger.warn("Error refreshing source {}: {}", getThing().getUID(), e);
            }
        }, 0, config.refreshInterval < 15 ? 15 : config.refreshInterval, TimeUnit.SECONDS); // Minimum interval is 15 s
        logger.debug("Initializing thingspeak Connection");
        config = getConfigAs(purpleairConfig.class);
        scheduler.scheduleWithFixedDelay(() -> {
            logger.debug("Running thingspeak refresh cycle");
            try {
                refreshthingspeak();
                updateStatus(ThingStatus.ONLINE);
                // Very rudimentary Exception differentiation
            } catch (IOException e) {
                logger.debug("Error reading response from thingspeak: {}", e);
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                        "Communication error. Please retry later.");
            } catch (JsonSyntaxException je) {
                logger.warn("Invalid JSON when refreshing source {}: {}", getThing().getUID(), je);
            } catch (Exception e) {
                logger.warn("Error refreshing source {}: {}", getThing().getUID(), e);
            }
        }, 0, config.refreshInterval < 15 ? 15 : config.refreshInterval, TimeUnit.SECONDS); // Minimum interval is 15 s
        scheduler.scheduleWithFixedDelay(() -> {
            logger.debug("Running AQI calculations.");
            try {
                refreshAQI();
            } catch (IOException e) {
                logger.debug("Error reading response from thingspeak: {}", e);
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                        "Communication error. Please retry later.");
            } catch (JsonSyntaxException je) {
                logger.warn("Invalid JSON when refreshing source {}: {}", getThing().getUID(), je);
            } catch (Exception e) {
                logger.warn("Error refreshing source {}: {}", getThing().getUID(), e);
            }
        }, 0, config.refreshInterval < 15 ? 15 : config.refreshInterval, TimeUnit.SECONDS); // Minimum interval is 15 s

    }

    public void refreshpurpleair() throws Exception {
        // Get the JSON - somehow
        logger.trace("Starting purpleair refresh handler");
        String httpMethod = "GET";
        String url = "https://www.purpleair.com/json?show=" + config.stationId;
        logger.debug("Attempting to load data from {}", url);
        String response = HttpUtil.executeUrl(httpMethod, url, timeout);
        logger.debug("Attempting to load data from String {}", response);
        JsonElement purpleairDataElement = new JsonParser().parse(response);
        JsonObject purpleairData = purpleairDataElement.getAsJsonObject();

        JsonArray jsonarr = purpleairData.getAsJsonArray("results");
        JsonElement json0 = jsonarr.get(0);
        JsonObject sensorAjson = json0.getAsJsonObject();
        THINGSPEAKPRIMARYID = sensorAjson.get("THINGSPEAK_PRIMARY_ID").getAsString();
        THINGSPEAK_PRIMARY_ID_READ_KEY = sensorAjson.get("THINGSPEAK_PRIMARY_ID_READ_KEY").getAsString();
        PM25VAL = sensorAjson.get("PM2_5Value").getAsFloat();

        // Check whether the data is well-formed
        if (sensorAjson.has(purpleairBindingConstants.PURPLEAIR_JSON_ROOT)) {
            logger.trace("Attempting to read data");
            for (purpleairChannel channelConfig : purpleairChannel.values()) {
                if (sensorAjson.has(channelConfig.getIndex())) {
                    String value = sensorAjson.get(channelConfig.getIndex()).getAsString();
                    Channel channel = getThing().getChannel(channelConfig.getId());
                    State state = getpurpleState(value, channelConfig);
                    if (channel != null) {
                        logger.trace("Update channel state: {}", state);
                        updateState(channel.getUID(), state);
                    }
                } else {
                    logger.debug("Error refreshing source {}", getThing().getUID(), channelConfig.getId());
                }
            }

        } else

        {
            logger.warn("Data retrieval failed, no data returned {}", response);
        }
    }

    private void refreshthingspeak() throws Exception {
        // Get the JSON - somehow
        logger.trace("Starting thingspeak refresh handler");
        String httpMethod = "GET";
        String url = "https://api.thingspeak.com/channels/" + THINGSPEAKPRIMARYID + "/feeds.json?api_key="
                + THINGSPEAK_PRIMARY_ID_READ_KEY + "&results=1&";
        logger.debug("Attempting to load data from {}", url);
        String response = HttpUtil.executeUrl(httpMethod, url, timeout);
        JsonElement thingspeakDataElement = new JsonParser().parse(response);
        JsonObject thingspeakData = thingspeakDataElement.getAsJsonObject();

        JsonArray jsonarr = thingspeakData.getAsJsonArray("feeds");
        JsonElement json0 = jsonarr.get(0);
        // JsonElement json1 = jsonarr.get(1);
        JsonObject thingspeakjson = json0.getAsJsonObject();

        logger.debug("ThingspeakID = {}", THINGSPEAKPRIMARYID);
        logger.debug("Thinkspeak KEY= {}", THINGSPEAK_PRIMARY_ID_READ_KEY);

        // Check whether the data is well-formed
        if (thingspeakjson.has(purpleairBindingConstants.THINGSPEAK_JSON_ROOT)) {
            logger.trace("Attempting to read data");
            for (thingspeakPri channelConfig : thingspeakPri.values()) {
                if (thingspeakjson.has(channelConfig.getIndex())) {
                    String value = thingspeakjson.get(channelConfig.getIndex()).getAsString();
                    Channel channel = getThing().getChannel(channelConfig.getId());
                    State state = getThingPriState(value, channelConfig);
                    if (channel != null) {
                        logger.trace("Update channel state: {}", state);
                        updateState(channel.getUID(), state);
                    }
                } else {
                    logger.debug("Error refreshing source {}", getThing().getUID(), channelConfig.getId());
                }
            }
        } else {
            logger.warn("Data retrieval failed, no data returned {}", response);
        }
    }

    public void refreshAQI() throws Exception {
        Number aqi = purpleairFunctions.aqiFromPM((Float) (PM25VAL));
        String aqidisc = purpleairFunctions.getAQIDescription(aqi.intValue());
        String aqimess = purpleairFunctions.getAQIMessage(aqi.floatValue());
        updateState("aqi", new DecimalType(aqi.intValue()));
        updateState("aqidescription", new StringType(aqidisc));
        updateState("aqimessage", new StringType(aqimess));
    }

    private State getpurpleState(String value, purpleairChannel type) {
        switch (type) {
            // Only DateTime channel
            case CHANNEL_LASTSEEN:
                try {
                    logger.trace("Parsing date {}", value);
                    try {
                        Date date = new SimpleDateFormat("dd.MM.yy HH:mm:ss").parse(value);
                        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");// dd/MM/yyyy
                        String strDate = sdfDate.format(date);

                        logger.trace("Parsing date successful. Returning date. {}", new DateTimeType(strDate));
                        return new DateTimeType(strDate);
                    } catch (ParseException fpe) {
                        logger.trace("Parsing date failed. Returning string.", fpe);
                        return new StringType(value);
                    }
                } catch (IllegalArgumentException e) {
                    logger.warn("Parsing date failed: {}. Returning nothing", e);
                    return UnDefType.UNDEF;
                }
                // All other channels should be numbers
            case CHANNEL_LABEL:
                logger.trace("Parsing label {}", value);
                try {
                    return new StringType(value);
                } catch (IllegalArgumentException e) {
                    logger.warn("Parsing label failed: {}. Returning nothing", e);
                    return UnDefType.UNDEF;
                }
            default:
                try {
                    logger.trace("Parsing number {}", value);
                    return new DecimalType(new BigDecimal(value));
                } catch (NumberFormatException e) {
                    // Log a warning and return UNDEF
                    logger.warn("Parsing number failed: {}. Returning nothing", e);
                    return UnDefType.UNDEF;
                }
        }
    }

    private State getThingPriState(String value, thingspeakPri type) {
        switch (type) {

            default:
                try {
                    logger.trace("Parsing number {}", value);
                    return new DecimalType(new BigDecimal(value));
                } catch (NumberFormatException e) {
                    // Log a warning and return UNDEF
                    logger.warn("Parsing number failed: {}. Returning nothing", e);
                    return UnDefType.UNDEF;
                }
        }
    }
}