# Functions

All functions are prefixed with the `function` keyword 
```
function name(type: name, type2: name2): return_type {
    // code
}
```
Functions can have generic parameters (see classes.md)
```
function name(T: name): T {
    // code
}
```

All functions are class private unless prefixed with the `global` keyword  
Functions prefixed with the pure keyword have global set implicitly, and does not have access to class members
```

class A {
    const five = 5;

    function add(Int: a, Int: b):Int {
        return a + b;
    }
    
    global function add2ToFive(Int: a):Int {
        return five + 2;
    }
    
    pure function add3(Int: a):Int {
        return a + 3;
    } 
}

class B {
    A.add2ToFive(2);
    A.add3(2);
}
```