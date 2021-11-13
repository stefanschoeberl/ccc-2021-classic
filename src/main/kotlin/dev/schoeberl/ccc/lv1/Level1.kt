package dev.schoeberl.ccc.lv1

import java.io.File

fun runFile(input: String) {
    println("running $input...")
    val lines = File(input).readLines()
    val it = lines.iterator()
    val sb = StringBuilder()

    // ---

    val loc = it.next().toInt()

    var code = ""
    for (i in 0 until loc) {
        code += it.next() + " "
    }

    val words = code.split(" ")

    for (i in 2 until words.size step 2) {
        sb.append(words[i])
    }

    // ---

    val file = File("$input.out")
    file.writeText(sb.toString())
    println()
}

fun main() {
    runFile("inputs/level1.in")
}