package dk.reflevel;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;

import dk.reflevel.Tracker.BasisPoint;
import dk.reflevel.Tracker.SuperPoint;
import dk.reflevel.Tracker.Tracker;

/**
 * Created by Ruifeng Shi on 4/27/2017.
 */

public class TakePointsActivity extends FragmentActivity {
	private						BasisPoint		lastLocation				= null;
	private static final		String			TAG							= "MainActivity";
	private final				int				REQ_CODE_LOCATION			= 0x1234;

	private LinearLayout		optionsLayout = null;
	private Button				lastPointsButton = null;
	private Button				newPointsButton = null;

	private RelativeLayout		basisLayout = null;
	private TextView			hintTextView = null;
	private RelativeLayout		basisPtLayout1 = null;
	private RelativeLayout		basisPtLayout2 = null;
	private RelativeLayout		basisPtLayout3 = null;

	private Button				btnStartSync = null;

	private ImageView			mainImageView = null;


	private Tracker trackerInstance = null;


	private IntentFilter intentFilter = null;
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.w(TAG, "onReceive==============");
			String action = intent.getAction();
			if (action.equals("LocationChanged")) {
				String location = intent.getStringExtra("location");

				BasisPoint point = new BasisPoint();
				try {
					point.decodeFromJSON(new JSONObject(location));
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				lastLocation = point;

				synchronized (pointsArray) {
					if (CURRENT_POINT_INDEX >= 0) {
						pointsArray.add(lastLocation);
					}

					if (pointsArray.size() == POINTS_COUNT_FOR_BASIS) {
						BasisPoint basisPoint = new BasisPoint();

						basisPoint.index = 0;

						int minIndex = 0, maxIndex = 0;
						double tempMin = 0, tempMax = 0;

						// Calculate average latitude
						{
							minIndex = maxIndex = 0;
							tempMin = pointsArray.get(0).latitude;
							tempMax = pointsArray.get(0).latitude;
							for (int i = 0; i < pointsArray.size(); i++) {
								if (tempMin > pointsArray.get(i).latitude) {
									tempMin = pointsArray.get(i).latitude;
									minIndex = i;
								}

								if (tempMax < pointsArray.get(i).latitude) {
									tempMax = pointsArray.get(i).latitude;
									maxIndex = i;
								}
							}

							for (int i = 0; i < pointsArray.size(); i++) {
								if (i == maxIndex || i == minIndex)
									continue;

								basisPoint.latitude += pointsArray.get(i).latitude / (POINTS_COUNT_FOR_BASIS - 2);
							}
						}

						// Calculate average longitude
						{
							minIndex = maxIndex = 0;
							tempMin = pointsArray.get(0).longitude;
							tempMax = pointsArray.get(0).longitude;
							for (int i = 0; i < pointsArray.size(); i++) {
								if (tempMin > pointsArray.get(i).longitude) {
									tempMin = pointsArray.get(i).longitude;
									minIndex = i;
								}

								if (tempMax < pointsArray.get(i).longitude) {
									tempMax = pointsArray.get(i).longitude;
									maxIndex = i;
								}
							}

							for (int i = 0; i < pointsArray.size(); i++) {
								if (i == maxIndex || i == minIndex)
									continue;

								basisPoint.longitude += pointsArray.get(i).longitude / (POINTS_COUNT_FOR_BASIS - 2);
							}
						}


						stopProgress();

						boolean result = Tracker.saveBasisLocation(TakePointsActivity.this, basisPoint, CURRENT_POINT_INDEX);
						if (result) {
							Toast.makeText(TakePointsActivity.this, "BP" + (CURRENT_POINT_INDEX + 1) + ":(" + basisPoint.latitude + "," + basisPoint.longitude + ")", Toast.LENGTH_SHORT).show();
							if (CURRENT_POINT_INDEX == 0)
								basisPtLayout1.setVisibility(View.GONE);
							else if (CURRENT_POINT_INDEX == 1)
								basisPtLayout2.setVisibility(View.GONE);
							else if (CURRENT_POINT_INDEX == 2)
								basisPtLayout3.setVisibility(View.GONE);

							checkStateForStop();
						} else {
							Toast.makeText(TakePointsActivity.this, "Basis point " + (CURRENT_POINT_INDEX + 1) + " selection failed. Please try again later", Toast.LENGTH_SHORT).show();
						}

						pointsArray.clear();
						CURRENT_POINT_INDEX = -1;
					}
				}
			}
		}
	};


	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_points);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		trackerInstance = Tracker.getSharedInstance();

		basisLayout = (RelativeLayout)findViewById(R.id.basis_points_layout);
		optionsLayout = (LinearLayout)findViewById(R.id.options_layout);
		lastPointsButton = (Button)findViewById(R.id.get_last_points);
		lastPointsButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onClickedLastPoints();
			}
		});
		newPointsButton = (Button)findViewById(R.id.new_points);
		newPointsButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onClickedNewPoints();
			}
		});

		hintTextView = (TextView)findViewById(R.id.hint_textview);
		basisPtLayout1 = (RelativeLayout)findViewById(R.id.basis_layout1);
		basisPtLayout2 = (RelativeLayout)findViewById(R.id.basis_layout2);
		basisPtLayout3 = (RelativeLayout)findViewById(R.id.basis_layout3);

		btnStartSync = (Button)findViewById(R.id.start_sync);

		findViewById(R.id.pointButton1).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onClickedPoint1();
			}
		});
		findViewById(R.id.pointButton2).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onClickedPoint2();
			}
		});
		findViewById(R.id.pointButton3).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onClickedPoint3();
			}
		});

		btnStartSync.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onClickedStartSync();
			}
		});

		mainImageView = (ImageView)findViewById(R.id.pitch_imageView);

		// Intent filter registeration
		{
			intentFilter = new IntentFilter();
			intentFilter.addAction("LocationChanged");

			registerReceiver(receiver, intentFilter);
		}

		updateUI();

		requestLocationPermission();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		unregisterReceiver(receiver);
		intentFilter = null;
	}


	private final int POINTS_COUNT_FOR_BASIS = 12;
	private int CURRENT_POINT_INDEX = -1;
	private ArrayList<SuperPoint> pointsArray = new ArrayList<>();
	private ProgressDialog progressDialog = null;
	private void showProgress() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(TakePointsActivity.this);
			progressDialog.setIndeterminate(true);
			progressDialog.setMessage("Please wait...");
			progressDialog.setCancelable(false);
		}

		progressDialog.show();
	}

	private void stopProgress() {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}


	private void onClickedPoint1() {
		if (lastLocation == null) {
			Toast.makeText(TakePointsActivity.this, "Location is not retrieved yet.", Toast.LENGTH_SHORT).show();
			return;
		}

		showProgress();
		synchronized (pointsArray) {
			pointsArray.clear();
			CURRENT_POINT_INDEX = 0;
		}
	}

	private void onClickedPoint2() {
		if (lastLocation == null) {
			Toast.makeText(TakePointsActivity.this, "Location is not retrieved yet.", Toast.LENGTH_SHORT).show();
			return;
		}

		showProgress();
		synchronized (pointsArray) {
			pointsArray.clear();
			CURRENT_POINT_INDEX = 1;
		}
//		boolean result = Tracker.saveBasisLocation(TakePointsActivity.this, lastLocation, 1);
//		if (result) {
//			Toast.makeText(TakePointsActivity.this, "BP2:(" + lastLocation.latitude + "," + lastLocation.longitude + ")", Toast.LENGTH_SHORT).show();
//			basisPtLayout2.setVisibility(View.GONE);
//			checkStateForStop();
//		} else {
//			Toast.makeText(TakePointsActivity.this, "Basis point 2 selection failed. Please try again later", Toast.LENGTH_SHORT).show();
//		}
	}

	private void onClickedPoint3() {
		if (lastLocation == null) {
			Toast.makeText(TakePointsActivity.this, "Location is not retrieved yet.", Toast.LENGTH_SHORT).show();
			return;
		}

		showProgress();
		synchronized (pointsArray) {
			pointsArray.clear();
			CURRENT_POINT_INDEX = 2;
		}
//		boolean result = Tracker.saveBasisLocation(TakePointsActivity.this, lastLocation, 2);
//		if (result) {
//			Toast.makeText(TakePointsActivity.this, "BP3:(" + lastLocation.latitude + "," + lastLocation.longitude + ")", Toast.LENGTH_SHORT).show();
//			basisPtLayout3.setVisibility(View.GONE);
//			checkStateForStop();
//		} else {
//			Toast.makeText(TakePointsActivity.this, "Basis point 3 selection failed. Please try again later", Toast.LENGTH_SHORT).show();
//		}
	}


	private void checkStateForStop() {
		boolean hasNull = false;
		for (int i = 0; i < 3; i++) {
			if (Tracker.loadBasisLocation(TakePointsActivity.this, i) == null) {
				hasNull = true;
				break;
			}
		}

		if (!hasNull) {
			Tracker.saveTakePointsState(TakePointsActivity.this, Tracker.STATE_START_TRACK);
			updateUI();
		}
	}


	private void onClickedStartSync() {
		AlertDialog alertDialog = new AlertDialog.Builder(TakePointsActivity.this)
				.setMessage("Would you remove logged points?")
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (Tracker.startSync(TakePointsActivity.this)) {
							Toast.makeText(TakePointsActivity.this, "Synced points", Toast.LENGTH_SHORT).show();
							Tracker.resetState(TakePointsActivity.this);
							updateUI();
						}
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Tracker.startSync(TakePointsActivity.this);
					}
				})
				.create();
		alertDialog.show();
	}


	/*
	 * Location initialization
	 */
	private void requestLocationPermission() {
		Log.w(TAG, "requestLocationPermission");

		int permissionResult = ContextCompat.checkSelfPermission(TakePointsActivity.this, Tracker.LOCATION_PERMISSION);
		if (permissionResult == PackageManager.PERMISSION_GRANTED) {
			// Start location service
			Tracker.getSharedInstance().startLocationService();
		} else {
			ActivityCompat.requestPermissions(TakePointsActivity.this,
					new String[]{Tracker.LOCATION_PERMISSION},
					REQ_CODE_LOCATION);
		}
	}


	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		Log.w(TAG, "onRequestPermissionsResult");

		int permissionResult = ContextCompat.checkSelfPermission(TakePointsActivity.this, Tracker.LOCATION_PERMISSION);
		if (permissionResult == PackageManager.PERMISSION_GRANTED) {
			// Permission granted
			// Start location service
			Tracker.getSharedInstance().startLocationService();
		} else {
			// Permission denied
			Log.w(TAG, "Location permission denied");
		}
	}


	private void updateUI() {
		int state = Tracker.loadTakePointsState(TakePointsActivity.this);
		if (state == Tracker.STATE_TAKE_BASIS_POINT) {
			btnStartSync.setVisibility(View.GONE);

			hintTextView.setText("Select 3 corners of the pitch");

			BasisPoint point1 = Tracker.loadBasisLocation(TakePointsActivity.this, 0);
			BasisPoint point2 = Tracker.loadBasisLocation(TakePointsActivity.this, 1);
			BasisPoint point3 = Tracker.loadBasisLocation(TakePointsActivity.this, 2);

			if (point1 != null && point2 != null && point3 != null) {
				basisLayout.setVisibility(View.GONE);
				optionsLayout.setVisibility(View.VISIBLE);
			} else {
				basisPtLayout1.setVisibility(Tracker.loadBasisLocation(TakePointsActivity.this, 0) == null ? View.VISIBLE : View.GONE);
				basisPtLayout2.setVisibility(Tracker.loadBasisLocation(TakePointsActivity.this, 1) == null ? View.VISIBLE : View.GONE);
				basisPtLayout3.setVisibility(Tracker.loadBasisLocation(TakePointsActivity.this, 2) == null ? View.VISIBLE : View.GONE);
			}
		} else if (state == Tracker.STATE_SYNC) {
			btnStartSync.setVisibility(View.VISIBLE);

			basisLayout.setVisibility(View.GONE);
			optionsLayout.setVisibility(View.GONE);
			basisPtLayout1.setVisibility(View.VISIBLE);
			basisPtLayout2.setVisibility(View.VISIBLE);
			basisPtLayout3.setVisibility(View.VISIBLE);
		} else {
			btnStartSync.setVisibility(View.GONE);

			basisLayout.setVisibility(View.VISIBLE);
			optionsLayout.setVisibility(View.GONE);
			basisPtLayout1.setVisibility(View.GONE);
			basisPtLayout2.setVisibility(View.GONE);
			basisPtLayout3.setVisibility(View.GONE);

			hintTextView.setText("Basis points are all selected");
		}
	}



	private PointF ptDown = new PointF();
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			ptDown = new PointF(ev.getX(), ev.getY());
		} else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
			if (ev.getX() > ptDown.x && ev.getX() - ptDown.x > mainImageView.getWidth() / 2) {
				// Need to finish.
				finish();
			}

			return false;
		}

		return super.dispatchTouchEvent(ev);
	}

	private void onClickedLastPoints() {
		Tracker.saveTakePointsState(TakePointsActivity.this, Tracker.STATE_START_TRACK);
		updateUI();
	}

	private void onClickedNewPoints() {
		optionsLayout.setVisibility(View.GONE);
		basisLayout.setVisibility(View.VISIBLE);
		Tracker.clearBasisPoints(TakePointsActivity.this);
		updateUI();
	}

}
