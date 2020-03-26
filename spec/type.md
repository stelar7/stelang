# Types

## Primitives
```
Text
Int
Float
Bool
null
[] // array 
```

### Text
```
"this is some text"

"""
this is some
multiline
text
that only end after the quotes
"""
```

### Int  
```
Ints are signed two's compliment numbers based on their prefix
```

```
5
32
86546

// numbers can be separated with _
100_000_000

// 0x prefixes hex digits
0x0F
0x0FADCD13

// 0b prefixes binary digits (leading 0s are ignored)
0b1 // 1
0b0000001 // 1

0b100 // 4
0b0000100 // 4

0b1000000 // 64
0b00000001000000 // 64 

```

### Float  
```
Floats are ints with decimals
```

```
32.5
5.0000001
86546412.6549684316574
```

### Bool
```
A truthy or falsy value, as set by its constants;
TRUE;
- true
- Any number over 0
- Any object that is not null
- !null 

FALSE;
- false
- Any number 0 or below
- null 
```

### Null
```
Special value denoting emptyness;
null
```

### Array
```
//  [1, 2, 3]
val     arr = [1, 2, 3];
Int[]:  arr = [1, 2, 3];
val     arr = 1..3;

// [0, 1, 2, 3]
val     arr = ..3;

// first element
arr[0]
// second element
arr[1]

// last element
arr[-0]
// second last element
arr[-1]
```
