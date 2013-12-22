package com.hit3.android.terrealted3;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.hit3.android.terrealted3.utilities.JSONUtilities;


public class Functions {

	
	public static boolean isAuthenticated(Context context) {
		boolean result = false;
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		String jsonLogin = preferences.getString("prefLogin", "");
		if (StringUtils.isNotEmpty(jsonLogin)) {
			Map<String, String> map = JSONUtilities.jsonToMap(jsonLogin);
			String statusCode = map.get(Constant.LOGIN_STATUS_CODE);
			if (StringUtils.equals(statusCode, "200")) {
				String roles = map.get(Constant.LOGIN_ROLES);
				result = true;
			}
		}
		return result;
	}
	
	
	public static List<Integer> getAuthenticatedGroupId(Context context) {
		List<Integer> result = new ArrayList<Integer>();
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		String jsonLogin = preferences.getString("prefLogin", "");
		if (StringUtils.isNotEmpty(jsonLogin)) {
			Map<String, String> map = JSONUtilities.jsonToMap(jsonLogin);
			String statusCode = map.get(Constant.LOGIN_STATUS_CODE);
			if (StringUtils.equals(statusCode, "200")) {
				String roles = map.get(Constant.LOGIN_ROLES);
				roles = StringUtils.remove(roles, "{");
				roles = StringUtils.remove(roles, "}");
				String[] array = StringUtils.split(roles, ",");
				for (String item : array) {
					result.add(new Integer(StringUtils.substringBefore(item, "=").toString().trim()).intValue());
				}
			}
		}
		return result;
	}

}
