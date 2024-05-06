from configparser import ConfigParser
from openai import OpenAI

config = ConfigParser()
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

def parse_backticks(string):
    copy_string = string.split("\n")
    if (copy_string[0][0:3] == "```"):
        copy_string = copy_string[1:]
    if (copy_string[-1][-3:] == "```"):
        copy_string = copy_string[:-1]

    return "\n".join(copy_string)

print(parse_backticks(response.choices[0].message.content))
