package org.zywx.wbpalmstar.plugin.uexbluetoothle;

public class JsConst {
    public static final String ON_CONNECTION_STATE_CHANGE = "uexBluetoothLE.onConnectionStateChange";
    public static final String ON_CHARACTERISTIC_READ = "uexBluetoothLE.cbReadCharacteristic";
    public static final String ON_CHARACTERISTIC_CHANGED = "uexBluetoothLE.onCharacteristicChanged";
    public static final String ON_CHARACTERISTIC_CHANGED_JSON = "uexBluetoothLE.onCharacteristicChangedJson";
    public static final String ON_CHARACTERISTIC_WRITE = "uexBluetoothLE.cbWriteCharacteristic";
    public static final String ON_CHARACTERISTIC_WRITE_JSON = "uexBluetoothLE.cbWriteCharacteristicJson";
    public static final String ON_CHARACTERISTIC_WRITE_BY_JSON = "uexBluetoothLE.cbWriteCharacteristicByJson";
    public static final String ON_CHARACTERISTIC_WRITE_BY_JSONS = "uexBluetoothLE.cbWriteCharacteristicByJsons";
    public static final String ON_LE_SCAN = "uexBluetoothLE.onLeScan";
    public static final String ON_READ_REMOTE_RSSI="uexBluetoothLE.onReadRemoteRssi";
    public static final String CALLBACK_CONNECT = "uexBluetoothLE.cbConnect";
    public static final String CALLBACK_CONNECT_PRINTER = "uexBluetoothLE.cbConnectPrinter";
    public static final String CALLBACK_SEARCH_FOR_CHARACTERISTIC = "uexBluetoothLE.cbSearchForCharacteristic";
    public static final String CALLBACK_SEARCH_FOR_DESCRIPTOR = "uexBluetoothLE.cbSearchForDescriptor";
    public static final String CALLBACK_READ_DESCRIPTOR = "uexBluetoothLE.cbReadDescriptor";
    public static final String CALLBACK_WRITE_DESCRIPTOR = "uexBluetoothLE.cbWriteDescriptor";
    public static final String CALLBACK_INIT = "uexBluetoothLE.cbInit";
    public static final String CALLBACK_CONNECT_PRINTER_STATECHANGE ="uexBluetoothLE.cbConnectPrinterStateChange";
    public static final String CALLBACK_PRINT_BY_JSON = "uexBluetoothLE.cbPrintByJson";
    public static final String CALLBACK_GET_BONDED_DEVICES = "uexBluetoothLE.cbGetBondedDevices";

    public static final String SERIALPORTPATH = "SerialPortPath";
    public static final String SERIALPORTBAUDRATE = "SerialPortBaudrate";
    public static final String WIFI_CONFIG_IP = "wifi config ip";
    public static final String WIFI_CONFIG_PORT = "wifi config port";
    public static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    public static final int BLUETOOTH_REQUEST_CODE = 0x001;
    public static final int USB_REQUEST_CODE = 0x002;
    public static final int WIFI_REQUEST_CODE = 0x003;
    public static final int SERIALPORT_REQUEST_CODE = 0x006;
    public static final int CONN_STATE_DISCONN = 0x007;
    public static final int MESSAGE_UPDATE_PARAMETER = 0x009;

    /**
     * wifi 默认ip
     */
    public static final String WIFI_DEFAULT_IP = "192.168.123.100";

    /**
     * wifi 默认端口号
     */
    public static final int WIFI_DEFAULT_PORT = 9100;
}
