package com.github.coco.pojo;

/**
 * @author db1995
 */
public abstract class AbstractResponseData {
    protected String message;
    protected Type type;
    protected int customerInQueue;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCustomerInQueue() {
        return customerInQueue;
    }

    public void setCustomerInQueue(int customerInQueue) {
        this.customerInQueue = customerInQueue;
    }

    public enum Type {
        FORWARD,
        MESSAGE,
        WAIT_SERVICE,
        START_SERVICE,
        CUSTOMER_LEFT,
        SERVICE_DOWN
    }
}
