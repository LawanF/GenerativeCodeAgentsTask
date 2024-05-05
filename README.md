# GenerativeCodeAgentsTask
A simple CLI tool that feeds a given Python script through a large language model in order to fix errors. 

### Setup
1. Configure the ``config.ini`` file in the top directory, adding your desired API key, base url and model.
2. Compile the Kotlin code.
```$ kotlinc src/main/kotlin/Main.kt -include-runtime -d codeAgent.jar```
3. Run the ``codeAgent.jar``.
```$ java -jar codeAgent.jar```
4. Input the source Python script.
5. Input the desired destination. If no destination is given, the resulting code is stored in ``LLMIO/output/output.py``.

### Note!
This was developed using the OpenAI API for Python, using Llama3 8b with a Perplexity API token.
