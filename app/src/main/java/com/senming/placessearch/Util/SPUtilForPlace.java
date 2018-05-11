package com.senming.placessearch.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.senming.placessearch.DataObjects.PlaceResult;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

public class SPUtilForPlace {

    public static final String FILE_NAME = "Favorites";

    public static void saveFavoritePlace(Context context, String placeId, PlaceResult place) throws Exception {
        if(place instanceof Serializable) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(FILE_NAME, context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(place);
                String temp = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
                editor.putString(placeId, temp);
                editor.commit();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            throw new Exception("PlaceResults must implements Serializable");
        }
    }

    public static PlaceResult getFavoritePlace(Context context, String placeId) {
        SharedPreferences sharedPreferences=context.getSharedPreferences(FILE_NAME,context.MODE_PRIVATE);
        String temp = sharedPreferences.getString(placeId, "");
        ByteArrayInputStream bais =  new ByteArrayInputStream(Base64.decode(temp.getBytes(), Base64.DEFAULT));
        PlaceResult place = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(bais);
            place = (PlaceResult) ois.readObject();
        } catch (IOException e) {
        }catch(ClassNotFoundException e1) {

        }
        return place;
    }

    public static void removeFavoritePlace(Context context, String placeId) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(placeId);
        editor.commit();
    }

    public static Map<String, ?> getAllFavoritePlacesMap(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, context.MODE_PRIVATE);
        Map<String, ?> map = sp.getAll();
        return map;
    }

    public static boolean contains(Context context, String placeId) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, context.MODE_PRIVATE);
        return sp.contains(placeId);
    }
}
