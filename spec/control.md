# Control flow

## if
```
if (boolean) {} 
else if (boolean) {}
else {}


const b = true;
if(b)

const x = 1, y = 2, z = 3; 
if(x < y < x)

const o = object();
if(o)

const n = null;
if(!n)
```

## for
```
for (initialization; comparison; increment) {} 
then {}

for (Type t : container) {} 
then {}


for (val i = 0; i < 100; i++)
for (Int i : Stream.of(1,2,3))
for (Int i : Stream.of(1..100))
for (Text c : "Some text")
for ({Text key, Text value} : Map.of("key", "value"))

val myList = List.of(MyClass());
for (MyClass o : myList)


for (val i = 0; i < 100; i++) 
{
    // some stuff
} then {
    Console.output("Done counting to 100");
}

for (val i = 0; i < 100; i++) 
{
    if (i > 50) break;
} then {
    Console.output("We stopped at 50 this time");
}
```

## while
```
while(comparison) {}
then{}

do {} while(comparison) 
then {}


while (true) {}
while(i++ < b) {}

do { a++; } while(a < b);

```

### Special keywords in FOR and WHILE
```
// continue;
Stops the current iteration, and goes on to the next one

// break;
Exits the loop
```

### Special keywords after FOR and WHILE
```
// then {}
Always ran after the end of the loop
```


## switch
```
switch(condition) {
    case test: {
        // code
    };
    
    // default is added automatically if not specified
    default: {
        // code
        // implied return null
    };
}


// these all match, but only the first will hit
val a = "test";
switch(a) {
    case "test": {
        Console.output(a);
    };
    
    case Text: {
        Console.output(a);
    };
    
    case RegEx("test"): {
        Console.output(a);
    };
    
    case {Text: "test"}: {
        Console.output(a);
    };
}

val b = switch(a) {
    case "test": {
        return "b value";
    };
}


val c = ..3;
switch(c) {
    case ..3: {
        // this matches because its the same value
    };
    
    case [0, 1, 2, 3]: {
        // this matches because its the same value
    };
}

switch (c) {

    // Int or "testy"
    case {Int, "testy"}: {
        Console.output(a);
    };
    
    // Float or "test"
    case {Float, "test"}: {
        Console.output(a);
    };
}
```