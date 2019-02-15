package vn.edu.uit.pmcl2015.movie_recommender.core.use_case

import vn.edu.uit.pmcl2015.movie_recommender.core.data_provider.MovieRepository
import vn.edu.uit.pmcl2015.movie_recommender.core.data_provider.UnitOfWorkProvider
import vn.edu.uit.pmcl2015.movie_recommender.core.entity.InvalidArgumentException
import vn.edu.uit.pmcl2015.movie_recommender.core.entity.Movie

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

  fun getMovie(id: Int): Movie {
    unitOfWorkProvider.get().use {
      movieRepository.connect(it)
      return movieRepository.getMovie(id) ?: throw InvalidArgumentException("Movie with id $id does not exist")
    }
  }
}