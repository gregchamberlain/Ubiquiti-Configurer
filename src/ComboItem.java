import java.io.File;

public class ComboItem {
	
	private String key;
	private File file;
	
	public ComboItem(String key, File file) {
		this.key = removeFileExtension(key);
		this.file = file;
	}

	@Override
	public String toString() {
		return key;
	}
	
	public String getKey() {
		return key;
	}
	
	public File getFile() {
		return file;
	}
		
	private String removeFileExtension(String fileName) {
		return fileName.substring(0, fileName.lastIndexOf('.'));
	}
	

}
