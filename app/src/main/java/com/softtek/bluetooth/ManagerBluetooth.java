package com.softtek.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ManagerBluetooth {

    private Context context;

    public BluetoothAdapter bluetoothAdapter;
    public boolean estado;
    private List<BluetoothDevice> devices;
    //private AdapterDevice lista;

    public ManagerBluetooth(Context ctx) {
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.context = ctx;
        devices = new ArrayList<>();

        checkViability();
        setEstado();
    }

    public BluetoothAdapter getBluetoothAdapter(){
        return bluetoothAdapter;
    }
    private void setEstado(){
        estado = bluetoothAdapter.isEnabled();
    }
    public boolean getEstado(){
        return estado;
    }
    public List<BluetoothDevice> getDevices(){
        Log.e("Devices", ""+devices.size());
        return devices;
    }
    private void checkViability(){
        if(bluetoothAdapter == null){
            new AlertDialog.Builder(context)
                    .setTitle("No compatible")
                    .setMessage("Su telefono no soporta Bluetooth")
                    .setPositiveButton("Salir", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }
    
    public void activar(){
        if(estado == false){
            //Toast.makeText(context, "Bluetooth ya se encuentra activo", Toast.LENGTH_SHORT).show();
            bluetoothAdapter.enable();
            setEstado();
        }else {
            bluetoothAdapter.enable();
            setEstado();
            //Toast.makeText(context, "Bluetooth activado", Toast.LENGTH_SHORT).show();
        }
    }
    public void desactivar(){
        if(estado) {
            bluetoothAdapter.disable();
            setEstado();
            //Toast.makeText(context, "Bluetooth desactivado", Toast.LENGTH_SHORT).show();
        }
    }

    public void scanDevices(){

            Toast.makeText(context, "Buscando dispositivos bluetooth", Toast.LENGTH_LONG).show();
            // scan device
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            context.getApplicationContext().registerReceiver(receiver, filter);
            bluetoothAdapter.startDiscovery();
    }

    // Crea un BroadcastReceiver para la deteccion
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                devices.add(device);

            }
        }
    };
}
