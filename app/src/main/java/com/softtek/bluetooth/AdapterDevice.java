package com.softtek.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AdapterDevice extends ArrayAdapter<BluetoothDevice> {

    TextView txtNombre;
    TextView txtState;

    public AdapterDevice (Context context, List<BluetoothDevice> deviceItems) {
        super(context, 0, deviceItems);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(view == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.device_layout,parent,false);
        }

        txtNombre = (TextView)view.findViewById(R.id.lbl_name);
        txtState = (TextView)view.findViewById(R.id.lbl_state);

        BluetoothDevice item = getItem(position);
        String nombre = item.getName();
        String estado;
        if(item.getBondState() == 12){
            estado = "Vinculado";
            txtState.setTextColor(Color.GREEN);
        }else{
            estado = "No vinculado";
            txtState.setTextColor(Color.RED);
        }

        txtNombre.setText(nombre);
        txtState.setText(estado);

        return view;
    }
}
