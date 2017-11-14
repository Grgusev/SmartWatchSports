package dk.reflevel.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import dk.reflevel.MainActivity;
import dk.reflevel.R;

public class HometeamSubActivity extends Activity {

    @BindView(R.id.textInteam)
    TextView textInteam;
    @BindView(R.id.textOutteam)
    TextView textOutteam;

    Handler handler;
    Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hometeam_sub);
        ButterKnife.bind(this);

        String sub_in = getIntent().getStringExtra("sub_in");
        String sub_out = getIntent().getStringExtra("sub_out");
        textInteam.setText(sub_in);
        textOutteam.setText(sub_out);

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(HometeamSubActivity.this, MainActivity.class));
                finish();
            }
        };
        handler.postDelayed(runnable, 2000);
    }


}
