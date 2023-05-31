import java.io.InputStream
import java.io.File
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.PI
enum class State(val value: Int) {
    Error(0),
    Start(1),
    Integer(2),
    Double(3),
    StringAlpha(4),
    StringBeta(5),
    StringGama(6),
    Plus(7),
    Minus(8),
    LeftParentheses(9),
    RightParentheses(10),
    LeftVigle(11),
    RightVigle(12),
    Skip(13),
    EOF(14),
    Country_City_Circle_C(15),
    Country_O(16),
    Country_U(17),
    Country_N(18),
    Country_T(19),
    Country_R(20),
    Country_Y(21),
    City_Circle_I(22),
    City_T(23),
    City_Y(24),
    Airplane_Airport_A(25),
    Airplane_Airport_I(26),
    Airplane_Airport_R(27),
    Airplane_Airport_P(28),
    Airplane_L(29),
    Airplane_AA(30),
    Airplane_N(31),
    Airplane_E(32),
    Airport_O(33),
    Airport_R(34),
    Airport_T(35),
    Building_Box_B(36),
    Building_U(37),
    Building_I(38),
    Building_L(39),
    Building_D(40),
    Building_II(41),
    Building_N(42),
    Building_G(43),
    Terminal_T(44),
    Terminal_E(45),
    Terminal_R(46),
    Terminal_M(47),
    Terminal_I(48),
    Terminal_N(49),
    Terminal_A(50),
    Terminal_L(51),
    Gate_G(52),
    Gate_A(53),
    Gate_T(54),
    Gate_E(55),
    Runway_R(56),
    Runway_U(57),
    Runway_N(58),
    Runway_W(59),
    Runway_A(60),
    Runway_Y(61),
    BOX_O(62),
    BOX_X(63),
    POINT_P(64),
    POINT_O(65),
    POINT_I(66),
    POINT_N(67),
    POINT_T(68),
    CIRCLE_R(69),
    CIRCLE_C(70),
    CIRCLE_L(71),
    CIRCLE_E(72),
    Comma(73),
    IF_I(74),
    IF_F(75),
    Else_E(76),
    Else_L(77),
    Else_S(78),
    Else_EE(79),
    Line_L(80),
    Line_I(81),
    Line_N(82),
    Line_E(83),
}
enum class Symbol(val value: Int) {
    EOF(-1),
    Skip(0),
    Integer(2),
    Double(3),
    String(6),
    Plus(7),
    Minus(8),
    LeftParentheses(9),
    RightParentheses(10),
    LeftVigle(11),
    RightVigle(12),
    Country(13),
    City(14),
    Airplane(15),
    Airport(16),
    Building(17),
    Terminal(18),
    Gate(19),
    Runway(20),
    Box(21),
    Point(22),
    Circle(23),
    Comma(24),
    If(25),
    Else(26),
    Line(27),
}

const val EOF = -1
const val NEWLINE = '\n'.code
interface DFA {
    val states: Set<Int>
    val alphabet: IntRange
    fun next(state: Int, code: Int): Int
    fun symbol(state: Int): Int
    val startState: Int
    val finalStates: Set<Int>
}
object Automaton: DFA {
    override val states = (1 .. 83).toSet()
    override val alphabet = 0 .. 255
    override val startState = 1
    override val finalStates = setOf(2, 3, 6, 7, 8, 9, 10, 11, 12, 13, 14, 21, 24, 32, 35, 43, 51, 55, 61, 63, 68, 72, 73, 75, 79, 83)

    private val numberOfStates = states.max() + 1 // plus the ERROR_STATE
    private val numberOfCodes = alphabet.max() + 1 // plus the EOF
    private val transitions = Array(numberOfStates) {IntArray(numberOfCodes)}
    private val values = Array(numberOfStates) { Symbol.Skip.value }

    private fun setTransition(from: Int, chr: Char, to: Int) {
        transitions[from][chr.code + 1] = to // + 1 because EOF is -1 and the array starts at 0
    }

    private fun setTransition(from: Int, code: Int, to: Int) {
        transitions[from][code + 1] = to
    }

    private fun setSymbol(state: Int, symbol: Int) {
        values[state] = symbol
    }

    override fun next(state: Int, code: Int): Int {
        assert(states.contains(state))
        assert(alphabet.contains(code))
        return transitions[state][code + 1]
    }

    override fun symbol(state: Int): Int {
        assert(states.contains(state))
        return values[state]
    }

