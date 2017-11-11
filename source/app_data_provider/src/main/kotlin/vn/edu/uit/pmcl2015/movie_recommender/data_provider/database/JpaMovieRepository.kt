package vn.edu.uit.pmcl2015.movie_recommender.data_provider.database

import vn.edu.uit.pmcl2015.movie_recommender.core.data_provider.MovieRepository
import vn.edu.uit.pmcl2015.movie_recommender.core.entity.Movie

class JpaMovieRepository : JpaRepository(), MovieRepository {
  @Suppress("UNCHECKED_CAST")
  override fun getAllMoviesLike(search: String): List<Movie> {
    if (!search.isEmpty()) {
      return jpaUnitOfWork.entityManager.createQuery("select movie from Movie movie " +
                                                     "where movie.title like '%$search%'").resultList as List<Movie>
    }
    return jpaUnitOfWork.entityManager.createQuery("select movie from Movie movie").resultList as List<Movie>
  }

  override fun save(movie: Movie) {
    jpaUnitOfWork.entityManager.persist(movie)
  }

  override fun deleteAllMovies() {
    jpaUnitOfWork.entityManager.createQuery("delete from Movie").executeUpdate()
  }
}