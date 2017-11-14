package dk.reflevel;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.ArrayMap;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.zip.Inflater;

import ca.hss.heatmaplib.HeatMap;
import dk.reflevel.MathUtils.PolyUtil;
import dk.reflevel.MathUtils.SphericalUtil;

/**
 * Created by Ruifeng Shi on 4/27/2017.
 */

public class FirstScreenActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks {
	private final String TAG = "FirstScreenActivity";
	private GoogleApiClient mGoogleApiClient = null;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_first_screen);

		// Added by Heather
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addApi(Wearable.API)
				.addConnectionCallbacks(this)
				.enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
					@Override
					public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
						Toast.makeText(FirstScreenActivity.this, "Connection failed : " + connectionResult.getErrorCode(), Toast.LENGTH_SHORT).show();
					}
				})
				.build();

		mapView = (HeatMap) findViewById(R.id.heatMapView);
		{
			mapView.setMinimum(0);
			mapView.setMaximum(100);
			mapView.setRadius(500);

			Map<Float, Integer> colorStops = new ArrayMap<>();
			colorStops.put(0.0f, 0xFFFFFF00);
			colorStops.put(1.0f, 0xFFFF0000);
			mapView.setColorStops(colorStops);

			mapView.setMaximumOpactity(255);
			mapView.setMinimumOpactity(0);
		}