    private fun initializeSymbols() {
        //INT
        for(c in '0' .. '9') {
            // S1 -> S2
            setTransition(State.Start.value, c, State.Integer.value)
            // S2 -> S2
            setTransition(State.Integer.value, c, State.Integer.value)

            setTransition(State.Double.value, c, State.Double.value)

        }
        //DOUBLE
        setTransition(State.Integer.value, '.', State.Double.value)
        for (c in '0'..'9') {
            setTransition(State.Double.value, c, State.Double.value)
        }
        // STRING
        setTransition(State.Start.value, '"', State.StringAlpha.value)
        for(c in 'a' .. 'z') {
            // S1 -> S3
            setTransition(State.StringAlpha.value, c, State.StringBeta.value)
            // S3 -> S3
            setTransition(State.StringBeta.value, c, State.StringBeta.value)
        }
        for(c in 'A' .. 'Z') {
            // S1 -> S3
            setTransition(State.StringAlpha.value, c, State.StringBeta.value)
            // S3 -> S3
            setTransition(State.StringBeta.value, c, State.StringBeta.value)
        }
        setTransition(State.StringBeta.value, '"', State.StringGama.value)

        // OPERATORS
        // S1 -> S4
        setTransition(State.Start.value, '+', State.Plus.value)
        // S1 -> S5
        setTransition(State.Start.value, '-', State.Minus.value)
        // S1 -> S6
        setTransition(State.Start.value, '(', State.LeftParentheses.value)
        // S1 -> S7
        setTransition(State.Start.value, ')', State.RightParentheses.value)
        // S1 -> S8
        setTransition(State.Start.value, '{', State.LeftVigle.value)
        // S1 -> S8
        setTransition(State.Start.value, '}', State.RightVigle.value)

        setTransition(State.Start.value, ',', State.Comma.value)

        // Ignore
        setTransition(State.Start.value, ' ', State.Skip.value)
        setTransition(State.Start.value, '\t', State.Skip.value)
        setTransition(State.Start.value, '\n', State.Skip.value)
        setTransition(State.Start.value, '\r', State.Skip.value)
        setTransition(State.Skip.value, ' ', State.Skip.value)
        setTransition(State.Skip.value, '\t', State.Skip.value)
        setTransition(State.Skip.value, '\n', State.Skip.value)
        setTransition(State.Skip.value, '\r', State.Skip.value)
        // EOF
        // S1 -> S15
        setTransition(State.Start.value, EOF, State.EOF.value)
    }

    private fun initializeCountryKeyword() {
        setTransition(State.Start.value, 'c', State.Country_City_Circle_C.value)
        setTransition(State.Country_City_Circle_C.value, 'o', State.Country_O.value)
        setTransition(State.Country_O.value, 'u', State.Country_U.value)
        setTransition(State.Country_U.value, 'n', State.Country_N.value)
        setTransition(State.Country_N.value, 't', State.Country_T.value)
        setTransition(State.Country_T.value, 'r', State.Country_R.value)
        setTransition(State.Country_R.value, 'y', State.Country_Y.value)
    }

    private fun initializeCityKeyword() {
        setTransition(State.Country_City_Circle_C.value, 'i', State.City_Circle_I.value)
        setTransition(State.City_Circle_I.value, 't', State.City_T.value)
        setTransition(State.City_T.value, 'y', State.City_Y.value)
    }

    private fun initializeAirplaneKeyword() {
        setTransition(State.Start.value, 'a', State.Airplane_Airport_A.value)
        setTransition(State.Airplane_Airport_A.value, 'i', State.Airplane_Airport_I.value)
        setTransition(State.Airplane_Airport_I.value, 'r', State.Airplane_Airport_R.value)
        setTransition(State.Airplane_Airport_R.value, 'p', State.Airplane_Airport_P.value)
        setTransition(State.Airplane_Airport_P.value, 'l', State.Airplane_L.value)
        setTransition(State.Airplane_L.value, 'a', State.Airplane_AA.value)
        setTransition(State.Airplane_AA.value, 'n', State.Airplane_N.value)
        setTransition(State.Airplane_N.value, 'e', State.Airplane_E.value)
    }

    private fun initializeAirportKeyword(){
        setTransition(State.Start.value, 'a', State.Airplane_Airport_A.value)
        setTransition(State.Airplane_Airport_A.value, 'i', State.Airplane_Airport_I.value)
        setTransition(State.Airplane_Airport_I.value, 'r', State.Airplane_Airport_R.value)
        setTransition(State.Airplane_Airport_R.value, 'p', State.Airplane_Airport_P.value)
        setTransition(State.Airplane_Airport_P.value,'o',State.Airport_O.value)
        setTransition(State.Airport_O.value,'r',State.Airport_R.value)
        setTransition(State.Airport_R.value,'t',State.Airport_T.value)
    }

    private fun initializeTerminalKeyword(){
        setTransition(State.Start.value, 't', State.Terminal_T.value)
        setTransition(State.Terminal_T.value, 'e', State.Terminal_E.value)
        setTransition(State.Terminal_E.value, 'r', State.Terminal_R.value)
        setTransition(State.Terminal_R.value, 'm', State.Terminal_M.value)
        setTransition(State.Terminal_M.value, 'i', State.Terminal_I.value)
        setTransition(State.Terminal_I.value, 'n', State.Terminal_N.value)
        setTransition(State.Terminal_N.value, 'a', State.Terminal_A.value)
        setTransition(State.Terminal_A.value, 'l', State.Terminal_L.value)
    }

