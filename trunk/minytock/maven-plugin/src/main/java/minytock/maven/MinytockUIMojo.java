package minytock.maven;

import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * 
 * TODO allow a user to specify additional jars, wars
 * 
 * @author reesbyars
 *
 */
@Mojo(name="deploy-ui", defaultPhase=LifecyclePhase.PREPARE_PACKAGE)
public class MinytockUIMojo extends AbstractMinytockMojo {
	
	@Component
    private ArtifactFactory artifactFactory;  
    
	public void doMojo() throws Exception {
    	
    }
	
}