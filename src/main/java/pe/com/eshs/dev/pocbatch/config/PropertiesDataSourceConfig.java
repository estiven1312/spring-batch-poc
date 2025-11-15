package pe.com.eshs.dev.pocbatch.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@ConfigurationProperties(prefix = "datasource")
@Data
public class PropertiesDataSourceConfig {

  private final Map<String, DataSourceProperties> datasources = new HashMap<>();

  public DataSourceProperties getPropertiesByKey(String key) {
    var result = datasources.get(key);
    if (result == null)
      throw new IllegalArgumentException("Datasource with name " + key + " not found");
    return result;
  }

  @Data
  public static class DataSourceProperties {
    private String url;
    private String username;
    private String password;

    @JsonProperty("driver-class-name")
    private String driverClassName;

    private Integer connectionTimeout = 30000;
    private Integer idleTimeout = 600000;
    private Integer maxLifetime = 1800000;

    @JsonProperty("pool-size")
    private Integer poolSize = 10;
  }


}
