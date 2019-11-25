package org.zywx.wbpalmstar.plugin.uexbluetoothle;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gprinter.command.EscCommand;
import com.gprinter.command.LabelCommand;
import com.gprinter.io.PortManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.zywx.wbpalmstar.base.BDebug;
import org.zywx.wbpalmstar.engine.EBrowserView;
import org.zywx.wbpalmstar.engine.universalex.EUExBase;
import org.zywx.wbpalmstar.plugin.uexbluetoothle.vo.BluetoothDeviceVO;
import org.zywx.wbpalmstar.plugin.uexbluetoothle.vo.CharacteristicVO;
import org.zywx.wbpalmstar.plugin.uexbluetoothle.vo.ConnectedVO;
import org.zywx.wbpalmstar.plugin.uexbluetoothle.vo.DescriptorInputVO;
import org.zywx.wbpalmstar.plugin.uexbluetoothle.vo.DeviceConnFactoryManager;
import org.zywx.wbpalmstar.plugin.uexbluetoothle.vo.GattDescriptorVO;
import org.zywx.wbpalmstar.plugin.uexbluetoothle.vo.PrintVo;
import org.zywx.wbpalmstar.plugin.uexbluetoothle.vo.ReadRemoteRssiVO;
import org.zywx.wbpalmstar.plugin.uexbluetoothle.vo.ResultVO;
import org.zywx.wbpalmstar.plugin.uexbluetoothle.vo.SearchForCharacteristicInputVO;
import org.zywx.wbpalmstar.plugin.uexbluetoothle.vo.SearchForCharacteristicOutputVO;
import org.zywx.wbpalmstar.plugin.uexbluetoothle.vo.SearchForDescriptorInputVO;
import org.zywx.wbpalmstar.plugin.uexbluetoothle.vo.SearchForDescriptorOutputVO;
import org.zywx.wbpalmstar.plugin.uexbluetoothle.vo.SetCharacteristicNotificationInputVO;
import org.zywx.wbpalmstar.plugin.uexbluetoothle.vo.ThreadPool;
import org.zywx.wbpalmstar.widgetone.uexBluetoothLE.R;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;



