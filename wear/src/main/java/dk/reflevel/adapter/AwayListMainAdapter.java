package dk.reflevel.adapter;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import dk.reflevel.R;
import dk.reflevel.common.CardModel;
import dk.reflevel.common.SharedPref;

import static dk.reflevel.common.Global.calculate1ndHalf;
import static dk.reflevel.common.Global.calculate2ndHalf;

public class AwayListMainAdapter extends RecyclerView.Adapter<AwayListMainAdapter.MyViewHolder> {

    private static final String TAG = "HomeListMainAdapter";
    SharedPref sharedPref;
    private Context context;
    List<CardModel> stringList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textView, textClock;
        ImageView imageView;
        int position = 0;

        public MyViewHolder(View view) {
            super(view);
            textView = (TextView) view.findViewById(R.id.tvTitle);
            imageView = (ImageView) view.findViewById(R.id.viewCard);
            textClock = (TextView) view.findViewById(R.id.time_clock);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }


    public AwayListMainAdapter(Context context, List<CardModel> list) {
        this.context = context;
        this.stringList = list;
        sharedPref = new SharedPref(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_card_data, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.position = position;
        CardModel model = stringList.get(position);

        holder.textView.setText(stringList.get(position).getName());
        String str = holder.textView.getText().toString().trim();
        if (str.length() != 0) {
            str = str.replace("Home Team", "");
            str = str.replace("Away Team", "");
            str = str.replace("Nr. ", "");
            holder.textView.setText(str);
        }

        if ("red".equalsIgnoreCase(stringList.get(position).getCardColor())) {
            holder.textView.setTextColor(ContextCompat.getColor(context, R.color.black));
            holder.imageView.setBackgroundResource(R.drawable.card_red_shape);
            if (stringList.get(position).getHlafName().equalsIgnoreCase("2nd Half")) {
                holder.textClock.setText(calculate2ndHalf(stringList.get(position).getCurrentTimer(), stringList.get(position).getHlafTime()));
            } else {
                holder.textClock.setText(calculate1ndHalf(stringList.get(position).getCurrentTimer(), stringList.get(position).getHlafTime()));
            }
        } else {
            holder.textView.setTextColor(ContextCompat.getColor(context, R.color.black));
            holder.imageView.setBackgroundResource(R.drawable.card_yellow_shape);
            setDataUpdate(holder.textClock, stringList.get(position), position);
        }
        if (stringList.get(position).isAddeddata()) {
            holder.imageView.setBackgroundResource(R.drawable.image_yellow_red);
        }
    }

    private void setDataUpdate(final TextView textView, final CardModel model, final int position) {
        Log.d(TAG, "setDataUpdate: " + model.getExtraTimer() + " - " + " " + model.getCurrentTimer() + " -  " + model.getTimer() + "===" + model.isTimerWork());
        if (model.getExtraTimer().equalsIgnoreCase("")) {
            Log.d(TAG, "No data: ");
            model.setExtraTimer("00:00");

            if (model.getHlafName().equalsIgnoreCase("2nd Half")) {
                textView.setText(calculate2ndHalf(model.getCurrentTimer(), model.getHlafTime()));
            } else {
                textView.setText(calculate1ndHalf(model.getCurrentTimer(), model.getHlafTime()));
            }
        } else if (model.getExtraTimer().equalsIgnoreCase("00:00")) {
            Log.d(TAG, "00:00: " + position);
//            textView.setText(calculateTime(model.getCurrentTimer(), model.getHlafTime()));
            if (model.getHlafName().equalsIgnoreCase("2nd Half")) {
                textView.setText(calculate2ndHalf(model.getCurrentTimer(), model.getHlafTime()));
            } else {
                textView.setText(calculate1ndHalf(model.getCurrentTimer(), model.getHlafTime()));
            }
        } else if (model.getTimer().equalsIgnoreCase("00:00")) {
            Log.d(TAG, "00:00: " + position);
//            textView.setText(calculateTime(model.getCurrentTimer(), model.getHlafTime()));
            if (model.getHlafName().equalsIgnoreCase("2nd Half")) {
                textView.setText(calculate2ndHalf(model.getCurrentTimer(), model.getHlafTime()));
            } else {
                textView.setText(calculate1ndHalf(model.getCurrentTimer(), model.getHlafTime()));
            }
        } else if (model.isTimerWork()) {
//            String data = Global.calculateTime(model.getTimer(), model.getExtraTimer());
//            Log.d(TAG, "position: " + model.getTimer() + " " + data);
            final long totalTime = sharedPref.getMilisecound(model.getTimer());
//            final long ta = sharedPref.getMilisecound(model.getTimer());

            new CountDownTimer(totalTime, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
//                    Log.d("seconds elapsed: ", "" + ((totalTime - millisUntilFinished) + ta) / 1000);
                    String data = msTimeFormatter((millisUntilFinished));
                    textView.setText(data);
                    Log.d("-->", "+-+" + model.getName() + "  " + data);
                    if (!stringList.isEmpty()) {
                        CardModel m = stringList.get(position);
                        m.setTimer(data + "");
                        m.setTimerWork(true);
                        stringList.set(position, m);
                    }
//                    sharedPref.setDataInPref(STORE_DATA, new Gson().toJson(list));
                }

                @Override
                public void onFinish() {
                    Log.e("-->", "+-+" + model.getName() + "  " + "0:0");
//                    textView.setText(Global.calculateTime(model.getCurrentTimer(), model.getHlafTime()));
                    if (model.getHlafName().equalsIgnoreCase("2nd Half")) {
                        textView.setText(calculate2ndHalf(model.getCurrentTimer(), model.getHlafTime()));
                    } else {
                        textView.setText(calculate1ndHalf(model.getCurrentTimer(), model.getHlafTime()));
                    }

                    if (!stringList.isEmpty()) {
                        CardModel m = stringList.get(position);
                        m.setTimer("00:00");
                        m.setTimerWork(false);
                        stringList.set(position, m);

                    }
                }
            }.start();
        }

        if (!model.isTimerWork()) {
            if (!model.getTimer().equalsIgnoreCase("00:00")) {
                textView.setText(model.getTimer());
            }
        }
    }

    private String msTimeFormatter(long milliSeconds) {
        String hms = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));
        return hms;
    }

    @Override
    public int getItemCount() {
        return stringList.size();
    }
}