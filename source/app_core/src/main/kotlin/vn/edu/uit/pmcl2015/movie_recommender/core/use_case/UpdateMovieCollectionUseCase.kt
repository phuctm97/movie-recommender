package vn.edu.uit.pmcl2015.movie_recommender.core.use_case

import org.apache.commons.csv.CSVFormat
import vn.edu.uit.pmcl2015.movie_recommender.core.data_provider.MovieRepository
import vn.edu.uit.pmcl2015.movie_recommender.core.data_provider.UnitOfWorkProvider
import vn.edu.uit.pmcl2015.movie_recommender.core.entity.InvalidArgumentException
import vn.edu.uit.pmcl2015.movie_recommender.core.entity.Movie
import vn.edu.uit.pmcl2015.movie_recommender.core.updateMovieCollectionSecretKey
import java.io.StringReader

/*******************************************************************************************************/
/* Exceptions */

/*******************************************************************************************************/
/* Models */

/*******************************************************************************************************/
/* Use case */

class UpdateMovieCollectionUseCase(private val unitOfWorkProvider: UnitOfWorkProvider,
                                   private val movieRepository: MovieRepository) {
  fun updateMovieCollectionByCsv(secretKey: String, csvContent: String) {
    if (secretKey != updateMovieCollectionSecretKey()) throw InvalidArgumentException("Invalid secret key")
    val records = CSVFormat.EXCEL.withHeader().parse(StringReader(csvContent))

    unitOfWorkProvider.get().use {
      movieRepository.connect(it)

      it.flush {
        movieRepository.deleteAllMovies()

        for (record in records) {
          val id = record["movieId"].toInt()
          val title = record["title"] ?: throw InvalidArgumentException("Invalid content")
          val newMovie = Movie()
          newMovie.id = id
          newMovie.title = title
          movieRepository.save(newMovie)
        }
      }
    }
  }
}