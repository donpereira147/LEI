package pack;


import javax.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;

import static textminer.TextMiner.inicializaDados;

@ApplicationPath("pack")
public class MyRESTServices extends ResourceConfig {

    public MyRESTServices()
    {
    	inicializaDados();
    	packages("pack");
    }
}
