import scala.concurrent.duration.DurationInt
// 1. Инициализация переменных
val a = 1
def b = 2
lazy val c = 3
a + b + c
def expr = {
  val x = { print("x"); 1 }
  lazy val y = { print("y"); 2 }
  def z = { print("z"); 3 }
  z + y + x + z + y + x
}
println(expr)
// 2. Полиморфизм
//   - полиморфизм подтипов (Succ.+ = this.predecessor + that.successor)
//   - параметрический полиморфизм: List[T]
List(0,2,3).sorted
//   - ad-hoc полиморфизм
// 3. Неявных преобразований
0 to 10
0.to(10)
/*
 Неявные преобразования, определение через implicit в контексте выполнения:
   - implicit class
   - implicit функции/переменные
   - implicit в параметрах функции
 */
//- implicit class
implicit class MyIntRich(i: Int){
  def isEven = i % 2 == 0
}
new MyIntRich(2).isEven
val _2: MyIntRich = 2
_2.isEven
4.isEven
5.isEven
15.seconds
//- implicit функции/переменные
//- implicit в параметрах функции
//trait MyList[+T] {
//  def sum[G <: T](f: (G, G) => T): T
//}
//
//case class Cons[T](val head: T, val tail: MyList[T]) extends MyList[T]{
//    override def sum(f: (T, T) => T) = tail match {
//        case Nil => head
//        case _ => f(head,  tail.sum(f))
//    }
//}
//
//object Nil extends MyList[Nothing] {
//    override def sum(f: (Nothing, Nothing) => Nothing) = throw new Error("Список пуст")
//}
//
//Cons(1, Cons(2, Nil)).sum(_ + _)
def sumList[T](list: List[T])(implicit sum: (T, T) => T): T = list match {
  case head :: tail if tail.nonEmpty => sum(head, sumList(tail)(sum))
  case head :: tail if tail.isEmpty => head
  case Nil => throw new Error("Список пуст")
}
implicit val sumInt: (Int, Int) => Int  = _ + _
implicit val sumString: (String, String) => String = _ + _
sumList(List(1,2,3))
sumList(List("implicit ", "val"))
//sumList(List(true, false))
List(2,3,1,5,2).sorted
// 4. LazyList
//LazyList[T](val head: T, val tail: () => LazyList[T])
LazyList(1,2,3)
1 #:: 2 #:: LazyList.empty
LazyList
def from(n: Int): LazyList[Int] = n #:: from(n + 1)
val nats = from(1)
val natsBourbaki = 0 #:: nats
val evens = nats.filter(_ % 2 == 0)
val alsoEvens = nats.map(_ * 2)
alsoEvens(2)
def isPrime(n: Int): Boolean = (2 until n).forall(n % _ != 0)
(0 to 120).filter(isPrime)
def sieve(list: LazyList[Int]): LazyList[Int] = {
  list.head #:: sieve(list.filter(_ % list.head != 0))
}
val primes = sieve(from(2))
(primes take 120).toList
/*
Монады:
 - списки
 - Option
 - Try
 - Either
 - Future
 */