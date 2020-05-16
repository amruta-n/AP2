package com.example.ap2;

import android.content.Context;
import android.widget.BaseAdapter;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class StateAdaptar  {
    private ArrayList<StateItem> stateItems;
    private Context context;
    private Object StateAdaptar;

    public StateAdaptar(ArrayList<StateItem> stateItems,Context context)
    {
        this.stateItems = stateItems;
        this.context = context;
    }
    public void setFilter (ArrayList<StateItem> newList) {
        stateItems = new ArrayList<>();
        stateItems.addAll(newList);
        ((BaseAdapter)StateAdaptar).notifyDataSetChanged();
    }
}
