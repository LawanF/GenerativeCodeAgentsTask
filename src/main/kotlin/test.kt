import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader


fun main(args: Array<String>) {
    /*
    val outPyPath = "LLMInputOutput/output.py"
    val apiPyPath = "python/api.py"
    val dummyPath = "pythonScripts/testScripts/dummy.py"
    // File(outPyPath).writeText("Bread")
    println(getOutputFromPythonScript(apiPyPath))
     */

    /*
    var currentLine: String? = null
    val path: String = "python/api.py"
    val process: Process = Runtime.getRuntime().exec("python $path")
    val stdInput = BufferedReader(InputStreamReader(process.inputStream))
    process.waitFor()
     */

    var currentLine: String? = null

    val pythonPath: String = "src/main/python/api.py"

    val pythonProcess: Process = Runtime.getRuntime().exec("python $pythonPath")

    val stdInput = BufferedReader(InputStreamReader(pythonProcess.inputStream))

    val stdError = BufferedReader(InputStreamReader(pythonProcess.errorStream))

    // read the output from the command

    pythonProcess.waitFor()
    // read the output from the command

    currentLine = stdInput.readLine()

    println("Here is the standard output of the command:\n")
    while (currentLine != null) {
        println(currentLine)
        currentLine = stdInput.readLine()
    }


    // read any errors from the attempted command
    currentLine = stdError.readLine()

    println("Here is the standard error of the command (if any):\n")
    while (currentLine != null) {
        println(currentLine)
        currentLine = stdError.readLine()
    }
}



