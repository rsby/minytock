package minytock.maven;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * 
 * @author reesbyars
 *
 */
@Mojo(name="deploy-ui", defaultPhase=LifecyclePhase.PREPARE_PACKAGE)
public class MinytockUIMojo extends AbstractMinytockMojo {
	
	@Parameter(property = "minytock.autoDeployDirectory")
	private String autoDeployDirectory;
	
	@Component
    private ArtifactFactory artifactFactory; 
	
	@Component
    protected ArtifactResolver artifactResolver;

	@Parameter(property = "project.remoteArtifactRepositories", readonly=true, required=true)
    protected List<?> remoteRepositories;

	@Parameter(property = "localRepository", readonly=true, required=true)
    protected ArtifactRepository localRepository;
    
	public void doMojo() throws Exception {
		
		if (autoDeployDirectory != null) {
			
			Artifact war = artifactFactory.createArtifact("com.googlecode.minytock", "minytock-ui", version, "compile", "jar");

			artifactResolver.resolve(war, remoteRepositories, localRepository);
			
			FileInputStream fis = null;
			fis = new FileInputStream(war.getFile());
			
			File destination = new File(autoDeployDirectory + "/minytock-ui.jar");
			FileOutputStream fos = new FileOutputStream(destination);
			
			IOUtils.copy(fis, fos);
			
			IOUtils.closeQuietly(fis);
			IOUtils.closeQuietly(fos);
			
		}
		
    }
	
}