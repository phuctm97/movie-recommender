package vn.edu.uit.pmcl2015.movie_recommender.core.use_case

import vn.edu.uit.pmcl2015.movie_recommender.core.data_provider.MovieRepository
import vn.edu.uit.pmcl2015.movie_recommender.core.data_provider.UnitOfWorkProvider
import vn.edu.uit.pmcl2015.movie_recommender.core.entity.Movie

/*******************************************************************************************************/
/* Exceptions */

/*******************************************************************************************************/
/* Models */

/*******************************************************************************************************/
/* Use case */

class GetMovieCollectionUseCase(private val unitOfWorkProvider: UnitOfWorkProvider,
                                private val movieRepository: MovieRepository) {
  fun getMovieCollection(search: String): List<Movie> {
    unitOfWorkProvider.get().use {
      movieRepository.connect(it)
      return movieRepository.getAllMoviesLike(search)
    }
  }
}