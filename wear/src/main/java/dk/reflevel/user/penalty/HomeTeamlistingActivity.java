package dk.reflevel.user.penalty;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dk.reflevel.R;
import dk.reflevel.adapter.PenaltyUserAdapter;
import dk.reflevel.model.PenaltyUser;

public class HomeTeamlistingActivity extends Activity {

    List<PenaltyUser> penaltyUserList = new ArrayList<>();
    @BindView(R.id.recycleUserList)
    RecyclerView recyclerView;
    PenaltyUserAdapter penaltyUserAdapter;
    @BindView(R.id.btnCancel)
    Button btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_penalty_userlisting);
        ButterKnife.bind(this);

        btnCancel.setVisibility(View.GONE);
        penaltyUserAdapter = new PenaltyUserAdapter(HomeTeamlistingActivity.this,penaltyUserList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(penaltyUserAdapter);

        PenaltyUser penaltyUser = new PenaltyUser();
        penaltyUser.setTitle("Simon");
        penaltyUserList.add(penaltyUser);
        penaltyUser.setTitle("Andreas");
        penaltyUserList.add(penaltyUser);
        penaltyUser.setTitle("Paulo");
        penaltyUserList.add(penaltyUser);
        penaltyUser.setTitle("Ponaldo");
        penaltyUserList.add(penaltyUser);
        penaltyUserAdapter.notifyDataSetChanged();
    }

    @OnClick({R.id.btnOk, R.id.btnCancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnOk:
//                startActivity(new Intent(HomeTeamlistingActivity.this, PenaltyActivity.class));
//                finish();
                break;

        }
    }
}
