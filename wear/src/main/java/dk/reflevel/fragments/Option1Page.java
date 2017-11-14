package dk.reflevel.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.gson.Gson;

import java.util.List;

import dk.reflevel.MainActivity;
import dk.reflevel.R;
import dk.reflevel.Tracker.Tracker;
import dk.reflevel.activities.KickOffTeam;
import dk.reflevel.activities.SettingsActivity;
import dk.reflevel.activities.substitution.SubstitutionActivity;
import dk.reflevel.common.CardModel;
import dk.reflevel.common.Global;
import dk.reflevel.common.SharedPref;
import dk.reflevel.common.StoreData;
import dk.reflevel.user.penalty.PenaltyTeamActivity;

import static dk.reflevel.fragments.MainScreenFragment.firstSecondHalfText;
import static dk.reflevel.fragments.MainScreenFragment.focus;
import static dk.reflevel.fragments.MainScreenFragment.kickOffTeamAway;
import static dk.reflevel.fragments.MainScreenFragment.kickOffTeamHome;
import static dk.reflevel.fragments.MainScreenFragment.mTimeFormatter;
import static dk.reflevel.fragments.MainScreenFragment.setProgressBarValues;
import static dk.reflevel.fragments.MainScreenFragment.t1;
import static dk.reflevel.fragments.MainScreenFragment.textViewTime;
import static dk.reflevel.fragments.MainScreenFragment.timeCountInMilliSeconds;
import static dk.reflevel.fragments.MainScreenFragment.timerStatus;

/**
 * Created by Rimon Nassory on 16-04-2017.
 */

public class Option1Page extends Fragment {

    private static final String TAG = "Option1Page";

    public static Button startHalf, endHalf, resetBtn, setTimeBtn, btnSetting, selectKickoffTeam, subBtn;
    View view;
    private SharedPref sharedPref;
    private List<CardModel> list;

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.screen3, container, false);

        sharedPref = new SharedPref(getActivity());

        subBtn = (Button) view.findViewById(R.id.sub_btn);
        subBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SubstitutionActivity.class);
                getActivity().startActivity(intent);
            }
        });

        //Start Half Button
        startHalf = (Button) view.findViewById(R.id.btn_start_half);
        startHalf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnSetting = (Button) view.findViewById(R.id.btn_setting);
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                getActivity().startActivity(intent);
            }
        });

        /*
        setTimeBtn = (Button) view.findViewById(R.id.set_time_btn);
        setTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SetTimeActivity.class);
                startActivity(intent);
            }
        });
*/
        endHalfButton();
        resetButton();
