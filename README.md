# Amazon Alexa Skills Kit Tellask SDK for Java
This SDK is an extension to the Alexa Skills SDK for Java. It provides a __framework for handling
speechlet requests__ with multi-variant __response utterances organized in YAML__ files that make
it easy to __create localized skills__. This SDK also lets you build your __skill in declarative style__
and avoids a lot of boilerplate code.

## Key features
* Your entire code is released from having speech phrases hardcoded in your skill, because ..:
* Response utterances and reprompts are held in YAML files
* Have slot placeholder in those utterances which the engine resolves with values
from your POJO models or from an output given by the intent handler
* Have multi-phrase-collections in your utterance to vary how Alexa replies
* Have multiple YAML-files one for each locale what makes it easy to build
multilingual skills with zero code redundancy
* Compatible with state models from Alexa Skills Kit States SDK which does all the
state handling for your POJO models by using S3, Dynamo or Alexa session as a store.
* Speechlet handlers (either Lambda or Servlet) configurable with annotations
* Request handler classes focus on the actual skill logic.
* Register request handlers with an annotation and without any need to bind it
to the speechlet handler.
* Have multiple request handlers for the same intent and route an intent
based on custom criteria (like presence of a specific slot)
* Explicit exception handling in request handlers to have Alexa react accordingly
in all situations.

## How to use

### Prepare a request handler
The request handler does what it says. It reacts on incoming speechlet request and
replies with a response. You could either host this code in a Lambda function or in
a Servlet. Here's how you set up a RequestStreamHandler for your Lambda function:

```java
@AlexaApplication(applicationIds = "amzn1.echo-sdk-ams.app.c26b1c82...")
public class MySpeechletHandler extends AlexaRequestStreamHandler {
}
```
You could also override a getter for providing a set of application ids supported
 by the handler. There's more you can set up at this point, but let's keep things
 simpel. Here's another example for a HttpRequestServlet:

```java
@AlexaApplication(applicationIds = "amzn1.echo-sdk-ams.app.c26b1c82...")
public class MyHttpRequestServlet extends AlexaSpeechletServlet {
}
```

### Prepare your utterance YAML file
Having response speech phrases in code is evil. You will know when you start
translating your skill for another locale. This SDK organizes all the editorial
content of your skill in YAML files. You're much more flexible with this approach.
You manage those contents similar to how you do it in the Alexa developer console -
by using Intents and slots. This is a sample YAML file
```yaml
SayWelcome:
  Utterances:
    - "[Hello|Hi|Welcome|Hey] {name} <p>Nice to meet you.</p>"
    - "Thanks {name} for using my skill."
  Reprompts:
    - "What would you like to do now?"
    - "How can I help you?"

SaySorry:
  - "Sorry, something went wrong."

SayGoodBye:
  - "[Bye|See you|Good bye]"
```
_SayWelcome_, _SayGoodBye_, _SaySorry_ are response intents you will refer to later on.
The first one has two utterances where the engine will pick one of them randomly. It
also got multiphrases wrapped in square brackets. Only one of them is chosen by the
engine as well. That said Alexa got six ways of welcoming a user. The utterances
also contain a slot _{name}_ which will be resolved by the engine. All these features
are also available for reprompts which you define right below the general utterances.

### Create an AlexaLaunchHandler
which handles a launch event - whenever your skill is started by the
user. You register your handler with an annotation.
```java
@AlexaLaunchListener
public class LaunchHandler implements AlexaLaunchHandler {

    @Override
    public AlexaOutput handleRequest(final AlexaInput alexaInput) {
        return AlexaOutput.ask("SayWelcome")
                .putSlot(new AlexaOutputSlot("name", "Joe").formatAs(PHONEME_IPA))
                .withReprompt(true)
                .build();
    }

    @Override
    public AlexaOutput handleError(final AlexaRequestHandlerException e) {
        return AlexaOutput.tell("SaySorry").build();
    }
}
```
There's only one launch handler per skill. The _handleRequest_ method simply asks
you to turn an _AlexaInput_ (providing all necessary information like the session)
into an _AlexaOutput_. The output object is where you refer to the response intent
you set up in the YAML file. You also give it a single slot with name, value and
optionally a format. There's one format for each possible SSML-tag in Alexa so the
slot value is not only put into the utterance but is optionally wrapped in an SSML tag.

You don't need to have explicit error handling in that method. Whatever goes wrong
with your handler you are given the chance to react on it in the _handleError_ method.

From above example what it send back to Alexa is something like this:

    <speak>Hey <phoneme alphabet="ipa" ph="Joe"></phoneme> <p>Nice to meet you.</p></speak>

### Create an AlexaIntentHandler
which handles all incoming intents. Like you did with the launch handler you
need to register it with an annotation. Secondly you have to tell it which intent
your handler should listen for.

```java
@AlexaIntentListener(intentType = AlexaIntentType.INTENT_CANCEL, priority = 100)
public class CancelIntentHandler implements AlexaIntentHandler {
    @Override
    public boolean verify(final AlexaInput input) {
        return true;
    }

    @Override
    public AlexaOutput handleRequest(final AlexaInput input) throws AlexaRequestHandlerException, AlexaStateException {
        return AlexaOutput.tell("SayGoodBye").build();
    }

    @Override
    public AlexaOutput handleError(final AlexaRequestHandlerException exception) {
        return AlexaOutput.tell("SaySorry").build();
    }
}
```
You can choose from one of the built-in intents of Alexa or you can choose _CUSTOM_
and give it the name of your custom intent defined in the intent schema in Alexa developer console.

Whenever an intent is received from the speechlet request handler this handler is
picket automatically in case _verfiy_ returns true. This is how you could have multiple
handlers for the same intent (e.g. the YES-intent) and let the engine pick the correct
one based on certain conditions checked in the _verfiy_-method. If there's more than
one handler listening on the same intent and verifing the request then _priority_ comes
into play.

Once again exception handling in _handleRequest_ is done for you from the outside and
errors will be routed to _handleError_ so you can react on it with output speech.

#### AlexaOutput
We saw _AlexaOutput_ is returned by the request handlers. There's a lot you can provide
to the output:
* Slots having values having specific SSML format
* POJO models from Alexa Skills Kit States SDK whose state will be saved by the engine
* Cards for sending it to Alexa App
* a flag which decides for using reprompts (in case they are set up in the YAML)
* _tell_ and _ask_ to decide if the session ends after having Alexa respond with your utterance

