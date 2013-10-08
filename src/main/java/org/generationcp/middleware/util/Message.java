package org.generationcp.middleware.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Daniel Villafuerte

 */
public class Message {
    private String messageKey;

    private String[] messageParams;

    public Message(String messageKey) {
        this.messageKey = messageKey;
    }

    public Message(String messageKey, String... params) {
        this.messageKey = messageKey;
        if (params != null) {
            messageParams = params;
        }
    }

    public String getMessageKey() {
        return messageKey;
    }

    public String[] getMessageParams() {
        return messageParams;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    public void setMessageParams(String[] messageParams) {
        this.messageParams = messageParams;
    }

    public String[] getMessageParams() {
        return messageParams.toArray(new String[messageParams.size()]);
    }
}
