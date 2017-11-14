package dk.reflevel.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dk.reflevel.MainActivity;
import dk.reflevel.R;
import dk.reflevel.common.Global;
import dk.reflevel.common.SharedPref;

import static dk.reflevel.fragments.MainScreenFragment.textViewTime;

public class SetTimeActivity extends Activity {

    private ListView lv;
    public static final String time[] = {"01", "05", "10", "12",
            "15", "20", "25", "30", "35", "40", "45"};
    SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_time);

        // TimeSharedPref = new SharedPref(SetTimeActivity.this);

        lv = (ListView) findViewById(R.id.listPickTime);


        final ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < time.length; ++i) {
            list.add(time[i]);
        }

        sharedPref = new SharedPref(SetTimeActivity.this);
        final StableArrayAdapter adapter = new StableArrayAdapter(this,
                android.R.layout.simple_list_item_1, list);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                if (!TextUtils.isEmpty(textViewTime.getText())) {
                    saveTimerString("TimerTextView", time[position].toString()+":00");
                }

                Intent intent = new Intent(SetTimeActivity.this, MainActivity.class);
                intent.putExtra("time", textViewTime.getText()+":00");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();

                Intent intnet = new Intent("action_time");
                sendBroadcast(intnet);
            }

        });
    }

    private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public StableArrayAdapter(Context context, int textViewResourceId,
                                  List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }


    //To Save value
    private void saveTimerString(String key, String value){
        sharedPref.setDataInPref(Global.SAVE_TIME,value);
        /*
        SharedPreferences saveScore = getSharedPreferences("saveTime", android.content.Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = saveScore.edit();
        editor.putString(key, value);
        editor.commit();
        */
    }
}
