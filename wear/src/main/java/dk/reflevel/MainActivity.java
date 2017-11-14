package dk.reflevel;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import com.squareup.otto.Subscribe;

import org.json.JSONObject;

import dk.reflevel.Tracker.BasisPoint;
import dk.reflevel.Tracker.SuperPoint;
import dk.reflevel.Tracker.Tracker;
import dk.reflevel.viewpager.EventBus;
import dk.reflevel.viewpager.PageChangedEvent;
import dk.reflevel.viewpager.VerticalPager;


public class MainActivity extends FragmentActivity {
    private static final String TAG = "MainActivity";

    private static final int CENTRAL_PAGE_INDEX = 1;
    public static VerticalPager mVerticalPager;


    public static SuperPoint lastLocation = null;

    private IntentFilter intentFilter = null;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.w(TAG, "onReceive==============");
            String action = intent.getAction();
            if (action.equals("LocationChanged")) {
                String location = intent.getStringExtra("location");

                SuperPoint point = new BasisPoint();
                try {
                    point.decodeFromJSON(new JSONObject(location));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                lastLocation = point;
            }
        }
    };

    private ViewPager.OnPageChangeListener mPagerChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            EventBus.getInstance().post(new PageChangedEvent(CENTRAL_PAGE_INDEX == position));
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Tracker.initInstance(MainActivity.this);
        findViews();

        // Intent filter registeration
        {
            intentFilter = new IntentFilter();
            intentFilter.addAction("LocationChanged");
            registerReceiver(receiver, intentFilter);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        intentFilter = null;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    private void findViews() {
        mVerticalPager = (VerticalPager) findViewById(R.id.activity_main_vertical_pager);
        initViews();
    }


    private void initViews() {
        snapPageWhenLayoutIsReady(mVerticalPager, CENTRAL_PAGE_INDEX);
    }

    private void snapPageWhenLayoutIsReady(final View pageView, final int page) {
        pageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                mVerticalPager.snapToPage(page, VerticalPager.PAGE_SNAP_DURATION_INSTANT);

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
                    // recommended removeOnGlobalLayoutListener method is available since API 16 only
                    pageView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                else
                    removeGlobalOnLayoutListenerForJellyBean(pageView);
            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            private void removeGlobalOnLayoutListenerForJellyBean(final View pageView) {
                pageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getInstance().register(this);
    }

    @Override
    protected void onPause() {
        EventBus.getInstance().unregister(this);
        super.onPause();
    }

    @Subscribe
    public void onLocationChanged(PageChangedEvent event) {
        mVerticalPager.setPagingEnabled(event.hasVerticalNeighbors());
    }
}