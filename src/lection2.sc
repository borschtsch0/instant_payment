import scala.annotation.tailrec

def square(x: Int) = x * x
def factorial(n: Int): Int = {
  @tailrec
  def factorialInt(n: Int, acc: Int): Int = if (n < 1) acc else factorialInt(n - 1, n * acc)
  factorialInt(n ,1)
}
{
  def sum(x: Int): Int = if (x == 0) 0 else sum(x - 1) + x
  sum(1)
  sum(2)
  sum(3)

  def sumSquare(x: Int): Int = if (x == 0) 0 else sum(x - 1) + square(x)
  def sumFact(x: Int): Int = if (x == 0) 0 else sum(x - 1) + factorial(x)
}
{
  def sum(map: Int => Int, x: Int): Int = if (x == 0) 0 else sum(map, x - 1) + map(x)
  sum(a => a, 1)
  sum(square, 2)
  def sumSquare(x: Int) = sum(square, x)
  def sumFact(x: Int) = sum(factorial, x)
}


{
  def sum(f: Int => Int)(x: Int): Int = if (x == 0) 0 else sum(f)(x - 1) + f(x)
  print(sum(a => a)(1))
  sum(square)(2)
  def sumSquare: Int => Int = sum(square)
  def sumFact: Int => Int = sum(factorial)
  sumSquare(3)
}

def product(map: Int => Int, x: Int): Int = if (x == 1) 1 else product(map, x - 1) * map(x)

def mapReduce(combine: (Int, Int) => Int, init: Int)(map: Int => Int)(x: Int): Int = {
  if (x == 1) init else combine(mapReduce(combine, init)(map)(x - 1),  map(x))
}
4

val sum: (Int => Int) => (Int => Int) = mapReduce((a,b) => a + b, 0)
def product: (Int => Int) => (Int => Int) = mapReduce((a,b) => a * b, 1)


def sumSquare: Int => Int = sum(square)
// def sumSquare2(x: Int) = mapReduce((a,b) => a + b, 0, square, x)
def productSquare: Int => Int = sum(square)
def sumFact: Int => Int = sum(factorial)
def productFact: Int => Int = sum(factorial)

sumSquare(2)
//[1,2,3]

Set(1,3,2)
type Set = Int => Boolean
val один: Set = x => x == 1
val ОдинПять: Set = x => x >= 1 && x <= 5
один(1)
один(2)