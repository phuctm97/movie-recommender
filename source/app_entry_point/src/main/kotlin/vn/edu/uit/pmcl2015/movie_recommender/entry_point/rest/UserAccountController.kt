package vn.edu.uit.pmcl2015.movie_recommender.entry_point.rest

import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import vn.edu.uit.pmcl2015.movie_recommender.core.use_case.SignUpUseCase

/*********************************************************************************************/
/* Models */

data class SignUpRequest(val username: String = "",
                         val password: String = "")


/*********************************************************************************************/
/* Controller */

@RestController
@RequestMapping("user_accounts")
class UserAccountController(private val signUpUseCase: SignUpUseCase) {

  @PostMapping(consumes = arrayOf(APPLICATION_JSON_VALUE),
               produces = arrayOf(APPLICATION_JSON_VALUE))
  fun signUp(@RequestBody request: SignUpRequest): Any {
    return signUpUseCase.signUp(request.username, request.password)
  }
}