## PROJECT/REPO GOALS:
The primary objective of this project/repository is to develop a custom Backus-Naur Form (BNF) language and implement a transformation process that converts the inputted BNF language into the GeoJSON format. The purpose of this transformation is to facilitate the visualization of various objects, such as aircraft and airports, on a map specifically for the region of Slovenia.

## Intended Use
This project provides a tool for transforming user-inputted strings into GeoJSON format, enabling the display of different objects on a map. Additionally, the program includes a validation mechanism to ensure that the inputted strings adhere to the correct syntax.

## Product Scope
The repository supports following funtionalities:
- Scanner: This component verifies the correctness of symbols within the inputted strings
- Parser: The parser component ensures that the strings are correctly formatted according to the defined grammar rules
- Transformation to GeoJSON: This process converts the validated BNF language strings into the GeoJSON format

## 1 Installation
To use the code, you need to have the following installed:

### 1.1 Dava Development Kit (JDK)
Since the code is implemented in Java, it is necessary to have the JDK installed on your system. You can obtain the latest version of the JDK from the Oracle website or use a package manager suitable for your operating system.

### 1.2 Kotlin Compiler
The code employs Kotlin syntax, so it is essential to have the Kotlin compiler installed. You can install the Kotlin compiler by following the instructions provided on the official Kotlin website.

Once you have the JDK and Kotlin compiler installed, you can compile and run the code using a Java or Kotlin development environment such as IntelliJ IDEA, Eclipse, or Android Studio. Ensure that you set up a project, create the required source files, and copy the code into your project files.

### 1.3 Dependencies
The project uses the following dependecies:
- Kotlin Multiplatform Plugin: This plugin enables the building of multiplatform projects
- Compose Plugin: The Compose plugin provides support for constructing Compose applications
- Compose Desktop Dependency: The dependency org.jetbrains.compose.desktop.currentOs is necessary to access Compose Desktop functionalities
- JDK 11: The code specifies JDK 11 as the target JVM requirement

## 2 Usage
The code is divided into the following sections:

### 2.1 BNF
The provided BNF code represents the markup language used within the program. It specifies the symbols, keywords, and the required structure of the inputted strings. The BNF code serves as a reference for understanding the language's syntax.
```bnf
START ::= COUNTRY

//točke
POINT ::= point ( UNARY , UNARY )
UNARY ::= plus PRIMARY | minus PRIMARY | PRIMARY
PRIMARY ::= int | double |( UNARY )

//if
IF ::= if ( UNARY LOGIC UNARY ) { CODES } ELSE
ELSE ::= else { CODES } | ε
CODES ::= ALL_ELEMENTS CODE
CODE ::= CODES | ε

//logic
LOGIC ::= == | <= | >= | > | < | !=

//izris
BOX ::= box ( POINT , POINT )
CIRCLE ::= circle ( POINT , UNARY )
LINE ::= line ( POINT , POINT )

//city
CITY ::= city string { CITY_ELEMENTS }
CITY_ELEMENTS ::= ELEMENTS_INSIDE_CITY CITY_ELEMENT
CITY_ELEMENT ::= CITY_ELEMENTS | ε

//airport
AIRPORT ::= airport string { AIRPORT_ELEMENTS }
AIRPORT_ELEMENTS ::= ELEMENTS_INSIDE_AIRPORT AIRPORT_ELEMENT
AIRPORT_ELEMENT ::= AIRPORT_ELEMENTS | ε

//building
BUILDING ::= building string { BOX }

//terminal
TERMINAL ::= terminal string { TERMINAL_ELEMENTS }
TERMINAL_ELEMENTS ::= ELEMENTS_INSIDE_TERMINAL TERMINAL_ELEMENT
TERMINAL_ELEMENT ::= TERMINAL_ELEMENTS | ε

//runway
RUNWAY ::= runway string { LINE }

//gate
GATE ::= gate string { CIRCLE }

//country
COUNTRY ::= country string { COUNTRY_ELEMENTS }
COUNTRY_ELEMENTS ::= ELEMENTS_INSIDE_COUNTRY COUNTRY_ELEMENT
COUNTRY_ELEMENT ::= COUNTRY_ELEMENTS | ε

//airplane
AIRPLANE ::= airplane string { POINT }

//elementi
ELEMENTS_INSIDE_COUNTRY ::= CITY | AIRPLANE | IF
ELEMENTS_INSIDE_CITY ::= AIRPORT | BUILDING | IF
ELEMENTS_INSIDE_AIRPORT ::=  BUILDING | TERMINAL | RUNWAY | IF
ELEMENTS_INSIDE_TERMINAL ::= GATE | IF
ALL_ELEMENTS ::= CITY | AIRPLANE | AIRPORT | BUILDING | TERMINAL | RUNWAY | GATE | IF
```