//		Handler handler = new Handler();
//		handler.postDelayed(new Runnable() {
//			@Override
//			public void run() {
//				Log.w(TAG, "Width must be bigger than 0 : " + mapView.getWidth());
//				basisPoints.add(new BasisPoint(0, 0, 0));
//				basisPoints.add(new BasisPoint(0.0001, 0, 1));
//				basisPoints.add(new BasisPoint(0, 0.0001, 2));
//
//				Random rand = new Random();
//				for (int i = 0; i < 1000; i++) {
//					pointsArray.add(new SuperPoint(clamp(rand.nextFloat(), 0.0f, 0.0001), clamp(rand.nextFloat(), 0.0f, 0.0001)));
//				}
//
//				showOnMap();
//			}
//		}, 1000);
	}


	private double clamp(double value, double min, double max) {
		return value * (max - min) + min;
	}


	private int totalLen = 0;
	private ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

	private ArrayList<BasisPoint> basisPoints = new ArrayList<>();
	private ArrayList<SuperPoint> pointsArray = new ArrayList<>();


	private DataApi.DataListener dataListener = new DataApi.DataListener() {
		@Override
		public void onDataChanged(DataEventBuffer dataEventBuffer) {
			for (DataEvent event : dataEventBuffer) {
				if (event.getType() == DataEvent.TYPE_CHANGED) {
					DataItem item = event.getDataItem();
					if (item.getUri().getPath().compareTo("/zip_len") == 0) {
						DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
						totalLen = dataMap.getInt("sync_data");

						if (byteStream != null) {
							try {
								byteStream.close();
							} catch (Exception ex) {
								ex.printStackTrace();
							}
						}

						byteStream = new ByteArrayOutputStream();

						Log.w(TAG, "Zip bytes length : " + totalLen);
						Toast.makeText(FirstScreenActivity.this, "Zip bytes length : " + totalLen, Toast.LENGTH_SHORT).show();
					} else if (item.getUri().getPath().compareTo("/zip_data") == 0) {
						DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();

						try {
							byteStream.write(dataMap.getByteArray("sync_data"));
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					} else if (item.getUri().getPath().compareTo("/zip_last_data") == 0) {
						DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();

						try {
							byteStream.write(dataMap.getByteArray("sync_data"));
							byteStream.close();
						} catch (Exception ex) {
							ex.printStackTrace();
						}

						// Data transfer is finished. Need to parse data
						parseData();
					}
				} else if (event.getType() == DataEvent.TYPE_DELETED) {
					Toast.makeText(FirstScreenActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
				}
			}
		}
	};



	private void parseData() {
		basisPoints.clear();
		pointsArray.clear();

		try { byteStream.close(); } catch (Exception ex) { ex.printStackTrace(); }

		byte[] bytesArray = byteStream.toByteArray();
		if (totalLen == 0 || bytesArray.length == 0) {
			Log.e(TAG, "Data transfer error");
			Toast.makeText(FirstScreenActivity.this, "Data transfer error", Toast.LENGTH_SHORT).show();
			return;
		}

		int lenByte1 = bytesArray[0] < 0 ? 256 + bytesArray[0] : bytesArray[0];
		int lenByte2 = bytesArray[1] < 0 ? 256 + bytesArray[1] : bytesArray[1];
		int lenByte3 = bytesArray[2] < 0 ? 256 + bytesArray[2] : bytesArray[2];
		int lenByte4 = bytesArray[3] < 0 ? 256 + bytesArray[3] : bytesArray[3];

		int orgSize = lenByte1 * 0x1000000 + lenByte2 * 0x10000 + lenByte3 * 0x100 + lenByte4;
		Log.w(TAG, "Org size calculated : " + orgSize);

		byte[] orgBytesArray = new byte[orgSize];

		try {
			Inflater inflater = new Inflater();
			inflater.setInput(bytesArray, 4, bytesArray.length - 4);

			int inflateCount = inflater.inflate(orgBytesArray);
			inflater.end();
			if (inflateCount != orgSize) {
				Log.e(TAG, "Data length error : " + inflateCount + ", org size : " + orgSize);
				Toast.makeText(FirstScreenActivity.this, "Data length error : " + inflateCount + ", org size : " + orgSize, Toast.LENGTH_SHORT).show();
				throw new Exception("Data error");
			}
		} catch (Exception ex) {
			Log.e(TAG, "Data parse error : " + ex.getMessage());
			Toast.makeText(FirstScreenActivity.this, "Data parse error : " + ex.getMessage(), Toast.LENGTH_SHORT).show();
		}

		try {
			String jsonString = new String(orgBytesArray, "UTF-8");
			JSONObject jsonObject = new JSONObject(jsonString);

			// Get basis points
			for (int i = 0; i < 3; i++) {
				JSONObject basisLocation = jsonObject.getJSONObject("basis_location" + i);

				BasisPoint basisPoint = new BasisPoint();
				basisPoint.decodeFromJSON(basisLocation);

				if (basisPoint.index < 0) {
					Log.e(TAG, "No BasisPoint : " + i);
					Toast.makeText(FirstScreenActivity.this, "No BasisPoint selected. Index : " + i, Toast.LENGTH_SHORT).show();
					return;
				}

				basisPoints.add(basisPoint);

				Log.w(TAG, "Basis Point : (" + basisPoint.latitude + ", " + basisPoint.longitude + ")");
			}

			// Get points array
			JSONArray jsonPointsArray = jsonObject.getJSONArray("location_data");
			for (int i = 0; i < jsonPointsArray.length(); i++) {
				JSONObject pointItem = jsonPointsArray.getJSONObject(i);

				SuperPoint point = new SuperPoint();
				point.decodeFromJSON(pointItem);

				pointsArray.add(point);
			}

			Log.w(TAG, "Parse Finished : " + pointsArray.size() + ", " + basisPoints.size());
			Toast.makeText(FirstScreenActivity.this, "Parse success", Toast.LENGTH_SHORT).show();

			showOnMap();
		} catch (Exception ex) {
			Log.e(TAG, "JSON parse error : " + ex.getMessage());
			Toast.makeText(FirstScreenActivity.this, "JSON parse error", Toast.LENGTH_SHORT).show();
		}
	}



	private double width = 0, height = 0;
	private HeatMap mapView = null;
	private LatLng basisPoint1 = null;
	private LatLng basisPoint2 = null;
	private LatLng basisPoint3 = null;

	private void showOnMap() {
		Log.w(TAG, "showOnMap started");

		mapView.clearData();


		if (width == 0) {
			basisPoint1 = new LatLng(basisPoints.get(0).latitude, basisPoints.get(0).longitude);
			basisPoint2 = new LatLng(basisPoints.get(1).latitude, basisPoints.get(1).longitude);
			basisPoint3 = new LatLng(basisPoints.get(2).latitude, basisPoints.get(2).longitude);

			width = SphericalUtil.computeDistanceBetween(basisPoint2, basisPoint1);
			height = SphericalUtil.computeDistanceBetween(basisPoint3, basisPoint1);
		}

		for (int i = 0; i < pointsArray.size(); i++) {
			SuperPoint pointItem = pointsArray.get(i);
			LatLng p = new LatLng(pointItem.latitude, pointItem.longitude);

			double x = PolyUtil.distanceToLine(p, basisPoint1, basisPoint3);
			double y = PolyUtil.distanceToLine(p, basisPoint1, basisPoint2);

			int value = 50;
			if (width == 0 || height == 0) {
				mapView.addData(new HeatMap.DataPoint(0.01f, 0.01f, value));
			} else {
				mapView.addData(new HeatMap.DataPoint((float)(x / width), (float)(y / height), value));
			}
		}

		mapView.forceRefresh();

	}

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


	@Override
	public void onConnected(@Nullable Bundle bundle) {
		Wearable.DataApi.addListener(mGoogleApiClient, dataListener);
		Toast.makeText(FirstScreenActivity.this, "Connected to Google Api", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onConnectionSuspended(int i) {
		Toast.makeText(FirstScreenActivity.this, "Connection Suspended", Toast.LENGTH_SHORT).show();
	}


	@Override
	protected void onStart() {
		super.onStart();
		mGoogleApiClient.connect();
	}


	@Override
	protected void onStop() {
		super.onStop();
		Wearable.DataApi.removeListener(mGoogleApiClient, dataListener);
		mGoogleApiClient.disconnect();
	}
}

