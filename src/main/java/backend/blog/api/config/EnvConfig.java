package backend.blog.api.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class EnvConfig {
    static {
        try {
            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMissing()   // <- don't fail if .env is not there
                    .load();

            dotenv.entries().forEach(entry -> {
                if (System.getenv(entry.getKey()) == null) {
                    System.setProperty(entry.getKey(), entry.getValue());
                }
            });
        } catch (Exception e) {
            // Optionally log but don't break ApplicationContext startup
            System.err.println("EnvConfig: failed to load .env, continuing with system env only: " + e.getMessage());
        }
    }
}