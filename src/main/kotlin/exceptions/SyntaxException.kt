package exceptions

class SyntaxException(msg: String) : RuntimeException(
    "SYNTAX ERROR" +
            if (msg.isNotBlank()) ": $msg" else ""
) {
    constructor() : this("")
}