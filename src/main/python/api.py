from openai import OpenAI
import configparser

config = configparser.ConfigParser()
config.read("config.ini")
model_parameters = config["model_parameters"]


API_KEY = model_parameters["api_key"]
base_url = model_parameters["base_url"]

system_message = model_parameters["system_message"]
prompt = open("LLMIO/input/prompt.txt").read()
model = model_parameters["model"]
temperature = model_parameters["temperature"]

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

client = OpenAI(api_key=API_KEY, base_url=base_url)

response = client.chat.completions.create(
    model=model,
    messages=messages,
    temperature=1.0,
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
