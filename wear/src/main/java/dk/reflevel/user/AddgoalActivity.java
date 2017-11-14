package dk.reflevel.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.OnClick;
import dk.reflevel.R;

public class AddgoalActivity extends Activity {


    String isType;
    private String goalType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addgoal);
        ButterKnife.bind(this);
        isType = getIntent().getStringExtra("isType");
        goalType = getIntent().getStringExtra("goalType");


    }

    @OnClick({R.id.btnNormal, R.id.btnPenalty, R.id.btnOwngoal})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnNormal:
                startActivity(new Intent(AddgoalActivity.this, PenaltyUserlistingActivity.class).putExtra("isType",isType).putExtra("goalType",goalType));
                finish();
                break;
            case R.id.btnPenalty:

                startActivity(new Intent(AddgoalActivity.this, PenaltyUserlistingActivity.class).putExtra("isType",isType).putExtra("goalType",goalType));
                finish();
                break;
            case R.id.btnOwngoal:
                startActivity(new Intent(AddgoalActivity.this, AwayTeamOwngoalActivity.class).putExtra("isType",isType).putExtra("goalType",goalType));
                finish();
                break;
        }
    }
}
