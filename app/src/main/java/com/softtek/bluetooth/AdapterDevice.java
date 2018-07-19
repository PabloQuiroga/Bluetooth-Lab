package com.softtek.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class AdapterDevice extends ArrayAdapter<BluetoothDevice> {

    public AdapterDevice (Context context, List<BluetoothDevice> deviceItems) {
        super(context, 0, deviceItems);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if(view == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.device_layout,parent,false);
        }

        TextView txtNombre = (TextView) view.findViewById(R.id.lbl_name);

        BluetoothDevice item = getItem(position);
        String nombre = item.getName();

        txtNombre.setText(nombre);

        return view;
    }
}
