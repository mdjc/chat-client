package com.github.mdjc.chatclient;

import java.util.Properties;

import com.github.mdjc.chatclient.domain.ServerClient;
import com.github.mdjc.chatclient.gui.LoginWindow;
import com.github.mdjc.chatclient.net.SocketServerClient;
import com.github.mdjc.commons.IOUtils;

public class App {
	public static void main(String[] args) throws Exception {
		Properties properties = IOUtils.loadConfig("app.properties");
		String ipAddress = properties.getProperty("ipaddress");
		int portNumber = Integer.valueOf(properties.getProperty("port.number"));
		
		ServerClient serverClient = new SocketServerClient(ipAddress, portNumber);
		LoginWindow.create(serverClient);
	}
}
