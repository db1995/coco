package com.github.coco.pojo;

import com.github.coco.GlobalState;

/**
 * @author db1995
 */
public class CustomerResponseData extends AbstractResponseData {
    private String serviceName;

    public CustomerResponseData() {
    }

    public CustomerResponseData(Type type) {
        if (type != Type.MESSAGE) {
            customerInQueue = GlobalState.CUSTOMER_QUEUE.size();
        }
        this.type = type;
    }

    public CustomerResponseData(Type type, String message) {
        if (type != Type.MESSAGE) {
            customerInQueue = GlobalState.CUSTOMER_QUEUE.size();
        }
        this.type = type;
        this.message = message;
    }

    public CustomerResponseData(Type type, String serviceName, String message) {
        if (type != Type.MESSAGE) {
            customerInQueue = GlobalState.CUSTOMER_QUEUE.size();
        }
        this.type = type;
        this.serviceName = serviceName;
        this.message = message;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Type getType() {
        return type;
    }
}
