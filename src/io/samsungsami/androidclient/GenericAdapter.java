package io.samsungsami.androidclient;

import java.util.ArrayList;

import com.samihub.androidclient.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
 
public class GenericAdapter extends ArrayAdapter<Item> {
 
        private final Context context;
        private final ArrayList<Item> itemsArrayList;
 
        public GenericAdapter(Context context, ArrayList<Item> itemsArrayList) {
 
            super(context, R.layout.row, itemsArrayList);
 
            this.context = context;
            this.itemsArrayList = itemsArrayList;
        }
 
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
 
            // 1. Create inflater 
            LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
            View rowView = inflater.inflate(R.layout.row, parent, false);
 
            TextView labelView = (TextView) rowView.findViewById(R.id.label);
            TextView valueView = (TextView) rowView.findViewById(R.id.value);
 
            labelView.setText(itemsArrayList.get(position).getItemId());
            valueView.setText(itemsArrayList.get(position).getDescription());

            return rowView;
        }
}