    private fun initializeBuildingKeyword(){
        setTransition(State.Start.value,'b',State.Building_Box_B.value)
        setTransition(State.Building_Box_B.value,'u',State.Building_U.value)
        setTransition(State.Building_U.value,'i',State.Building_I.value)
        setTransition(State.Building_I.value,'l',State.Building_L.value)
        setTransition(State.Building_L.value,'d',State.Building_D.value)
        setTransition(State.Building_D.value,'i',State.Building_II.value)
        setTransition(State.Building_II.value,'n',State.Building_N.value)
        setTransition(State.Building_N.value,'g',State.Building_G.value)
    }

    private fun initializeGateStates() {
        setTransition(State.Start.value,'g',State.Gate_G.value)
        setTransition(State.Gate_G.value,'a',State.Gate_A.value)
        setTransition(State.Gate_A.value,'t',State.Gate_T.value)
        setTransition(State.Gate_T.value,'e',State.Gate_E.value)
    }

    private fun initializeRunwayStates(){
        setTransition(State.Start.value,'r',State.Runway_R.value)
        setTransition(State.Runway_R.value,'u',State.Runway_U.value)
        setTransition(State.Runway_U.value,'n',State.Runway_N.value)
        setTransition(State.Runway_N.value,'w',State.Runway_W.value)
        setTransition(State.Runway_W.value,'a',State.Runway_A.value)
        setTransition(State.Runway_A.value,'y',State.Runway_Y.value)

    }

    private fun initializeBoxStates(){
        setTransition(State.Building_Box_B.value,'o',State.BOX_O.value)
        setTransition(State.BOX_O.value,'x',State.BOX_X.value)
    }

    private fun initializeCircleStates(){
        setTransition(State.City_Circle_I.value,'r',State.CIRCLE_R.value)
        setTransition(State.CIRCLE_R.value,'c',State.CIRCLE_C.value)
        setTransition(State.CIRCLE_C.value,'l',State.CIRCLE_L.value)
        setTransition(State.CIRCLE_L.value,'e',State.CIRCLE_E.value)

    }

    private fun initializePointStates(){
        setTransition(State.Start.value,'p',State.POINT_P.value)
        setTransition(State.POINT_P.value,'o',State.POINT_O.value)
        setTransition(State.POINT_O.value,'i',State.POINT_I.value)
        setTransition(State.POINT_I.value,'n',State.POINT_N.value)
        setTransition(State.POINT_N.value,'t',State.POINT_T.value)

    }

    private fun initializeIfStates(){
        setTransition(State.Start.value,'i',State.IF_I.value)
        setTransition(State.IF_I.value,'f',State.IF_F.value)
    }

    private fun initializeElseStates(){
        setTransition(State.Start.value,'e',State.Else_E.value)
        setTransition(State.Else_E.value,'l',State.Else_L.value)
        setTransition(State.Else_L.value,'s',State.Else_S.value)
        setTransition(State.Else_S.value,'e',State.Else_EE.value)
    }

    private fun initializeLineStates() {
        setTransition(State.Start.value, 'l', State.Line_L.value)
        setTransition(State.Line_L.value, 'i', State.Line_I.value)
        setTransition(State.Line_I.value, 'n', State.Line_N.value)
        setTransition(State.Line_N.value, 'e', State.Line_E.value)
    }

