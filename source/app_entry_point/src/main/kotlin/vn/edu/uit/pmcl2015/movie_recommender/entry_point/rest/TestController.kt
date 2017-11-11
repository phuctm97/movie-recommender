package vn.edu.uit.pmcl2015.movie_recommender.entry_point.rest

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/*********************************************************************************************/
/* Models */



/*********************************************************************************************/
/* Controller */

@RestController
@RequestMapping("/tests")
open class TestController {

  @GetMapping()
  fun getAllTests() = "Get All Tests"

  @GetMapping("/{id}")
  fun getTest(@PathVariable("id") id: Int) = "Get Test Of Id $id"
}