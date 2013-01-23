package minytock.maven;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

@Mojo(name="minytock", defaultPhase=LifecyclePhase.PREPARE_PACKAGE)
public class MinytockMojo extends AbstractMojo {
	
	@Parameter(property = "minytock.deploy", defaultValue = "false")
	private boolean deploy;
	
	@Parameter(property = "minytock.addJars", defaultValue = "true")
	private boolean addJars;
	
	@Parameter(property = "minytock.version", defaultValue = "unspecified")
	private String version;
	
	@Component
    private MavenProject project;
	
	@Component
    private ArtifactFactory artifactFactory;  
	
    
	public void execute() throws MojoExecutionException {
    	if (deploy) {
    		getLog().info( "executing minytock mojo");
    		
    		this.resolveVersion();
    		this.addJars();
            
            getLog().info( "completed minytock mojo" );
    	} else {
    		getLog().info( "skipping minytock mojo" );
    	}
    }
	
	protected void resolveVersion() {
		if ("unspecified".equals(version)) {
			version = getClass().getPackage().getImplementationVersion();
		}
		getLog().info("minytock version - " + version);
	}
    
	@SuppressWarnings("unchecked")
    protected void addJars() {
		if (addJars) {
			getLog().info( "adding jars..." );
			Artifact artifact = artifactFactory.createArtifact("com.googlecode.minytock", "minytock-core", version, "compile", "jar");
	        this.project.getDependencyArtifacts().add(artifact);
		}
    }
}