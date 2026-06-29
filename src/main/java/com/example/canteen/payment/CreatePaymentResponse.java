package com.example.canteen.payment;

public class CreatePaymentResponse {

    private String orderId;
    private Integer amount;
    private String key;

    public CreatePaymentResponse(String orderId,Integer amount,String key){
        this.orderId=orderId;
        this.amount=amount;
        this.key=key;
    }

    public String getOrderId() {
        return orderId;
    }

    public Integer getAmount() {
        return amount;
    }

    public String getKey() {
        return key;
    }

}