package mx.nmp.mipp.customer.job.engine.batch.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import org.springframework.data.relational.core.dialect.AnsiDialect;
import org.springframework.data.relational.core.dialect.Dialect;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class BatchConfig extends AbstractJdbcConfiguration {

    @Override
    public Dialect jdbcDialect(NamedParameterJdbcOperations operations) {
        return AnsiDialect.INSTANCE;
    }

    @Bean
    @Primary
    public JdbcTemplate batchJdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    @ConfigurationProperties("spring.datasource.batch")
    public DataSourceProperties batchDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "dataSource")
    @Primary
    public DataSource batchDatasource(DataSourceProperties batchDataSourceProperties) {
        return batchDataSourceProperties
                .initializeDataSourceBuilder()
                .build();
    }

    @Bean
    @Primary
    public DataSourceTransactionManager transactionManager(@Qualifier("dataSource") DataSource batchDatasource) {
        return new DataSourceTransactionManager(batchDatasource);
    }

}
