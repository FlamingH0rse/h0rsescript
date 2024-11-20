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
- [History](#history)
- [Contributing](#contributions)
- [License](#license)

---

## **Getting Started**

### **Installation**
1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/h0rsescript.git
   cd h0rsescript
   ```
2. Build the interpreter:
   ```bash
   ./gradlew build
   ```
3. Run the interpreter:
   ```bash
   ./build/bin/native/releaseExecutable/hs.kexe <file>.hs 
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
### **Variable Declaration**

Variables are declared using 1 of 5 `Assignment Operators`, and the `data` method:
```py
my_number -> data [652.03]                          # NUM type
my_string -> data ["Hello World"]                   # STR type
my_boolean -> data [true]                           # BOOL type
my_array -> data [ {1, 3, "PLUH", TRUE, false} ]    # ARR type

my_constant <-> data ["password123"]

my_string > data ["Goodbye World"]

<- my_string             # Deletes my_string variable
< my_number, my_boolean   # Empties my_number and my_boolean, sets their values as null

print [my_boolean] # Prints null

print [my_string]  # Throws ReferenceError
```
#### Assignment Operators
| SYMBOL |   TYPE   |
|:------:|:--------:|
|   >    |   EDIT   |
|   ->   | VARIABLE |
|  <->   | CONSTANT |
|   <-   |  DELETE  |
|   <    |  EMPTY   |

Internally, the function just returns the value passed to it. `fun data(x) = x`

---

### **Control Structures**

#### **Conditionals**
```py
$define my_function
    print ["my_number is greater than 10"]
$end

more_than_ten -> conditionals.more_than [my_number, 10]
conditionals.run [more_than_ten, my_function]
```

#### **Loops**
```py
i -> data [0]
$define for_loop_example
$parameters my_array
    element -> arrays.get [my_array, i]
    
    print [ "Array element at index :i is :element" ]
    
    # i++
    i > math.increment [i]
    
    array_length -> arrays.length [my_array]
    
    # i < my_array.length
    conditionals.run [conditionals.lessThan [i, arrayLength], forLoopExample]
$end

# Begin the loop
for_loop_example [my_array]
```

---

### **Functions**

```py
$define greet               # FUN type
$parameters name
    result -> data ["Hello :name!"]
    $return result
$end

greeting -> greet ["FlamingH0rse"]
print [greeting]
```

---

### **Error Handling**

Simple `try-catch` equivalent:
```py
$try
    # Code that might fail
    result -> math.divide [10, 0]
$catch
    print ["An error occurred: Division by zero."]
$end
```

Default behavior:
```py
result -> math.divide [10, 0] # Prints error and exits.
```

---

### **Input/Output**

Input and output are simple but verbose:
```py
$def simulate_input_output
    user_name -> console.read []
    print ["User entered: :user_name"]
$end

simulate_input_output []
```

---

## **Modes**

Modes customize the function's behavior, defined at the top of each function:
```py
$define employee
$mode static_typing
$parameters STR:name, NUM:age

    STR:employee_info -> data ["Employee name - \":name\" Employee age - :age"]
    print [employee_info]
    
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
$parameters *args
    fact factorial [5]
$end

$def factorial
$mode static_typing
$parameters NUM: n
    BOOL:is_base_case -> conditionals.is_equal [n, 1]
    NUM:base_result -> data [1]
    
    NUM:previous_num -> math.decrement [n]
    
    NUM: previous_num_fact -> factorial [previous_num]
    
    NUM:recursive_result -> math.multiply [n, previous_num_fact]
    
    NUM:result -> conditionals.run_or_else [is_base_case, base_result, recursive_result]
    
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

### Contributions
If you wish to contribute to this project, simply open a pull request, and it will be reviewed accordingly.

### License

This project is licensed under the GNU GPL-3.0 License - see the [LICENSE](LICENSE) file for details.
