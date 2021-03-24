var z = "Hallo "
val b = z
var c = b
z = z.strip()

System.out.println(b)
System.out.println(System.identityHashCode(b))
System.out.println(c)
System.out.println(System.identityHashCode(c))
System.out.println(System.identityHashCode(z))