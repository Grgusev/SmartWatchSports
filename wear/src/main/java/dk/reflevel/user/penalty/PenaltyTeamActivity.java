package dk.reflevel.user.penalty;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dk.reflevel.MainActivity;
import dk.reflevel.R;
import dk.reflevel.Tracker.Tracker;
import dk.reflevel.activities.KickOffTeam;
import dk.reflevel.adapter.PenaltyTeam1Adapter;
import dk.reflevel.common.AlertCustomDialog;
import dk.reflevel.common.CardModel;
import dk.reflevel.common.SharedPref;
import dk.reflevel.common.StoreData;
import dk.reflevel.fragments.MainScreenFragment;
import dk.reflevel.user.addname.AwayNumberPickerActivity;

import static dk.reflevel.fragments.MainScreenFragment.firstSecondHalfText;
import static dk.reflevel.fragments.MainScreenFragment.focus;
import static dk.reflevel.fragments.MainScreenFragment.kickOffTeamAway;
import static dk.reflevel.fragments.MainScreenFragment.kickOffTeamHome;
import static dk.reflevel.fragments.MainScreenFragment.mTimeFormatter;
import static dk.reflevel.fragments.MainScreenFragment.setProgressBarValues;
import static dk.reflevel.fragments.MainScreenFragment.textViewTime;
import static dk.reflevel.fragments.MainScreenFragment.timeCountInMilliSeconds;
import static dk.reflevel.fragments.Option1Page.endHalf;
import static dk.reflevel.fragments.Option1Page.saveHalfTimeString;
import static dk.reflevel.fragments.Option1Page.saveTimerString;
import static dk.reflevel.fragments.Option1Page.startHalf;

public class PenaltyTeamActivity extends Activity {

    @BindView(R.id.listHome)
    RecyclerView listHome;
    @BindView(R.id.listAway)
    RecyclerView listAway;

    private SharedPref sharedPref;

    private ArrayList<String> dataListHome = new ArrayList<>();
    private ArrayList<String> dataListAway = new ArrayList<>();
    private PenaltyTeam1Adapter adapter1;
    private PenaltyTeam1Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_penalty_team);
        ButterKnife.bind(this);
        sharedPref = new SharedPref(PenaltyTeamActivity.this);

        adapter = new PenaltyTeam1Adapter(PenaltyTeamActivity.this, dataListHome);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        listHome.setLayoutManager(mLayoutManager);
        listHome.setItemAnimator(new DefaultItemAnimator());
        listHome.setAdapter(adapter);


        adapter1 = new PenaltyTeam1Adapter(PenaltyTeamActivity.this, dataListAway);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        listAway.setLayoutManager(mLayoutManager);
        listAway.setItemAnimator(new DefaultItemAnimator());
        listAway.setAdapter(adapter1);
    }

    @Override
    protected void onResume() {
        super.onResume();

        dataListHome.clear();
        dataListHome.add("Nr: 1 Paulo");
        dataListHome.add("Nr: 2 Anders");
        dataListHome.addAll(sharedPref.getArrayPref(SharedPref.listHome));
        for (int i = 0; i < dataListHome.size(); i++) {
            Log.e(":::", "data :: " + dataListHome.get(i));
        }
        adapter.notifyDataSetChanged();

        dataListAway.clear();
        dataListAway.add("Nr: 1 Paulo");
        dataListAway.add("Nr: 2 Anders");
        dataListAway.addAll(sharedPref.getArrayPref(SharedPref.listAway));
        adapter1.notifyDataSetChanged();
    }

    @OnClick({R.id.btnHome, R.id.btnAway, R.id.btnEnd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnHome:
                startActivity(new Intent(PenaltyTeamActivity.this, AwayNumberPickerActivity.class).putExtra("isFromGoal", true));
//                startActivity(new Intent(PenaltyTeamActivity.this, HomeTeamlistingActivity.class));
//                finish();
                break;
            case R.id.btnAway:
                startActivity(new Intent(PenaltyTeamActivity.this, AwayNumberPickerActivity.class).putExtra("isFromGoal", true));
                break;
            case R.id.btnEnd:
                AlertCustomDialog.openCustomDialog(PenaltyTeamActivity.this, "END penalties?", "Yes", "NO", new AlertCustomDialog.AlertUtilsNegativeListener() {
                    @Override
                    public void onPositiveButtonClick() {
                        endHalf();
                    }

                    @Override
                    public void onNegativeButtonClick() {

                    }
                });
//                startActivity(new Intent(PenaltyTeamActivity.this, EndPenaltiesDialogActivity.class));
//                finish();
                break;
        }
    }

    public void endHalf() {
        Intent intnet = new Intent("SOME_ACTION");
        intnet.putExtra("save", true);
        sendBroadcast(intnet);


        focus.stop();
        focus.setVisibility(View.GONE);

        textViewTime.setVisibility(View.VISIBLE);
        textViewTime.setText(mTimeFormatter(timeCountInMilliSeconds) + ":00");

        setProgressBarValues();

        startHalf.setVisibility(View.VISIBLE);
        endHalf.setVisibility(View.GONE);
        endHalf.setEnabled(false);

        if (KickOffTeam.kickOffTeamSelected == true) {
            if (kickOffTeamHome.getVisibility() == View.VISIBLE) {
                kickOffTeamHome.setVisibility(View.GONE);
                kickOffTeamAway.setVisibility(View.VISIBLE);
            } else {
                kickOffTeamHome.setVisibility(View.VISIBLE);
                kickOffTeamAway.setVisibility(View.GONE);
            }
        }

        // changing the timer status to stopped
        MainScreenFragment.timerStatus = MainScreenFragment.TimerStatus.STOPPED;

        if (firstSecondHalfText.getText().equals("1st Half")) {
            firstSecondHalfText.setText("Start 2nd Half");
        } else if (firstSecondHalfText.getText().equals("2nd Half")) {
            firstSecondHalfText.setText("Kick Off");
            kickOffTeamHome.setVisibility(View.GONE);
            kickOffTeamAway.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(textViewTime.getText())) {
            saveTimerString("TimerTextView", textViewTime.getText().toString(), sharedPref);
        }

        if (!TextUtils.isEmpty(firstSecondHalfText.getText())) {
            saveHalfTimeString("firstSecondHalfTextView", firstSecondHalfText.getText().toString(), PenaltyTeamActivity.this);
        }

        Tracker.getSharedInstance().stopTrackingLocation();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                List<CardModel> list = StoreData.getList(sharedPref);

                for (int i = 0; i < list.size(); i++) {
                    CardModel model = list.get(i);
                    model.setTimerWork(false);

                    if (firstSecondHalfText.getText().equals("Kick Off")) {
                        model.setTimer("00:00");
                    }

                    list.set(i, model);
                }
                sharedPref.setDataInPref(StoreData.STORE_DATA, new Gson().toJson(list));
                Intent intnet = new Intent("SOME_ACTION");
                sendBroadcast(intnet);


                Intent intent = new Intent(PenaltyTeamActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("time", textViewTime.getText());
                intent.putExtra("halfTimeTxt", firstSecondHalfText.getText());
                startActivity(intent);
                finish();

            }
        }, 700);
    }

}
