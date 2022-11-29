package it.twinsbrains.fpik.chapter12

import arrow.Kind
import java.text.SimpleDateFormat
import java.util.Date

class ForValidation
typealias ValidationPartialOf<E> = Kind<ForValidation, E>
typealias ValidationOf<E, A> = Kind<ValidationPartialOf<E>, A>

fun <E, A> ValidationOf<E, A>.fix(): Validation<E, A> = this as Validation<E, A>

sealed class Validation<out E, out A> : ValidationOf<E, A>
data class Failure<E>(
  val head: E,
  val tail: List<E> = emptyList()
) : Validation<E, Nothing>() {
  operator fun plus(another: Failure<E>): Failure<E> {
    return Failure(this.head, this.tail + listOf(another.head) + another.tail)
  }
}

data class Success<A>(val successValue: A) : Validation<Nothing, A>()

fun <E> validation(): Applicative<ValidationPartialOf<E>> = object : Applicative<ValidationPartialOf<E>> {
  override fun <A> unit(a: A): ValidationOf<E, A> = Success(a)

  override fun <A, B> apply(
    fab: ValidationOf<E, (A) -> B>,
    fa: ValidationOf<E, A>
  ): ValidationOf<E, B> = when (val a = fa.fix()) {
    is Failure -> when (val f = fab.fix()) {
      is Failure -> a + f
      is Success -> a
    }

    is Success -> when (val f = fab.fix()) {
      is Failure -> f
      is Success -> unit(f.successValue(a.successValue))
    }
  }
}

object Examples {
  data class WebForm(val f1: String, val f2: Date, val f3: String)

  fun validName(name: String): Validation<String, String> =
    if (name != "") Success(name)
    else Failure("Name cannot be empty")

  fun validDateOfBirth(dob: String): Validation<String, Date> =
    try {
      Success(SimpleDateFormat("yyyy-MM-dd").parse(dob))
    } catch (e: Exception) {
      Failure("Date of birth must be in format yyyy-MM-dd")
    }

  fun validPhone(phone: String): Validation<String, String> =
    if (phone.matches("[0-9]{10}".toRegex())) Success(phone)
    else Failure("Phone number must be 10 digits")

  private val F = validation<String>()

  fun validatedWebForm(
    name: String,
    dob: String,
    phone: String
  ): Validation<String, WebForm> {
    val result = F.map3(
      validName(name),
      validDateOfBirth(dob),
      validPhone(phone)
    ) { n, d, p -> WebForm(n, d, p) }
    return result.fix()
  }


}