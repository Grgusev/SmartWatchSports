package dk.reflevel.activities.substitution;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import dk.reflevel.R;
import dk.reflevel.common.Global;
import dk.reflevel.common.SharedPref;
import dk.reflevel.user.SubActivity;
import dk.reflevel.user.addname.AwayNumberPickerActivity;


public class SubstitutionActivity extends Activity {

    Button subHome, subAway;
    SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_substitution);

        subHome = (Button) findViewById(R.id.sub_home);
        subAway = (Button) findViewById(R.id.sub_away);
        sharedPref = new SharedPref(SubstitutionActivity.this);

        subHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sharedPref.getBoolean(Global.IS_ADVANCE_SUB)) {
                    startActivity(new Intent(SubstitutionActivity.this, SubActivity.class));
                } else {
                    Intent intent = new Intent(SubstitutionActivity.this, SubstitutionHome.class);
                    startActivity(intent);
                }
            }
        });

        subAway.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sharedPref.getBoolean(Global.IS_ADVANCE_SUB)) {
                    startActivity(new Intent(SubstitutionActivity.this, AwayNumberPickerActivity.class));
                    finish();
                } else {
                    Intent intent = new Intent(SubstitutionActivity.this, SubstitutionAway.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

}
