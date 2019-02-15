package vn.edu.uit.pmcl2015.movie_recommender.entry_point.rest

import org.springframework.web.bind.annotation.*
import vn.edu.uit.pmcl2015.movie_recommender.core.use_case.GetRecommendationsUseCase
import vn.edu.uit.pmcl2015.movie_recommender.core.use_case.Recommendation


/*********************************************************************************************/
/* Models */

/*********************************************************************************************/
/* Controller */

@RestController
@RequestMapping("recommendations")
class RecommendationController(private val getRecommendationsUseCase: GetRecommendationsUseCase) {

  @GetMapping
  fun getRecommendations(@RequestParam("method", defaultValue = GetRecommendationsUseCase.BY_MEAN_RATINGS) method: String,
                         @RequestParam("limit", defaultValue = "10") limit: Int,
                         @RequestParam("reference_movie_id", defaultValue = "0") referenceMovieId: Int)
      = getRecommendationsUseCase.getRecommendations(method, limit, referenceMovieId)

}