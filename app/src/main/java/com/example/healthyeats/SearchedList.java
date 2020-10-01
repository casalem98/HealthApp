package com.example.healthyeats;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/*
 * Displays the users search results for food items.
 */

public class SearchedList extends AppCompatActivity {
    ListView searchResults;
    ArrayList<Food> lresults = new ArrayList<>();
    String condition = "searched_foods";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searched_list);

        lresults = (ArrayList<Food>) getIntent().getSerializableExtra("searched_foods");

        //ArrayList<Food> myList = (ArrayList<Food>) getIntent().getParcelableExtra("searched_foods");

        searchResults = (ListView) findViewById(R.id.list_items);

        //ArrayAdapter<Food> adapter = new ArrayAdapter<Food>(this, android.R.layout.simple_list_item_1, lresults);
        //searchResults.setAdapter(adapter);
        List<HashMap<String, String>> listItems = new ArrayList<>();

        SimpleAdapter adapter = new SimpleAdapter(this, listItems, R.layout.list_item, new String[]{"Name", "Brand", "Calories"},
                new int[]{R.id.name, R.id.brand, R.id.calories});

        for(int i = 0; i < lresults.size(); i++){
            HashMap<String, String> resultsMap = new HashMap<>();
            resultsMap.put("Name", lresults.get(i).getName());
            resultsMap.put("Brand", lresults.get(i).getBrand());
            resultsMap.put("Calories", lresults.get(i).getCal());
            listItems.add(resultsMap);
        }

        searchResults.setAdapter(adapter);

        searchResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object clickedObject = parent.getItemAtPosition(position);
               // System.out.println(clickedObject);
               // System.out.println(position);
                System.out.println(lresults.get(position).toString());
                Food item = lresults.get(position);
                Intent intentItem = new Intent(SearchedList.this, SelectedItem.class);
                intentItem.putExtra("sampleObj", item);
                startActivity(intentItem);

            }
        });

    }

}