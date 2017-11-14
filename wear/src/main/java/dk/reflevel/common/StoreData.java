package dk.reflevel.common;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 4/26/2017.
 */

public class StoreData {

    public static String STORE_DATA = "storeData";

    public static void addData(SharedPref sharedPref, String name, String color, boolean isadded, String time, String currentTime,
                               String extraTime, boolean isTimerStart, String hlafTime, String halfName, String cardOptionName, int listPosition) {

        String data = sharedPref.getDataFromPref(STORE_DATA, "");
        List<CardModel> list = new ArrayList<>();
        if (!data.isEmpty()) {
            list = new Gson().fromJson(data, new TypeToken<List<CardModel>>() {
            }.getType());
        }

        CardModel model = new CardModel();
        model.setName(name);
        model.setCardColor(color);
        model.setCardSelected(isadded);
        model.setTimer(time);
        model.setExtraTimer(extraTime);
        model.setCurrentTimer(currentTime);
        model.setHlafTime(hlafTime);
        model.setHlafName(halfName);
        model.setTimerWork(isTimerStart);
        model.setCardOptionName(cardOptionName);

        if (listPosition == -1) {
            model.setAddeddata(false);
            list.add(model);
        } else {
            model.setAddeddata(true);
//            model.setTimerWork(false);
            list.set(listPosition, model);
        }

        sharedPref.setDataInPref(STORE_DATA, new Gson().toJson(list));
    }

    public static List<CardModel> getList(SharedPref sharedPref) {
        String data = sharedPref.getDataFromPref(STORE_DATA, "");
        List<CardModel> list = new ArrayList<>();
        if (!data.isEmpty()) {
            list = new Gson().fromJson(data, new TypeToken<List<CardModel>>() {
            }.getType());
            return list;
        } else {
            return list;
        }
    }



    public static boolean isCheckData1(SharedPref sharedPref, String name, String color) {
        List<CardModel> list = getList(sharedPref);
        for (int i = 0; i < list.size(); i++) {
            if (name.equalsIgnoreCase(list.get(i).getName()) && !color.equalsIgnoreCase(list.get(i).getCardColor())) {
                if (list.get(i).isAddeddata())
                    return true;
            }
        }
        return false;
    }

    public static int isCheckData(SharedPref sharedPref, String name, String color) {
        List<CardModel> list = getList(sharedPref);
        for (int i = 0; i < list.size(); i++) {
            if (name.equalsIgnoreCase(list.get(i).getName()) && color.equalsIgnoreCase(list.get(i).getCardColor())) {
                return i;
            }
        }
        return -1;
    }

    public static void updateTimeStop(SharedPref sharedPref, int position) {
//        List<CardModel> list = getList(sharedPref);
//        CardModel model = list.get(position);
//        model.setTimerWork(false);
//        list.set(position,model);
//        sharedPref.setDataInPref(STORE_DATA, new Gson().toJson(list));
    }

    public static void getDataList(List<CardModel> listDataHome, List<CardModel> listDataAway, List<CardModel> listDataMain) {
        listDataHome.clear();
        listDataAway.clear();
        for (int i = 0; i < listDataMain.size(); i++) {

            if (listDataMain.get(i).getCardOptionName().equalsIgnoreCase("home")) {
                listDataHome.add(listDataMain.get(i));
            } else {
                listDataAway.add(listDataMain.get(i));
            }
        }
    }
}
