package dev.schoeberl.ccc.lv2

import CCC2BaseVisitor
import CCC2Lexer
import CCC2Parser
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import java.io.File

fun runFile(inputFile: String) {
    println("running $inputFile...")
    val sb = StringBuilder()

    val input = CharStreams.fromStream(File(inputFile).inputStream())
    val lexer = CCC2Lexer(input)
    val tokens = CommonTokenStream(lexer)
    val parser = CCC2Parser(tokens)
    val tree = parser.program()

    var returned = false

    val visitor = object: CCC2BaseVisitor<Void?>() {

        override fun visitPrintStatement(ctx: CCC2Parser.PrintStatementContext): Void? {
            if (!returned) {
                sb.append(ctx.expr().text)
            }
            return null
        }

        override fun visitIfElseStatement(ctx: CCC2Parser.IfElseStatementContext): Void? {
            if (ctx.condition.text == "true") {
                super.visit(ctx.thenBranch)
            } else if (ctx.elseBranch != null) {
                super.visit(ctx.elseBranch)
            }
            return null
        }

        override fun visitReturnStatement(ctx: CCC2Parser.ReturnStatementContext?): Void? {
            returned = true
            return super.visitReturnStatement(ctx)

        }
    }

    visitor.visit(tree)

    // ---

    val file = File("$inputFile.out")
    file.writeText(sb.toString())
    println()
}

fun main() {
    runFile("inputs/level2.in")
}