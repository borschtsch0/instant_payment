abstract class IntSet {
  def value: Int
  def contains(e: Int): Boolean
  def include(e: Int): IntSet
  def union(e: IntSet): IntSet
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

  override def union(other: IntSet): IntSet = {
    ((left union right) union other) include value
  }
}

object Empty extends IntSet {
  override def value = throw new Error("Пустое множество пусто")
  override def contains(e: Int) = false
  override def include(e: Int) = Element(e, Empty, Empty)
  override def union(e: IntSet): IntSet = e
}

val one = Element(3, Empty, Empty)
val two = one.include(1)
val three = Element(4, two, Empty)
val four = three.include(8)
val five = four.include(6)
val six = five.include(9)
six.contains(1)
six.contains(2)
six.contains(3)
six.contains(9)
six.contains(4)
six.contains(6)

val ein = Element(3, Empty, Empty)
val zwei = ein.include(2)
val drei = zwei.include(6)
drei.contains(1)
drei.contains(2)
drei.contains(3)
drei.contains(9)
drei.contains(4)
drei.contains(6)

val set1 = six.union(drei)
set1.contains(1)
set1.contains(2)
set1.contains(3)
set1.contains(9)
set1.contains(4)
set1.contains(6)

1 == 1
val a: List[Int] = List(1, 2, 3, 4, 5)

a.tail
a
a.tail.head
a

val chars: List[Char] = List('b', 'r', 'i', 'l', 'l', 'i', 'a', 'n', 't', 'w', 'o', 'r', 'k')
val temp = for {o1 <- chars} yield (o1, List(o1).length)
val temp1 = temp.groupBy(_._1).map {case (char, quant) => char -> quant.length}.toList.sortBy(_._2)
for {o1 <- temp1} yield (o1._1, o1._2)