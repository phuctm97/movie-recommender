package vn.edu.uit.pmcl2015.movie_recommender.data_provider.database

import vn.edu.uit.pmcl2015.movie_recommender.core.data_provider.Repository
import vn.edu.uit.pmcl2015.movie_recommender.core.data_provider.UnitOfWork

open class JpaRepository : Repository {
  protected lateinit var jpaUnitOfWork: JpaUnitOfWork

  override fun connect(unitOfWork: UnitOfWork) {
    jpaUnitOfWork = unitOfWork as JpaUnitOfWork
  }
}