package dk.reflevel.user.penalty;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.OnClick;
import dk.reflevel.R;

public class EndPenaltiesDialogActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_penalties);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btnRemovePenaltyYes, R.id.btnRemovePenaltyNO})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnRemovePenaltyYes:
                break;
            case R.id.btnRemovePenaltyNO:
                break;
        }
    }
}
