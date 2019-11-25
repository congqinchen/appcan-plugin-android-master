var serviceUUID;
var characteristicUUID;
function init() {
  var params = {};
  var data = JSON.stringify(params);
  uexBluetoothLE.init(data);
}

function connect() {
  var params = {
    address: "DC:1D:30:34:55:02"
  };
  var data = JSON.stringify(params);
  uexBluetoothLE.connect(data);
}

function connectPrinter() {
  var params = {
    address: "DC:1D:30:34:55:02"
  };
  var data = JSON.stringify(params);
  uexBluetoothLE.connectPrinter(data);
}

function disconnect() {
  var params = {};
  var data = JSON.stringify(params);
  uexBluetoothLE.disconnect(data);
}

function scanDevice() {
  var uuid = new Array();
  uuid[0] = "00001800-0000-1000-8000-00805f9b34fb";
  uexBluetoothLE.scanDevice(JSON.stringify(uuid));
}

function stopScanDevice() {
  var params = {};
  var data = JSON.stringify(params);
  uexBluetoothLE.stopScanDevice(data);
}

function writeCharacteristic() {
  var params = {};
  var data = JSON.stringify(params);
  uexBluetoothLE.writeCharacteristic(data);
}

function readCharacteristic() {
  var params = {};
  var data = JSON.stringify(params);
  uexBluetoothLE.readCharacteristic(data);
}

function searchForCharacteristic() {
  var params = {
    serviceUUID:serviceUUID[0]
  };
  var data = JSON.stringify(params);
  uexBluetoothLE.searchForCharacteristic(data);
}

function searchForDescriptor() {
  var params = {};
  var data = JSON.stringify(params);
  uexBluetoothLE.searchForDescriptor(data);
}

function readDescriptor() {
  var params = {};
  var data = JSON.stringify(params);
  uexBluetoothLE.readDescriptor(data);
}

function writeDescriptor() {
  var params = {};
  var data = JSON.stringify(params);
  uexBluetoothLE.writeDescriptor(data);
}

function printByJson() {
  var params ={
        params:{
               order_num:"XH11200000096",
               customer_num:"1001",
               customer_name:"武汉白云神山盈晖母婴用品店(账期赊销)",
               salesrep_num:"1005",
               salesrep_name:"钱喜东",
               dt_name:"XXXX商贸有限公司",
               warehouse_num:"111",
               warehouse_name:"中心仓",
               itemList:[
                   {
                       uom:"桶",
                       item_name:"康师傅 超爽桶老坛酸菜牛肉面 171g/包",
                       piece_bar_code:"6900873024285",
                       item_num:"67050132",
                       type:"",
                       order_quantity:"1",
                       unit_price:"4.29",
                       amount:"4.29"
                   },
                   {
                      uom:"包",
                      item_name:"炫迈无糖薄荷糖22.5g劲爽薄荷味",
                      piece_bar_code:"6924513907103",
                      item_num:"67050175",
                      type:"1",
                      order_quantity:"1",
                      unit_price:"0.00",
                      amount:"0.00"
                  }
               ],
               amount:"4.29",
               amount_ar:"4.29",
               cashback_amount:"0.00",
               last_update_date:"2019-11-1 10:10:00",
               total_quantity:"2支",
               qcardUrl:"/widget/images/zhidingbao_print.png"
            },
        count:1
  } ;
  var data = JSON.stringify(params);
  uexBluetoothLE.printByJson(data);
}

function getBondedDevices(){
    uexBluetoothLE.getBondedDevices();
}


window.uexOnload = function(type) {
  if (type == 0) {
    uexBluetoothLE.cbConnect = cbConnect;
    uexBluetoothLE.onLeScan = onLeScan;
    uexBluetoothLE.onConnectionStateChange = onConnectionStateChange;
    uexBluetoothLE.onServicesDiscovered = onServicesDiscovered;
    uexBluetoothLE.cbCharacteristicRead = onCharacteristicRead;
    uexBluetoothLE.onCharacteristicChanged = onCharacteristicChanged;
    uexBluetoothLE.cbCharacteristicWrite = onCharacteristicWrite;
    uexBluetoothLE.cbSearchForCharacteristic = cbSearchForCharacteristic;
    uexBluetoothLE.cbSearchForDescriptor = cbSearchForDescriptor;
    uexBluetoothLE.cbReadDescriptor = cbReadDescriptor;
    uexBluetoothLE.cbWriteDescriptor = cbWriteDescriptor;
    uexBluetoothLE.cbInit = cbInit;
    uexBluetoothLE.cbConnectPrinter = cbConnectPrinter;
    uexBluetoothLE.cbPrintByJson =cbPrintByJson;
    uexBluetoothLE.cbGetBondedDevices = cbGetBondedDevices;
  }
};

function cbGetBondedDevices(info){
    var json = JSON.parse(info);
}
function cbConnectPrinter(info){
     alert(info);
}


function cbPrintByJson(info){
    alert(info);
}
function cbConnect(info) {
    var json = JSON.parse(info);
    serviceUUID = json.services;
}

function cbInit(info) {
  alert(info);
}

function onLeScan(info) {
  alert(info);
}

function onConnectionStateChange(info) {
  alert(info);
}

function onServicesDiscovered(info) {
  alert(info);
}

function onCharacteristicRead(info) {
  alert(info);
}

function onCharacteristicChanged(info) {
  alert(info);
}

function onCharacteristicWrite(info) {
  alert(info);
}

function cbSearchForCharacteristic(info) {
   var json = JSON.parse(info);
   var characteristics = json.characteristics;
    characteristicUUID = characteristics[4].UUID;
}

function getCharacteristicUUID(o){
   characteristicUUID =  $(o).data("uuid");
   alert(characteristicUUID);
   printByJson();
}

function cbSearchForDescriptor(info) {
  alert(info);
}

function cbReadDescriptor(info) {
  alert(info);
}

function cbWriteDescriptor(info) {
  alert(info);
}