    private fun initializeFiniteStates() {
        setSymbol(State.Integer.value, Symbol.Integer.value)
        setSymbol(State.Double.value,Symbol.Double.value)
        setSymbol(State.StringGama.value,Symbol.String.value)
        setSymbol(State.Plus.value, Symbol.Plus.value)
        setSymbol(State.Minus.value, Symbol.Minus.value)
        setSymbol(State.LeftParentheses.value, Symbol.LeftParentheses.value)
        setSymbol(State.RightParentheses.value, Symbol.RightParentheses.value)
        setSymbol(State.LeftVigle.value,Symbol.LeftVigle.value)
        setSymbol(State.RightVigle.value,Symbol.RightVigle.value)
        setSymbol(State.EOF.value, Symbol.EOF.value)
        setSymbol(State.Country_City_Circle_C.value,Symbol.EOF.value)
        setSymbol(State.Country_O.value,Symbol.EOF.value)
        setSymbol(State.Country_U.value,Symbol.EOF.value)
        setSymbol(State.Country_N.value,Symbol.EOF.value)
        setSymbol(State.Country_T.value,Symbol.EOF.value)
        setSymbol(State.Country_R.value,Symbol.EOF.value)
        setSymbol(State.Country_Y.value,Symbol.Country.value)
        setSymbol(State.City_Circle_I.value,Symbol.EOF.value)
        setSymbol(State.City_T.value,Symbol.EOF.value)
        setSymbol(State.City_Y.value,Symbol.City.value)
        setSymbol(State.Airplane_Airport_A.value, Symbol.EOF.value)
        setSymbol(State.Airplane_Airport_I.value, Symbol.EOF.value)
        setSymbol(State.Airplane_Airport_R.value, Symbol.EOF.value)
        setSymbol(State.Airplane_Airport_P.value, Symbol.EOF.value)
        setSymbol(State.Airplane_L.value, Symbol.EOF.value)
        setSymbol(State.Airplane_AA.value, Symbol.EOF.value)
        setSymbol(State.Airplane_N.value, Symbol.EOF.value)
        setSymbol(State.Airplane_E.value, Symbol.Airplane.value)
        setSymbol(State.Airport_O.value,Symbol.EOF.value)
        setSymbol(State.Airport_R.value,Symbol.EOF.value)
        setSymbol(State.Airport_T.value,Symbol.Airport.value)
        setSymbol(State.Building_Box_B.value,Symbol.EOF.value)
        setSymbol(State.Building_U.value,Symbol.EOF.value)
        setSymbol(State.Building_I.value,Symbol.EOF.value)
        setSymbol(State.Building_L.value,Symbol.EOF.value)
        setSymbol(State.Building_D.value,Symbol.EOF.value)
        setSymbol(State.Building_II.value,Symbol.EOF.value)
        setSymbol(State.Building_N.value,Symbol.EOF.value)
        setSymbol(State.Building_G.value,Symbol.Building.value)
        setSymbol(State.Terminal_T.value,Symbol.EOF.value)
        setSymbol(State.Terminal_E.value,Symbol.EOF.value)
        setSymbol(State.Terminal_R.value,Symbol.EOF.value)
        setSymbol(State.Terminal_M.value,Symbol.EOF.value)
        setSymbol(State.Terminal_I.value,Symbol.EOF.value)
        setSymbol(State.Terminal_N.value,Symbol.EOF.value)
        setSymbol(State.Terminal_A.value,Symbol.EOF.value)
        setSymbol(State.Terminal_L.value,Symbol.Terminal.value)
        setSymbol(State.Gate_G.value,Symbol.EOF.value)
        setSymbol(State.Gate_A.value,Symbol.EOF.value)
        setSymbol(State.Gate_T.value,Symbol.EOF.value)
        setSymbol(State.Gate_E.value,Symbol.Gate.value)
        setSymbol(State.Runway_R.value,Symbol.EOF.value)
        setSymbol(State.Runway_U.value,Symbol.EOF.value)
        setSymbol(State.Runway_N.value,Symbol.EOF.value)
        setSymbol(State.Runway_W.value,Symbol.EOF.value)
        setSymbol(State.Runway_A.value,Symbol.EOF.value)
        setSymbol(State.Runway_Y.value,Symbol.Runway.value)
        setSymbol(State.BOX_O.value,Symbol.EOF.value)
        setSymbol(State.BOX_X.value,Symbol.Box.value)
        setSymbol(State.CIRCLE_R.value,Symbol.EOF.value)
        setSymbol(State.CIRCLE_C.value,Symbol.EOF.value)
        setSymbol(State.CIRCLE_L.value,Symbol.EOF.value)
        setSymbol(State.CIRCLE_E.value,Symbol.Circle.value)
        setSymbol(State.POINT_P.value,Symbol.EOF.value)
        setSymbol(State.POINT_O.value,Symbol.EOF.value)
        setSymbol(State.POINT_I.value,Symbol.EOF.value)
        setSymbol(State.POINT_N.value,Symbol.EOF.value)
        setSymbol(State.POINT_T.value,Symbol.Point.value)
        setSymbol(State.Comma.value,Symbol.Comma.value)
        setSymbol(State.IF_I.value,Symbol.EOF.value)
        setSymbol(State.IF_F.value,Symbol.If.value)
        setSymbol(State.Else_E.value,Symbol.EOF.value)
        setSymbol(State.Else_L.value,Symbol.EOF.value)
        setSymbol(State.Else_S.value,Symbol.EOF.value)
        setSymbol(State.Else_EE.value,Symbol.Else.value)
        setSymbol(State.Line_L.value,Symbol.EOF.value)
        setSymbol(State.Line_I.value,Symbol.EOF.value)
        setSymbol(State.Line_N.value,Symbol.EOF.value)
        setSymbol(State.Line_E.value,Symbol.Line.value)

    }

    init {
        initializeSymbols()
        initializeCountryKeyword()
        initializeCityKeyword()
        initializeAirplaneKeyword()
        initializeAirportKeyword()
        initializeBuildingKeyword()
        initializeTerminalKeyword()
        initializeGateStates()
        initializeRunwayStates()
        initializeBoxStates()
        initializeCircleStates()
        initializePointStates()
        initializeIfStates()
        initializeElseStates()
        initializeLineStates()

        initializeFiniteStates()
    }
}
data class Token(val symbol: Int, val lexeme: String, val startRow: Int, val startColumn: Int)
class Scanner(private val automaton: DFA, private val stream: InputStream) {
    private var last: Int? = null
    private var row = 1
    private var column = 1

    private fun updatePosition(code: Int) {
        if (code == NEWLINE) {
            row += 1
            column = 1
        } else {
            column += 1
        }
    }

