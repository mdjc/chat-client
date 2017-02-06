package com.github.mdjc.chatclient.gui;

import java.awt.Dimension;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mdjc.chatclient.domain.ServerClient;

public class ChatWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(ChatWindow.class);

	private JTextArea messagesArea;
	private JTextField messageField;
	private JButton sendButton;

	private final String username;
	private final ServerClient client;

	private ChatWindow(ServerClient client, String username) {
		this.username = username;
		this.client = client;
		client.setUnavailableServerFunction(this::manageServerUnavailable);
		client.setUserLoginConsumer(this::displayUserLogsIn);
		client.setUserLogoutConsumer(this::displayUserLogsOut);
		client.startReceivingMessages(this::display);
		initComponents();
	}

	public void initComponents() {
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		registerWindowLister();

		JMenuBar menuBar = new JMenuBar();
		JMenu userMenu = new JMenu(username);
		menuBar.add(userMenu);
		JMenuItem logoutMenuItem = new JMenuItem("Logout and Exit");
		logoutMenuItem.addActionListener(this::close);
		userMenu.add(logoutMenuItem);

		messagesArea = new JTextArea();
		messagesArea.setEditable(false);
		messageField = new JTextField(30);
		sendButton = new JButton("Send");
		sendButton.addActionListener(this::sendMessage);

		JScrollPane areaScrollPane = new JScrollPane(messagesArea);
		areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		areaScrollPane.setPreferredSize(new Dimension(430, 275));

		Panel p = new Panel();
		p.add(areaScrollPane);
		p.add(messageField);
		p.add(sendButton);

		this.setSize(450, 420);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("Chat - Logged as: " + username);
		this.setJMenuBar(menuBar);
		this.add(p);
		this.setVisible(true);
	}

	public static void create(ServerClient client, String username) {
		javax.swing.SwingUtilities.invokeLater(() -> new ChatWindow(client, username));
	}

	private void displayUserLogsIn(String username) {
		display(username + " has entered");
	}

	private void displayUserLogsOut(String username) {
		display(username + " has exited");
	}

	private void display(String message, String sender) {
		messagesArea.append(String.format("%s: %s%n", sender, message));
	}

	private void display(String message) {
		messagesArea.append(String.format("%s%n", message));
	}

	private void registerWindowLister() {
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				client.close();
			}
		});
	}

	private void close(ActionEvent ae) {
		try {
			client.close();
			this.dispose();
		} catch (Exception e) {
			LOGGER.error("Exception: {} ", e);
			JOptionPane.showMessageDialog(this, "Unexpected error");
		}
	}

	private void sendMessage(ActionEvent ae) {
		try {
			String message = messageField.getText();
			display(message, username);
			messageField.setText("");
			client.send(message);
		} catch (Exception e) {
			LOGGER.error("Exception: {} ", e);
			JOptionPane.showMessageDialog(this, "Server is unavailable, try log in again");
		}
	}

	public void manageServerUnavailable() {
		JOptionPane.showMessageDialog(this, "Server is unavailable, try log in again");
		LoginWindow.create(client);
		close(null);
	}
}