package org.generationcp.middleware.operation.parser;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Daniel Villafuerte

 */
public class WorkbookParserException extends Exception{

    private List<String> messages;

    public WorkbookParserException() {
    }

    public WorkbookParserException(List<String> messages) {
        this.messages = messages;
    }

    public WorkbookParserException(String message) {
        super(message);
    }

    public WorkbookParserException(String message, Throwable cause) {
        super(message, cause);
    }

    public void addMessage(String message) {
        if (messages == null) {
            messages = new LinkedList<String>();
        }

        messages.add(message);
    }

    public List<String> getErrorMessages() {
        return messages;
    }


}
