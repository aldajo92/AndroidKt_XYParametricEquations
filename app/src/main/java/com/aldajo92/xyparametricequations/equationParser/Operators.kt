package com.aldajo92.xyparametricequations.equationParser

enum class NormalOperators(val sign: String, val precedence: Int) {
    PLUS("+", 2),
    MINUS("-", 2),
    MULTIPLY("*", 3),
    DIVISION("/", 4),
    POWER("^", 5),
    EXPONENTIAL("E", 5),
    UNARY("u", 6);
}

enum class FunctionalOperators(val func: String) {
    SIN("sin("),
    COS("cos("),
    TAN("tan("),
    ASIN("asin("),
    ACOS("acos("),
    ATAN("atan("),
    SINH("sinh("),
    COSH("cosh("),
    TANH("tanh("),
    LOG2("log2("),
    LOG10("log10("),
    LN("ln("),
    LOGX("log"),
    SQRT("sqrt("),
    EXP("exp(")
}

infix fun <T> String.isIn(operators: Array<T>): Boolean {
    for (operator in operators) {
        if (operator is NormalOperators) {
            if (this == operator.sign) {
                return true
            }
        } else if (operator is FunctionalOperators) {
            if (this.contains(operator.func)) {
                return true
            } else if (this.contains(FunctionalOperators.LOGX.func)) {
                return true
            }
        }
    }
    return false
}

infix fun <T> String.notIn(operators: Array<T>): Boolean {
    return !(this isIn operators)
}