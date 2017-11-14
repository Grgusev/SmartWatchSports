package dk.reflevel.activities.redcard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import dk.reflevel.R;
import dk.reflevel.common.SharedPref;
import dk.reflevel.common.StoreData;

import static dk.reflevel.activities.RedCardChooseHomeAwayTeam.redCardTxtHome;

public class HomeRedCardBooking extends Activity {

    String selectedData = "";
    public static String homeRedstr;
    NumberPicker numberPickerRedCard1, numberPickerRedCard2;
    Button btnHomeRedCard;
    protected TextView redCardPlayerNumber1, redCardPlayerNumber2;
    String cardOptionName="Home";
    SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_red_card_booking);

        Intent intent = getIntent();
        homeRedstr = intent.getStringExtra("redCardHomeTeamSelected");
        final String isFrom = intent.getStringExtra("isFrom");
        redCardTxtHome.setText(homeRedstr);
        sharedPref = new SharedPref(HomeRedCardBooking.this);

        numberPickerRedCard1 = (NumberPicker) findViewById(R.id.numberpicker_red_booking1);
        numberPickerRedCard2 = (NumberPicker) findViewById(R.id.numberpicker_red_booking2);
        btnHomeRedCard = (Button) findViewById(R.id.btn_home_red_card);

        redCardPlayerNumber1 = (TextView) findViewById(R.id.red_card_player_number1);
        redCardPlayerNumber2 = (TextView) findViewById(R.id.red_card_player_number2);

        numberPickerRedCard1.setMinValue(0);
        numberPickerRedCard1.setMaxValue(10);

        numberPickerRedCard2.setMinValue(0);
        numberPickerRedCard2.setMaxValue(10);

        redCardPlayerNumber1.setText("0" + homeRedstr);
        redCardPlayerNumber2.setText("0" + homeRedstr);

        numberPickerRedCard1.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                // TODO Auto-generated method stub
                String New = "";
                redCardPlayerNumber1.setText(New.concat(String.valueOf(newVal)) + homeRedstr);
            }
        });

        numberPickerRedCard2.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                // TODO Auto-generated method stub
                String New = "";
                redCardPlayerNumber2.setText(New.concat(String.valueOf(newVal)) + homeRedstr);
            }
        });


        btnHomeRedCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String task = redCardPlayerNumber1.getText().toString();
                String task2 = redCardPlayerNumber2.getText().toString();
                int listPosition = StoreData.isCheckData(sharedPref, "Nr. " + task + task2, isFrom);
                if (listPosition != -1) {
                    StoreData.updateTimeStop(sharedPref, listPosition);
                    Toast.makeText(HomeRedCardBooking.this, "Already assign red card to this player.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (StoreData.isCheckData1(sharedPref, "Nr. " + task + task2, "red")) {
                    Toast.makeText(HomeRedCardBooking.this, "Already assign red card to this player.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(HomeRedCardBooking.this, RedCardOptions.class);
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
