import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

public class NewConfigDialog extends JDialog{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JFileChooser fileChooser;
	private JButton chooserButton, saveButton;
	private FileNameExtensionFilter filter;
	private JTextField nameText;
//	private JDialog dialog;
	
	public NewConfigDialog() {
		setTitle("New Configuration");
		setContentPane(getPanel());
		pack();
		setLocationRelativeTo(null);
		setModal(true);
		setVisible(true);
	}
	
	public void displayGUI() {
//		dialog = new JDialog();
		setTitle("New Configuration");
		setContentPane(getPanel());
		pack();
		setLocationRelativeTo(null);
		setModal(true);
		setVisible(true);
    }

    private JPanel getPanel() {
    	
    	// Create FileChooser and set filter
    	fileChooser = new JFileChooser("Browse...");
    	filter = new FileNameExtensionFilter("TEXT FILES", "cfg");
    	fileChooser.setFileFilter(filter);
    	
    	// Create Main Panel
    	JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
    	
    	// Setup Form Panel
        mainPanel.add(setupFormPanel(), BorderLayout.CENTER);
        
        // Create Bottom Panel
        JPanel bottomPanel = new JPanel();
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        // Setup save button
        saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveConfig();
			}
        });
        bottomPanel.add(saveButton);

        return mainPanel;
    }
    
    private void chooseFile() {
    	switch(fileChooser.showOpenDialog(null)) {
    	case JFileChooser.APPROVE_OPTION:
    		String fileName = fileChooser.getSelectedFile().getName();
    		chooserButton.setText(fileName);
    		if (nameText.getText().equals("")) {
    			nameText.setText(fileName.substring(0, fileName.lastIndexOf('.')));
    		}
    		break;
    	case JFileChooser.ERROR_OPTION:
    		JOptionPane.showMessageDialog(null, "Error Selecting file, please try again.");
    		break;
    	}
    }
    
    private void saveConfig() {
    	if (nameText.getText().equals("")) {
    		JOptionPane.showMessageDialog(null, "Please Enter Configuration Name.", "Error", JOptionPane.WARNING_MESSAGE);
    	} else if (fileChooser.getSelectedFile() == null) {
    		JOptionPane.showMessageDialog(null, "Please Select Configuration File.", "Error", JOptionPane.WARNING_MESSAGE);
    	} else {
    		String filePath = fileChooser.getSelectedFile().getAbsolutePath();
    		FileInputStream inStream = null;
    		FileOutputStream outStream = null;
    		try {
    			File source = new File(filePath);
    			File dest = new File(App.CONFIGS_PATH + File.separator + nameText.getText() + ".cfg");
    			inStream = new FileInputStream(source);
    			outStream = new FileOutputStream(dest);
    			
    			byte[] buffer = new byte[1024];
    			
    			int length;
    			while((length = inStream.read(buffer)) > 0) {
    				outStream.write(buffer, 0, length);
    			}
    			
    			if (inStream != null)inStream.close();
                if (outStream != null)outStream.close();
                System.out.println("File Copied..");
    		} catch(IOException e){
                e.printStackTrace();
            }
    		dispose();
    	}
    }
    
    private JPanel setupFormPanel() {
    	JPanel panel = new JPanel();
    	panel.setLayout(new GridBagLayout());
    	GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(5,5,5,5);
        c.anchor = GridBagConstraints.LINE_END;
        panel.add(new JLabel("Configuration Name: "), c);
        c.gridy ++;
        panel.add(new JLabel("Configuration File: "), c);
        c.gridx = 1;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_START;
        nameText = new JTextField(15);
        panel.add(nameText, c);
        c.gridy ++;
        chooserButton = new JButton("Browse...");
        chooserButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				chooseFile();
			}
        });
        panel.add(chooserButton, c);
    	return panel;
    }
    
    
}
