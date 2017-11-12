package vn.edu.uit.pmcl2015.movie_recommender.core.use_case

import org.slf4j.LoggerFactory
import vn.edu.uit.pmcl2015.movie_recommender.core.data_provider.MovieRepository
import vn.edu.uit.pmcl2015.movie_recommender.core.data_provider.RatingRepository
import vn.edu.uit.pmcl2015.movie_recommender.core.data_provider.UnitOfWorkProvider
import vn.edu.uit.pmcl2015.movie_recommender.core.entity.NotSupportedOperationException
import vn.edu.uit.pmcl2015.movie_recommender.core.entity.Rating
import java.util.*
import kotlin.system.measureTimeMillis

/*******************************************************************************************************/
/* Exceptions */

/*******************************************************************************************************/
/* Models */

data class Recommendation(val movieTitle: String, val score: Float)

/*******************************************************************************************************/
/* Use case */

class GetRecommendationsUseCase(private val unitOfWorkProvider: UnitOfWorkProvider,
                                private val ratingRepository: RatingRepository,
                                private val movieRepository: MovieRepository) {
  companion object {
    val LOGGER = LoggerFactory.getLogger(GetRecommendationsUseCase::class.java)!!
    const val BY_MEAN_RATINGS = "BY_MEAN_RATINGS"
    const val BY_DAMPED_MEAN_RATINGS = "BY_DAMPED_MEAN_RATINGS"
    const val BY_BASIC_ASSOCIATIONS = "BY_BASIC_ASSOCIATIONS"
    const val BY_LIFTED_ASSOCIATIONS = "BY_LIFTED_ASSOCIATIONS"
  }

  fun getRecommendations(method: String, limit: Int, referenceMovieId: Int): List<Recommendation> {
    return when (method) {
      BY_MEAN_RATINGS        -> getRecommendationsByMeanRatings(limit)
      BY_DAMPED_MEAN_RATINGS -> getRecommendationsByDampedMeanRating(limit)
      BY_BASIC_ASSOCIATIONS  -> getRecommendationsByBasicAssociations(limit, referenceMovieId)
      BY_LIFTED_ASSOCIATIONS -> getRecommendationsByLiftedAssociations(limit, referenceMovieId)
      else                   -> throw NotSupportedOperationException("Support only recommendation methods: " +
                                                                     "$BY_MEAN_RATINGS, " +
                                                                     "$BY_DAMPED_MEAN_RATINGS, " +
                                                                     "$BY_BASIC_ASSOCIATIONS, " +
                                                                     "$BY_LIFTED_ASSOCIATIONS.")
    }
  }

  @Suppress("MemberVisibilityCanPrivate")
  fun getRecommendationsByMeanRatings(limit: Int): List<Recommendation> {
    // Query all ratings

    var ratingList: List<Rating>? = null

    unitOfWorkProvider.get().use {
      ratingRepository.connect(it)
      ratingList = ratingRepository.getAllRatings()
    }

    // Calculate movie mean ratings

    val movieRatingsMap = ratingList!!.groupBy { it.movieId }

    val movieMeanRatingMap = mutableMapOf<Int, Float>()
    for (movieRatingsEntry in movieRatingsMap) {
      val movieId = movieRatingsEntry.key
      val movieRatings = movieRatingsEntry.value

      movieMeanRatingMap[movieId] = movieRatings.sumByDouble { it.rating.toDouble() }.toFloat() /
                                    movieRatings.size
    }

    LOGGER.info("Calculated Movie Mean Ratings Map: ${movieMeanRatingMap.size} rows")

    // Find top n ratings

    val results = mutableListOf<Pair<Int, Float>>()
    for (movieMeanRatingEntry in movieMeanRatingMap) {
      val movieId = movieMeanRatingEntry.key
      val score = movieMeanRatingEntry.value

      if (results.size < limit) {
        results.add(Pair(movieId, score))
        sortResults(results)
      }
      else {
        val minResult = results.last()
        val minResultMovieId = minResult.first
        val minResultScore = minResult.second

        if (score > minResultScore || (score == minResultScore && movieId < minResultMovieId)) {
          results.removeAt(results.lastIndex)
          results.add(Pair(movieId, score))
          sortResults(results)
        }
      }
    }

    return generateRecommendations(results)
  }

  @Suppress("MemberVisibilityCanPrivate")
  fun getRecommendationsByDampedMeanRating(limit: Int): List<Recommendation> {
    // Query all ratings
    var ratingList: List<Rating>? = null

    unitOfWorkProvider.get().use {
      ratingRepository.connect(it)
      ratingList = ratingRepository.getAllRatings()
    }

    val dampingTerm = 5 // TODO: move to parameter

    // Calculate global mean ratings

    val globalMeanRating = (ratingList!!.sumByDouble { it.rating.toDouble() } / ratingList!!.size).toFloat()

    LOGGER.info("Calculated Global Mean Rating: $globalMeanRating")

    // Calculate movie damped mean ratings

    val movieRatingsMap = ratingList!!.groupBy { it.movieId }

    val movieMeanRatingMap = mutableMapOf<Int, Float>()
    for (movieRatingsEntry in movieRatingsMap) {
      val movieId = movieRatingsEntry.key
      val movieRatings = movieRatingsEntry.value

      movieMeanRatingMap[movieId] = (movieRatings.sumByDouble { it.rating.toDouble() }.toFloat() +
                                     dampingTerm * globalMeanRating) /
                                    (movieRatings.size + dampingTerm)
    }

    LOGGER.info("Calculated Movie Mean Ratings Map: ${movieMeanRatingMap.size} rows")

    // Find top n ratings

    val results = mutableListOf<Pair<Int, Float>>()
    for (movieMeanRatingEntry in movieMeanRatingMap) {
      val movieId = movieMeanRatingEntry.key
      val score = movieMeanRatingEntry.value

      if (results.size < limit) {
        results.add(Pair(movieId, score))
        sortResults(results)
      }
      else {
        val minResult = results.last()
        val minResultMovieId = minResult.first
        val minResultScore = minResult.second

        if (score > minResultScore || (score == minResultScore && movieId < minResultMovieId)) {
          results.removeAt(results.lastIndex)
          results.add(Pair(movieId, score))
          sortResults(results)
        }
      }
    }

    return generateRecommendations(results)
  }

  @Suppress("MemberVisibilityCanPrivate")
  fun getRecommendationsByBasicAssociations(limit: Int, referenceMovieId: Int): List<Recommendation> {
    // Query all ratings

    var ratingList: List<Rating>? = null

    unitOfWorkProvider.get().use {
      ratingRepository.connect(it)
      ratingList = ratingRepository.getAllRatings()
    }

    val positiveRating = 2.5F

    // Calculate movie rated users map

    val moviePositiveRatedUsersMap = mutableMapOf<Int, MutableList<Int>>()

    val moviePositiveRatingsMap = ratingList!!.filter { it.rating >= positiveRating }.groupBy { it.movieId }
    for (moviePositiveRatingsEntry in moviePositiveRatingsMap) {
      val movieId = moviePositiveRatingsEntry.key
      val moviePositiveRatings = moviePositiveRatingsEntry.value

      val positiveRatedUsers = mutableListOf<Int>()
      moviePositiveRatings.mapTo(positiveRatedUsers) { it.userId }
      moviePositiveRatedUsersMap[movieId] = positiveRatedUsers
    }

    LOGGER.info("Calculated movie rated users map: ${moviePositiveRatedUsersMap.size} rows")

    // Calculate association rule

    val associationMatrix = mutableMapOf<Int, MutableMap<Int, Float>>()

    for (moviePositiveRatedUsersEntry1 in moviePositiveRatedUsersMap) {
      val movieId1 = moviePositiveRatedUsersEntry1.key
      if (movieId1 != referenceMovieId) continue // TODO: remove this

      val ratedUsers1 = moviePositiveRatedUsersEntry1.value

      val movie1AssociationMap = mutableMapOf<Int, Float>()

      for (moviePositiveRatedUsersEntry2 in moviePositiveRatedUsersMap) {
        val movieId2 = moviePositiveRatedUsersEntry2.key
        if (movieId1 == movieId2) continue

        val ratedUsers2 = moviePositiveRatedUsersEntry2.value

        val score = countIntersect(ratedUsers1, ratedUsers2).toFloat() / ratedUsers1.size
        movie1AssociationMap[movieId2] = score
      }

      if (movie1AssociationMap.isNotEmpty()) {
        associationMatrix[movieId1] = movie1AssociationMap
      }
    }

    LOGGER.info("Calculated association matrix: ${associationMatrix.size} rows")

    // Find top n associated item

    val results = mutableListOf<Pair<Int, Float>>()

    val movieSet = ratingList!!.groupBy { it.movieId }.keys

    for (movieId in movieSet) {
      if (movieId == referenceMovieId) continue
      val score = associationMatrix[referenceMovieId]?.get(movieId) ?: continue

      if (results.size < limit) {
        results.add(Pair(movieId, score))
        sortResults(results)
      }
      else {
        val minResult = results.last()
        val minResultMovieId = minResult.first
        val minResultScore = minResult.second

        if (score > minResultScore || (score == minResultScore && movieId < minResultMovieId)) {
          results.removeAt(results.lastIndex)
          results.add(Pair(movieId, score))
          sortResults(results)
        }
      }
    }

    return generateRecommendations(results)
  }

  @Suppress("MemberVisibilityCanPrivate")
  fun getRecommendationsByLiftedAssociations(limit: Int, referenceMovieId: Int): List<Recommendation> {
    // Query all ratings

    var ratingList: List<Rating>? = null

    unitOfWorkProvider.get().use {
      ratingRepository.connect(it)
      ratingList = ratingRepository.getAllRatings()
    }

    val positiveRating = 2.5F

    // Calculate number of users
    val totalUsers = ratingList!!.groupBy { it.userId }.size

    // Calculate movie rated users map

    val moviePositiveRatedUsersMap = mutableMapOf<Int, MutableList<Int>>()

    val moviePositiveRatingsMap = ratingList!!.filter { it.rating >= positiveRating }.groupBy { it.movieId }
    for (moviePositiveRatingsEntry in moviePositiveRatingsMap) {
      val movieId = moviePositiveRatingsEntry.key
      val moviePositiveRatings = moviePositiveRatingsEntry.value

      val positiveRatedUsers = mutableListOf<Int>()
      moviePositiveRatings.mapTo(positiveRatedUsers) { it.userId }
      moviePositiveRatedUsersMap[movieId] = positiveRatedUsers
    }

    LOGGER.info("Calculated movie rated users map: ${moviePositiveRatedUsersMap.size} rows")

    // Calculate association rule

    val associationMatrix = mutableMapOf<Int, MutableMap<Int, Float>>()

    for (moviePositiveRatedUsersEntry1 in moviePositiveRatedUsersMap) {
      val movieId1 = moviePositiveRatedUsersEntry1.key
      if (movieId1 != referenceMovieId) continue // TODO: remove this

      val ratedUsers1 = moviePositiveRatedUsersEntry1.value

      val movie1AssociationMap = mutableMapOf<Int, Float>()

      for (moviePositiveRatedUsersEntry2 in moviePositiveRatedUsersMap) {
        val movieId2 = moviePositiveRatedUsersEntry2.key
        if (movieId1 == movieId2) continue

        val ratedUsers2 = moviePositiveRatedUsersEntry2.value

        val score = (countIntersect(ratedUsers1, ratedUsers2).toFloat() * totalUsers) / (ratedUsers1.size * ratedUsers2.size)
        movie1AssociationMap[movieId2] = score
      }

      if (movie1AssociationMap.isNotEmpty()) {
        associationMatrix[movieId1] = movie1AssociationMap
      }
    }

    LOGGER.info("Calculated association matrix: ${associationMatrix.size} rows")

    // Find top n associated item

    val results = mutableListOf<Pair<Int, Float>>()

    val movieSet = ratingList!!.groupBy { it.movieId }.keys

    for (movieId in movieSet) {
      if (movieId == referenceMovieId) continue
      val score = associationMatrix[referenceMovieId]?.get(movieId) ?: continue

      if (results.size < limit) {
        results.add(Pair(movieId, score))
        sortResults(results)
      }
      else {
        val minResult = results.last()
        val minResultMovieId = minResult.first
        val minResultScore = minResult.second

        if (score > minResultScore || (score == minResultScore && movieId < minResultMovieId)) {
          results.removeAt(results.lastIndex)
          results.add(Pair(movieId, score))
          sortResults(results)
        }
      }
    }

    return generateRecommendations(results)
  }

  private fun sortResults(results: List<Pair<Int, Float>>) {
    Collections.sort(results, kotlin.Comparator { o1, o2 ->
      if (o1.second < o2.second) return@Comparator 1
      if (o1.second == o2.second) {
        if (o1.first > o2.first) return@Comparator 1
        if (o1.first == o2.first) return@Comparator 0
      }
      return@Comparator -1
    })
  }

  private fun countIntersect(list1: List<Int>, list2: List<Int>) = list1.count { list2.contains(it) }

  private fun generateRecommendations(results: List<Pair<Int, Float>>): List<Recommendation> {
    val recommendations = mutableListOf<Recommendation>()
    unitOfWorkProvider.get().use {
      movieRepository.connect(it)

      for (result in results) {
        val movie = movieRepository.getMovie(result.first) ?: continue
        recommendations.add(Recommendation(movie.title, result.second))
      }
    }
    return recommendations
  }

}