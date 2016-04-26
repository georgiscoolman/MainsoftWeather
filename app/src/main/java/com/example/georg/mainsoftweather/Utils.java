package com.example.georg.mainsoftweather;

import android.app.Dialog;
import android.app.ProgressDialog;
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

    public static ProgressDialog initProgressDialog(Context context, String title, String message){

        ProgressDialog res = new ProgressDialog(context);
        res.setTitle(title);
        res.setMessage(message);
        res.setCancelable(false);

        return res;

    }

    public static ProgressDialog initProgressDialog(Context context, String title, String message, DialogInterface.OnClickListener cancelClickListener){

        ProgressDialog res = new ProgressDialog(context);
        res.setTitle(title);
        res.setMessage(message);
        res.setCancelable(false);
        res.setButton(Dialog.BUTTON_NEGATIVE, context.getString(android.R.string.cancel), cancelClickListener);
        return res;

    }

    public static void dissmissDialog(ProgressDialog dialog){
        if (dialog != null){
            if (dialog.isShowing())
            dialog.dismiss();
        }
    }
}
