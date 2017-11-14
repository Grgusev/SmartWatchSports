package dk.reflevel.user;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.OnClick;
import dk.reflevel.R;
import dk.reflevel.common.SharedPref;

public class CurrentRunningTeamActivity extends Activity {

    private SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_running_team);
        ButterKnife.bind(this);
        sharedPref = new SharedPref(CurrentRunningTeamActivity.this);
    }

    @OnClick({R.id.btnHome, R.id.btnAway})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnHome:
                sharedPref.setBoolean(SharedPref.isHome, true);
                sharedPref.setBoolean(SharedPref.isAway, false);
                break;
            case R.id.btnAway:
                sharedPref.setBoolean(SharedPref.isAway, true);
                sharedPref.setBoolean(SharedPref.isHome, false);
                break;
        }
    }
}
