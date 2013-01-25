package minytock.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;

public abstract class AbstractMinytockMojo extends AbstractMojo {
	
	@Parameter(property = "minytock.enabled", defaultValue = "false")
	private boolean enabled;
	
	@Parameter(property = "minytock.version")
	protected String version;
	
	public AbstractMinytockMojo() {
		version = getClass().getPackage().getImplementationVersion();
	}
	
	public final void execute() throws MojoExecutionException {
		
    	if (enabled) {
    		try {
				this.doMojo();
			} catch (Exception e) {
				throw new MojoExecutionException("Error while performing minytock mojo", e);
			}
    	}
	}
	
	protected abstract void doMojo() throws Exception;
	
}
