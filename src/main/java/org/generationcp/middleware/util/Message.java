
package org.generationcp.middleware.util;

/**
 * Created by IntelliJ IDEA. User: Daniel Villafuerte
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
			this.messageParams = params;
		}
	}

	public String getMessageKey() {
		return this.messageKey;
	}

	public String[] getMessageParams() {
		return this.messageParams;
	}

	public void setMessageKey(String messageKey) {
		this.messageKey = messageKey;
	}

	public void setMessageParams(String[] messageParams) {
		this.messageParams = messageParams;
	}
}
