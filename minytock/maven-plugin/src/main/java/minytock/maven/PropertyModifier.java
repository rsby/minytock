package minytock.maven;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

public class PropertyModifier {
	
	private String bundleFileName = "";
	private String path;
	private Map<String, String> properties;
	
	public String getBundleFileName() {
		return bundleFileName;
	}
	public void setBundleFileName(String bundleFileName) {
		this.bundleFileName = bundleFileName;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public Map<String, String> getProperties() {
		return properties;
	}
	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}
	
	public void execute(String baseDir) throws Exception {
		
		Properties props = new Properties();
	    InputStream is = null;
	    OutputStream os = null;
	    
	    try {
	        File f = new File(baseDir + "/" + bundleFileName + path);
	        is = new FileInputStream(f);
	        props.load(is);
	        props.putAll(properties);
	        os = new FileOutputStream(f);
	        props.store(os, "properties edited by minytock");
	    } finally { 
	    	IOUtils.closeQuietly(is);
	    }
	}
	

}
