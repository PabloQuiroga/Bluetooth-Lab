package com.softtek.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener{

    private ListView listadoDevice;
    private TextView txtDevice;

    private BluetoothAdapter bluetoothAdapter;

    private ArrayList<BluetoothDevice> lista;
    private AdapterDevice adapter;
    private boolean estado;

    /******************* MENU INICIO***************************/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_activar) {
            bluetoothAdapter.enable();
            txtDevice.setText(R.string.BTstateOn);
            estado = true;
            return true;
        }
        if (id == R.id.action_buscar) {
            if(estado){
                scanDevice();
                //adapter.notifyDataSetChanged();
                //txtDevice.setText(R.string.BTsearch);
            }
            return true;
        }
        if (id == R.id.action_desactivar) {
            bluetoothAdapter.disable();

            listadoDevice.setVisibility(View.GONE);
            txtDevice.setVisibility(View.VISIBLE);
            txtDevice.setText(R.string.BTstateOff);

            estado = false;
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    /********************* MENU FIN ****************************/

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listadoDevice = (ListView) findViewById(R.id.listado_device);
        txtDevice = (TextView) findViewById(R.id.lbl_devices);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) { //comprueba bluetooth soporte on device
            new AlertDialog.Builder(this)
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

        estado = bluetoothAdapter.isEnabled();
        lista = new ArrayList<>();
    }

    @Override
    protected void onResume() {
        super.onResume();

        lista = new ArrayList<>();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        adapter = new AdapterDevice(this, lista);
        listadoDevice.setAdapter(adapter);
        listadoDevice.setOnItemClickListener(this);
        listadoDevice.setOnItemLongClickListener(this);

        estado = bluetoothAdapter.isEnabled();
        if(estado){
            txtDevice.setText(R.string.BTstateOn);
        }else{
            listadoDevice.setVisibility(View.GONE);
            txtDevice.setVisibility(View.VISIBLE);
            txtDevice.setText(R.string.BTstateOff);
        }
    }

    public void scanDevice() {
        if(bluetoothAdapter.isDiscovering()) {
            //bluetoothAdapter.cancelDiscovery();
            //lista = new ArrayList<>();
            //adapter.clear();
            //lista.clear();
            /*IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(receiver, filter);*/
            //bluetoothAdapter.startDiscovery();

        }else {
            Toast.makeText(this, "Buscando dispositivos bluetooth", Toast.LENGTH_LONG).show();
            // scan device
            //lista = new ArrayList<>();
            lista.clear();
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(receiver, filter);
            bluetoothAdapter.startDiscovery();
        }
    }

    // Crea un BroadcastReceiver para la deteccion
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                listadoDevice.setVisibility(View.VISIBLE);
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                lista.add(device);
                Log.e("RECEIVER", "descubierto: "+device.getName());
            }
            //adapter.notifyDataSetChanged();
        }
    };


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        bluetoothAdapter.cancelDiscovery();

        final BluetoothDevice bluetoothDevice = lista.get(position);
        DeviceItem device = new DeviceItem(bluetoothDevice.getName(), bluetoothDevice.getAddress(), bluetoothDevice.getBondState());

        if(device.isEmparejado() != BluetoothDevice.BOND_BONDED){
            new AlertDialog.Builder(this)
                    .setTitle(device.getName())
                    .setMessage("Sus dispositivos no se encuentran vinculados")
                    .setPositiveButton("Vincular", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                boolean isPaired = bluetoothDevice.createBond(); //vinculacion
                                //UUID SPP_UUID = UUID.fromString("00001106-0000-1000-8000-00805F9B34FB");
                                //btSocket = bluetoothDevice.createRfcommSocketToServiceRecord(SPP_UUID);
                            }catch (NullPointerException ex){
                                Log.e("Vinculacion", "error "+ ex.getMessage());
                            }/*catch (IOException e){
                                Log.e("btSocket asign", "error "+ e.getMessage());
                            }*/
                        }
                    })
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // continuar con lo que hacia
                        }
                    })
                    .setIcon(android.R.drawable.btn_star)
                    .show();
        }else{
            Toast.makeText(MainActivity.this, " already conected to "+device.getName(), Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        //TODO enviar archivo aqui
        Toast.makeText(MainActivity.this, "Long click", Toast.LENGTH_SHORT).show();

        /*BluetoothMessenger messenger = new BluetoothMessenger(btSocket);
        String example = "Convert Java String";
        byte[] bytes = example.getBytes();
        messenger.write(bytes);
        */

        return true;
    }
}