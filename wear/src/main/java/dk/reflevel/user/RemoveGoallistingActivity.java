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
import dk.reflevel.adapter.RemoveGoalListAdapter;
import dk.reflevel.common.AlertCustomDialog;
import dk.reflevel.common.Global;
import dk.reflevel.common.SharedPref;
import dk.reflevel.model.PenaltyUser;

import static dk.reflevel.fragments.MainScreenFragment.awayGoal;
import static dk.reflevel.fragments.MainScreenFragment.homeGoal;

public class RemoveGoallistingActivity extends Activity {

    List<PenaltyUser> penaltyUserList = new ArrayList<>();
    @BindView(R.id.recycleUserList)
    RecyclerView recyclerView;
    RemoveGoalListAdapter removeGoalListAdapter;
    private String userName;
    private String isType;

    SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_penalty_userlisting);
        ButterKnife.bind(this);
        isType = getIntent().getStringExtra("isType");
        sharedPref = new SharedPref(RemoveGoallistingActivity.this);


        removeGoalListAdapter = new RemoveGoalListAdapter(RemoveGoallistingActivity.this, penaltyUserList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(removeGoalListAdapter);

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

        removeGoalListAdapter.notifyDataSetChanged();
    }

    @OnClick({R.id.btnOk, R.id.btnCancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnOk:

                AlertCustomDialog.openCustomDialog(RemoveGoallistingActivity.this, "Remove " + userName + "?", "Yes", "No", new AlertCustomDialog.AlertUtilsNegativeListener() {
                    @Override
                    public void onPositiveButtonClick() {
                        if (isType.equalsIgnoreCase("home")) {

                            homeGoal(Global.MINUS, sharedPref);

                        } else if (isType.equalsIgnoreCase("away")) {
                            awayGoal(Global.MINUS, sharedPref);
                        }

                        startActivity(new Intent(RemoveGoallistingActivity.this, MainActivity.class));
                        finish();

                    }

                    @Override
                    public void onNegativeButtonClick() {


                    }
                });


                break;
            case R.id.btnCancel:
                startActivity(new Intent(RemoveGoallistingActivity.this, RemoveGoalActivity.class));
                finish();


                break;
        }
    }


    public void selectedName(String name) {
        userName = name;

    }
}
