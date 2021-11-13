package dev.schoeberl.ccc.lv3

import CCC3BaseVisitor
import CCC3Lexer
import CCC3Parser
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import java.io.File
import java.lang.RuntimeException

class ReturnException(val consoleText: String): RuntimeException()
class ErrorException: RuntimeException()

fun evaluateFunction(function: CCC3Parser.FunctionContext): String {
    val sb = StringBuilder()

    val variables = mutableMapOf<String, String>()

    val visitor = object: CCC3BaseVisitor<String?>() {

        override fun visitPrintStatement(ctx: CCC3Parser.PrintStatementContext): String? {
            sb.append(visit(ctx.expr()))
            return null
        }

        override fun visitIfElseStatement(ctx: CCC3Parser.IfElseStatementContext): String? {
            val evaluatedCondition = super.visit(ctx.condition)
            if (evaluatedCondition == "true") {
                super.visit(ctx.thenBranch)
            } else if (evaluatedCondition == "false") {
                super.visit(ctx.elseBranch)
            } else {
                throw ErrorException()
            }

            return null
        }

        override fun visitReturnStatement(ctx: CCC3Parser.ReturnStatementContext?): String? {
            throw ReturnException(sb.toString())
        }

        override fun visitVarStatement(ctx: CCC3Parser.VarStatementContext): String? {
            val varName = ctx.name.text
            if (variables.containsKey(varName)) {
                throw ErrorException()
            }

            variables[varName] = super.visit(ctx.expr())!!

            return null
        }

        override fun visitSetStatement(ctx: CCC3Parser.SetStatementContext): String? {
            val varName = ctx.name.text
            if (!variables.containsKey(varName)) {
                throw ErrorException()
            }

            variables[varName] = super.visit(ctx.expr())!!

            return null
        }

        override fun visitIntExpr(ctx: CCC3Parser.IntExprContext): String? {
            return ctx.text
        }

        override fun visitStringExpr(ctx: CCC3Parser.StringExprContext): String? {
            if (variables.containsKey(ctx.text)) {
                return variables[ctx.text]
            } else {
                return ctx.text
            }
        }

        override fun visitBoolExpr(ctx: CCC3Parser.BoolExprContext): String? {
            return ctx.text
        }
    }

    try {
        visitor.visitFunction(function)
        return sb.toString()
    } catch (e: ErrorException) {
        return "ERROR"
    }

}

fun runFile(inputFile: String) {
    println("running $inputFile...")
    val sb = StringBuilder()

    val input = CharStreams.fromStream(File(inputFile).inputStream())
    val lexer = CCC3Lexer(input)
    val tokens = CommonTokenStream(lexer)
    val parser = CCC3Parser(tokens)
    val tree = parser.program()

    val visitor = object: CCC3BaseVisitor<Void?>() {

        override fun visitFunction(ctx: CCC3Parser.FunctionContext): Void? {
            try {
                sb.appendLine(evaluateFunction(ctx))
            } catch (e: ReturnException) {
                sb.appendLine(e.consoleText)
            }
            return null
        }
    }

    visitor.visit(tree)

    // ---

    val file = File("$inputFile.out")
    file.writeText(sb.toString())
    println()
}

fun main() {
    runFile("inputs/level3.in")
}