package dk.reflevel.user.addname;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dk.reflevel.R;
import dk.reflevel.adapter.TeamAdapter;
import dk.reflevel.common.SharedPref;
import dk.reflevel.model.PenaltyUser;

public class RadioNameActivity extends Activity {

    @BindView(R.id.recycleUserList)
    RecyclerView recycleUserList;
    @BindView(R.id.layout)
    LinearLayout layout;
    SharedPref sharedPref;
    List<PenaltyUser> penaltyUserList = new ArrayList<>();
    ArrayList<String> dataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_penalty_userlisting);
        ButterKnife.bind(this);
        sharedPref = new SharedPref(RadioNameActivity.this);
        layout.setVisibility(View.GONE);
        ArrayList<String>  dataList = sharedPref.getArrayPref(SharedPref.subAwayList);

        for (int i = 0; i < dataList.size(); i++) {
            PenaltyUser model = new PenaltyUser();
            model.setTitle(dataList.get(i));
            penaltyUserList.add(model);
        }

        recycleUserList.setAdapter(new TeamAdapter(this, penaltyUserList));
        recycleUserList.setLayoutManager(new LinearLayoutManager(this));
    }
}