/*
        selectKickoffTeam = (Button) view.findViewById(R.id.kickoff_team);
        selectKickoffTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KickoffTeam();
            }
        });
*/
        return view;
    }

    private void KickoffTeam() {
        Intent intent = new Intent(getActivity(), KickOffTeam.class);
        getActivity().startActivity(intent);
    }

    public void resetButton() {
        //Reset Button
        //Button Reset option
        resetBtn = (Button) view.findViewById(R.id.reset_btn);
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                builder1.setMessage("Reset all Score and Time?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                resetScoreAndTime();
                            }
                        });

                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        });
    }

    public void endHalfButton() {
        //End half Button
        endHalf = (Button) view.findViewById(R.id.btn_end_half);
        endHalf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


//                startActivity(new Intent(getActivity(), PenaltyActivity.class));
                if (firstSecondHalfText.getText().equals("1st Half")) {

                    endDataScreen();

                } else {


                    final AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                    builder1.setMessage("Stop time?");
                    builder1.setCancelable(true);
                    builder1.setPositiveButton(
                            "End",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    endHalf();
                                }
                            });

                    builder1.setNeutralButton(
                            "Penalties",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    startActivity(new Intent(getActivity(), PenaltyTeamActivity.class));
                                }
                            });

                    builder1.setNegativeButton(
                            "Extra time",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    endHalf();
//                                startActivity(new Intent(getActivity(), MainActivity.class));
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }
            }
        });
    }

    public void endHalf() {
        Intent intnet = new Intent("SOME_ACTION");
        intnet.putExtra("save", true);
        getActivity().sendBroadcast(intnet);


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
        timerStatus = MainScreenFragment.TimerStatus.STOPPED;

        if (firstSecondHalfText.getText().equals("1st Half")) {
            firstSecondHalfText.setText("Start 2nd Half");
        } else if (firstSecondHalfText.getText().equals("2nd Half")) {
            firstSecondHalfText.setText("Kick Off");
            kickOffTeamHome.setVisibility(View.GONE);
            kickOffTeamAway.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(textViewTime.getText())) {
            saveTimerString("TimerTextView", textViewTime.getText().toString(),sharedPref);
        }

        if (!TextUtils.isEmpty(firstSecondHalfText.getText())) {
            saveHalfTimeString("firstSecondHalfTextView", firstSecondHalfText.getText().toString(),getActivity());
        }

        Tracker.getSharedInstance().stopTrackingLocation();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                list = StoreData.getList(sharedPref);
                Log.e(TAG, "onClick: Endtime====" + list.size());
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
                getActivity().sendBroadcast(intnet);


                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("time", textViewTime.getText());
                intent.putExtra("halfTimeTxt", firstSecondHalfText.getText());
                startActivity(intent);
                getActivity().finish();

            }
        }, 700);
    }


    //To Save value
    public static void saveTimerString(String key, String value,SharedPref sharedPref) {
        /*
        SharedPreferences saveScore = getContext().getSharedPreferences("saveTime", android.content.Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = saveScore.edit();
		editor.putString(key, value);
		editor.commit();
		*/
        sharedPref.setDataInPref(Global.SAVE_TIME, value);
    }

    public static void saveHalfTimeString(String key, String value, Context context) {
        SharedPreferences saveScore = context.getSharedPreferences("saveHalfTime", android.content.Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = saveScore.edit();
        editor.putString(key, value);
        editor.commit();
    }

    //This will reset the Scores, the progress bar and the first-second half text
    public void resetScoreAndTime() {
        getActivity().getSharedPreferences("saveScore", 0).edit().clear().commit();
        getActivity().getSharedPreferences("saveScores", 0).edit().clear().commit();
        getActivity().getSharedPreferences("saveHalfTime", 0).edit().clear().commit();
        getActivity().getSharedPreferences("saveBookingCardChoice", 0).edit().clear().commit();


        String STORE_MINUTE = sharedPref.getDataFromPref(Global.STORE_MINUTE, "00:00");
        sharedPref.clearAllPref();
        sharedPref.setDataInPref(Global.STORE_MINUTE, STORE_MINUTE);
        Intent intnet = new Intent("SOME_ACTION");
        getActivity().sendBroadcast(intnet);

        t1.setText("0");
        MainScreenFragment.t2.setText("0");

        new SharedPref(getActivity()).clearAllPref();
        Tracker.resetState(getContext());

        setProgressBarValues();

        if (MainScreenFragment.countDownTimer != null) {
            MainScreenFragment.countDownTimer.cancel();
        }

        textViewTime.setEnabled(true);

        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().finish();


    }

    private void endDataScreen() {

        final AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
        builder1.setMessage("Stop time?");
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        endHalf();

                       /* Intent intnet = new Intent("SOME_ACTION");
                        intnet.putExtra("save", true);
                        getActivity().sendBroadcast(intnet);


                        focus.stop();
                        focus.setVisibility(View.GONE);

                        textViewTime.setVisibility(View.VISIBLE);
                        textViewTime.setText(mTimeFormatter(timeCountInMilliSeconds) + ":00");

                        setProgressBarValues();

                        startHalf.setVisibility(View.VISIBLE);
                        setTimeBtn.setEnabled(true);
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
                        timerStatus = MainScreenFragment.TimerStatus.STOPPED;

                        if (firstSecondHalfText.getText().equals("1st Half")) {
                            firstSecondHalfText.setText("Start 2nd Half");
                        } else if (firstSecondHalfText.getText().equals("2nd Half")) {
                            firstSecondHalfText.setText("Kick Off");
                            kickOffTeamHome.setVisibility(View.GONE);
                            kickOffTeamAway.setVisibility(View.GONE);
                        }

                        if (!TextUtils.isEmpty(textViewTime.getText())) {
                            saveTimerString("TimerTextView", textViewTime.getText().toString());
                        }

                        if (!TextUtils.isEmpty(firstSecondHalfText.getText())) {
                            saveHalfTimeString("firstSecondHalfTextView", firstSecondHalfText.getText().toString());
                        }

                        Tracker.getSharedInstance().stopTrackingLocation();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                list = StoreData.getList(sharedPref);
                                Log.e(TAG, "onClick: Endtime====" + list.size());
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
                                getActivity().sendBroadcast(intnet);


                                Intent intent = new Intent(getContext(), MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("time", textViewTime.getText());
                                intent.putExtra("halfTimeTxt", firstSecondHalfText.getText());
                                startActivity(intent);
                                getActivity().finish();

                            }
                        }, 700);*/
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }


}