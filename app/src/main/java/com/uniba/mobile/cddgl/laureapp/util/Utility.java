package com.uniba.mobile.cddgl.laureapp.util;

import android.content.Context;
import android.content.res.Resources;

import com.uniba.mobile.cddgl.laureapp.R;

public class Utility {

    public static int getNumberFromString(String inputString, int defaultValue) {
        if (inputString == null || inputString.isEmpty()) {
            return defaultValue;
        }

        StringBuilder numberString = new StringBuilder();
        boolean hasDecimalPoint = false;

        for (char c : inputString.toCharArray()) {
            if (Character.isDigit(c)) {
                numberString.append(c);
            } else if (c == '.' && !hasDecimalPoint) {
                numberString.append(c);
                hasDecimalPoint = true;
            }
        }

        if (numberString.length() > 0) {
            float floatValue = Float.parseFloat(numberString.toString());
            return Math.round(floatValue);
        } else {
            return defaultValue;
        }
    }

    public static String translateScope(Resources resources, String scope) {
        switch(scope) {
            case "Informatica":
            case "Information technology":
                return resources.getStringArray(R.array.ambiti)[0];
            case "Biologia":
            case "Biology":
                return resources.getStringArray(R.array.ambiti)[1];
            case "Astronomia":
            case "Astronomy":
                return resources.getStringArray(R.array.ambiti)[2];
            case "Arte":
            case "Art":
                return resources.getStringArray(R.array.ambiti)[3];
            case "Medicina":
            case "Medicine":
                return resources.getStringArray(R.array.ambiti)[4];
            case "Giurisprudenza":
            case "Jurisprudence":
                return resources.getStringArray(R.array.ambiti)[5];
            case "Finanza":
            case "Finance":
                return resources.getStringArray(R.array.ambiti)[6];
            case "Economia":
            case "Economy":
                return resources.getStringArray(R.array.ambiti)[7];
            case "Ingegneria":
            case "Engineering":
                return resources.getStringArray(R.array.ambiti)[8];
            default:
                return "";
        }
    }

}
