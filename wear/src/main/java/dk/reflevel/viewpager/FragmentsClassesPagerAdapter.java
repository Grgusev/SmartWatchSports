package dk.reflevel.viewpager;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class FragmentsClassesPagerAdapter extends FragmentPagerAdapter {
	private List<Class<? extends Fragment>> mPagesClasses;
	private Context mContext;

	public FragmentsClassesPagerAdapter(FragmentManager fragmentManager, Context context,
										List<Class<? extends Fragment>> pages) {
		super(fragmentManager);
		mPagesClasses = pages;
		mContext = context;
	}

	@Override
	public Fragment getItem(int posiiton) {
		return Fragment.instantiate(mContext, mPagesClasses.get(posiiton).getName());
	}

	@Override
	public int getCount() {
		return mPagesClasses.size();
	}
}
