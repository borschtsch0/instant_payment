/**
 * Код Хаффмана
 * https://ru.wikipedia.org/wiki/%D0%9A%D0%BE%D0%B4_%D0%A5%D0%B0%D1%84%D1%84%D0%BC%D0%B0%D0%BD%D0%B0
 *
 */
object Huffman {

  /**
   * Код Хаффмана представлен бинарным деревом.
   *
   * Каждый `Leaf` дерева содержит символ, который может быть закодирован.
   * weight - частота использования символа char
   *
   * Узел `Fork` состоит из chars - списка символов, которые он кодирует и weight - суммы весов дочерних узлов
   */
  abstract class CodeTree

  case class Fork(left: CodeTree, right: CodeTree, chars: List[Char], weight: Int) extends CodeTree

  case class Leaf(char: Char, weight: Int) extends CodeTree


  // 1. Базовые методы
  def weight(tree: CodeTree): Int = tree match { // tree match ...
    case Leaf(_, weight) => weight
    case Fork(_, _, _, weight) => weight
  }

  def chars(tree: CodeTree): List[Char] = tree match { // tree match ...
    case Leaf(char, _) => List(char)
    case Fork(_, _, chars, _) => chars
  }

  def makeCodeTree(left: CodeTree, right: CodeTree) =
    Fork(left, right, chars(left) ::: chars(right), weight(left) + weight(right))


  // 2. Генерация дерева Хаффмана

  /**
   * Преобразует строку в список символов
   */
  def string2Chars(str: String): List[Char] = str.toList

  /**
   * Данная функция подсчитывает количество уникальных символов в списке. Например
   *
   * times(List('a', 'b', 'a'))
   *
   * Должна вернуть следующий список (порядок не важен)
   *
   * List(('a', 2), ('b', 1))
   *
   * Список `List[(Char, Int)]` состоит из пар (кортежей), каждая из которых состоит из символа и числа (вес)
   *
   * val pair: (Char, Int) = ('c', 1)
   *
   * Для обращения к элементам кортежа можно использовать `_1` and `_2`:
   *
   * val theChar = pair._1
   * val theInt  = pair._2
   *
   * Либо pattern match
   *
   * pair match {
   * case (theChar, theInt) =>
   * println("character is: "+ theChar)
   * println("integer is  : "+ theInt)
   * }
   */
    def times(chars: List[Char]): List[(Char, Int)] = {
      val temp = for {o1 <- chars} yield (o1, List(o1).length)
      temp.groupBy(_._1).map { case (char, quant) => char -> quant.length }.toList
    }
//      for {
//    o1 <- chars
//    o2 <- chars if o2 == o1
//  } yield (o2, List(o2).length)

  /**
   * Возвращает список листьев дерева для частотной таблицы `freqs`
   *
   * Возвращаемый список должен быть отсортировано по возрастанию весов.
   */
  def makeOrderedLeafList(freqs: List[(Char, Int)]): List[Leaf] = {
    val temp = freqs.sortBy(_._2)
    for {o1 <- temp} yield Leaf(o1._1, o1._2)
  }

  /**
   * Проверяет состоит ли дерево `trees` из одного элемента
   */
  def singleton(trees: List[CodeTree]): Boolean = trees.length == 1

  /**
   * Параметр `trees`список деревьев отсортированный по возрастанию веса
   *
   * Данная функция берет первые два элемента списка `trees`, комбинирует
   * их в узел `Fork` и возвращает список с остальными элементами и полученным узлом,
   * добавленного на позицию, сохраняющую упорядоченность списка по весу.
   *
   * Если `trees` содержит менее двух элементов, возвращается `trees` без изменения
   */
  def combine(trees: List[CodeTree]): List[CodeTree] = if (trees.length < 2) trees else
    (trees.tail.tail :+ makeCodeTree(trees.head, trees.tail.head)).sortBy(el => weight(el))

  /**
   *  Эта функция вызывается следующим образом:
   *
   *   until(singleton, combine)(trees)
   *
   *
   * где `trees` типа `List[CodeTree]`, `singleton` and `combine` ссылаются на соответствующие функции,
   * определенные выше.
   *
   * В данном вызове `until` должна вызывать две этих функции
   * пока список с элементами дерева не будет содержать одного элемента
   *
   * Подсказка: перед реализацией
   *  - начните с определением типов параметрам, согласно примеру вызова. Также определите тип возвращаемого значения
   *  - подберите соответствующие название параметров.
   */
  def until(sngl_func: (List[CodeTree]) => Boolean, comb_func: (List[CodeTree]) => List[CodeTree])(trees: List[CodeTree]): CodeTree = {
    if (sngl_func(trees)) trees.head else until(sngl_func, comb_func)(comb_func(trees))
  }

  /**
   * Данная функция создает дерево `CodeTree`, оптимизированное для кодировки `chars`.
   *
   */
  def createCodeTree(chars: List[Char]): CodeTree = until(singleton, combine)(makeOrderedLeafList(times(chars)))


  // 3: Расшифрование

  type Bit = Int

