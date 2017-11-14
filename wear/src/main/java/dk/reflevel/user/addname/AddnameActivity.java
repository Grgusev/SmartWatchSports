package dk.reflevel.user.addname;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dk.reflevel.R;
import dk.reflevel.common.SharedPref;

public class AddnameActivity extends Activity {

    @BindView(R.id.textEnterName)
    EditText textEnterName;
    SharedPref sharedPref;
    ArrayList<String> dataList = new ArrayList<>();
    String goalNumber = "00";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addname);
        ButterKnife.bind(this);
        sharedPref = new SharedPref(AddnameActivity.this);

        isFromGoal = getIntent().getBooleanExtra("isFromGoal", false);

        if (isFromGoal) {
            dataList = sharedPref.getArrayPref(SharedPref.listHome);
            goalNumber = getIntent().getStringExtra("number");
        } else
            dataList = sharedPref.getArrayPref(SharedPref.subAwayList);
    }

    boolean isFromGoal;

    @OnClick(R.id.btnYes)
    public void onViewClicked() {
        if (textEnterName.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Enter name", Toast.LENGTH_SHORT).show();
            return;
        }

        dataList.add("Nr: "+goalNumber+" "+textEnterName.getText().toString().trim());
        if (isFromGoal) {
            sharedPref.setArrayPref(SharedPref.listHome, dataList);
            finish();
        } else {
            sharedPref.setArrayPref(SharedPref.subAwayList, dataList);
            startActivity(new Intent(AddnameActivity.this, RadioNameActivity.class));
            finish();
        }
    }
}
