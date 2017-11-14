package dk.reflevel.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.OnClick;
import dk.reflevel.MainActivity;
import dk.reflevel.R;
import dk.reflevel.common.SharedPref;

import static dk.reflevel.fragments.MainScreenFragment.awayGoal;
import static dk.reflevel.fragments.MainScreenFragment.homeGoal;

public class RemoveGoalActivity extends Activity {

    private String isType;
    private String goalType;

    SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_goal);
        ButterKnife.bind(this);
        sharedPref = new SharedPref(RemoveGoalActivity.this);
        isType = getIntent().getStringExtra("isType");
        goalType = getIntent().getStringExtra("goalType");
    }

    @OnClick({R.id.btnRemoveGoal, R.id.btnRemoveOther})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnRemoveGoal:

                if (isType.equalsIgnoreCase("home")) {

                    homeGoal(goalType, sharedPref);

                } else if (isType.equalsIgnoreCase("away")) {
                    awayGoal(goalType, sharedPref);
                }


                startActivity(new Intent(RemoveGoalActivity.this, MainActivity.class));
                finish();
                break;
            case R.id.btnRemoveOther:
                startActivity(new Intent(RemoveGoalActivity.this, RemoveGoallistingActivity.class).putExtra("isType",isType));
                finish();
                break;
        }
    }
}
