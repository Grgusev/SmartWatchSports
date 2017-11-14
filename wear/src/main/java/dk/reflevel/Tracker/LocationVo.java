package dk.reflevel.Tracker;

/**
 * Created by Grishma on 17/5/16.
 */
public class LocationVo {
    private double mLatitude, mLongitude;
    private String mLocAddress;

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double mLatitude) {
        this.mLatitude = mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double mLongitude) {
        this.mLongitude = mLongitude;
    }

    public String getLocAddress() {
        return mLocAddress;
    }

    public void setLocAddress(String mLocAddress) {
        this.mLocAddress = mLocAddress;
    }
}
