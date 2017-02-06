package com.github.mdjc.chatclient.domain;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.github.mdjc.commons.Arguments;

public abstract class ServerClient {
	protected VoidFunction unavailableServerFunction;
	protected Consumer<String> userLogsInConsumer;
	protected Consumer<String> userLogsOutConsumer;
	protected BiConsumer<String, String> messageConsumer;

	public ServerClient() {
		unavailableServerFunction = () -> {
		};
		userLogsInConsumer = s -> {
		};
		userLogsInConsumer = s -> {
		};
		messageConsumer = (u, m) -> {
		};
	}

	public void setUserLoginConsumer(Consumer<String> userLogsInConsumer) {
		this.userLogsInConsumer = Arguments.checkNull(userLogsInConsumer);
	}

	public void setUserLogoutConsumer(Consumer<String> userLogsInConsumer) {
		this.userLogsOutConsumer = Arguments.checkNull(userLogsInConsumer);
	}

	public void setUnavailableServerFunction(VoidFunction unavailableServerFunction) {
		this.unavailableServerFunction = Arguments.checkNull(unavailableServerFunction);
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
