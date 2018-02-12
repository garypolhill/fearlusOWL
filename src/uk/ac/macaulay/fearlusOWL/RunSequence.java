/*
 * uk.ac.macaulay.fearlusOWL: RunSequence.java
 * 
 * Copyright (C) 2009 Macaulay Institute
 * 
 * This file is part of OWLAPITest1.
 * 
 * OWLAPITest1 is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * OWLAPITest1 is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with OWLAPITest1. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contact information: Gary Polhill Macaulay Institute, Craigiebuckler,
 * Aberdeen. AB15 8QH. UK. g.polhill@macaulay.ac.uk
 */
package uk.ac.macaulay.fearlusOWL;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.LocatorImpl;

/**
 * <!-- RunSequence -->
 * 
 * <p>
 * This is the main class to run fearlusOWL. The program takes two arguments:
 * </p>
 * 
 * <ol>
 * <li><i>'base URI'</i> -- the physical URI of a path to which to save the
 * ontologies (e.g. file:/home/me/fearlusOWL/run-1234/test.owl). The 'leaf' of
 * the path will be edited to form the names of the files to save.</li>
 * <li><i>schedule XML</i> -- the location of the XML file you want to use for
 * the schedule.</li>
 * </ol>
 * 
 * <p>
 * There is an XML schema file for the schedule: 'schedule.xsd'. The schedule
 * will require you to supply CSV files for the lookup tables.
 * </p>
 * 
 * @author Gary Polhill
 */
public class RunSequence extends DefaultHandler {

  private String baseURI;
  private Integer step = null;
  boolean dontRun = false;
  private String mypackage;

  public static final String DEFAULT_ONTOLOGY_SUFFIX = ".owl";

  private class Action {
    private Class<? extends AbstractOntologyEditor> actionClass;
    private String[] args;

    Action(Class<? extends AbstractOntologyEditor> actionClass, String[] args) {
      this.actionClass = actionClass;
      if(args == null) args = new String[0];
      this.args = args;
    }

    @SuppressWarnings("unchecked")
    Action(String actionClass, String[] args) throws ClassNotFoundException {
      this((Class<? extends AbstractOntologyEditor>)Class.forName(actionClass), args);
    }

    Class<? extends AbstractOntologyEditor> getActionClass() {
      return actionClass;
    }

    boolean run() {
      String[] fullargs = new String[args.length + 2];
      for(int i = 0; i < args.length; i++) {
        fullargs[i] = args[i];
      }
      if(step == null) {
        fullargs[fullargs.length - 2] = new String(baseURI + DEFAULT_ONTOLOGY_SUFFIX);
        step = 0;
      }
      else {
        fullargs[fullargs.length - 2] = new String(baseURI + "_Step_" + step + ".owl");
        step++;
      }
      fullargs[fullargs.length - 1] = new String(baseURI + "_Step_" + step + ".owl");

      try {
        AbstractOntologyEditor obj = actionClass.newInstance();
        if(dontRun) {
          StringBuffer buf = new StringBuffer();
          for(int i = 0; i < fullargs.length; i++) {
            buf.append(fullargs[i]);
            if(i < fullargs.length - 1) buf.append(" ");
          }
          System.out.println("Step " + step + ": pretending to run action " + obj.getClass().getName()
            + " with arguments " + buf);
          return true;
        }
        return obj.run(fullargs) == 0 ? true : false;
      }
      catch(InstantiationException e) {
        System.err.println("Error instantiating object of class " + actionClass + ": " + e);
        System.exit(1);
        throw new RuntimeException("Panic!");
      }
      catch(IllegalAccessException e) {
        System.err.println("Error instantiating object of class " + actionClass + ": " + e);
        System.exit(1);
        throw new RuntimeException("Panic!");
      }
    }
  }

  private class ActionSequence {
    private LinkedList<Action> actions;
    private int repetitions;

    ActionSequence() {
      actions = new LinkedList<Action>();
      repetitions = 1;
    }

