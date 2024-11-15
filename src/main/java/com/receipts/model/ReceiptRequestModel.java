package com.receipts.model;

import java.util.List;

public class ReceiptRequestModel {


    public String retailer;

    public String purchaseDate;
    public String purchaseTime;
    public String total;

    public List<ItemModel> items;

    public void validateMessage() throws Exception {
        if (this.retailer.isEmpty() || this.retailer == null
                || this.purchaseTime.isEmpty() || this.purchaseTime == null
                || this.total.isEmpty() || this.total == null
                || this.purchaseDate.isEmpty() || this.purchaseDate == null
                || this.items.size() == 0 || this.items.isEmpty())
            throw new Exception("Invalid payload");
    }


}
