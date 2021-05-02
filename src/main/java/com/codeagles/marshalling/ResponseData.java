package com.codeagles.marshalling;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Codeagles
 * Date: 2021/5/2
 * Time: 下午9:34
 * <p>
 * Description:
 */
public class ResponseData implements Serializable {
    private static final long serialVersionUID = 7359175860641122157L;

    private String id;

    private String name;

    private String responseMessage;

    private byte[] attachment;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public byte[] getAttachment() {
        return attachment;
    }

    public void setAttachment(byte[] attachment) {
        this.attachment = attachment;
    }
}
