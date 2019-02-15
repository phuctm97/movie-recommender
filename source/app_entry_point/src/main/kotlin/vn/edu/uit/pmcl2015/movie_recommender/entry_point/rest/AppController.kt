package vn.edu.uit.pmcl2015.movie_recommender.entry_point.rest

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import vn.edu.uit.pmcl2015.movie_recommender.core.entity.DomainException
import vn.edu.uit.pmcl2015.movie_recommender.entry_point.EntryPointBootstrap

/*********************************************************************************************/
/* Models */



/*********************************************************************************************/
/* Controller */

@RestController
open class AppController(private val entryPointConfig: RestEntryPointConfig,
                         private val entryPointBootstrap: EntryPointBootstrap) {
  @GetMapping("/exit")
  fun exit(@RequestParam("secret_key", required = true) secretKey: String): String {
    if (secretKey != entryPointConfig.appExitSecretKey) throw DomainException("WRONG_EXIT_SECRET_KEY", "Incorrect exit secret code")

    Thread({
             Thread.sleep(1000)
             entryPointBootstrap.exit(0)
           }).start()

    return "Accepted exit request. App is exiting in 1 second."
  }
}