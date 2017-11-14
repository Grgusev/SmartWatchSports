package dk.reflevel.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import dk.reflevel.R;
import dk.reflevel.activities.yellowcard.AwayYellowCardBooking;
import dk.reflevel.activities.yellowcard.HomeYellowCardBooking;
import dk.reflevel.common.SharedPref;

public class ChooseHomeAwayTeam extends Activity {

    Button btnHomeYellow, btnAwayYellow;
    public static TextView txtHome, txtAway;
    SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_home_away_team);

        sharedPref = new SharedPref(ChooseHomeAwayTeam.this);
        final String isFrom = getIntent().getStringExtra("isFrom");

        btnHomeYellow = (Button) findViewById(R.id.home_team_booking_btn);
        btnHomeYellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    txtHome = (TextView) findViewById(R.id.txt_home);
                    txtHome.setText("Home Team");

                    Intent intent = new Intent(ChooseHomeAwayTeam.this, HomeYellowCardBooking.class);
                    intent.putExtra("homeTeamSelected", txtHome.getText());
                    intent.putExtra("isFrom", isFrom);
                    startActivity(intent);
                    finish();
            }
        });

        btnAwayYellow = (Button) findViewById(R.id.away_team_booking_btn);
        btnAwayYellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    txtAway = (TextView) findViewById(R.id.txt_away);
                    txtAway.setText("Away Team");

                    Intent intent = new Intent(ChooseHomeAwayTeam.this, AwayYellowCardBooking.class);
                    intent.putExtra("awayTeamSelected", txtAway.getText());
                    intent.putExtra("isFrom", isFrom);
                    startActivity(intent);
                    finish();
            }
        });

    }
}
