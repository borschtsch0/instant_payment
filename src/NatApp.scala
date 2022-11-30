object NatApp extends App {
  // Натуральные числа
  abstract class Nat{
    // является ли нулем
    def isZero: Boolean
    // возвращает предыдущее натуральное число
    def predecessor: Nat
    // возвращает следущее натуральное число
    def successor: Nat = new Succ(this)
    // возвращает результат сложения с that
    def +(that: Nat): Nat
    // возвращает результат вычитания that
    def -(that: Nat):Nat
  }

  object Zero extends Nat{
    // является ли нулем
    override def isZero: Boolean = true

    // возвращает предыдущее натуральное число
    // Zero является началом исчисления
    override def predecessor: Nat = throw new Error()

    // возвращает результат сложения с that
    override def +(that: Nat): Nat = that

    // возвращает результат вычитания that
    // отрицательные числа невозможны в данном контексте
    override def -(that: Nat): Nat = if (that == Zero) this
    else throw new Error()
  }

  case class Succ(n: Nat) extends Nat {
    // является ли нулем
    override def isZero: Boolean = false

    // возвращает предыдущее натуральное число
    override def predecessor: Nat = n

    // возвращает результат сложения с that
    override def +(that: Nat): Nat = if (that.isZero) this
    else this.successor.+(that.predecessor)

    // возвращает результат вычитания that
    override def -(that: Nat): Nat = if (that.isZero) this
    else this.predecessor.-(that.predecessor)
  }

  val _1 = Succ(Zero)
  val _2 = Succ(Succ(Zero))

  println(_1)
  println(_2)

  println(_1 + _2)
  println(_2 - _1)
  println(_2.isZero)
  println(Zero.isZero)
}
