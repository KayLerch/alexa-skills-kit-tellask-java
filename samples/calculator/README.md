# How to use Tellask SDK - Calculator skill

This skill uses [Alexa Tellask SDK](https://github.com/KayLerch/alexa-skills-kit-tellask-java)
in conjunction with [Alexa States SDK](https://github.com/KayLerch/alexa-skills-kit-states-java)
for a multilingual Alexa skill without having a single line of code redundancy.
All the output speeches are sourced out to YAML files and will be picked by [Alexa Tellask SDK](https://github.com/KayLerch/alexa-skills-kit-tellask-java) based on the locale coming in
with the speechlet request. Moreover, there's also a trivial session state management
introduced to this skill by leveraging [Alexa States SDK](https://github.com/KayLerch/alexa-skills-kit-states-java) which can be used stand-alone
as well.

## What this skill does
The skill calculates results of an addition, subtraction, multiplication or
division of two numbers. The skill remembers the last result within one user session so that it can
add, subtract, multiply or divide more numbers. You can also set the precision of returned results
by setting the decimal places Alexa rounds results to. The precision you set will be remembered
permanently for you by storing that information in AWS Dynamo DB.

Sounds complex? Well, have a look at the code and you will see that the implementation is
straight-forward, easy to understand and anything but heavyweight.

This skill has output speeches in three languages - all of them sourced out
to YAML files inside the _/resources/out/_-folder.

    You     >   "Start calculator sample"
    Alexa   >   "Welcome! Give me two numbers and an operator."
    You     >   "One plus two"
    Alexa   >   "The result of one plus three is three."
    You     >   "Multiply with four"
    Alexa   >   "The result of three times four is twelve"
    You     >   "Set precision to three"
    Alexa   >   "From now on I round results for you to three decimal places"
    You     >   "Divide by eleven"
    Alexa   >   "Twelve divided by eleven is one point zero nine one"
    You     >   "Stop"
    Alexa   >   "Bye"

You could have the same dialogue in German.

## How to set up this skill
1. Create a skill in Alexa developer console. Learn about [here](https://developer.amazon.com/public/community/post/Tx2XUAQ741IYQI4/How-to-Build-a-Multi-Language-Alexa-Skill)
2. Roll out the intent schemas for all of the three supported languages German, British English and American English. The intent schemas and utterances can be found in _/resources/in_-folder of this project
3. Add the application-id of your new skill in the _CalculationSpeechletHandler_-class of this project
4. Deploy this code to a Lambda function or use _CalculationHttpRequestServlet_ to host and endpoint for Alexa on your webserver
5. Point to your skill implementation with either the Lambda ARN or the endpoint URL of your webservice in the Alexa developer console


