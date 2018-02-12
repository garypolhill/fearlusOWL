/*
 * uk.ac.macaulay.fearlusOWL: AbstractOntologyEditor.java
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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.inference.OWLReasoner;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.inference.UnsupportedReasonerOperationException;
import org.semanticweb.owl.io.RDFXMLOntologyFormat;
import org.semanticweb.owl.model.AddAxiom;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLConstant;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataPropertyExpression;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLObjectPropertyExpression;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyAnnotationAxiom;
import org.semanticweb.owl.model.OWLOntologyChangeException;
import org.semanticweb.owl.model.OWLOntologyCreationException;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.model.OWLOntologyStorageException;
import org.semanticweb.owl.model.RemoveAxiom;
import org.semanticweb.owl.model.UnknownOWLOntologyException;
import org.semanticweb.owl.util.InferredAxiomGenerator;
import org.semanticweb.owl.util.InferredAxiomGeneratorException;
import org.semanticweb.owl.util.InferredClassAssertionAxiomGenerator;
import org.semanticweb.owl.util.InferredDataPropertyCharacteristicAxiomGenerator;
import org.semanticweb.owl.util.InferredDisjointClassesAxiomGenerator;
import org.semanticweb.owl.util.InferredEquivalentClassAxiomGenerator;
import org.semanticweb.owl.util.InferredEquivalentDataPropertiesAxiomGenerator;
import org.semanticweb.owl.util.InferredEquivalentObjectPropertyAxiomGenerator;
import org.semanticweb.owl.util.InferredInverseObjectPropertiesAxiomGenerator;
import org.semanticweb.owl.util.InferredObjectPropertyCharacteristicAxiomGenerator;
import org.semanticweb.owl.util.InferredOntologyGenerator;
import org.semanticweb.owl.util.InferredPropertyAssertionGenerator;
import org.semanticweb.owl.util.InferredSubClassAxiomGenerator;
import org.semanticweb.owl.util.InferredSubDataPropertyAxiomGenerator;
import org.semanticweb.owl.util.InferredSubObjectPropertyAxiomGenerator;
import org.semanticweb.owl.util.SimpleURIMapper;
import org.semanticweb.owl.vocab.XSDVocabulary;

/**
 * AbstractOntologyEditor
 * 
 * 
 * 
 * @author Gary Polhill
 */
public abstract class AbstractOntologyEditor {
  private static final String DEFAULT_INDIVIDUAL_URI_PREFIX = "thing";
  OWLOntologyManager manager;
  OWLOntology ontology;
  OWLDataFactory factory;
  URI physicalIn;
  URI physicalOut;
  Random rand;
  Set<OWLAxiom> addAxioms;
  Set<OWLAxiom> removeAxioms;
  Map<String, Integer> individualCounter;
  int nullIndividualCounter;
  Class<OWLReasoner> reasonerClass = null;

  public static final URI PREVIOUS_ONTOLOGY =
    URI.create("http://www.macaulay.ac.uk/obiama/fearlusOWLObiama.owl#previousOntology");
  public static final URI ONTOLOGY_CREATED_BY =
    URI.create("http://www.macaulay.ac.uk/obiama/fearlusOWLObiama.owl#ontologyCreatedBy");

  int parseOpt(String args[], int i) {
    throw new RuntimeException("Command line argument " + args[i] + " not recognised");
  }

  @SuppressWarnings("unchecked")
  int getOpts(String args[]) {
    int i = 0;

    while(i < args.length && args[i].startsWith("-")) {
      if(args[i].equals("-fearlus")) {
        manager.addURIMapper(new SimpleURIMapper(FearlusOntology.ONTOLOGY_URI, URI.create(args[i + 1])));
        i++;
      }
      else if(args[i].equals("-seed")) {
        rand = new Random(Long.parseLong(args[i + 1]));
        i++;
      }
      else if(args[i].equals("-reasoner")) {
        try {
          reasonerClass = (Class<OWLReasoner>)Class.forName(args[i + 1]);
        }
        catch(ClassNotFoundException e) {
          System.err.println("Cannot find requested reasoner class " + args[i + 1] + ": " + e);
          System.exit(1);
        }
        i++;
      }
      else {
        i = parseOpt(args, i);
      }
      i++;
    }

    return i;
  }

