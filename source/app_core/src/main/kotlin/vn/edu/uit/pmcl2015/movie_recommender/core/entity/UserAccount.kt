package vn.edu.uit.pmcl2015.movie_recommender.core.entity

class UserAccount {
  var id: Int? = null

  var username: String = ""
    set(value) {
      if (value.length < 6 || value.length > 20) throw InvalidArgumentException("Username length must be between 6 and 20 characters")
      field = value
    }

  var hashedPassword: String = ""
    set(value) {
      if (value.length < 20 || value.length > 254) throw InvalidArgumentException("Hashed password length must be between 20 and 254 characters")
      field = value
    }
}