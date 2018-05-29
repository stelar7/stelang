# Variables

```
All variables are class private
Variables without a keyword are dynamic by default
```

## Initialization
```
Int:value = 31;
```

## Constants
```
Constant variables are defined with a prefixed 'const' keyword

const num = 31;
const txt = "abd";

num = 31 // ok, since the value is known at compile time, and is the same
num = 1; // compile error
```

## Dynamic
```
Dynamic variables are defined with a prefixed 'val' keyword

val num = 31;
val txt = "abd";

num = 31 // ok
num = 1; // ok
```