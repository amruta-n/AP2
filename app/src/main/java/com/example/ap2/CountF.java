package com.example.ap2;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class CountF extends Fragment{
    TextView tCount, tCount1, tCount2, tCount3;
    ImageView iv;
    int cow;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.f_count,container, false);
        Button bBount = (Button) view.findViewById(R.id.button);
        bBount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new doit().execute();
            }
        });
        tCount = (TextView) view.findViewById(R.id.countCountry);
        tCount1 = (TextView) view.findViewById(R.id.countState);
        tCount2 = (TextView) view.findViewById(R.id.countState2);
        tCount3 = (TextView) view.findViewById(R.id.countState3);

        Spinner mySpinner = (Spinner) view.findViewById(R.id.spinner);

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(CountF.this.getActivity(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.names));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);

        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                cow = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                cow = 1;
            }
        });

        return view;
    }





    public class doit extends AsyncTask<Void, Void, Void>
    {
        String a,b1, b2, b3;

        @Override
        protected Void doInBackground(Void... parameters) {
            try {
               Document doc = Jsoup.connect("https://www.worldometers.info/coronavirus/country/india/").get();
                final String words = doc.select("div.maincounter-number").text();
                a = words;
                Document doc1 = Jsoup.connect("https://www.mohfw.gov.in/").get();
                final String numbers1 = doc1.select("table.table.table-striped").select("tr").get(cow+1).select("td").get(2).text();
                b1 = numbers1;
                final String numbers2 = doc1.select("table.table.table-striped").select("tr").get(cow+1).select("td").get(3).text();
                b2 = numbers2;
                final String numbers3 = doc1.select("table.table.table-striped").select("tr").get(cow+1).select("td").get(4).text();
                b3 = numbers3;
            }catch (Exception e)
            {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            tCount.setText(a);
            tCount1.setText(b1);
            tCount2.setText(b2);
            tCount3.setText(b3);

        }
    }
}

