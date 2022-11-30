// 2. Иерархия классов в Скала
// packages
// imports
// Автоматические импорты
// ▶ import scala
// ▶ import java.lang
// ▶ import scala.Predef
// http://www.scala-lang.org/files/archive/api/current



val i: Int = 1
val s: String  = "ssdfdf"
val i2 = {7 + 8}
def j: Int = ???
val res = if ( true ) 1 else false
def foo: Unit = {}
val u: Unit = ()
def error: Nothing = throw new Error
val uu: Any = ()
//val vi: Int = ()
def k: Int = throw new Error
def nothing: Nothing = throw new Error


class MyClass /*extends AnyRef*/


trait MyList[+T]
case class Cons[T](val head: T, val tail: MyList[T]) extends MyList[T]
object Nil extends MyList[Nothing]

Cons(1, Nil)
Cons(1, Cons(2, Nil))
Cons("1", Cons(2, Nil))


trait Planar
class Triangle extends Planar
class Square extends Planar
class Circle extends Planar

Cons[Planar](new Circle, Nil)
Cons(new Circle, Cons(new Square, Nil))

// T1 <: T2
// Triangle <: Planar

val p: Planar = new Triangle

// F[T]

// ковариантость
// F[T1] <: F[T2]


val triangles: MyList[Triangle] = Cons(new Triangle, Nil)
val planars: MyList[Planar] = Cons(new Triangle, Cons(new Square, Nil))


type TriangleList = MyList[Triangle]
type PlanarList = MyList[Planar]
// PlanarList >: TriangleList

val l2: TriangleList = triangles
val l3: PlanarList = triangles
val l4: PlanarList = planars
//val l5: TriangleList = planars

// Planar >: Triangle
// MyList[Planar] >: MyList[Triangle]

// контравариантность
// F[T1] >: F[T2]

type CI = Circle => Int
type PI = Planar => Int

def perimeterCircle: CI = circle => ???
def perimeterPlanar: PI = planar => ???

val a1: CI = perimeterCircle
val a2: CI = perimeterPlanar
val a3: PI = perimeterPlanar
//val a4: PI = perimeterCircle

// PI <: CI
// F[Planar] <: F[Circle]
// Planar >: Circle

trait Function1[-T, +U] {
  def apply(x: T): U
}