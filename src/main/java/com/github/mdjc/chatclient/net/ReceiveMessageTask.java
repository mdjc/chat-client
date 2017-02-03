package com.github.mdjc.chatclient.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReceiveMessageTask implements Runnable {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReceiveMessageTask.class);

	private final BufferedReader reader;
	private final Consumer<String> userLogsInConsumer;
	private final Consumer<String> userLogsOutConsumer;
	private final BiConsumer<String, String> messageConsumer;

	public ReceiveMessageTask(BufferedReader reader, Consumer<String> userLogsInConsumer,
			Consumer<String> userLogsOutConsumer, BiConsumer<String, String> messageConsumer) {
		this.reader = reader;
		this.userLogsInConsumer = userLogsInConsumer;
		this.userLogsOutConsumer = userLogsOutConsumer;
		this.messageConsumer = messageConsumer;
	}

	@Override
	public void run() {
		try {
			String message;
			while ((message = reader.readLine()) != null) {
				String[] messageSplit = message.split(":");

				if (userLogsIn(message)) {
					String user = messageSplit[1];
					userLogsInConsumer.accept(user);
					continue;
				}

				if (userLogsOut(message)) {
					String user = messageSplit[1];
					userLogsOutConsumer.accept(user);
					continue;
				}

				messageConsumer.accept(messageSplit[0], messageSplit[1]);
			}
		} catch (IOException e) {
			LOGGER.error("Exception receiving message from server: {}", e.getMessage());
		}
	}

	private boolean userLogsIn(String message) {
		return message.startsWith("_login_:");
	}

	private boolean userLogsOut(String message) {
		return message.startsWith("_logout_:");
	}
}
