package vn.edu.uit.pmcl2015.movie_recommender.core.data_provider

import vn.edu.uit.pmcl2015.movie_recommender.core.entity.UserAccount

interface Repository {
  fun connect(unitOfWork: UnitOfWork)
}

interface UserAccountRepository : Repository {
  fun existUserAccountByUsername(username: String): Boolean

  fun userAccountByUsername(username: String): UserAccount?

  fun save(userAccount: UserAccount)
}