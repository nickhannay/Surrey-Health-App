package com.example.poject_01.UI;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.poject_01.R;

/**
 * This Activity allows the user to filter the main restaurant list using multiple values.
 */
public class ListFilterFragment extends AppCompatDialogFragment {
    private Spinner violationsSpinner;
    private Spinner hazardSpinner;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // create the view
        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.filter_fragment_layout,null);

        // setup dropdown
        setupDropDowns(v);


        // create button listener
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        getUserInput();

                        break;
                    case DialogInterface.BUTTON_NEGATIVE:

                        //Do nothing - User to cancel
                        break;
                    case DialogInterface.BUTTON_NEUTRAL:
                        ((MainActivity)getActivity()).restaurantAdapter.getFilter().filter("");
                        break;

                }
            }

        };


        // build alert dialog
        AlertDialog alertD =  new AlertDialog.Builder(getActivity())
                .setView(v)
                .setPositiveButton(R.string.save_filter, listener)
                .setNegativeButton(R.string.cancel_filter, listener)
                .setNeutralButton(R.string.reset_filter, listener)
                .create();
        alertD.setCanceledOnTouchOutside(false);
        return alertD;

    }

    private void setupDropDowns(View v) {
        violationsSpinner = v.findViewById(R.id.violationsDropdown);
        String[] items = new String[]{getString(R.string.empty_string) ,getString(R.string.less_drop_down), getString(R.string.more_drop_down)};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(v.getContext(), R.layout.filter_dropdown, items);
        violationsSpinner.setAdapter(adapter);

        hazardSpinner = v.findViewById(R.id.spinnerHazard);
        String[] items1 = new String[]{getString(R.string.empty_string) , getString(R.string.low), getString(R.string.moderate), getString(R.string.high)};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(v.getContext(), R.layout.filter_dropdown, items1);
        hazardSpinner.setAdapter(adapter1);
    }

    private void getUserInput() {
        EditText editViolations = getDialog().findViewById(R.id.filterViolations);
        EditText editName = getDialog().findViewById(R.id.filterName);

        String inputName = editName.getText().toString();
        String inputViolations =  editViolations.getText().toString();
        String inputBoolean = violationsSpinner.getSelectedItem().toString();
        String inputHazardLvl = hazardSpinner.getSelectedItem().toString();
        String filterLump = inputName + "|" + inputHazardLvl +"|"+ inputViolations  +"|"+ inputBoolean;

        Log.d("filter fragment", "filter lump - " + filterLump);
        Log.d("filter fragment", "input hazard empty- " + inputHazardLvl.isEmpty());

        ((MainActivity)getActivity()).restaurantAdapter.getFilter().filter(filterLump);

    }

}
