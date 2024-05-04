from openai import OpenAI

API_KEY = "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"
system_message = "You are an artificial intelligence assistant and you need to suggest fixes and improvements to the user's python code. Only return the code with no explanations."
prompt = open("LLMIO/prompt.txt").read()
model = "llama-3-8b-instruct"

messages = [
    {
        "role": "system",
        "content": system_message,
    },
    {
        "role": "user",
        "content": prompt,
    },
]

client = OpenAI(api_key=API_KEY, base_url="https://api.perplexity.ai")

response = client.chat.completions.create(
    model=model,
    messages=messages,
)
response_content = response.choices[0].message.content

def parseBackticks(string):
    copyString = string.split("\n")
    if (copyString[0][0:3] == "```"):
        copyString = copyString[1:]
    if (copyString[-1][-3:] == "```"):
        copyString = copyString[:-1]

    return "\n".join(copyString)

print(parseBackticks(response.choices[0].message.content))
