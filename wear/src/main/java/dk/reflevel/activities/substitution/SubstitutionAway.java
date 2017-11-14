package dk.reflevel.activities.substitution;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import dk.reflevel.R;

/**
 * Created by Rimon Nassory on 20-07-2017.
 */

public class SubstitutionAway extends Activity {

    /** Items entered by the user is stored in this ArrayList variable */
    ArrayList<String> list = new ArrayList<String>();

    /** Declaring an ArrayAdapter to set items to ListView */
    public static final String MyPREFERENCES = "MyPrefs" ;
    ArrayAdapter<String> adapter;
    Button btnDelete;
    EditText edit;
    SharedPreferences spref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.substitution_away);

        /** Reference to the button of the layout main.xml */
        ListView listview = (ListView) findViewById(R.id.list_away);
        Button btn = (Button) findViewById(R.id.btnAdd);
        btnDelete = (Button) findViewById(R.id.btnDelete);

        spref = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        list = getArray();

        /** Defining the ArrayAdapter to set items to ListView */
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);

        /** Setting the event listener for the add button */
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit = (EditText) findViewById(R.id.txtItem);
                list.add(edit.getText().toString());

                SharedPreferences.Editor editor = spref.edit();
                edit.setText("");
                adapter.notifyDataSetChanged();
            }
        });
        if (listview != null) {
            listview.setAdapter(adapter);
        }

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSub();
            }
        });


    }

    public void onStop() {
        saveArray();
        super.onStop();
    }


    public boolean saveArray() {
        SharedPreferences sp = this.getSharedPreferences("subaway", Activity.MODE_PRIVATE);
        SharedPreferences.Editor mEdit1 = sp.edit();
        Set<String> set = new HashSet<String>();
        set.addAll(list);
        mEdit1.putStringSet("listaway", set);
        return mEdit1.commit();
    }

    public ArrayList<String> getArray() {
        SharedPreferences sp = this.getSharedPreferences("subaway", Activity.MODE_PRIVATE);

        //NOTE: if shared preference is null, the method return empty Hashset and not null
        Set<String> set = sp.getStringSet("listaway", new HashSet<String>());

        return new ArrayList<String>(set);
    }

    public void deleteSub() {
        list.clear(); // this list which you hava passed in Adapter for your listview
        adapter.notifyDataSetChanged();
    }
}
