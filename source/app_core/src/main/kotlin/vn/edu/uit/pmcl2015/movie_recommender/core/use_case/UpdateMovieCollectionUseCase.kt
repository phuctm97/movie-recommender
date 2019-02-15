package vn.edu.uit.pmcl2015.movie_recommender.core.use_case

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.httpGet
import com.google.gson.Gson
import org.apache.commons.csv.CSVFormat
import vn.edu.uit.pmcl2015.movie_recommender.core.data_provider.MovieRepository
import vn.edu.uit.pmcl2015.movie_recommender.core.data_provider.UnitOfWorkProvider
import vn.edu.uit.pmcl2015.movie_recommender.core.entity.InvalidArgumentException
import vn.edu.uit.pmcl2015.movie_recommender.core.entity.Movie
import vn.edu.uit.pmcl2015.movie_recommender.core.updateMovieCollectionSecretKey
import java.io.StringReader

/*******************************************************************************************************/
/* Models */

data class TmdbModel(val backdrop_path: String = "",
                     val poster_path: String = "") {
  class Deserializer : ResponseDeserializable<TmdbModel> {
    override fun deserialize(content: String) = Gson().fromJson(content, TmdbModel::class.java)
  }
}

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

  fun updateMovieCollectionImagesByCsv(secretKey: String, csvContent: String) {
    if (secretKey != updateMovieCollectionSecretKey()) throw InvalidArgumentException("Invalid secret key")
    val records = CSVFormat.EXCEL.withHeader().parse(StringReader(csvContent))

    unitOfWorkProvider.get().use {
      movieRepository.connect(it)

      val gson = Gson()

      it.flush {
        for (record in records) {
          val id = record["movieId"].toInt()
          val tmdbId = record["tmdbId"] ?: continue

          val tmdbUrl = "https://api.themoviedb.org/3/movie/$tmdbId?api_key=32821a97de8877f3d54c9081974a3a08"
          val (request, response, result) = tmdbUrl.httpGet().responseObject(TmdbModel.Deserializer())
          val tmdbModel = result.component1() ?: continue

          val movie = movieRepository.getMovie(id) ?: continue
          movie.poster = "https://image.tmdb.org/t/p/w300" + tmdbModel.poster_path
          movie.backdrop = "https://image.tmdb.org/t/p/w500" + tmdbModel.backdrop_path
        }
      }
    }
  }
}