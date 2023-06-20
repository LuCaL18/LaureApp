package com.uniba.mobile.cddgl.laureapp.util;

import static com.uniba.mobile.cddgl.laureapp.ui.tesi.ClassificaTesiFragment.SHARED_PREFS_NAME;
import static com.uniba.mobile.cddgl.laureapp.ui.tesi.ClassificaTesiFragment.TESI_LIST_KEY_PREF;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.CoRelatorPermissions;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static ArrayList<String> getTesiList(Context context) {
        try {
            SharedPreferences sp = context.getSharedPreferences(SHARED_PREFS_NAME, Activity.MODE_PRIVATE);
            String listJson = sp.getString(TESI_LIST_KEY_PREF, null);

            // Converti la stringa JSON nella mappa originale
            if (listJson != null) {
                Gson gson = new Gson();
                Type type = new TypeToken<List<String>>() {
                }.getType();

                return gson.fromJson(listJson, type);
            }
        } catch (Exception e) {
            Log.e("getTesiList", e.getMessage());
        }
        return new ArrayList<>();
    }

    public static Map<String, Object> getObjectProperties(Object obj) {
        Map<String, Object> properties = new HashMap<>();

        // Ottenere la classe dell'oggetto
        Class<?> clazz = obj.getClass();

        // Ottenere tutti i campi della classe, inclusi quelli ereditati
        Field[] fields = clazz.getDeclaredFields();

        // Iterare sui campi e recuperare le proprietà
        for (Field field : fields) {
            field.setAccessible(true); // Per accedere ai campi privati

            String propertyName = field.getName();
            Object propertyValue = null;

            try {
                propertyValue = field.get(obj);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            // Aggiungere la proprietà alla mappa
            properties.put(propertyName, propertyValue);
        }

        return properties;
    }

    public static String translatePermissionFromEnum(Resources resources, CoRelatorPermissions permission) {
        switch(permission) {
            case EDIT_SEARCH_KEYS:
                return resources.getString(R.string.edit_search_keys);
            case EDIT_CONSTRAINTS:
                return resources.getString(R.string.edit_constraints);
            case EDIT_DOCUMENTS:
                return resources.getString(R.string.edit_documents);
            case EDIT_NOTES:
                return resources.getString(R.string.edit_notes);
            default:
                return "";
        }
    }
}
