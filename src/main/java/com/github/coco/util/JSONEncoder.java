package com.github.coco.util;

import com.alibaba.fastjson.JSON;
import com.github.coco.pojo.AbstractResponseData;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

/**
 * Encode Object to JSON
 *
 * @author db1995
 */
public class JSONEncoder implements Encoder.Text<AbstractResponseData> {
    @Override
    public String encode(AbstractResponseData responseData) throws EncodeException {
        return JSON.toJSONString(responseData);
    }

    @Override
    public void init(EndpointConfig endpointConfig) {

    }

    @Override
    public void destroy() {

    }
}
