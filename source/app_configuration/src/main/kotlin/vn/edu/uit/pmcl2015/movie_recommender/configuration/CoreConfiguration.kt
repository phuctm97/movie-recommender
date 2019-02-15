package vn.edu.uit.pmcl2015.movie_recommender.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import vn.edu.uit.pmcl2015.movie_recommender.core.data_provider.MovieRepository
import vn.edu.uit.pmcl2015.movie_recommender.core.data_provider.RatingRepository
import vn.edu.uit.pmcl2015.movie_recommender.core.data_provider.UserAccountRepository
import vn.edu.uit.pmcl2015.movie_recommender.core.data_provider.UnitOfWorkProvider
import vn.edu.uit.pmcl2015.movie_recommender.core.use_case.*

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

  @Bean
  open fun updateMovieCollectionUseCase(unitOfWorkProvider: UnitOfWorkProvider,
                                        movieRepository: MovieRepository)
      = UpdateMovieCollectionUseCase(unitOfWorkProvider,
                                     movieRepository)

  @Bean
  open fun getMovieCollectionUseCase(unitOfWorkProvider: UnitOfWorkProvider,
                                     movieRepository: MovieRepository)
      = GetMovieCollectionUseCase(unitOfWorkProvider,
                                  movieRepository)

  @Bean
  open fun updateRatingDataUseCase(unitOfWorkProvider: UnitOfWorkProvider,
                                   ratingRepository: RatingRepository)
      = UpdateRatingDataUseCase(unitOfWorkProvider,
                                ratingRepository)

  @Bean
  open fun getRecommendationsUseCase(unitOfWorkProvider: UnitOfWorkProvider,
                                     ratingRepository: RatingRepository,
                                     movieRepository: MovieRepository)
      = GetRecommendationsUseCase(unitOfWorkProvider,
                                  ratingRepository,
                                  movieRepository)
}