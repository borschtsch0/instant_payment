val oppositeDirs = Map(
  "NORTH" -> "SOUTH",
  "EAST" -> "WEST",
  "SOUTH" -> "NORTH",
  "WEST" -> "EAST"
)
def isOpposite(one: String, other: String): Boolean = oppositeDirs(one).equals(other)

def dirReduc(arr: Array[String]): Array[String] = {
  def helper(mass1: Array[String], mass2: Array[String]): Array[String] = {
    if (mass1.isEmpty == true) mass2

    else if (mass2.length >= 1)
      if (isOpposite(mass2.last, mass1.head)) helper(mass1.tail, mass2.init)
      else helper(mass1.tail, mass2 :+ mass1.head)

    else if (mass1.length >= 2)
      if (isOpposite(mass1.head, mass1.tail.head)) helper(mass1.tail.tail, mass2)
      else helper(mass1.tail, mass2 :+ mass1.head)

    else helper(mass1.tail, mass2 :+ mass1.head)
  }

  val checking: Array[String] = Array()
  helper(arr, checking)
}

// val arr = Array("NORTH", "SOUTH", "SOUTH", "EAST", "WEST", "NORTH", "WEST")
//arr.head  // первый элемент
//arr.tail  // все кроме первого
//arr.last  // последний элемент
//arr.init  // все кроме последнего
//val empty = Array() // создание пустого
//val newarr = arr :+ "EAST" // добавление элемента в конец массива
//val newarr2 = "WEST" +: arr // добавление элемента в начало массива

// dirReduc(Array("NORTH", "SOUTH", "SOUTH", "EAST", "WEST", "NORTH", "WEST")) == Array("WEST")
dirReduc(Array("NORTH", "SOUTH", "SOUTH", "EAST", "WEST", "NORTH", "WEST"))
dirReduc(Array("NORTH", "EAST", "SOUTH", "WEST", "WEST"))