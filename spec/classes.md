# Classes


```
class MyClass {
    function myFunction() {
        System.output("Hello World!");
    }
}
```


Enums are classes with `pure` variables
```
enum MyEnum {
    A, B, C, D;
}

enum MyEnum2 {
    A(2), B(5), C(8), D(10);
    MyEnum2(Int:value);
}

enum MyEnum3 {
    A(RegEx("abc")), B(RegEx("ace")), C(RegEx("test")), D(RegEx("abcd"));
    MyEnum2(RegEx:value);
}


class UseEnum {
    function test() {
        // return a reference to the enum object
        MyEnum.A;
        MyEnum2.A;
        
        // return a value from the enum
        MyEnum.A.ordinal();  // the index of the item in the class
        MyEnum2.A.value;     // the parameter named value from the constructor
        
        
        // Create a reference from a value
        MyEnum.from(2); returns MyEnum.C;
        MyEnum2.from(8); retuns MyEnum2.C;
        MyEnum3.from("test"); retuns MyEnum3.C;
    }
}
```