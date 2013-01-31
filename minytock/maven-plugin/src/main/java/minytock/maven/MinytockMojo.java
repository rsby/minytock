package minytock.maven;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

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
	
	@Parameter
	private List<PropertyModifier> propertyModifiers;
	
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
		} else {
			throw new MinytockMojoException("for projects not of type [ear] or [war], either the earDir or warDir must be specified in the minytock plugin configuration", new NullPointerException());
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
		this.placeArtifact(libDir, "minytock-ui", version);
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
					String webAppDir = baseDir + "/" + bundleFileName;
					WebXmlModifier.build(webAppDir).execute();
					this.placeJSPs(baseDir + "/" + bundleFileName);
				}
			} else {
				throw new MinytockMojoException("for projects of type [ear], at least one bundleFileName must be specified in the minytock plugin configuration", new NullPointerException());
			}
		} else {
			WebXmlModifier.build(baseDir).execute();
			this.placeJSPs(baseDir);
		}
		
		//execute the property modifiers
		for (PropertyModifier modifier : this.propertyModifiers) {
			modifier.execute(baseDir);
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
		
		String dest = destinationDir + "/" + artifact.getArtifactId() + "." + artifact.getType();
		this.getLog().info("copying " + artifact.toString() + " to " + dest);
		
		FileInputStream fis = null;
		FileOutputStream fos = null;
		
		try {
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
	
	protected void placeJSPs(String webAppDir) throws Exception {
		
		InputStream is = null;
		OutputStream os = null;
		
		File destinationDir = new File(webAppDir + "/WEB-INF/minytock");
		destinationDir.mkdir();
		
		try {
			//this approach requires the ui jar on the plugin's classpath
			is = this.getClass().getClassLoader().getResourceAsStream("/WEB-INF/minytock/dashboard.jsp");
			os = new FileOutputStream(new File(webAppDir + "/WEB-INF/minytock/dashboard.jsp"));
			IOUtils.copy(is, os);
		} catch (Exception e) {
			throw e;
		} finally {
			IOUtils.closeQuietly(is);
			IOUtils.closeQuietly(os);
		}
	}
	
}