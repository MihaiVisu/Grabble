package com.grabble.CustomClasses;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.grabble.R;

public class CustomListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] itemName;
    private final String[] itemDescription;
    private final TextDrawable[] drawable;

    public CustomListAdapter(Activity context,
                             String[] itemName,
                             String[] itemDescription,
                             TextDrawable[] drawable) {
        super(context, R.layout.activity_letter_list, itemName);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.itemName=itemName;
        this.itemDescription = itemDescription;
        this.drawable = drawable;
    }

    @NonNull
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.letter_list_item, null,true);

        TextView txtTitle = (TextView)rowView.findViewById(R.id.list_item_name);
        TextView txtDescription = (TextView)rowView.findViewById(R.id.list_item_left);
        ImageView imageView = (ImageView)rowView.findViewById(R.id.list_item_image);

        txtTitle.setText(itemName[position]);
        txtDescription.setText(itemDescription[position]);
        imageView.setImageDrawable(drawable[position]);
        return rowView;
    }
}