package exceptions

import java.lang.RuntimeException

class CompileTimeException(msg: String, name: String, line: Int): RuntimeException("$msg $name:$line")