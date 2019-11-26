package com.github.coco;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.coco.pojo.CustomerResponseData;
import com.github.coco.pojo.ServiceResponseData;
import com.github.coco.util.JSONEncoder;
import com.github.coco.util.JwtUtil;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.EOFException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.github.coco.GlobalState.*;
import static com.github.coco.config.GlobalConfig.getWelcome;
import static com.github.coco.pojo.AbstractResponseData.Type;

/**
 * WebSocket connection of service
 *
 * @author db1995
 */
@ServerEndpoint(value = "/service/{token}", encoders = JSONEncoder.class)
@Component
public class ServiceConnection extends AbstractConnection {
    private String serviceName;
    private Map<String, CustomerConnection> customerConnectionMap = new HashMap<>();

    @OnOpen
    @Override
    public synchronized void onOpen(Session session, @PathParam("token") String token) throws IOException, EncodeException {
        // Check identity
        if (!JwtUtil.checkToken(token)) {
            session.close();
            return;
        }

        // Initial
        this.session = session;
        this.id = UUID.randomUUID().toString();
        session.setMaxIdleTimeout(600000);
        this.serviceName = JwtUtil.getClaims().getBody().getSubject();
        SERVICE_MAP.put(this.id, this);

        // Connect to a customer if exist a waiting custom
        while (CUSTOMER_QUEUE.size() > 0) {
            CustomerConnection customerConnection = CUSTOMER_QUEUE.poll();
            customerConnection.setServiceConnection(this);
            this.customerConnectionMap.put(customerConnection.id, customerConnection);
            customerConnection.session.getBasicRemote().sendObject(new CustomerResponseData(Type.START_SERVICE, serviceName, getWelcome()));
            this.session.getBasicRemote().sendObject(new ServiceResponseData(customerConnection.id, Type.START_SERVICE));
            CUSTOMER_QUEUE.forEach(c -> {
                try {
                    c.session.getBasicRemote().sendObject(new CustomerResponseData(Type.FORWARD));
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (EncodeException e) {
                    e.printStackTrace();
                }
            });
            SERVICE_MAP.forEach((k, s) -> {
                try {
                    s.session.getBasicRemote().sendObject(new ServiceResponseData(Type.FORWARD));
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (EncodeException e) {
                    e.printStackTrace();
                }
            });
        }
        SERVICE_QUEUE.add(this);
    }

    @OnMessage
    @Override
    public void onMessage(Session session, String jsonMessage) throws IOException, EncodeException {
        JSONObject jsonObject = JSON.parseObject(jsonMessage);
        String cid = (String) jsonObject.get("customerId");
        String msg = (String) jsonObject.get("message");
        this.customerConnectionMap.get(cid)
                .session.getBasicRemote().sendObject(new CustomerResponseData(Type.MESSAGE, msg));
    }

    @OnClose
    @Override
    public void onClose() {

    }

    @OnError
    @Override
    public void onError(Throwable error, Session session) {
        error.printStackTrace();
        if (error instanceof EOFException) {

        } else {
            dealCloseAndError(session);
        }
    }

    private void dealCloseAndError(Session session) {
        /*String sessionId = session.getId();
        // 告知所有正在服务的客户，客服已掉线
        SERVICE_MAP.get(sessionId).getCustomerSet().forEach(c -> {
            AbstractResponseData rd = new CustomerResponseData(Type.SERVICE_DOWN);
            try {
                c.getSession().getBasicRemote().sendText(JSON.toJSONString(rd));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        SERVICE_MAP.remove(sessionId);
        SERVICE_QUEUE.remove(SERVICE_MAP.get(sessionId));
        try {
            session.close();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    public Map<String, CustomerConnection> getCustomerConnectionMap() {
        return customerConnectionMap;
    }

    public String getServiceName() {
        return serviceName;
    }
}