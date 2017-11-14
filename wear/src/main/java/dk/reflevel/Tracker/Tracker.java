package dk.reflevel.Tracker;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;
import java.util.zip.Deflater;

/**
 * Created by Ruifeng Shi on 4/27/2017.
 */

public class Tracker {
	private static final	String			TAG							= "Tracker";
	private static final	String			SHARED_PREF					= "Shared_Pref_Tracker";
	public static final		String			LOCATION_PERMISSION			= Manifest.permission.ACCESS_FINE_LOCATION;

	public static final		String			ZIP_LENGTH					= "/zip_len";
	public static final		String			ZIP_DATA					= "/zip_data";
	public static final		String			ZIP_LAST_DATA				= "/zip_last_data";

	public static final		String			SYNC_KEY					= "sync_data";
	public static final		String			BASIS_LOCATION_KEY			= "basis_location";
	public static final		String			LOCATION_POINTS_KEY			= "location_data";

	private		FragmentActivity			activityInstance			= null;
	private	static	GoogleApiClient			googleApiClient				= null;


	private static final String				STATE_KEY					= "Take Points Key";
	public static final int					STATE_TAKE_BASIS_POINT		= 0;
	public static final int					STATE_START_TRACK			= 1;
	public static final int					STATE_STOP_TRACK			= 2;
	public static final int					STATE_SYNC					= 3;

	private static final boolean			isTest						= false;


	private static Tracker sharedInstance = null;
	public static void initInstance(FragmentActivity activityInstace) {
		if (sharedInstance == null)
			sharedInstance = new Tracker(activityInstace);
	}

	public static Tracker getSharedInstance() {
		return sharedInstance;
	}

	public Tracker(FragmentActivity activity) {
		this.activityInstance = activity;
	}


