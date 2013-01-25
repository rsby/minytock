package minytock.maven;

import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.model.Dependency;
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
@Mojo(name="minytock-jars", defaultPhase=LifecyclePhase.PREPARE_PACKAGE)
public class MinytockJarMojo extends AbstractMinytockMojo {
	
	@Parameter(property = "minytock.dependencies")
	private Dependency[] dependencies;
	
	@Component
    private MavenProject project;
	
	@Component
    private ArtifactFactory artifactFactory;  
    
	public void doMojo() throws Exception {
		
		this.addJars();
		this.addAdditionalJars();
		
        getLog().info( "completed minytock-jars mojo" );
    	
    }
    
	@SuppressWarnings("unchecked")
    protected void addJars() {
		getLog().info( "adding jars..." );
		Set<Artifact> artifacts = this.project.getDependencyArtifacts();
		artifacts.add(getArtifact("minytock-core"));
		artifacts.add(getArtifact("minytock-spring"));
    }
	
	@SuppressWarnings("unchecked")
    protected void addAdditionalJars() {
		if (dependencies != null) {
			getLog().info( "adding additional jars..." );
			Set<Artifact> artifacts = this.project.getDependencyArtifacts();
			for (Dependency dependency : this.dependencies) {
				getLog().info("adding " + dependency.getArtifactId());
				artifacts.add(getArtifact(dependency));
			}
		}
    }
	
	protected Artifact getArtifact(String id) {
		return artifactFactory.createArtifact("com.googlecode.minytock", id, version, "compile", "jar");
	}
	
	protected Artifact getArtifact(Dependency dependency) {
		return artifactFactory.createArtifact(dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion(), dependency.getScope(), dependency.getType());
	}
	
}