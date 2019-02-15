package vn.edu.uit.pmcl2015.movie_recommender.core.use_case

import vn.edu.uit.pmcl2015.movie_recommender.core.data_provider.UserAccountRepository
import vn.edu.uit.pmcl2015.movie_recommender.core.data_provider.UnitOfWorkProvider
import vn.edu.uit.pmcl2015.movie_recommender.core.entity.DomainException
import vn.edu.uit.pmcl2015.movie_recommender.core.entity.UserAccount
import vn.edu.uit.pmcl2015.movie_recommender.core.generateUserAccountSessionJwt
import vn.edu.uit.pmcl2015.movie_recommender.core.hashSha1
import vn.edu.uit.pmcl2015.movie_recommender.core.passwordHashingSalt

/*******************************************************************************************************/
/* Exceptions */

class IncorrectUserAccountException(developerMessage: String = "", moreInformation: String = "")
  : DomainException("CE002001", "Incorrect sign in information", developerMessage, moreInformation)


/*******************************************************************************************************/
/* Models */

data class SignInSuccessModel(val accountId: Int,
                              val username: String,
                              val jwt: String,
                              val jwtExpireTime: Long)

/*******************************************************************************************************/
/* Use case */

class SignInUseCase(private val unitOfWorkProvider: UnitOfWorkProvider,
                    private val userAccountRepository: UserAccountRepository) {
  fun signIn(username: String, password: String): SignInSuccessModel {
    unitOfWorkProvider.get().use {
      userAccountRepository.connect(it)

      val userAccount: UserAccount = userAccountRepository.userAccountByUsername(username) ?: throw IncorrectUserAccountException()

      val hashedPassword = hashSha1(password, passwordHashingSalt())
      if (userAccount.hashedPassword != hashedPassword) throw IncorrectUserAccountException()

      val jwt = generateUserAccountSessionJwt(userAccount.id!!)
      return SignInSuccessModel(userAccount.id!!,
                                userAccount.username,
                                jwt.token,
                                jwt.expireTime)
    }
  }
}