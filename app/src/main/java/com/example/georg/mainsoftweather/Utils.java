package com.example.georg.mainsoftweather;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by Georg on 25.04.2016.
 */
public class Utils {

    public static void showOkDialog(Context context, String title, String mesage){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(mesage)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void showOkDialog(Context context, String title, String mesage, DialogInterface.OnClickListener onClickListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(mesage)
                .setPositiveButton(android.R.string.ok, onClickListener);
        AlertDialog alert = builder.create();
        alert.show();
    }
}
