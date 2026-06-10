package com.pkcorporate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.net.URI;

@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
@EnableScheduling
public class PkCorporateApplication {
    public static void main(String[] args) {
        String dbUrl = System.getenv("DB_URL");
        System.out.println("[Startup] DB_URL from environment: " + (dbUrl != null ? "PRESENT (hidden for security)" : "NULL"));
        if (dbUrl != null && dbUrl.startsWith("postgresql://")) {
            try {
                URI uri = new URI(dbUrl);
                String host = uri.getHost();
                int port = uri.getPort();
                String path = uri.getPath();
                String query = uri.getQuery();
                
                String jdbcUrl = "jdbc:postgresql://" + host + (port != -1 ? ":" + port : "") + path;
                if (query != null) {
                    jdbcUrl += "?" + query;
                }
                
                System.out.println("[Startup] Parsed JDBC URL: " + jdbcUrl);
                System.setProperty("spring.datasource.url", jdbcUrl);
                
                String userInfo = uri.getUserInfo();
                if (userInfo != null) {
                    String[] parts = userInfo.split(":", 2);
                    String username = parts[0];
                    String password = parts.length > 1 ? parts[1] : "";
                    
                    System.setProperty("spring.datasource.username", username);
                    System.setProperty("spring.datasource.password", password);
                    System.out.println("[Startup] Extracted datasource credentials successfully");
                }
            } catch (Exception e) {
                System.err.println("[Startup] Failed to parse DB_URL into JDBC format: " + e.getMessage());
            }
        }
        try {
            SpringApplication.run(PkCorporateApplication.class, args);
        } catch (Exception e) {
            System.err.println("[Startup] CRITICAL LAUNCH FAILURE:");
            e.printStackTrace();
            throw e;
        }
    }
}
