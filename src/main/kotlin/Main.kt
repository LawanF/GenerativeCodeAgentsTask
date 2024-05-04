import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

private const val MAX_ATTEMPTS: Int = 1 // Min: 1
private val apiPyPath = "src/main/python/api.py"
private val promptTxtPath = "LLMIO/prompt.txt"
private val outputPyPath = "LLMIO/output.py"

// PRE: path gives a valid (content root or absolute) path to a python script.
fun getErrorFromPythonScript(path: String): String {
    val process: Process = Runtime.getRuntime().exec("python $path")
    process.waitFor()
    val stdError = BufferedReader(InputStreamReader(process.errorStream))
    val returnString = stdError.use(BufferedReader::readText)
    println(returnString)
    return returnString
}

// PRE: path gives a valid (content root or absolute) path to a python script.
fun getOutputFromPythonScript(path: String): String {
    val process: Process = Runtime.getRuntime().exec("python $path")
    process.waitFor()
    val stdInput = BufferedReader(InputStreamReader(process.inputStream))
    val returnString = stdInput.use(BufferedReader::readText)
    println(returnString)
    return returnString
}

// PRE: path gives a valid (content root or absolute) path to a file.
fun writeStringToFile(string: String, path: String) {
    val file: File = File(path)
    file.writeText(string)
}

// PRE: path gives a valid (content root or absolute) path to a file.
fun getStringFromFile(filePath: String) = File(filePath).readText()

// PRE: path gives a valid (content root or absolute) path to a file.
fun formPrompt(file: File, errorString: String) = """
Fix the following Python code:
```python
${file.readText()}
```
that gives the following error:
```
$errorString```
Return the whole script.
""".trimIndent()

fun main() {
    // Opening the input script and output script.
    println("Input the path to the python script: ")
    val pythonIn: File = File(readln())
    val pythonOut: File = File(outputPyPath)

    // Grab error if there is one.
    var errorString: String = getErrorFromPythonScript(pythonIn.absolutePath)

    // If there is an error.
    if (errorString.isNotEmpty()) {
        // Write the prompt to a text file that the api.py script can read.
        writeStringToFile(formPrompt(pythonIn, errorString), promptTxtPath)

        // Write the output of the LLM to output location.
        pythonOut.writeText(getOutputFromPythonScript(apiPyPath))

        // Get the new error.
        errorString = getErrorFromPythonScript(pythonOut.absolutePath)

        // Repeat the process until there no longer is an error.
        var attempts: Int = 1
        while (errorString.isNotEmpty() && attempts < MAX_ATTEMPTS) {
            writeStringToFile(formPrompt(pythonOut, errorString), promptTxtPath)
            pythonOut.writeText(getOutputFromPythonScript(apiPyPath))
            errorString = getErrorFromPythonScript(pythonOut.absolutePath)
            attempts++
        }

        if (errorString.isNotEmpty()) {
            println("Couldn't resolve the error. Last attempt stored in ${pythonOut.path}")
        } else {
            println("Error is resolved. Resulting code is stored in ${pythonOut.path}.")
        }

    } else {
        println("Your script ran without errors. No changes made.")
    }

//    val newFile: File = File("pythonScripts/output/output.txt")
//
//    var errorString: String = ""
//
//    if (file.exists() && file.path.takeLast(3) == ".py") {
//        errorString = getError(file.path)
//    } else {
//        throw IllegalArgumentException("File does not exist or is not a python script.")
//    }
//
//    while (errorString.isNotEmpty()) {
//        newFile.delete()
//        newFile.createNewFile()
//        newFile.writeText(callLLM(file, errorString))
//        errorString = ""
//    }
}