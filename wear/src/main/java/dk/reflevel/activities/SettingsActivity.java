package dk.reflevel.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import dk.reflevel.R;
import dk.reflevel.common.Global;
import dk.reflevel.common.SharedPref;

public class SettingsActivity extends Activity {

    public static CheckBox checkBoxAdvanced, checkBoxNeutral;
    public static boolean checkState = true;
    CheckBox check10min;
    CheckBox check5min;
    CheckBox checkNone, advGoalNo, advGoalYes, advSUBYes, advSUBNo;
    SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        sharedPref = new SharedPref(SettingsActivity.this);
        checkNone = (CheckBox) findViewById(R.id.checkNone);
        check5min = (CheckBox) findViewById(R.id.check5min);
        check10min = (CheckBox) findViewById(R.id.check10min);
        advGoalNo = (CheckBox) findViewById(R.id.advGoalNo);
        advGoalYes = (CheckBox) findViewById(R.id.advGoalYes);
        advSUBYes = (CheckBox) findViewById(R.id.advSUBYes);
        advSUBNo = (CheckBox) findViewById(R.id.advSUBNo);
        String data = sharedPref.getDataFromPref(Global.STORE_MINUTE, "00:00");
        if (data.isEmpty()) {
            setOnCheck(checkNone);
        } else if (data.equalsIgnoreCase("5:00")) {
            setOnCheck(check5min);
        } else if (data.equalsIgnoreCase("10:00")) {
            setOnCheck(check10min);
        }

        if (sharedPref.getBoolean(Global.IS_ADVANCE_GOAL)) {
            advGoalYes.setChecked(true);
            advGoalNo.setChecked(false);
        } else {
            advGoalYes.setChecked(false);
            advGoalNo.setChecked(true);
        }

        if (sharedPref.getBoolean(Global.IS_ADVANCE_SUB)) {
            advSUBYes.setChecked(true);
            advSUBNo.setChecked(false);
        } else {
            advSUBYes.setChecked(false);
            advSUBNo.setChecked(true);
        }

        checkNone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    check5min.setChecked(false);
                    check10min.setChecked(false);
                    sharedPref.setDataInPref(Global.STORE_MINUTE, "");
                }
            }
        });
        check5min.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkNone.setChecked(false);
                    check10min.setChecked(false);
                    sharedPref.setDataInPref(Global.STORE_MINUTE, "5:00");
                }
            }
        });
        check10min.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    check5min.setChecked(false);
                    checkNone.setChecked(false);
                    sharedPref.setDataInPref(Global.STORE_MINUTE, "10:00");
                }
            }
        });

        advGoalYes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    advGoalNo.setChecked(false);
                    sharedPref.setBoolean(Global.IS_ADVANCE_GOAL, true);
                }else{
                    advGoalNo.setChecked(true);
                    sharedPref.setBoolean(Global.IS_ADVANCE_GOAL, false);
                }
            }
        });

        advGoalNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    advGoalYes.setChecked(false);
                    sharedPref.setBoolean(Global.IS_ADVANCE_GOAL, false);
                }else{
                    advGoalYes.setChecked(true);
                    sharedPref.setBoolean(Global.IS_ADVANCE_GOAL, true);
                }
            }
        });

        advSUBYes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    advSUBNo.setChecked(false);
                    sharedPref.setBoolean(Global.IS_ADVANCE_SUB, true);
                }else{
                    advSUBNo.setChecked(true);
                    sharedPref.setBoolean(Global.IS_ADVANCE_SUB, false);
                }
            }
        });

        advSUBNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    advSUBYes.setChecked(false);
                    sharedPref.setBoolean(Global.IS_ADVANCE_SUB, false);
                }else{
                    advSUBYes.setChecked(true);
                    sharedPref.setBoolean(Global.IS_ADVANCE_SUB, true);
                }
            }
        });


    }

    /*
    public void sendCheckState(){
        SharedPreferences sp = getSharedPreferences("advanced", Context.MODE_PRIVATE);
        SharedPreferences.Editor spEdit = sp.edit();
        spEdit.putBoolean("checkState",checkState);
        spEdit.apply();
        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
       // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
*/
    private void setOnCheck(CheckBox checkBox) {
        checkNone.setChecked(false);
        check5min.setChecked(false);
        check10min.setChecked(false);
        checkBox.setChecked(true);
    }


}
