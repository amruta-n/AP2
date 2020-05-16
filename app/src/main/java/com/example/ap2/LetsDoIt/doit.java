package com.example.ap2.LetsDoIt;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


public class doit extends AsyncTask<Void, Void, Void> {
    public String name, num, namef, numf;
   @Override
   public Void doInBackground(Void... parameters) {
       try {


           Document doc = Jsoup.connect("https://www.mohfw.gov.in/").get();
           //Element table = doc.select("table.table.table-striped").get(0);
           // Elements rows = table.select("tr");
           //for(int i = 1; i<=3; i++)
           //{
           final String words = doc.select("table.table.table-striped").select("tr").get(3).select("td").get(2).text();
           final String names = doc.select("table.table.table-striped").select("tr").get(3).select("td").get(1).text();
               /* Element row = rows.get(i);
               Elements col = row.select("td");
               String aa = col.toString();*/
           name = names;//nam[i] = names;
           num = words;
           //}
       } catch (Exception e) {
           e.printStackTrace();
       }

       return null;
   }
   public void onPostExecute(Void aVoid) {
       super.onPostExecute(aVoid);
       namef = name;
       numf = num;
   }
}
