/*
 * uk.ac.macaulay.fearlusOWL: AddSubpopulation.java
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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectProperty;

/**
 * AddSubpopulation
 * 
 * 
 * 
 * @author Gary Polhill
 */
public class AddSubpopulation extends AbstractOntologyEditor {

  public final String NORMAL_DIST_STR = "normal";
  public final String UNIFORM_DIST_STR = "uniform";

  String aspirationDist = null;
  double aspirationMin;
  double aspirationMax;
  double aspirationMean;
  double aspirationVar;

  String incomerDist = null;
  double incomerMin;
  double incomerMax;
  double incomerMean;
  double incomerVar;

  String offerDist = null;
  double offerMin;
  double offerMax;
  double offerMean;
  double offerVar;

  String imitateDist = null;
  double imitateMin;
  double imitateMax;
  double imitateMean;
  double imitateVar;

  String name = null;

  /**
   * <!-- main -->
   * 
   * @param args
   */
  public static void main(String[] args) {
    AddSubpopulation obj = new AddSubpopulation();
    System.exit(obj.run(args));
  }

  @Override
  int parseOpt(String[] args, int i) {
    if(args[i].equals("-aspiration")) {
      aspirationDist = args[++i];
      if(aspirationDist.equals(NORMAL_DIST_STR)) {
        aspirationMean = Double.parseDouble(args[++i]);
        aspirationVar = Double.parseDouble(args[++i]);
      }
      else if(aspirationDist.equals(UNIFORM_DIST_STR)) {
        aspirationMin = Double.parseDouble(args[++i]);
        aspirationMax = Double.parseDouble(args[++i]);
      }
      else {
        System.err.println("Distribution not recognised: " + aspirationDist);
        System.exit(1);
      }
    }
    else if(args[i].equals("-incomer")) {
      incomerDist = args[++i];
      if(incomerDist.equals(NORMAL_DIST_STR)) {
        incomerMean = Double.parseDouble(args[++i]);
        incomerVar = Double.parseDouble(args[++i]);
      }
      else if(incomerDist.equals(UNIFORM_DIST_STR)) {
        incomerMin = Double.parseDouble(args[++i]);
        incomerMax = Double.parseDouble(args[++i]);
      }
      else {
        System.err.println("Distribution not recognised: " + incomerDist);
        System.exit(1);
      }
    }
    else if(args[i].equals("-offer")) {
      offerDist = args[++i];
      if(offerDist.equals(NORMAL_DIST_STR)) {
        offerMean = Double.parseDouble(args[++i]);
        offerVar = Double.parseDouble(args[++i]);
      }
      else if(offerDist.equals(UNIFORM_DIST_STR)) {
        offerMin = Double.parseDouble(args[++i]);
        offerMax = Double.parseDouble(args[++i]);
      }
      else {
        System.err.println("Distribution not recognised: " + offerDist);
        System.exit(1);
      }
    }
    else if(args[i].equals("-imitate")) {
      imitateDist = args[++i];
      if(imitateDist.equals(NORMAL_DIST_STR)) {
        imitateMean = Double.parseDouble(args[++i]);
        imitateVar = Double.parseDouble(args[++i]);
      }
      else if(imitateDist.equals(UNIFORM_DIST_STR)) {
        imitateMin = Double.parseDouble(args[++i]);
        imitateMax = Double.parseDouble(args[++i]);
      }
      else {
        System.err.println("Distribution not recognised: " + imitateDist);
        System.exit(1);
      }
    }
    else if(args[i].equals("-name")) {
      name = args[++i];
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
    OWLClass subpopClass = factory.getOWLClass(FearlusOntology.SUBPOPULATION_CLASS_URI);
    OWLObjectProperty aspDistProp = factory.getOWLObjectProperty(FearlusOntology.ASPIRATION_DIST_PROP_URI);
    OWLObjectProperty incomerDistProp = factory.getOWLObjectProperty(FearlusOntology.INCOMER_OFFER_DIST_PROP_URI);
    OWLObjectProperty offerDistProp = factory.getOWLObjectProperty(FearlusOntology.LAND_OFFER_DIST_PROP_URI);
    OWLObjectProperty imitateDistProp = factory.getOWLObjectProperty(FearlusOntology.IMITATE_P_DIST_PROP_URI);

    if(aspirationDist == null) {
      System.err.println("You must specify an aspiration distribution");
      System.exit(1);
    }
    if(incomerDist == null) {
      System.err.println("You must specify an incomer offer distribution");
      System.exit(1);
    }
    if(offerDist == null) {
      System.err.println("You must specify a land offer threshold distribution");
      System.exit(1);
    }
    if(ontology.containsObjectPropertyReference(FearlusOntology.IMITATE_P_DIST_PROP_URI) && imitateDist == null) {
      System.err.println("This FEARLUS ontology requires you to specify an imitation probability distribution");
      System.exit(1);
    }
    else if(imitateDist != null) {
      System.err
          .println("Warning: This FEARLUS ontology does not require an imitation probability distribution. This will be ignored.");
      imitateDist = null;
    }

    URI subpopURI = name == null ? getNewIndividualURI("subpopulation") : URI.create(ontology.getURI() + "#" + name);
    OWLIndividual subpop = factory.getOWLIndividual(subpopURI);
    add.add(factory.getOWLClassAssertionAxiom(subpop, subpopClass));

    Map<Double, Map<Double, OWLIndividual>> normalDists = new HashMap<Double, Map<Double, OWLIndividual>>();
    Map<Double, Map<Double, OWLIndividual>> uniformDists = new HashMap<Double, Map<Double, OWLIndividual>>();

    OWLIndividual aspiration =
      getDistribution(aspirationDist, aspirationMin, aspirationMax, aspirationMean, aspirationVar, normalDists,
          uniformDists, add);
    add.add(factory.getOWLObjectPropertyAssertionAxiom(subpop, aspDistProp, aspiration));

    OWLIndividual incomer =
      getDistribution(incomerDist, incomerMin, incomerMax, incomerMean, incomerVar, normalDists, uniformDists, add);
    add.add(factory.getOWLObjectPropertyAssertionAxiom(subpop, incomerDistProp, incomer));

    OWLIndividual offer =
      getDistribution(offerDist, offerMin, offerMax, offerMean, offerVar, normalDists, uniformDists, add);
    add.add(factory.getOWLObjectPropertyAssertionAxiom(subpop, offerDistProp, offer));

    if(imitateDist != null) {
      OWLIndividual imitate =
        getDistribution(imitateDist, imitateMin, imitateMax, imitateMean, imitateVar, normalDists, uniformDists, add);
      add.add(factory.getOWLObjectPropertyAssertionAxiom(subpop, imitateDistProp, imitate));
    }
  }

  private OWLIndividual getDistribution(String dist, double min, double max, double mean, double var,
      Map<Double, Map<Double, OWLIndividual>> normals, Map<Double, Map<Double, OWLIndividual>> uniforms,
      Set<OWLAxiom> add) {

    if(dist.equals(NORMAL_DIST_STR)) {
      OWLClass normalClass = factory.getOWLClass(FearlusOntology.NORMAL_DIST_CLASS_URI);
      OWLDataProperty meanProp = factory.getOWLDataProperty(FearlusOntology.MEAN_DATA_URI);
      OWLDataProperty varProp = factory.getOWLDataProperty(FearlusOntology.VARIANCE_DATA_URI);

      return getDistribution(normalClass, meanProp, varProp, mean, var, normals, add);
    }
    else if(dist.equals(UNIFORM_DIST_STR)) {
      OWLClass uniformClass = factory.getOWLClass(FearlusOntology.UNIFORM_DIST_CLASS_URI);
      OWLDataProperty minProp = factory.getOWLDataProperty(FearlusOntology.MINIMUM_DATA_URI);
      OWLDataProperty maxProp = factory.getOWLDataProperty(FearlusOntology.MAXIMUM_DATA_URI);

      return getDistribution(uniformClass, minProp, maxProp, min, max, uniforms, add);
    }
    else {
      throw new RuntimeException("Bug!");
    }
  }

  private OWLIndividual getDistribution(OWLClass type, OWLDataProperty prop1, OWLDataProperty prop2, double param1,
      double param2, Map<Double, Map<Double, OWLIndividual>> previous, Set<OWLAxiom> add) {
    if(previous.containsKey(param1) && previous.get(param1).containsKey(param2)) {
      return previous.get(param1).get(param2);
    }

    OWLIndividual dist = factory.getOWLIndividual(getNewIndividualURI("distribution"));
    add.add(factory.getOWLClassAssertionAxiom(dist, type));
    add.add(factory.getOWLDataPropertyAssertionAxiom(dist, prop1, param1));
    add.add(factory.getOWLDataPropertyAssertionAxiom(dist, prop2, param2));

    Map<Double, OWLIndividual> param2map;
    if(previous.containsKey(param1)) {
      param2map = previous.get(param1);
    }
    else {
      param2map = new HashMap<Double, OWLIndividual>();
      previous.put(param1, param2map);
    }
    param2map.put(param2, dist);

    return dist;
  }
}
