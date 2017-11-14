package dk.reflevel.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import dk.reflevel.R;

public class PenaltyTeam1Adapter extends RecyclerView.Adapter<PenaltyTeam1Adapter.MyViewHolder> {

    private Context context;
    private List<String> list;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        int position = 0;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }
    }


    public PenaltyTeam1Adapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_penalty_user, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.position = position;
        holder.title.setText(list.get(position));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}