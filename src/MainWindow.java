import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class MainWindow extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenuItem newConfigMenuItem;
	private JComboBox<ComboItem> configPicker;
	private File[] configFiles;
	
	private JLabel ipLabel, usernameLabel, passwordLabel;
	private JTextField ipText, usernameText, passwordText, unitNameText, staticIpText;
	private JButton configButton;
	private JPanel topPanel, centerPanel, bottomPanel;
	
	public MainWindow() {
		super("UBNT Config");
		setLayout(new BorderLayout());
		getContentPane().setBackground(Color.WHITE);
		
		topPanel = new JPanel();
		centerPanel = new JPanel();
		bottomPanel = new JPanel();
		add(topPanel, BorderLayout.NORTH);
		add(centerPanel, BorderLayout.CENTER);
		add(bottomPanel, BorderLayout.SOUTH);
		
		//Create MenuBar
		menuBar = new JMenuBar();
		fileMenu = new JMenu("File");
		newConfigMenuItem = new JMenuItem("New Configuration");
		newConfigMenuItem.addActionListener(newConfig());
		setJMenuBar(menuBar);
		menuBar.add(fileMenu);
		fileMenu.add(newConfigMenuItem);
		
		ipLabel = new JLabel("IP");
		topPanel.add(ipLabel);
		ipText = new JTextField("192.168.1.20");
		ipText.setColumns(10);
		topPanel.add(ipText);
		usernameLabel = new JLabel("Username");
		topPanel.add(usernameLabel);
		usernameText = new JTextField("ubnt");
		usernameText.setColumns(10);
		topPanel.add(usernameText);
		passwordLabel = new JLabel("Password");
		topPanel.add(passwordLabel);
		passwordText = new JTextField("ubnt");
		passwordText.setColumns(10);
		topPanel.add(passwordText);
		
		centerPanel.add(new JLabel("Unit Name: "));
		unitNameText = new JTextField(10);
		centerPanel.add(unitNameText);
		
		centerPanel.add(new JLabel("Static IP: "));
		staticIpText = new JTextField(15);
		centerPanel.add(staticIpText);
		
		configPicker = new JComboBox<ComboItem>();
		populateConfigs();
		centerPanel.add(configPicker);
		configPicker.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				changeConfig(((ComboItem) configPicker.getSelectedItem()).getFile());
			}
		});
		configPicker.setSelectedIndex(0);
		
		configButton = new JButton("Configure");
		configButton.addActionListener(configureUnit());
		bottomPanel.add(configButton);
	}
	
	private ActionListener configureUnit() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				HashMap<String, String> configs = Tools.fileToHashMap(((ComboItem) configPicker.getSelectedItem()).getFile());
				configs.put("resolv.host.1.status", "enabled");
				configs.put("resolv.host.1.name", unitNameText.getText());
				String staticIp = staticIpText.getText();
				configs.put("netconf.1.ip", staticIpText.getText());
				System.out.println(staticIp.substring(0, staticIp.lastIndexOf(".")) + ".1");
//				configs.put("route.1.gateway", staticIpText.getText().substring(0, staticIpText.getText().lastIndexOf(".")) + ".1");
				Tools.WriteTempFile(App.CONFIGS_PATH, configs);
				String tmpConfigPath = App.CONFIGS_PATH + File.separator + "tmp" + File.separator + "tmp.cfg";
				String HOST = ipText.getText();
				String USER = usernameText.getText();
				String PASS = passwordText.getText();
				SSH.copyFile(HOST, USER, PASS, tmpConfigPath, "/tmp/system.cfg");
				SSH.sendCommand(HOST, USER, PASS, "cfgmtd -wp /etc && reboot");
				System.out.println("Done Configuring, Rebooting...");
				boolean rebooting = true;
				String[] ips = HOST.split(".");
				try {
					InetAddress inet = InetAddress.getByName(HOST);
					while (rebooting) {
						if (inet.isReachable(5000)) {
							rebooting = false;
						}
					}
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("Rebooted");
			}
		};
	}

	private ActionListener newConfig() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				NewConfigDialog configDialog = new NewConfigDialog();
				configDialog.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosed(WindowEvent e) {
						configPicker.removeAllItems();
						populateConfigs();
					}
				});
			}
		};
	}
	
	private File[] getFiles() {
		File configFolder = new File(System.getProperty("user.home") + File.separator + ".ubnt_configs");
		return configFolder.listFiles();
	}
	
	private void populateConfigs() {
		configFiles = getFiles();
		for (File file : configFiles) {
			if (!file.isDirectory()) {
				configPicker.addItem(new ComboItem(file.getName(), file));
			}
		}
	}
	
	private void changeConfig(File file) {
		HashMap<String, String> configs = Tools.fileToHashMap(file);
		staticIpText.setText(configs.get("netconf.1.ip"));
	}

}
