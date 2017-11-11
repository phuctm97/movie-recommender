package vn.edu.uit.pmcl2015.movie_recommender

import vn.edu.uit.pmcl2015.movie_recommender.entry_point.EntryPointBootstrap
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(basePackages = arrayOf("vn.edu.uit.pmcl2015.movie_recommender.configuration"))
open class Application(private val bootstrap: EntryPointBootstrap) : CommandLineRunner {
  override fun run(vararg args: String?) {
    bootstrap.run(*args)
  }
}

fun main(args: Array<String>) {
  SpringApplication.run(Application::class.java, *args)
}