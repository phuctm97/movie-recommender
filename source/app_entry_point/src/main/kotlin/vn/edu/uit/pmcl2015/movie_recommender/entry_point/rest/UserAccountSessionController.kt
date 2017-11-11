package vn.edu.uit.pmcl2015.movie_recommender.entry_point.rest

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import vn.edu.uit.pmcl2015.movie_recommender.core.use_case.SignInUseCase


/*********************************************************************************************/
/* Models */

data class SignInRequest(val username: String = "",
                         val password: String = "")


/*********************************************************************************************/
/* Controller */

@RestController
@RequestMapping("user_account_sessions")
class UserAccountSessionController(private val signInUseCase: SignInUseCase) {
  @PostMapping(consumes = arrayOf(MediaType.APPLICATION_JSON_VALUE),
               produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
  fun signIn(@RequestBody request: SignInRequest): Any {
    return signInUseCase.signIn(request.username, request.password)
  }
}