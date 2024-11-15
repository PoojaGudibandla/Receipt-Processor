package com.receipts.controller;

import com.receipts.model.ReceiptRequestModel;
import com.receipts.model.ReceiptResponseModel;
import com.receipts.service.ProcessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/receipts")
public class ReceiptController {

    @Autowired
    private ProcessorService processorService;


    @GetMapping("/health")
    public String getService() {
        return "ReceiptController is up";
    }


    //Takes in a JSON receipt and returns a JSON object with an ID generated
    @PostMapping("/process")
    public ResponseEntity<ReceiptResponseModel> process(@RequestBody ReceiptRequestModel receiptRequest) {

        ReceiptResponseModel receiptResponseModel = new ReceiptResponseModel();
        try {
            receiptRequest.validateMessage();

            String id = processorService.storeReceipt(receiptRequest);
            receiptResponseModel.setId(id);

        } catch (Exception exp) {
            if(exp.getMessage().contains("Invalid payload"))
                return new ResponseEntity<ReceiptResponseModel>(HttpStatus.BAD_REQUEST);

            return new ResponseEntity<ReceiptResponseModel>(HttpStatus.INTERNAL_SERVER_ERROR);

        }
        return new ResponseEntity<ReceiptResponseModel>(receiptResponseModel, HttpStatus.OK);
    }

    // looks up the receipt by the ID and returns an object specifying the points awarded
    @GetMapping("/{id}/points")
    public ResponseEntity<Map> getPoints(@PathVariable String id) {
        HashMap response = new HashMap();
        try {
            response.put("points", processorService.calculatePoints(id));
        } catch (Exception exp) {
            return new ResponseEntity<Map>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<Map>(response, HttpStatus.OK);
    }


}
