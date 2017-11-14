package dk.reflevel.common;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by agile-01 on 6/9/2016.
 */
public class AlertCustomDialog {

    AlertCustomDialog() {

    }

    public interface AlertUtilsNegativeListener {
        void onPositiveButtonClick();

        void onNegativeButtonClick();
    }

    public interface AlertUtilsPositiveListener {
        void onPositiveButtonClick();

        void onNegativeButtonClick();

        void onNeutralButtonClick();
    }

    public static void chooesTeamDialog(Context mContext, final AlertUtilsNegativeListener callback) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
        builder1.setMessage("Choose Kick off team");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "HOME",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (callback != null)
                            callback.onPositiveButtonClick();
                    }
                });


        builder1.setNegativeButton(
                "AWAY",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (callback != null)
                            callback.onNegativeButtonClick();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public static void addGoalDialog(Context mContext, String title, final AlertUtilsNegativeListener callback) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
        builder1.setMessage(title);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "+",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (callback != null)
                            callback.onPositiveButtonClick();
                    }
                });

        builder1.setNeutralButton(
                "-",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (callback != null)
                            callback.onNegativeButtonClick();
                    }
                });

        builder1.setNegativeButton(
                "NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public static void openCustomDialog(Context mContext, String title, String positive, String nagitive, final AlertUtilsNegativeListener callback) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
        builder1.setMessage(title);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                positive,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (callback != null)
                            callback.onPositiveButtonClick();
                    }
                });


        builder1.setNegativeButton(
                nagitive,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (callback != null)
                            callback.onNegativeButtonClick();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

}