### 2.1 Lexical Analyzer (Scanner)
The lexical analyzer, also known as the scanner, is responsible for examining the inputted strings and verifying the correctness of each symbol and keyword. The scanner detects any invalid symbols or keywords and reports them as errors. The valid symbols and keywords are defined by the BNF language grammar. Here are the symbols and keywords recognized by the scanner:

#### 2.1.1 Symbols:
These are all the symbols that are recognized:
- Double and Int are also recohnized: the INT is any round  number and doube is any decimal number
- '(' and ')': Parentheses used for grouping expressions
- '{' and '}': Curly braces used for defining blocks of code
- ',': Comma used to separate values or arguments
- '+' and '-': Plus and minus symbols used for numerical operations.
- '==', '<=', '>=', '>', '<', and '!=': Comparison operators used for logical expressions
- 'double': Represents a decimal number
- 'int': Represents an integer number
- 'string': Represents a name

The 'double', 'int' and 'string' are not keyword vut rather token types representing numbers and strings in the BNF language. These tokens are used to specify values in the input strings.

#### 2.1.2 Keywords:
These are all the keywords that are recognized:

- 'point', 'plus', 'minus', 'int', 'double', 'if', 'else', 'box', 'circle', 'line', 'city', 'airport', 'building', 'terminal', 'runway', 'gate', 'country', 'airplane'

### 2.2 Syntactic Analyzer (Parser)
The parser component processes the tokens produced by the scanner and checks their compliance with the defined grammar rules. It constructs a parse tree or abstract syntax tree based on the tokens. The parser ensures that the inputted strings are correctly formatted according to the specified grammar. Here is an examle of an inputed string and how it should be structured.

```Example:
country "Slovenia" {
    airplane "delta"{
        point ( 4 , 4 )
    }
    airplane "alfa" {
        point ( 8 , 8 )
    }
    city "Ljubljana" {
        airport "Ljubljanaairport" {
            building "pickup" {
                box ( point ( 46 , 13 ) , point ( 45 , 16 ) )
            }
            building "dropoff" {
                box ( point ( 1 , 1 ) , point ( 2 , 2 ) )
            }
            building "checkin" {
                box ( point ( 3 , 3 ) , point ( 4 , 4 ) )
            }
            building "store" {
                box ( point ( 6 , 3 ) , point ( 8 , 4 ) )
            }
            terminal "TerminalA" {
                gate "Aone" {
                    circle ( point ( 1 , 1 ) , 0.5 )
                }
                gate "Awto" {
                    circle ( point ( 7 , 6 ) , 1 )
                }
            }
            terminal "TerminalB" {
                gate "Bone" {
                    circle ( point ( 19 , 12 ) , 1 )
                }
                gate "Btwo" {
                    circle ( point ( 7 , 6 ) , 1 )
                }
            }
            runway "RunwayA" {
                line ( point ( -10 , 4 ) , point ( -80 , 4 ) )
            }
        }
    }
    city "Maribor" {
        airport "Mariborairport" {
            building "pickup" {
                box ( point ( 2 , 1 ) , point ( 3 , 4 ) )
            }
            building "dropoff" {
                box ( point ( 2 , 3 ) , point ( 4 , 5 ) )
            }
            building "checkin" {
                box ( point ( 1 , 5 ) , point ( 2 , 4 ) )
            }
            building "store" {
                box ( point ( 3 , 4 ) , point ( -5 , -4 ) )
            }
            terminal "TerminalA" {
                gate "Aone" {
                    circle ( point ( 6 , 2 ) , 0.5 )
                }
                gate "Atwo" {
                    circle ( point ( 7 , -6 ) , 1 )
                }
            }
        }
    }
}
```

## Definitions and Acronyms:
- BNF: Backus-Naur Form, a notation used to describe the syntax of programming languages or other formal grammars.
- GeoJSON: A format for encoding geographical data structures based on the JavaScript Object Notation (JSON) format.
- Scanner: A component responsible for breaking down the inputted string into individual tokens or lexemes.
- Parser: A component that analyzes the tokens produced by the scanner according to the defined grammar rules, creating a parse tree or abstract syntax tree.
- GitHub: A web-based hosting service for version control using Git. It allows collaborative development and management of code repositories.
