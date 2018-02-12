/*
 * uk.ac.macaulay.fearlusOWL: RandomChooser.java
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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectProperty;

/**
 * RandomChooser
 * 
 * 
 * 
 * @author Gary Polhill
 */
public class RandomChooser extends AbstractOntologyEditor {

  String subjectStr = null;
  String propertyStr = null;
  String choiceSetStr = null;
  Double probability = 1.0;

  /**
   * <!-- main -->
   * 
   * @param args
   */
  public static void main(String[] args) {
    RandomChooser obj = new RandomChooser();
    System.exit(obj.run(args));
  }

  @Override
  int parseOpt(String[] args, int i) {
    if(args[i].equals("-subject")) {
      subjectStr = args[++i];
    }
    else if(args[i].equals("-property")) {
      propertyStr = args[++i];
    }
    else if(args[i].equals("-choiceSet")) {
      choiceSetStr = args[++i];
    }
    else if(args[i].equals("-probability")) {
      probability = new Double(args[++i]);
    }
    else {
      i = super.parseOpt(args, i);
    }
    return i;
  }

  /**
   * <!-- step -->
   * 
   * @see uk.ac.macaulay.fearlusOWL.AbstractOntologyEditor#step(java.util.Set,
   *      java.util.Set)
   */
  @Override
  void step(Set<OWLAxiom> add, Set<OWLAxiom> remove) {
    if(rand.nextDouble() > probability) return;
    URI subject;
    URI property;
    URI choiceSet;
    try {
      subject = getUsableURI(subjectStr);
    }
    catch(URISyntaxException e) {
      System.err.println("Can't make a valid URI from " + subjectStr + ": " + e);
      System.exit(1);
      throw new RuntimeException("Panic!");
    }

    try {
      property = getUsableURI(propertyStr);
    }
    catch(URISyntaxException e) {
      System.err.println("Can't make a valid URI from " + propertyStr + ": " + e);
      System.exit(1);
      throw new RuntimeException("Panic!");
    }
    
    try {
      choiceSet = getUsableURI(choiceSetStr);
    }
    catch(URISyntaxException e) {
      System.err.println("Can't make a valid URI from " + choiceSetStr + ": " + e);
      System.exit(1);
      throw new RuntimeException("Panic!");
    }
    
    if(subject == null) {
      System.err.println("No subject specified");
      System.exit(1);
    }
    if(property == null) {
      System.err.println("No property specified");
      System.exit(1);
    }
    if(choiceSet == null) {
      System.err.println("No choice set specified");
      System.exit(1);
    }

    if(!ontology.containsObjectPropertyReference(property)) {
      System.err.println("The ontology does not have the property " + property);
      System.exit(1);
    }
    OWLObjectProperty owlProperty = factory.getOWLObjectProperty(property);

    if(!ontology.containsClassReference(choiceSet)) {
      System.err.println("The ontology does not have the class " + choiceSet);
      System.exit(1);
    }
    OWLClass owlChoiceSet = factory.getOWLClass(choiceSet);

    if(ontology.containsClassReference(subject)) {
      OWLClass owlSubjectClass = factory.getOWLClass(subject);
      for(OWLIndividual owlSubject: owlSubjectClass.getIndividuals(ontology)) {
        step(add, remove, owlSubject, owlProperty, owlChoiceSet);
      }
    }
    else if(ontology.containsIndividualReference(subject)) {
      OWLIndividual owlSubject = factory.getOWLIndividual(subject);
      step(add, remove, owlSubject, owlProperty, owlChoiceSet);
    }
    else {
      System.err.println("The ontology does not contain the individual or class " + subject);
      System.exit(1);
    }
  }

  private void step(Set<OWLAxiom> add, Set<OWLAxiom> remove, OWLIndividual subj, OWLObjectProperty prop,
      OWLClass choiceClass) {
    Set<OWLIndividual> choices = choiceClass.getIndividuals(ontology);
    OWLIndividual[] choiceArr = choices.toArray(new OWLIndividual[0]);
    OWLIndividual choice = choiceArr[rand.nextInt(choiceArr.length)];
    OWLIndividual curChoice = getFunctionalObject(subj.getObjectPropertyValues(ontology), prop);
    if(!choice.equals(curChoice)) {
      remove.add(factory.getOWLObjectPropertyAssertionAxiom(subj, prop, curChoice));
      add.add(factory.getOWLObjectPropertyAssertionAxiom(subj, prop, choice));
    }
  }
}
