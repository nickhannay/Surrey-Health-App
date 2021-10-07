package com.example.poject_01.UI;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.UnicodeSetSpanner;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.poject_01.R;
import com.example.poject_01.UI.MainActivity;
import com.example.poject_01.UI.MapsActivity;
import com.example.poject_01.UI.WelcomeActivity;
import com.example.poject_01.model.DownloadRequest;

import static android.content.Context.MODE_PRIVATE;


/**
 * Handles initial dialog and its cases for downloading or not
 */
public class DownloadFragment extends AppCompatDialogFragment {
    private SharedPreferences prefs;
    private DownloadRequest restaurantsDownload;
    private DownloadRequest inspectionsDownload;
    private SharedPreferences.Editor editor;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //create the view
        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.download_alert,null);

        //create button listener
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        prefs = WelcomeActivity.getContext().getSharedPreferences("startup_logic", MODE_PRIVATE);
                        editor = prefs.edit();
                        restaurantsDownload = ((WelcomeActivity)getActivity()).getRestaurantsRequest();
                        inspectionsDownload = ((WelcomeActivity)getActivity()).getInspectionsRequest();
                        if (restaurantsDownload.dataModified()){
                            restaurantsDownload.downloadData();
                            int count  = prefs.getInt("url_count", 0);
                            count +=1;
                            editor.putInt("url_count", count);
                            editor.commit();
                        }
                        if (inspectionsDownload.dataModified()){
                            inspectionsDownload.downloadData();
                            int count  = prefs.getInt("url_count", 0);
                            count +=1;
                            editor.putInt("url_count", count);
                            editor.commit();
                        }
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        Intent intent = MapsActivity.getIntent(WelcomeActivity.getContext());
                        startActivity(intent);
                        ((WelcomeActivity)getActivity()).finish();
                        //Do nothing - User chose not to download
                        break;
                }
            }

        };


        //build alert dialog
        AlertDialog alertD =  new AlertDialog.Builder(getActivity())
                .setTitle(R.string.update_alert_title)
                .setView(v)
                .setPositiveButton(R.string.update_alert_yes, listener)
                .setNegativeButton(R.string.update_alert_no, listener)
                .create();
        alertD.setCanceledOnTouchOutside(false);
        return alertD;

    }






}
