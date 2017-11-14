package dk.reflevel.viewpager;

public class PageChangedEvent {
	public PageChangedEvent(boolean hasVerticalNeighbors) {
		mHasVerticalNeighbors = hasVerticalNeighbors;
	}

	private boolean mHasVerticalNeighbors = true;

	public boolean hasVerticalNeighbors() {
		return mHasVerticalNeighbors;
	}

}
