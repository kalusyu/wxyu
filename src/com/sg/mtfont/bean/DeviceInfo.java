package com.sg.mtfont.bean;

import java.io.Serializable;

/**
 * 
 * @author Kalus Yu
 * 
 */
public class DeviceInfo implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 3503837813374819719L;
    String id;
    String imei;
    String macAddress;
    String imsi;
    String product;
    String brand;
    String manufacturer;
    String device;
    String sdk;
    String board;
    String display;
    String host;
    String model;
    String time;
    String androidVersion;
    
    String telephone;
    String networkType;
    String simOperatorName;
    String simSerialNumber;
    String simState;
    
    
    public DeviceInfo(String imei, String macAddress, String imsi,
            String product, String brand, String manufacturer, String device,
            String sdk, String board, String display, String host,
            String model, String time, String androidVersion, String telephone,
            String networkType, String simOperatorName, String simSerialNumber,
            String simState) {
        super();
        this.imei = imei;
        this.macAddress = macAddress;
        this.imsi = imsi;
        this.product = product;
        this.brand = brand;
        this.manufacturer = manufacturer;
        this.device = device;
        this.sdk = sdk;
        this.board = board;
        this.display = display;
        this.host = host;
        this.model = model;
        this.time = time;
        this.androidVersion = androidVersion;
        this.telephone = telephone;
        this.networkType = networkType;
        this.simOperatorName = simOperatorName;
        this.simSerialNumber = simSerialNumber;
        this.simState = simState;
    }
    public String getImei() {
        return imei;
    }
    public void setImei(String imei) {
        this.imei = imei;
    }
    public String getMacAddress() {
        return macAddress;
    }
    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }
    public String getImsi() {
        return imsi;
    }
    public void setImsi(String imsi) {
        this.imsi = imsi;
    }
    public String getProduct() {
        return product;
    }
    public void setProduct(String product) {
        this.product = product;
    }
    public String getBrand() {
        return brand;
    }
    public void setBrand(String brand) {
        this.brand = brand;
    }
    public String getManufacturer() {
        return manufacturer;
    }
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }
    public String getDevice() {
        return device;
    }
    public void setDevice(String device) {
        this.device = device;
    }
    public String getSdk() {
        return sdk;
    }
    public void setSdk(String sdk) {
        this.sdk = sdk;
    }
    public String getBoard() {
        return board;
    }
    public void setBoard(String board) {
        this.board = board;
    }
    public String getDisplay() {
        return display;
    }
    public void setDisplay(String display) {
        this.display = display;
    }
    public String getHost() {
        return host;
    }
    public void setHost(String host) {
        this.host = host;
    }
    public String getModel() {
        return model;
    }
    public void setModel(String model) {
        this.model = model;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getAndroidVersion() {
        return androidVersion;
    }
    public void setAndroidVersion(String androidVersion) {
        this.androidVersion = androidVersion;
    }
    public String getTelephone() {
        return telephone;
    }
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
    public String getNetworkType() {
        return networkType;
    }
    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }
    public String getSimOperatorName() {
        return simOperatorName;
    }
    public void setSimOperatorName(String simOperatorName) {
        this.simOperatorName = simOperatorName;
    }
    public String getSimSerialNumber() {
        return simSerialNumber;
    }
    public void setSimSerialNumber(String simSerialNumber) {
        this.simSerialNumber = simSerialNumber;
    }
    public String getSimState() {
        return simState;
    }
    public void setSimState(String simState) {
        this.simState = simState;
    }
    @Override
    public String toString() {
        return "DeviceInfo [id=" + id + ", imei=" + imei + ", macAddress="
                + macAddress + ", imsi=" + imsi + ", product=" + product
                + ", brand=" + brand + ", manufacturer=" + manufacturer
                + ", device=" + device + ", sdk=" + sdk + ", board=" + board
                + ", display=" + display + ", host=" + host + ", model="
                + model + ", time=" + time + ", androidVersion="
                + androidVersion + ", telephone=" + telephone
                + ", networkType=" + networkType + ", simOperatorName="
                + simOperatorName + ", simSerialNumber=" + simSerialNumber
                + ", simState=" + simState + "]";
    }
    
    
    
    
}
