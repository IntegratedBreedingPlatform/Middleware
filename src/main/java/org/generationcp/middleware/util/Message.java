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

    private List<String> messageParams;

    public Message(String messageKey) {
        this.messageKey = messageKey;
    }

    public Message(String messageKey, String... params) {
        if (params != null) {
            messageParams = Arrays.asList(params);
        }
    }

    public void addMessageParameter(String parameter) {
        if (messageParams == null) {
            messageParams = new LinkedList<String>();
        }

        messageParams.add(parameter);
    }

    public String getMessageKey() {
        return messageKey;

    }

    public void setMessageParams(List<String> messageParams) {
        this.messageParams = messageParams;
    }
}
