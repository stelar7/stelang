# Default classes


## Bool
```
.toggle()
.TRUE
.FALSE
```

## Text
```
.at(int)
.concat(text)
.contains(text)
.endsWith(RegEx)
.equalsIgnoreCase(text)
.format(text...)
.indexOf(text)
.replace(text, text)
.reverse()
.repeat(int)            // repeats the text int times
.substring(int)
.substring(int, int)
.startsWith(RegEx)
.size()
.stretch(int)           // repeats the text untill its int characters long (might also shorten it)
.split(RegEx)
.trim()
.toUpperCase()
.toLowerCase()
.toTitleCase()
```

## Num
```
.INFINITY
.isInfinite()
.oneBits()
.totalBits()
.flipBits()
.leadingZeroBits()
.trailingZeroBits()
.toHex()
.toOctal()
.toBinary()
.highestOneBit()
.sign()
.rotateLeft()
.rotateRight()
```

## Float
```
.INFINITY
.isInfinite()
.isNaN()
.toHex()
.toOctal()
.toBinary()
```

## Class
```
.type()
```

## Enum
```
.name()
.ordinal()
.from(object)
```

## System
```
.output(text)
.outputFormat(text, params)

.input()

.millis()
```
## Math
```
.PI
.TAU
.E

.asin(a)
.acos(a)
.atan(a)
.atan2(a)
.sin(a)
.cos(a)
.tan(a)
.hypot(a, b)

.abs(a)
.ceil(a)
.floor(a)
.round(a)

.max(a...)
.min(a...)

.sign(a)

.log(a)
.exp(a)

.pow(a, b)

.sqrt(a)
.cbrt(a)
```

## Random
```
.num()          // random number between Num.min and Num.max 
.numP()         // random number between 0 and Num.max
.numN()         // random number between Num.min and -0 
.numP(max)      // random number between 0 and max
.numN(min)      // random number between min and -0
.num(min, max)  // random number between min and max

.nums()         // infinite stream of numbers 
.numsP()
.numsN()
.numsP(max)
.numsN(min)
.nums(min, max)


.text()
.text(length)

.texts()
.texts(length)


.float()
.floatP()
.floatN()
.floatP(max)
.floatN(min)
.float(min, max)

.floats()
.floatPs()
.floatNs()
.floatPs(max)
.floatNs(min)
.floats(min, max)


.bool()
.bools()
```

## List
```
<Type>
.add(a...)
.add(list)

.contains(a...)
.contains(list)

.of(a...)

.distinct()
.filter(predicate)
.map(function)
.reduce(start_value, function)

.get(index)
.index(object)

.isEmpty()
.remove(index)
.retain(list)

.size()

.sort(comparator)
.sublist(from, to)

.asStream()
.asArray()
```

## Stream
```
<Type>
.allMatches(predicate)
.anyMatches(predicate)
.noneMatches(predicate)

.count()

.distinct()
.filter(predicate)
.map(function)
.reduce(start_value, function)
.sorted(comparator)

.generate(supplier)
.of(a...)

.peek()
.first()
.isEmpty()
.min(comparator)
.max(comparator)

.limit(count)
.skip(count)

.toList()
```

## Map
```
<Key, Value>
.of(key..., value...)

.get(key)
.getDefault(key, default)

.put(key, value)
.put(map)

.hasKey(key)
.hasValue(value)

.isEmpty()

.keys()
.values()

.putIfPresent(key, value)
.putIfAbsent(key, value)

.remove(key)
```

## Json
```
.read(text)
.read(path)

.get(text)
.set(text, value)

.asText()
.asNum()
.asFloat()
.asBool()
.asArray()

.toText()

.write(path)
```


## IO
```
// Path
.parse(text)

.write(text)
.write(num[]) // writes the numbers as binary, so 255 is written as the raw byte 11111111
.read()

.parent()
.resolve()
.filename()
```

####RandomAccessReader
```
.readBoolean()
.readBytes(num)
.readnum()
.readText(num)  // read num bytes of text
.readText()     // reads the remaining of the reader as a text string



```

## HTTP
```
// URL
.create(text)

.connect()
.delete()
.get()
.head()
.options()
.patch(text, text)
.put(text, text)
.post(text, text)
.trace()

.websocket()

// URLConnection
.responseCode()
.responseBytes()
.responseText()
.headers()

// Websocket
.state()
.close()
.send(text)
.onMessage(function)
```

##Vectors
```
// Vector2
<X, Y>
.x()
.y()


// Vector3
<X, Y, Z>
.x()
.y()
.z()

// Vector4
<X, Y, Z, W>
.x()
.y()
.z()
.w()
```


## Time
```
// DateTime
.now()
.of(instant)
.ofEpoch(num)

.get(field)
.set(field, value)
.plus(field, value)
.minus(field, value)

.isBefore(datetime)
.isBefore(instant)
.isAfter(datetime)
.isAfter(instant)


// Instant
.EPOCH
.now()

.epochMillis()
.epochSeconds()

.set(field, value)
.plus(field, value)
.minus(field, value)


// Duration
.ZERO
.of(field, value)
.as(field)

.nanos()
.millis()
.seconds()
.hours()
.days()

.set(field, value)
.plus(field, value)
.minus(field, value)
.multiplied(float)
.divided(float)
.negated()


// TimeField
MICRO
NANO
MILLI
SECOND
MINUTE
HOUR
DAY
WEEK
MONTH
YEAR
CENTURY
MILLENNIA
FOREVER
```


## RegEx
```
.create(text)

.groups()
.matches()
.pattern()
```


## Crypto
```
.to/from

A1Z26
Base64
Caesar
Vigenere


```
