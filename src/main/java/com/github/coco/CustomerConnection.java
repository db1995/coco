package com.github.coco;

import com.alibaba.fastjson.JSON;
import com.github.coco.pojo.AbstractResponseData;
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
        CUSTOMER_MAP.put(this.id, this);

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
    public void onClose() throws IOException, EncodeException {
        dealCloseAndError();
    }

    @OnError
    @Override
    public void onError(Throwable error, Session session) throws IOException, EncodeException {
        error.printStackTrace();
        if (error instanceof EOFException) {

        } else {
            dealCloseAndError();
        }
    }

    private void dealCloseAndError() throws IOException, EncodeException {
        CustomerConnection cc = CUSTOMER_MAP.get(id);
        if (CUSTOMER_QUEUE.contains(cc)) { // 某顾客离开时正在队列中，通知其之后的所有顾客前进一位
            CUSTOMER_QUEUE.forEach(c -> {
                if (c.getCreateTime() > cc.getCreateTime()) {
                    AbstractResponseData rd = new CustomerResponseData(Type.FORWARD);
                    try {
                        c.session.getBasicRemote().sendText(JSON.toJSONString(rd));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            CUSTOMER_QUEUE.remove(cc);
            AbstractResponseData rd = new CustomerResponseData(Type.FORWARD);
            SERVICE_MAP.forEach((k, s) -> {
                try {
                    s.session.getBasicRemote().sendText(JSON.toJSONString(rd));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } else { // 某顾客离开时正在处于被服务状态，通知其他所有等待队列中的顾客前进一位
            AbstractResponseData rd = new CustomerResponseData(Type.FORWARD);
            CUSTOMER_QUEUE.forEach(c -> {
                try {
                    if (c != null && c.session != null) {
                        c.session.getBasicRemote().sendText(JSON.toJSONString(rd));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            SERVICE_MAP.forEach((k, s) -> {
                try {
                    s.session.getBasicRemote().sendText(JSON.toJSONString(rd));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            // 让客服接入新的客户
            if (CUSTOMER_QUEUE.size() > 0) {
                CustomerConnection connection = CUSTOMER_QUEUE.poll();
                ServiceConnection sc = connection.serviceConnection;
                if (sc != null) {
                    connection.serviceConnection = sc;
                    sc.getCustomerConnectionMap().put(this.id, this);
                    startService(connection, sc);
                }
            }
        }
        CUSTOMER_MAP.remove(id);
        try {
            session.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startService(CustomerConnection cc, ServiceConnection sc) throws IOException, EncodeException {
        sc.session.getBasicRemote().sendObject(new CustomerResponseData(Type.START_SERVICE, serviceConnection.getServiceName(), getWelcome()));
        cc.serviceConnection.session.getBasicRemote().sendObject(new ServiceResponseData(this.id, Type.START_SERVICE));
    }

    public void setServiceConnection(ServiceConnection serviceConnection) {
        this.serviceConnection = serviceConnection;
    }
}