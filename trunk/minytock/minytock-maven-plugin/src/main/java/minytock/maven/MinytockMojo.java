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
	
	@Parameter(property = "minytock", defaultValue="false")
	private String executionFlag;
	
	@Component
    private MavenProject project;
	
	@Component
    private ArtifactFactory artifactFactory;  
	
    @SuppressWarnings("unchecked")
	public void execute() throws MojoExecutionException {
    	if ("true".equals(executionFlag)) {
    		getLog().info( "executing minytock mojo" );
    		String version = getClass().getPackage().getImplementationVersion();
    		getLog().info("minytock version - " + version);
            Artifact artifact = artifactFactory.createArtifact("com.googlecode.minytock", "minytock-core", version, "compile", "jar");
            this.project.getDependencyArtifacts().add(artifact);
            getLog().info( "completed minytock mojo" );
    	} else {
    		getLog().info( "skipping minytock mojo" );
    	}
    }
}