    fun getToken(): Token {
        val startRow = row
        val startColumn = column
        val buffer = mutableListOf<Char>()

        var code = last ?: stream.read()
        var state = automaton.startState
        while (true) {
            val nextState = automaton.next(state, code)
            if (nextState == State.Error.value) break // Longest match

            state = nextState
            updatePosition(code)
            buffer.add(code.toChar())
            code = stream.read()
        }
        last = code // The code following the current lexeme is the first code of the next lexeme

        if (automaton.finalStates.contains(state)) {
            val symbol = automaton.symbol(state)
            return if (symbol == Symbol.Skip.value) {
                getToken()
            } else {
                val lexeme = String(buffer.toCharArray())
                Token(symbol, lexeme, startRow, startColumn)
            }
        } else {
            throw Error("Invalid pattern at ${row}:${column}")
        }
    }
}
fun name(symbol: Int) =
    when (symbol) {
        Symbol.Integer.value -> "int"
        Symbol.Double.value -> "double"
        Symbol.String.value -> "string"
        Symbol.Plus.value -> "plus"
        Symbol.Minus.value -> "minus"
        Symbol.LeftParentheses.value -> "lparen"
        Symbol.RightParentheses.value -> "rparen"
        Symbol.LeftVigle.value -> "lvigle"
        Symbol.RightVigle.value -> "rvigle"
        Symbol.Country.value -> "county"
        Symbol.City.value -> "city"
        Symbol.Airplane.value -> "airplane"
        Symbol.Airport.value -> "airport"
        Symbol.Building.value -> "building"
        Symbol.Terminal.value -> "terminal"
        Symbol.Gate.value -> "gate"
        Symbol.Runway.value -> "runway"
        Symbol.Box.value -> "box"
        Symbol.Circle.value -> "circle"
        Symbol.Point.value -> "point"
        Symbol.Comma.value -> "comma"
        Symbol.If.value -> "if"
        Symbol.Else.value -> "else"
        Symbol.Line.value -> "line"

        else -> throw Error("Invalid symbol")
    }
fun printTokens(scanner: Scanner) {
    val token = scanner.getToken()
    if (token.symbol != Symbol.EOF.value) {
        print("${name(token.symbol)}(\"${token.lexeme}\"), ")
        printTokens(scanner)
    }
}
fun readFromFile(path: String): String {
    val file = File(path)
    return file.readText()
}

fun main(args: Array<String>) {
    println("Lexikalni anlizator: ")
    val inputString="country \"Slovenia\" { airplane \"delta\"{ point ( 13.40 , 45.97 ) } airplane \"alfa\" { point ( 15.2 , 45.67 ) } city \"Ljubljana\" { airport \"Ljubljanaairport\" { building \"pickup\" { box ( point ( 14.55 , 45 ) , point ( 14.56 , 45.05 ) ) } building \"dropoff\" { box ( point ( 14.65 , 45.10 ) , point ( 14.2 , 46.2 ) ) } building \"checkin\" { box ( point ( 13.9 , 45.9 ) , point ( 1.98, 45.87 ) ) } building \"store\" { box ( point ( 16.24545454 , 47.13 ) , point ( 16.21 , 47.15 ) ) } terminal \"TermilA\" { gate \"Aone\" { circle ( point ( 14.65 , 45.9 ) , 0.0005 ) } gate \"Awto\" { circle ( point ( 7 , 6 ) , 1 ) } gate \"Athree\" { circle ( point ( 9 , 2 ) , 2 ) } gate \"Afour\" { circle ( point ( 8 , 3 ) , 0.2 ) } gate \"Afive\" { circle ( point ( 4.6 , 3.7 ) , 0.4 ) } gate \"Asix\" { circle ( point ( 2 , 9 ) , 0.2 ) } gate \"Aseven\" { circle ( point ( 5 , 7 ) , 3) } gate \"Aeight\" { circle ( point ( 12 , 13 ) , 1) } } terminal \"TerminalB\" { gate \"Bone\" { circle ( point ( 19 , 12 ) , 1 ) } gate \"Btwo\" { circle ( point ( 7 , 6 ) , 1 ) } gate \"Bthree\" { circle ( point ( 9 , 2 ) , 2 ) } gate \"Bfour\" { circle ( point ( 8 , 3 ) , 0.2 ) } gate \"Bfive\" { circle ( point ( 1 , 1 ) , 0.5 ) } gate \"Bsix\" { circle ( point ( 2 , 9 ) , 0.2 ) } gate \"Bseven\" { circle ( point ( 5 , 7 ) , 3) } gate \"Beight\" { circle ( point ( 8 , 3 ) , 0.2 ) } } runway \"RunwayA\" { line ( point ( -10 , 4 ) , point ( -80 , 4 ) ) } runway \"RunwayB\" { line ( point ( -10 , -8 ) , point ( -95 , -8 ) ) } runway \"RunwayC\" { line ( point ( -10 , -12) , point ( -120 , -12 ) ) } } } city \"Maribor\" { airport \"Mariborairport\" { building \"pickup\" { box ( point ( 2 , 1 ) , point ( 3 , 4 ) ) } building \"dropoff\" { box ( point ( 2 , 3 ) , point ( 4 , 5 ) ) } building \"checkin\" { box ( point ( 1 , 5 ) , point ( 2 , 4 ) ) } building \"store\" { box ( point ( 3 , 4 ) , point ( -5 , -4 ) ) } terminal \"TerminalA\" { gate \"Aone\" { circle ( point ( 6 , 2 ) , 0.5 ) } gate \"Atwo\" { circle ( point ( 7 , -6 ) , 1 ) } gate \"Athree\" { circle ( point ( -9 , -2 ) , 2 ) } gate \"Afour\" { circle ( point ( 8 , 3 ) , 0.2 ) } gate \"Afive\" { circle ( point ( 4.6 , -3.7 ) , 0.4 ) } gate \"Asix\" { circle ( point ( -2 , 9 ) , 0.2 ) } gate \"Aseven\" { circle ( point ( -5 , 7 ) , 3) } gate \"Aeight\" { circle ( point ( -12 , -13 ) , 1) } } } } }"
    val scanner=Scanner(Automaton, inputString.byteInputStream())
    printTokens(scanner)
    println("\n")
    val parser = Parser(Scanner(Automaton,inputString.byteInputStream()),"")
    println("Sintakstni anlizator: " + parser.Parse())
    println()
    println("Vrnjen JSON v GeoJson formatu: ")
    println(parser.stringJson_All)
}

