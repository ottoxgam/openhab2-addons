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

/**
 * The {@link purpleairConfig} class contains fields mapping thing configuration parameters.
 *
 * @author Zack Lapinski - Initial contribution
 */
package org.openhab.binding.purpleair.internal;

public class purpleairFunctions {
    public static Number aqiFromPM(float pm) {

        if (Double.isNaN(Double.parseDouble(Float.toString(pm)))) {
            return null;
        }
        if ((Float.toString(pm)) == null) {
            return null;
        }
        if (pm < 0) {
            return pm;
        }
        if (pm > 1000) {
            return null;
        }
        /*
         * Good 0 - 50 0.0 - 15.0 0.0 – 12.0
         * Moderate 51 - 100 >15.0 - 40 12.1 – 35.4
         * Unhealthy for Sensitive Groups 101 – 150 >40 – 65 35.5 – 55.4
         * Unhealthy 151 – 200 > 65 – 150 55.5 – 150.4
         * Very Unhealthy 201 – 300 > 150 – 250 150.5 – 250.4
         * Hazardous 301 – 400 > 250 – 350 250.5 – 350.4
         * Hazardous 401 – 500 > 350 – 500 350.5 – 500
         */
        if (pm > 350.5) {
            return calcAQI(pm, 500, 401, 500, 350.5);
        } else if (pm > 250.5) {
            return calcAQI(pm, 400, 301, 350.4, 250.5);
        } else if (pm > 150.5) {
            return calcAQI(pm, 300, 201, 250.4, 150.5);
        } else if (pm > 55.5) {
            return calcAQI(pm, 200, 151, 150.4, 55.5);
        } else if (pm > 35.5) {
            return calcAQI(pm, 150, 101, 55.4, 35.5);
        } else if (pm > 12.1) {
            return calcAQI(pm, 100, 51, 35.4, 12.1);
        } else if (pm >= 0) {
            return calcAQI(pm, 50, 0, 12, 0);
        } else {
            return null;
        }

    }

    public int bplFromPM(int pm) {
        if (Double.isNaN(Double.parseDouble(Float.toString(pm)))) {
            return 0;
        }
        if ((Float.toString(pm)) == null) {
            return 0;
        }
        if (pm < 0) {
            return 0;
        }
        /*
         * Good 0 - 50 0.0 - 15.0 0.0 – 12.0
         * Moderate 51 - 100 >15.0 - 40 12.1 – 35.4
         * Unhealthy for Sensitive Groups 101 – 150 >40 – 65 35.5 – 55.4
         * Unhealthy 151 – 200 > 65 – 150 55.5 – 150.4
         * Very Unhealthy 201 – 300 > 150 – 250 150.5 – 250.4
         * Hazardous 301 – 400 > 250 – 350 250.5 – 350.4
         * Hazardous 401 – 500 > 350 – 500 350.5 – 500
         */
        if (pm > 350.5) {
            return 401;
        } else if (pm > 250.5) {
            return 301;
        } else if (pm > 150.5) {
            return 201;
        } else if (pm > 55.5) {
            return 151;
        } else if (pm > 35.5) {
            return 101;
        } else if (pm > 12.1) {
            return 51;
        } else if (pm >= 0) {
            return 0;
        } else {
            return 0;
        }

    }

    public int bphFromPM(float pm) {
        // return 0;
        if (Double.isNaN(Double.parseDouble(Float.toString(pm)))) {
            return 0;
        }
        if ((Float.toString(pm)) == null) {
            return 0;
        }
        if (pm < 0) {
            return 0;
        }
        /*
         * Good 0 - 50 0.0 - 15.0 0.0 – 12.0
         * Moderate 51 - 100 >15.0 - 40 12.1 – 35.4
         * Unhealthy for Sensitive Groups 101 – 150 >40 – 65 35.5 – 55.4
         * Unhealthy 151 – 200 > 65 – 150 55.5 – 150.4
         * Very Unhealthy 201 – 300 > 150 – 250 150.5 – 250.4
         * Hazardous 301 – 400 > 250 – 350 250.5 – 350.4
         * Hazardous 401 – 500 > 350 – 500 350.5 – 500
         */
        if (pm > 350.5) {
            return 500;
        } else if (pm > 250.5) {
            return 500;
        } else if (pm > 150.5) {
            return 300;
        } else if (pm > 55.5) {
            return 200;
        } else if (pm > 35.5) {
            return 150;
        } else if (pm > 12.1) {
            return 100;
        } else if (pm >= 0) {
            return 50;
        } else {
            return 0;
        }

    }

    public static int calcAQI(float Cp, int Ih, int Il, double BPh, double BPl) {

        float a = (Ih - Il);
        float b = (float) (BPh - BPl);
        float c = (float) (Cp - BPl);
        return Math.round((a / b) * c + Il);

    }

    public static String getAQIDescription(int aqi) {
        if (aqi >= 401) {
            return "Hazardous";
        } else if (aqi >= 301) {
            return "Hazardous";
        } else if (aqi >= 201) {
            return "Very Unhealthy";
        } else if (aqi >= 151) {
            return "Unhealthy";
        } else if (aqi >= 101) {
            return "Unhealthy for Sensitive Groups";
        } else if (aqi >= 51) {
            return "Moderate";
        } else if (aqi >= 0) {
            return "Good";
        } else {
            return "undefined";
        }
    }

    public static String getAQIMessage(float aqi) {
        if (aqi >= 401) {
            return ">401: Health alert: everyone may experience more serious health effects";
        } else if (aqi >= 301) {
            return "301-400: Health alert: everyone may experience more serious health effects";
        } else if (aqi >= 201) {
            return "201-300: Health warnings of emergency conditions. The entire population is more likely to be affected. ";
        } else if (aqi >= 151) {
            return "151-200: Everyone may begin to experience health effects; members of sensitive groups may experience more serious health effects.";
        } else if (aqi >= 101) {
            return "101-150: Members of sensitive groups may experience health effects. The general public is not likely to be affected.";
        } else if (aqi >= 51) {
            return "51-100: Air quality is acceptable; however, for some pollutants there may be a moderate health concern for a very small number of people who are unusually sensitive to air pollution.";
        } else if (aqi >= 0) {
            return "0-50: Air quality is considered satisfactory, and air pollution poses little or no risk";
        } else {
            return null;
        }
    }
}