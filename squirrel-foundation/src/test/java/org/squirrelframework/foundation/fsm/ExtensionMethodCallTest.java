package org.squirrelframework.foundation.fsm;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.squirrelframework.foundation.fsm.annotation.ContextInsensitive;
import org.squirrelframework.foundation.fsm.annotation.State;
import org.squirrelframework.foundation.fsm.annotation.StateMachineParameters;
import org.squirrelframework.foundation.fsm.annotation.States;
import org.squirrelframework.foundation.fsm.annotation.Transit;
import org.squirrelframework.foundation.fsm.annotation.Transitions;
import org.squirrelframework.foundation.fsm.impl.AbstractUntypedStateMachine;

public class ExtensionMethodCallTest {
    
    @Transitions({
        @Transit(from="A", to="B", on="ToB", callMethod="fromAToB", whenMvel="Excellect:::(context>=90)"),
    })
    @States({
        @State(name="A", exitCallMethod="leftA"), 
        @State(name="B", entryCallMethod="enterB")})
    @StateMachineParameters(stateType=String.class, eventType=String.class, contextType=Integer.class)
    @ContextInsensitive
    static class UntypedStateMachineBase extends AbstractUntypedStateMachine {
        
        protected StringBuilder logger = new StringBuilder();

        protected void leftA(String from, String to, String event) {
            logger.append("leftA");
        }
        
        protected void exitA(String from, String to, String event) {
            logger.append("exitA");
        }
        
        protected void beforeExitAny(String from, String to, String event) {
            logger.append("beforeExitAny");
        }
        
        protected void afterExitAny(String from, String to, String event) {
            logger.append("afterExitAny");
        }
        
        protected void enterB(String from, String to, String event) {
            logger.append("enterB");
        }
        
        protected void entryB(String from, String to, String event) {
            logger.append("entryB");
        }
        
        protected void beforeEntryAny(String from, String to, String event) {
            logger.append("beforeEntryAny");
        }
        
        protected void afterEntryAny(String from, String to, String event) {
            logger.append("afterEntryAny");
        }
        
        protected void fromAToB(String from, String to, String event) {
            logger.append("fromAToB");
        }
        
        protected void transitFromAToBOnToBWhenExcellect(String from, String to, String event) {
            logger.append("transitFromAToBOnToBWhenExcellect");
        }
        
        protected void transitFromAToBOnToB(String from, String to, String event) {
            logger.append("transitFromAToBOnToB");
        }
        
        protected void transitFromAnyToBOnToB(String from, String to, String event) {
            logger.append("transitFromAnyToBOnToB");
        }
        
        protected void transitFromAToAnyOnToB(String from, String to, String event) {
            logger.append("transitFromAToAnyOnToB");
        }
        
        protected void transitFromAToB(String from, String to, String event) {
            logger.append("transitFromAToB");
        }
        
        protected void onToB(String from, String to, String event) {
            logger.append("onToB");
        }
        
        @Override
        protected void beforeActionInvoked(Object fromState, Object toState, Object event, Object context) {
            addOptionalDot();
        }
        
        private void addOptionalDot() {
            if (logger.length() > 0) {
                logger.append('.');
            }
        }
        
        public String consumeLog() {
            final String result = logger.toString();
            logger = new StringBuilder();
            return result;
        }
    }
    
    @Test
    public void testExtensionMethodCallSequence() {
        UntypedStateMachineBuilder builder = StateMachineBuilderFactory.create(UntypedStateMachineBase.class);
        UntypedStateMachineBase fsm = builder.newUntypedStateMachine("A", 
                StateMachineConfiguration.create().setDebugEnabled(true), 
                new Object[0]);
        fsm.start();
        fsm.consumeLog();
        fsm.fire("ToB", 91);
        assertThat(fsm.consumeLog(), is(equalTo(
                "beforeExitAny.leftA.exitA.afterExitAny." +
                "fromAToB.transitFromAToBOnToBWhenExcellect.transitFromAToBOnToB." +
                "transitFromAnyToBOnToB.transitFromAToAnyOnToB.transitFromAToB.onToB." +
                "beforeEntryAny.enterB.entryB.afterEntryAny")));
        fsm.terminate();
        assertTrue(fsm.getListenerSize()==2); // start event listener to attach logger and terminate event listener to detach logger
    }
}
