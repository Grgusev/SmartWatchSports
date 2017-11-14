package dk.reflevel.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dk.reflevel.MainActivity;
import dk.reflevel.R;

public class RemoveGoalDialogActivity extends Activity {

    @BindView(R.id.removeGoalName)
    TextView removeGoalName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_goal_dialog);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btnRemoveGoalNO, R.id.btnRemoveGoalYes})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnRemoveGoalNO:
                startActivity(new Intent(RemoveGoalDialogActivity.this, RemoveGoallistingActivity.class));
                finish();
                break;
            case R.id.btnRemoveGoalYes:
                startActivity(new Intent(RemoveGoalDialogActivity.this, MainActivity.class));
                finish();
                break;
        }
    }
}
