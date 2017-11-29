package vn.edu.uit.pmcl2015.movie_recommender.configuration

import org.springframework.boot.ExitCodeGenerator
import org.springframework.boot.SpringApplication
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import vn.edu.uit.pmcl2015.movie_recommender.core.use_case.GetMovieCollectionUseCase
import vn.edu.uit.pmcl2015.movie_recommender.core.use_case.GetRecommendationsUseCase
import vn.edu.uit.pmcl2015.movie_recommender.entry_point.EntryPointBootstrap
import vn.edu.uit.pmcl2015.movie_recommender.entry_point.web.AppController
import vn.edu.uit.pmcl2015.movie_recommender.entry_point.web.MovieController
import vn.edu.uit.pmcl2015.movie_recommender.entry_point.web.WebEntryPointBootstrap
import vn.edu.uit.pmcl2015.movie_recommender.entry_point.web.WebEntryPointConfig

@Configuration
open class WebEntryPointConfiguration {
  @Bean
  open fun webEntryPointConfig()
      = WebEntryPointConfig(System.getenv("APP_PORT")?.toInt() ?: throw RuntimeException("Environment variable APP_PORT is not set"),
                            System.getenv("APP_DEBUG")?.toBoolean() ?: throw RuntimeException("Environment variable APP_DEBUG is not set"),
                            System.getenv("APP_EXIT_SECRET_KEY") ?: throw RuntimeException("Environment variable APP_EXIT_SECRET_KEY is not set"))

  @Bean
  open fun webEntryPointBootstrap(applicationContext: ApplicationContext): EntryPointBootstrap = object : WebEntryPointBootstrap() {
    override fun exit(exitCode: Int) {
      SpringApplication.exit(applicationContext, ExitCodeGenerator { exitCode })
    }
  }

  @Bean
  open fun embeddedServletContainerCustomizer(config: WebEntryPointConfig) = EmbeddedServletContainerCustomizer { container ->
    container.setPort(config.appPort)
  }

  @Bean
  open fun appController(config: WebEntryPointConfig,
                         entryPointBootstrap: EntryPointBootstrap)
      = AppController(config, entryPointBootstrap)

  @Bean
  open fun movieController(getMovieCollectionUseCase: GetMovieCollectionUseCase,
                           getRecommendationsUseCase: GetRecommendationsUseCase)
      = MovieController(getMovieCollectionUseCase,
                        getRecommendationsUseCase)
}