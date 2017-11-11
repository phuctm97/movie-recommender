package vn.edu.uit.pmcl2015.movie_recommender.core.data_provider

import vn.edu.uit.pmcl2015.movie_recommender.core.entity.Movie
import vn.edu.uit.pmcl2015.movie_recommender.core.entity.Rating
import vn.edu.uit.pmcl2015.movie_recommender.core.entity.UserAccount

interface Repository {
  fun connect(unitOfWork: UnitOfWork)
}

interface UserAccountRepository : Repository {
  fun existUserAccountByUsername(username: String): Boolean

  fun userAccountByUsername(username: String): UserAccount?

  fun save(userAccount: UserAccount)
}

interface MovieRepository : Repository {
  fun deleteAllMovies()

  fun getAllMoviesLike(search: String): List<Movie>

  fun save(movie: Movie)
}

interface RatingRepository : Repository {
  fun deleteAllRatings()

  fun save(rating: Rating)
}