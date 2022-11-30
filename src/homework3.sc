val oppositeDirs = Map(
  "NORTH" -> "SOUTH",
  "EAST" -> "WEST",
  "SOUTH" -> "NORTH",
  "WEST" -> "EAST"
)
def isOpposite(one: String, other: String): Boolean = oppositeDirs(one).equals(other)

def dirReduc(arr: Array[String]): Array[String] = {
  def helper(mass1: Array[String], mass2: Array[String]): Array[String] = {
    if (mass1.length == 1 && mass2.nonEmpty) {
      val empty: Array[String] = Array()
      if (mass2.last == mass1.head) helper(empty, mass2)
      else if (isOpposite(mass2.last, mass1.head)) helper(empty, mass2.init)
      else helper(empty, mass2 :+ mass1.head)
    }
    else if (mass2.length > 1 && isOpposite(mass2.head, mass2(1))) helper(mass1, mass2.tail.tail)
    else if (mass1.isEmpty) mass2
    else if (isOpposite(mass1.head, mass1(1))) helper(mass1.tail.tail, mass2)
    else helper(mass1.tail, mass2 :+ mass1.head)
  }

  val checking: Array[String] = Array()
  helper(arr, checking)
}

// dirReduc(Array("NORTH", "SOUTH", "SOUTH", "EAST", "WEST", "NORTH", "WEST")) == Array("WEST")
dirReduc(Array("NORTH", "SOUTH", "SOUTH", "EAST", "WEST", "NORTH", "WEST"))
dirReduc(Array("NORTH", "EAST", "SOUTH", "WEST", "WEST"))
dirReduc(Array("SOUTH", "SOUTH"))