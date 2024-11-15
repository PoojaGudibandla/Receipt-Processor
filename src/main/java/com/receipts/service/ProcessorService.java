package com.receipts.service;

import com.receipts.model.ItemModel;
import com.receipts.model.ReceiptRequestModel;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ProcessorService {

    private Hashtable receipts = new Hashtable();
    private static List<String> oddDays = Arrays.asList(new String[]{"MONDAY", "WEDNESDAY", "FRIDAY", "SUNDAY"});


    public String storeReceipt(ReceiptRequestModel receiptRequestModel) {
        UUID uuid = UUID.randomUUID();
        receipts.put(uuid.toString(), receiptRequestModel);
        return uuid.toString();

    }

    public String calculatePoints(String id) throws Exception {
        ReceiptRequestModel receipt = (ReceiptRequestModel) receipts.get(id);
        if (receipt == null)
            throw new Exception("Receipt not available");

        int points = 0;
        //One point for every alphanumeric character in the retailer name
        if (!receipt.retailer.isEmpty())
            points += countAlphaNumeric(receipt.retailer);

        //50 points if the total is a round dollar amount with no cents
        if (!receipt.total.isEmpty() && isRoundDollarAmount(receipt.total))
            points += 50;

        //25 points if the total is a multiple of 0.25
        if (!receipt.total.isEmpty())
            if (Double.parseDouble(receipt.total) % 0.25 == 0)
                points += 25;

        //5 points for every two items on the receipt.
        if (!receipt.items.isEmpty())
            points += 5 * (receipt.items.size() / 2);


        //If the trimmed length of the item description is a multiple of 3,
        // multiply the price by 0.2 and round up to the nearest integer.
        // The result is the number of points earned.
        if (!receipt.items.isEmpty()) {
            for (ItemModel item : receipt.items) {
                int descLength = item.shortDescription.trim().length();

                if (descLength % 3 == 0)
                    points += Math.ceil(Double.valueOf(item.price) * 0.2);
            }
        }
        //6 points if the day in the purchase date is odd
        if (!receipt.purchaseDate.isEmpty()) {

            Integer date = Integer.valueOf(receipt.purchaseDate.substring(receipt.purchaseDate.length() - 2));

            if (date % 2 != 0)
                points += 6;

        }

        //10 points if the time of purchase is after 2:00pm and before 4:00pm
        if (!receipt.purchaseTime.isEmpty()) {
            LocalTime startTime = LocalTime.of(14, 0);
            LocalTime endTime = LocalTime.of(16, 0);


            // Check if the purchaseTime is within the range
            if (LocalTime.parse(receipt.purchaseTime).isAfter(startTime) && LocalTime.parse(receipt.purchaseTime).isBefore(endTime)) {
                points += 10;
            }


        }
        return String.valueOf(points);
    }

    private Integer countAlphaNumeric(String retailer) {

        if (isAlphaNumeric(retailer)) {
            return retailer.length();

        } else {
            return removeNonAlphanumeric(retailer).length();

        }
    }

    private Boolean isRoundDollarAmount(String total) {
        if (total.contains(".")) {
            String decimalVal = total.split("[\\.]")[1];
            if (decimalVal.replace("0", "").length() == 0)
                return true;
            else
                return false;
        }
        return true;
    }

    private String removeNonAlphanumeric(String str) {

        str = str.replaceAll(
                "[^a-zA-Z0-9]", "");

        return str;
    }

    public boolean isAlphaNumeric(String str) {
        // Regex to check string is alphanumeric or not.
        String regex = "^(?=.*[a-zA-Z])(?=.*[0-9])[A-Za-z0-9]+$";

        Pattern p = Pattern.compile(regex);


        if (str == null) {
            return false;
        }

        Matcher m = p.matcher(str);

        return m.matches();
    }


}