    ActionSequence(int repetitions) {
      this();
      this.repetitions = repetitions;
    }

    void add(Action action) {
      actions.addLast(action);
    }

    boolean run() {
      for(int i = 1; i <= repetitions; i++) {
        for(Action action: actions) {
          if(!action.run()) {
            System.err.println("Action " + action.getActionClass() + " failed in repetition " + i + ", step " + step);
            return false;
          }
        }
      }
      return true;
    }
  }

  private LinkedList<ActionSequence> schedule;
  private XMLReader reader;
  private Stack<String> elementStack;
  private Class<?> ontologyClass;
  private String[] ontologyArgs;
  private String source;
  private Locator locator;
  private StringBuffer content;
  private String currentOption;
  private String currentImplementation;
  private Map<String, String> currentOptions;
  private ActionSequence currentSequence;

  public static final String ROOT_ELEMENT = "schedule";
  public static final String ONTOLOGY_ELEMENT = "ontology";
  public static final String SEQUENCE_ELEMENT = "sequence";
  public static final String IMPLEMENTATION_ELEMENT = "implementation";
  public static final String OPTION_ELEMENT = "option";
  public static final String ACTION_ELEMENT = "action";
  public static final String ARG_ATTRIBUTE = "arg";
  public static final String REPEAT_ATTRIBUTE = "repeat";

  /**
   * <!-- main -->
   * 
   * @param args
   */
  public static void main(String[] args) {
    if(args.length < 2) {
      System.err
          .println("You must supply a base URI and a schedule file on the command line. You may specify the -dry option.");
      System.exit(1);
    }

    RunSequence obj = new RunSequence();

    int i = 0;
    while(args[i].startsWith("-")) {
      if(args[i].equals("-dry")) {
        obj.dontRun = true;
      }
      else {
        System.err.println("Command line option " + args[i] + " not recognised");
      }
      i++;
    }

    try {
      if(args[i].endsWith(DEFAULT_ONTOLOGY_SUFFIX)) {
        obj.baseURI = args[i].substring(0, args[i].length() - DEFAULT_ONTOLOGY_SUFFIX.length());
      }
      else {
        obj.baseURI = args[i];
      }
      i++;
      obj.readSchedule(args[i]);
    }
    catch(ParserConfigurationException e) {
      System.err.println("Problem configuring parser for schedule file " + args[i] + ": " + e);
      System.exit(1);
    }
    catch(SAXException e) {
      System.err.println("Problem reading schedule file " + args[i] + ": " + e);
      System.exit(1);
    }
    catch(IOException e) {
      System.err.println("Unable to load schedule file " + args[i] + ": " + e);
      System.exit(1);
    }
    if(!obj.run()) {
      System.err.println("Run failed");
    }
    else {
      System.out.println("Run successful");
    }
  }

  public void readSchedule(String filename) throws ParserConfigurationException, SAXException, IOException {
    mypackage = getClass().getPackage().getName();
    SAXParserFactory factory = SAXParserFactory.newInstance();
    SAXParser parser = factory.newSAXParser();
    reader = parser.getXMLReader();
    reader.setContentHandler(this);
    setDocumentLocator(new LocatorImpl());
    BufferedReader buf;
    if(filename.startsWith("http://") || filename.startsWith("https://")) {
      URL url = new URL(filename);
      buf = new BufferedReader(new InputStreamReader(url.openStream()));
    }
    else {
      buf = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
    }
    InputSource input = new InputSource(buf);
    elementStack = new Stack<String>();
    source = filename;
    reader.parse(input);
  }

