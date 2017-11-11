package vn.edu.uit.pmcl2015.movie_recommender.core

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.apache.commons.csv.CSVFormat
import java.io.StringReader
import java.security.MessageDigest
import java.util.*
import kotlin.experimental.and

fun currentTimestamp(): Long = System.currentTimeMillis()

fun hashSha1(source: String, salt: String): String {
  val md = MessageDigest.getInstance("SHA-1")
  md.update(salt.toByteArray())

  val sourceBytes = md.digest(source.toByteArray());
  val sb = StringBuilder();
  for (byte in sourceBytes) {
    sb.append(Integer.toString((byte and 0xff.toByte()) + 0x100, 16).substring(1));
  }

  return sb.toString()
}

data class Jwt(val token: String, val expireTime: Long)

fun generateUserAccountJwt(accountId: Int): Jwt {
  val algorithm = Algorithm.HMAC256(userAccountJwtSecretKey())
  val expireTime = currentTimestamp() + userAccountJwtExpiryDuration()

  val builder = JWT.create()
      .withIssuer("vn.edu.uit.pmcl2015.movie_recommender")
      .withIssuedAt(Date(currentTimestamp()))
      .withJWTId(UUID.randomUUID().toString())
      .withClaim("accountId", accountId)

  val jwt = builder.sign(algorithm)
  return Jwt(jwt, expireTime)
}