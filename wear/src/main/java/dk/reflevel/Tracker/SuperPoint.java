package dk.reflevel.Tracker;

import org.json.JSONObject;

/**
 * Created by Ruifeng Shi on 4/29/2017.
 */

public class SuperPoint {
	public double latitude = 0;
	public double longitude = 0;

	public SuperPoint() {}

	public SuperPoint(double lat, double lng) {
		latitude = lat;
		longitude = lng;
	}

	public JSONObject encodeToJSON() {
		JSONObject result = new JSONObject();

		try {
			result.put("latitude", latitude);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		try {
			result.put("longitude", longitude);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	public void decodeFromJSON(JSONObject jsonObj) {
		try {
			latitude = jsonObj.optDouble("latitude", 0);
			longitude = jsonObj.optDouble("longitude", 0);
		} catch (Exception ex) {
			ex.printStackTrace();
			latitude = longitude = 0;
		}
	}
}
