package com.softtek.bluetooth;

import android.bluetooth.BluetoothDevice;

public class DeviceItem {
    private final int[]state = {BluetoothDevice.BOND_NONE, BluetoothDevice.BOND_BONDING, BluetoothDevice.BOND_BONDED};
    private String name;
    private String address;
    private int estado;
    BluetoothDevice device;

    public DeviceItem(BluetoothDevice btDevice){
        this.device = btDevice;
        this.name = device.getName();
        this.address = device.getAddress();
        this.estado = device.getBondState();
    }

    public DeviceItem(String name, String address, int estado) {
        this.name = name;
        this.address = address;
        this.estado = estado;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int isEmparejado() {
        return estado;
    }

    public void setEmparejado(int estado) {
        this.estado = estado;
    }
}
