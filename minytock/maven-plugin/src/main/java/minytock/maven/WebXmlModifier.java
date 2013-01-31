package minytock.maven;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 * @author reesbyars
 *
 */
public class WebXmlModifier {
	
	private String webAppDir;
	
	private WebXmlModifier(){}
	
	public static WebXmlModifier build(String webAppDir) throws Exception {
		WebXmlModifier modifier = new WebXmlModifier();
		modifier.webAppDir = webAppDir;
		return modifier;
	}
	
	public void execute() throws TransformerFactoryConfigurationError, Exception {
		this.modifyDeploymentDescriptor(this.getUnmodifiedWebXml(webAppDir));
	}
	
	private File getUnmodifiedWebXml(String webAppDir) throws Exception {
		
		File unmodifiedWebXml = new File(webAppDir + "/WEB-INF/unminytocked-web.xml");
		File webXml = new File(webAppDir + "/WEB-INF/web.xml");
		
		if (!unmodifiedWebXml.exists()) {
			
			InputStream is = null;
			OutputStream os = null;
			
			try {
				is = new FileInputStream(webXml);
				os = new FileOutputStream(unmodifiedWebXml);
				IOUtils.copy(is, os);
			} finally {
				IOUtils.closeQuietly(is);
				IOUtils.closeQuietly(os);
			}
			
		} else {
			
			InputStream is = null;
			OutputStream os = null;
			
			try {
				is = new FileInputStream(unmodifiedWebXml);
				os = new FileOutputStream(webXml);
				IOUtils.copy(is, os);
			} finally {
				IOUtils.closeQuietly(is);
				IOUtils.closeQuietly(os);
			}
			
		}
		
		return webXml;
		
	}
	
	
	private void modifyDeploymentDescriptor(File webXml) throws TransformerFactoryConfigurationError, Exception {
		
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
        			paramValue.setTextContent(paramValue.getTextContent() + ", classpath:/minytock/ui/minytock-context.xml");
        			break;
        		}
        	}
        }
        
        Element webApp = (Element) doc.getElementsByTagName("web-app").item(0);
       
        this.addServlet(doc, webApp);
        
        
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		DOMSource source = new DOMSource(doc);
		FileOutputStream os = null;
		try {
			os = new FileOutputStream(webXml);
			StreamResult result = new StreamResult(os);
			transformer.transform(source, result); 
		} catch (Exception e) {
			throw e;
		} finally {
			IOUtils.closeQuietly(os);
		}
	}
	
	private void addServlet(Document doc, Element webApp) {
	
		Element servlet = doc.createElement("servlet");
	    
	    Element servletName = doc.createElement("servlet-name");
	    servletName.setTextContent("minytock");
	    servlet.appendChild(servletName);
	    
	    Element servletClass = doc.createElement("servlet-class");
	    servletClass.setTextContent("org.springframework.web.servlet.DispatcherServlet");
	    servlet.appendChild(servletClass);
	    
	    Element initParam = doc.createElement("init-param");
	    servlet.appendChild(initParam);
	    
	    Element paramName = doc.createElement("param-name");
	    paramName.setTextContent("contextConfigLocation");
	    initParam.appendChild(paramName);
	    
	    Element paramValue = doc.createElement("param-value");
	    paramValue.setTextContent("classpath:/minytock/ui/minytock-servlet.xml");
	    initParam.appendChild(paramValue);
	    
	    Element servletMapping = doc.createElement("servlet-mapping");
	    
	    Element servletNameRef = doc.createElement("servlet-name");
	    servletNameRef.setTextContent("minytock");
	    servletMapping.appendChild(servletNameRef);
	    
	    Element servletUrlPattern = doc.createElement("url-pattern");
	    servletUrlPattern.setTextContent("/minytock/*");
	    servletMapping.appendChild(servletUrlPattern);
    
	    
	    webApp.insertBefore(servletMapping, webApp.getElementsByTagName("servlet").item(0));
	    webApp.insertBefore(servlet, servletMapping);
		
	}

}
