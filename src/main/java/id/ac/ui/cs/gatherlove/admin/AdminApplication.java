package id.ac.ui.cs.gatherlove.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import id.ac.ui.cs.gatherlove.admin.config.JwtProperties;
import io.github.cdimascio.dotenv.Dotenv;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import java.sql.Connection;
import java.sql.SQLException;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class    AdminApplication {
    @Autowired
    private DataSource dataSource;

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        System.setProperty("DB_HOST", dotenv.get("DB_HOST"));
        System.setProperty("DB_PORT", dotenv.get("DB_PORT"));
        System.setProperty("DB_NAME", dotenv.get("DB_NAME"));
        System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
        System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
        System.setProperty("APP_JWT_SECRET", dotenv.get("APP_JWT_SECRET"));
        System.setProperty("APP_JWT_ISSUER", dotenv.get("APP_JWT_ISSUER"));
        System.setProperty("APP_JWT_EXPIRES_IN", dotenv.get("APP_JWT_EXPIRES_IN"));
        System.setProperty("APP_JWT_ALGORITHM", dotenv.get("APP_JWT_ALGORITHM"));

        SpringApplication.run(AdminApplication.class, args);
    }
}