/*
fun echo(prefix: String, stream: InputStream) {
    val buffer = LinkedList<Byte>()

    while (true) {
        val symbol = stream.read()
        if (symbol == -1) break
        buffer.add(symbol.toByte())

    }
    println(prefix + String(buffer.toByteArray()))
}

fun main(args: Array<String>) {
    echo("ANSWER\n", File(args[0]).inputStream())
}
 */
class Parser(var scanner: Scanner,var stringJson_All: String){
    fun createCircle(input:String):String{
        val parts = input.split("]").first().removePrefix("[").split(",")
        val center = Pair(parts[0].toDouble(), parts[1].toDouble())
        val radius = input.split("]")[1].toDouble()

        val points = mutableListOf<String>()
        val (x, y) = center

        // Calculate the angle between each point
        val angleStep = 2 * PI / 32

        // Generate the points using the parametric equation of a circle
        for (i in 0 until 32) {
            val theta = i * angleStep
            val pointX = x + radius * cos(theta)
            val pointY = y + radius * sin(theta)
            points.add("[$pointX, $pointY]")
        }

        return points.joinToString(", ") + (", ") + points[0]
    }
    var prvi=""
    var drugi=""
    var boxString=""
    var token:Token?=null

    fun Parse():Boolean{
        stringJson_All="{\"type\":\"FeatureCollection\", \"features\":["
        token=scanner.getToken()
        return STA() && EOF()
    }

    fun STA():Boolean{
        return COUNTRY()
    }

    fun COUNTRY():Boolean{
        if (token?.symbol==Symbol.Country.value){
            token=scanner.getToken()
            if (token?.symbol==Symbol.String.value){
                token=scanner.getToken()
                if (token?.symbol==Symbol.LeftVigle.value){
                    token=scanner.getToken()
                    if (COUNTRY_ELEMENTS()){
                        if (token?.symbol==Symbol.RightVigle.value){
                            token=scanner.getToken()
                            return true
                        }
                    }
                }
            }
        }
        return false
    }

    fun COUNTRY_ELEMENTS():Boolean{
        return ELEMENTS_INSIDE_COUNTRY() && COUNTRY_ELEMENT()
    }

    fun COUNTRY_ELEMENT():Boolean{
        if (COUNTRY_ELEMENTS()){
            return true
        }
        stringJson_All=stringJson_All.substring(0,stringJson_All.length-2)
        return true
    }

    fun ELEMENTS_INSIDE_COUNTRY():Boolean{
        if (CITY() || AIRPLANE()){
            return true
        }
        return false
    }

    fun CITY():Boolean{
        if (token?.symbol==Symbol.City.value){
            token=scanner.getToken()
            if (token?.symbol==Symbol.String.value){
                token=scanner.getToken()
                if (token?.symbol==Symbol.LeftVigle.value){
                    token=scanner.getToken()
                    if (CITY_ELEMENTS()){
                        if (token?.symbol==Symbol.RightVigle.value){
                            token=scanner.getToken()
                            return true
                        }
                    }
                }
            }
        }
        return false
    }

    fun CITY_ELEMENTS():Boolean{
        return ELEMENTS_INSIDE_CITY() && CITY_ELEMENT()
    }

    fun ELEMENTS_INSIDE_CITY():Boolean{
        if (BUILDING() || AIRPORT() ) {
            return true
        }
        return false
    }

    fun BUILDING():Boolean{
        if (token?.symbol==Symbol.Building.value){
            token=scanner.getToken()
            if (token?.symbol==Symbol.String.value) {
                token = scanner.getToken()
                if (token?.symbol == Symbol.LeftVigle.value) {
                    token = scanner.getToken()
                    if (BOX()) {
                        if (token?.symbol == Symbol.RightVigle.value) {
                            token = scanner.getToken()
                            return true
                        }
                    }
                }
            }
        }
        return false
    }

