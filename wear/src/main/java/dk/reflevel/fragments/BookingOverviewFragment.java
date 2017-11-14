package dk.reflevel.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import dk.reflevel.R;
import dk.reflevel.common.CardModel;
import dk.reflevel.common.SharedPref;
import dk.reflevel.common.StoreData;

import static dk.reflevel.common.Global.calculate1ndHalf;
import static dk.reflevel.common.Global.calculate2ndHalf;
import static dk.reflevel.common.StoreData.STORE_DATA;

/**
 * Created by Rimon Nassory on 24-04-2017.
 */

public class BookingOverviewFragment extends Fragment {

    public static ListView list_view_home, list_view_away;
    View view;
    SharedPref sharedPref;

    private BroadcastReceiver receiver;
    private MySimpleArrayAdapter adapter, adapter1;
    List<CardModel> list = new ArrayList<>();
    List<CardModel> listHome = new ArrayList<>();
    List<CardModel> listAway = new ArrayList<>();

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.screen5, container, false);

        list_view_home = (ListView) view.findViewById(R.id.list_view_home);
        list_view_away = (ListView) view.findViewById(R.id.list_view_away);

        sharedPref = new SharedPref(getActivity());
        list = StoreData.getList(sharedPref);

        StoreData.getDataList(listHome, listAway, list);

        list_view_home.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        list_view_away.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        setListViewHeightBasedOnChildren(list_view_home);
        list_view_home.setScrollContainer(true);

        adapter = new MySimpleArrayAdapter(getActivity(), listHome);
        list_view_home.setAdapter(adapter);

        setListViewHeightBasedOnChildren(list_view_away);
        list_view_away.setScrollContainer(true);

        adapter1 = new MySimpleArrayAdapter(getActivity(), listAway);
        list_view_away.setAdapter(adapter1);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //do something based on the intent's action
                if (sharedPref != null) {
                    if (intent.getBooleanExtra("save", false)) {
                        sharedPref.setDataInPref(STORE_DATA, new Gson().toJson(list));
                        return;
                    }
                    list.clear();
                    list.addAll(StoreData.getList(sharedPref));
                    StoreData.getDataList(listHome, listAway, list);
                    if (adapter != null)
                        adapter.notifyDataSetChanged();
                    if (adapter1 != null)
                        adapter1.notifyDataSetChanged();
                }
            }
        };
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("-->>", "--onPause---");
        sharedPref.setDataInPref(STORE_DATA, new Gson().toJson(list));
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("SOME_ACTION");
        getActivity().registerReceiver(receiver, filter);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            getActivity().unregisterReceiver(receiver);
            receiver = null;
        }
    }

    public class MySimpleArrayAdapter extends ArrayAdapter<CardModel> {
        private final Context context;
        List<CardModel> stringList;

        public MySimpleArrayAdapter(Context context, List<CardModel> stringList) {
            super(context, -1, stringList);
            this.context = context;
            this.stringList = stringList;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.row_card_data, parent, false);
            TextView textView = (TextView) rowView.findViewById(R.id.tvTitle);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.viewCard);
            TextView textClock = (TextView) rowView.findViewById(R.id.time_clock);
            textView.setText(stringList.get(position).getName());
            String str = textView.getText().toString().trim();

            if (str.length() != 0) {
                str = str.replace("Home Team", "");
                str = str.replace("Away Team", "");
                str = str.replace("Nr. ", "");
                textView.setText(str);
            }
            Log.e(TAG, "getView: " + position + "==" + stringList.get(position).isAddeddata());


            if ("red".equalsIgnoreCase(stringList.get(position).getCardColor())) {
                textView.setTextColor(ContextCompat.getColor(context, R.color.black));
                imageView.setBackgroundResource(R.drawable.card_red_shape);
                if (stringList.get(position).getHlafName().equalsIgnoreCase("2nd Half")) {
                    textClock.setText(calculate2ndHalf(stringList.get(position).getCurrentTimer(), stringList.get(position).getHlafTime()));
                } else {
                    textClock.setText(calculate1ndHalf(stringList.get(position).getCurrentTimer(), stringList.get(position).getHlafTime()));
                }
            } else {
                textView.setTextColor(ContextCompat.getColor(context, R.color.black));
                imageView.setBackgroundResource(R.drawable.card_yellow_shape);
                setDataUpdate(textClock, stringList.get(position), position);
            }
            if (stringList.get(position).isAddeddata()) {
                imageView.setBackgroundResource(R.drawable.image_yellow_red);
            }
            return rowView;
        }
    }

    private static final String TAG = "BookingOverviewFragment";

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

                    //changed by guusev
                    model.setTimer(data + "");
                    model.setTimerWork(true);

