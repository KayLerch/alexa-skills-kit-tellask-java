[![Join the chat at https://gitter.im/alexa-skills-kit-tellask-java/Lobby](https://badges.gitter.im/alexa-skills-kit-tellask-java/Lobby.svg)](https://gitter.im/alexa-skills-kit-tellask-java/Lobby?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Maven central](https://img.shields.io/badge/maven%20central-v0.2.1-orange.svg)](http://search.maven.org/#artifactdetails%7Cio.klerch%7Calexa-skills-kit-tellask-java%7C0.2.1%7Cjar)
![SonarQube Coverage](https://img.shields.io/badge/code%20coverage-88%25-green.svg)

# Alexa Tellask SDK for Java
This SDK is an extension to the Alexa Skills SDK for Java. It provides a __framework for handling
speechlet requests__ with multi-variant __response utterances organized in YAML__ files that make
it easy to __create localized skills__. This SDK also lets you build your __skill in declarative style__
and avoids a lot of boilerplate code.

## Key features
* Your entire code is released from having output speech hardcoded in your skill, because ..
* Response utterances and reprompts are sourced out to YAML files
* YAML files can be part of your JAR or can be stored in an AWS S3 bucket
* Have multiple YAML-files - one for each locale. That makes it easy to build
multi-language skills without code redundancy
* Have slots in those utterances which the engine fills up with values
from your POJO models or from an output given by your intent handlers
* Have multi-phrase-collections in your utterance to vary Alexa's replies
* Compatible with state models from [Alexa States SDK](https://github.com/KayLerch/alexa-skills-kit-states-java) which does all the
state management of your POJO models by using S3, Dynamo or Alexa session as a store.
* Speechlet handlers (either Lambda or Servlet) configurable with annotations
* Auto-registered intent listeners subscribe for one to many intents matching
custom criteria - it was never that flexible to react on intents
* Explicit exception handling in intent handlers to have Alexa react accordingly
in any situation

If you are more into Python you better check out John Wheeler's [flask-ask](https://github.com/johnwheeler/flask-ask) which is another powerful toolkit for building skills.

## How to use

Learn how to use this SDK by having a look into the samples -
 where you can find reference implementations for a multi-language skills.

Before you start make sure your IDE is set up for annotation processing. (How to for
[IDEA](https://www.jetbrains.com/help/idea/2016.2/configuring-annotation-processing.html) and [Eclipse](http://help.eclipse.org/neon/index.jsp?topic=%2Forg.eclipse.jdt.doc.isv%2Fguide%2Fjdt_apt_getting_started.htm))

Add below Maven dependency to your project.

```xml
<dependencies>
  ...
  <dependency>
    <groupId>io.klerch</groupId>
    <artifactId>alexa-skills-kit-tellask-java</artifactId>
    <version>0.2.1</version>
  </dependency>
  ...
</dependencies>
```

### Prepare a request handler
The request handler does what it says. It reacts on incoming speechlet requests and
replies with a response. You could either have your skill implementation in a Lambda function or in
a Servlet. Here's how you set up an _AlexaRequestStreamHandler_ for your Lambda function:

```java
@AlexaApplication(applicationIds = "amzn1.echo-sdk-ams.app.c26b1c82...")
public class MySpeechletHandler extends AlexaRequestStreamHandler {
}
```
You could also override a getter for providing a set of application ids supported
 by the handler. There's more you can set up for your skill, but let's keep things
 simple. Here's another example for an _AlexaHttpRequestServlet_ in case you want
 you skill implementation run on a webserver:

```java
@AlexaApplication(applicationIds = "amzn1.echo-sdk-ams.app.c26b1c82...")
public class MyHttpRequestServlet extends AlexaSpeechletServlet {
}
```

### Prepare your utterance YAML file
Having output speech in code is evil. You will know when you start
preparing your skill for another locale. This SDK organizes all the editorial
content of your skill in YAML files. You're much more flexible with this approach.
You manage those contents similar to how you do it in the Alexa developer console -
by using __intents__ and __slots__. This is a sample YAML file
```yaml
WelcomeSpeeches: "[Hello|Hi|Welcome|Hey]"

SayWelcome:
  Utterances:
    - "${WelcomeSpeeches} {name} <p>Nice to meet you.</p>"
    - "${WelcomeSpeeches} {name} for using my skill."
  Reprompts:
    - "What would you like to do now?"
    - "How can I help you?"

SaySorry:
  - "Sorry, something went wrong."

SayGoodBye:
  - "[Bye|See you|Good bye]"
```
_SayWelcome_, _SayGoodBye_, _SaySorry_ are __response intents__ you will refer to later on.
The first one has two __response utterances__ where the engine will pick one of them randomly.
Inside YAML files you can also use templates like _WelcomeSpeeches_ to reuse certain
speeches in utterances and reprompts by referring to them with _${templatename}_.
In templates but also directly in utterances you can have __multi-phrases__ wrapped in square brackets.
Only one of them is chosen by the engine randomly as well. That said Alexa got five ways of welcoming a user.
The utterances also contain an __output slot__ called _{name}_ which will be resolved by the engine.
All aforementioned features are also available for __reprompts__ which you define right below the general utterances.

### Create an AlexaLaunchHandler
which handles a launch event whenever your skill is started by the
user. You register your handler with an annotation.
```java
@AlexaLaunchListener
public class LaunchHandler implements AlexaLaunchHandler {

    @Override
    public AlexaOutput handleRequest(final AlexaInput alexaInput) throws AlexaRequestHandlerException {
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
you set up in the YAML file. You also give it a slot with name, value and
optionally a __slot output format__. There's one format for each supported SSML-tag in Alexa so the
slot value is not only put into the utterance but is optionally wrapped in an SSML tag.

You don't need to have explicit error handling in _handleRequest_. Whatever goes wrong
with your handler you are given the chance to react on it in the _handleError_ method.

From above example what it sends back to Alexa is something like this:

    <speak>Hey <phoneme alphabet="ipa" ph="Joe"></phoneme> <p>Nice to meet you.</p></speak>

### Create an AlexaIntentHandler
which handles all incoming intents. Like you did with the launch handler you
need to register it with an annotation. Secondly you have to tell it which intent(s)
your handler should listen for.

```java
@AlexaIntentListener(builtInIntents = {INTENT_CANCEL, INTENT_STOP}, priority = 100)
public class CancelIntentHandler implements AlexaIntentHandler {
    @Override
    public boolean verify(final AlexaInput input) {
        return true;
    }

    @Override
    public AlexaOutput handleRequest(final AlexaInput input) throws AlexaRequestHandlerException {
        return AlexaOutput.tell("SayGoodBye").build();
    }

    @Override
    public AlexaOutput handleError(final AlexaRequestHandlerException exception) {
        return AlexaOutput.tell("SaySorry").build();
    }
}
```
You can choose from one to many of the built-in intents of Alexa or you can provide names of
your custom intents defined in the intent schema in the Alexa developer console.

Whenever an intent is received from the speechlet handler the intent handler is
picked automatically in case its _verfiy_ returns true. This is how you could have multiple
handlers for the same intent (e.g. the YES-intent) and let the engine pick the correct
one based on certain conditions checked in the _verify_-method. If there's more than
one intent handler interested in the same intent and all of them also verify the request
then _priority_ comes into play.

Once again exception handling in _handleRequest_ is done for you from the outside and
errors will be routed to _handleError_ so you can react on it with output speech.

#### AlexaInput
The _AlexaInput_ is given to the launch- and intent handlers. It provides everything which
comes with the speechlet request and also gives you an _AlexaSessionStateHandler_ useful for
reading/writing state to Alexa session (learn more about state handlers in [Alexa States SDK](https://github.com/KayLerch/alexa-skills-kit-states-java))
Most important for you might be the intents coming in with a request. _AlexaInput_
has some really useful helpers so that you can check and get values from input intents.

Assume you work with an _AlexaInput_ in the _verfiy_method of an intent handler, the following
might be interesting to you:

```java
@Override
    public boolean verify(final AlexaInput input) {
        final boolean slotIsNumber = input.hasSlotIsNumber("slotName");
        final boolean slotIsNotBlank = input.hasSlotNotBlank("slotName");
        final boolean slotEquals = input.hasSlotIsEqual("slotName", "someValue");
        final boolean slotPhoneticEqual = input.hasSlotIsDoubleMetaphoneEqual("slotName", "drew");
        final boolean slotHasTrueValue = input.hasSlotIsTrue("slotName");
        // ...
    }
```

So you can check a slot for a number but also for a certain value. Moreover, you
could even check for a phonetic equivalent by levering the [Double Metaphone](https://en.wikipedia.org/wiki/Metaphone#Double_Metaphone) algorithm.
For example the line checking for value of "drew" with _hasSlotIsDoubleMetaphoneEqual_ will
return true if the slot value contains "true" as a value - which is a phonetic sibling of "drew".
Finally you can obtain a slots value with _getSlotValue_.

#### AlexaOutput
You saw _AlexaOutput_ is returned by the launch- and intent-handlers. There's a lot you
can provide to the output:
* Slots having values (optionally associated with an SSML format)
* POJO models from [States SDK](https://github.com/KayLerch/alexa-skills-kit-states-java) whose state will be saved by the engine
* Cards for sending to Alexa App
* a flag which decides for using reprompts (in case they are set up in the YAML)
* _tell_ and _ask_ to decide if the session ends after having Alexa respond with your utterance
* an option to override the locale

#### Provide slot values from POJO models
Instead of explicitly set slot values with _AlexaOutput_._putSlot_ you could also give
it a model class which extends from _AlexaStateModel_ and got fields with _AlexaSlotSave_-annotation.
Once you give an instance of this model class to _AlexaOutput_._putState_ the underlying
speechlet will put values into the slots of an utterance. Moreover, the speechlet also
takes care of writing state of _AlexaStateSave_-annotated fields to either Alexa session
or one of the persistence stores supported by [States SDK](https://github.com/KayLerch/alexa-skills-kit-states-java).
Learn more about POJO state models and _AlexaStateHandlers_ with [States SDK](https://github.com/KayLerch/alexa-skills-kit-states-java)

This is a typical POJO state model:
```java
public class Calculation extends AlexaStateModel {
    @AlexaStateSave(Scope = AlexaScope.USER)
    @AlexaSlotSave(slotName = "precision", formatAs = AlexaOutputFormat.NUMBER)
    private int precision = 1;

    @AlexaSlotSave(slotName = "result", formatAs = AlexaOutputFormat.NUMBER)
    @AlexaStateSave
    private double result = 0;
    // ...
}
```
And this is how you would read/write model state from and to a store. Also the _precision_
value will be filled up in an _output slot_ if it exists in a _response utterance_:
```java
@AlexaIntentListener(customIntents = "Precision")
public class PrecisionIntentHandler implements AlexaIntentHandler {
    @Override
    public boolean verify(final AlexaInput input) {
        return input.hasSlotIsNumber("decimalplaces");
    }
    @Override
    public AlexaOutput handleRequest(final AlexaInput input) throws AlexaRequestHandlerException {
        final AlexaStateHandler sessionHandler = input.getSessionStateHandler();
        final AlexaStateHandler dynamoHandler = new AWSDynamoStateHandler(sessionHandler.getSession());

        final Calculation calc = sessionHandler.readModel(Calculation.class)
            .orElse(dynamoHandler.readModel(Calculation.class)
            .orElse(dynamoHandler.createModel(Calculation.class)));

        cal.setPrecision(Integer.valueOf(input.getSlotValue("decimalplaces")));

        return AlexaOutput.ask("SayNewPrecision")
            .putState(calc)
            .build();
    }
    // ...
}
```
This also is a good example of how to subscribe / listen to an intent matching a
custom critera (a slot named _decimalplaces_ must have a numeric value).

A corresponding utterance in the YAML file could be the following:

```yaml
SayNewPrecision:
  - "From now on I round results for you to {precision} decimal places"
  - "Calculation results will be rounded to {precision} decimal places"
```

### Store your utterances YAMLs in an S3 bucket
Another great feature of this SDK is to source editorial content out of the JAR. By storing
utterance YAML-files in an S3 bucket you can easily work on speech phrases in your skill without
redeploying code.

The SDK uses an _UtteranceReader_ for accessing YAML files. By default _ResourceUtteranceReader_ is set
to read YAMLs from the _/resources_-folder in your project. The default file-structure is the following

    /resources
        /{locale}
            /utterances.yml

As an example you might end up with the following

    /resources
        /de-DE
            /utterances.yml
        /en-US
            /utterances.yml
        /en-UK
            /utterances.yml

Of course you can alter the file-structure as you want by customizing the _UtteranceReader_
in the speechlet handler. Override the _getUtteranceReader_ method and do something like:

```java
@AlexaApplication(applicationIds = "amzn1.echo-sdk-ams.app.c26b1c82...")
public class MySpeechletHandler extends AlexaRequestStreamHandler {
    @Override
    public UtteranceReader getUtteranceReader() {
        return new ResourceUtteranceReader("/leading/path", "/trailing/path/my-utterances.yml");
    }
}
```
and you will be able to structure your files like this:

    /resources
        /leading
            /path
                /en-US
                    /trailing
                        /path
                            /my-utterances.yml
                /en-UK
                    /trailing
                        /path
                            /my-utterances.yml

As you now know how to set up an _UtteranceReader_ you can leverage _S3UtteranceReader_
for having your skill load utterances from files you stored in S3. Let me demonstrate how
to set this up with the Servlet (you can do the same with _AlexaRequestStreamHandler_ for Lambda)

```java
@AlexaApplication(applicationIds = "amzn1.echo-sdk-ams.app.c26b1c82...")
public class MyHttpRequestServlet extends AlexaSpeechletServlet {
    @Override
        public UtteranceReader getUtteranceReader() {
            return new S3UtteranceReader("bucketName", "/leading/path", "/trailing/path/my-utterances.yml");
        }
}
```
Above file-structure applies - but now instead of your files being in the _/resources_-folder
they are read from the _bucketName_ bucket. Optionally you can give the _S3UtteranceReader_ an
_AmazonS3Client_ to provide custom credentials, another AWS region or some proxy configuration.
If you don't, _S3UtteranceReader_ uses the default AWS configuration from the runtime environment.

### That's it
Although there's even more to discover in this SDK you already got the most important
basics for creating a skill with Tellask SDK. There's a lot going on behind the scenes
but you won't care. You are just asked to:

1. Create a speechlet handler by creating a class either extending _AlexaRequestStreamHandler_ (for Lambda functions)
or _AlexaSpeechletServlet_ (for an external webservice)
2. Built YAML-files with multi-language utterances and reprompts
3. Create a set of intent handlers which extend from _AlexaIntentHandler_ and register
them with just adding the _AlexaIntentListener_-annotation
4. Create one launch handler which extends from _AlexaLaunchHandler_ and register it
with the _AlexaLaunchListener_-annotation

Rest of the time you spend on your actual skill logic. Good luck.

More details on this SDK can be found in the [Javadocs](https://kaylerch.github.io/alexa-skills-kit-tellask-java/)
