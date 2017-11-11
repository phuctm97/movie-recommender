package vn.edu.uit.pmcl2015.movie_recommender.configuration

import org.springframework.boot.ExitCodeGenerator
import org.springframework.boot.SpringApplication
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import vn.edu.uit.pmcl2015.movie_recommender.core.use_case.SignInUseCase
import vn.edu.uit.pmcl2015.movie_recommender.core.use_case.SignUpUseCase
import vn.edu.uit.pmcl2015.movie_recommender.entry_point.EntryPointBootstrap
import vn.edu.uit.pmcl2015.movie_recommender.entry_point.rest.*

@Configuration
open class RestEntryPointConfiguration {
  @Bean
  open fun restEntryPointConfig()
      = RestEntryPointConfig(System.getenv("APP_PORT")?.toInt() ?: throw RuntimeException("Environment variable APP_PORT is not set"),
                             System.getenv("APP_DEBUG")?.toBoolean() ?: throw RuntimeException("Environment variable APP_DEBUG is not set"),
                             System.getenv("APP_EXIT_SECRET_KEY") ?: throw RuntimeException("Environment variable APP_EXIT_SECRET_KEY is not set"))

  @Bean
  open fun restEntryPointBootstrap(applicationContext: ApplicationContext): EntryPointBootstrap = object : RestEntryPointBootstrap() {
    override fun exit(exitCode: Int) {
      SpringApplication.exit(applicationContext, ExitCodeGenerator { exitCode })
    }
  }

  @Bean
  open fun restResponseExceptionHandler() = RestResponseExceptionHandler();

  @Bean
  open fun embeddedServletContainerCustomizer(config: RestEntryPointConfig) = EmbeddedServletContainerCustomizer { container ->
    container.setPort(config.appPort)
  }

  @Bean
  open fun testController()
      = TestController()

  @Bean
  open fun appController(restEntryPointConfig: RestEntryPointConfig,
                         entryPointBootstrap: EntryPointBootstrap)
      = AppController(restEntryPointConfig, entryPointBootstrap)

  @Bean
  open fun doctorAccountController(signUpUseCase: SignUpUseCase)
      = UserAccountController(signUpUseCase)

  @Bean
  open fun doctorAccountSessionController(signInUseCase: SignInUseCase)
      = UserAccountSessionController(signInUseCase)
}