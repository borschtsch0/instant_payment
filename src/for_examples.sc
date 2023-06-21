val a = collection.mutable.Map[Int, Int]()

println(a)

a.put(1, 0)
a.put(2, 100)
a.put(3, 1000)

val p = a.get(1)

p
println(a)

a.put(1, 50)

p
println(a)