    fun BOX():Boolean{
        boxString=""
        if (token?.symbol==Symbol.Box.value){
            token=scanner.getToken()
            stringJson_All=stringJson_All+"{\"type\":\"Feature\", \"geometry\":{\"type\":\"Polygon\", \"coordinates\":[["
            if (token?.symbol==Symbol.LeftParentheses.value){
                token=scanner.getToken()
                var temp = stringJson_All.length
                if (POINT()){
                    prvi=boxString
                    boxString=""
                    stringJson_All=stringJson_All+","
                    if (token?.symbol==Symbol.Comma.value){
                        token=scanner.getToken()
                        if (POINT()){
                            if (token?.symbol==Symbol.RightParentheses.value){
                                drugi=boxString
                                stringJson_All=stringJson_All.substring(0,temp)
                                stringJson_All=stringJson_All+prvi
                                stringJson_All=stringJson_All+","
                                stringJson_All=stringJson_All+prvi.substring(0,prvi.indexOf(','))
                                stringJson_All=stringJson_All+drugi.substring(drugi.indexOf(','))
                                stringJson_All=stringJson_All+","
                                stringJson_All=stringJson_All+drugi
                                stringJson_All=stringJson_All+","
                                stringJson_All=stringJson_All+drugi.substring(0,drugi.indexOf(','))
                                stringJson_All=stringJson_All+prvi.substring(drugi.indexOf(','))
                                stringJson_All=stringJson_All+","
                                stringJson_All=stringJson_All+prvi


                                boxString=""
                                stringJson_All=stringJson_All+"]]}, "
                                stringJson_All=stringJson_All+"\"properties\":null}, "

                                token=scanner.getToken()

                                return true
                            }
                        }
                    }
                }
            }
        }
        return false
    }

    fun POINT():Boolean{
        if (token?.symbol==Symbol.Point.value){
            stringJson_All=stringJson_All+"["
            boxString=boxString+"["

            token=scanner.getToken()
            if (token?.symbol==Symbol.LeftParentheses.value){
                token=scanner.getToken()
                if (UNARY()){
                    if (token?.symbol==Symbol.Comma.value){
                        stringJson_All=stringJson_All+", "
                        boxString=boxString+", "
                        token=scanner.getToken()
                        if (UNARY()){
                            if (token?.symbol==Symbol.RightParentheses.value) {
                                stringJson_All=stringJson_All+"]"
                                boxString=boxString+"]"
                                token = scanner.getToken()
                                return true
                            }
                        }
                    }
                }
            }
        }
        return false
    }

    fun UNARY():Boolean{
        if (token?.symbol==Symbol.Plus.value) {
            token = scanner.getToken()
            return PRIMARY()
        }
        if (token?.symbol==Symbol.Minus.value) {
            token = scanner.getToken()
            return PRIMARY()
        }
        return PRIMARY()
    }

    fun PRIMARY():Boolean{
        if (token?.symbol==Symbol.Integer.value) {
            boxString=boxString+token?.lexeme.toString()
            stringJson_All= stringJson_All + token?.lexeme.toString()
            token = scanner.getToken()

            return true
        }
        if (token?.symbol==Symbol.Double.value) {
            boxString=boxString+token?.lexeme.toString()
            stringJson_All= stringJson_All + token?.lexeme.toString()
            token = scanner.getToken()
            return true
        }
        if (token?.symbol==Symbol.LeftParentheses.value) {
            token = scanner.getToken()
            if (UNARY()){
                if (token?.symbol==Symbol.RightParentheses.value) {
                    token = scanner.getToken()
                    return true
                }
            }
        }
        return false
    }

    fun CITY_ELEMENT():Boolean{
        if (CITY_ELEMENTS()){
            return true
        }
        return true
    }

    fun AIRPLANE():Boolean{
        if (token?.symbol==Symbol.Airplane.value){
            stringJson_All=stringJson_All+"{\"type\":\"Feature\", \"geometry\":{\"type\":\"Point\", \"coordinates\":"
            token=scanner.getToken()
            if (token?.symbol==Symbol.String.value) {
                token = scanner.getToken()
                if (token?.symbol == Symbol.LeftVigle.value) {
                    token = scanner.getToken()
                    if (POINT()) {
                        if (token?.symbol == Symbol.RightVigle.value) {
                            stringJson_All=stringJson_All+"}, "
                            stringJson_All=stringJson_All+"\"properties\":null}, "
                            token = scanner.getToken()
                            return true
                        }
                    }
                }
            }
        }
        return false
    }

    /*
    fun IF():Boolean{
        if (token?.symbol==Symbol.LeftParentheses.value) {
            token = scanner.getToken()
            if (UNARY()){
                if (LOGIC()){
                    if (UNARY()){
                        if (token?.symbol==Symbol.RightParentheses.value) {
                            token = scanner.getToken()
                            if (token?.symbol == Symbol.LeftVigle.value) {
                                token = scanner.getToken()
                                if (CODES()){
                                    if (token?.symbol == Symbol.RightVigle.value) {
                                        token = scanner.getToken()
                                        return ELSE()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return true
    }

    fun LOGIC():Boolean{

    }

     */

