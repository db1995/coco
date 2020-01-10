package com.github.coco;

import javax.websocket.EncodeException;
import javax.websocket.Session;
import java.io.IOException;
import java.util.Collection;

/**
 * @author db1995
 */
public abstract class AbstractConnection {
    protected String id;
    protected Session session;
    protected long createTime = System.currentTimeMillis();

    /**
     * Invoke when connection open
     *
     * @param session
     * @param token   Authentication token, if any
     * @throws IOException
     */
    protected abstract void onOpen(Session session, String token) throws IOException, EncodeException;

    /**
     * Invoke when receive message
     *
     * @param message Received message
     */
    protected abstract void onMessage(Session session, String message) throws IOException, EncodeException;

    /**
     * Invoke when connection closed
     */
    protected abstract void onClose() throws IOException, EncodeException;

    /**
     * Invoke when connection closed
     */
    protected abstract void onError(Throwable error, Session session) throws IOException, EncodeException;

    /**
     * Send message to the session of this connection
     *
     * @param message The message you want to push
     * @throws IOException
     */
    protected void sendAndReceiveMessage(String message) throws IOException {
        session.getBasicRemote().sendText(message);
    }

    /**
     * Notify some targets
     *
     * @param jsonMessage
     * @param targets     The clients you want to notify
     */
    protected void notifyClients(String jsonMessage, Collection<? extends AbstractConnection>... targets) {
        for (Collection<? extends AbstractConnection> collection : targets) {
            collection.forEach(c -> {
                try {
                    c.sendAndReceiveMessage(jsonMessage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public long getCreateTime() {
        return createTime;
    }
}