//                    if (!list.isEmpty()) {
//                        CardModel m = list.get(position);
//                        m.setTimer(data + "");
//                        m.setTimerWork(true);
//                        list.set(position, m);
//                    }
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

                    //changed by ggusev
                    model.setTimer("00:00");
                    model.setTimerWork(false);

//                    if (!list.isEmpty()) {
//                        CardModel m = list.get(position);
//                        m.setTimer("00:00");
//                        m.setTimerWork(false);
//                        list.set(position, m);
//
//                    }
                }
            }.start();
        }

        if (!model.isTimerWork()) {
            if (!model.getTimer().equalsIgnoreCase("00:00")) {
                textView.setText(model.getTimer());
            }
        }
    }

//    private void setDataUpdate(final TextView textView, final CardModel model, final int position) {
//        Log.d(TAG, "setDataUpdate: " + model.getExtraTimer() + " - " + " " + model.getCurrentTimer() + " -  " + model.getTimer() + "===" + model.isTimerWork());
//        if (model.getExtraTimer().equalsIgnoreCase("")) {
//            Log.d(TAG, "No data: ");
//            model.setExtraTimer("00:00");
//            if (model.getHlafName().equalsIgnoreCase("2nd Half")) {
//                textView.setText(calculate2ndHalf(model.getCurrentTimer(), model.getHlafTime()));
//            } else {
//                textView.setText(calculate1ndHalf(model.getCurrentTimer(), model.getHlafTime()));
//            }
//        } else if (!sharedPref.getBoolean(SharedPref.isTimerStart)) {
//            textView.setText("00:00");
//            CardModel m = list.get(position);
//            m.setTimer("00:00");
//            m.setTimerWork(false);
//            list.set(position, m);
//            sharedPref.setDataInPref(STORE_DATA, new Gson().toJson(list));
//        } else if (model.getExtraTimer().equalsIgnoreCase("00:00")) {
//            Log.d(TAG, "00:00: " + position);
//            if (model.getHlafName().equalsIgnoreCase("2nd Half")) {
//                textView.setText(calculate2ndHalf(model.getCurrentTimer(), model.getHlafTime()));
//            } else {
//                textView.setText(calculate1ndHalf(model.getCurrentTimer(), model.getHlafTime()));
//            }
//        } else if (model.getTimer().equalsIgnoreCase("00:00")) {
//            Log.d(TAG, "00:00: " + position);
//            if (model.getHlafName().equalsIgnoreCase("2nd Half")) {
//                textView.setText(calculate2ndHalf(model.getCurrentTimer(), model.getHlafTime()));
//            } else {
//                textView.setText(calculate1ndHalf(model.getCurrentTimer(), model.getHlafTime()));
//            }
//        } else if (model.isAddeddata()) {
//            if (model.getHlafName().equalsIgnoreCase("2nd Half")) {
//                textView.setText(calculate2ndHalf(model.getCurrentTimer(), model.getHlafTime()));
//            } else {
//                textView.setText(calculate1ndHalf(model.getCurrentTimer(), model.getHlafTime()));
//            }
//        } else if (model.isTimerWork()) {
//            final long totalTime = sharedPref.getMilisecound(model.getTimer());
//            new CountDownTimer(totalTime, 1000) {
//                @Override
//                public void onTick(long millisUntilFinished) {
//                    String data = msTimeFormatter((millisUntilFinished));
//                    textView.setText(data);
//                    Log.d("-->", "+-+" + model.getName() + "  " + data);
//                    if (!list.isEmpty()) {
//                        CardModel m = list.get(position);
//                        m.setTimer(data + "");
//                        m.setTimerWork(true);
//                        list.set(position, m);
//                    }
//                }
//
//                @Override
//                public void onFinish() {
//                    Log.e("-->", "+-+" + model.getName() + "  " + "0:0");
//                    if (model.getHlafName().equalsIgnoreCase("2nd Half")) {
//                        textView.setText(calculate2ndHalf(model.getCurrentTimer(), model.getHlafTime()));
//                    } else {
//                        textView.setText(calculate1ndHalf(model.getCurrentTimer(), model.getHlafTime()));
//                    }
//
//                    if (!list.isEmpty()) {
//                        CardModel m = list.get(position);
//                        m.setTimer("00:00");
//                        m.setTimerWork(false);
//                        list.set(position, m);
////                        sharedPref.setDataInPref(STORE_DATA, new Gson().toJson(list));
//                    }
//                }
//            }.start();
//        }
//
//        if (!model.isTimerWork()) {
//            if (!model.getTimer().equalsIgnoreCase("00:00")) {
//                textView.setText(model.getTimer());
//            }
//        }
//    }

    private String msTimeFormatter(long milliSeconds) {
        String hms = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));
        return hms;
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}