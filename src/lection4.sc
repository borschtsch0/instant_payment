// в SCALA при ООП никогда не используется NULL

// абстрактные классы не реализуют методы, они их реализуют в переменных-потомках

// apply() - это метод какого-то объекта-компаньона (50 минута лекции)

// 55 минута лекции - кейс классы

// 1 ч 12 минут - трейты
// трейты - это какое-то поведение. Обычно вместо абстрактного класса выбирается трейт

abstract class IntSet {
  def value: Int
  def contains(e: Int): Boolean
  def include(e: Int): IntSet
}

case class Element(val value: Int, left: IntSet, right: IntSet) extends IntSet {
  override def contains(e: Int): Boolean = {
    if (e == value) true
    else if (e < value) left.contains(e)
    else right.contains(e)
  }
  override def include(e: Int): IntSet = {
    if (e == value) this
    else if (e < value) new Element(value, left.include(e), right)
    else new Element(value, left, right.include(e))
  }
}

object Empty extends IntSet {
  override def value = throw new Error("Пустое множество пусто")
  override def contains(e: Int) = false
  override def include(e: Int) = Element(e, Empty, Empty)
}

object IntSet {
  def apply() = Empty
  def apply(e: Int) = Element(e, Empty, Empty)
}



Empty.contains(1)
val one = Element(1, Empty, Empty)
one.value
one.contains(1)
one.contains(2)
val oneTwo = one.include(2)
val oneTwo_ = new Element(2, one, Empty)
oneTwo.contains(1)
oneTwo.contains(2)
oneTwo.contains(3)

val e1 = IntSet.apply()
val e2 = IntSet()
val two = IntSet(2)

val a = Array(1,2,3)
a.head == a(0)


class Person2(val name: String, relation: String)
val p2 = new Person2("ssdf", "sdfdsf")
p2.name
object Person2 {
  def apply = ???
}
p2

case class Person(name: String, relation: String = "empty")
object Person {
  def apply(name: String): Person = Person(name, "sdfdfgdfg")
}
val p = Person("dfdf", "sdfsdf")
p.name
val p1 = p.copy(relation = "dfgdfg")
p
p1
val p_ = Person("dfdf", "sdfsdf")
p_ == p
val p3 = Person("Object")
p3

/*
 object:
  - apply
  - unapply
 class:
  - val
  - copy
  - toString
 */


trait Planar {
  def height: Int
  def width: Int
  def surface = height * width
}

trait Shape {
  def move: Int
}
trait Movable {
  def move: Int = 2
}

class Square(v: Int) extends Planar with Shape with Movable {
  override def height = v
  override def width = v
}
new Square(5).move