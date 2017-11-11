package vn.edu.uit.pmcl2015.movie_recommender.data_provider.database

import vn.edu.uit.pmcl2015.movie_recommender.core.data_provider.RatingRepository
import vn.edu.uit.pmcl2015.movie_recommender.core.entity.Rating

class JpaRatingRepository : JpaRepository(), RatingRepository {
  override fun deleteAllRatings() {
    jpaUnitOfWork.entityManager.createQuery("delete from Rating").executeUpdate()
  }

  override fun save(rating: Rating) {
    if (rating.id == null) {
      jpaUnitOfWork.entityManager.persist(rating)
    }
    else {
      jpaUnitOfWork.entityManager.merge(rating)
    }
  }
}