package it.twinsbrains.chapter4

sealed class Option<out A> {
  companion object {
    fun <A> none(): Option<A> = None
    fun <A> some(a: A): Option<A> = Some(a)
  }
}

data class Some<out A>(val get: A) : Option<A>()
object None : Option<Nothing>()

fun <A, B> Option<A>.map(f: (A) -> B): Option<B> =
  when (this) {
    is None -> None
    is Some -> Some(f(this.get))
  }

fun <A, B> Option<A>.flatMap(f: (A) -> Option<B>): Option<B> = TODO()
fun <A> Option<A>.getOrElse(default: () -> A): A = TODO()
fun <A> Option<A>.orElse(ob: () -> Option<A>): Option<A> = TODO()
fun <A> Option<A>.filter(f: (A) -> Boolean): Option<A> = TODO()
