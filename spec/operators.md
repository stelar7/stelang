# Operators

## Comparison
```
== Equals
!= Not Equals
> Left greater than right
>= Left greater than or equal to right
< Right greater than left
=< Right greater than or equal to left
<=> Comparison (-1 if less, 0 if equal, 1 if greater)
```

## Arithmetic

Arithmatic operators allow overflow

```
+ Addition
- Subtraction
* Multiply
/ Divide
% Remainder
```
```
Arithmatic operators have an implicit 0 prefix;
this allows you to do:

+5 == 0 + 5 == 5
-5 == 0 - 5 == -5
```

```
You can do pre- and postfix operations on variables and constants

++i prefix increment
i++ postfix increment
--i prefix decrement
i-- postfix decrement

{
    i = 0;
    i = i++ + ++3; 
    // 3 + 1 + i;
    // i + 1
    i == 5;
}

```

## Logic
```
&& And 
|| Or
! Not
= Set
```

## Bitwise
```
& And
| Or
^ Xor

<< Shift left
>> Shift right
```


All operators can be prefixed with `=` to set the value on the left param.
```
a === b  // a = (a == b)
a =!= b  // a = (a != b)
a => b   // a = (a > b)
a =>= b  // a = (a >= b)
a =< b   // a = (a < b)
a =<= b  // a = (a <= b)
a =<=> b // a = (a <=> b)

a =+ b  // a = (a + b)
a =- b  // a = (a - b)
a =* b  // a = (a * b)
a =/ b  // a = (a / b)
a =% b  // a = (a % b)

a =&& b  // a = (a && b)
a =|| b  // a = (a || b)
a =! b  // a = (a && !b)

a =& b  // a = (a & b)
a =| b  // a = (a | b)
a =^ b  // a = (a ^ b)
a =>> b  // a = (a >> b)
a =<< b  // a = (a << b)
```


## Operator overloading
Operators can be set on your own classes with the following syntax;
```
operator+ (MyClass:left, other_class: right): Int {
    return left.some_field + right.other_field;
}
```


You can set special operators aswell
```
operator$ (MyClass: left, some_object: right): Text {
    return "cash money yoooooo";
}
```