package vn.edu.uit.pmcl2015.movie_recommender.data_provider.database

import org.hibernate.Hibernate
import vn.edu.uit.pmcl2015.movie_recommender.core.data_provider.UnitOfWork
import vn.edu.uit.pmcl2015.movie_recommender.core.data_provider.UnitOfWorkProvider
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory

class JpaUnitOfWork(val entityManager: EntityManager) : UnitOfWork {
  override fun <T> flush(body: () -> T): T {
    if (entityManager.transaction.isActive)
      throw UnsupportedOperationException("There is another in-progress transaction")

    entityManager.transaction.begin()
    val result = body()
    commit()

    return result
  }

  override fun fetch(entity: Any) {
    Hibernate.initialize(entity)
  }

  override fun commit() {
    if (!entityManager.transaction.isActive)
      throw UnsupportedOperationException("There is no active transaction to commit")

    entityManager.transaction.commit()
  }

  override fun rollback() {
    if (!entityManager.transaction.isActive)
      throw UnsupportedOperationException("There is no active transaction to rollback")

    entityManager.transaction.rollback()
  }

  override fun close() {
    try {
      if (entityManager.transaction.isActive) {
        commit()
      }
    }
    catch (ex: Throwable) {
      if (entityManager.transaction.isActive) {
        rollback()
      }
    }
    finally {
      entityManager.close()
    }
  }
}

class JpaUnitOfWorkProvider(private val entityManagerFactory: EntityManagerFactory) : UnitOfWorkProvider {
  override fun get(): UnitOfWork = JpaUnitOfWork(entityManagerFactory.createEntityManager())
}