package com.tidemedia.nntv.util;

import java.lang.reflect.Type;
import java.util.Date;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.DateTypeAdapter;

public class PreferencesUtil {
	private static Gson mGson = new GsonBuilder().registerTypeAdapter(Date.class, new DateTypeAdapter()).create();

	public static void savePreference(Context context,String preferenceType,String preferenceKey,String preferenceValue){
		SharedPreferences preference = context.getSharedPreferences(preferenceType,Context.MODE_PRIVATE);
        Editor edit = preference.edit();
        edit.putString(preferenceKey,preferenceValue);
        edit.commit();
	}

	public static String getPreference(Context context,String preferenceType,String preferenceKey){
		SharedPreferences preference = context.getSharedPreferences(preferenceType,Context.MODE_PRIVATE);
		return preference.getString(preferenceKey,"");
	}

	public static void clearPreference(Context context,String preferenceType,String preferenceKey){
		SharedPreferences preference = context.getSharedPreferences(preferenceType,Context.MODE_PRIVATE);
        Editor edit = preference.edit();
        edit.putString(preferenceKey,"");
        edit.commit();   
	}
	

	public static <T> void  saveByJson(Context context,String preferenceType,String key,T value){
		String strValue;
		if(value == null)
			strValue = "";
		else if(value instanceof String)
			strValue = (String)value;
		else
			strValue = mGson.toJson(value);
		SharedPreferences preference = context.getSharedPreferences(preferenceType,Context.MODE_PRIVATE);
        preference.edit().putString(key,strValue).commit();
	}

	public static <T> T getByJson(Context context,String preferenceType,String key,Type dataType){
		SharedPreferences preference = context.getSharedPreferences(preferenceType,Context.MODE_PRIVATE);		
		String strValue = preference.getString(key,"");
		if(StringUtil.isNotEmpty(strValue))
			return mGson.fromJson(strValue, dataType);	
		else
			return null;
		}

	public static void clearPreference(Context context,String preferenceType){
		SharedPreferences preference = context.getSharedPreferences(preferenceType,Context.MODE_PRIVATE);
        Editor edit = preference.edit();
        edit.clear();
        edit.commit();   
	}
	
	
}
