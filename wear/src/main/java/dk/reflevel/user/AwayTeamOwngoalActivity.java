package dk.reflevel.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dk.reflevel.MainActivity;
import dk.reflevel.R;
import dk.reflevel.adapter.AwayTeamOwnGoalAdapter;
import dk.reflevel.common.SharedPref;
import dk.reflevel.model.PenaltyUser;

import static dk.reflevel.fragments.MainScreenFragment.awayGoal;
import static dk.reflevel.fragments.MainScreenFragment.homeGoal;

public class AwayTeamOwngoalActivity extends Activity {

    List<PenaltyUser> penaltyUserList = new ArrayList<>();
    @BindView(R.id.recycleUserList)
    RecyclerView recyclerView;
    AwayTeamOwnGoalAdapter awayTeamOwnGoalAdapter;
    private SharedPref sharedPref;
    private String isType;
    private String goalType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_away_team_owngoal);
        ButterKnife.bind(this);

        isType = getIntent().getStringExtra("isType");
        goalType = getIntent().getStringExtra("goalType");

        sharedPref = new SharedPref(AwayTeamOwngoalActivity.this);

        awayTeamOwnGoalAdapter = new AwayTeamOwnGoalAdapter(AwayTeamOwngoalActivity.this,penaltyUserList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(awayTeamOwnGoalAdapter);

        PenaltyUser penaltyUser = new PenaltyUser();
        penaltyUser.setTitle("Simon");
        penaltyUserList.add(penaltyUser);

        penaltyUser = new PenaltyUser();
        penaltyUser.setTitle("Andreas");
        penaltyUserList.add(penaltyUser);

        penaltyUser = new PenaltyUser();
        penaltyUser.setTitle("Paulo");
        penaltyUserList.add(penaltyUser);

        penaltyUser = new PenaltyUser();
        penaltyUser.setTitle("Ponaldo");
        penaltyUserList.add(penaltyUser);

        awayTeamOwnGoalAdapter.notifyDataSetChanged();
    }

    @OnClick({R.id.btnOk, R.id.btnCancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnOk:

                if (isType.equalsIgnoreCase("home")) {

                    homeGoal(goalType, sharedPref);

                } else if (isType.equalsIgnoreCase("away")) {
                    awayGoal(goalType, sharedPref);
                }

                startActivity(new Intent(AwayTeamOwngoalActivity.this, MainActivity.class));
                finish();
                break;
            case R.id.btnCancel:
                startActivity(new Intent(AwayTeamOwngoalActivity.this, AddgoalActivity.class));
                finish();
                break;
        }
    }
}
