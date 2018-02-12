/*
 * uk.ac.macaulay.fearlusOWL: LandManagerCreator.java
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

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLConstant;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataPropertyExpression;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLObjectPropertyExpression;
import org.semanticweb.owl.model.OWLOntology;

/**
 * LandManagerCreator
 * 
 * 
 * 
 * @author Gary Polhill
 */
public class LandManagerCreator {

  private Random rand;
  private AbstractOntologyEditor caller;

  OWLDataFactory factory;

  OWLDataProperty ageProp;
  OWLDataProperty aspProp;
  OWLDataProperty landOfferProp;
  OWLObjectProperty belongsProp;
  OWLDataProperty accountProp;
  OWLDataProperty profitProp;
  OWLObjectProperty containsMgrsProp;
  OWLDataProperty imitateProp = null;

  Map<OWLIndividual, OWLIndividual> aspDists;
  Map<OWLIndividual, OWLIndividual> landOfferDists;
  Map<OWLIndividual, OWLIndividual> imitateDists;

  Set<OWLIndividual> normalDists;
  Set<OWLIndividual> uniformDists;
  Map<OWLIndividual, Double> distMeans;
  Map<OWLIndividual, Double> distVars;
  Map<OWLIndividual, Double> distMins;
  Map<OWLIndividual, Double> distMaxs;

  OWLIndividual[] subpopArr;

  public LandManagerCreator(OWLDataFactory factory, OWLOntology ontology, AbstractOntologyEditor caller, Random rand) {
    this.rand = rand;
    this.factory = factory;
    this.caller = caller;

    ageProp = factory.getOWLDataProperty(FearlusOntology.AGE_DATA_URI);
    aspProp = factory.getOWLDataProperty(FearlusOntology.ASPIRATION_DATA_URI);
    profitProp = factory.getOWLDataProperty(FearlusOntology.LAST_PROFIT_DATA_URI);
    landOfferProp = factory.getOWLDataProperty(FearlusOntology.LAND_OFFER_DATA_URI);
    landOfferDists = new HashMap<OWLIndividual, OWLIndividual>();

    belongsProp = factory.getOWLObjectProperty(FearlusOntology.BELONGS_SUBPOP_PROP_URI);
    accountProp = factory.getOWLDataProperty(FearlusOntology.ACCOUNT_DATA_URI);
    if(ontology.containsDataPropertyReference(FearlusOntology.IMITATE_P_DATA_URI)) {
      imitateProp = factory.getOWLDataProperty(FearlusOntology.IMITATE_P_DATA_URI);
      imitateDists = new HashMap<OWLIndividual, OWLIndividual>();
    }

    OWLClass subpopClass = factory.getOWLClass(FearlusOntology.SUBPOPULATION_CLASS_URI);
    Set<OWLIndividual> subpops = subpopClass.getIndividuals(ontology);
    subpopArr = subpops.toArray(new OWLIndividual[0]);
    OWLObjectProperty aspDistProp = factory.getOWLObjectProperty(FearlusOntology.ASPIRATION_DIST_PROP_URI);
    containsMgrsProp = factory.getOWLObjectProperty(FearlusOntology.CONTAINS_MANAGERS_PROP_URI);
    OWLObjectProperty landOfferDistProp = factory.getOWLObjectProperty(FearlusOntology.LAND_OFFER_DIST_PROP_URI);
    OWLObjectProperty imitateDistProp = factory.getOWLObjectProperty(FearlusOntology.IMITATE_P_DIST_PROP_URI);

    // Define maps from subpops to aspiration and land offer distributions

    aspDists = new HashMap<OWLIndividual, OWLIndividual>();

    for(OWLIndividual subpop: subpops) {
      Map<OWLObjectPropertyExpression, Set<OWLIndividual>> subpopProps = subpop.getObjectPropertyValues(ontology);
      aspDists.put(subpop, caller.getFunctionalObject(subpopProps, aspDistProp));
      if(landOfferProp != null) {
        landOfferDists.put(subpop, caller.getFunctionalObject(subpopProps, landOfferDistProp));
      }
      if(imitateProp != null) {
        imitateDists.put(subpop, caller.getFunctionalObject(subpopProps, imitateDistProp));
      }
    }

    // Normal distribution properties and individuals

    OWLClass normalDistClass = factory.getOWLClass(FearlusOntology.NORMAL_DIST_CLASS_URI);
    normalDists = normalDistClass.getIndividuals(ontology);
    OWLDataProperty meanProp = factory.getOWLDataProperty(FearlusOntology.MEAN_DATA_URI);
    OWLDataProperty varProp = factory.getOWLDataProperty(FearlusOntology.VARIANCE_DATA_URI);

    distMeans = new HashMap<OWLIndividual, Double>();
    distVars = new HashMap<OWLIndividual, Double>();

    for(OWLIndividual dist: normalDists) {
      Map<OWLDataPropertyExpression, Set<OWLConstant>> distData = dist.getDataPropertyValues(ontology);
      distMeans.put(dist, caller.getFunctionalDouble(distData, meanProp));
      distVars.put(dist, caller.getFunctionalDouble(distData, varProp));
    }

    // Uniform distribution properties and individuals

    OWLClass uniformDistClass = factory.getOWLClass(FearlusOntology.UNIFORM_DIST_CLASS_URI);
    uniformDists = uniformDistClass.getIndividuals(ontology);
    OWLDataProperty minProp = factory.getOWLDataProperty(FearlusOntology.MINIMUM_DATA_URI);
    OWLDataProperty maxProp = factory.getOWLDataProperty(FearlusOntology.MAXIMUM_DATA_URI);

    distMins = new HashMap<OWLIndividual, Double>();
    distMaxs = new HashMap<OWLIndividual, Double>();

    for(OWLIndividual dist: uniformDists) {
      Map<OWLDataPropertyExpression, Set<OWLConstant>> distData = dist.getDataPropertyValues(ontology);
      distMins.put(dist, caller.getFunctionalDouble(distData, minProp));
      distMaxs.put(dist, caller.getFunctionalDouble(distData, maxProp));
    }

  }

