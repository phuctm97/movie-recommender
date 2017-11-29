package vn.edu.uit.pmcl2015.movie_recommender.entry_point.web

import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import vn.edu.uit.pmcl2015.movie_recommender.core.use_case.GetMovieCollectionUseCase
import vn.edu.uit.pmcl2015.movie_recommender.core.use_case.GetRecommendationsUseCase

data class MovieModel(val id: Int = 0, val title: String = "")

@Controller
@RequestMapping("movies")
class MovieController(private val getMovieCollectionUseCase: GetMovieCollectionUseCase,
                      private val getRecommendationsUseCase: GetRecommendationsUseCase) {

  @GetMapping()
  fun getAll(@RequestParam("search", defaultValue = "") search: String,
             model: ModelMap): String {
    val list = getMovieCollectionUseCase.getMovieCollection(search)
    model.put("list", list.take(20))
    return "index"
  }

  @GetMapping("{id}")
  fun get(@PathVariable("id") id: Int, model: ModelMap): String {
    val movie = getMovieCollectionUseCase.getMovie(id)
    model.put("title", movie.title)

    val list1 = getRecommendationsUseCase.getRecommendationsByDampedMeanRating(5)
    val list2 = getRecommendationsUseCase.getRecommendationsByBasicAssociations(5, id)

    model.put("list1", list1)
    model.put("list2", list2)
    return "film"
  }
}