# h0rsescript

**h0rsescript** is a dynamically-typed and interpreted, and trauma-inducing programming language designed to make simple tasks extremely complicated. It's a joke language, making fun of the absurdity of the purely-functional programming in languages like Haskell (hence, the name `HS` - file extension for Haskell files). It also incorporates boilerplate *features* that Java has. It's written in Kotlin (obviously) and open-source.

---

## **Table of Contents**

- [Getting Started](#getting-started)
- [Features](#features)
- [Basic Syntax](#basic-syntax)
    - [Function Calls](#function-calls)
    - [Variable Declaration](#variable-declaration)
    - [Control Structures](#control-structures)
    - [Functions](#functions)
    - [Error Handling](#error-handling)
    - [Input/Output](#inputoutput)
- [Modes](#modes)
- [Examples](#examples)
- [History](#history)
- [Contributing](#contributions)
- [License](#license)

---

## **Getting Started**

### **Installation**
1. Clone the repository:
   ```bash
   git clone https://github.com/FlamingH0rse/h0rsescript.git
   cd h0rsescript
   ```
2. Build the interpreter:
   ```bash
   ./gradlew build
   ```
3. Run the interpreter:
   ```bash
   ./build/bin/native/releaseExecutable/hs.kexe <file_name>
   ```

---

## **Features**

- **Memory unsafe**: There's no garbage collection, so every variable you create is stored directly in a list.
- **Function datatype**: Able to pass a function as an object.
- **Modes**: Choose between `Static` or `Dynamic` typing, and more to come.
- **Ownership and referencing**: Able to pass around variables as a reference.
- **Module control**: Choose which packages to use in your code using `$include`. You can even choose to exclude the root `hs` module from your function.

---

## **Basic Syntax**

### **Function Calls**
```py
# This is a comment

function_name [parameter1, parameter2, parameter3, ...]
```
### **Variables**

There are 5 main data types in h0rsescript: `NUM` , `STR`, `BOOL`, `ARRAY`, `FUN` and a `NULL` type.<br>
Variables are modified using 1 of 5 [Assignment Operators](#assignment-operators), and the `data` method:
```py
$define main
$include hs, console
    my_number -> data [652.03]                             # NUM type
    my_string -> data ["Hello World"]                      # STR type
    my_boolean -> data [TRUE]                              # BOOL type
    my_array -> data [{1, 3, "PLUH", TRUE, my_boolean}]    # ARRAY type
    
    # Create a constant
    my_constant <-> data ["password123"]
    
    # Edit the my_string variable's value
    my_string > data ["Goodbye World"]
    
    # Delete my_string variable
    <- my_string
    
    # Empty my_number and my_boolean, i.e, set their values as null
    < my_number, my_boolean
    
    console.write_line [my_boolean] # Prints null
    
    console.write_line [my_string]  # Throws ReferenceError

$end
```
The `data` method is provided by the root `hs` library which contains essential methods.<br>
Internally, the `data` method just returns the value passed to it to allow for variable creation.<br>
`fun data(x) = x`<br>
You can still choose to exclude the `hs` library from your scope, if you wish to.
#### Assignment Operators
| SYMBOL |   TYPE   |
|:------:|:--------:|
|   \>   |   EDIT   |
|   ->   | VARIABLE |
|  <->   | CONSTANT |
|   <-   |  DELETE  |
|   <    |  EMPTY   |

---

### **Control Structures**

#### **Conditionals**
The conditional methods are provided by the `conditionals` library.
```py
$define main
$include hs, conditionals

    # Create a function to run, if a certain condition is true
    $define my_function
    $include console
        console.write_line [":my_number is greater than 10"]
    $end

    my_number -> data [14]
    more_than_ten -> conditionals.more_than [my_number, 10]
    conditionals.run_if [more_than_ten, my_function, {}]
$end
```
There also exists a `conditionals.run_if_else` method.

#### **Loops**
There are no actual *loops* in h0rsescript, you can achieve similar behaviour through recursively running a function.<br>
See [examples](examples) for a better understanding.

```py
$define main
$include hs
    i -> data [0]
    my_array -> data [{"banana", "apple", "dog"}]

    $define for_loop_example
    $include math, conditionals, array, console
    $parameters my_array
        element -> array.get [my_array, i]

        console.write_line [ "Array element at index :i is :element" ]

        # i++
        i > math.add [i, 1]

        array_length -> array.length [my_array]

        # i < array.length [my_array]
        is_less_than -> conditionals.less_than [i, array_length]
        conditionals.run_if [is_less_than, for_loop_example, {}]
    $end

    # Begin the loop
    for_loop_example [my_array]
$end
```

---

### **Functions**

```py
$define main
$include console
    
    greeting -> greet ["FlamingH0rse"]
    console.write_line [greeting]
    
$end

$define greet
$include hs
$parameters name
    result -> data ["Hello :name!"]
    $return result
$end
```

---

### **Error Handling**

Error handling might be added soon.

---

### **Input/Output**

Input and output are simple:
```py
$define simulate_input_output
$include console
    user_name -> console.read_line ["Enter your name:"]
    console.write_line ["User entered: :user_name"]
$end

simulate_input_output []
```

---

## **Modes**

To be added in future versions.<br>
Modes customize the function's behavior, defined at the top of each function:
```py
$define employee
$include hs, console
$mode static_typing
$parameters STR:name, NUM:age

    STR:employee_info -> data ["Employee name - \":name\" Employee age - :age"]
    console.write_line [employee_info]
    
$end
```

Available Modes:
- `static_typing`

---

## **Examples**

### **Hello World**
```py
$define main
$parameters *args

    print ["Hello, World!"]
    
$end
```

### **Factorial Calculation**
```py
$define main
$include console
$parameters args
    fact -> factorial [5]
    console.write_line [fact]
$end

$define factorial
$parameters n
$include hs, math, conditionals

    result -> data [n]

    is_one -> conditionals.equals [n, 0]
    is_zero -> conditionals.equals [n, 1]
    is_zero_or_one -> conditionals.or [is_zero, is_one]

    $define recursive_factorial
    $parameters n

        n > math.subtract [n, 1]

        is_more_than_zero -> conditionals.more_than [n, 0]

        $define multiply_result_with_prev_num
            result > math.multiply [result, n]
            recursive_factorial[]
        $end

        conditionals.run_if [
                is_more_than_zero,
                multiply_result_with_prev_num, {}
            ]
    $end

    $define return_one
        result > data [1]
    $end

    conditionals.run_if_else[
        is_zero_or_one,
        return_one,
        {},
        recursive_factorial,
        {n}
    ]

    $return result
$end
```

---


## **History**

The oldest known record of the word 'h0rsescript' was found in a Discord DM with my friend on 15th December 2021, at exactly 3:49 PM GMT+5:30 as seen here.
![image](https://cdn.discordapp.com/attachments/807423250396217385/1308484802265223341/image.png?ex=673e1cef&is=673ccb6f&hm=b588b788e8195fde2beb2241fd7812253ad84e8d801b0bd522da16a95dcff080&)

The idea of h0rsescript was to make a language really annoying to use, by combining the most frustrating features of all languages, from the boilerplate of **Java** to the functional programming of **Haskell**, to the existence of **JavaScript**.
There have been *at least* 4 different iterations of this language, repositories of which I'll soon be making public.

---


## **Quick Links**
- [Issues](https://github.com/FlamingH0rse/h0rsescript/issues)
- [Discussions](https://github.com/FlamingH0rse/h0rsescript/discussions)

### Resources
If you wish to learn how to make your own custom (interpreted) programming language, you can choose to refer to these following articles:
- [Writing an interpreter](https://www.toptal.com/scala/writing-an-interpreter)
- [Build your own programming language](https://github.com/codecrafters-io/build-your-own-x/#build-your-own-programming-language)
- [AST explained in plain english](https://dev.to/balapriya/abstract-syntax-tree-ast-explained-in-plain-english-1h38)

### Contributions
If you wish to contribute to this project, simply open a pull request, and it will be reviewed accordingly.

### License

This project is licensed under the GNU GPL-3.0 License - see the [LICENSE](LICENSE) file for details.