  public void init(String args[]) throws OWLOntologyCreationException {
    manager = OWLManager.createOWLOntologyManager();
    factory = manager.getOWLDataFactory();
    rand = new Random();
    addAxioms = new HashSet<OWLAxiom>();
    removeAxioms = new HashSet<OWLAxiom>();
    individualCounter = new HashMap<String, Integer>();
    nullIndividualCounter = 0;

    int start_arg = getOpts(args);

    if(start_arg + 1 >= args.length) {
      System.err.println("You need to supply an 'in' ontology and an 'out' ontology");
      System.exit(1);
    }

    physicalIn = URI.create(args[start_arg]);
    physicalOut = URI.create(args[start_arg + 1]);

    System.out.println("Loading ontology from physical URI: " + physicalIn);
    ontology = manager.loadOntologyFromPhysicalURI(physicalIn);
    System.out.println("Loaded ontology from physical URI: " + physicalIn);

    Pattern indNumPattern = Pattern.compile(".*#(\\D+)(\\d+)");
    for(OWLIndividual ind: ontology.getReferencedIndividuals()) {
      Matcher indNumMatcher = indNumPattern.matcher(ind.getURI().toString());
      if(indNumMatcher.matches() && indNumMatcher.groupCount() == 2) {
        String name = indNumMatcher.group(1);
        int id = Integer.parseInt(indNumMatcher.group(2));

        if(!individualCounter.containsKey(name) || individualCounter.get(name) < id) {
          individualCounter.put(indNumMatcher.group(1), Integer.parseInt(indNumMatcher.group(2)));
        }
      }
    }
  }

  public void save() throws UnknownOWLOntologyException, OWLOntologyStorageException {
    System.out.println("Saving ontology to physical URI: " + physicalOut);
    manager.saveOntology(ontology, new RDFXMLOntologyFormat(), physicalOut);
    System.out.println("Saved ontology to physical URI: " + physicalOut);
  }

  public int run(String args[]) {
    try {
      init(args);
      System.out.println("Stepping " + this.getClass().getName());
      step(addAxioms, removeAxioms);
      for(OWLOntologyAnnotationAxiom annotation: ontology.getAnnotations(ontology)) {
        URI annotationURI = annotation.getAnnotation().getAnnotationURI();
        if(annotationURI.equals(PREVIOUS_ONTOLOGY) || annotationURI.equals(ONTOLOGY_CREATED_BY)) {
          removeAxioms.add(annotation);
        }
      }
      addAxioms.add(factory.getOWLOntologyAnnotationAxiom(ontology, factory.getOWLConstantAnnotation(PREVIOUS_ONTOLOGY,
          factory.getOWLTypedConstant(physicalIn.toString(), factory.getOWLDataType(XSDVocabulary.ANY_URI.getURI())))));
      addAxioms.add(factory.getOWLOntologyAnnotationAxiom(ontology, factory.getOWLConstantAnnotation(
          ONTOLOGY_CREATED_BY, factory.getOWLTypedConstant(this.getClass().getName()))));
      System.out.println("Acquired axiom changes (" + addAxioms.size() + " additions, " + removeAxioms.size()
        + " removals)");

      List<RemoveAxiom> axiomsToRemove = new LinkedList<RemoveAxiom>();

      for(OWLAxiom axiom: removeAxioms) {
        axiomsToRemove.add(new RemoveAxiom(ontology, axiom));
      }

      manager.applyChanges(axiomsToRemove);

      List<AddAxiom> axiomsToAdd = new LinkedList<AddAxiom>();

      for(OWLAxiom axiom: addAxioms) {
        axiomsToAdd.add(new AddAxiom(ontology, axiom));
      }

      manager.applyChanges(axiomsToAdd);
      System.out.println("Applied the changes");

      if(reasonerClass != null) {
        try {
          OWLReasoner reasoner = getReasonerOrDie();
          reasoner.classify();

          try {
            reasoner.realise();
          }
          catch(UnsupportedReasonerOperationException e) {
            // Ignore it
          }

          List<InferredAxiomGenerator<? extends OWLAxiom>> gens =
            new ArrayList<InferredAxiomGenerator<? extends OWLAxiom>>();

          gens.add(new InferredDataPropertyCharacteristicAxiomGenerator());
          gens.add(new InferredDisjointClassesAxiomGenerator());
          gens.add(new InferredEquivalentClassAxiomGenerator());
          gens.add(new InferredEquivalentDataPropertiesAxiomGenerator());
          gens.add(new InferredEquivalentObjectPropertyAxiomGenerator());
          gens.add(new InferredInverseObjectPropertiesAxiomGenerator());
          gens.add(new InferredObjectPropertyCharacteristicAxiomGenerator());
          gens.add(new InferredSubClassAxiomGenerator());
          gens.add(new InferredSubDataPropertyAxiomGenerator());
          gens.add(new InferredSubObjectPropertyAxiomGenerator());

          gens.add(new InferredClassAssertionAxiomGenerator());
          gens.add(new InferredPropertyAssertionGenerator());

          InferredOntologyGenerator iog = new InferredOntologyGenerator(reasoner, gens);

          System.out.println("Acquired inferred ontology changes");

          iog.fillOntology(manager, ontology);

          System.out.println("Applied inferred ontology changes");
        }
        catch(OWLReasonerException e) {
          System.err.println("Problem reasoning: " + e);
          return 1;
        }
        catch(InferredAxiomGeneratorException e) {
          System.err.println("Problem building inferred axioms: " + e);
          return 1;
        }
        catch(OWLOntologyChangeException e) {
          System.err.println("Problem updating ontology with inferred axioms: " + e);
          return 1;
        }
      }

      save();
    }
    catch(IllegalArgumentException e) {
      System.err.println("Unrecognised argument: " + e);
      return 1;
    }
    catch(OWLOntologyCreationException e) {
      System.err.println("Problem loading ontology " + physicalIn + ": " + e);
      return 1;
    }
    catch(UnknownOWLOntologyException e) {
      System.err.println("Problem saving ontology " + physicalOut + ": " + e);
      return 1;
    }
    catch(OWLOntologyStorageException e) {
      System.err.println("Problem saving ontology " + physicalOut + ": " + e);
      return 1;
    }
    catch(OWLOntologyChangeException e) {
      System.err.println("Problem updating ontology: " + e);
      return 1;
    }
    return 0;
  }

