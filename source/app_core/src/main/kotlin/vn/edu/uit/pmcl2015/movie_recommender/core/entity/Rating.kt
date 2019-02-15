package vn.edu.uit.pmcl2015.movie_recommender.core.entity

class Rating {
  var id: Long? = null

  var userId: Int = 0

  var movieId: Int = 0

  var rating: Float = 0F

  var timestamp: Long = 0
}