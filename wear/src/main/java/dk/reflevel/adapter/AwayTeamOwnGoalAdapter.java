package dk.reflevel.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import dk.reflevel.R;
import dk.reflevel.model.PenaltyUser;

public class AwayTeamOwnGoalAdapter extends RecyclerView.Adapter<AwayTeamOwnGoalAdapter.MyViewHolder> {

    private Context context;
    private List<PenaltyUser> penaltyUsersList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        int position = 0;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                  //  Toast.makeText(context, "Click====" + position, Toast.LENGTH_SHORT).show();

                    for (int i = 0; i < penaltyUsersList.size(); i++) {
                        PenaltyUser model = penaltyUsersList.get(i);
                        if (i == position)
                            model.setSelected(true);
                        else
                            model.setSelected(false);
                        penaltyUsersList.set(i, model);
                    }
                    notifyDataSetChanged();
                }
            });
        }
    }


    public AwayTeamOwnGoalAdapter(Context context, List<PenaltyUser> PenaltyUsersList) {
        this.context = context;
        this.penaltyUsersList = PenaltyUsersList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_penalty_user, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        PenaltyUser penaltyUser = penaltyUsersList.get(position);
        holder.title.setText(penaltyUser.getTitle());
        holder.position = position;
        if (penaltyUser.isSelected()) {
            holder.title.setTextColor(ContextCompat.getColor(context, R.color.selected_color));
        } else {
            holder.title.setTextColor(ContextCompat.getColor(context, R.color.white));
        }

    }

    @Override
    public int getItemCount() {
        return penaltyUsersList.size();
    }
}