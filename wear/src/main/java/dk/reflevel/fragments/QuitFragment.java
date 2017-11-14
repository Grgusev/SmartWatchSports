package dk.reflevel.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import dk.reflevel.R;

/**
 * Created by Rimon Nassory on 18-04-2017.
 */

public class QuitFragment extends Fragment {
	// Inflate the view for the fragment based on layout XML
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.screen4, container, false);

		ImageView quit = (ImageView) view.findViewById(R.id.quit_btn);
		quit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
				builder1.setMessage("Quit App?");
				builder1.setCancelable(true);

				builder1.setPositiveButton(
						"Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								System.exit(0);
							}
						});

				builder1.setNegativeButton(
						"No",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});

				AlertDialog alert11 = builder1.create();
				alert11.show();
			}
		});

		return view;
	}
}