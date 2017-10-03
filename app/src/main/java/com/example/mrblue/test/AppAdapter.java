package com.example.mrblue.test;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Mr.Blue on 10/3/2017.
 */

class AppAdapter extends ArrayAdapter<ResolveInfo> {
    private PackageManager pm=null;

    private LayoutInflater layoutInflater;

    AppAdapter(Context context, PackageManager pm, List<ResolveInfo> apps) {
        super(context, R.layout.row, apps);

        layoutInflater = LayoutInflater.from(context);
        this.pm=pm;
    }

    @Override
    public View getView(int position, View convertView,
                        ViewGroup parent) {
        if (convertView==null) {
            convertView=newView(parent);
        }

        bindView(position, convertView);

        return(convertView);
    }

    private View newView(ViewGroup parent) {
        return(layoutInflater.inflate(R.layout.row, parent, false));
    }

    private void bindView(int position, View row) {
        TextView label=(TextView)row.findViewById(R.id.label);

        label.setText(getItem(position).loadLabel(pm));

        ImageView icon=(ImageView)row.findViewById(R.id.icon);

        icon.setImageDrawable(getItem(position).loadIcon(pm));
    }
}
