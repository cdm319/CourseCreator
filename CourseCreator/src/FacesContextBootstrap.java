import javax.faces.context.FacesContext;
import javax.faces.validator.BeanValidator;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.validation.Validation;


public class FacesContextBootstrap implements ServletContextListener {
	public static final String BEANS_VALIDATION_AVAILABILITY_CACHE_KEY = "javax.faces.BEANS_VALIDATION_AVAILABLE";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        FacesContext.getCurrentInstance().getExternalContext().getApplicationMap().remove(BEANS_VALIDATION_AVAILABILITY_CACHE_KEY);
        FacesContext.getCurrentInstance().getExternalContext().getApplicationMap()
                .put(BeanValidator.VALIDATOR_FACTORY_KEY, Validation.buildDefaultValidatorFactory());
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    	
    }
}
