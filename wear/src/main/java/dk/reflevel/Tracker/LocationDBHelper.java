package dk.reflevel.Tracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Grishma on 17/5/16.
 */
public class LocationDBHelper {
    // objects
	private String							TAG				= "LocationDBHelper";
    private Context							mContext		= null;
    private static LocationDBHelper			helper			= null;


    private LocationDBHelper(Context pContxt) {
        mContext = pContxt;
    }


    public synchronized static LocationDBHelper getInstance(Context pcontxt) {
        if (helper != null) return helper;

        return helper = new LocationDBHelper(pcontxt);
    }


    /**
     * Insert records of contacts into database.
     */
    public boolean insertLocationDetails(LocationVo locationInfo) {
        SQLiteDatabase m_provider = null;

        try {
            m_provider = SQLiteDBProvider.getInstance(mContext).openToWrite();
            m_provider.beginTransaction();

			ContentValues m_conVal = new ContentValues();
			m_conVal.put(LocationMaster.mloc_latitude.name(), locationInfo.getLatitude());
			m_conVal.put(LocationMaster.mloc_longitude.name(), locationInfo.getLongitude());
			m_conVal.put(LocationMaster.mloc_address.name(), locationInfo.getLocAddress());

			m_provider.replace(LocationMaster.getName(), null, m_conVal);
            m_provider.setTransactionSuccessful();

			Log.w(TAG, "Records Inserted.");

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (m_provider != null)
                m_provider.endTransaction();
        }

        return false;
    }


    public void clearDB() {
		SQLiteDatabase m_provider = null;
		try {
			m_provider = SQLiteDBProvider.getInstance(mContext).openToWrite();
			m_provider.beginTransaction();
			m_provider.execSQL("DELETE FROM location_table");
			m_provider.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (m_provider != null)
				m_provider.endTransaction();
		}
	}

    /**
     * Get all the contact list from the database.
     *
     * @return-Arraylist of contacts
     */
    public ArrayList<LocationVo> getAllLocationLatLongDetails() {
        ArrayList<LocationVo> m_arryContVo = new ArrayList<>();
        SQLiteDatabase m_provider = SQLiteDBProvider.getInstance(mContext).openToRead();

        Cursor m_contCursor = m_provider.query(
                  LocationMaster.getName(),
                  null,
                  null,
                  null,
                  null,
                  null,
                  null, null);

        if (m_contCursor.getCount() > 0) {
            m_contCursor.moveToFirst();
            do {
                LocationVo m_conVo = new LocationVo();
                m_conVo.setLatitude(m_contCursor.getDouble(m_contCursor
                          .getColumnIndex(LocationMaster.mloc_latitude.name())));
                m_conVo.setLongitude(m_contCursor.getDouble(m_contCursor
                          .getColumnIndex(LocationMaster.mloc_longitude.name())));
                m_conVo.setLocAddress(m_contCursor.getString(m_contCursor
                          .getColumnIndex(LocationMaster.mloc_address.name())));

                m_arryContVo.add(m_conVo);

            } while (m_contCursor.moveToNext());
            m_contCursor.close();
        }

        return m_arryContVo;

    }

    public enum LocationMaster {
        mloc_id, mloc_latitude, mloc_longitude, mloc_address;

        public static String getName() {
            return "location_table";
        }
    }
}
