package minytock.maven;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.IOUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 * @author reesbyars
 *
 */
@Mojo(name = "minytock", defaultPhase = LifecyclePhase.INSTALL)
public class MinytockMojo extends AbstractMojo {
	
	@Parameter(property = "minytock.deployThisJar", defaultValue = "true")
	private boolean deployThisJar;
	
	@Parameter(property = "minytock.version")
	protected String version = getClass().getPackage().getImplementationVersion();
	
	@Parameter(property = "minytock.autoDeployDirectory")
	private String autoDeployDirectory;
	
	@Parameter(property = "minytock.dependencies")
	private Dependency[] dependencies;

	@Parameter(property = "project.remoteArtifactRepositories", readonly=true, required=true)
    protected List<?> remoteRepositories;

	@Parameter(property = "localRepository", readonly=true, required=true)
    protected ArtifactRepository localRepository;
	
	@Parameter(property = "minytock.earDir")
    protected String earDir;
	
	@Parameter(property = "minytock.earDir")
    protected String warDir;
	
	@Parameter(defaultValue="${project.build.directory}/${project.build.finalName}")
	private String explodedDir;
	
	@Parameter
	private String[] bundleFileNames;
	
	@Component
    private MavenProject project;
	
	@Component
    private ArtifactFactory artifactFactory; 
	
	@Component
    protected ArtifactResolver artifactResolver;
	
	public final void execute() throws MojoExecutionException {
		try {
			this.doMojo();
		} catch (Exception e) {
			throw new MojoExecutionException("error while performing minytock mojo", e);
		}
	}
    
	public void doMojo() throws Exception {
		
		//deploy the war
		if (autoDeployDirectory != null) {
			this.getLog().info("deploying the minytock war");
			Artifact war = artifactFactory.createArtifact("com.googlecode.minytock", "minytock-ui", version, "compile", "jar");
			this.placeArtifact(autoDeployDirectory, war);
		} else {
			throw new MinytockMojoException("the autoDeployDirectory must be specified in the minytock plugin configuration", new NullPointerException());
		}
		
		//establish the app type and base dir
		String appType = project.getPackaging();
		String baseDir = null;
		if (this.earDir != null) {
			baseDir = this.earDir;
			appType = "ear";
		} else if (this.warDir != null) {
			baseDir = this.warDir;
			appType = "war";
		} else if ("ear".equals(appType) || "war".equals(appType)) {
			baseDir = this.explodedDir;
		} else if ("jar".equals(appType)){
			throw new MinytockMojoException("for projects of type [jar], either the earDir or warDir must be specified in the minytock plugin configuration", new NullPointerException());
		} else {
			throw new MinytockMojoException("project must be of type ear, war, or jar when executing the minytock mojo");
		}
		
		//establish the lib dir
		String libDir = null;
		if ("ear".equals(appType)) {
			libDir = baseDir + "/APP-INF/lib";
		} else {
			libDir = baseDir + "/WEB-INF/lib";
		}
		
		//add the minytock jars to the exploded lib dir
		this.getLog().info("adding the minytock jars");
		this.placeArtifact(libDir, "minytock-core", version);
		this.placeArtifact(libDir, "minytock-spring", version);
		
		//add any add'l dependencies specified by the plugin config - if the plugin is declared in a jar pom, then that jar is deployed by default
		this.getLog().info("adding additional jars");
		if (this.deployThisJar && "jar".equals(this.project.getPackaging())) {
			this.placeArtifact(libDir, this.project.getArtifact());
		}
		if (dependencies != null) {
			for (Dependency dependency : this.dependencies) {
				this.placeArtifact(libDir, dependency);
			}
		}
		
		//edit the web.xml to include the minytock-context.xml in its contextConfigLocation param
		this.getLog().info("modifying the web.xml(s)");
		if ("ear".equals(appType)) {
			if (this.bundleFileNames != null && this.bundleFileNames.length > 0) {
				for (String bundleFileName : this.bundleFileNames) {
					this.modifyDeploymentDescriptor(new File(baseDir + "/" + bundleFileName + "/WEB-INF/web.xml"));
				}
			} else {
				throw new MinytockMojoException("for projects of type [ear], at least one bundleFileName must be specified in the minytock plugin configuration", new NullPointerException());
			}
		} else {
			this.modifyDeploymentDescriptor(new File(baseDir + "/WEB-INF/web.xml"));
		}
		
    }
	
	protected void placeArtifact(String destinationDir, String minytockArtifactId, String version) throws Exception {
		this.placeArtifact(destinationDir, artifactFactory.createArtifact("com.googlecode.minytock", minytockArtifactId, version, "compile", "jar"));
	}
	
	protected void placeArtifact(String destinationDir, Dependency dependency) throws Exception {
		this.placeArtifact(destinationDir, artifactFactory
				.createArtifact(dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion(), dependency.getScope(), dependency.getType()));
	}
	
	protected void placeArtifact(String destinationDir, Artifact artifact) throws Exception {
		
		artifactResolver.resolve(artifact, remoteRepositories, localRepository);
		
		FileInputStream fis = null;
		FileOutputStream fos = null;
		
		try {
			String dest = destinationDir + "/" + artifact.getArtifactId() + "." + artifact.getType();
			this.getLog().info("copying " + artifact.toString() + " to " + dest);
			fis = new FileInputStream(artifact.getFile());
			File destination = new File(dest);
			fos = new FileOutputStream(destination);
			IOUtils.copy(fis, fos);
		} catch (Exception e) {
			throw e;
		} finally {
			IOUtils.closeQuietly(fis);
			IOUtils.closeQuietly(fos);
		}
		
	}
	
	protected void modifyDeploymentDescriptor(File webXml) throws TransformerFactoryConfigurationError, Exception {
		this.getLog().info("editing the web.xml at " + webXml.getPath());
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
	
}