	public void connectGoogleApiClient(FragmentActivity activity) {
		if (googleApiClient != null && googleApiClient.isConnected())
			return;

		googleApiClient = new GoogleApiClient.Builder(activity)
				.addConnectionCallbacks(connectionCallbacks)
				.addApi(Wearable.API)
				.enableAutoManage(activity, new GoogleApiClient.OnConnectionFailedListener() {
					@Override
					public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
						Log.e(TAG, "Connection Failed : " + connectionResult.getErrorCode());
					}
				})
				.build();
		googleApiClient.connect();
	}


	private static SharedPreferences getSharedPreferences(Context context) {
		SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
		return sharedPref;
	}


	public static void saveTakePointsState(Context context, int state) {
		SharedPreferences pref = getSharedPreferences(context);
		SharedPreferences.Editor editor = pref.edit();
		editor.putInt(STATE_KEY, state);
		editor.apply();
	}

	public static int loadTakePointsState(Context context) {
		SharedPreferences pref = getSharedPreferences(context);
		return pref.getInt(STATE_KEY, STATE_TAKE_BASIS_POINT);
	}


	public static void resetState(Context context) {
		saveTakePointsState(context, STATE_TAKE_BASIS_POINT);
		LocationDBHelper.getInstance(context).clearDB();
	}


	public static void clearBasisPoints(Context context) {
		saveBasisLocation(context, null, 0);
		saveBasisLocation(context, null, 1);
		saveBasisLocation(context, null, 2);
	}


	/**
	 * Save the basis location
	 *
	 * @param index Starts from 0
	 * @return
	 */
	public static boolean saveBasisLocation(Context context, BasisPoint basisPoint, int index) {
		Log.w(TAG, "saveBasisLocation : " + index);

		if (basisPoint != null)
			basisPoint.index = index;

		SharedPreferences sharedPref = getSharedPreferences(context);

		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString("basis_point" + index, basisPoint == null ? "" : basisPoint.encodeToJSON().toString());
		editor.apply();

		return true;
	}


	/**
	 * Load the basis location
	 *
	 * @param index Starts from 0
	 * @return
	 */
	public static BasisPoint loadBasisLocation(Context context, int index) {
		SharedPreferences sharedPref = getSharedPreferences(context);
		String pointString = sharedPref.getString("basis_point" + index, "");

		JSONObject jsonPoint;
		try {
			jsonPoint = new JSONObject(pointString);

			Log.w(TAG, "loadBasisLocation : " + index);

			BasisPoint result = new BasisPoint();
			result.decodeFromJSON(jsonPoint);

			return result;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}


	public static boolean startSync(FragmentActivity context) {
		if (googleApiClient != null && googleApiClient.isConnected()) {
			googleApiClient.stopAutoManage(context);
			googleApiClient.disconnect();
			googleApiClient = null;
		}

		getSharedInstance().connectGoogleApiClient(context);
		return true;
	}



	public void startTrackingLocation() {
		Log.w(TAG, "startTrackingLocation==========");
		Intent intent = new Intent(activityInstance, LocationUpdateService.class);
		intent.putExtra("start_tracking", 1);

		saveTakePointsState(activityInstance, STATE_STOP_TRACK);

		activityInstance.startService(intent);
	}


	public void stopTrackingLocation() {
		Log.w(TAG, "stopTrackingLocation==========");
		Intent intent = new Intent(activityInstance, LocationUpdateService.class);
		intent.putExtra("start_tracking", 0);

		saveTakePointsState(activityInstance, STATE_SYNC);

		activityInstance.startService(intent);
	}

	/**
	 * Should be called to start tracking
	 */
	public void startLocationService() {
		Log.w(TAG, "startLocationService==========");
		activityInstance.startService(new Intent(activityInstance, LocationUpdateService.class));
	}



	/**
	 * Should be called to stop tracking
	 */
	public void stopLocationService() {
		Log.w(TAG, "stopLocationService==========");
		activityInstance.stopService(new Intent(activityInstance, LocationUpdateService.class));
	}


	private GoogleApiClient.ConnectionCallbacks connectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {
		@Override
		public void onConnected(@Nullable Bundle bundle) {
			Log.w(TAG, "Connected google api client for wearable api");
			sync(activityInstance);
		}
		@Override
		public void onConnectionSuspended(int i) {
			Log.w(TAG, "Connection suspended for wearable api");
		}
	};

	private void sync(Context context) {
		JSONObject jsonData = new JSONObject();
		try {
			BasisPoint point1 = new BasisPoint(56.1123826, 10.1932803, 0);
			BasisPoint point2 = new BasisPoint(56.1127859, 10.196908, 1);
			BasisPoint point3 = new BasisPoint(56.1121205, 10.1937114, 2);

			if (!isTest) {
				jsonData.put(BASIS_LOCATION_KEY + "0", loadBasisLocation(context, 0).encodeToJSON());
				jsonData.put(BASIS_LOCATION_KEY + "1", loadBasisLocation(context, 1).encodeToJSON());
				jsonData.put(BASIS_LOCATION_KEY + "2", loadBasisLocation(context, 2).encodeToJSON());
			} else {
				jsonData.put(BASIS_LOCATION_KEY + "0", point1.encodeToJSON());
				jsonData.put(BASIS_LOCATION_KEY + "1", point2.encodeToJSON());
				jsonData.put(BASIS_LOCATION_KEY + "2", point3.encodeToJSON());
			}

			ArrayList<LocationVo> pointsArray;

			if (!isTest) {
				pointsArray = LocationDBHelper.getInstance(context).getAllLocationLatLongDetails();
			} else {
				Random random = new Random();

				pointsArray = new ArrayList<>();
				for (int i = 0; i < 100; i++) {
					LocationVo point = new LocationVo();

					double lat = random.nextInt(1000) / 1000000.0 + 56.1121205;
					double lng = random.nextInt(1000) / 1000000.0 + 10.1932803;

					point.setLatitude(lat);
					point.setLongitude(lng);

					pointsArray.add(point);
				}
			}

			Toast.makeText(context, "Total count : " + pointsArray.size(), Toast.LENGTH_SHORT).show();

			JSONArray jsonPoints = new JSONArray();
			for (int i = 0; i < pointsArray.size(); i++) {
				LocationVo locVoItem = pointsArray.get(i);

				SuperPoint pt = new SuperPoint(locVoItem.getLatitude(), locVoItem.getLongitude());
				jsonPoints.put(pt.encodeToJSON());
			}
			jsonData.put(LOCATION_POINTS_KEY, jsonPoints);
		} catch (Exception ex) {
			ex.printStackTrace();
			Toast.makeText(context, "JSON Encoding failed", Toast.LENGTH_SHORT).show();
			return;
		}

		String result = jsonData.toString();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		int totalLen = 0;

		try {
			byte[] resultBytes = result.getBytes();
			totalLen = resultBytes.length;

			Deflater deflater = new Deflater();
			deflater.setInput(resultBytes);
			deflater.finish();

			byte[] buffer = new byte[1024];
			while (!deflater.finished()) {
				int count = deflater.deflate(buffer);
				outputStream.write(buffer, 0, count);
			}
			try {
				outputStream.close();
			} catch (Exception ex) {
				ex.printStackTrace();
				Toast.makeText(context, "Output stream failed--0", Toast.LENGTH_SHORT).show();
				return;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			Toast.makeText(context, "Output stream failed--1", Toast.LENGTH_SHORT).show();
			return;
		}

		ByteArrayOutputStream resultStream = new ByteArrayOutputStream();

		try {
			byte[] lenBytes = new byte[4];
			lenBytes[0] = (byte)(totalLen / 0x1000000);
			lenBytes[1] = (byte)((totalLen / 0x10000) % 0x100);
			lenBytes[2] = (byte)((totalLen / 0x100) % 0x100);
			lenBytes[3] = (byte)(totalLen % 0x100);

			resultStream.write(lenBytes);
			resultStream.write(outputStream.toByteArray());

			resultStream.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			Toast.makeText(context, "Output stream failed--2", Toast.LENGTH_SHORT).show();
			return;
		}

		byte[] zipBytes = resultStream.toByteArray();

		Log.w(TAG, "Data Sync. Org Length : " + result.length() + ", Zip Length : " + (zipBytes.length - 4));

		// Creating data trasfer events
		{
			// Create event for ziped length
			int len = totalLen + 4;

			PutDataMapRequest putDataMapReq = PutDataMapRequest.create(ZIP_LENGTH);
			putDataMapReq.getDataMap().putInt(SYNC_KEY, len);
			putDataMapReq.getDataMap().putLong("time", new Date().getTime());
			PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
			PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi.putDataItem(googleApiClient, putDataReq);

			// Create event for pile of data(50KB)
			int pileSize = 50 * 1024;
			int targetLen = zipBytes.length;
			int remainingSize = zipBytes.length;

			while (remainingSize > 0) {
				if (remainingSize <= pileSize) {
					putDataMapReq = PutDataMapRequest.create(ZIP_LAST_DATA);
					putDataMapReq.getDataMap().putByteArray(SYNC_KEY, Arrays.copyOfRange(zipBytes, targetLen - remainingSize, targetLen));
					putDataMapReq.getDataMap().putLong("time", new Date().getTime());

					remainingSize = 0;
				} else {
					putDataMapReq = PutDataMapRequest.create(ZIP_DATA);
					putDataMapReq.getDataMap().putByteArray(SYNC_KEY, Arrays.copyOfRange(zipBytes, targetLen - remainingSize, targetLen - remainingSize + pileSize));
					putDataMapReq.getDataMap().putLong("time", new Date().getTime());

					remainingSize -= pileSize;
				}

				putDataReq = putDataMapReq.asPutDataRequest();
				Wearable.DataApi.putDataItem(googleApiClient, putDataReq);
			}
		}

		// Clear Basis Locations
		Log.w(TAG, "Basis locations are cleard");
	}
}
