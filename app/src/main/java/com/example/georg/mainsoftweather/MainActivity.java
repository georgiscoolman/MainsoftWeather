package com.example.georg.mainsoftweather;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    ProgressDialog searchLocationDialog;

    // Key e96b626a0cb231086ffea9d1f23488bd

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private void searchLocation(){

        searchLocationDialog = new ProgressDialog(this);
        searchLocationDialog.setTitle("Searching location");
        searchLocationDialog.setButton(Dialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        searchLocationDialog.show();

    }
}
