package vn.edu.uit.pmcl2015.movie_recommender.entry_point.rest

import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.*
import vn.edu.uit.pmcl2015.movie_recommender.core.use_case.UpdateRatingDataUseCase

/*********************************************************************************************/
/* Models */


/*********************************************************************************************/
/* Controller */

@RestController
@RequestMapping("ratings")
class RatingController(private val updateRatingDataUseCase: UpdateRatingDataUseCase) {
  @PostMapping
  fun updateMovieCollection(@RequestHeader(HttpHeaders.AUTHORIZATION) secretKey: String,
                            @RequestBody content: String): Any? {
    updateRatingDataUseCase.updateRatingDataUseCase(secretKey, content)
    return null
  }
}