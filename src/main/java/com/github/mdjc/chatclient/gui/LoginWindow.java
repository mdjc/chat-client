package com.github.mdjc.chatclient.gui;

import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mdjc.chatclient.domain.AlreadyLoggedInException;
import com.github.mdjc.chatclient.domain.ServerClient;

public class LoginWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(LoginWindow.class);

	private final ServerClient client;
	private JTextField usernameField;
	private JButton loginButton;

	private LoginWindow(ServerClient client) {
		this.client = client;
		initComponents();
	}

	private void initComponents() {
		JLabel usernameLabel = new JLabel("Username: ");
		usernameField = new JTextField(20);
		loginButton = new JButton("Login");
		loginButton.addActionListener(this::login);
		resize(loginButton, 16.8f);
		resize(usernameField, 14.8f);
		resize(usernameLabel, 16.8f);

		Panel panel = new Panel();
		panel.add(usernameLabel);
		panel.add(usernameField);
		panel.add(loginButton);

		this.setTitle("Chat - Login");
		this.setSize(350, 200);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.add(panel);
		this.setVisible(true);
	}

	public static void create(ServerClient client) {
		javax.swing.SwingUtilities.invokeLater(() -> new LoginWindow(client));
	}

	private void login(ActionEvent event) {
		try {
			client.logIn(usernameField.getText());
			ChatWindow.create(client, usernameField.getText());
			this.dispose();
		} catch (AlreadyLoggedInException aae) {
			JOptionPane.showMessageDialog(this, "Already Logged in");
		} catch (IOException ioe) {
			JOptionPane.showMessageDialog(this, "Connection cannot be established");
		} catch (Exception e) {
			LOGGER.error("Exception: {} ", e);
			JOptionPane.showMessageDialog(this, "Unexpected error");
		}
	}

	private void resize(JComponent component, float size) {
		component.setFont(component.getFont().deriveFont(size));
	}
}