  public boolean run() {
    StringBuffer args = new StringBuffer();
    try {
      Method mainMethod = ontologyClass.getMethod("main", new Class<?>[] { String[].class });
      if(ontologyArgs == null) ontologyArgs = new String[0];
      String[] mainArgs = new String[ontologyArgs.length + 1];
      for(int i = 0; i < ontologyArgs.length; i++) {
        mainArgs[i] = ontologyArgs[i];
      }
      mainArgs[ontologyArgs.length] = new String(baseURI + ".owl");
      for(int i = 0; i < mainArgs.length; i++) {
        args.append(mainArgs[i]);
        if(i < mainArgs.length - 1) args.append(" ");
      }
      if(dontRun) {
        System.out.println("Initial: Pretending to run ontology generation class " + ontologyClass + " with arguments "
          + args);
      }
      else {
        mainMethod.invoke(null, (Object)mainArgs);
      }
    }
    catch(SecurityException e) {
      System.err.println("Unable to run main method of ontology class " + ontologyClass + ": " + e);
      return false;
    }
    catch(NoSuchMethodException e) {
      System.err.println("Ontology class " + ontologyClass + " has no main method");
      return false;
    }
    catch(IllegalArgumentException e) {
      System.err.println("Unable to run main method of ontology class " + ontologyClass + " with arguments " + args
        + ": " + e);
      return false;
    }
    catch(IllegalAccessException e) {
      System.err.println("Unable to run main method of ontology class " + ontologyClass + ": " + e);
      return false;
    }
    catch(InvocationTargetException e) {
      System.err.println("Unable to invoke main method on target ontology class " + ontologyClass + ": " + e);
      return false;
    }

    for(ActionSequence sequence: schedule) {
      if(!sequence.run()) return false;
    }

    return true;
  }

  private String[] getArgs(Map<String, String> options) {
    if(options == null || options.size() == 0) return null;

    int length = 0;
    for(String key: options.keySet()) {
      length++;
      String value = options.get(key);
      if(value.length() > 0 && value.matches(".*\\S.*")) {
        String[] optargs = value.split("\\s+");
        for(int j = 0; j < optargs.length; j++) {
          if(optargs[j].matches("\\S+")) {
            length++;
          }
        }
      }
    }

    String[] args = new String[length];

    int i = 0;
    for(String key: options.keySet()) {
      args[i] = key;
      i++;
      String value = options.get(key);
      if(value.length() > 0 && value.matches(".*\\S.*")) {
        String[] optargs = value.split("\\s+");
        for(int j = 0; j < optargs.length; j++) {
          if(optargs[j].matches("\\S+")) {
            args[i] = optargs[j];
            i++;
          }
        }
      }
    }
    return args;
  }

  private String localQName(String localName, String qName) {
    String str = null;
    try {
      str = reader.getFeature("http://www.xml.org/sax/features/namespace-prefixes") ? qName : localName;
    }
    catch(SAXNotRecognizedException e) {
      str = (localName == null || localName.equals("")) ? qName : localName;
    }
    catch(SAXNotSupportedException e) {
      str = (localName == null || localName.equals("")) ? qName : localName;
    }
    if(str == null || str.equals(""))
      throw new RuntimeException("Bug! (localName = " + localName + ", qName = " + qName + ")");
    if(str.equals(qName)) {
      String[] colons = str.split(":");
      str = colons[colons.length - 1];
      if(str == null || str.equals("")) {
        StringBuffer buf = new StringBuffer();
        for(int i = 0; i < colons.length; i++) {
          buf.append(colons[i]);
          if(i < colons.length - 1) buf.append(" : ");
        }
        throw new RuntimeException("Null last element in " + buf);
      }
    }
    if(str == null || str.equals(""))
      throw new RuntimeException("Bug! (localName = " + localName + ", qName = " + qName + ")");
    return str;
  }

  public void setDocumentLocator(Locator loc) {
    super.setDocumentLocator(loc);
    locator = loc;
  }

  private String location() {
    return " in schedule file " + source + ", line " + locator.getLineNumber() + ", column "
      + locator.getColumnNumber();
  }

  private void noAttributesAllowed(Attributes atts, String elementType, String elementName) throws SAXException {
    if(atts.getLength() > 0) {
      String attName = localQName(atts.getLocalName(0), atts.getQName(0));
      throw new SAXException("Attribute \"" + attName + "\" is not valid for " + elementType + " element <"
        + elementName + ">" + location());
    }
  }

