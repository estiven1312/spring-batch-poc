package pe.com.eshs.dev.pocbatch.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.batch.BatchDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.support.JdbcTransactionManager;

@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
public class DataSourceConfig {
    private final PropertiesDataSourceConfig propertiesConfig;

    @Bean
    @Primary
    @BatchDataSource
    public HikariDataSource originDataSource() {
        PropertiesDataSourceConfig.DataSourceProperties props = propertiesConfig.getPropertiesByKey("origin");
        return buildDataSourceProperties(props);
    }

    @Bean
    @Qualifier("targetDataSource")
    public HikariDataSource targetDataSource() {
        PropertiesDataSourceConfig.DataSourceProperties props = propertiesConfig.getPropertiesByKey("target");
        return buildDataSourceProperties(props);
    }

    @Bean
    @Primary
    public JdbcTransactionManager originTransactionManager(HikariDataSource originDataSource) {
        return new JdbcTransactionManager(originDataSource);
    }

    @Bean
    public JdbcTransactionManager targetTransactionManager(@Qualifier("targetDataSource") HikariDataSource targetDataSource) {
        return new JdbcTransactionManager(targetDataSource);
    }

    private static HikariDataSource buildDataSourceProperties(PropertiesDataSourceConfig.DataSourceProperties props) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(props.getUrl());
        dataSource.setUsername(props.getUsername());
        dataSource.setPassword(props.getPassword());
        dataSource.setDriverClassName(props.getDriverClassName());
        dataSource.setMaximumPoolSize(props.getPoolSize());
        dataSource.setConnectionTimeout(props.getConnectionTimeout());
        dataSource.setIdleTimeout(props.getIdleTimeout());
        dataSource.setMaxLifetime(props.getMaxLifetime());
        return dataSource;
    }
}
