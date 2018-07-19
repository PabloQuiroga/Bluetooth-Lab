package com.softtek.bluetooth;

import android.bluetooth.BluetoothDevice;

public class DeviceItem {
    private String name;
    private String address;
    private int estado;
    private BluetoothDevice device;

    public DeviceItem(BluetoothDevice btDevice){
        setDevice(btDevice);
        setName(device.getName());
        setAddress(device.getAddress());
        setEmparejado(device.getBondState());
    }

    public DeviceItem(String name, String address, int estado) {
        setName(name);
        setAddress(address);
        setEmparejado(estado);
    }

    private void setDevice(BluetoothDevice btDevice){
        this.device = btDevice;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    private void setAddress(String address) {
        this.address = address;
    }

    public int isEmparejado() {
        return estado;
    }

    private void setEmparejado(int estado) {
        this.estado = estado;
    }
}
