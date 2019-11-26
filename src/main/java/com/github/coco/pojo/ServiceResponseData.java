package com.github.coco.pojo;

import com.github.coco.GlobalState;

/**
 * @author db1995
 */
public class ServiceResponseData extends AbstractResponseData {
    private String customerId;

    public ServiceResponseData(Type type) {
        if (type != Type.MESSAGE) {
            customerInQueue = GlobalState.CUSTOMER_QUEUE.size();
        }
        this.type = type;
    }

    public ServiceResponseData(String customerId, Type type) {
        if (type != Type.MESSAGE) {
            customerInQueue = GlobalState.CUSTOMER_QUEUE.size();
        }
        this.customerId = customerId;
        this.type = type;
    }

    public ServiceResponseData(String customerId, Type type, String message) {
        if (type != Type.MESSAGE) {
            customerInQueue = GlobalState.CUSTOMER_QUEUE.size();
        }
        this.customerId = customerId;
        this.type = type;
        this.message = message;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public Type getType() {
        return type;
    }
}
