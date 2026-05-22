package configuración;

import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Obtenemos la ruta absoluta de la carpeta de videos
        String reportPath = System.getProperty("user.dir") + "/src/main/resources/static/videos/";

        // Esto le dice a Spring: "Cuando alguien pida /videos/**, búscalo en esta carpeta"
        registry.addResourceHandler("/videos/**")
                .addResourceLocations("file:" + reportPath + "/");
    }
}