  private String getSingleAttributeValue(String att, Attributes atts, String elementType, String elementName,
      boolean required) throws SAXException {
    String str = null;
    for(int i = 0; i < atts.getLength(); i++) {
      String attName = localQName(atts.getLocalName(i), atts.getQName(i));
      if(i > 0 || !attName.equals(att)) {
        throw new SAXException("Attribute \"" + attName + "\" is not valid for " + elementType + " element <"
          + elementName + ">" + location());
      }
      str = atts.getValue(i);
    }
    if(required && str == null) {
      throw new SAXException("Attribute \"" + att + "\" is required for " + elementType + " element <" + elementName
        + ">" + location());
    }
    return str;
  }

  private void noContentAllowed(String elementType, String elementName, StringBuffer content) throws SAXException {
    if(content != null) {
      throw new SAXException("The " + elementType + " element <" + elementName + "> should not have any text ("
        + content + ")" + location());
    }
  }

  public void startElement(String nameSpace, String localName, String qName, Attributes atts) throws SAXException {
    String useName = localQName(localName, qName);
    String enclosingName;
    try {
      enclosingName = elementStack.peek();
    }
    catch(EmptyStackException e) {
      enclosingName = null;
    }
    elementStack.push(useName);
    if(useName.equals(ROOT_ELEMENT)) {
      if(enclosingName != null) {
        throw new SAXException("<" + ROOT_ELEMENT + "> must be the root element" + location());
      }
      schedule = new LinkedList<ActionSequence>();
      ontologyClass = null;
      ontologyArgs = null;
      currentSequence = null;
      currentImplementation = null;
      currentOptions = null;
      currentOption = null;
      content = null;
    }
    else if(useName.equals(ONTOLOGY_ELEMENT)) {
      if(!enclosingName.equals(ROOT_ELEMENT)) {
        throw new SAXException("Ontology element <" + ONTOLOGY_ELEMENT + "> must be enclosed by the root element <"
          + ROOT_ELEMENT + ">" + location());
      }
      noAttributesAllowed(atts, "ontology", ONTOLOGY_ELEMENT);
      currentOptions = null;
      currentImplementation = null;
    }
    else if(useName.equals(SEQUENCE_ELEMENT)) {
      if(!enclosingName.equals(ROOT_ELEMENT)) {
        throw new SAXException("Sequence element <" + SEQUENCE_ELEMENT + "> must be enclosed by the root element <"
          + ROOT_ELEMENT + ">" + location());
      }
      String repetitions = getSingleAttributeValue(REPEAT_ATTRIBUTE, atts, "sequence", SEQUENCE_ELEMENT, false);
      if(repetitions == null) {
        currentSequence = new ActionSequence();
      }
      else {
        currentSequence = new ActionSequence(Integer.parseInt(repetitions));
      }
    }
    else if(useName.equals(ACTION_ELEMENT)) {
      if(!enclosingName.equals(SEQUENCE_ELEMENT)) {
        throw new SAXException("Action element <" + ACTION_ELEMENT + "> must be enclosed by the sequence element <"
          + SEQUENCE_ELEMENT + ">" + location());
      }
      noAttributesAllowed(atts, "action", ACTION_ELEMENT);
      currentOptions = null;
      currentImplementation = null;
    }
    else if(useName.equals(IMPLEMENTATION_ELEMENT)) {
      if(!(enclosingName.equals(ACTION_ELEMENT) || enclosingName.equals(ONTOLOGY_ELEMENT))) {
        throw new SAXException("Implementation element <" + IMPLEMENTATION_ELEMENT
          + " must be enclosed by either the ontology element <" + ONTOLOGY_ELEMENT + "> or the action element <"
          + ACTION_ELEMENT + ">" + location());
      }
      noAttributesAllowed(atts, "implementation", IMPLEMENTATION_ELEMENT);
      content = new StringBuffer();
    }
    else if(useName.equals(OPTION_ELEMENT)) {
      if(!(enclosingName.equals(ACTION_ELEMENT) || enclosingName.equals(ONTOLOGY_ELEMENT))) {
        throw new SAXException("Option element <" + OPTION_ELEMENT
          + " must be enclosed by either the ontology element <" + ONTOLOGY_ELEMENT + "> or the action element <"
          + ACTION_ELEMENT + ">" + location());
      }
      content = new StringBuffer();
      currentOption = getSingleAttributeValue(ARG_ATTRIBUTE, atts, "option", OPTION_ELEMENT, true);
    }
    else {
      throw new SAXException("Element <" + useName + "> not recognised" + location());
    }
  }

