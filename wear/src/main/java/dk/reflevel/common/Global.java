package dk.reflevel.common;

/**
 * Created by lenovo on 5/9/2017.
 */

public class Global {

    public static String STORE_MINUTE = "store_minute";
    public static String SAVE_TIME = "saveTime";
    public static String IS_ADVANCE_GOAL = "isAdvanceGoal";
    public static String IS_ADVANCE_SUB = "isAdvanceSub";
    public static String PLUS = "plus";
    public static String MINUS = "minus";

    public static String calculate1ndHalf(String startTime, String totalTime) {
        int totalTime1 = getTimeToSecound(totalTime);
        int startTime1 = getTimeToSecound(startTime);
        int totalTi = totalTime1 - startTime1;
        int minutes = (totalTi % 3600) / 60;
        int seconds = totalTi % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }


    public static String calculate2ndHalf(String startTime, String totalTime) {
        int totalTime1 = getTimeToSecound(totalTime);
        int startTime1 = getTimeToSecound(startTime);
        int totalTi = totalTime1 - startTime1 + totalTime1;
        int minutes = (totalTi % 3600) / 60;
        int seconds = totalTi % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private static int getTimeToSecound(String time) {
        String[] units = time.split(":"); //will break the string up into an array
        int minutes = Integer.parseInt(units[0]); //first element
        int seconds = Integer.parseInt(units[1]); //second element
        return 60 * minutes + seconds; //add up our values
    }

}
