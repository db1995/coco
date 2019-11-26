package com.github.coco;

import com.github.coco.pojo.CustomerResponseData;
import com.github.coco.pojo.ServiceResponseData;
import com.github.coco.util.JSONEncoder;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.EOFException;
import java.io.IOException;
import java.util.UUID;

import static com.github.coco.GlobalState.*;
import static com.github.coco.config.GlobalConfig.*;
import static com.github.coco.pojo.AbstractResponseData.Type;

/**
 * WebSocket connection of customer
 *
 * @author db1995
 */
@ServerEndpoint(value = "/customer/{id}", encoders = JSONEncoder.class)
@Component
public class CustomerConnection extends AbstractConnection {
    private ServiceConnection serviceConnection;
    @OnOpen
    @Override
    public synchronized void onOpen(Session session, @PathParam("id") String id) throws IOException, EncodeException {
        // Initial
        this.id = UUID.randomUUID().toString();
        this.session = session;
        session.setMaxIdleTimeout(600000);
        CUSTOMER_MAP.put(id, this);

        if (SERVICE_QUEUE.size() > 0) { // 如果有空闲的客服
            ServiceConnection serviceConnection = SERVICE_QUEUE.element();
            this.serviceConnection = serviceConnection;
            serviceConnection.getCustomerConnectionMap().put(id, this);
            if (serviceConnection.getCustomerConnectionMap().size() == getMaxCustomerPerService()) {
                SERVICE_QUEUE.poll();
            }
            this.session.getBasicRemote().sendObject(new CustomerResponseData(Type.START_SERVICE, serviceConnection.getServiceName(), getWelcome()));
            this.serviceConnection.session.getBasicRemote().sendObject(new ServiceResponseData(this.id, Type.START_SERVICE));
        } else if (SERVICE_MAP.size() == 0) {  // 没有客服在上班
            CUSTOMER_QUEUE.add(this);
            this.session.getBasicRemote().sendObject(new CustomerResponseData(Type.MESSAGE, getUnifiedServiceName(), getAutoReplyAfterWork()));
        } else { // 如果有客服在线，但服务人数已满
            CUSTOMER_QUEUE.add(this);
            this.session.getBasicRemote().sendObject(new CustomerResponseData(Type.WAIT_SERVICE, getUnifiedServiceName(), getWelcome()));
            // 通知所有客服，等待人数+1
            SERVICE_MAP.forEach((k, s) -> {
                try {
                    s.session.getBasicRemote().sendObject(new ServiceResponseData(Type.WAIT_SERVICE));
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (EncodeException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @OnMessage
    @Override
    public void onMessage(Session session, String message) throws IOException, EncodeException {
        this.serviceConnection.session.getBasicRemote().sendObject(new ServiceResponseData(this.id, Type.MESSAGE, message));
    }

    @OnClose
    @Override
    public void onClose() throws IOException {
        dealCloseAndError(session);
    }

    @OnError
    @Override
    public void onError(Throwable error, Session session) throws IOException {
        error.printStackTrace();
        if (error instanceof EOFException) {

        } else {
            dealCloseAndError(session);
        }
    }

    private void dealCloseAndError(Session session) throws IOException {
        /*Customer oldCustomer = CUSTOMER_MAP.get(id);
        if (CUSTOMER_QUEUE.contains(oldCustomer)) {
            CUSTOMER_QUEUE.forEach(c -> {
                if (c.getCreateTime() > oldCustomer.getCreateTime()) {
                    AbstractResponseData rd = new CustomerResponseData(Type.FORWARD);
                    try {
                        c.getSession().getBasicRemote().sendText(JSON.toJSONString(rd));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            CUSTOMER_QUEUE.remove(oldCustomer);
        } else {
            AbstractResponseData rd = new CustomerResponseData(Type.FORWARD);
            CUSTOMER_QUEUE.forEach(c -> {
                try {
                    c.getSession().getBasicRemote().sendText(JSON.toJSONString(rd));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            SERVICE_MAP.forEach((k, s) -> {
                try {
                    s.getSession().getBasicRemote().sendText(JSON.toJSONString(rd));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            // 让客服接入新的客户
            if (CUSTOMER_QUEUE.size() > 0) {
                Customer newCustomer = CUSTOMER_QUEUE.poll();
                Service service = oldCustomer.getService();
                if (service != null) {
                    newCustomer.setService(service);
                    service.getCustomerSet().add(newCustomer);
                    startService(newCustomer, service);
                }
            }
        }
        CUSTOMER_MAP.remove(id);
        try {
            session.close();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    public void setServiceConnection(ServiceConnection serviceConnection) {
        this.serviceConnection = serviceConnection;
    }
}