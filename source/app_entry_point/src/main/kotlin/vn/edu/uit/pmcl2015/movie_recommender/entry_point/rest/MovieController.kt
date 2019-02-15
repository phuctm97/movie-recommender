package vn.edu.uit.pmcl2015.movie_recommender.entry_point.rest

import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.*
import vn.edu.uit.pmcl2015.movie_recommender.core.entity.Movie
import vn.edu.uit.pmcl2015.movie_recommender.core.use_case.GetMovieCollectionUseCase
import vn.edu.uit.pmcl2015.movie_recommender.core.use_case.UpdateMovieCollectionUseCase

/*********************************************************************************************/
/* Models */


/*********************************************************************************************/
/* Controller */

@RestController
@RequestMapping("movies")
class MovieController(private val updateMovieCollectionUseCase: UpdateMovieCollectionUseCase,
                      private val getMovieCollectionUseCase: GetMovieCollectionUseCase) {
  @PostMapping
  fun updateMovieCollection(@RequestHeader(HttpHeaders.AUTHORIZATION) secretKey: String,
                            @RequestBody content: String): Any? {
    updateMovieCollectionUseCase.updateMovieCollectionByCsv(secretKey, content)
    return null
  }

  @GetMapping
  fun getMovieCollection(@RequestParam("search", defaultValue = "") search: String)
      = getMovieCollectionUseCase.getMovieCollection(search)
}