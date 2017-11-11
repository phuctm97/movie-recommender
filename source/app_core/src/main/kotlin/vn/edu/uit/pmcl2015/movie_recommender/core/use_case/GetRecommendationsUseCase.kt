package vn.edu.uit.pmcl2015.movie_recommender.core.use_case

import vn.edu.uit.pmcl2015.movie_recommender.core.entity.NotSupportedOperationException

/*******************************************************************************************************/
/* Exceptions */

/*******************************************************************************************************/
/* Models */

data class Recommendation(val movieTitle: String, val predictedRating: Float)

/*******************************************************************************************************/
/* Use case */

class GetRecommendationsUseCase {
  companion object {
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

  fun getRecommendationsByMeanRatings(limit: Int): List<Recommendation> {
    return listOf()
  }

  fun getRecommendationsByDampedMeanRating(limit: Int): List<Recommendation> {
    throw NotSupportedOperationException()
  }

  fun getRecommendationsByBasicAssociations(limit: Int, referenceMovieId: Int): List<Recommendation> {
    throw NotSupportedOperationException()
  }

  fun getRecommendationsByLiftedAssociations(limit: Int, referenceMovieId: Int): List<Recommendation> {
    throw NotSupportedOperationException()
  }
}