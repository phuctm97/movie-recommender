package vn.edu.uit.pmcl2015.movie_recommender.data_provider.database

import vn.edu.uit.pmcl2015.movie_recommender.core.data_provider.UserAccountRepository
import vn.edu.uit.pmcl2015.movie_recommender.core.entity.UserAccount
import javax.persistence.NoResultException

class JpaUserAccountRepository : JpaRepository(), UserAccountRepository {
  override fun existUserAccountByUsername(username: String): Boolean {
    val resultList = jpaUnitOfWork.entityManager.createQuery("select doctorAccount from UserAccount doctorAccount " +
                                                             "where username=:username")
        .setParameter("username", username)
        .resultList
    return !resultList.isEmpty()
  }

  override fun userAccountByUsername(username: String): UserAccount? {
    return try {
      jpaUnitOfWork.entityManager.createQuery("select doctorAccount from UserAccount doctorAccount " +
                                              "where username=:username")
          .setParameter("username", username)
          .singleResult as UserAccount
    }
    catch (ex: NoResultException) {
      null
    }
  }

  override fun save(userAccount: UserAccount) {
    if (userAccount.id == null) {
      jpaUnitOfWork.entityManager.persist(userAccount)
    }
    else {
      jpaUnitOfWork.entityManager.merge(userAccount)
    }
  }
}