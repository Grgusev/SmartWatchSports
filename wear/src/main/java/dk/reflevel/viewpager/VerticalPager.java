package dk.reflevel.viewpager;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class VerticalPager extends ViewGroup {
	public static final int PAGE_SNAP_DURATION_DEFAULT = 300;
	public static final int PAGE_SNAP_DURATION_INSTANT = 1;

	private boolean mIsPagingEnabled = true;

	public static final String TAG = "VerticalPager";

	private static final int INVALID_SCREEN = -1;
	public static final int SPEC_UNDEFINED = -1;
	private static final int TOP = 0;
	private static final int BOTTOM = 1;
	private static final int SNAP_VELOCITY = 1000;

	private int pageHeight;
	private int measuredHeight;

	private boolean mFirstLayout = true;

	private int mCurrentPage;
	private int mNextPage = INVALID_SCREEN;

	private Scroller mScroller;
	private VelocityTracker mVelocityTracker;

	private int mTouchSlop;
	private int mMaximumVelocity;

	private float mLastMotionY;
	private float mLastMotionX;

	private final static int TOUCH_STATE_REST = 0;
	private final static int TOUCH_STATE_SCROLLING = 1;

	private int mTouchState = TOUCH_STATE_REST;

	private boolean mAllowLongPress;

	private Set<OnScrollListener> mListeners = new HashSet<OnScrollListener>();

	public VerticalPager(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public VerticalPager(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		mScroller = new Scroller(getContext(), new DecelerateInterpolator());
		mCurrentPage = 0;

		final ViewConfiguration configuration = ViewConfiguration.get(getContext());
		mTouchSlop = configuration.getScaledTouchSlop();
		mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
	}

	public int getCurrentPage() {
		return mCurrentPage;
	}

	void setCurrentPage(int currentPage) {
		mCurrentPage = Math.max(0, Math.min(currentPage, getChildCount()));
		scrollTo(getScrollYForPage(mCurrentPage), 0);
		invalidate();
	}

	public int getPageHeight() {
		return pageHeight;
	}

	// public void setPageHeight(int pageHeight) {
	// this.pageHeightSpec = pageHeight;
	// }
	private int getScrollYForPage(int whichPage) {
		int height = 0;
		for (int i = 0; i < whichPage; i++) {
			final View child = getChildAt(i);
			if (child.getVisibility() != View.GONE) {
				height += child.getHeight();
			}
		}
		return height - pageHeightPadding();
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
		} else if (mNextPage != INVALID_SCREEN) {
			mCurrentPage = mNextPage;
			mNextPage = INVALID_SCREEN;
			clearChildrenCache();
		}
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {

		final long drawingTime = getDrawingTime();
		// todo be smarter about which children need drawing
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			drawChild(canvas, getChildAt(i), drawingTime);
		}

		for (OnScrollListener mListener : mListeners) {
			int adjustedScrollY = getScrollY() + pageHeightPadding();
			mListener.onScroll(adjustedScrollY);
			if (adjustedScrollY % pageHeight == 0) {
				mListener.onViewScrollFinished(adjustedScrollY / pageHeight);
			}
		}
	}

	int pageHeightPadding() {
		return ((getMeasuredHeight() - pageHeight) / 2);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		pageHeight = getMeasuredHeight();

		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			getChildAt(i).measure(MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.EXACTLY),
					MeasureSpec.makeMeasureSpec(pageHeight, MeasureSpec.EXACTLY));
		}

		if (mFirstLayout) {
			scrollTo(getScrollYForPage(mCurrentPage), 0);
			mFirstLayout = false;
		}
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		measuredHeight = 0;

		final int count = getChildCount();
		int height;
		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			if (child.getVisibility() != View.GONE) {
				height = pageHeight * (int) Math.ceil((double) child.getMeasuredHeight() / (double) pageHeight);
				height = Math.max(pageHeight, height);
				child.layout(0, measuredHeight, right - left, measuredHeight + height);
				measuredHeight += height;
			}
		}
	}

	@Override
	public boolean requestChildRectangleOnScreen(View child, Rect rectangle, boolean immediate) {
		int screen = indexOfChild(child);
		if (screen != mCurrentPage || !mScroller.isFinished()) {
			return true;
		}
		return false;
	}

	@Override
	protected boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
		int focusableScreen;
		if (mNextPage != INVALID_SCREEN) {
			focusableScreen = mNextPage;
		} else {
			focusableScreen = mCurrentPage;
		}
		getChildAt(focusableScreen).requestFocus(direction, previouslyFocusedRect);
		return false;
	}

	@Override
	public boolean dispatchUnhandledMove(View focused, int direction) {
		if (direction == View.FOCUS_LEFT) {
			if (getCurrentPage() > 0) {
				snapToPage(getCurrentPage() - 1);
				return true;
			}
		} else if (direction == View.FOCUS_RIGHT) {
			if (getCurrentPage() < getChildCount() - 1) {
				snapToPage(getCurrentPage() + 1);
				return true;
			}
		}
		return super.dispatchUnhandledMove(focused, direction);
	}

	@Override
	public void addFocusables(ArrayList<View> views, int direction) {
		getChildAt(mCurrentPage).addFocusables(views, direction);
		if (direction == View.FOCUS_LEFT) {
			if (mCurrentPage > 0) {
				getChildAt(mCurrentPage - 1).addFocusables(views, direction);
			}
		} else if (direction == View.FOCUS_RIGHT) {
			if (mCurrentPage < getChildCount() - 1) {
				getChildAt(mCurrentPage + 1).addFocusables(views, direction);
			}
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (!mIsPagingEnabled)
			return false;
		final int action = ev.getAction();
		if ((action == MotionEvent.ACTION_MOVE) && (mTouchState != TOUCH_STATE_REST)) {
			// Log.d(TAG, "onInterceptTouchEvent::shortcut=true");
			return true;
		}

		final float y = ev.getY();
		final float x = ev.getX();

		switch (action) {
			case MotionEvent.ACTION_MOVE:
				if (mTouchState == TOUCH_STATE_REST) {
					checkStartScroll(x, y);
				}

				break;

			case MotionEvent.ACTION_DOWN:
				// Remember location of down touch
				mLastMotionX = x;
				mLastMotionY = y;
				mAllowLongPress = true;

				mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST : TOUCH_STATE_SCROLLING;
				break;

			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
				// Release the drag
				clearChildrenCache();
				mTouchState = TOUCH_STATE_REST;
				break;
		}

		return mTouchState != TOUCH_STATE_REST;
	}

	public void setPagingEnabled(boolean enabled) {
		mIsPagingEnabled = enabled;
	}

	public boolean isPagingEnabled() {
		return mIsPagingEnabled;
	}

	private void checkStartScroll(float x, float y) {
		final int xDiff = (int) Math.abs(x - mLastMotionX);
		final int yDiff = (int) Math.abs(y - mLastMotionY);

		boolean xMoved = xDiff > mTouchSlop;
		boolean yMoved = yDiff > mTouchSlop;

		if (xMoved || yMoved) {

			if (yMoved) {
				mTouchState = TOUCH_STATE_SCROLLING;
				enableChildrenCache();
			}
			if (mAllowLongPress) {
				mAllowLongPress = false;
				final View currentScreen = getChildAt(mCurrentPage);
				currentScreen.cancelLongPress();
			}
		}
	}

	void enableChildrenCache() {
		setChildrenDrawingCacheEnabled(true);
		setChildrenDrawnWithCacheEnabled(true);
	}

	void clearChildrenCache() {
		setChildrenDrawnWithCacheEnabled(false);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (!mIsPagingEnabled)
			return false;

		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(ev);

		final int action = ev.getAction();
		final float x = ev.getX();
		final float y = ev.getY();

		switch (action) {
			case MotionEvent.ACTION_DOWN:
				if (!mScroller.isFinished()) {
					mScroller.abortAnimation();
				}

				// Remember where the motion event started
				mLastMotionY = y;
				break;
			case MotionEvent.ACTION_MOVE:
				if (mTouchState == TOUCH_STATE_REST) {
					checkStartScroll(y, x);
				} else if (mTouchState == TOUCH_STATE_SCROLLING) {
					// Scroll to follow the motion event
					int deltaY = (int) (mLastMotionY - y);
					mLastMotionY = y;

					// Apply friction to scrolling past boundaries.
					final int count = getChildCount();
					if (getScrollY() < 0 || getScrollY() + pageHeight > getChildAt(count - 1).getBottom()) {
						deltaY /= 2;
					}

					scrollBy(0, deltaY);
				}
				break;
			case MotionEvent.ACTION_UP:
				if (mTouchState == TOUCH_STATE_SCROLLING) {
					final VelocityTracker velocityTracker = mVelocityTracker;
					velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
					int velocityY = (int) velocityTracker.getYVelocity();

					final int count = getChildCount();

					// check scrolling past first or last page?
					if (getScrollY() < 0) {
						snapToPage(0);
					} else if (getScrollY() > measuredHeight - pageHeight) {
						snapToPage(count - 1, BOTTOM, PAGE_SNAP_DURATION_DEFAULT);
					} else {
						for (int i = 0; i < count; i++) {
							final View child = getChildAt(i);
							if (child.getTop() < getScrollY() && child.getBottom() > getScrollY() + pageHeight) {
								// we're inside a page, fling that bitch
								mNextPage = i;
								mScroller.fling(getScrollX(), getScrollY(), 0, -velocityY, 0, 0, child.getTop(),
										child.getBottom() - getHeight());
								invalidate();
								break;
							} else if (child.getBottom() > getScrollY() && child.getBottom() < getScrollY() + getHeight()) {
								// stuck in between pages, oh snap!
								if (velocityY < -SNAP_VELOCITY) {
									snapToPage(i + 1);
								} else if (velocityY > SNAP_VELOCITY) {
									snapToPage(i, BOTTOM, PAGE_SNAP_DURATION_DEFAULT);
								} else if (getScrollY() + pageHeight / 2 > child.getBottom()) {
									snapToPage(i + 1);
								} else {
									snapToPage(i, BOTTOM, PAGE_SNAP_DURATION_DEFAULT);
								}
								break;
							}
						}
					}

					if (mVelocityTracker != null) {
						mVelocityTracker.recycle();
						mVelocityTracker = null;
					}
				}
				mTouchState = TOUCH_STATE_REST;
				break;
			case MotionEvent.ACTION_CANCEL:
				mTouchState = TOUCH_STATE_REST;
		}

		return true;
	}

	private void snapToPage(final int whichPage, final int where, int duration) {
		enableChildrenCache();

		boolean changingPages = whichPage != mCurrentPage;

		mNextPage = whichPage;

		View focusedChild = getFocusedChild();
		if (focusedChild != null && changingPages && focusedChild == getChildAt(mCurrentPage)) {
			focusedChild.clearFocus();
		}

		final int delta;
		if (getChildAt(whichPage).getHeight() <= pageHeight || where == TOP) {
			delta = getChildAt(whichPage).getTop() - getScrollY();
		} else {
			delta = getChildAt(whichPage).getBottom() - pageHeight - getScrollY();
		}

		mScroller.startScroll(0, getScrollY(), 0, delta, duration);
		invalidate();
	}

	public void snapToPage(final int whichPage) {
		snapToPage(whichPage, TOP, PAGE_SNAP_DURATION_DEFAULT);
	}

	public void snapToPage(final int whichPage, int duration) {
		snapToPage(whichPage, TOP, duration);
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		final SavedState state = new SavedState(super.onSaveInstanceState());
		state.currentScreen = mCurrentPage;
		return state;
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		SavedState savedState = (SavedState) state;
		super.onRestoreInstanceState(savedState.getSuperState());
		if (savedState.currentScreen != INVALID_SCREEN) {
			mCurrentPage = savedState.currentScreen;
		}
	}

	public void scrollUp() {
		if (mNextPage == INVALID_SCREEN && mCurrentPage > 0 && mScroller.isFinished()) {
			snapToPage(mCurrentPage - 1);
		}
	}

	public void scrollDown() {
		if (mNextPage == INVALID_SCREEN && mCurrentPage < getChildCount() - 1 && mScroller.isFinished()) {
			snapToPage(mCurrentPage + 1);
		}
	}

	public int getScreenForView(View v) {
		int result = -1;
		if (v != null) {
			ViewParent vp = v.getParent();
			int count = getChildCount();
			for (int i = 0; i < count; i++) {
				if (vp == getChildAt(i)) {
					return i;
				}
			}
		}
		return result;
	}

	public boolean allowLongPress() {
		return mAllowLongPress;
	}

	public static class SavedState extends BaseSavedState {
		int currentScreen = -1;

		SavedState(Parcelable superState) {
			super(superState);
		}

		private SavedState(Parcel in) {
			super(in);
			currentScreen = in.readInt();
		}

		@Override
		public void writeToParcel(Parcel out, int flags) {
			super.writeToParcel(out, flags);
			out.writeInt(currentScreen);
		}

		public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
			public SavedState createFromParcel(Parcel in) {
				return new SavedState(in);
			}

			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}
		};
	}

	public void addOnScrollListener(OnScrollListener listener) {
		mListeners.add(listener);
	}

	public void removeOnScrollListener(OnScrollListener listener) {
		mListeners.remove(listener);
	}

	public static interface OnScrollListener {
		void onScroll(int scrollX);

		void onViewScrollFinished(int currentPage);
	}
}
