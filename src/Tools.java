import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class Tools {
	
	public static HashMap<String, String> fileToHashMap(File file) {
		BufferedReader reader = null;
		HashMap<String, String> configs = new HashMap<>();
		String filePath = file.getAbsolutePath();
		try {
			String line;
			reader = new BufferedReader(new FileReader(filePath));
			while((line = reader.readLine()) != null) {
				String[] pairs = line.split("=");
				String key = pairs[0];
				String value = "";
				if (pairs.length > 1) {
					value = pairs[1];
				}
				configs.put(key, value);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null)reader.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return configs;
	}
	
	public static void WriteFileToUnit(String src, String dst, String host, String username, String password) {
		try {
			JSch jsch = new JSch();
//			Session session = jsch.getSession(username, host);
			Session session = jsch.getSession("ubnt", "192.168.10.1", 22);
//			session.setPassword(password);
			session.setPassword("ubnt");
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();
			session.sendKeepAliveMsg();
			Channel channel = session.openChannel("sftp");
			ChannelSftp channelSftp = (ChannelSftp) channel;
			channelSftp.connect();
			if (channelSftp.isConnected()) {
				System.out.println("Conneted to unit!!!");
			} else {
				System.out.println("Fail to connect to unit! :(");
			}
//			((ChannelSftp) channel).put(src, dst);
		} catch (JSchException e) {
			e.printStackTrace();
		}
//		} catch (SftpException e) {
//			e.printStackTrace();
//		}
 catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void WriteTempFile(String basePath, HashMap<String, String> configs) {
		String tmpPath = basePath + File.separator + "tmp";
		File tmpFolder = new File(tmpPath);
		if (!tmpFolder.exists()) {
			tmpFolder.mkdirs();
		}
		String tmpFilePath = tmpPath + File.separator + "tmp.cfg";
		try {
			FileOutputStream out = new FileOutputStream(tmpFilePath);
			Iterator it = configs.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, String> pair = (Map.Entry<String, String>)it.next();
				String line = pair.getKey() + "=" + pair.getValue();
				out.write(line.getBytes());
				out.write(System.getProperty("line.separator").getBytes());
				it.remove();
			}
			out.close();
			System.out.println("File Created!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
