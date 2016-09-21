/**
 * Created by Kay Lerch (https://twitter.com/KayLerch)
 *
 * Contribute to https://github.com/KayLerch/alexa-skills-kit-tellask-java
 *
 * Attached license applies.
 * This source is licensed under GNU GENERAL PUBLIC LICENSE Version 3 as of 29 June 2007
 */
package io.klerch.alexa.tellask.dummies.model;

import io.klerch.alexa.state.model.AlexaScope;
import io.klerch.alexa.state.model.AlexaStateIgnore;
import io.klerch.alexa.state.model.AlexaStateModel;
import io.klerch.alexa.state.model.AlexaStateSave;

import java.util.ArrayList;
import java.util.List;

public class StateModel extends AlexaStateModel {
    private String privateField;
    @AlexaStateSave
    public String sampleString;
    @AlexaStateSave(Scope = AlexaScope.USER) public String sampleUser;
    @AlexaStateSave(Scope = AlexaScope.APPLICATION) public boolean sampleApplication;
    @AlexaStateSave(Scope = AlexaScope.SESSION) public List<String> sampleSession = new ArrayList<>();
    @AlexaStateIgnore
    public String sampleIgnore;
    @AlexaStateIgnore(Scope= AlexaScope.SESSION) public String sampleIgnoreSession;
    @AlexaStateIgnore(Scope= AlexaScope.USER) public String sampleIgnoreUser;
    @AlexaStateIgnore(Scope= AlexaScope.APPLICATION) public String sampleIgnoreApplication;
    public StateModel() {}
}
