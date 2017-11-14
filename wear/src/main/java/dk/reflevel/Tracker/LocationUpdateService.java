package dk.reflevel.Tracker;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by Grishma on 16/5/16.
 */
public class LocationUpdateService extends Service implements
		GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener,
		LocationListener {

	protected static final String TAG = "LocationUpdateService";

	public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000;
	public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;

	/**
	 * Tracks the status of the location updates request. Value changes when the user presses the
	 * Start Updates and Stop Updates buttons.
	 */
	public Integer mRequestingLocationUpdates = -1;

	private GoogleApiClient mGoogleApiClient;
	private LocationRequest mLocationRequest;
	private Location mCurrentLocation;

	@Override
	public void onCreate() {
		super.onCreate();
		// Kick off the process of building a GoogleApiClient and requesting the LocationServices API.
	}


	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// Within {@code onPause()}, we pause location updates, but leave the
		// connection to GoogleApiClient intact.  Here, we resume receiving
		// location updates if the user has requested them.

		int value = intent.getIntExtra("start_tracking", -1);
		if (value >= 0)
			mRequestingLocationUpdates = value;

		Log.d(TAG, "On Start Command. Location Update Flag : " + mRequestingLocationUpdates);

		if (mGoogleApiClient == null) {
			buildGoogleApiClient();
		}

		return Service.START_REDELIVER_INTENT;
	}


	@Override
	public void onConnected(Bundle bundle) {
		startLocationUpdates();
	}

	@Override
	public void onConnectionSuspended(int i) {
		// The connection to Google Play services was lost for some reason. We call connect() to
		// attempt to re-establish the connection.
		Log.i(TAG, "Connection suspended==");
		Toast.makeText(getApplicationContext(), "Connection suspended", Toast.LENGTH_SHORT).show();

		mGoogleApiClient.connect();
	}


	@Override
	public void onLocationChanged(Location location) {
		Log.w(TAG, "Location changed");

		BasisPoint basisPoint = new BasisPoint();
		basisPoint.latitude = location.getLatitude();
		basisPoint.longitude = location.getLongitude();

		Intent intent = new Intent("LocationChanged");
		intent.putExtra("location", basisPoint.encodeToJSON().toString());
		sendBroadcast(intent);

		mCurrentLocation = location;

		if (mRequestingLocationUpdates == 1)
			insertToDB();
	}


	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		// Refer to the javadoc for ConnectionResult to see what error codes might be returned in
		// onConnectionFailed.
		Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
		Toast.makeText(getApplicationContext(), "Connection failed", Toast.LENGTH_SHORT).show();
	}

	/**
	 * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
	 * LocationServices API.
	 */
	protected synchronized void buildGoogleApiClient() {
		Log.i(TAG, "Building GoogleApiClient===========");

		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API)
				.build();

		createLocationRequest();
	}


	/**
	 * Updates the latitude, the longitude, and the last location time in the UI.
	 */
	private void insertToDB() {
		LocationVo locationVo = new LocationVo();
		locationVo.setLatitude(mCurrentLocation.getLatitude());
		locationVo.setLongitude(mCurrentLocation.getLongitude());
		locationVo.setLocAddress("");

		Log.w(TAG, "Inserted to DB : (" + mCurrentLocation.getLatitude() + ", " +  + mCurrentLocation.getLongitude() + ")");

		LocationDBHelper.getInstance(this).insertLocationDetails(locationVo);
	}


	/**
	 * Sets up the location request. Android has two location request settings:
	 * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
	 * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
	 * the AndroidManifest.xml.
	 * <p/>
	 * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
	 * interval (5 seconds), the Fused Location Provider API returns location updates that are
	 * accurate to within a few feet.
	 * <p/>
	 * These settings are appropriate for mapping applications that show real-time location
	 * updates.
	 */
	protected void createLocationRequest() {
		mGoogleApiClient.connect();

		mLocationRequest = new LocationRequest();

		mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
		mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	}


	/**
	 * Requests location updates from the FusedLocationApi.
	 */
	protected void startLocationUpdates() {
		try {
			Log.d(TAG, "startLocationUpdates==========");
			LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
		} catch (SecurityException ex) {
			ex.printStackTrace();
		}
	}


	/**
	 * Removes location updates from the FusedLocationApi.
	 */
	protected void stopLocationUpdates() {
		Log.d(TAG, "stopLocationUpdates============");
		LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		stopLocationUpdates();
	}


}
