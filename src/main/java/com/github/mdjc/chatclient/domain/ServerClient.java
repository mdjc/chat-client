package com.github.mdjc.chatclient.domain;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class ServerClient {
	protected Consumer<String> userLogsInConsumer;
	protected Consumer<String> userLogsOutConsumer;
	protected BiConsumer<String, String> messageConsumer;

	public void setUserLoginConsumer(Consumer<String> userLogsInConsumer) {
		if (userLogsInConsumer == null) {
			userLogsInConsumer = s -> {
			};
		}

		this.userLogsInConsumer = userLogsInConsumer;
	}

	public void setUserLogoutConsumer(Consumer<String> userLogsInConsumer) {
		if (userLogsInConsumer == null) {
			userLogsInConsumer = s -> {
			};
		}

		this.userLogsOutConsumer = userLogsInConsumer;
	}

	protected abstract void connect() throws Exception;

	public abstract void logIn(String username) throws Exception;

	public abstract void send(String message) throws Exception;

	public void startReceivingMessages(BiConsumer<String, String> messageConsumer) {
		if (messageConsumer == null) {
			messageConsumer = (u, m) -> {
			};
		}

		this.messageConsumer = messageConsumer;
	}

	public abstract void close();
}
