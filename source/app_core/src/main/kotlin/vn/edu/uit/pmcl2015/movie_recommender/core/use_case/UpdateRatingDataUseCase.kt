package vn.edu.uit.pmcl2015.movie_recommender.core.use_case

import org.apache.commons.csv.CSVFormat
import vn.edu.uit.pmcl2015.movie_recommender.core.data_provider.RatingRepository
import vn.edu.uit.pmcl2015.movie_recommender.core.data_provider.UnitOfWorkProvider
import vn.edu.uit.pmcl2015.movie_recommender.core.entity.InvalidArgumentException
import vn.edu.uit.pmcl2015.movie_recommender.core.entity.Rating
import vn.edu.uit.pmcl2015.movie_recommender.core.updateRatingDataSecretKey
import java.io.StringReader

/*******************************************************************************************************/
/* Models */

/*******************************************************************************************************/
/* Use case */

class UpdateRatingDataUseCase(private val unitOfWorkProvider: UnitOfWorkProvider,
                              private val ratingRepository: RatingRepository) {
  fun updateRatingDataUseCase(secretKey: String, csvContent: String) {
    if (secretKey != updateRatingDataSecretKey()) throw InvalidArgumentException("Invalid secret key")
    val records = CSVFormat.EXCEL.withHeader().parse(StringReader(csvContent))

    unitOfWorkProvider.get().use {
      ratingRepository.connect(it)

      it.flush {
        ratingRepository.deleteAllRatings()

        for (record in records) {
          val userId = record["userId"].toInt()
          val movieId = record["movieId"].toInt()
          val rating = record["rating"].toFloat()
          val timestamp = record["timestamp"].toLong()

          val newRating = Rating()
          newRating.userId = userId
          newRating.movieId = movieId
          newRating.rating = rating
          newRating.timestamp = timestamp
          ratingRepository.save(newRating)
        }
      }
    }
  }
}