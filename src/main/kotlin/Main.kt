import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

private const val DEFAULT_MAX_ATTEMPTS: Int = 5 // Min: 1
private const val apiPyPath: String = "src/main/python/api.py"
private const val promptTxtPath: String = "LLMIO/input/prompt.txt"
private const val defaultOutputPyPath: String = "LLMIO/output/output.py"

// PRE: path gives a valid (content root or absolute) path to a python script.
fun getErrorFromPythonScript(path: String): String {
    val process: Process = Runtime.getRuntime().exec("python $path")
    process.waitFor()
    val stdError = BufferedReader(InputStreamReader(process.errorStream))
    return stdError.use(BufferedReader::readText)
}

// PRE: path gives a valid (content root or absolute) path to a python script.
fun getOutputFromPythonScript(path: String): String {
    val process: Process = Runtime.getRuntime().exec("python $path")
    process.waitFor()
    val stdInput = BufferedReader(InputStreamReader(process.inputStream))
    return stdInput.use(BufferedReader::readText)
}

// PRE: path gives a valid (content root or absolute) path to a file.
fun writeStringToFile(
    string: String,
    path: String,
) {
    val file = File(path)
    file.writeText(string)
}

// PRE: path gives a valid (content root or absolute) path to a file.
fun formPrompt(
    file: File,
    errorString: String,
) = """
Fix the following Python code:
```python
${file.readText()}file.readText()}
```
that gives the following error:
```
$errorString```
Return the whole script.
    """.trimIndent()

fun main() {
    // Opening the input script and output script.
    println("Input the path to the python script: ")
    val pythonIn = File(readln())

    println("Note that the destination file will be overridden if it exists.")
    println("Input the desired destination file (leave empty for LLMIO/output/output.py): ")
    val pythonOut: File =
        readln().let { path ->
            if (path.isEmpty()) {
                File(defaultOutputPyPath)
            } else {
                File(path)
            }
        }

    // Get error if there is one.
    var errorString: String = getErrorFromPythonScript(pythonIn.absolutePath)

    // If there is an error.
    if (errorString.isNotEmpty()) {
        // Write the prompt to a text file that the api.py script can read.
        writeStringToFile(formPrompt(pythonIn, errorString), promptTxtPath)

        // Write the output of the LLM to output location.
        pythonOut.writeText(getOutputFromPythonScript(apiPyPath))

        // Get the new error.
        errorString = getErrorFromPythonScript(pythonOut.absolutePath)

        // Repeat the process until there no longer is an error, or if number of max attempts is reached.
        var attempts: Int = 1
        while (errorString.isNotEmpty() && attempts < DEFAULT_MAX_ATTEMPTS) {
            writeStringToFile(formPrompt(pythonOut, errorString), promptTxtPath)
            pythonOut.writeText(getOutputFromPythonScript(apiPyPath))
            errorString = getErrorFromPythonScript(pythonOut.absolutePath)
            attempts++
        }

        if (errorString.isNotEmpty()) {
            println("Couldn't resolve the error(s). Last attempt stored in ${pythonOut.path}")
        } else {
            println("Error(s) resolved. Resulting code is stored in ${pythonOut.path}.")
        }
    } else {
        println("Your script ran without errors. No changes made.")
    }
}
