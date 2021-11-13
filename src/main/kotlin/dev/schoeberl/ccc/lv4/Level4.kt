package dev.schoeberl.ccc.lv4

import CCC4BaseVisitor
import CCC4Lexer
import CCC4Parser
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import java.io.File
import java.lang.RuntimeException

class ReturnException(val consoleText: String): RuntimeException()
class ErrorException: RuntimeException()

fun evaluateFunction(function: CCC4Parser.FunctionContext): String {
    val sb = StringBuilder()

    val variables = mutableMapOf<String, String>()

    fun executeInNewQueueContext(ctx: CCC4Parser.StatementListContext) {
        val statementQueue = mutableListOf<CCC4Parser.StatementListContext>()
        statementQueue.add(ctx)

        val visitor = object : CCC4BaseVisitor<String?>() {

            override fun visitPrintStatement(ctx: CCC4Parser.PrintStatementContext): String? {
                sb.append(visit(ctx.expr()))
                return null
            }

            override fun visitIfElseStatement(ctx: CCC4Parser.IfElseStatementContext): String? {
                val evaluatedCondition = super.visit(ctx.condition)
                if (evaluatedCondition == "true") {
                    executeInNewQueueContext(ctx.thenBranch)
                } else if (evaluatedCondition == "false") {
                    executeInNewQueueContext(ctx.elseBranch)
                } else {
                    throw ErrorException()
                }

                return null
            }

            override fun visitReturnStatement(ctx: CCC4Parser.ReturnStatementContext?): String? {
                throw ReturnException(sb.toString())
            }

            override fun visitVarStatement(ctx: CCC4Parser.VarStatementContext): String? {
                val varName = ctx.name.text
                if (variables.containsKey(varName)) {
                    throw ErrorException()
                }

                variables[varName] = super.visit(ctx.expr())!!

                return null
            }

            override fun visitSetStatement(ctx: CCC4Parser.SetStatementContext): String? {
                val varName = ctx.name.text
                if (!variables.containsKey(varName)) {
                    throw ErrorException()
                }

                variables[varName] = super.visit(ctx.expr())!!

                return null
            }

            override fun visitIntExpr(ctx: CCC4Parser.IntExprContext): String? {
                return ctx.text
            }

            override fun visitStringExpr(ctx: CCC4Parser.StringExprContext): String? {
                if (variables.containsKey(ctx.text)) {
                    return variables[ctx.text]
                } else {
                    return ctx.text
                }
            }

            override fun visitBoolExpr(ctx: CCC4Parser.BoolExprContext): String? {
                return ctx.text
            }

            override fun visitPostponeStatment(ctx: CCC4Parser.PostponeStatmentContext): String? {
                statementQueue.add(ctx.statementList())
                return null
            }
        }

        while (statementQueue.isNotEmpty()) {
            visitor.visit(statementQueue.removeFirst())
        }
    }

    try {
        executeInNewQueueContext(function.statementList())
        return sb.toString()
    } catch (e: ErrorException) {
        return "ERROR"
    }

}

fun runFile(inputFile: String) {
    println("running $inputFile...")
    val sb = StringBuilder()

    val input = CharStreams.fromStream(File(inputFile).inputStream())
    val lexer = CCC4Lexer(input)
    val tokens = CommonTokenStream(lexer)
    val parser = CCC4Parser(tokens)
    val tree = parser.program()

    val visitor = object: CCC4BaseVisitor<Void?>() {

        override fun visitFunction(ctx: CCC4Parser.FunctionContext): Void? {
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
    runFile("inputs/level4.in")
}