  public void characters(char[] chars, int start, int length) throws SAXException {
    boolean ok = true;
    if(content == null) {
      content = new StringBuffer();
      ok = false;
    }
    for(int i = 0; i < length; i++) {
      content.append(chars[start + i]);
    }
    if(!ok) {
      if(content.toString().matches("\\S")) {
        try {
          String curElement = elementStack.peek();
          throw new SAXException("Unexpected content (" + content + ") in element <" + curElement + ">" + location());
        }
        catch(EmptyStackException e) {
          throw new SAXException("Unexpected content (" + content + ")" + location());
        }
      }
      content = null;
    }
  }

  public void endElement(String nameSpace, String localName, String qName) throws SAXException {
    String useName = localQName(localName, qName);
    String popName = elementStack.pop();
    if(!popName.equals(useName)) {
      throw new SAXException("Element <" + useName + "> appears to close element <" + popName + ">" + location());
    }
    String enclosingName;
    try {
      enclosingName = elementStack.peek();
    }
    catch(EmptyStackException e) {
      enclosingName = null;
    }

    if(useName.equals(ROOT_ELEMENT)) {
      noContentAllowed("root", ROOT_ELEMENT, content);
    }
    else if(useName.equals(ONTOLOGY_ELEMENT)) {
      noContentAllowed("ontology", ONTOLOGY_ELEMENT, content);
      ontologyArgs = getArgs(currentOptions);
      currentOptions = null;
    }
    else if(useName.equals(SEQUENCE_ELEMENT)) {
      noContentAllowed("sequence", SEQUENCE_ELEMENT, content);
      schedule.addLast(currentSequence);
      currentSequence = null;
    }
    else if(useName.equals(ACTION_ELEMENT)) {
      noContentAllowed("action", ACTION_ELEMENT, content);
      try {
        currentSequence.add(new Action(currentImplementation, getArgs(currentOptions)));
      }
      catch(ClassNotFoundException e) {
        try {
          currentSequence.add(new Action(mypackage + "." + currentImplementation, getArgs(currentOptions)));
        }
        catch(ClassNotFoundException ee) {
          throw new SAXException("Action class " + currentImplementation + ", specified in" + location()
            + ", not found, and neither is " + mypackage + "." + currentImplementation);
        }
      }
      catch(ClassCastException e) {
        throw new SAXException("Proposed action class " + currentImplementation + " cannot be cast to an action class"
          + location());
      }
      currentImplementation = null;
      currentOptions = null;
    }
    else if(useName.equals(IMPLEMENTATION_ELEMENT)) {
      if(enclosingName.equals(ONTOLOGY_ELEMENT)) {
        try {
          ontologyClass = Class.forName(content.toString());
        }
        catch(ClassNotFoundException e) {
          try {
            ontologyClass = Class.forName(mypackage + "." + content.toString());
          }
          catch(ClassNotFoundException ee) {
            throw new SAXException("Ontology class " + content + ", specified in" + location()
              + ", not found, and neither is " + mypackage + "." + currentImplementation);
          }
        }
      }
      else {
        currentImplementation = content.toString();
      }
    }
    else if(useName.equals(OPTION_ELEMENT)) {
      if(currentOptions == null) {
        currentOptions = new HashMap<String, String>();
      }
      currentOptions.put(currentOption, content.toString());
    }
    content = null;
  }
}