  public Double getFunctionalDouble(Map<OWLDataPropertyExpression, Set<OWLConstant>> data, OWLDataProperty prop) {
    Set<OWLConstant> values = data.get(prop);
    Double d = null;

    if(values == null) return null;
    for(OWLConstant value: values) {
      if(d == null) {
        d = Double.parseDouble(value.getLiteral());
      }
      else {
        // TODO exception
      }
    }
    return d;
  }

  public Integer getFunctionalInteger(Map<OWLDataPropertyExpression, Set<OWLConstant>> data, OWLDataProperty prop) {
    Set<OWLConstant> values = data.get(prop);
    Integer i = null;

    if(values == null) return null;
    for(OWLConstant value: values) {
      if(i == null) {
        i = Integer.parseInt(value.getLiteral());
      }
      else {
        // TODO exception
      }
    }
    return i;
  }

  public OWLIndividual getFunctionalObject(Map<OWLObjectPropertyExpression, Set<OWLIndividual>> data,
      OWLObjectProperty prop) {
    Set<OWLIndividual> values = data.get(prop);
    OWLIndividual ind = null;

    if(values == null) return null;
    for(OWLIndividual value: values) {
      if(ind == null) {
        ind = value;
      }
      else {
        // TODO exception
      }
    }
    return ind;
  }

  public URI getNewIndividualURI(String prefix) {
    if(prefix == null) prefix = DEFAULT_INDIVIDUAL_URI_PREFIX;
    if(!individualCounter.containsKey(prefix)) {
      individualCounter.put(prefix, 0);
    }
    URI uri;
    do {
      individualCounter.put(prefix, individualCounter.get(prefix) + 1);
      uri = URI.create(ontology.getURI() + "#" + prefix + individualCounter.get(prefix));
    } while(ontology.containsIndividualReference(uri));
    return uri;
  }

  public URI getUsableURI(String str) throws URISyntaxException {
    if(str == null) return null;

    if(str.startsWith("http://") || str.startsWith("https://")) {
      return new URI(str);
    }
    else if(str.substring(0, 1).equals("#")) {
      return new URI(ontology.getURI() + str);
    }
    else {
      return new URI(ontology.getURI() + "#" + str);
    }
  }

  public OWLReasoner getReasoner() throws SecurityException, NoSuchMethodException, ClassNotFoundException,
      IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
    OWLReasoner reasoner = null;
    Constructor<OWLReasoner> builder;

    builder = reasonerClass.getConstructor(OWLOntologyManager.class);
    reasoner = builder.newInstance(manager);
    return reasoner;
  }

  public OWLReasoner getReasonerOrDie() {
    try {
      return getReasoner();
    }
    catch(Exception e) {
      System.err.println("Caught exception " + e.getClass() + " whilst building the reasoner: " + e);
      System.exit(1);
      throw new RuntimeException("Panic");
    }
  }

  abstract void step(Set<OWLAxiom> add, Set<OWLAxiom> remove);
}
