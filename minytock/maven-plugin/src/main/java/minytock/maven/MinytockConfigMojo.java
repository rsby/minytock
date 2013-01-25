package minytock.maven;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * 
 * @author reesbyars
 *
 */
@Mojo(name="minytock-config", defaultPhase=LifecyclePhase.INSTALL)
public class MinytockConfigMojo extends AbstractMinytockMojo {
	
	@Parameter
	private String[] bundleFileNames;
	
	@Parameter(defaultValue="${project.build.directory}/${project.build.finalName}")
	private String explodedWebModuleDir;
    
	public void doMojo() throws Exception {
		
		try {
			getLog().info( "adding minytock-context.xml to the context locations in the web.xml in the exploded directory");
			if (this.bundleFileNames != null && this.bundleFileNames.length > 0) {
				getLog().info( "bundle file name provided in configuration, proceeding with EAR configuration");
				for (String bundleFileName : this.bundleFileNames) {
					this.modifyDeploymentDescriptor(new File(this.explodedWebModuleDir + "/" + bundleFileName + "/WEB-INF/web.xml"));
				}
			} else {
				getLog().info( "no bundle file name provided in configuration, proceeding with WAR configuration.  provide bundle file name in plugin configuration if this is an EAR project.");
				this.modifyDeploymentDescriptor(new File(this.explodedWebModuleDir + "/WEB-INF/web.xml"));
			}
		} catch (Exception e) {
			throw new MojoExecutionException("there was an exception while modifying the web.xml to include the minytock Spring config", e);
		} 
        
        getLog().info( "completed minytock-config mojo" );
    	
    }
	
	protected void modifyDeploymentDescriptor(File webXml) throws SAXException, IOException, ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse(webXml);
        NodeList contextParamNodes = doc.getElementsByTagName("context-param");
        for (int i = 0; i < contextParamNodes.getLength(); i++) {
        	Node contextParam = contextParamNodes.item(i);
        	NodeList paramChildren = contextParam.getChildNodes();
        	for (int ii = 0; ii < paramChildren.getLength(); ii++) {
        		Node paramChild = paramChildren.item(ii);
        		if ("param-name".equals(paramChild.getNodeName()) && "contextConfigLocation".equals(paramChild.getTextContent())) {
        			Node paramValue = paramChildren.item(ii + 2);
        			paramValue.setTextContent(paramValue.getTextContent() + ", classpath:/context/minytock-context.xml");
        			break;
        		}
        	}
        }
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		DOMSource source = new DOMSource(doc);
		FileOutputStream os = new FileOutputStream(webXml);
		StreamResult result = new StreamResult(os);
		transformer.transform(source, result); 
	}
}