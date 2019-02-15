package vn.edu.uit.pmcl2015.movie_recommender.core.use_case

import vn.edu.uit.pmcl2015.movie_recommender.core.*
import vn.edu.uit.pmcl2015.movie_recommender.core.data_provider.UserAccountRepository
import vn.edu.uit.pmcl2015.movie_recommender.core.data_provider.UnitOfWorkProvider
import vn.edu.uit.pmcl2015.movie_recommender.core.entity.DomainException
import vn.edu.uit.pmcl2015.movie_recommender.core.entity.UserAccount


/*******************************************************************************************************/
/* Exceptions */

class UserAccountExistedException(developerMessage: String = "", moreInformation: String = "")
  : DomainException("CE001001", "Doctor account has already existed", developerMessage, moreInformation)


/*******************************************************************************************************/
/* Models */

data class SignUpSuccessModel(val accountId: Int,
                              val username: String,
                              val jwt: String,
                              val jwtExpireTime: Long)

/*******************************************************************************************************/
/* Use case */

class SignUpUseCase(private val unitOfWorkProvider: UnitOfWorkProvider,
                    private val userAccountRepository: UserAccountRepository) {
  fun signUp(username: String, password: String): SignUpSuccessModel {
    unitOfWorkProvider.get().use {
      userAccountRepository.connect(it)

      if (userAccountRepository.existUserAccountByUsername(username))
        throw UserAccountExistedException()

      val userAccount = UserAccount()
      userAccount.username = username
      userAccount.hashedPassword = hashSha1(password, passwordHashingSalt())

      it.flush {
        userAccountRepository.save(userAccount)
      }

      val jwt = generateUserAccountSessionJwt(userAccount.id!!)
      return SignUpSuccessModel(userAccount.id!!,
                                userAccount.username,
                                jwt.token,
                                jwt.expireTime)
    }
  }
}