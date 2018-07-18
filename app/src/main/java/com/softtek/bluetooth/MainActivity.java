package com.softtek.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private ListView listadoDevice;
    private TextView txtDevice;

    private ManagerBluetooth managerBluetooth;

    private List<BluetoothDevice> lista;
    private AdapterDevice adapter;
    private boolean estado;

    /******************* MENU INICIO***************************/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_activar) {
            managerBluetooth.activar();

            txtDevice.setText("Bluetooth encendido");

            return true;
        }
        if (id == R.id.action_buscar) {

            if(estado == false){
                managerBluetooth.scanDevices();
                txtDevice.setText("Buscando dispositivos");
            }

            return true;
        }
        if (id == R.id.action_desactivar) {
            managerBluetooth.desactivar();

            txtDevice.setVisibility(View.VISIBLE);
            listadoDevice.setVisibility(View.GONE);
            txtDevice.setText("Bluetooth apagado");

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

        listadoDevice = (ListView)findViewById(R.id.listado_device);
        txtDevice = (TextView)findViewById(R.id.lbl_devices);

    }

    @Override
    protected void onResume() {
        super.onResume();

        managerBluetooth = new ManagerBluetooth(this);
        lista = managerBluetooth.getDevices();
        adapter = new AdapterDevice(this, lista);
        listadoDevice.setAdapter(adapter);

        estado = managerBluetooth.getEstado();
        if(estado){
            txtDevice.setText("Bluetooth encendido");
        }else{
            txtDevice.setVisibility(View.VISIBLE);
            listadoDevice.setVisibility(View.GONE);
            txtDevice.setText("Bluetooth apagado");
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //TODO manejar eventos de dispositivo aqui
    }
}
/*
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener{
    private final String TAG = MainActivity.class.getSimpleName();

    private ImageButton btn_bluetooth;
    private Button btnScan;
    private TextView lblListado;
    private ListView listado;

    private boolean btState;
    private BluetoothAdapter bluetoothAdapter;
    private List<BluetoothDevice> devices;
    private AdapterDevice lista;

    private BluetoothSocket btSocket = null;
    private static final UUID MY_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_bluetooth = (ImageButton)findViewById(R.id.btn_bluetooth_on);
        btnScan = (Button)findViewById(R.id.btn_buscar);
        lblListado = (TextView)findViewById(R.id.lbl_devices);
        listado = (ListView)findViewById(R.id.listado_device);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        btState = bluetoothAdapter.isEnabled();
        if(!btState){
            lblListado.setText("Bluetooth apagado");
            btnScan.setEnabled(false);
        }else{
            lblListado.setText("no device");
            btnScan.setEnabled(true);
        }

        devices = new ArrayList<>();
        lista = new AdapterDevice(this, devices);
        listado.setAdapter(lista);
        listado.setOnItemClickListener(this);
        listado.setOnItemLongClickListener(this);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(receiverBond, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //bluetooth adapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        btState = bluetoothAdapter.isEnabled();

        if (bluetoothAdapter == null) {
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
        }else{
            setImageBluetooth(btState);
        }
    }

    public void clickImagen(View view) {
        setEstadoBluetooth();
    }

    private void setImageBluetooth(boolean valor){
        if(valor){
            btn_bluetooth.setImageResource(R.drawable.bluetooth_on);
        }else{
            btn_bluetooth.setImageResource(R.drawable.bluetooth_off);
        }
    }

    private void setEstadoBluetooth(){
        if (btState) {
            Log.i("Bluetooth", "desactivado");
            btState = false;
            setImageBluetooth(false);
            bluetoothAdapter.disable();
            btnScan.setEnabled(false);
            lblListado.setVisibility(View.VISIBLE);
            listado.setVisibility(View.GONE);
            lblListado.setText("Bluetooth apagado");
            bluetoothAdapter.cancelDiscovery();
        }else{
            Log.i("Bluetooth", "activado");
            btState = true;
            setImageBluetooth(true);
            bluetoothAdapter.enable();
            btnScan.setEnabled(true);
            lblListado.setVisibility(View.VISIBLE);
            listado.setVisibility(View.GONE);
            lblListado.setText("no device");
        }
    }

    public void scanDevice(View view) {
        if(bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
            devices.clear();
            bluetoothAdapter.startDiscovery();

        }else {
            Toast.makeText(this, "Buscando dispositivos bluetooth", Toast.LENGTH_LONG).show();
            // scan device
            devices.clear();
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
                listado.setVisibility(View.VISIBLE);
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                lista.add(device);
            }
        }
    };
    // Crea un BroadcastReceiver para el emparejado (usado?) //TODO checkar su uso
    private final BroadcastReceiver receiverBond = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // 3 casos de uso
                // caso 1: ya emparejado
                if(device.getBondState() == BluetoothDevice.BOND_BONDED){
                    Log.i("BondReceiver", "ya emparejado");
                }
                //caso 2: emparejando
                if(device.getBondState() == BluetoothDevice.BOND_BONDING){
                    Log.i("BondReceiver", "emparejando");
                }
                //caso 3: no emparejado
                if(device.getBondState() == BluetoothDevice.BOND_NONE){
                    Log.i("BondReceiver", "no emparejado");
                }
            }
        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        bluetoothAdapter.cancelDiscovery();

        final BluetoothDevice bluetoothDevice = devices.get(position);
        DeviceItem device = new DeviceItem(bluetoothDevice.getName(), bluetoothDevice.getAddress(), bluetoothDevice.getBondState());

        if(device.isEmparejado() != BluetoothDevice.BOND_BONDED){
            new AlertDialog.Builder(this)
                    .setTitle(device.getName())
                    .setMessage("Sus dispositivos no se encuentran vinculados")
                    .setPositiveButton("Vincular", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                boolean isPaired = bluetoothDevice.createBond(); //vinculacion
                                UUID SPP_UUID = UUID.fromString("00001106-0000-1000-8000-00805F9B34FB");
                                btSocket = bluetoothDevice.createRfcommSocketToServiceRecord(SPP_UUID);
                            }catch (NullPointerException ex){
                                Log.e("Vinculacion", "error "+ ex.getMessage());
                            }catch (IOException e){
                                Log.e("btSocket asign", "error "+ e.getMessage());
                            }
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
        Toast.makeText(MainActivity.this, "Long click", Toast.LENGTH_SHORT).show();
        BluetoothMessenger messenger = new BluetoothMessenger(btSocket);

        String example = "Convert Java String";
        byte[] bytes = example.getBytes();
        messenger.write(bytes);
        //TODO manejar opciones

        return true;
    }
}*/
