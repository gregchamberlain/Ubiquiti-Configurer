import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Properties;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class SSH {
	
	public static void sendCommand(String host, String user, String password, String command) {
		try {
			JSch jsch=new JSch();
//			Session session=jsch.getSession("ubnt", "192.168.10.1" , 22);
//			session.setPassword("ubnt");
			Session session=jsch.getSession(user, host , 22);
			session.setPassword(password);
			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();

			ChannelExec channel=(ChannelExec) session.openChannel("exec");
			BufferedReader in=new BufferedReader(new InputStreamReader(channel.getInputStream()));
//			channel.setCommand("cfgmtd -wp /etc && reboot");
			channel.setCommand(command);
			channel.connect();

			String msg=null;
			while((msg=in.readLine())!=null){
			  System.out.println(msg);
			}

			channel.disconnect();
			session.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSchException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void copyFile(String host, String user, String password, String src, String dest) {
		System.out.println("++++++SCP START");

	    FileInputStream fis=null;
	    try{

//	      String lfile= System.getProperty("user.home") + File.separator + ".ubnt_configs" + File.separator + "Bearish.cfg";
//	      String user="ubnt";
//	    
//	      String host="192.168.10.1";
//	      String rfile="/tmp/system.cfg";
	    	
    	  String lfile= src;
	      String rfile=dest;

	      JSch jsch=new JSch();
	      Session session=jsch.getSession(user, host, 22);
//	      session.setPassword("ubnt");
	      session.setPassword(password);
	    
	      Properties config = new Properties();
	      config.put("StrictHostKeyChecking","no");
	      session.setConfig(config);
	      session.connect();

	      boolean ptimestamp = true;

	      // exec 'scp -t rfile' remotely
	      String command="scp " + (ptimestamp ? "-p" :"") +" -t "+rfile;
	      Channel channel=session.openChannel("exec");
	      ((ChannelExec)channel).setCommand(command);

	      // get I/O streams for remote scp
	      OutputStream out=channel.getOutputStream();
	      InputStream in=channel.getInputStream();

	      channel.connect();

	      if(checkAck(in)!=0){
	    	  System.exit(0);
	      }

	      File _lfile = new File(lfile);

	      if(ptimestamp){
	        command="T "+(_lfile.lastModified()/1000)+" 0";
	        // The access time should be sent here,
	        // but it is not accessible with JavaAPI ;-<
	        command+=(" "+(_lfile.lastModified()/1000)+" 0\n");
	        out.write(command.getBytes()); out.flush();
	        if(checkAck(in)!=0){
	        	System.exit(0);
	        }
	      }

	      // send "C0644 filesize filename", where filename should not include '/'
	      long filesize=_lfile.length();
	      command="C0644 "+filesize+" ";
	      if(lfile.lastIndexOf('/')>0){
	        command+=lfile.substring(lfile.lastIndexOf('/')+1);
	      }
	      else{
	        command+=lfile;
	      }
	      command+="\n";
	      out.write(command.getBytes()); out.flush();
	      if(checkAck(in)!=0){
	    	  System.exit(0);
	      }

	      // send a content of lfile
	      fis=new FileInputStream(lfile);
	      byte[] buf=new byte[1024];
	      while(true){
	        int len=fis.read(buf, 0, buf.length);
	        if(len<=0) break;
	        out.write(buf, 0, len); //out.flush();
	      }
	      fis.close();
	      fis=null;
	      // send '\0'
	      buf[0]=0; out.write(buf, 0, 1); out.flush();
	      if(checkAck(in)!=0){
	    	  System.exit(0);
	      }
	      out.close();

	      channel.disconnect();
	      session.disconnect();
	      System.out.println("++++++SCP END");
//	      System.exit(0);
	    }
	    catch(Exception e){
	      System.out.println(e);
	      try{if(fis!=null)fis.close();}catch(Exception ee){}
	    }
	}
	
	static int checkAck(InputStream in) throws IOException {
		int b=in.read();
		// b may be 0 for success,
		//          1 for error,
		//          2 for fatal error,
		if(b==0) return b;
		if(b==-1) return b;
		
		if(b==1 || b==2){
			StringBuffer sb=new StringBuffer();
			int c;
			do {
				c=in.read();
				sb.append((char)c);
			} while(c!='\n');
			if(b==1){ // error
				System.out.print(sb.toString());
			}
			if(b==2){ // fatal error
				System.out.print(sb.toString());
			}
		}
	    return b;
	  }

}
