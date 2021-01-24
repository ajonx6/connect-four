package com.curaxu.game;

import java.awt.EventQueue;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class LoginWindow extends JFrame {
	private static final long serialVersionUID = 1L;

	private JPanel contentPane;

	private JLabel lblUsername;
	private JTextField txtUsername;
	private JLabel lblIPAddress;
	private JTextField txtIPAddress;
	private JLabel lblPort;
	private JTextField txtPort;
	private JButton btnLogin;
	private JCheckBox chckbxHostingGame;

	private boolean hostingGameSelected = false;
	private String oldIPValue = "";

	public LoginWindow() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(200, 300);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		lblUsername = new JLabel("Username");
		lblUsername.setBounds(68, 11, 48, 14);
		contentPane.add(lblUsername);

		txtUsername = new JTextField();
		txtUsername.setBounds(27, 36, 130, 20);
		contentPane.add(txtUsername);
		txtUsername.setColumns(10);

		txtIPAddress = new JTextField();
		txtIPAddress.setColumns(10);
		txtIPAddress.setBounds(27, 92, 130, 20);
		contentPane.add(txtIPAddress);

		lblIPAddress = new JLabel("IP Address");
		lblIPAddress.setBounds(66, 67, 52, 14);
		contentPane.add(lblIPAddress);

		chckbxHostingGame = new JCheckBox("Hosting Game");
		chckbxHostingGame.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				hostingGameSelected = !hostingGameSelected;
				if (hostingGameSelected) {
					lblIPAddress.setEnabled(false);
					oldIPValue = txtIPAddress.getText();
					txtIPAddress.setText("");
					txtIPAddress.setEnabled(false);
				} else {
					lblIPAddress.setEnabled(true);
					txtIPAddress.setEnabled(true);
					txtIPAddress.setText(oldIPValue);
				}
			}
		});
		chckbxHostingGame.setBounds(43, 126, 97, 23);
		contentPane.add(chckbxHostingGame);

		btnLogin = new JButton("Login");
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (txtUsername.getText().equals("") || (txtIPAddress.isEnabled() && txtIPAddress.getText().equals("")) || !txtPort.getText().matches("[0-9]{4}")) return;
				new Game(txtUsername.getText(), txtIPAddress.getText(), Integer.parseInt(txtPort.getText()));
				dispose();
			}
		});
		btnLogin.setBounds(27, 218, 130, 23);
		contentPane.add(btnLogin);

		lblPort = new JLabel("Port");
		lblPort.setBounds(82, 156, 20, 14);
		contentPane.add(lblPort);

		txtPort = new JTextField();
		txtPort.setColumns(10);
		txtPort.setBounds(27, 181, 130, 20);
		contentPane.add(txtPort);
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoginWindow frame = new LoginWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}