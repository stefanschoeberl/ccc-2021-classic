package dev.schoeberl.ccc.lv5

import CCC5BaseVisitor
import CCC5Lexer
import CCC5Parser
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import java.io.File
import java.lang.RuntimeException

class ReturnException(val returnValue: String): RuntimeException()
class ErrorException: RuntimeException()

fun evaluateFunction(function: CCC5Parser.FunctionContext, allFunctions: List<CCC5Parser.FunctionContext>, sb: StringBuilder, variables: MutableMap<String, String> = mutableMapOf<String, String>()) {

    fun executeInNewQueueContext(ctx: CCC5Parser.StatementListContext) {
        val statementQueue = mutableListOf<CCC5Parser.StatementListContext>()
        statementQueue.add(ctx)

        val visitor = object : CCC5BaseVisitor<String?>() {

            override fun visitPrintStatement(ctx: CCC5Parser.PrintStatementContext): String? {
                sb.append(super.visit(ctx.expr()))
                return null
            }

            override fun visitIfElseStatement(ctx: CCC5Parser.IfElseStatementContext): String? {
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

            override fun visitReturnStatement(ctx: CCC5Parser.ReturnStatementContext): String? {
                throw ReturnException(super.visit(ctx.expr())!!)
            }

            override fun visitVarStatement(ctx: CCC5Parser.VarStatementContext): String? {
                val varName = ctx.name.text
                if (variables.containsKey(varName)) {
                    throw ErrorException()
                }

                variables[varName] = super.visit(ctx.expr())!!

                return null
            }

            override fun visitSetStatement(ctx: CCC5Parser.SetStatementContext): String? {
                val varName = ctx.name.text
                if (!variables.containsKey(varName)) {
                    throw ErrorException()
                }

                variables[varName] = super.visit(ctx.expr())!!

                return null
            }

            override fun visitIntExpr(ctx: CCC5Parser.IntExprContext): String? {
                return ctx.text
            }

            override fun visitStringExpr(ctx: CCC5Parser.StringExprContext): String? {
                if (variables.containsKey(ctx.text)) {
                    return variables[ctx.text]
                } else {
                    return ctx.text
                }
            }

            override fun visitBoolExpr(ctx: CCC5Parser.BoolExprContext): String? {
                return ctx.text
            }

            override fun visitCallExpr(ctx: CCC5Parser.CallExprContext): String? {
                return super.visit(ctx.callExpression())
            }

            override fun visitPostponeStatment(ctx: CCC5Parser.PostponeStatmentContext): String? {
                statementQueue.add(ctx.statementList())
                return null
            }

            override fun visitCallExpression(ctx: CCC5Parser.CallExpressionContext): String? {
                val functionIndex = super.visit(ctx.expr())!!.toIntOrNull()
                if (functionIndex != null) {
                    try {
                        if (functionIndex - 1 < allFunctions.size) {

                            val localCopy = HashMap(variables)

                            evaluateFunction(allFunctions[functionIndex - 1], allFunctions, sb, localCopy)
                            throw RuntimeException("unreachable")
                        } else {
                            throw ErrorException()
                        }
                        return null
                    } catch (e: ReturnException) {
                        return e.returnValue
                    }
                } else {
                    throw ErrorException()
                }
            }
        }

        while (statementQueue.isNotEmpty()) {
            visitor.visit(statementQueue.removeFirst())
        }
    }

    executeInNewQueueContext(function.statementList())
    throw ReturnException("true")
}

fun runFile(inputFile: String) {
    println("running $inputFile...")
    val sb = StringBuilder()

    val input = CharStreams.fromStream(File(inputFile).inputStream())
    val lexer = CCC5Lexer(input)
    val tokens = CommonTokenStream(lexer)
    val parser = CCC5Parser(tokens)
    val tree = parser.program()

    val visitor = object: CCC5BaseVisitor<Void?>() {

        override fun visitFunction(ctx: CCC5Parser.FunctionContext): Void? {
            val functionSb = StringBuilder()
            try {
                evaluateFunction(ctx, tree.functions, functionSb)
                throw RuntimeException("unreachable")
            } catch (e: ReturnException) {
                sb.appendLine(functionSb.toString())
            } catch (e: ErrorException) {
                sb.appendLine("ERROR")
            } catch (e: StackOverflowError) {
                sb.appendLine("ERROR")
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
    runFile("inputs/level5.in")
}