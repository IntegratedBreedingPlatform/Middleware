package org.generationcp.middleware.exceptions;

import org.generationcp.middleware.util.Message;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Daniel Villafuerte

 */
public class WorkbookParserException extends Exception{

	private static final long serialVersionUID = 1L;

	private List<Message> messages;

    public WorkbookParserException() {
    }

    public WorkbookParserException(List<Message> messages) {
        this.messages = messages;
    }

    public WorkbookParserException(String message) {
        super(message);
    }

    public WorkbookParserException(String message, Throwable cause) {
        super(message, cause);
    }

    public void addMessage(Message message) {
        if (messages == null) {
            messages = new LinkedList<Message>();
        }

        messages.add(message);
    }

    public List<Message> getErrorMessages() {
        return messages;
    }


}
