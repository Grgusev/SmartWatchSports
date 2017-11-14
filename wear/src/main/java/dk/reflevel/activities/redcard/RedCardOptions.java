package dk.reflevel.activities.redcard;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dk.reflevel.MainActivity;
import dk.reflevel.R;
import dk.reflevel.common.Global;
import dk.reflevel.common.SharedPref;
import dk.reflevel.common.StoreData;
import dk.reflevel.fragments.MainScreenFragment;

import static dk.reflevel.fragments.MainScreenFragment.firstSecondHalfText;

public class    RedCardOptions extends Activity {

    public static TextView redCardChoice, playerNumber;
    //    MaterialNumberPicker dataPicker;
    ListView lv;
    SharedPref sharedPref;

    public static final String warnings[] = {"Forsøg slag/spark/spytte", "Slå modspiller", "Sparke modspiller",
            "Spytte på modspiller", "Voldsom tackling u/bold", "Voldsom tackling m/bold", "Upassende sprog/tegn", "Voldsom adfærd", "Andet"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_red_card_options);

        sharedPref = new SharedPref(RedCardOptions.this);

        redCardChoice = (TextView) findViewById(R.id.txt_red_card);
        playerNumber = (TextView) findViewById(R.id.playerNumber);

        Intent intent = getIntent();
        final String playerNumber = intent.getStringExtra("playerNumber");
        final String playerNumber2 = intent.getStringExtra("playerNumber2");
        final String isFrom = intent.getStringExtra("isFrom");
        final String cardOptionName = intent.getStringExtra("cardOptionName");
        lv = (ListView) findViewById(R.id.listData);

        List<String> stringList = new ArrayList<String>(Arrays.asList(warnings));

        MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(RedCardOptions.this, stringList);
        for (int i = 0; i < warnings.length; ++i) {
            lv.setAdapter(adapter);
        }

        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                int listPosition = StoreData.isCheckData(sharedPref, "Nr. " + playerNumber + playerNumber2, isFrom);

                StoreData.addData(sharedPref, "Nr. " + playerNumber + playerNumber2, isFrom, true,
                        sharedPref.getDataFromPref(Global.STORE_MINUTE, "00:00"),
                        "" + MainScreenFragment.CURRENT_TIME, sharedPref.getDataFromPref(Global.STORE_MINUTE, "00:00"), true, sharedPref.getDataFromPref(Global.SAVE_TIME, "00:00")
                        , firstSecondHalfText.getText().toString(), cardOptionName, listPosition);

                final Dialog dialog = new Dialog(RedCardOptions.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                dialog.setContentView(R.layout.red_card_dialog);
                // set the custom dialog components - text, image and button
                TextView text = (TextView) dialog.findViewById(R.id.txt_red_card_dialog);
                text.setText(playerNumber + playerNumber2);
                TextView homeAwayTxt = (TextView) dialog.findViewById(R.id.home_away_red_txt_dialog);

                if (playerNumber.contains("Home Team") || playerNumber2.contains("Home Team")) {
                    String str = text.getText().toString().trim();
                    if (str.length() != 0) {
                        str = str.replace("Home Team", "");
                        text.setText(str);
                    }
                    homeAwayTxt.setText("Home Team");
                } else {
                    String str = text.getText().toString().trim();
                    if (str.length() != 0) {
                        str = str.replace("Away Team", "");
                        text.setText(str);
                    }
                    homeAwayTxt.setText("Away Team");
                }

                dialog.show();

                final Handler handler = new Handler();
                final Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        if (dialog.isShowing()) {
                            Intent intent = new Intent(RedCardOptions.this, MainActivity.class);
                            startActivity(intent);
                            finish();

                            Intent intnet = new Intent("SOME_ACTION");
                            sendBroadcast(intnet);
                            dialog.dismiss();
                        }
                    }
                };

                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        handler.removeCallbacks(runnable);
                    }
                });

                handler.postDelayed(runnable, 3000);
            }
        });
    }

    public class MySimpleArrayAdapter extends ArrayAdapter<String> {
        private final Context context;
        List<String> stringList;

        public MySimpleArrayAdapter(Context context, List<String> stringList) {
            super(context, -1, stringList);
            this.context = context;
            this.stringList = stringList;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.row_red_card, parent, false);
            TextView textView = (TextView) rowView.findViewById(R.id.tvTitle);
            TextView tvCount = (TextView) rowView.findViewById(R.id.tvCount);
            textView.setText(stringList.get(position));
            tvCount.setText(position + 1 + "");

            return rowView;
        }
    }
}
