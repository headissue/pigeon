# headissue Pigeon

![pigeon logo](http://headissue.com/~maxi/pigeon/pigeon.png)

Simple step-by-step feedback forms you can easily embed wherever you like.
Self hosted survey management for full control.
Every answer is sent one after another so even not fully filled out surveys provide information.

## QUICK START

### Get the sources

Clone this repository

    git clone https://github.com/headissue/pigeon.git

### Run

Start it with:

    cd pigeon
    mvn tomcat7:run

**Please Note**: For quick setup purposes we use an in-memory-database here. If you shut down tomcat your survey data will be gone.
For using pigeon in production see [Server Setup](#server-setup).

### Is Everything Up And Running?

With your browser, navigate to [http://localhost:8080/feedback/api/hello](http://localhost:8080/feedback/api/hello). You should be greeted by Pigeon. After that, have a look at [http://localhost:8080/feedback/mockup.html](http://localhost:8080/feedback/mockup.html) - we included an example feedback form there. Now try to add the example to your webpage:

### Embed A Survey

Make sure that you have [jQuery](http://jquery.org) or a jQuery-compatible library like [ender/jeesh](http://ender.jit.su) or [zepto](http://zeptojs.com) embedded in your page.

Then include this survey-div at the position you want to have the survey rendered:

    <div class="pigeon" data-pigeon-surveyid="1"></div>

Include this Javascript snippet right before `</body>`:

    <script>
    (function (c, w) {
      if (document.readyState === "complete")
        return c();
      if (w.attachEvent)
        w.attachEvent('onload', c);
      else
       w.addEventListener('load', c, false);
    })((function (){
      var d = document, t = 'script', s = d.createElement(t), b = d.getElementsByTagName(t)[0];
      s.async = true;
      s.src = 'http://localhost:8080/feedback/js/pigeon.js';
      b.parentNode.insertBefore(s, b);
    }), window);
    </script>

### Be The First To Complete A Survey

Now you can browse to your page. The survey should be the last thing which is rendered on the so you may have to be patient. Answer some questions (at least one) and verify that the answer has been processed correctly by going to `http://localhost:8080/feedback/admin/survey/1/export/csv` and downloading the survey as CSV file.

### Create Your Own Survey

Surveys are managed here `http://localhost:8080/feedback/admin/`. Right now, the surveys can be edited as JSON in plain text, a GUI might be added in further releases. Creating a new survey shows you an empty skeleton you can easily extend.

## <a name="documentation"></a>DOCUMENTATION

### Technologies

  * [Java](#java-frameworks)
  * JPA: [EclipseLink](http://www.eclipse.org/eclipselink/)
  * [CoffeeScript](http://coffeescript.org/)
  * [Gator](http://craig.is/riding/gators)

### Requirements

  * Servlet 2.5 eg. [Tomcat 6 or 7](http://tomcat.apache.org/index.html)
  * [PostgreSQL](http://www.postgresql.org/) version 8.4 or higher
  * a modern browser
  * [jQuery](http://jquery.org) or a jQuery-compatible library like [ender/jeesh](http://ender.jit.su) or [zepto](http://zeptojs.com) embedded in your page

### Goals

#### Quickly Gather Some Feedback

Our initial goal. As we evaluated some webservices, we came to the conclusion that no tool really matches our needs. Though most survey tools have a lot of features and customizations, most of them open in an overlay or even in a different window. We do not want our visitors to leave our page to fill out some huge evaluation form. We just want some kind of polling like

  * Question 1: "Did you overall like this page?" - "thumbs up" / "thumbs down"
  * Question 2: "What did you especially like? What bothers you here?" - "Fill in your suggestions!"

So we decided to create a tool for many different small surveys that show up wherever we want some quick feedback.

#### Make It A Good User Experience

We all hate popups and overlays we have to dismiss before continuing on what we did initially, before we got disturbed so disrespectful. We want the user to give feedback without being distracted from the content. We do not want to force clicks on "Close popup and be asked later". If our vistors decide to provide some information, the handling of the feedback form should be easy to grasp and should really just take half a minute of our users lifetime.
A simple design, visual feedback and one question at a time did the trick for us.

#### Get Answers Even When The User Abandoned It Midways

Survey services usually just save the answers if the user copletes a survey. But what happens when the user abandones the form after the second question? The first answer is lost in eternity. Pigeon sends each answer immediately, so you can access previous answers and notice when they left the survey. This provides some meta information about your survey. Maybe this particular question is too hard or ambiguous.

#### Continously Improve To Create Something More Than A Quick Poll Tool

Pigeon is flexible enough to be used as survey tool, though you obviously will miss many features you know from established survey webservices. Have a look at the Features and Roadmap to get a good overview of what Pigeon can handle and what is in the pipe.
When you are missing some functionality which 'definitely must be added!!11!!!', refer to the [Feature Request Page](https://github.com/headissue/pigeon/issues) or [help us](CONTRIBUTING.md) build that feature.

### <a name="server-setup"></a> Server Setup

#### Servlet

Make sure that [Maven](http://maven.apache.org/) is installed.

From command line:

    cd pigeon
    mvn compile war:war

this will generate a .war file in your `pigeon/target` folder. Just deploy this file to an Apache Tomcat or
any other application server running Servlet 2.5.

#### Database Setup

Get [PostgreSQL](http://www.postgresql.org/) and create the database

    createdb pigeon

  * There is an [sql file](src/main/resources/pigeon.1.sql) to create the tables and relations
    in PostgreSQL.
  * Configure the database connection for JPA. See [com.headissue.pigeon.setup.DeveloperSetup](src/main/java/com/headissue/pigeon/setup/DeveloperSetup.java)

#### Embed a survey

To embed a survey, you have to add two snippets into the body of your webpage.

##### HTML Survey Snippet

This snippet defines which survey is rendered and which additional information will be sent.

    <div class='pigeon'
        data-pigeon-surveyid='1'
        data-pigeon-pagekey='unique for every location'
        data-pigeon-userkey='randomly generated key'
        data-pigeon-userdata='{"a-b-test-key":"b"}'>
    </div>

  * `class='pigeon'`: [Required] Pigeons autoloading (See [Javascript](#javascript-snippet)) is searching for elements with this class.
  * `data-pigeon-surveyid`: [Required] the unique survey id. You can find this in the Administration.
  * `data-pigeon-pagekey`: [Optional] This information will always be sent. Here you can identify
    your page and location when you have more than one survey on a webpage (your pagekey may be
    "home-top" and "home-bottom") or when you have the same survey on multiple pages.
  * `data-pigeon-userkey`: [Optional] Use this to make sure consecutive answers are from a single user.
  * `data-pigeon-userdata`: [Optional] Additional custom data e.g. A/B-testing-flag, supported
    browser freatures, ... formatted as JSON

##### <a name="javascript-snippet"></a> Javascript Snippet

Include this at the end of your pages body. After your webpage has loaded, this script will include
the pigeon.js.

    <script>
    (function (c, w) {
      if (document.readyState === "complete")
        return c();
      if (w.attachEvent)
        w.attachEvent('onload', c);
      else
        w.addEventListener('load', c, false);
    })((function (){
      var d = document, t = 'script', s = d.createElement(t), b = d.getElementsByTagName(t)[0];
      s.async = true;
      s.src = 'http://localhost:8080/feedback/js/pigeon.js';
      b.parentNode.insertBefore(s, b);
    }), window);
    </script>

After it has loaded it tests for an supported browser (feature testing) with `Pigeon.supported()` and searches for surveys on this page with `Pigeon.findSurveysOnPage()`.

##### CSS class definitions

The display of feedback forms is handled by setting the class attribute on the wrapper div.
Possible states are:

  * `pigeon-loading`: on load, Pigeon trys to load a survey
  * `pigeon-loaded`: Pigeon did load a question and waits for the user to answer the question
  * `pigeon-fail`: the answer could not be sent
  * `pigeon-done`: the user completed the survey, an appreciation page is displayed

#### Browser Debugging

Debugging Pigeon with the browsers javascript console can be enabled by setting `Pigeon.debug` to true or request the page where the survey is embedded with `#pigeondebug=yxcvbnm`.

Example: http://example.com/page-with-survey.html#pigeondebug=yxcvbnm (a hash to not collide with other parameters)

After loading the page, something like the following lines should appear in the console:

    foundSurvey 500437
    embedSurvey 500437, <div id="pigeon-survey-500437" class="pigeon sidebar pigeon-loading" data-pigeon-surveyid="500437" data-pigeon-pagekey="vvk"></div>
    load data
    parse Object {id: 500437, name: "surveyName", questions: Array[4]}
    display PigeonBoolQuestion {id: 500438, title: "first question", text: "Do you like what you see?", answers: Array[2]}
    wrapHTML PigeonBoolQuestion {id: 500438, title: "first question", text: "Do you like what you see?", answers: Array[2]}, "bool"
    questionEl <div>…</div>

### Developer Knowledge

#### <a name="java-frameworks"></a> Java Frameworks

  * [Jersey](http://jersey.java.net/) _Reference Implementation for building RESTful Web services (JSR 311)_
  * [EclipseLink](http://www.eclipse.org/eclipselink/) _Provider for JPA_
  * [Google Guice](http://code.google.com/p/google-guice/) _Lightweight dependency injection framework_
  * [Jackson 1.9](http://jackson.codehaus.org/) _JSON processor (JSON parser + JSON generator) written in Java_
  * [Joda Time](http://joda-time.sourceforge.net/) _Java date and time API_

#### Persistence Configuration

The base JPA configuration is defined in the [persistence.xml](src/main/resources/META-INF/persistence.xml)

    <?xml version="1.0" encoding="UTF-8" ?>
    <persistence xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://java.sun.com/xml/ns/persistence http://java.sun.com/xml/ns/persistence/persistence_2_0.xsd"
             version="2.0" xmlns="http://java.sun.com/xml/ns/persistence">
      <persistence-unit name="pigeon-webapp" transaction-type="RESOURCE_LOCAL">
        <provider>org.eclipse.persistence.jpa.PersistenceProvider</provider>
        <class>com.headissue.pigeon.survey.Survey</class>
        <class>com.headissue.pigeon.survey.Question</class>
        <class>com.headissue.pigeon.survey.QuestionText</class>
        <class>com.headissue.pigeon.survey.answer.Answer</class>
        <class>com.headissue.pigeon.survey.answer.UserMap</class>
        <properties>
          <property name="javax.persistence.jdbc.driver" value="org.postgresql.Driver" />
          <!--
            url, user and password will be loaded/overwrited from the PersistenceConfig class
          -->
          <property name="javax.persistence.jdbc.url" value="jdbc:postgresql://localhost/pigeon"/>
          <!--
            Note: user will be reading from System.getProperty("user.name")
            <property name="javax.persistence.jdbc.user" value="user" />
            <property name="javax.persistence.jdbc.password" value="xy" />
          -->
          <property name="eclipselink.target-database"
                    value="org.eclipse.persistence.platform.database.PostgreSQLPlatform"/>
          <!--
            http://wiki.eclipse.org/EclipseLink/Examples/JPA/Logging
            LogLevel: OFF, SEVERE, WARNING, INFO, CONFIG, FINE, FINER, FINEST, ALL
          -->
          <property name="eclipselink.logging.level" value="CONFIG"/>
          <property name="eclipselink.logging.level.sql" value="FINE"/>
          <property name="eclipselink.logging.parameters" value="true"/>
          <property name="eclipselink.logging.session" value="true"/>
          <property name="eclipselink.orm.throw.exceptions" value="true"/>
          <!--
            More Information: http://wiki.eclipse.org/EclipseLink/Examples/JPA/DDL
          -->
          <!--
            <property name="eclipselink.ddl-generation" value="drop-and-create-tables" />
          -->
          <property name="eclipselink.ddl-generation.output-mode" value="database" />
        </properties>
      </persistence-unit>
    </persistence>

This project uses Postgresql 8.4 or higher. You may define different databases in the [Setup Package](src/main/java/com/headissue/pigeon/setup/).

    /**
     * Setup:
     *
     * - Persistence configuration
     */
    public class DeveloperSetup extends PigeonModule {
      {
        setPersistenceConfig(new PersistenceConfig() {
          {
            // drive is configured in persistence.xml
            // addDriver("org.postgresql.Driver");
            addUser("Database User");
            addUrl("jdbc:postgresql://localhost/pigeon");
            addPassword("XXX");
            // addProperty("eclipselink.ddl-generation", "create-tables"); add additional raw property directly
          }
          @Override
          public String getUnitName() {
            return "pigeon-webapp"; // unit name see in "persistence.xml"
          }
        });
      }
    }

#### Webapp Configuration

Two properties have to be set in the [web.xml](src/main/webapp/WEB-INF/web.xml):

  * Listener: needed by Guice to create an Injector
  * Filter: The GuiceFilter, which will include the other settings in the web.xml

##### Security

In a developement environment, you don't have to care for security. But in production you have to secure the `admin` url calls. Permissions are commented in the web.xml.

#### Guice Module

Setting the Guice Module to load can be done by:

  * setting `context-param` in the "web.xml"

        <context-param>
          <description>load this guice module</description>
          <param-name>com.headissue.pigeon.guice.Module</param-name>
          <param-value>com.headissue.pigeon.setup.DeveloperSetup</param-value>
        </context-param>

  * setting an enviroment variable `com_headissue_pigeon_guice_Module`

        export com_headissue_pigeon_guice_Module=com.headissue.pigeon.setup.DeveloperSetup

  * or as property parameter:

        -Dcom.headissue.pigeon.guice.Module=com.headissue.pigeon.setup.DeveloperSetup

Always use the full qualified classname!
