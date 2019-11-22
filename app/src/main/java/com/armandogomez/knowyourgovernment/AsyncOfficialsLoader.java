package com.armandogomez.knowyourgovernment;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class AsyncOfficialsLoader extends AsyncTask<String, Integer, String> {
	private final static String API_KEY = "AIzaSyDNAGUUhDePJFsYjHw6-ccTzci0uvPQWug";
	private final static String API_URL = " https://www.googleapis.com/civicinfo/v2/representatives?key=";
	private static final String TAG = "AsyncOfficialsLoader";

	private MainActivity mainActivity;
	private static List<GovernmentOfficial> officialList;
	private static String locationText;

	AsyncOfficialsLoader(MainActivity mainActivity){
		this.mainActivity = mainActivity;
	}

	@Override
	protected void onPostExecute(String s) {
		this.officialList = parseJson(s);
		mainActivity.updateOfficialList(officialList);
	}

	@Override
	protected String doInBackground(String... params) {
		String address = params[0];
		Uri repUri = Uri.parse(API_URL);
		String urlToUse = repUri.toString() + API_KEY + "&address=" + address;
		StringBuilder sb = new StringBuilder();
		try {
			URL url = new URL(urlToUse);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			Log.d(TAG, "doInBackground: ResponseCode: " + conn.getResponseCode());
			conn.setRequestMethod("GET");
			InputStream is = conn.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));

			String line;
			while((line = reader.readLine()) != null) {
				sb.append(line).append('\n');
			}
		} catch (Exception e) {
			Log.e(TAG, "doInBackground: ", e);
			return null;
		}
		return sb.toString();
	}

	private List<GovernmentOfficial> parseJson(String s){
		List<GovernmentOfficial> officialList = new ArrayList<>();
		try {
			JSONObject outerJson = new JSONObject(s);
			JSONObject normalizedInputObj = outerJson.getJSONObject("normalizedInput");
			String city = normalizedInputObj.optString("city");
			String state = normalizedInputObj.optString("state");
			String zip = normalizedInputObj.optString("zip");

			StringBuilder sb = new StringBuilder();
			if(!city.isEmpty()) {
				sb.append(city + ", ");
			}
			if(!state.isEmpty()) {
				sb.append(state);
			}
			if(!zip.isEmpty()) {
				sb.append(" ");
				sb.append(zip);
			}

			this.locationText = sb.toString();

			JSONArray offices = outerJson.getJSONArray("offices");
			for(int i=0; i < offices.length(); i++) {
				String office = offices.getJSONObject(i).getString("name");
				JSONArray officialIndices = offices.getJSONObject(i).getJSONArray("officialIndices");
				for(int j = 0; j < officialIndices.length(); j++) {
					int index = officialIndices.getInt(j);
					GovernmentOfficial g = new GovernmentOfficial();
					JSONObject officialObj = outerJson.getJSONArray("officials").getJSONObject(index);
					g.setOffice(office);
					g.setName(officialObj.getString("name"));

					if(officialObj.optJSONArray("address") != null) {
						JSONObject addressObj = officialObj.optJSONArray("address").getJSONObject(0);
						sb = new StringBuilder();
						sb.append(addressObj.optString("line1"));
						sb.append("\n");
						if(addressObj.optString("line2").length() > 0) {
							sb.append(addressObj.optString("line2"));
							sb.append("\n");
						}
						if(addressObj.optString("line3").length() > 0) {
							sb.append(addressObj.optString("line3"));
							sb.append("\n");
						}
						sb.append(addressObj.optString("city"));
						sb.append(", ");
						sb.append(addressObj.optString("state"));
						sb.append(" ");
						sb.append(addressObj.optString("zip"));
						g.setAddress(sb.toString());
					}

					g.setParty(officialObj.optString("party", "Unknown"));

					if(officialObj.optJSONArray("phones") != null){
						String phone = officialObj.optJSONArray("phones").optString(0);
						if(phone.length() > 0) {
							g.setPhone(phone);
						}
					}

					if(officialObj.optJSONArray("urls") != null){
						String url = officialObj.optJSONArray("urls").optString(0);
						if(url.length() > 0) {
							g.setWebsite(url);
						}
					}

					if(officialObj.optJSONArray("emails") != null) {
						String email = officialObj.optJSONArray("emails").optString(0);
						if(email.length() > 0) {
							g.setEmail(email);
						}
					}

					g.setPhotoUrl(officialObj.optString("photoUrl"));

					if(officialObj.optJSONArray("channels") != null) {
						JSONArray channelsArray = officialObj.getJSONArray("channels");
						for(int k = 0 ; k < channelsArray.length(); k++) {
							JSONObject jsonObject = channelsArray.getJSONObject(k);
							String type = jsonObject.optString("type");
							String id = jsonObject.optString("id");
							g.setSocial(type, id);
						}
					}
					officialList.add(g);
				}
			}
			return officialList;
		} catch (Exception e) {
			Log.d(TAG, "parseOuterJSON: " + e.getMessage());
			e.printStackTrace();
		}

		return null;
	}

	public static String getLocationText() {
		return locationText;
	}

	public static GovernmentOfficial getOfficial(int index) {
		return officialList.get(index);
	}

}
