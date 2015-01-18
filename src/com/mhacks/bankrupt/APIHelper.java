package com.mhacks.bankrupt;

import android.util.Log;

import org.json.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

/**
 * Created by ktomega on 1/18/15.
 */
public class APIHelper {
	private static final String API_ROOT = "http://localhost:8000";

	public static JSONObject getEndpoint(String endpoint) {
		try {
			URL url = new URL(API_ROOT + endpoint);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					url.openStream()));

			while (!br.ready()) {
			}

			String data = "", input;
			while ((input = br.readLine()) != "") {
				data += input;
			}

			return new JSONObject(data);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static void levelPrint(String str, int level) {
		String delim = "  ";
		String out = "";
		for (int i = 0; i < level; i++)
			out += delim;
		out += str;
		Log.e("LOGSHIT", out);
	}

	public static void printJSON(JSONObject arr, int level) {
		if (arr == null)
			return;

		Iterator<String> keys = arr.keys();
		do {
			try {
				Object next = arr.get(keys.next());
				if (next.getClass() == JSONObject.class) {
					levelPrint("{", level);
					printJSON((JSONObject) next, level + 1);
					levelPrint("}", level);
				} else if (next.getClass() == JSONArray.class) {
					JSONArray n = (JSONArray) next;
					levelPrint("[", level);
					printJSON(n, level + 1);
					levelPrint("]", level);
				} else {
					levelPrint(next.toString(), level);
				}
			} catch (Exception e) {

			}
		} while (keys.hasNext());
	}

	public static void printJSON(JSONArray arr, int level) {
		if (arr == null)
			return;

		for (int i = 0; i < arr.length(); i++) {
			try {
				Object next = arr.get(i);

				if (next.getClass() == JSONObject.class) {
					levelPrint("{", level);
					printJSON((JSONObject) next, level + 1);
					levelPrint("}", level);
				} else if (next.getClass() == JSONArray.class) {
					JSONArray n = (JSONArray) next;
					levelPrint("[", level);
					printJSON(n, level + 1);
					levelPrint("]", level);
				} else {
					levelPrint(next.toString(), level);
				}
			} catch (Exception e) {

			}
		}
	}
}