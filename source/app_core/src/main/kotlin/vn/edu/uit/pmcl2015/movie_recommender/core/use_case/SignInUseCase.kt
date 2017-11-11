package vn.edu.uit.pmcl2015.movie_recommender.core.use_case

import vn.edu.uit.pmcl2015.movie_recommender.core.data_provider.UserAccountRepository
import vn.edu.uit.pmcl2015.movie_recommender.core.data_provider.UnitOfWorkProvider
import vn.edu.uit.pmcl2015.movie_recommender.core.entity.CoreException
import vn.edu.uit.pmcl2015.movie_recommender.core.entity.UserAccount
import vn.edu.uit.pmcl2015.movie_recommender.core.generateUserAccountJwt
import vn.edu.uit.pmcl2015.movie_recommender.core.hashSHA1
import vn.edu.uit.pmcl2015.movie_recommender.core.passwordHashingSalt

/*******************************************************************************************************/
/* Exceptions */

class IncorrectUserAccountException(developerMessage: String = "", moreInformation: String = "")
  : CoreException("CE002001", "Incorrect sign in information", developerMessage, moreInformation)


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

      val hashedPassword = hashSHA1(password, passwordHashingSalt())
      if (userAccount.hashedPassword != hashedPassword) throw IncorrectUserAccountException()

      val jwt = generateUserAccountJwt(userAccount.id!!)
      return SignInSuccessModel(userAccount.id!!,
                                userAccount.username,
                                jwt.token,
                                jwt.expireTime)
    }
  }
}