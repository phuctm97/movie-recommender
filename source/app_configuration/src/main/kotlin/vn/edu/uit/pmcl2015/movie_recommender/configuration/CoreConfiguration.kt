package vn.edu.uit.pmcl2015.movie_recommender.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import vn.edu.uit.pmcl2015.movie_recommender.core.data_provider.UserAccountRepository
import vn.edu.uit.pmcl2015.movie_recommender.core.data_provider.UnitOfWorkProvider
import vn.edu.uit.pmcl2015.movie_recommender.core.use_case.SignInUseCase
import vn.edu.uit.pmcl2015.movie_recommender.core.use_case.SignUpUseCase

@Configuration
open class CoreConfiguration {
  @Bean
  open fun signUpUseCase(unitOfWorkProvider: UnitOfWorkProvider,
                         userAccountRepository: UserAccountRepository)
      = SignUpUseCase(unitOfWorkProvider,
                      userAccountRepository)

  @Bean
  open fun signInUseCase(unitOfWorkProvider: UnitOfWorkProvider,
                         userAccountRepository: UserAccountRepository)
      = SignInUseCase(unitOfWorkProvider,
                      userAccountRepository)
}