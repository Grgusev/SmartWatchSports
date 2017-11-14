package dk.reflevel.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import dk.reflevel.MainActivity;
import dk.reflevel.R;

import static dk.reflevel.MainActivity.mVerticalPager;
import static dk.reflevel.fragments.MainScreenFragment.kickOffTeamAway;
import static dk.reflevel.fragments.MainScreenFragment.kickOffTeamHome;

public class KickOffTeam extends Activity {

    Button homeKickOff, awayKickoff;
    public static boolean kickOffTeamSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kick_off_team);

        homeKickOff = (Button) findViewById(R.id.home_kickoff);
        awayKickoff = (Button) findViewById(R.id.away_kickoff);

        homeKickOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeTeamSelected();
            }
        });

        awayKickoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AwayTeamSelected();
            }
        });
    }

    public void HomeTeamSelected () {

        kickOffTeamSelected = true;
        kickOffTeamHome.setVisibility(View.VISIBLE);

        mVerticalPager.scrollDown();

        Intent intent = new Intent(KickOffTeam.this, MainActivity.class);
        startActivity(intent);
    }

    public void AwayTeamSelected () {

        kickOffTeamSelected = true;
        kickOffTeamAway.setVisibility(View.VISIBLE);

        mVerticalPager.scrollDown();

        Intent intent = new Intent(KickOffTeam.this, MainActivity.class);
        startActivity(intent);
    }
}
