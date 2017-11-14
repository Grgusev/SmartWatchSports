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
import dk.reflevel.adapter.PenaltyUserAdapter;
import dk.reflevel.common.SharedPref;
import dk.reflevel.model.PenaltyUser;

import static dk.reflevel.fragments.MainScreenFragment.awayGoal;
import static dk.reflevel.fragments.MainScreenFragment.homeGoal;

public class PenaltyUserlistingActivity extends Activity {

    List<PenaltyUser> penaltyUserList = new ArrayList<>();
    @BindView(R.id.recycleUserList)
    RecyclerView recyclerView;
    PenaltyUserAdapter penaltyUserAdapter;
    SharedPref sharedPref;
    private String isType;
    private String goalType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_penalty_userlisting);
        ButterKnife.bind(this);


        isType = getIntent().getStringExtra("isType");
        goalType = getIntent().getStringExtra("goalType");

        sharedPref = new SharedPref(PenaltyUserlistingActivity.this);

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


        penaltyUserAdapter = new PenaltyUserAdapter(PenaltyUserlistingActivity.this, penaltyUserList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(penaltyUserAdapter);

        penaltyUserAdapter.notifyDataSetChanged();
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

                startActivity(new Intent(PenaltyUserlistingActivity.this, MainActivity.class));
                finish();
                break;
            case R.id.btnCancel:
                startActivity(new Intent(PenaltyUserlistingActivity.this, AddgoalActivity.class));
                finish();
                break;
        }
    }
}
