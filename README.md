# MonumentGo
An interactive city exploring App that enables learning through interactive quizzes.


## OpenAI Key useage

When trying to compile the app you will run into an issue if you don't specify your OpenAI key.

You will have to specify the key in the `local.properties` file without parentheses:

```
openai_api_key=123123123123
```

> [!WARNING]
> The key can **NOT** be public. `local.properties` is the only place to put it, so that it will not get published!
