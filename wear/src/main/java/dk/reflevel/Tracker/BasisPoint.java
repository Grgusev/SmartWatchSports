package dk.reflevel.Tracker;

import org.json.JSONObject;

/**
 * Created by Ruifeng Shi on 4/29/2017.
 */

public class BasisPoint extends SuperPoint {
	public int index = -1;


	public BasisPoint() {
	}

	public BasisPoint(double lat, double lng, int index) {
		this.latitude = lat;
		this.longitude = lng;
		this.index = index;
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
		try {
			result.put("index", index);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	public void decodeFromJSON(JSONObject jsonObj) {
		try {
			latitude = jsonObj.optDouble("latitude", 0);
			longitude = jsonObj.optDouble("longitude", 0);
			index = jsonObj.optInt("index", -1);
		} catch (Exception ex) {
			ex.printStackTrace();
			latitude = longitude = 0;
			index = -1;
		}
	}
}
