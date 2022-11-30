//Занятие 6.
//- Коллекции
//- Tuple
//- Pattern Matching
//  - Функции высших порядок коллекций
//  - for comprehension
//- Редукция коллекций

//----------------
// 1. Collection Hierarchy
//----------------
//
/*
                 Iterable
            /       |      \
      Seq          Set     Map
  /    |    \
List Vector Range
*/

// класс Seq - упорядоченные коллекции

// List
// обращение к элементам довольно медленное, чем у Set
val fruits = List("apples", "oranges", "pears")

val otherFruits = "apples" :: "oranges" :: "pears" :: Nil
otherFruits.head
otherFruits.tail

val plusFruits = "banana" :: fruits // O(1)
plusFruits(3) // O(n)

val emptyList = List()
val nil = Nil

// Vector
// обращение к элементам довольно быстрое
val names = Vector("louis", "frank", "hiromi")
names(2) // O(log(n))
"jack" +: names :+ "helena"
// добавление элементов уже не ::, как у List, а +: и :+

// Range
// [0..10]
val r = Range(0, 10)
r.foreach(print)
(0 to 10).foreach(print)
(0 until 10).foreach(print)
// способ задания интервалов в скале

// Set
val set = Set(1,5,2,3,4,2)
set.contains(5)

// Map
// некоторая структура данных которая работает в парпе ключ-значение
Map("a" -> 1, ("b", 3))

// Tuple
// Кортеж, объединяющий некоторые данные в кортеж
val tuple = ("a",1) // аналогично "a" -> 1
val tuple2 = "b" -> 2 -> 3
case class Triple(letter: String, i: Int)
Seq("a"->1, "b"->2).toMap


//---
// 2. Match pattern
//--
trait Expr
case class Number(value: Int) extends Expr
case class Sum(left: Expr, right: Expr) extends Expr
case class Prod(left: Expr, right: Expr) extends Expr

def eval(e: Expr): Int = {
  e match {
    case number: Number => number.value
    case Sum(left, right)  => eval(left) + eval(right)
    case Prod(left, right) => eval(left) * eval(right)
  }
}

fruits match {
  case Nil => println("fruits are empty") // если список пустой - возвращаем фразу
  case head :: _ => println(s"$head is head") // если непустой - возвращаем начало
}




/*
Match синтаксис:
 ▶ match предшествует последовательности cases, pat => expr.
 ▶ Каждый case ассоциирует выражение expr с паттерном pat.
 ▶ если селектор не найден возбуждается MatchError.
 Паттернами являются:
▶ конструкторы, например Number, Sum,
▶ переменные, например n, e1, ,e2
▶ wildcard _,
▶ константы, например 1, true.
 */

case class Foo(x: Int, y: Int)

Foo(1,2) match  {
  case Foo(x,y) => x + y
  case x Foo y => x + y
}


emptyList match {
  case Nil => println("list is empty")
  case ::(head, _) => println(s"$head is head")
}


//---
// 3. Функции высших порядок коллекций
//---
val list = List(1,2,3)
list.map(x => x + 1) // применение к каждому элементу
list.map(_ + 1)


case class Order(goods: List[String], price: Int, client: String)

val orders = List(
  Order(List("Apple", "Milk"), 200, "John"),
  Order(List("Pen", "Sugar"), 100, "Ann"),
  Order(List("Apple", "Banana"), 300, "Ann"),
  Order(List("Pen", "Shirt", "Cup"), 500, "Nick")
)

val q1 = orders.sortBy(_.price)
q1
orders

orders.map(_.client).toSet
orders.filter(order => order.price > 200 && order.goods.length > 2)
  .map(_.client)

orders.zip(1 to orders.length)
  .filter(tuple => {
    val (order, _) = tuple
    order.price > 200
  })
  .map { case (_, number) => number }

orders.groupBy(_.client).map { case (name, orders) => name -> orders.length }

// ---
// 4. for comprehension
// ---
// Задача - найти товары которые встречаются в разных заказах
for {
  o1 <- orders  // переменная о1 пробегается по всем заказам
  g1 <- o1.goods // дальше g1 пробегается по всем покупкам в заказе
  o2 <- orders if o2 != o1 // пробегаемся по всем заказам кроме нашего заказа
  g2 <- o2.goods if g1 == g2 // пробегаемся по всем покупкам в заказе, которые равны нашим
} yield g1 // возвращаем g1

// ---
// 5. Reduction
// ---

list.sum
list.reduce((a,acc) => a + acc)
list.reduce(_ + _)
//List[Int]().reduce(_ + _)
(0 :: list).reduce(_ + _)

list.fold(0)(_ + _)
list.foldLeft(0)(_ + _)

{
  val oppositeDirs = Map(
    "NORTH" -> "SOUTH",
    "EAST" -> "WEST",
    "SOUTH" -> "NORTH",
    "WEST" -> "EAST"
  )

  def isOpposite(one: String, other: String): Boolean = oppositeDirs(one).equals(other)

  def dirReduc(arr: List[String]): List[String] = {
    arr.foldLeft(List[String]()) { (acc, dir) =>
      if (acc.isEmpty) acc :+ dir
      else if (isOpposite(acc.last, dir)) acc.init
      else acc :+ dir
    }
  }
}

def dirReduc(arr: List[String]): List[String] =
  arr.foldRight(List.empty[String]) { (elem, acc) =>
    (elem, acc) match {
      case (_, Nil) => List(elem)
      case ("NORTH", "SOUTH" :: rest) => rest
      case ("SOUTH", "NORTH" :: rest) => rest
      case ("EAST", "WEST" :: rest) => rest
      case ("WEST", "EAST" :: rest) => rest
      case _ => elem :: acc
    }
  }


dirReduc(List("NORTH", "SOUTH", "SOUTH", "EAST", "WEST", "NORTH", "WEST"))