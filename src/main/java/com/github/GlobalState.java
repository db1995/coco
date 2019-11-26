package com.github.coco;

import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author db1995
 */
public final class GlobalState {
    public static final Queue<CustomerConnection> CUSTOMER_QUEUE = new ConcurrentLinkedQueue<>();
    public static final Queue<ServiceConnection> SERVICE_QUEUE = new ConcurrentLinkedQueue<>();

    /**
     * Map<id, Customer>
     */
    public static final Map<String, CustomerConnection> CUSTOMER_MAP = new ConcurrentHashMap<>();
    /**
     * Map<service_id, Service>
     */
    public static final Map<String, ServiceConnection> SERVICE_MAP = new ConcurrentHashMap<>();

    private GlobalState() {
    }
}
