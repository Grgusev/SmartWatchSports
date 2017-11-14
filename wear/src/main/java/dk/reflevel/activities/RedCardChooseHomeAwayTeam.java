package dk.reflevel.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import dk.reflevel.R;
import dk.reflevel.activities.redcard.AwayRedCardBooking;
import dk.reflevel.activities.redcard.HomeRedCardBooking;
import dk.reflevel.common.SharedPref;

public class RedCardChooseHomeAwayTeam extends Activity {

    Button btnHomeRed, btnAwayRed;
    public static TextView redCardTxtHome, redCardTxtAway;
    SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_red_card_choose_home_away_team);

        sharedPref = new SharedPref(RedCardChooseHomeAwayTeam.this);
        final String isFrom = getIntent().getStringExtra("isFrom");

        btnHomeRed = (Button) findViewById(R.id.red_card_home_team_booking_btn);
        btnHomeRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    redCardTxtHome = (TextView) findViewById(R.id.red_card_txt_home);
                    redCardTxtHome.setText("Home Team");

                    Intent intent = new Intent(RedCardChooseHomeAwayTeam.this, HomeRedCardBooking.class);
                    intent.putExtra("redCardHomeTeamSelected", redCardTxtHome.getText());
                    intent.putExtra("isFrom", isFrom);
                    startActivity(intent);
                    finish();

            }
        });

        btnAwayRed = (Button) findViewById(R.id.red_card_away_team_booking_btn);
        btnAwayRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    redCardTxtAway = (TextView) findViewById(R.id.red_card_txt_away);
                    redCardTxtAway.setText("Away Team");
                    Intent intent = new Intent(RedCardChooseHomeAwayTeam.this, AwayRedCardBooking.class);
                    intent.putExtra("redCardAwayTeamSelected", redCardTxtAway.getText());
                    intent.putExtra("isFrom", isFrom);
                    startActivity(intent);
                    finish();

            }
        });
    }
}
