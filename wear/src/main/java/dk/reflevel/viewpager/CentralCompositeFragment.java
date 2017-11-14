package dk.reflevel.viewpager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import dk.reflevel.R;
import dk.reflevel.fragments.MainScreenFragment;
import dk.reflevel.fragments.Option2Page;
import dk.reflevel.fragments.QuitFragment;

public class CentralCompositeFragment extends Fragment {
	public static ViewPager mHorizontalPager;
	public static int mCentralPageIndex = 0;

	private OnPageChangeListener mPagerChangeListener = new OnPageChangeListener() {
		@Override
		public void onPageSelected(int position) {
			EventBus.getInstance().post(new PageChangedEvent(mCentralPageIndex == position));
		}

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

		}

		@Override
		public void onPageScrollStateChanged(int state) {
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(R.layout.fragment_composite_central, container, false);
		findViews(fragmentView);

		return fragmentView;
	}

	private void findViews(View fragmentView) {
		mHorizontalPager = (ViewPager) fragmentView.findViewById(R.id.fragment_composite_central_pager);
		initViews();
	}

	private void initViews() {
		populateHozizontalPager();
		mHorizontalPager.setCurrentItem(mCentralPageIndex);
		mHorizontalPager.setOnPageChangeListener(mPagerChangeListener);
	}

	private void populateHozizontalPager() {
		ArrayList<Class<? extends Fragment>> pages = new ArrayList<Class<? extends Fragment>>();
		pages.add(QuitFragment.class);
		pages.add(MainScreenFragment.class);
		pages.add(Option2Page.class);
		mCentralPageIndex = pages.indexOf(MainScreenFragment.class);
		mHorizontalPager.setAdapter(new FragmentsClassesPagerAdapter(getChildFragmentManager(), getActivity(), pages));


	}
}
