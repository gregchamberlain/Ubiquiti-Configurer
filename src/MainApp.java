import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class MainApp {
	
	public static String CONFIGS_PATH = System.getProperty("user.home") + File.separator + ".ubnt_configs";
	
	public static void main(String args[]) throws IOException	 {
		
//		 InetAddress inet;
//
//	    try {
//			inet = InetAddress.getByAddress(new byte[] { (byte) 192, (byte) 168, 1, 20 });
//			System.out.println(inet.isReachable(5000));
//		} catch (UnknownHostException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		
		File file = new File(CONFIGS_PATH);
		if (!file.exists()) {
			boolean success = false;
			success = file.mkdirs();
			if (success) {
				System.out.println("Configs folder created!");
			} else {
				System.out.println("Error creating configs foler!");
			}
		}
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		MainWindow mainWindow = new MainWindow();
		mainWindow.setSize(450,200);
		mainWindow.pack();
		mainWindow.setLocationRelativeTo(null);
		mainWindow.setVisible(true);
	}
}
