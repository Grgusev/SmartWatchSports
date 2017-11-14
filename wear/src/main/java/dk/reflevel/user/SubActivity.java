package dk.reflevel.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dk.reflevel.R;
import dk.reflevel.adapter.SubAdapter;
import dk.reflevel.model.PenaltyUser;

public class SubActivity extends Activity {

    List<PenaltyUser> penaltyUserList = new ArrayList<>();
    @BindView(R.id.recycleUserList)
    RecyclerView recyclerView;
    SubAdapter subAdapter;
    @BindView(R.id.btnCancel)
    Button btnCancel;
    @BindView(R.id.tvTitle)
    TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_penalty_userlisting);
        ButterKnife.bind(this);

        btnCancel.setVisibility(View.GONE);
        tvTitle.setVisibility(View.VISIBLE);
        subAdapter = new SubAdapter(SubActivity.this, penaltyUserList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(subAdapter);

        tvTitle.setText("SUB OUT");

        List<String> myArrayList = Arrays.asList(getResources().getStringArray(R.array.testArray));
        for (int i = 0; i < myArrayList.size(); i++) {
            PenaltyUser penaltyUser = new PenaltyUser();
            penaltyUser.setTitle(myArrayList.get(i));
            penaltyUserList.add(penaltyUser);
        }

        subAdapter.notifyDataSetChanged();
    }

    private String sub_out = "";
    private String sub_in = "";

    public void selectedValue(int position) {
        if (!isClicked) {
            sub_out = penaltyUserList.get(position).getTitle();
        } else {
            sub_in = penaltyUserList.get(position).getTitle();
        }
    }

    boolean isClicked;

    @OnClick({R.id.btnOk, R.id.btnCancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnOk:
                if (!isClicked) {

                    if (sub_out.isEmpty()) {
                        Toast.makeText(this, "Select sign out", Toast.LENGTH_SHORT).show();
                        break;
                    }

                    isClicked = true;

                    for (int i = 0; i < penaltyUserList.size(); i++) {
                        PenaltyUser model = penaltyUserList.get(i);
                        model.setSelected(false);
                        penaltyUserList.set(i, model);
                    }

                    tvTitle.setText("SUB IN");
                    subAdapter.notifyDataSetChanged();
                } else {
                    if (sub_in.isEmpty()) {
                        Toast.makeText(this, "Select sign in", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    startActivity(new Intent(SubActivity.this, HometeamSubActivity.class).putExtra("sub_out", sub_out).putExtra("sub_in", sub_in));
                    finish();
                }
                break;
        }
    }
}
