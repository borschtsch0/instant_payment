// ЗАДАНИЕ 1

// Подсказка: потребуется сделать вспомогательную функцию
// с дополнительным параметром - "аккумулятором",
// содержащего результат предварительного расчета
def factorial(n: Int): Int = {
  def helping(a: Int, acc: Int): Int = {
    if (a <= 1) acc
    else helping(a - 1, a * acc)
  }

  helping(n, 1)
}

factorial(5)

// ЗАДАНИЕ 2
// Функция должна возвращать значение из треугольника
// Паскаля по координатам c и r - колонка и строка,
// соответсвенно, индексация с 0
def pascal(c: Int, r: Int): Int = factorial(r)/(factorial(c)*factorial(r-c))

pascal(0,0)
pascal(1,2)
pascal(5,10)

// ЗАДАНИЕ 3
// Требуется написать функцию проверки
// корректности расстановок скобок в выражении chars.
/* Примеры:
 * "(if (zero? x) max (/ 1 x))" - корректное выражение
 * "(-:-)$-)(-%" - некорректное
*/

var sentence1: String = "(if (zero? x) max (/ 1 x))"
var sentence2: String = "(-:-)$-)(-%"

def balance(chars: List[Char]): Boolean = {
  // Ввожу переменную-индикатор, которая показывает некорректность скобок
  var a:Int = 0
  val brackets=chars.filter((n: Char) => n=='(' | n==')')
  for (n <- brackets)
  brackets.foreach((n: Char) => (if (a<0) return false else if (n=='(') a=a+1 else a=a-1))
  if (a==0) true else false
}

// P.s. Немного объясню свою логику - значение индикатора не может быть отрицательным
// Это ситуация - [ (,),) ]
// Последний if в методе - проверка на то, что все скобки были закрыты

balance(sentence1.toList)
balance(sentence2.toList)
