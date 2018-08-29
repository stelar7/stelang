# Classes


```
class MyClass {
    function myFunction() {
        System.output("Hello World!");
    }
}
```


Classes can have generic parameters
```
class <X, Y> myClass 
{
    function add(X:x, Y:y) 
    {
        return x + y;
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

enum MyEnum4 {
    A(2), B(5), C(8), D(2);
    MyEnum4(Int:value);
}

enum MyEnum5 extends MyEnum4 {
    E(2), F(5), G(8), H(2);
}

enum MyEnum6 extends MyEnum4 {
    E("asd"), F("asd"), G("asd"), H("ASd");
    
    MyEnum6(Text: value) 
    {
        super(2);
    }
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
        MyEnum.from(2);         //returns MyEnum.C;
        MyEnum2.from(8);        //retunrs MyEnum2.C;
        MyEnum3.from("test");   //retunrs MyEnum3.C;
        MyEnum4.from(2);        //retunrs MyEnum4.A; because it matches by param, then ordinal
    }
}
```