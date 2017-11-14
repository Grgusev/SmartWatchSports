package dk.reflevel.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextClock;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;
import java.util.concurrent.TimeUnit;

import dk.reflevel.R;
import dk.reflevel.Tracker.BasisPoint;
import dk.reflevel.Tracker.Tracker;
import dk.reflevel.activities.ChooseHomeAwayTeam;
import dk.reflevel.activities.RedCardChooseHomeAwayTeam;
import dk.reflevel.activities.SetTimeActivity;
import dk.reflevel.common.AlertCustomDialog;
import dk.reflevel.common.CardModel;
import dk.reflevel.common.Global;
import dk.reflevel.common.SharedPref;
import dk.reflevel.common.StoreData;
import dk.reflevel.user.AddgoalActivity;
import dk.reflevel.user.RemoveGoalActivity;

import static dk.reflevel.MainActivity.mVerticalPager;

/**
 * Created by Rimon Nassory on 16-04-2017.
 */


public class MainScreenFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "MainScreenFragment";
    public static String CURRENT_TIME = "0:0";
    private SharedPref sharedPref;

    public enum TimerStatus {
        STARTED,
        STOPPED
    }

    public static TimerStatus timerStatus = TimerStatus.STOPPED;

    public static ProgressBar progressBarCircle;
    public static TextView textViewTime;

    protected static CountDownTimer countDownTimer;
    public static TextView t1;
    public static TextView t2;
    public static TextView firstSecondHalfText;
    private Vibrator v;
    public static Chronometer focus;
    View view, viewHomeActive, viewAwayActive;
    public static Button btnRed, btnYellow;
    public static LinearLayout homeScoreBoard, awayScoreBoard, kickOffTeamHome, kickOffTeamAway, kickoffTime;

    public static long timeCountInMilliSeconds = 1 * 60000;

    private BroadcastReceiver receiver;

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.screen1, container, false);

        sharedPref = new SharedPref(getActivity());

        // method call to initialize the views
        initViews();
        // method call to initialize the listeners
        initListeners();
        homeScreenLayout();

        TextClock digital = (TextClock) view.findViewById(R.id.digital_clock);
        digital.setFormat12Hour(null);
        digital.setFormat24Hour("HH:mm");

        kickoffTime = (LinearLayout) view.findViewById(R.id.kickoff_time);

        kickoffTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SetTimeActivity.class);
                startActivity(intent);
            }
        });

        viewAwayActive = (View) view.findViewById(R.id.viewAwayActive);
        viewHomeActive = (View) view.findViewById(R.id.viewHomeActive);
        kickOffTeamHome = (LinearLayout) view.findViewById(R.id.kickoff_home_team_selected);
        kickOffTeamHome.bringToFront();

        kickOffTeamAway = (LinearLayout) view.findViewById(R.id.kickoff_away_team_selected);
        kickOffTeamAway.bringToFront();

        kickOffTeamHome.setVisibility(View.VISIBLE);
        kickOffTeamAway.setVisibility(View.VISIBLE);

        return view;
    }

    public void homeScreenLayout() {
        homeScoreBoard = (LinearLayout) view.findViewById(R.id.home_score_board);
        homeScoreBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertCustomDialog.addGoalDialog(getActivity(), "Add Home Goal?", new AlertCustomDialog.AlertUtilsNegativeListener() {
                    @Override
                    public void onPositiveButtonClick() {
                        if (sharedPref.getBoolean(Global.IS_ADVANCE_GOAL)) {
                            startActivity(new Intent(getActivity(), AddgoalActivity.class).putExtra("isType", "home").putExtra("goalType", Global.PLUS));
                        } else {
                            homeGoal(Global.PLUS,sharedPref);

                        }
                    }

                    @Override
                    public void onNegativeButtonClick() {
                        if (sharedPref.getBoolean(Global.IS_ADVANCE_GOAL)) {
                            startActivity(new Intent(getActivity(), RemoveGoalActivity.class).putExtra("isType", "home").putExtra("goalType", Global.MINUS));
                        } else {
                            homeGoal(Global.MINUS,sharedPref);

                        }
                    }

                });

            }
        });

        awayScoreBoard = (LinearLayout) view.findViewById(R.id.away_score_board);
        awayScoreBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertCustomDialog.addGoalDialog(getActivity(), "Add Away Goal?", new AlertCustomDialog.AlertUtilsNegativeListener() {
                    @Override
                    public void onPositiveButtonClick() {
                        if (sharedPref.getBoolean(Global.IS_ADVANCE_GOAL)) {
                            startActivity(new Intent(getActivity(), AddgoalActivity.class).putExtra("isType", "away").putExtra("goalType", Global.PLUS));
                        } else {
                            awayGoal(Global.PLUS,sharedPref);


                        }
                    }

                    @Override
                    public void onNegativeButtonClick() {
                        if (sharedPref.getBoolean(Global.IS_ADVANCE_GOAL)) {
                            startActivity(new Intent(getActivity(), RemoveGoalActivity.class).putExtra("isType", "away").putExtra("goalType", Global.MINUS));
                        } else {

                            awayGoal(Global.MINUS,sharedPref);
                        }
                    }
                });
            }
        });

        final Intent intent3 = getActivity().getIntent();
        String str3 = intent3.getStringExtra("time");
        textViewTime.setText(str3);

        final Intent intent4 = getActivity().getIntent();
        String str4 = intent4.getStringExtra("halfTimeTxt");
        firstSecondHalfText.setText(str4);

        textViewTime.setText(getTimerString("TimerTextView"));

        firstSecondHalfText.setText(getHalfTimeString("firstSecondHalfTextView"));

        final Intent intent = getActivity().getIntent();
        String str = intent.getStringExtra("home");
        t1.setText(str);

        t1.setText(getString("homeTextView"));

        Intent intent2 = getActivity().getIntent();
        String str2 = intent2.getStringExtra("away");
        t2.setText(str2);

        t2.setText(getString("awayTextView"));

        if (t1.getText().equals("") || t1.getText() == null) {
            String text = "0";
            t1.setText(text);
        }

        if (t2.getText().equals("") || t2.getText() == null) {
            String text = "0";
            t2.setText(text);
        }

        if (getTimerString("TimerTextView").isEmpty() || getTimerString("TimerTextView").equalsIgnoreCase("00:00")) {
            String text = "45:00";
            textViewTime.setText(text);
        }

        if (firstSecondHalfText.getText().equals("") || firstSecondHalfText.getText() == null) {
            String text = "Kick Off";
            firstSecondHalfText.setText(text);
        }

        btnYellow = (Button) view.findViewById(R.id.btn_yellow);
        btnYellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getActivity(), ChooseHomeAwayTeam.class);
                intent1.putExtra("isFrom", "yellow");
                startActivity(intent1);
            }
        });

        btnRed = (Button) view.findViewById(R.id.btn_red);
        btnRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getActivity(), RedCardChooseHomeAwayTeam.class);
                intent1.putExtra("isFrom", "red");
                startActivity(intent1);
            }
        });

        Option1Page.endHalf.setEnabled(false);
    }

    public static void awayGoal(String type,SharedPref sharedPref) {
        String presentValStr = t2.getText().toString();
        int presentIntVal = Integer.parseInt(presentValStr);


        if (type.equalsIgnoreCase(Global.PLUS))
            presentIntVal++;
        else
            presentIntVal--;
        t2.setText(String.valueOf(presentIntVal));

        if (!TextUtils.isEmpty(t2.getText())) {
            sharedPref.setDataInPref("awayTextView", t2.getText().toString());


        }
    }

    public static void homeGoal(String type,SharedPref sharedPref) {
        String presentValStr = t1.getText().toString();
        int presentIntVal = Integer.parseInt(presentValStr);

        if (type.equalsIgnoreCase(Global.PLUS))
            presentIntVal++;
        else
            presentIntVal--;
        t1.setText(String.valueOf(presentIntVal));

        if (!TextUtils.isEmpty(t1.getText())) {
            sharedPref.setDataInPref("homeTextView", t1.getText().toString());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("-->>>", "onResume");
        IntentFilter filter = new IntentFilter();
        filter.addAction("action_time");
        getActivity().registerReceiver(receiver, filter);
        setActiveTeamColor();

        //  getCheckBoxState();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            getActivity().unregisterReceiver(receiver);
            receiver = null;
        }
    }

	/*
    public boolean getCheckBoxState(){
        SharedPreferences sp = getContext().getSharedPreferences("advanced",Context.MODE_PRIVATE);
        final boolean checkState = sp.getBoolean("checkState",false);
        return checkState;
	}
	*/

    //To Save value
    private void saveString(String key, String value) {

        sharedPref.setDataInPref(key, value);

//        SharedPreferences saveScore = getContext().getSharedPreferences("saveScore", android.content.Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = saveScore.edit();
//        editor.putString(key, value);
//        editor.commit();
    }

    private String getString(String key) {
//        SharedPreferences saveScore = getContext().getSharedPreferences("saveScore", Context.MODE_PRIVATE);
        return sharedPref.getDataFromPref(key, "");
    }

    private String getTimerString(String key) {
        /*
        SharedPreferences saveScore = getContext().getSharedPreferences("saveTime", Context.MODE_PRIVATE);
		return saveScore.getString(key, "");
		*/
        return sharedPref.getDataFromPref(Global.SAVE_TIME, "00:00");
    }

    private String getHalfTimeString(String key) {
        SharedPreferences saveScore = getContext().getSharedPreferences("saveHalfTime", Context.MODE_PRIVATE);
        return saveScore.getString(key, "");
    }

    /**
     * method to initialize the views
     */
    private void initViews() {
        t1 = (TextView) view.findViewById(R.id.homeTeamScore);
        t2 = (TextView) view.findViewById(R.id.awayTeamScore);
        progressBarCircle = (ProgressBar) view.findViewById(R.id.progressBarCircle);
        textViewTime = (TextView) view.findViewById(R.id.timer);
        firstSecondHalfText = (TextView) view.findViewById(R.id.first_second_half_text);
        focus = (Chronometer) view.findViewById(R.id.chronometer1);
    }

    /**
     * method to initialize the click listeners
     */
    private void initListeners() {
        Option1Page.startHalf.setOnClickListener(this);
    }

    /**
     * implemented method to listen clicks
     *
     * @param view
     */
    @Override
    public void onClick(View view) {

        Log.e(TAG, "onClick:--> " + firstSecondHalfText.getText());
        if (firstSecondHalfText.getText().toString().equals("Start 2nd Half")) {

            setupStart();
        } else {

            AlertCustomDialog.chooesTeamDialog(getActivity(), new AlertCustomDialog.AlertUtilsNegativeListener() {
                @Override
                public void onPositiveButtonClick() {
                    sharedPref.setBoolean(SharedPref.isHome, true);
                    sharedPref.setBoolean(SharedPref.isAway, false);
                    setActiveTeamColor();
                    setupStart();
                }

                @Override
                public void onNegativeButtonClick() {
                    sharedPref.setBoolean(SharedPref.isAway, true);
                    sharedPref.setBoolean(SharedPref.isHome, false);
                    setActiveTeamColor();
                    setupStart();
                }
            });
        }
    }

    private void setupStart() {
        List<CardModel> list = StoreData.getList(sharedPref);
        for (int i = 0; i < list.size(); i++) {
            CardModel model = list.get(i);
            if (!model.getTimer().equalsIgnoreCase("00:00")) {
                model.setTimerWork(true);
                list.set(i, model);
            }
        }
        sharedPref.setDataInPref(StoreData.STORE_DATA, new Gson().toJson(list));
        sharedPref.setBoolean(SharedPref.isTimerStart, true);
        Intent intnet = new Intent("SOME_ACTION");
        getActivity().sendBroadcast(intnet);

        mVerticalPager.scrollDown();
        startStop();
    }

    private void setActiveTeamColor() {
        if (viewHomeActive == null) return;
        if (sharedPref.getBoolean(SharedPref.isHome)) {
            viewHomeActive.setVisibility(View.VISIBLE);
            viewAwayActive.setVisibility(View.GONE);
        } else if (sharedPref.getBoolean(SharedPref.isAway)) {
            viewHomeActive.setVisibility(View.GONE);
            viewAwayActive.setVisibility(View.VISIBLE);
        } else {
            viewHomeActive.setVisibility(View.GONE);
            viewAwayActive.setVisibility(View.GONE);
        }
    }

    /**
     * method to start and stop count down timer
     */
    private void startStop() {
        // Start location service
        BasisPoint basisPoint1 = Tracker.loadBasisLocation(getActivity(), 0);
        BasisPoint basisPoint2 = Tracker.loadBasisLocation(getActivity(), 1);
        BasisPoint basisPoint3 = Tracker.loadBasisLocation(getActivity(), 2);
/*
        if (basisPoint1 == null || basisPoint2 == null || basisPoint3 == null) {
			// Not selected basis point yet.
			Toast.makeText(getContext(), "Basis points are not selected yet", Toast.LENGTH_LONG).show();
			return;
		} else {
			Toast.makeText(getContext(), "Half Started", Toast.LENGTH_SHORT).show();
		}
		*/

        //Tracker.getSharedInstance().startTrackingLocation();

        setTimerValues();

        // call to initialize the progress bar values
        setProgressBarValues();

        Option1Page.startHalf.setVisibility(View.GONE);
        Option1Page.endHalf.setVisibility(View.VISIBLE);
//		Option1Page.setTimeBtn.setEnabled(false);

        // changing the timer status to started
        timerStatus = TimerStatus.STARTED;
        // call to start the count down timer
        startCountDownTimer();
    }

    private void setTimerValues() {
        /*
        int time = 0;

		if (!textViewTime.getText().toString().isEmpty()) {
			// fetching value from edit text and type cast to integer
			time = Integer.parseInt(textViewTime.getText().toString().trim());
		}

		// assigning values after con½ng to milliseconds
		timeCountInMilliSeconds = time * 60 * 1000;
		*/

        String time = "00:00";
        if (!textViewTime.getText().toString().isEmpty()) {
            // fetching value from edit text and type cast to integer
            time = textViewTime.getText().toString().trim();
            Log.e("=====>>", "----> " + time);
            sharedPref.setDataInPref(Global.SAVE_TIME, time + "");
        }
        // assigning values after converting to milliseconds
        timeCountInMilliSeconds = sharedPref.getMilisecound(time);

        Log.e(TAG, "setTimerValues: " + timeCountInMilliSeconds);
    }

    /**
     * method to start count down timer
     */
    public void startCountDownTimer() {
        countDownTimer = new CountDownTimer(timeCountInMilliSeconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String data = msTimeFormatter(millisUntilFinished);
                textViewTime.setText(data);
                //textViewTime.setText(msTimeFormatter(millisUntilFinished));

                progressBarCircle.setProgress((int) (millisUntilFinished / 100));

                if (firstSecondHalfText.getText().equals("Start 2nd Half")) {
                    firstSecondHalfText.setText("2nd Half");
                } else if (firstSecondHalfText.getText().equals("Kick Off")) {
                    firstSecondHalfText.setText("1st Half");
                }

                CURRENT_TIME = data;
            }

            @Override
            public void onFinish() {
                textViewTime.setVisibility(View.GONE);
                focus.setVisibility(View.VISIBLE);
                Option1Page.endHalf.setEnabled(true);
                focus.setBase(SystemClock.elapsedRealtime() - 1);
                focus.start();
            }
        }.start();
        countDownTimer.start();
    }

    /**
     * method to set circular progress bar values
     */
    public static void setProgressBarValues() {
        progressBarCircle.setMax((int) timeCountInMilliSeconds / 100);
        progressBarCircle.setProgress((int) timeCountInMilliSeconds / 100);
    }

    /**
     * method to convert millisecond to time format
     *
     * @param milliSeconds
     * @return HH:mm:ss time formatted string
     */
    private String msTimeFormatter(long milliSeconds) {
        String hms = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));
        return hms;
    }

    public static String mTimeFormatter(long milliSeconds) {
        String hms = String.format("%d",
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)));
        return hms;
    }
}