    fun AIRPORT():Boolean{
        if (token?.symbol==Symbol.Airport.value) {
            token = scanner.getToken()
            if (token?.symbol==Symbol.String.value) {
                token = scanner.getToken()
                if (token?.symbol == Symbol.LeftVigle.value) {
                    token = scanner.getToken()
                    if (AIRPORT_ELEMENTS()) {
                        if (token?.symbol == Symbol.RightVigle.value) {
                            token = scanner.getToken()
                            return true
                        }
                    }
                }
            }
        }
        return false
    }

    fun AIRPORT_ELEMENTS():Boolean{
        return ELEMENTS_INSIDE_AIRPORT() && AIRPORT_ELEMENT()
    }

    fun AIRPORT_ELEMENT():Boolean{
        if (AIRPORT_ELEMENTS()){

            return true
        }
        return true
    }

    fun ELEMENTS_INSIDE_AIRPORT():Boolean{
        if (BUILDING()  || TERMINAL() || RUNWAY()) {
            return true
        }
        return false
    }

    fun TERMINAL():Boolean{
        if (token?.symbol == Symbol.Terminal.value) {
            token = scanner.getToken()
            if (token?.symbol == Symbol.String.value) {
                token = scanner.getToken()
                if (token?.symbol == Symbol.LeftVigle.value) {
                    token = scanner.getToken()
                    if (TERMINAL_ELEMENTS()){
                        if (token?.symbol == Symbol.RightVigle.value) {
                            token = scanner.getToken()
                            return true
                        }
                    }
                }
            }
        }
        return false
    }

    fun TERMINAL_ELEMENTS():Boolean{
        return ELEMENTS_INSIDE_TERMINAL() && TERMINAL_ELEMENT()
    }

    fun TERMINAL_ELEMENT():Boolean{
        if (TERMINAL_ELEMENTS()){
            return true
        }
        return true
    }

    fun ELEMENTS_INSIDE_TERMINAL():Boolean{
        if (GATE()) {
            return true
        }
        return false
    }

    fun GATE():Boolean{
        if (token?.symbol == Symbol.Gate.value) {
            token = scanner.getToken()
            if (token?.symbol == Symbol.String.value) {
                token = scanner.getToken()
                if (token?.symbol == Symbol.LeftVigle.value) {
                    token = scanner.getToken()
                    if (CIRCLE()){
                        if (token?.symbol == Symbol.RightVigle.value) {
                            token = scanner.getToken()
                            return true
                        }
                    }
                }
            }
        }
        return false
    }

    fun CIRCLE():Boolean{
        if (token?.symbol == Symbol.Circle.value) {
            stringJson_All=stringJson_All+"{\"type\":\"Feature\", \"geometry\":{\"type\":\"Polygon\", \"coordinates\":"
            token = scanner.getToken()
            if (token?.symbol == Symbol.LeftParentheses.value) {
                token = scanner.getToken()
                if (POINT()){
                    if (token?.symbol == Symbol.Comma.value) {
                        token = scanner.getToken()
                        if (UNARY()){
                            val lastIndex=stringJson_All.lastIndexOf(':')
                            val circle=stringJson_All.substring(lastIndex+1)
                            stringJson_All=stringJson_All.substringBeforeLast("[")
                            stringJson_All=stringJson_All+"[["
                            stringJson_All+=createCircle(circle)
                            if (token?.symbol == Symbol.RightParentheses.value) {
                                stringJson_All=stringJson_All+"]]"
                                stringJson_All=stringJson_All+"}, "
                                stringJson_All=stringJson_All+"\"properties\":null}, "

                                token = scanner.getToken()
                                return true
                            }
                        }
                    }
                }
            }
        }
        return false
    }

    fun RUNWAY():Boolean{
        if (token?.symbol == Symbol.Runway.value) {
            token = scanner.getToken()
            if (token?.symbol == Symbol.String.value) {
                token = scanner.getToken()
                if (token?.symbol == Symbol.LeftVigle.value) {
                    token = scanner.getToken()
                    if (LINE()){
                        if (token?.symbol == Symbol.RightVigle.value) {
                            token = scanner.getToken()
                            return true
                        }
                    }
                }
            }
        }
        return false
    }

    fun LINE():Boolean{
        if (token?.symbol == Symbol.Line.value) {
            stringJson_All=stringJson_All+"{\"type\":\"Feature\", \"geometry\":{\"type\":\"LineString\", \"coordinates\":"

            token = scanner.getToken()
            if (token?.symbol == Symbol.LeftParentheses.value) {
                token = scanner.getToken()
                stringJson_All=stringJson_All+"["

                if (POINT()){
                    if (token?.symbol == Symbol.Comma.value) {
                        stringJson_All=stringJson_All+","
                        token = scanner.getToken()
                        if(POINT()){
                            if (token?.symbol == Symbol.RightParentheses.value) {
                                stringJson_All=stringJson_All+"]"
                                token = scanner.getToken()
                                stringJson_All=stringJson_All+"}, "
                                stringJson_All=stringJson_All+"\"properties\":null}, "
                                return true
                            }
                        }
                    }
                }
            }
        }
        return true
    }

    fun EOF():Boolean{
        if (token?.symbol==-1){
            stringJson_All=stringJson_All+"]}"
            return true
        }
        return false
    }
}