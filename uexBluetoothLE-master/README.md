# uexBluetoothBLE 蓝牙BLE插件接口文档


### 平台支持

```
Android 4.3+(API 18)
iOS 6.0+
```


### 初始化接口

```
init（param）
var param={
    charFormat://可选。读取Characteristic里面内容时用于format byte
};

```

### 扫描蓝牙设备
扫描到设备后通过onLeScan回调结果

```
scanDevice ();

```

### 停止扫描设备

```
stopScanDevice();
```

### 连接指定蓝牙设备
```
connect（param）
var param={
    address://要连接的蓝牙地址
};
```

### 断开蓝牙连接
```
disconnect();
```


### 扫描到设备回调
每扫描到一个设备都会回调一次，找到需要链接设备时应停止扫描

```
onLeScan(param)
var param={
	address:,//蓝牙设备地址
	name://蓝牙设备名称
}
```

### 读取Characteristic


```
readCharacteristic(param)
var param={
	serviceUUID://service的UUID
	characteristicUUID://characteristic的UUID
}
```

### 写入数据到Characteristic

```
writeCharacteristic(param)
var param={
	serviceUUID://service的UUID
	characteristicUUID://characteristic的UUID
	value://要写入的值
}
```
### 写入json数据到打印机
printByJson(param)
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
                  type:"1",  //是否赠送品
                  order_quantity:"1",
                  unit_price:"0.00",
                  amount:"0.00"
              }
           ],
           amount:"4.29",
           amount_ar:"4.29",
           cashback_amount:"0.00",
           last_update_date:"2019-11-1 10:10:00",
           total_quantity:"2支"
        },
    count:1
} ;
var data = JSON.stringify(params);
uexBluetoothLE.printByJson(data);

### 获取配对设备
 uexBluetoothLE.getBondedDevices();

### 获取配对设备回调
uexBluetoothLE.cbGetBondedDevices = cbGetBondedDevices;
function cbGetBondedDevices(info){
    alert(info);
}

### info json字符串结果：
    info:{
        data:[
            {
                address:"XX:XX:XX:XX",
                name:"XXX",
                rssi:0
            }
            .....
        ],
        msg:"",
        resultCode:"0" 0为成功 1为失败 2为没有配对设备
    }


### 连接状态变化回调

```
onConnectionStateChange(param)
var param={
	resultCode://0-已连接上，1-已断开
}
```
### ServicesDiscovered回调

```
onServicesDiscovered(param)

回调结果为List<GattServiceVO> 的Json格式字符串

```

GattServiceVO中的字段为：

```
  String uuid;
  
  List<CharacteristicVO> characteristics;
```
CharacteristicVO中得字段为：

```
String valueString;

String uuid;

int permissions;

int writeType;

List<GattDescriptorVO> gattDescriptors;

```
GattDescriptorVO中的字段为：

```
String uuid;

String value;

int permissions;
```

### onCharacteristicRead回调

```
onCharacteristicRead(param)
var param={
	resultCode://0-成功，1-失败
	data:CharacteristicVO的Json格式
}
CharacteristicVO字段同上
```

### onCharacteristicChanged回调

```
onCharacteristicChanged(param);
返回内容为CharacteristicVO 的Json格式
CharacteristicVO字段同上
```

### onCharacteristicWrite回调

```
onCharacteristicWrite(param)
var param={
	resultCode://0-成功，1-失败
	data:CharacteristicVO的Json格式
}
CharacteristicVO字段同上

```

### 



