{
  class Rational(x: Int, y: Int) {
    def gcd(a: Int, b: Int): Int = if (b == 0) a else gcd(b, a % b)

    val g = gcd(x, y)
    val numer = x / g
    val denom = y / g

    def add(r: Rational): Rational = new Rational(this.numer * r.denom + this.denom * r.numer, this.denom * r.denom)

    def mul(r: Rational): Rational = new Rational(this.numer * r.numer, this.denom * r.denom)

    def neg: Rational = new Rational(-numer, denom)

    def sub(r: Rational) = this.add(r.neg)

    def less(that: Rational) = numer * that.denom < denom * that.numer

    override def toString: String = if (numer == 0) "0" else numer + "/" + denom
  }

  new Rational(1, 2)
  new Rational(1, 2).sub(new Rational(1, 2))
  new Rational(1, 2).add(new Rational(1, 3))
  new Rational(1, 4).add(new Rational(1, 4))
  new Rational(1, 4) add new Rational(1, 4)

  //    new Rational(1, 2) + (new Rational(1, 3))

  val r = new Rational(1, 2)
  r
}

class Rational(x: Int, y: Int) {
  def gcd(a: Int, b: Int): Int = if (b == 0) a else gcd(b, a % b)

  val g = gcd(x, y)
  val numer = x / g
  val denom = y / g

  def +(r: Rational): Rational = new Rational(this.numer * r.denom + this.denom * r.numer, this.denom * r.denom)

  def *(r: Rational): Rational = new Rational(this.numer * r.numer, this.denom * r.denom)

  def unary_- = new Rational(-numer, denom)

  def -(r: Rational) = this + -r

  def <(that: Rational) = numer * that.denom < denom * that.numer

  override def toString: String = if (numer == 0) "0" else numer + "/" + denom
}

val a = new Rational(1,2)
val b = new Rational(1,4)



a.+(b)
//infix notation
a + b
a - b
a < b


val x = new Rational(1, 6)
val y = new Rational(5, 6)
val z = new Rational(3, 2)

x + z * x
y + z * x < y
x < y
//x.neg + y
-x + y

/*
Типы идентификаторы в скала
  * Буквенно-цифровые
  * Символические
  * _
  * Буквенно-цифровые_символические
x1  ++   +%:?=  v2_++ v3_=
*/

val + = 1
+

val +%:?= = 5
+%:?= + +



  (a * b) + b
b + ( a * b )
b.+(a).*(b)


/*
Приоритеты инфиксных операторов по возрастанию согласно первому символу
(all letters)
|
^
&
< >
= !
:
+ -
* / %
(all other special characters)
 */

(y + (z * x)) < y
val c = 0
val d = 0


((a + b) ^? (c ?^ d)) less ((a ==> b) | c)