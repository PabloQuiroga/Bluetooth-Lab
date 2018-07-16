package com.softtek.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener{
    private final String TAG = MainActivity.class.getSimpleName();

    private ImageButton btn_bluetooth;
    private Button btnScan;
    private TextView lblListado;
    private ListView listado;

    private boolean btState;
    private BluetoothAdapter bluetoothAdapter;
    private ArrayAdapter<BluetoothDevice> arrayAdapter;

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
                Log.i("Bluetooth", "desactivando bluetooth");
                btState = false;
                setImageBluetooth(false);
                bluetoothAdapter.disable();
                btnScan.setEnabled(false);
                lblListado.setVisibility(View.VISIBLE);
                listado.setVisibility(View.GONE);
                lblListado.setText("Bluetooth apagado");
            }else{
                Log.i("Bluetooth", "activando bluetooth");
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

        }else {
            Toast.makeText(this, "Buscando dispositivos bluetooth", Toast.LENGTH_LONG).show();
            arrayAdapter = new ArrayAdapter<BluetoothDevice>(this, R.layout.dispositivo_layout);
            listado.setAdapter(arrayAdapter);
            listado.setOnItemClickListener(this);
            listado.setOnItemLongClickListener(this);

            // scan device
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
                arrayAdapter.add(device);
            }
        }
    };
    // Crea un BroadcastReceiver para el emparejado
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

        final BluetoothDevice bluetoothDevice = arrayAdapter.getItem(position);
        DeviceItem device = new DeviceItem(bluetoothDevice.getName(), bluetoothDevice.getAddress(), bluetoothDevice.getBondState());

        if(device.isEmparejado() != BluetoothDevice.BOND_BONDED){
            new AlertDialog.Builder(this)
                    .setTitle(device.getName())
                    .setMessage("Sus dispositivos no se encuentran vinculados")
                    .setPositiveButton("Vincular", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                boolean isPaired = bluetoothDevice.createBond(); //vinculacion
                            }catch (NullPointerException ex){
                                Log.e("Vinculacion", "error "+ ex.getMessage());
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
        return true;
    }
}