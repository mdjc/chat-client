package com.github.mdjc.chatclient.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.function.BiConsumer;

import com.github.mdjc.chatclient.domain.AlreadyLoggedInException;
import com.github.mdjc.chatclient.domain.ServerClient;
import com.github.mdjc.commons.IOUtils;
import com.github.mdjc.commons.Utils;

public class SocketServerClient extends ServerClient {
	private final String ipAddress;
	private final int portNumber;

	private String username;
	private Socket socket;
	private BufferedReader reader;
	private BufferedWriter writer;
	private ReceiveMessagesTask receiveMessageTask;

	public SocketServerClient(String ipAddress, int portNumber) throws Exception {
		this.ipAddress = ipAddress;
		this.portNumber = portNumber;
	}

	@Override
	protected void connect() throws Exception {
		socket = new Socket(ipAddress, portNumber);
		reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
	}

	@Override
	public void logIn(String username) throws Exception {
		connect();
		IOUtils.writeAndFlush(writer, "login:" + username);
		String loginResponse = reader.readLine();

		if (loginResponse.equalsIgnoreCase("AlreadyLoggedIn")) {
			throw new AlreadyLoggedInException();
		}

		if (!loginResponse.equalsIgnoreCase("OK")) {
			throw new RuntimeException(loginResponse);
		}

		this.username = username;
	}

	@Override
	public void close() {
		Utils.execIgnoreException(() -> IOUtils.writeAndFlush(writer, String.format("_logout:%s_", username)));
		this.receiveMessageTask.logout();
		Utils.closeQuietly(writer, reader, socket);
	}

	@Override
	public void send(String message) throws Exception {
		IOUtils.writeAndFlush(writer, message);
	}

	@Override
	public void startReceivingMessages(BiConsumer<String, String> messageConsumer) {
		super.startReceivingMessages(messageConsumer);
		receiveMessageTask = new ReceiveMessagesTask(reader, unavailableServerFunction, userLogsInConsumer,
				userLogsOutConsumer,
				messageConsumer);
		new Thread(receiveMessageTask).start();
	}
}
