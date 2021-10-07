package com.example.poject_01.UI;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.poject_01.R;
import com.example.poject_01.model.Inspection;
import com.example.poject_01.model.InspectionList;
import com.example.poject_01.model.Restaurant;
import com.example.poject_01.model.RestaurantList;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.List;
//image references
//https://thenounproject.com/term/food-tray/695390/
//https://thenounproject.com/term/silverfish/1975976/
//https://thenounproject.com/term/cleaning/1932669/
//https://thenounproject.com/term/clipboard/122200/
//https://thenounproject.com/term/kitchen/554415/
/**
 *  Shows details of the inspection clicked by user.
 *  Data such as Number of critical/non-critical violations, inspection type, inspection date, and violation type
 */
public class InspectionDetailsActivity extends AppCompatActivity {
    private Inspection curInspection;
    private int index;
    private Restaurant restaurant;
    private int restaurantIndex;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_inspection_details);
        extractDataFromIntent();
        setText();
        //List Violations
        populateListView();
        registerClickCallBack();

        //back button
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

    }


    private void populateListView() {
        curInspection = restaurant.getInspections().getInspectionIndex(index);
        //build adapter
        ArrayAdapter<String> adapter = new myListAdapter();
        ListView list =  findViewById(R.id.inspectionListView);
        list.setAdapter(adapter);

    }


    private class myListAdapter extends ArrayAdapter<String> {
        //get list
        public myListAdapter() {
            super(InspectionDetailsActivity.this, R.layout.inspection_content, curInspection.getViolationList());
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View itemView = convertView;
            if(itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.inspection_content, parent, false);
            }
            //find violation to work with
            String curViolation = curInspection.getViolationList().get(position);
            String[] violations = curViolation.split(",");
            //fill view
            ImageView imageView = itemView.findViewById(R.id.violationItem);
            //Violation Types: Food, Equipment, pest/animal, other
            if(violations[2].toLowerCase().contains("sani")) {
                imageView.setImageResource(R.drawable.sanitary);
            }
            else if(violations[2].toLowerCase().contains("equipment")) {
                imageView.setImageResource(R.drawable.kitchen);
            }
            else if(violations[2].toLowerCase().contains("food")) {
                imageView.setImageResource(R.drawable.food);
            }
            else if(violations[2].toLowerCase().contains("pest")) {
                imageView.setImageResource(R.drawable.pest);
            }
            else {
                imageView.setImageResource(R.drawable.clipboard);
            }
            // Violation Number
            TextView vioNumText = itemView.findViewById(R.id.violationNumber);
            vioNumText.setText(violations[0]);

            // Violation Type
            TextView criticalText = itemView.findViewById(R.id.violationType);
            int displayViolationType;
            if(violations[1].equals("Critical"))
                displayViolationType = R.string.critical_violation_list;
            else
                displayViolationType = R.string.non_critical_violation_list;
            criticalText.setText(getString(displayViolationType));

            // Icon for Critical/Non critical
            ImageView criticalIcon = itemView.findViewById(R.id.criticalImage);
            if(violations[1].toLowerCase().contains("not")) {
                criticalIcon.setImageResource(R.drawable.greenhazardicon);
            }
            else {
                criticalIcon.setImageResource(R.drawable.redhazardicon);
            }

            return itemView;
        }
    }
    private void registerClickCallBack() {
        ListView list = findViewById(R.id.inspectionListView);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                String curViolation = curInspection.getViolationList().get(position);
                String[] violations = curViolation.split(",");
                String message = getViolationDisplayLanguage(violations[0]);
                Toast.makeText(InspectionDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private  String getViolationDisplayLanguage(String vioNumber)
    {
        int vioDisplay;
        switch (vioNumber) {
            case "101":
                vioDisplay = R.string.vio101;
                break;
            case "102":
                vioDisplay = R.string.vio102;
                break;
            case "103":
                vioDisplay = R.string.vio103;
                break;
            case "104":
                vioDisplay = R.string.vio104;
                break;
            case "201":
                vioDisplay = R.string.vio201;
                break;
            case "202":
                vioDisplay = R.string.vio202;
                break;
            case "203":
                vioDisplay = R.string.vio203;
                break;
            case "204":
                vioDisplay = R.string.vio204;
                break;
            case "205":
                vioDisplay = R.string.vio205;
                break;
            case "206":
                vioDisplay = R.string.vio206;
                break;
            case "208":
                vioDisplay = R.string.vio208;
                break;
            case "209":
                vioDisplay = R.string.vio209;
                break;
            case "210":
                vioDisplay = R.string.vio210;
                break;
            case "211":
                vioDisplay = R.string.vio211;
                break;
            case "212":
                vioDisplay = R.string.vio212;
                break;
            case "301":
                vioDisplay = R.string.vio301;
                break;
            case "302":
                vioDisplay = R.string.vio302;
                break;
            case "303":
                vioDisplay = R.string.vio303;
                break;
            case "304":
                vioDisplay = R.string.vio304;
                break;
            case "305":
                vioDisplay = R.string.vio305;
                break;
            default:
                vioDisplay = R.string.vio306;
                break;
        }

        return  getString(vioDisplay);
    }

    private void extractDataFromIntent() {
        Intent intent = getIntent();
        index = intent.getIntExtra("EXTRA_INDEX", 0);
        restaurantIndex = intent.getIntExtra("REST_INDEX", 0);
        restaurant = MainActivity.getInstance().getFilteredList().get(restaurantIndex);

    }

    private String refactorDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate inspectionDate = LocalDate.parse(date, formatter);

        int monthDisplay;
        if(inspectionDate.getMonth().equals(Month.JANUARY))
            monthDisplay = R.string.january;
        else if(inspectionDate.getMonth().equals(Month.FEBRUARY))
            monthDisplay = R.string.february;
        else if(inspectionDate.getMonth().equals(Month.MARCH))
            monthDisplay = R.string.march;
        else if(inspectionDate.getMonth().equals(Month.APRIL))
            monthDisplay = R.string.april;
        else if(inspectionDate.getMonth().equals(Month.MAY))
            monthDisplay = R.string.may;
        else if(inspectionDate.getMonth().equals(Month.JUNE))
            monthDisplay = R.string.june;
        else if(inspectionDate.getMonth().equals(Month.JULY))
            monthDisplay = R.string.july;
        else if(inspectionDate.getMonth().equals(Month.AUGUST))
            monthDisplay = R.string.august;
        else if(inspectionDate.getMonth().equals(Month.SEPTEMBER))
            monthDisplay = R.string.september;
        else if(inspectionDate.getMonth().equals(Month.OCTOBER))
            monthDisplay = R.string.october;
        else if(inspectionDate.getMonth().equals(Month.NOVEMBER))
            monthDisplay = R.string.november;
        else
            monthDisplay = R.string.december;
        return (getString(monthDisplay)+ " " + inspectionDate.getDayOfMonth() + " " + inspectionDate.getYear());
    }

    private void setText() {
        curInspection = restaurant.getInspections().getInspectionIndex(index);
        String date = refactorDate(String.valueOf(curInspection.getInspectionDate()));
        String numCritical = getString(R.string.critical) +  curInspection.getNumCritical();
        String numNonCritical = getString(R.string.non_critical) + curInspection.getNumNonCritical();

        int inspectionTypeDisplay;
        if(curInspection.getInspectionType().equals("Routine"))
        {
            inspectionTypeDisplay = R.string.routine;
        }
        else
            inspectionTypeDisplay = R.string.follow_up;

        String inspectionType = getString(inspectionTypeDisplay);

        TextView textDate = findViewById(R.id.inspectionDate);
        textDate.setText(date);

        TextView textCritical = findViewById(R.id.inspectionCritical);
        textCritical.setText(numCritical);

        TextView textNonCritical =findViewById(R.id.inspectionNonCritical);
        textNonCritical.setText(numNonCritical);

        TextView textInspectionType =  findViewById(R.id.inspectionType);
        textInspectionType.setText(inspectionType);

    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}