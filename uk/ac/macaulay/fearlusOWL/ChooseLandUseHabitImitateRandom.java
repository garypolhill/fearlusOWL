/*
 * uk.ac.macaulay.fearlusOWL: ChooseLandUseHabitImitateRandom.java
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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLConstant;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataPropertyExpression;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLObjectPropertyExpression;

/**
 * ChooseLandUseHabitImitateRandom
 * 
 * 
 * 
 * @author Gary Polhill
 */
public class ChooseLandUseHabitImitateRandom extends ChooseLandUseHabitRandom {

  /**
   * <!-- main -->
   * 
   * @param args
   */
  public static void main(String[] args) {
    ChooseLandUseHabitImitateRandom obj = new ChooseLandUseHabitImitateRandom();
    System.exit(obj.run(args));
  }

  /**
   * <!-- step -->
   * 
   * @see uk.ac.macaulay.fearlusOWL.AbstractOntologyEditor#step(java.util.Set,
   *      java.util.Set)
   */
  @Override
  public void step(Set<OWLAxiom> add, Set<OWLAxiom> remove) {
    OWLClass landManagerClass = factory.getOWLClass(FearlusOntology.LAND_MANAGER_CLASS_URI);
    Set<OWLIndividual> landManagers = landManagerClass.getIndividuals(ontology);
    OWLObjectProperty ownsProp = factory.getOWLObjectProperty(FearlusOntology.OWNS_PARCELS_PROP_URI);
    OWLDataProperty imitateProp = factory.getOWLDataProperty(FearlusOntology.IMITATE_P_DATA_URI);

    OWLClass landUseClass = factory.getOWLClass(FearlusOntology.LAND_USE_CLASS_URI);
    Set<OWLIndividual> landUses = landUseClass.getIndividuals(ontology);
    OWLIndividual landUseArr[] = landUses.toArray(new OWLIndividual[0]);

    for(OWLIndividual landManager: landManagers) {
      Map<OWLObjectPropertyExpression, Set<OWLIndividual>> mgrProp = landManager.getObjectPropertyValues(ontology);
      Map<OWLDataPropertyExpression, Set<OWLConstant>> mgrData = landManager.getDataPropertyValues(ontology);

      double pImitate = getFunctionalDouble(mgrData, imitateProp);

      if(managerSatisfices(landManager, mgrData)) {
        Set<OWLIndividual> landParcels = mgrProp.get(ownsProp);

        for(OWLIndividual landParcel: landParcels) {
          if(rand.nextDouble() < pImitate) {
            updateLandUse(landParcel, chooseLandUseImitatively(landManager, landParcel, mgrProp, mgrData), add, remove);
          }
          else {
            updateLandUse(landParcel, chooseLandUseRandomly(landUseArr), add, remove);
          }
        }
      }
    }
  }

  OWLIndividual chooseLandUseImitatively(OWLIndividual landManager, OWLIndividual landParcel,
      Map<OWLObjectPropertyExpression, Set<OWLIndividual>> mgrProp,
      Map<OWLDataPropertyExpression, Set<OWLConstant>> mgrData) {
    OWLDataProperty yieldProp = factory.getOWLDataProperty(FearlusOntology.YIELD_DATA_URI);
    OWLObjectProperty nbrProp = factory.getOWLObjectProperty(FearlusOntology.NEIGHBOURING_MGRS_PROP_URI);
    OWLObjectProperty landUseProp = factory.getOWLObjectProperty(FearlusOntology.HAS_LAND_USE_PROP_URI);
    OWLObjectProperty ownsProp = factory.getOWLObjectProperty(FearlusOntology.OWNS_PARCELS_PROP_URI);
    OWLObjectProperty biophysProp = factory.getOWLObjectProperty(FearlusOntology.HAS_BIOPHYS_PROP_URI);

    Map<OWLIndividual, LinkedList<Double>> landUseYields = new HashMap<OWLIndividual, LinkedList<Double>>();

    OWLIndividual biophys = getFunctionalObject(landParcel.getObjectPropertyValues(ontology), biophysProp);

    for(OWLIndividual parcel: mgrProp.get(ownsProp)) {
      updateLandUseYields(parcel, biophysProp, biophys, landUseProp, yieldProp, landUseYields);
    }

    for(OWLIndividual nbrmanager: mgrProp.get(nbrProp)) {
      for(OWLIndividual nbrparcel: nbrmanager.getObjectPropertyValues(ontology).get(ownsProp)) {
        updateLandUseYields(nbrparcel, biophysProp, biophys, landUseProp, yieldProp, landUseYields);
      }
    }
    return chooseLandUse(landUseYields);
  }

  private void updateLandUseYields(OWLIndividual parcel, OWLObjectProperty biophysProp, OWLIndividual biophys,
      OWLObjectProperty landUseProp, OWLDataProperty yieldProp, Map<OWLIndividual, LinkedList<Double>> landUseYields) {
    Map<OWLObjectPropertyExpression, Set<OWLIndividual>> parcelProp = parcel.getObjectPropertyValues(ontology);
    OWLIndividual otherbiophys = getFunctionalObject(parcelProp, biophysProp);
    if(otherbiophys.equals(biophys)) {
      OWLIndividual otherlanduse = getFunctionalObject(parcelProp, landUseProp);
      double yield = getFunctionalDouble(parcel.getDataPropertyValues(ontology), yieldProp);
      if(landUseYields.containsKey(otherlanduse)) {
        landUseYields.get(otherlanduse).addLast(yield);
      }
      else {
        LinkedList<Double> yields = new LinkedList<Double>();
        yields.addLast(yield);
        landUseYields.put(otherlanduse, yields);
      }
    }
  }

  OWLIndividual chooseLandUse(Map<OWLIndividual, LinkedList<Double>> landUseScores) {
    Double bestScore = null;
    Set<OWLIndividual> bestUses = null;
    for(OWLIndividual landUse: landUseScores.keySet()) {
      double score = 0.0;
      double n = 0.0;
      for(Double value: landUseScores.get(landUse)) {
        score += value;
        n += 1.0;
      }

      if(bestScore == null || bestScore < score / n) {
        bestScore = score / n;
        bestUses = new HashSet<OWLIndividual>();
        bestUses.add(landUse);
      }
      else if(bestScore == score / n) {
        bestUses.add(landUse);
      }
    }

    return chooseLandUseRandomly(bestUses);
  }
}