@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class EUExBluetoothLE extends EUExBase {

    private static final String BUNDLE_DATA = "data";
    private static final int MSG_INIT = 1;
    private static final int MSG_CONNECT = 2;
    private static final int MSG_DISCONNECT = 3;
    private static final int MSG_SCAN_DEVICE = 4;
    private static final int MSG_STOP_SCAN_DEVICE = 5;
    private static final int MSG_WRITE_CHARACTERISTIC = 6;
    private static final int MSG_READ_CHARACTERISTIC = 7;
    private static final int MSG_SEARCH_FOR_CHARACTERISTIC = 8;
    private static final int MSG_SEARCH_FOR_DESCRIPTOR = 9;
    private static final int MSG_READ_DESCRIPTOR = 10;
    private static final int MSG_WRITE_DESCRIPTOR = 11;
    private static final int MSG_PRINT_BY_JSON = 12;
    private static final int MSG_CONNECT_PRINTER=13;
    private static final int MSG_GET_BONDED_DEVICES=14;

    private static final int	REQUEST_CODE = 0x004;
    private String mBluetoothDeviceAddress;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBluetoothGatt;
    private Gson mGson;


    private String mCharFormat=null;
    private List<BluetoothGattService> mGattServices;
    private Context _context;
    private static final String TAG="appcan";

    private EBrowserView mCallbackView;
    private static final int REQUEST_ENABLE_BT=1;
    private ThreadPool threadPool;
    private int id = 0;
    private PortManager mPort;

    private static final int CONN_MOST_DEVICES=0x11;
    private static final int CONN_PRINTER=0x12;
    /**
     * 连接状态断开
     */
    private static final int CONN_STATE_DISCONN = 0x007;

    /**
     * 使用打印机指令错误
     */
    private static final int PRINTER_COMMAND_ERROR = 0x008;


    /**
     * ESC查询打印机实时状态指令
     */
    private byte[] esc = { 0x10, 0x04, 0x02 };


    /**
     * CPCL查询打印机实时状态指令
     */
    private byte[] cpcl = { 0x1b, 0x68 };


    /**
     * TSC查询打印机状态指令
     */
    private byte[] tsc = { 0x1b, '!', '?' };

    public EUExBluetoothLE(Context context, EBrowserView eBrowserView) {
        super(context, eBrowserView);
        this._context=context;
    }



    @Override
    protected boolean clean() {
        return false;
    }

    public void init(String[] params) {
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_INIT;
        Bundle bd = new Bundle();
        bd.putStringArray(BUNDLE_DATA, params);
        msg.setData(bd);
        mHandler.sendMessage(msg);
    }

    private void initMsg(String[] params) {
        mGson=new Gson();
        String json;
        try {
            if (params!=null&&params.length>0) {
                json = params[0];
                JSONObject jsonObject = new JSONObject(json);
                mCharFormat = jsonObject.optString("charFormat");
            }
        } catch (JSONException e) {
        }
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) mContext.
                    getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {

            }
        }
        mCallbackView=mBrwView;
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        // 确保蓝牙在设备上可以开启
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }else{
            callBackResult(true);
        }
        Log.i(TAG, "plugin init");
    }

    private void callBackResult(boolean result){
        ResultVO resultVO=new ResultVO();
        resultVO.setResultCode(result?ResultVO.RESULT_OK:ResultVO.RESULT_FAILD);
        callBackPluginJs(JsConst.CALLBACK_INIT, mGson.toJson(resultVO));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQUEST_ENABLE_BT){
            if (resultCode== Activity.RESULT_OK){
                callBackResult(true);
            }else {
                callBackResult(false);
            }
        }
    }

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            ResultVO resultVO=new ResultVO();
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                resultVO.setResultCode(ResultVO.RESULT_OK);
                mBluetoothGatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                resultVO.setResultCode(ResultVO.RESULT_FAILD);
            }

            callBackPluginJs(JsConst.ON_CONNECTION_STATE_CHANGE,mGson.toJson(resultVO));
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                mGattServices =mBluetoothGatt.getServices();
                try {
                    displayGattServices(mGattServices);
                } catch (InterruptedException e) {
                }
            }else if(status==129){
                mBluetoothAdapter.disable();
                mBluetoothAdapter.enable();
                final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(mBluetoothDeviceAddress);
                mBluetoothGatt = device.connectGatt(mContext, true, mGattCallback);
            } else {
                callBackPluginJs(JsConst.CALLBACK_CONNECT, "status:"+status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            ResultVO resultVO=new ResultVO();
            if (status == BluetoothGatt.GATT_SUCCESS) {
                resultVO.setResultCode(ResultVO.RESULT_OK);
            }else if (status==BluetoothGatt.GATT_FAILURE){
                resultVO.setResultCode(ResultVO.RESULT_FAILD);
            }
            resultVO.setData(transformDataFromCharacteristic(characteristic));
            callBackPluginJs(JsConst.ON_CHARACTERISTIC_READ, mGson.toJson(resultVO));
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            callBackPluginJs(JsConst.ON_CHARACTERISTIC_CHANGED,mGson.toJson(transformDataFromCharacteristic(characteristic)));
            callBackJsObjectBlue(JsConst.ON_CHARACTERISTIC_CHANGED_JSON,mGson.toJsonTree(transformDataFromCharacteristic(characteristic)));
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            ResultVO<CharacteristicVO> resultVO=new ResultVO<CharacteristicVO>();
            if (status == BluetoothGatt.GATT_SUCCESS) {
                resultVO.setResultCode(ResultVO.RESULT_OK);
                resultVO.setData(transformDataFromCharacteristic(characteristic));
            }else if (status==BluetoothGatt.GATT_FAILURE){
                resultVO.setResultCode(ResultVO.RESULT_FAILD);
            }
            callBackJsObjectBlue(JsConst.ON_CHARACTERISTIC_WRITE_JSON,mGson.toJsonTree(resultVO));
            callBackPluginJs(JsConst.ON_CHARACTERISTIC_WRITE, mGson.toJson(resultVO));
           }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
            ResultVO resultVO=new ResultVO();
            if (status==BluetoothGatt.GATT_SUCCESS){
                resultVO.setResultCode(ResultVO.RESULT_OK);
                resultVO.setData(transfromDescriptor(descriptor));
            }else{
                resultVO.setResultCode(ResultVO.RESULT_FAILD);
            }
            callBackPluginJs(JsConst.CALLBACK_READ_DESCRIPTOR,mGson.toJson(resultVO));
        }


        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            ResultVO resultVO=new ResultVO();
            if (status==BluetoothGatt.GATT_SUCCESS){
                resultVO.setResultCode(ResultVO.RESULT_OK);
                resultVO.setData(transfromDescriptor(descriptor));
            }else{
                resultVO.setResultCode(ResultVO.RESULT_FAILD);
            }
            callBackPluginJs(JsConst.CALLBACK_WRITE_DESCRIPTOR, mGson.toJson(resultVO));
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            ReadRemoteRssiVO remoteRssiVO=new ReadRemoteRssiVO();
            remoteRssiVO.rssi=rssi;
            remoteRssiVO.status=status;
            callBackJsObjectBlue(JsConst.ON_READ_REMOTE_RSSI,mGson.toJsonTree(remoteRssiVO));
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
        }

    };

    public void readRemoteRssi(String[] params){
        if (mBluetoothGatt!=null){
            mBluetoothGatt.readRemoteRssi();
        }
    }


    public void setCharacteristicNotification(String[] params){
        if (params.length<1){
            return;
        }
        if (mBluetoothGatt==null){
            return;
        }
        SetCharacteristicNotificationInputVO inputVO=
                mGson.fromJson(params[0],SetCharacteristicNotificationInputVO.class);
        BluetoothGattCharacteristic characteristic=getCharacteristicByID(inputVO.serviceUUID,inputVO.characteristicUUID);
        mBluetoothGatt.setCharacteristicNotification(characteristic,inputVO.enable);
    }


    // Demonstrates how to iterate through the supported GATT Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the ExpandableListView
    // on the UI.
    private void displayGattServices(List<BluetoothGattService> gattServices) throws InterruptedException {
        if (gattServices == null) return;
        List<String> serviceUUIDs=new ArrayList<String>();
        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            String uuid = gattService.getUuid().toString();
            serviceUUIDs.add(uuid);
        }
        ConnectedVO connectedVO=new ConnectedVO();
        connectedVO.services=serviceUUIDs;
        BDebug.i(TAG,mGson.toJson(connectedVO));
        callBackPluginJs(JsConst.CALLBACK_CONNECT, mGson.toJson(connectedVO));
    }

    /**
     * 将指定byte数组以16进制的形式打印到控制台
     * @param hint String
     * @param b byte[]
     * @return void
     */
    public static void printHexString(String hint, byte[] b) {

        StringBuilder ss = new StringBuilder();

        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }

            ss.append(hex);

        }

        Log.i(TAG, ss.toString().toUpperCase());
    }

    public CharacteristicVO transformDataFromCharacteristic(BluetoothGattCharacteristic characteristic){
        CharacteristicVO characteristicVO=new CharacteristicVO();
        final byte[] data=characteristic.getValue();
        String vStr = null;
        if (data!=null) {
            if (!TextUtils.isEmpty(mCharFormat)) {

                StringBuilder stringBuilder = new StringBuilder(data.length);
                for (byte byteChar : data) {
                    stringBuilder.append(String.format(mCharFormat, byteChar));
                }


                //printHexString("", data);

                vStr = stringBuilder.toString();

                Log.i(TAG, "-- transformDataFromCharacteristic1 " + vStr);
                characteristicVO.setValue(vStr);
                characteristicVO.setNeedDecode(false);
            } else {


                //printHexString("", data);

                vStr = Base64.encodeToString(data,Base64.DEFAULT);

                Log.i(TAG, "-- transformDataFromCharacteristic base64 string" + vStr);

                characteristicVO.setValue(vStr);
                characteristicVO.setNeedDecode(true);
            }
        }
        characteristicVO.setPermissions(characteristic.getPermissions());
        characteristicVO.setWriteType(characteristic.getWriteType());
        characteristicVO.setUUID(characteristic.getUuid().toString());
        characteristicVO.setServiceUUID(characteristic.getService().getUuid().toString());
        List<BluetoothGattDescriptor> descriptors=characteristic.getDescriptors();
        List<GattDescriptorVO> gattDescriptorVOs=new ArrayList<GattDescriptorVO>();
        if (descriptors!=null&&!descriptors.isEmpty()){
            for (BluetoothGattDescriptor descriptor:descriptors){
                gattDescriptorVOs.add(transfromDescriptor(descriptor));
            }
        }
        characteristicVO.setDescriptors(gattDescriptorVOs);
        return characteristicVO;
    }

    private GattDescriptorVO transfromDescriptor(BluetoothGattDescriptor descriptor){
        GattDescriptorVO gattDescriptorVO=new GattDescriptorVO();
        if (descriptor.getUuid()!=null) {
            gattDescriptorVO.setUUID(descriptor.getUuid().toString());
        }
        if (descriptor.getValue()!=null) {
            gattDescriptorVO.setValue(Base64.encodeToString(descriptor.getValue(), Base64.DEFAULT));
            gattDescriptorVO.setNeedDecode(true);
        }
        gattDescriptorVO.setServiceUUID(descriptor.getCharacteristic().getService().getUuid().toString());
        gattDescriptorVO.setCharacteristicUUID(descriptor.getCharacteristic().getUuid().toString());
        gattDescriptorVO.setPermissions(descriptor.getPermissions());
        return gattDescriptorVO;
    }

    public void connectPrinter(String[] params) {
        if (params == null || params.length < 1) {
            errorCallback(0, 0, "error params!");
            return;
        }
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_CONNECT_PRINTER;
        Bundle bd = new Bundle();
        bd.putStringArray(BUNDLE_DATA, params);
        msg.setData(bd);
        mHandler.sendMessage(msg);
    }

    private void connectPrinterMsg(String[] params) {
        String json = params[0];
        Log.e("json",json);
        String address=null;
        try {
            JSONObject jsonObject = new JSONObject(json);
            address=jsonObject.optString("address").toUpperCase();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (mBluetoothAdapter == null || address == null) {
            return;
        }

        if (DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id]==null ) {
            new DeviceConnFactoryManager.Build()
                    .setConnMethod(DeviceConnFactoryManager.CONN_METHOD.BLUETOOTH)
                    .setId(id)
                    .setContext(_context)
                    .setMacAddress(address).setCallbackConnState(
                            new DeviceConnFactoryManager.CallbackConnState() {
                                @Override
                                public void getConnState(int states) {
                                        ResultVO resultVO = new ResultVO();
                                        resultVO.setResultCode(states);
                                        callBackPluginJs(JsConst.CALLBACK_CONNECT_PRINTER,mGson.toJson(resultVO));
                                }
                            }
                    )
                    .build();
        }

        if(DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id]!=null){
            threadPool = ThreadPool.getInstantiation();
            threadPool.addTask( new Runnable()
            {
                @Override
                public void run()
                {
                    DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].openPort();
                }
            } );
        }
      //  final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);

        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
       // mBluetoothGatt = device.connectGatt(mContext, false, mGattCallback);

      //  mBluetoothDeviceAddress = address;

    }


    public void connect(String[] params) {
        if (params == null || params.length < 1) {
            errorCallback(0, 0, "error params!");
            return;
        }
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_CONNECT;
        Bundle bd = new Bundle();
        bd.putStringArray(BUNDLE_DATA, params);
        msg.setData(bd);
        mHandler.sendMessage(msg);
    }

    private void connectMsg(String[] params) {
        String json = params[0];
        Log.e("json",json);
        String address=null;
        try {
            JSONObject jsonObject = new JSONObject(json);
            address=jsonObject.optString("address").toUpperCase();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (mBluetoothAdapter == null || address == null) {
            return;
        }

        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress) && mBluetoothGatt != null) {
            mBluetoothGatt.connect();
            return;
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);

        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(mContext, false, mGattCallback);

        mBluetoothDeviceAddress = address;

    }

    public void disconnect(String[] params) {
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_DISCONNECT;
        mHandler.sendMessage(msg);
    }

    private void disconnectMsg(String[] params) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.disconnect();

    }

    public void scanDevice(String[] params) {
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_SCAN_DEVICE;
        Bundle bd = new Bundle();
        bd.putStringArray(BUNDLE_DATA, params);
        msg.setData(bd);
        mHandler.sendMessage(msg);
    }

    private void scanDeviceMsg(String[] params) {
        if (params!=null&&params.length>0) {
            String json = params[0];
            List<String> uuidStrings = mGson.fromJson(json, new TypeToken<List<String>>() {
            }.getType());
            UUID[] uUIDs=new UUID[uuidStrings.size()];
            for (int i = 0; i < uuidStrings.size(); i++) {
                uUIDs[i]=UUID.fromString(uuidStrings.get(i));
            }
            mBluetoothAdapter.startLeScan(uUIDs,mLeScanCallback);
        }else{
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        }
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            BluetoothDeviceVO deviceVO=new BluetoothDeviceVO();
            deviceVO.setAddress(device.getAddress());
            deviceVO.setName(device.getName());
            deviceVO.setRssi(rssi);
            callBackPluginJs(JsConst.ON_LE_SCAN, mGson.toJson(deviceVO));
        }
    };

    public void stopScanDevice(String[] params) {
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_STOP_SCAN_DEVICE;
        mHandler.sendMessage(msg);
    }

    private void stopScanDeviceMsg(String[] params) {
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
    }

    public void writeCharacteristic(String[] params) {
        if (params == null || params.length < 1) {
            errorCallback(0, 0, "error params!");
            return;
        }
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_WRITE_CHARACTERISTIC;
        Bundle bd = new Bundle();
        bd.putStringArray(BUNDLE_DATA, params);
        msg.setData(bd);
        mHandler.sendMessage(msg);
    }

    private void writeCharacteristicMsg(String[] params) {
        String json = params[0];
        String serviceUUID=null;
        String characteristicUUID=null;
        String value=null;
        Boolean isHexString=false;
        try {
            JSONObject jsonObject = new JSONObject(json);
            serviceUUID=jsonObject.optString("serviceUUID");
            characteristicUUID=jsonObject.optString("characteristicUUID");
            value=jsonObject.optString("value");
            if (!jsonObject.isNull("isHexString")) {
                isHexString=jsonObject.optBoolean("isHexString");
            }

        } catch (JSONException e) {
        }
        writeCharacteristicByUUID(serviceUUID, characteristicUUID, value, isHexString);
    }

    public void getBondedDevices(String [] params){
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_GET_BONDED_DEVICES;
        Bundle bd = new Bundle();
        bd.putStringArray(BUNDLE_DATA, params);
        msg.setData(bd);
        mHandler.sendMessage(msg);
    }

    public void getBondedDevicesMsg(String [] params){
         mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
         ResultVO resultVO = new ResultVO();
         List<BluetoothDeviceVO> deviceVOS= new ArrayList<>();
        String msg="";
        if (mBluetoothAdapter == null) {
            msg = "该手机不支持蓝牙";
            resultVO.setResultCode(ResultVO.RESULT_FAILD);
        } else {
            // If BT is not on, request that it be enabled.
            // setupChat() will then be called during onActivityResult
            if (!mBluetoothAdapter.isEnabled()) {
                msg = "未开启手机蓝牙";
                resultVO.setResultCode(ResultVO.RESULT_FAILD);
            } else {
                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                if (pairedDevices.size() > 0) {
                    for (BluetoothDevice device : pairedDevices) {
                        BluetoothDeviceVO deviceVO = new BluetoothDeviceVO();
                        deviceVO.setName(device.getName());
                        deviceVO.setAddress(device.getAddress());
                        deviceVOS.add(deviceVO);
                    }
                    Log.e("deviceVO",mGson.toJson(deviceVOS)+"");
                    resultVO.setData(deviceVOS);
                } else {
                     msg = "没有配对设备";
                     resultVO.setResultCode(2);
                }
            }
        }
        callBackPluginJs(JsConst.CALLBACK_GET_BONDED_DEVICES,mGson.toJson(resultVO));
    }


    //根据JSON字符串写入
    public  void printByJson(String[] params){
        if (params == null || params.length < 1) {
            errorCallback(0, 0, "error params!");
            return;
        }
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_PRINT_BY_JSON;
        Bundle bd = new Bundle();
        bd.putStringArray(BUNDLE_DATA, params);
        msg.setData(bd);
        mHandler.sendMessage(msg);
    }

    private void printByJsonMsg(String[] params) {
        String json = params[0];
        String value=null;
        int counts = 1;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Log.e("mBluetoothAdapter",mBluetoothAdapter.isEnabled()+"");
        if(!mBluetoothAdapter.isEnabled()){
            ResultVO resultVO = new ResultVO();
            resultVO.setResultCode(ResultVO.RESULT_FAILD);
            resultVO.setData("蓝牙未打开");
            callBackPluginJs(JsConst.CALLBACK_PRINT_BY_JSON,new Gson().toJson(resultVO));
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject(json);
            value=jsonObject.optString("params");
            counts =TextUtils.isEmpty(jsonObject.optString("counts")) ?  1 :Integer.valueOf(jsonObject.optString("counts"));
            sendReceiptWithResponse(value,counts);
        } catch (JSONException e) {
        }
    }

    public  byte[] subBytes(byte[] src, int begin, int count) {
        byte[] bs = new byte[count];
        System.arraycopy(src, begin, bs, 0, count);
        return bs;
    }

    /**
     * 将两个ASCII字符合成一个字节；
     * 如："EF"--> 0xEF
     * @param src0 byte
     * @param src1 byte
     * @return byte
     */
    public static byte uniteBytes(byte src0, byte src1) {
        byte _b0 = Byte.decode("0x" + new String(new byte[]{src0})).byteValue();
        _b0 = (byte)(_b0 << 4);
        byte _b1 = Byte.decode("0x" + new String(new byte[]{src1})).byteValue();
        byte ret = (byte)(_b0 ^ _b1);
        return ret;
    }

    /**
     * 将指定字符串src，以每两个字符分割转换为16进制形式
     * 如："2B44EFD9" --> byte[]{0x2B, 0x44, 0xEF, 0xD9}
     * @param src String
     * @return byte[]
     */
    public static byte[] HexString2Bytes(String src){

        int len = src.length()/2;

        byte[] ret = new byte[len];
        byte[] tmp = src.getBytes();
        for(int i=0; i<len; i++){
            ret[i] = uniteBytes(tmp[i*2], tmp[i*2+1]);
        }
        return ret;
    }


    private void writeCharacteristicByUUID(String serviceUUID,String characteristicUUID,String value, Boolean isHexString){

        if (mGattServices==null){
            return;
        }
        for (int i = 0; i < mGattServices.size(); i++) {
            BluetoothGattService bluetoothGattService = mGattServices.get(i);
            if (serviceUUID.equals(bluetoothGattService.getUuid().toString())){
                BluetoothGattCharacteristic gattCharacteristic=bluetoothGattService.
                        getCharacteristic(UUID.fromString(characteristicUUID));

                if (isHexString == true) {


                    byte[] data = Base64.decode(value,Base64.DEFAULT);

                    byte[] orgData = HexString2Bytes(new String(data));

                    gattCharacteristic.setValue(orgData);



                } else {

                    gattCharacteristic.setValue(Base64.decode(value,Base64.DEFAULT));

                }


                mBluetoothGatt.writeCharacteristic(gattCharacteristic);
                break;
            }
        }
    }




    public void readCharacteristic(String[] params) {
        if (params == null || params.length < 1) {
            errorCallback(0, 0, "error params!");
            return;
        }
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_READ_CHARACTERISTIC;
        Bundle bd = new Bundle();
        bd.putStringArray(BUNDLE_DATA, params);
        msg.setData(bd);
        mHandler.sendMessage(msg);
    }

    private void readCharacteristicMsg(String[] params) {
        String json = params[0];
        String serviceUUID=null;
        String characteristicUUID=null;
        try {
            JSONObject jsonObject = new JSONObject(json);
            serviceUUID=jsonObject.optString("serviceUUID");
            characteristicUUID=jsonObject.optString("characteristicUUID");
        } catch (JSONException e) {
        }
        readCharacteristicByUUID(serviceUUID, characteristicUUID);
    }

    private void readCharacteristicByUUID(String serviceUUID,String characteristicUUID){
        if (mGattServices==null){
            return;
        }
        for (int i = 0; i < mGattServices.size(); i++) {
            BluetoothGattService bluetoothGattService = mGattServices.get(i);
            if (serviceUUID.equals(bluetoothGattService.getUuid().toString())){
                BluetoothGattCharacteristic gattCharacteristic=bluetoothGattService.
                        getCharacteristic(UUID.fromString(characteristicUUID));
                mBluetoothGatt.readCharacteristic(gattCharacteristic);
              break;
            }
        }
    }

    public void searchForCharacteristic(String[] params) {
        if (params == null || params.length < 1) {
            errorCallback(0, 0, "error params!");
            return;
        }
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_SEARCH_FOR_CHARACTERISTIC;
        Bundle bd = new Bundle();
        bd.putStringArray(BUNDLE_DATA, params);
        msg.setData(bd);
        mHandler.sendMessage(msg);
    }

    private void searchForCharacteristicMsg(String[] params) {
        String json = params[0];
        List<BluetoothGattCharacteristic> characteristicList=new ArrayList<BluetoothGattCharacteristic>();
        Log.e("servieUUid",json);
        SearchForCharacteristicInputVO inputVO=mGson.fromJson(json,SearchForCharacteristicInputVO.class);
        characteristicList=getServiceByID(inputVO.getServiceUUID()).getCharacteristics();
        List<CharacteristicVO> characteristicVOs=new ArrayList<CharacteristicVO>();
        if (characteristicList!=null){
            for (BluetoothGattCharacteristic characteristic:characteristicList){
                characteristicVOs.add(transformDataFromCharacteristic(characteristic));
            }
        }
        SearchForCharacteristicOutputVO outputVO=new SearchForCharacteristicOutputVO();
        outputVO.setServiceUUID(inputVO.getServiceUUID());
        outputVO.setCharacteristics(characteristicVOs);
        callBackPluginJs(JsConst.CALLBACK_SEARCH_FOR_CHARACTERISTIC, mGson.toJson(outputVO));
    }

    private BluetoothGattService getServiceByID(String UUID){
        for (BluetoothGattService service:mGattServices){
            if (service.getUuid().toString().equals(UUID)){
                return  service;
            }
        }
        return null;
    }

    private BluetoothGattCharacteristic getCharacteristicByID(String serviceUUID, String characteristicUUID){
        return getServiceByID(serviceUUID).getCharacteristic(UUID.fromString(characteristicUUID));
    }

    public void searchForDescriptor(String[] params) {
        if (params == null || params.length < 1) {
            errorCallback(0, 0, "count params!");
            return;
        }
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_SEARCH_FOR_DESCRIPTOR;
        Bundle bd = new Bundle();
        bd.putStringArray(BUNDLE_DATA, params);
        msg.setData(bd);
        mHandler.sendMessage(msg);
    }

    private void searchForDescriptorMsg(String[] params) {
        String json = params[0];
        List<GattDescriptorVO> descriptorVOs=new ArrayList<GattDescriptorVO>();
        SearchForDescriptorInputVO inputVO=mGson.fromJson(json,SearchForDescriptorInputVO.class);
        List<BluetoothGattDescriptor> descriptors=getCharacteristicByID(inputVO.getServiceUUID(),inputVO.getCharacteristicUUID()).getDescriptors();
        if (descriptors!=null){
            for (BluetoothGattDescriptor descriptor:descriptors){
                descriptorVOs.add(transfromDescriptor(descriptor));
            }
        }
        SearchForDescriptorOutputVO outputVO=new SearchForDescriptorOutputVO();
        outputVO.setServiceUUID(inputVO.getServiceUUID());
        outputVO.setCharacteristicUUID(inputVO.getCharacteristicUUID());
        outputVO.setDescriptors(descriptorVOs);
        callBackPluginJs(JsConst.CALLBACK_SEARCH_FOR_DESCRIPTOR, mGson.toJson(outputVO));
    }

    public void readDescriptor(String[] params) {
        if (params == null || params.length < 1) {
            errorCallback(0, 0, "error params!");
            return;
        }
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_READ_DESCRIPTOR;
        Bundle bd = new Bundle();
        bd.putStringArray(BUNDLE_DATA, params);
        msg.setData(bd);
        mHandler.sendMessage(msg);
    }

    private void readDescriptorMsg(String[] params) {
        String json = params[0];
        DescriptorInputVO inputVO=mGson.fromJson(json,DescriptorInputVO.class);
        BluetoothGattDescriptor descriptor = getCharacteristicByID(inputVO.getServiceUUID(), inputVO
                .getCharacteristicUUID())
                .getDescriptor(UUID
                        .fromString(inputVO.getDescriptorUUID()));
        mBluetoothGatt.readDescriptor(descriptor);
    }

    public void writeDescriptor(String[] params) {
        if (params == null || params.length < 1) {
            errorCallback(0, 0, "error params!");
            return;
        }
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_WRITE_DESCRIPTOR;
        Bundle bd = new Bundle();
        bd.putStringArray(BUNDLE_DATA, params);
        msg.setData(bd);
        mHandler.sendMessage(msg);
    }

    private void writeDescriptorMsg(String[] params) {
        String json = params[0];
        DescriptorInputVO inputVO=mGson.fromJson(json,DescriptorInputVO.class);
        BluetoothGattDescriptor descriptor=getCharacteristicByID(inputVO.getServiceUUID(),inputVO
                .getCharacteristicUUID())
                .getDescriptor(UUID
                        .fromString(inputVO.getDescriptorUUID()));
        descriptor.setValue(Base64.decode(inputVO.getValue(), Base64.DEFAULT));
        mBluetoothGatt.writeDescriptor(descriptor);
    }

    @Override
    public void onHandleMessage(Message message) {
        if(message == null){
            return;
        }
        Bundle bundle=message.getData();
        switch (message.what) {

            case MSG_INIT:
                initMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            case MSG_CONNECT:
                connectMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            case MSG_DISCONNECT:
                disconnectMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            case MSG_SCAN_DEVICE:
                scanDeviceMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            case MSG_STOP_SCAN_DEVICE:
                stopScanDeviceMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            case MSG_WRITE_CHARACTERISTIC:
                writeCharacteristicMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            case MSG_READ_CHARACTERISTIC:
                readCharacteristicMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            case MSG_SEARCH_FOR_CHARACTERISTIC:
                searchForCharacteristicMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            case MSG_SEARCH_FOR_DESCRIPTOR:
                searchForDescriptorMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            case MSG_READ_DESCRIPTOR:
                readDescriptorMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            case MSG_WRITE_DESCRIPTOR:
                writeDescriptorMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            case MSG_PRINT_BY_JSON:
                printByJsonMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            case MSG_GET_BONDED_DEVICES:
                getBondedDevicesMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            case MSG_CONNECT_PRINTER:
                    connectPrinterMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            default:
                super.onHandleMessage(message);
        }
    }

    private void callBackPluginJs(String methodName, String jsonData){
        if (mCallbackView==null){
            return;
        }
        String js = SCRIPT_HEADER + "if(" + methodName + "){"
                + methodName + "('" + jsonData + "');}";
        Log.e("js",js);
        mCallbackView.addUriTask(js);
//        onCallback(js);
    }

    private void callBackJsObjectBlue(String methodName, Object jsonData){
        if (mCallbackView==null){
            return;
        }
        String js = SCRIPT_HEADER + "if(" + methodName + "){"
                + methodName + "(" + jsonData + ");}";
        mCallbackView.addUriTask(js);
    }




    /**
     * 发送票据
     */
    void sendReceiptWithResponse(String json,int count)
    {
        PrintVo vo = new Gson().fromJson(json,PrintVo.class);
        List<PrintVo.ItemBean> itemBeans = new ArrayList<>();
        itemBeans = vo.getItemList();
        if(itemBeans == null && itemBeans.size()>0){
            ResultVO resultVO = new ResultVO();
            resultVO.setResultCode(2);
            resultVO.setData("未传入商品");
            if(resultVO!=null)
            {
                callBackPluginJs(JsConst.CALLBACK_PRINT_BY_JSON,mGson.toJson(resultVO));
            }

            return;
        }
        EscCommand esc = new EscCommand();
        esc.addInitializePrinter();
        esc.addPrintAndFeedLines( (byte) 2 );
        /* 设置打印居中 */
        esc.addSelectJustification( EscCommand.JUSTIFICATION.CENTER );
        /* 设置为倍高倍宽 */
        esc.addSelectPrintModes( EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.ON, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF );
        /* 打印文字 */
        esc.addText( vo.getDt_name()+"\n" );
        esc.addPrintAndLineFeed();

        /* 打印文字 */
        /* 取消倍高倍宽 */
        esc.addSelectPrintModes( EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF );
        /* 设置打印左对齐 */
        esc.addSelectJustification( EscCommand.JUSTIFICATION.LEFT );
        /* 打印文字 */
        esc.addText( "订单号:" );
        /* 打印文字 */
        esc.addText( vo.getOrder_num()+"\n" );
        esc.addText( "客 户：" );
        esc.addText( vo.getCustomer_num()+" "+ vo.getCustomer_name() +"\n" );
        esc.addText( "业 务：" );
        esc.addText( vo.getSalesrep_num()+" "+ vo.getSalesrep_name() +"\n" );
        esc.addText( "发货仓：" );
        esc.addText( vo.getWarehouse_num()+" "+ vo.getWarehouse_name() +"\n" );
        /* 打印繁体中文 需要打印机支持繁体字库 */
//        String message = "佳博智匯票據打印機\n";
//        esc.addText( message, "GB2312" );
//        esc.addPrintAndLineFeed();
        esc.addText( "------------------------------------------------\n" );

        /* 绝对位置 具体详细信息请查看GP58编程手册 */
        esc.addText( "序号" );
        esc.addSetHorAndVerMotionUnits( (byte) 7, (byte) 0 );
        esc.addSetAbsolutePrintPosition( (short) 3 );
        esc.addText("商品 / 单位");
        esc.addSetAbsolutePrintPosition( (short) 12);
        esc.addText( "数量" );
        esc.addSetAbsolutePrintPosition( (short) 16);
        esc.addText( "单价" );
        esc.addSetAbsolutePrintPosition( (short) 19 );
        esc.addText( "金额" );
        esc.addPrintAndLineFeed();

        for(int i = 0;i<itemBeans.size();i++){
            PrintVo.ItemBean itemBean = itemBeans.get(i);
            esc.addText(""+(i+1));
            esc.addSetHorAndVerMotionUnits( (byte) 7, (byte) 1);
            esc.addSetAbsolutePrintPosition( (short) 3 );
            if(itemBean.getType().equals("1")){
                esc.addText("【赠】"+itemBean.getItem_name()+"\n");
            }else{
                esc.addText(itemBean.getItem_name()+"\n");
            }
            esc.addSetAbsolutePrintPosition( (short) 3 );
            esc.addText(""+itemBean.getPiece_bar_code());
            esc.addSetAbsolutePrintPosition( (short) 16 );
            esc.addText(itemBean.getItem_num()+"\n");
            esc.addSetAbsolutePrintPosition( (short) 3 );
            esc.addText(""+itemBean.getUom());
            esc.addSetAbsolutePrintPosition( (short) 12);
            esc.addText("x"+itemBean.getOrder_quantity());
            esc.addSetAbsolutePrintPosition( (short) 16 );
            esc.addText(""+itemBean.getUnit_price());
            esc.addSetAbsolutePrintPosition( (short) 19 );
            esc.addText(itemBean.getAmount()+"\n");
            esc.addPrintAndLineFeed();
        }

        /* 打印一维条码 */
        /* 打印文字 */


        esc.addText( "------------------------------------------------\n" );

        esc.addText("数量合计：");
        esc.addText(vo.getTotal_quantity()+"\n");
        esc.addText("金额合计：");
        esc.addText(vo.getAmount()+"\n");
        esc.addText("返现优惠：");
        esc.addText(vo.getCashback_amount()+"\n");
        esc.addText("应收金额：");
        esc.addText(vo.getAmount_ar()+"\n");
        esc.addPrintAndLineFeed();

        esc.addText("打印时间:");
        esc.addText(vo.getLast_update_date()+"\n");
        esc.addText("打印次数:");
        esc.addText(count+"\n");

        esc.addPrintAndLineFeed();
        esc.addText("订单号:");
        esc.addSetAbsolutePrintPosition( (short) 8);
        esc.addText(vo.getOrder_num()+"\n");
        esc.addSelectJustification( EscCommand.JUSTIFICATION.CENTER );
        esc.addSelectPrintingPositionForHRICharacters( EscCommand.HRI_POSITION.NO_PRINT);
        /*
         * 设置条码可识别字符位置在条码下方
         * 设置条码高度为60点
         */
        esc.addSetBarcodeHeight( (byte) 60 );
        /* 设置条码单元宽度为1 */
        esc.addSetBarcodeWidth( (byte) 2);
        /* 打印Code128码 */
        esc.addCODE128( esc.genCodeB( ""+vo.getOrder_num()) );

        /* 设置打印居中对齐 */
        esc.addSelectJustification( EscCommand.JUSTIFICATION.LEFT );
        /* 打印文字 */
        esc.addText( "*智订宝APP打印*" );
        /* 打印文字 */
        esc.addSetAbsolutePrintPosition( (short) 9);
        esc.addText( "扫一扫下载APP\n" );
        esc.addSelectJustification( EscCommand.JUSTIFICATION.CENTER );
        /* 设置纠错等级 */
        esc.addSelectErrorCorrectionLevelForQRCode( (byte) 0x31 );
        /* 设置qrcode模块大小 */
        esc.addSelectSizeOfModuleForQRCode( (byte) 4);
        /* 设置qrcode内容 */
        esc.addStoreQRCodeData( "https://a.app.qq.com/o/simple.jsp?pkgname=com.bestone360.zhidingbao" );
        esc.addPrintQRCode(); /* 打印QRCode */

        /* 开钱箱 */
        esc.addGeneratePlus( LabelCommand.FOOT.F5, (byte) 255, (byte) 255 );
        esc.addPrintAndFeedLines( (byte) 3 );
        /* 加入查询打印机状态，用于连续打印 */
        byte[] bytes = { 29, 114, 1 };
        esc.addUserCommand( bytes );
        Vector<Byte> datas = esc.getCommand();
        /* 发送数据 */
        if( DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id]!=null){
            DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].sendDataImmediately( datas );
        }else{
            ResultVO resultVO1 = new ResultVO();
            resultVO1.setResultCode(ResultVO.RESULT_FAILD);
            resultVO1.setData("未连接打印机");
            callBackPluginJs(JsConst.CALLBACK_PRINT_BY_JSON,mGson.toJson(resultVO1));
            return;
        }
    }

    public Bitmap getBitmapImg(String url){
        Bitmap b=null;
        InputStream inputStream=null;
        AssetManager assets=_context.getAssets();
        try {
            inputStream=assets.open(url);
            b= BitmapFactory.decodeStream(inputStream);
            return b;
        } catch (Exception e) {
            e.printStackTrace();
            return b;
        }

    }

    /**
     * 发送票据测试
     */
    void sendReceiptWithResponse()
    {
        EscCommand esc = new EscCommand();
        esc.addInitializePrinter();
        esc.addPrintAndFeedLines( (byte) 2 );
        /* 设置打印居中 */
        esc.addSelectJustification( EscCommand.JUSTIFICATION.CENTER );
        /* 设置为倍高倍宽 */

        esc.addText( "扫一扫下载APP\n" );
        /* 设置纠错等级 */
        esc.addSelectErrorCorrectionLevelForQRCode( (byte) 0x31 );
        /* 设置qrcode模块大小 */
        esc.addSelectSizeOfModuleForQRCode( (byte) 4 );
        /* 设置qrcode内容 */
        esc.addStoreQRCodeData( "https://a.app.qq.com/o/simple.jsp?pkgname=com.bestone360.zhidingbao" );
        esc.addPrintQRCode(); /* 打印QRCode */
        /* 开钱箱 */
        esc.addGeneratePlus( LabelCommand.FOOT.F5, (byte) 255, (byte) 255 );
        esc.addPrintAndFeedLines( (byte) 3 );
        /* 加入查询打印机状态，用于连续打印 */
        byte[] bytes = { 29, 114, 1 };
        esc.addUserCommand( bytes );
        Vector<Byte> datas = esc.getCommand();
        Log.e("datas",new Gson().toJson(datas));
        /* 发送数据 */
        DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].sendDataImmediately( datas );

    }

    private BroadcastReceiver receiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive( Context context, Intent intent )
        {
            String action = intent.getAction();
            switch ( action )
            {
                case DeviceConnFactoryManager.ACTION_CONN_STATE:
                    int state = intent.getIntExtra( DeviceConnFactoryManager.STATE, -1 );
                    int deviceId = intent.getIntExtra( DeviceConnFactoryManager.DEVICE_ID, -1 );
                    switch ( state )
                    {
                        case DeviceConnFactoryManager.CONN_STATE_DISCONNECT:
                            if ( id == deviceId )
                            {
                                Toast.makeText(_context, "断开连接", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case DeviceConnFactoryManager.CONN_STATE_CONNECTING:
                             Toast.makeText(_context, "连接中....", Toast.LENGTH_SHORT).show();
                            break;
                        case DeviceConnFactoryManager.CONN_STATE_CONNECTED:
                                Toast.makeText(_context, "连接成功", Toast.LENGTH_SHORT).show();
                            break;
                        case DeviceConnFactoryManager.CONN_STATE_FAILED:
                                Toast.makeText(_context, "连接失败", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }
        }
    };



}
