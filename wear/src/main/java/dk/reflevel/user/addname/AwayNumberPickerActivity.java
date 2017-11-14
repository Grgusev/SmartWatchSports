package dk.reflevel.user.addname;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import dk.reflevel.R;
import dk.reflevel.common.AlertCustomDialog;
import dk.reflevel.common.SharedPref;


public class AwayNumberPickerActivity extends Activity {

    NumberPicker numberPicker11, numberPicker2;
    Button btnOK;
    boolean isFromGoal;
    SharedPref sharedPref;

    String number1 = "0", number2 = "0";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_away_yellow_card_booking);

        isFromGoal = getIntent().getBooleanExtra("isFromGoal", false);

        sharedPref = new SharedPref(AwayNumberPickerActivity.this);
        numberPicker11 = (NumberPicker) findViewById(R.id.numberpicker_red_booking1);
        numberPicker2 = (NumberPicker) findViewById(R.id.numberpicker_yellocard_booking2);
        btnOK = (Button) findViewById(R.id.btn_away_yellow_card);


        numberPicker11.setMinValue(0);
        numberPicker11.setMaxValue(10);

        numberPicker2.setMinValue(0);
        numberPicker2.setMaxValue(10);


        numberPicker11.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                number1 = "" + newVal;
            }
        });

        numberPicker2.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                number2 = "" + newVal;

            }
        });


        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertCustomDialog.openCustomDialog(AwayNumberPickerActivity.this, "Enter name?", "Yes", "NO", new AlertCustomDialog.AlertUtilsNegativeListener() {
                    @Override
                    public void onPositiveButtonClick() {
                        startActivity(new Intent(AwayNumberPickerActivity.this, AddnameActivity.class).putExtra("isFromGoal", isFromGoal).putExtra("number", number1 + number2));
                        finish();
                    }

                    @Override
                    public void onNegativeButtonClick() {
                        if (isFromGoal)
                            finish();
                        else
                            startActivity(new Intent(AwayNumberPickerActivity.this, RadioNameActivity.class));
                    }
                });
            }
        });
    }

}