  public OWLIndividual getManager(Set<OWLAxiom> add, Set<OWLAxiom> remove) {
    OWLIndividual newbie = factory.getOWLIndividual(caller.getNewIndividualURI("landManager"));
    return getManager(add, remove, newbie);
  }

  public OWLIndividual getManager(Set<OWLAxiom> add, Set<OWLAxiom> remove, OWLIndividual lm) {
    OWLIndividual subpop = subpopArr[rand.nextInt(subpopArr.length)];
    return getManager(add, remove, subpop, lm);
  }

  public OWLIndividual getManager(Set<OWLAxiom> add, Set<OWLAxiom> remove, OWLIndividual subpop, OWLIndividual lm) {

    // Initialise the manager's age, account and last profit

    add.add(factory.getOWLDataPropertyAssertionAxiom(lm, accountProp, 0.0));
    add.add(factory.getOWLDataPropertyAssertionAxiom(lm, ageProp, 0));
    add.add(factory.getOWLDataPropertyAssertionAxiom(lm, profitProp, 0.0));

    // Use the subpopulation to assign aspiration
    // and land offer thresholds

    add.add(factory.getOWLObjectPropertyAssertionAxiom(lm, belongsProp, subpop));
    add.add(factory.getOWLObjectPropertyAssertionAxiom(subpop, containsMgrsProp, lm));

    double asp = sampleDist(aspDists.get(subpop), normalDists, uniformDists, distMeans, distVars, distMins, distMaxs);
    add.add(factory.getOWLDataPropertyAssertionAxiom(lm, aspProp, asp));

    double offer =
      sampleDist(landOfferDists.get(subpop), normalDists, uniformDists, distMeans, distVars, distMins, distMaxs);
    add.add(factory.getOWLDataPropertyAssertionAxiom(lm, landOfferProp, offer));
    
    if(imitateProp != null) {
      double imitate =
        sampleDist(imitateDists.get(subpop), normalDists, uniformDists, distMeans, distVars, distMins, distMaxs);
      add.add(factory.getOWLDataPropertyAssertionAxiom(lm, imitateProp, imitate));
    }

    return lm;
  }

  private double sampleDist(OWLIndividual dist, Set<OWLIndividual> normalDists, Set<OWLIndividual> uniformDists,
      Map<OWLIndividual, Double> distMeans, Map<OWLIndividual, Double> distVars, Map<OWLIndividual, Double> distMins,
      Map<OWLIndividual, Double> distMaxs) {
    if(normalDists.contains(dist)) {
      double mean = distMeans.get(dist);
      double var = distVars.get(dist);

      if(var == 0.0) return mean;

      return (rand.nextGaussian() * Math.sqrt(var)) + mean;
    }
    else if(uniformDists.contains(dist)) {
      double min = distMins.get(dist);
      double max = distMaxs.get(dist);

      if(min == max) return min;

      return (rand.nextDouble() * (max - min)) + min;
    }
    else {
      System.err.println("Distribution not of a recognised class: " + dist.getURI());
      System.exit(1);
      throw new RuntimeException("Panic!");
    }

  }

}
