package org.zywx.wbpalmstar.plugin.uexbluetoothle.vo;

import java.io.Serializable;
import java.util.List;

/**
 * author : Administrator
 * date : 2019/10/31 0031 16:49
 * description :
 */
public class PrintVo implements Serializable {
    private String order_num; //订单号;
    private String customer_num; //客户编码
    private String customer_name; //客户名称
    private String salesrep_num;
    private String salesrep_name;
    private String dt_name; //供应商名称
    private String warehouse_num;  // 仓库编码
    private String warehouse_name;// 仓库名称
    private List<ItemBean> itemList; //商品列表
    private String amount; //商品金额
    private String amount_ar;//应付金额
    private String cashback_amount; // 返现金额
    private String last_update_date; //最后修改时间
    private String total_quantity; //总数量
    private String qcardUrl; //二维码路径

    public String getQcardUrl() {
        return qcardUrl;
    }

    public void setQcardUrl(String qcardUrl) {
        this.qcardUrl = qcardUrl;
    }

    public String getTotal_quantity() {
        return total_quantity;
    }

    public void setTotal_quantity(String total_quantity) {
        this.total_quantity = total_quantity;
    }

    public String getCashback_amount() {
        return cashback_amount;
    }

    public void setCashback_amount(String cashback_amount) {
        this.cashback_amount = cashback_amount;
    }

    public static class ItemBean{
        private  String uom; //单位
        private String item_name;
        private String piece_bar_code;
        private String item_num;
        private String type;  //传1为买赠 ，不传是不同的
        private String order_quantity; //订单数量
        private String unit_price; //单价
        private String amount; //金额

        public String getUom() {
            return uom;
        }

        public void setUom(String uom) {
            this.uom = uom;
        }

        public String getItem_name() {
            return item_name;
        }

        public void setItem_name(String item_name) {
            this.item_name = item_name;
        }

        public String getPiece_bar_code() {
            return piece_bar_code;
        }

        public void setPiece_bar_code(String piece_bar_code) {
            this.piece_bar_code = piece_bar_code;
        }

        public String getItem_num() {
            return item_num;
        }

        public void setItem_num(String item_num) {
            this.item_num = item_num;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getOrder_quantity() {
            return order_quantity;
        }

        public void setOrder_quantity(String order_quantity) {
            this.order_quantity = order_quantity;
        }

        public String getUnit_price() {
            return unit_price;
        }

        public void setUnit_price(String unit_price) {
            this.unit_price = unit_price;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }
    }

    public String getOrder_num() {
        return order_num;
    }

    public void setOrder_num(String order_num) {
        this.order_num = order_num;
    }

    public String getCustomer_num() {
        return customer_num;
    }

    public void setCustomer_num(String customer_num) {
        this.customer_num = customer_num;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getSalesrep_num() {
        return salesrep_num;
    }

    public void setSalesrep_num(String salesrep_num) {
        this.salesrep_num = salesrep_num;
    }

    public String getSalesrep_name() {
        return salesrep_name;
    }

    public void setSalesrep_name(String salesrep_name) {
        this.salesrep_name = salesrep_name;
    }

    public String getDt_name() {
        return dt_name;
    }

    public void setDt_name(String dt_name) {
        this.dt_name = dt_name;
    }

    public String getWarehouse_num() {
        return warehouse_num;
    }

    public void setWarehouse_num(String warehouse_num) {
        this.warehouse_num = warehouse_num;
    }

    public String getWarehouse_name() {
        return warehouse_name;
    }

    public void setWarehouse_name(String warehouse_name) {
        this.warehouse_name = warehouse_name;
    }

    public List<ItemBean> getItemList() {
        return itemList;
    }

    public void setItemList(List<ItemBean> itemList) {
        this.itemList = itemList;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getAmount_ar() {
        return amount_ar;
    }

    public void setAmount_ar(String amount_ar) {
        this.amount_ar = amount_ar;
    }

    public String getLast_update_date() {
        return last_update_date;
    }

    public void setLast_update_date(String last_update_date) {
        this.last_update_date = last_update_date;
    }
}

