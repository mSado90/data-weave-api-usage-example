package org.mule.dw2.examples;

import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.mule.dw2.examples.pojos.Account;
import org.mule.dw2.examples.pojos.User;
import org.mule.weave.v2.runtime.DataWeaveResult;
import org.mule.weave.v2.runtime.DataWeaveScript;
import org.mule.weave.v2.runtime.DataWeaveScriptingEngine;
import org.mule.weave.v2.runtime.ScriptingBindings;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;


public class DWEngineTest {

    //Creates The default WeaveScripting Engine. There are some advance parameters that can be constructed with
    //But for your case this should be good enough. Also this instance can and is recommended be shared across multiple executions
    final DataWeaveScriptingEngine dataWeaveScriptingEngine = new DataWeaveScriptingEngine();


    @Test
    public void testJavaMapsToJavaMaps() {

        String script = "%dw 2.0\n" +
                "input payload application/java\n" + //Declares the input and what kind of input is in  your case is java  It can be as many as you want
                "output application/java\n" + //Declares the output
                "---\n" +
                "{" + //Mapping an Object to an Object doing field mapping
                "  name: payload.userName," +
                "  email: payload.emailAddress," +
                "  lastName: payload.userLastName" +
                "}";

        //Compile method is being overload with multiple utility functions
        // In your case I think the one passing the script as a string is the best
        //This is the result of compiling the expression. Though it says compile the output is an intepreted execution
        //It can be cached and reused by multiple threads concurrently
        final DataWeaveScript compiledExpression = dataWeaveScriptingEngine.compile(script);


        //In here we need to bind all the inputs that we want to be available at runtime
        final ScriptingBindings scriptingBindings = new ScriptingBindings();
        final HashMap<String, Object> userObject = new HashMap<>();
        userObject.put("userName", "Mariano");
        userObject.put("userLastName", "Achaval");
        userObject.put("emailAddress", "mariano.achaval@mulesoft.com");

        scriptingBindings.addBinding("payload", userObject);
        try {
            final DataWeaveResult result = compiledExpression.write(scriptingBindings);
            final Object content = result.getContent();
            assertThat(content, CoreMatchers.instanceOf(Map.class));
            Map<String, Object> resultMap = (Map<String, Object>) content;
            assertThat(resultMap.get("name"), CoreMatchers.is("Mariano"));
            assertThat(resultMap.get("lastName"), CoreMatchers.is("Achaval"));
            assertThat(resultMap.get("email"), CoreMatchers.is("mariano.achaval@mulesoft.com"));
        } catch (Exception e) {
            //Catch any exception
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testJavaPojoToJavaPojo() {

        String script = "%dw 2.0\n" +
                "input payload application/java\n" + //Declares the input and what kind of input is in  your case is java  It can be as many as you want
                "output application/java\n" + //Declares the output
                "---\n" +
                "{" + //Mapping an Object to an Object doing field mapping
                "  name: payload.userName," +
                "  email: payload.emailAddress," +
                "  lastName: payload.userLastName" +
                "} as Object {class: \"org.mule.dw2.examples.pojos.Account\"}"; //With this class information it hints the DW runtime to what pojo instance needs to be created

        //Compile method is being overload with multiple utility functions
        // In your case I think the one passing the script as a string is the best
        //This is the result of compiling the expression. Though it says compile the output is an intepreted execution
        //It can be cached and reused by multiple threads concurrently
        final DataWeaveScript compiledExpression = dataWeaveScriptingEngine.compile(script);


        //In here we need to bind all the inputs that we want to be available at runtime
        final ScriptingBindings scriptingBindings = new ScriptingBindings();
        final User user = new User();
        user.setUserName("Mariano");
        user.setUserLastName("Achaval");
        user.setEmailAddress("mariano.achaval@mulesoft.com");

        scriptingBindings.addBinding("payload", user);
        try {
            final DataWeaveResult result = compiledExpression.write(scriptingBindings);
            final Object content = result.getContent();
            assertThat(content, CoreMatchers.instanceOf(Account.class));
            Account resultMap = (Account) content;
            assertThat(resultMap.getName(), CoreMatchers.is("Mariano"));
            assertThat(resultMap.getLastName(), CoreMatchers.is("Achaval"));
            assertThat(resultMap.getEmail(), CoreMatchers.is("mariano.achaval@mulesoft.com"));
        } catch (Exception e) {
            //Catch any exception it may occurred during write. It will have the line number and the user message of what was wrong
            e.printStackTrace();
            fail(e.getMessage());
        }

    }
}