  /**
   * Кодирует последовательность `bits` с использоваем дерева `tree`
   */
  def decode(tree: CodeTree, bits: List[Bit]): List[Char] = {
    def helper_dec(tr: CodeTree, bits: List[Bit], msg: List[Char]): List[Char] = {
      if (bits.isEmpty) tr match {case Leaf(char, _) => msg :+ char} else {
        if (bits.head == 0) tr match {
          case Leaf(char, _) => helper_dec(tree, bits, msg :+ char)
          case Fork(left, _, _, _) => helper_dec(left, bits.tail, msg)
        }
        else tr match {
          case Leaf(char, _) => helper_dec(tree, bits, msg :+ char)
          case Fork(_, right, _, _) => helper_dec(right, bits.tail, msg)
        }
      }
    }

    val chars: List[Char] = List()
    helper_dec(tree, bits, chars)
  }



  val frenchCode: CodeTree = Fork(Fork(Fork(Leaf('s',121895),Fork(Leaf('d',56269),Fork(Fork(Fork(Leaf('x',5928),Leaf('j',8351),List('x','j'),14279),Leaf('f',16351),List('x','j','f'),30630),Fork(Fork(Fork(Fork(Leaf('z',2093),Fork(Leaf('k',745),Leaf('w',1747),List('k','w'),2492),List('z','k','w'),4585),Leaf('y',4725),List('z','k','w','y'),9310),Leaf('h',11298),List('z','k','w','y','h'),20608),Leaf('q',20889),List('z','k','w','y','h','q'),41497),List('x','j','f','z','k','w','y','h','q'),72127),List('d','x','j','f','z','k','w','y','h','q'),128396),List('s','d','x','j','f','z','k','w','y','h','q'),250291),Fork(Fork(Leaf('o',82762),Leaf('l',83668),List('o','l'),166430),Fork(Fork(Leaf('m',45521),Leaf('p',46335),List('m','p'),91856),Leaf('u',96785),List('m','p','u'),188641),List('o','l','m','p','u'),355071),List('s','d','x','j','f','z','k','w','y','h','q','o','l','m','p','u'),605362),Fork(Fork(Fork(Leaf('r',100500),Fork(Leaf('c',50003),Fork(Leaf('v',24975),Fork(Leaf('g',13288),Leaf('b',13822),List('g','b'),27110),List('v','g','b'),52085),List('c','v','g','b'),102088),List('r','c','v','g','b'),202588),Fork(Leaf('n',108812),Leaf('t',111103),List('n','t'),219915),List('r','c','v','g','b','n','t'),422503),Fork(Leaf('e',225947),Fork(Leaf('i',115465),Leaf('a',117110),List('i','a'),232575),List('e','i','a'),458522),List('r','c','v','g','b','n','t','e','i','a'),881025),List('s','d','x','j','f','z','k','w','y','h','q','o','l','m','p','u','r','c','v','g','b','n','t','e','i','a'),1486387)

  /**
   * Данную последовательность битов надо декодировать с помощью кода `frenchCode`
   */
  val secret: List[Bit] = List(1, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 0, 0, 1, 0, 1, 0, 1, 0, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 0, 1, 0, 1, 1, 0, 0, 1, 1, 1, 0, 0, 0, 1, 1, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 1, 0)

  /**
   * Возращает декодированную последовательность secret
   */
  def decodedSecret: List[Char] = decode(frenchCode, secret)

  // 4a: Простое шифрование

  /**
   * Кодирует `text` с использованием `tree`
   */
  def encode(tree: CodeTree)(text: List[Char]): List[Bit] = {
    def helper_enc(tr: CodeTree, bits: List[Bit], msg: List[Char]): List[Bit] = {
      if (msg.isEmpty) bits else tr match {
          case Fork(left, _, _, _) if chars(left) contains msg.head => helper_enc(left, bits :+ 0, msg)
          case Fork(_, right, _, _) => helper_enc(right, bits :+ 1, msg)
          case Leaf(_, _) => helper_enc(tree, bits, msg.tail)
        }
    }

    val bits: List[Bit] = List()
    helper_enc(tree, bits, text)
  }

  // 4b: Шифрование с использованием таблицы кодов

  type CodeTable = List[(Char, List[Bit])]

  def codeBits(table: CodeTable)(char: Char): List[Bit] = table.filter(el => el._1 == char).head._2

  def convert(tree: CodeTree): CodeTable = tree match {
      case Leaf(char, _) => List((char,List()))
      case Fork(left, right, _, _) => mergeCodeTables(convert(left), convert(right))
    }

  // Я так понимаю, это вспомогательный метод, который строит "пути" к буквам дерева и соединяет ветки
  def mergeCodeTables(a: CodeTable, b: CodeTable): CodeTable = {
    def helper_mct(a: (Char, List[Bit]), b: Bit): (Char, List[Bit]) = (a._1, b +: a._2)
    // 0 или 1 добавляем в начало пути, так как мы идем снизу-вверх

    a.map(el => helper_mct(el, 0)) ::: b.map(el => helper_mct(el, 1))
  }

  def quickEncode(tree: CodeTree)(text: List[Char]): List[Bit] = {
    text.map(codeBits(convert(tree))).flatten
  }
}

// проверка кода
val message = Huffman.decodedSecret
Huffman.makeOrderedLeafList(Huffman.times(message))
// формирование дерева
val c_tree = Huffman.createCodeTree(message)
// 1 способ кодировки
val enc_message = Huffman.encode(c_tree)(message)
val dec_message = Huffman.decode(c_tree, enc_message)
// 2 способ кодировки
val q_enc_message = Huffman.quickEncode(c_tree)(message)
val q_dec_message = Huffman.decode(c_tree, q_enc_message)
