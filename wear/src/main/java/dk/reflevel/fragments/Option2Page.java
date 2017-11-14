package dk.reflevel.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import dk.reflevel.R;
import dk.reflevel.TakePointsActivity;


/**
 * Created by Rimon Nassory on 16-04-2017.
 */

public class Option2Page extends Fragment {
	public static Button selectPoints = null;

	// Inflate the view for the fragment based on layout XML
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.screen2, container, false);

/*
		selectPoints = (Button) view.findViewById(R.id.show_map);
		selectPoints.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onClickedSelectPoints();
			}
		});
*/


		return view;

	}

	private void onClickedSelectPoints() {
		Intent intent = new Intent(getActivity(), TakePointsActivity.class);
		getActivity().startActivity(intent);
	}


}


