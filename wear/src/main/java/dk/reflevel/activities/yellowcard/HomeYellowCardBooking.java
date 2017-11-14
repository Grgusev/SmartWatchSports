package dk.reflevel.activities.yellowcard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import dk.reflevel.R;
import dk.reflevel.activities.ChooseHomeAwayTeam;
import dk.reflevel.common.SharedPref;
import dk.reflevel.common.StoreData;


public class HomeYellowCardBooking extends Activity {

	String selectedData = "";
	public static String homeStr;

	NumberPicker numberPickerYellowCard1, numberPickerYellowCard2;
	Button btnHomeYellowCard;
	protected TextView yellowCardPlayerNumber1, yellowCardPlayerNumber2;
	String cardOptionName = "Home";
	SharedPref sharedPref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_yellow_card_booking);

		Intent intent = getIntent();
		homeStr = intent.getStringExtra("homeTeamSelected");
		final String isFrom = intent.getStringExtra("isFrom");
		ChooseHomeAwayTeam.txtHome.setText(homeStr);
		sharedPref = new SharedPref(HomeYellowCardBooking.this);

		numberPickerYellowCard1 = (NumberPicker) findViewById(R.id.numberpicker_red_booking1);
		numberPickerYellowCard2 = (NumberPicker) findViewById(R.id.numberpicker_yellocard_booking2);
		btnHomeYellowCard = (Button) findViewById(R.id.btn_home_yellow_card);

		yellowCardPlayerNumber1 = (TextView) findViewById(R.id.yellow_card_player_number1);
		yellowCardPlayerNumber2 = (TextView) findViewById(R.id.yellow_card_player_number2);

		numberPickerYellowCard1.setMinValue(0);
		numberPickerYellowCard1.setMaxValue(10);

		numberPickerYellowCard2.setMinValue(0);
		numberPickerYellowCard2.setMaxValue(10);

		yellowCardPlayerNumber1.setText("0" + homeStr);
		yellowCardPlayerNumber2.setText("0" + homeStr);

		numberPickerYellowCard1.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				// TODO Auto-generated method stub
				String New = "";
				yellowCardPlayerNumber1.setText(New.concat(String.valueOf(newVal)) + homeStr);
			}
		});

		numberPickerYellowCard2.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				// TODO Auto-generated method stub
				String New = "";
				yellowCardPlayerNumber2.setText(New.concat(String.valueOf(newVal)) + homeStr);
			}
		});


		btnHomeYellowCard.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String task = yellowCardPlayerNumber1.getText().toString();
				String task2 = yellowCardPlayerNumber2.getText().toString();

				int listPosition = StoreData.isCheckData(sharedPref, "Nr. " + task + task2, "red");
				if (listPosition != -1) {
					StoreData.updateTimeStop(sharedPref, listPosition);
					Toast.makeText(HomeYellowCardBooking.this, "Already assign red card to this player.", Toast.LENGTH_SHORT).show();
					return;
				}

				if (StoreData.isCheckData1(sharedPref, "Nr. " + task + task2, "red")) {
					Toast.makeText(HomeYellowCardBooking.this, "Already assign red card to this player.", Toast.LENGTH_SHORT).show();
					return;
				}

				Intent intent = new Intent(HomeYellowCardBooking.this, YellowCardOptions.class);
				intent.putExtra("playerNumber", task);
				intent.putExtra("playerNumber2", task2);
				intent.putExtra("cardOptionName", cardOptionName);
				intent.putExtra("isFrom", isFrom);
				startActivity(intent);
				finish();
			}
		});
	}

}
