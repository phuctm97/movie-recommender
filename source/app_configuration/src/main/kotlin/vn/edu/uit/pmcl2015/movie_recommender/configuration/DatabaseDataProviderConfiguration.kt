package vn.edu.uit.pmcl2015.movie_recommender.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.jdbc.datasource.DriverManagerDataSource
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import vn.edu.uit.pmcl2015.movie_recommender.core.data_provider.UserAccountRepository
import vn.edu.uit.pmcl2015.movie_recommender.data_provider.database.DatabaseDataProviderConfig
import vn.edu.uit.pmcl2015.movie_recommender.data_provider.database.JpaUserAccountRepository
import vn.edu.uit.pmcl2015.movie_recommender.data_provider.database.JpaUnitOfWorkProvider
import java.util.*
import javax.persistence.EntityManagerFactory
import javax.sql.DataSource

@Configuration
open class DatabaseDataProviderConfiguration {
  @Bean
  open fun databaseDataProviderConfig()
      = DatabaseDataProviderConfig(System.getenv("DATABASE_HOST") ?: throw RuntimeException("Environment variable DATABASE_HOST is not set"),
                                   System.getenv("DATABASE_PORT")?.toInt() ?: throw RuntimeException("Environment variable DATABASE_PORT is not set"),
                                   System.getenv("DATABASE_CATALOG") ?: throw RuntimeException("Environment variable DATABASE_CATALOG is not set"),
                                   System.getenv("DATABASE_SCHEMA_ACTION")?.toLowerCase() ?: throw RuntimeException("Environment variable DATABASE_SCHEMA_ACTION is not set"),
                                   System.getenv("DATABASE_UTF8")?.toBoolean() ?: throw RuntimeException("Environment variable DATABASE_UTF8 is not set"),
                                   System.getenv("DATABASE_USERNAME") ?: throw RuntimeException("Environment variable DATABASE_USERNAME is not set"),
                                   System.getenv("DATABASE_PASSWORD") ?: throw RuntimeException("Environment variable DATABASE_PASSWORD is not set"),
                                   System.getenv("DATABASE_DEBUG")?.toBoolean() ?: throw RuntimeException("Environment variable DATABASE_DEBUG is not set"))

  @Bean
  @Primary
  open fun dataSource(config: DatabaseDataProviderConfig): DataSource {
    val dataSource = DriverManagerDataSource()
    dataSource.setDriverClassName("com.mysql.jdbc.Driver")
    dataSource.url = "jdbc:mysql://${config.host}:${config.port}/${config.catalog}" +
                     "?autoReconnect=true&verifyServerCertificate=false&useSSL=true" +
                     if (config.utf8) "&useUnicode=yes&characterEncoding=UTF-8" else ""
    dataSource.username = config.username
    dataSource.password = config.password
    return dataSource
  }

  @Bean
  open fun entityManagerFactory(config: DatabaseDataProviderConfig,
                                dataSource: DataSource): LocalContainerEntityManagerFactoryBean {
    val entityManagerFactoryBean = LocalContainerEntityManagerFactoryBean()
    entityManagerFactoryBean.dataSource = dataSource
    entityManagerFactoryBean.jpaVendorAdapter = HibernateJpaVendorAdapter()

    val additionalProperties = Properties()

    // configures the used database dialect. This allows Hibernate to create SQL
    // that is optimized for the used database.
    additionalProperties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect")

    // specifies the action that is invoked to the database when
    // the Hibernate SessionFactory is created or closed.
    if (arrayOf("create", "update", "validate", "create-drop").contains(config.schemaAction))
      additionalProperties.put("hibernate.hbm2ddl.auto", config.schemaAction)

    //If the value of this property is true, Hibernate writes all SQL
    //statements to the console.
    additionalProperties.put("hibernate.show_sql", if (config.debug) "true" else "false");

    //If the value of this property is true, Hibernate will format the SQL
    //that is written to the console.
    additionalProperties.put("hibernate.format_sql", if (config.debug) "true" else "false");

    entityManagerFactoryBean.setJpaProperties(additionalProperties)
    return entityManagerFactoryBean
  }

  @Bean
  open fun unitOfWorkProvider(entityManagerFactory: EntityManagerFactory) = JpaUnitOfWorkProvider(entityManagerFactory)

  @Bean
  open fun userAccountRepository(): UserAccountRepository = JpaUserAccountRepository()
}