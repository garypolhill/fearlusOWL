/* uk.ac.macaulay.fearlusOWL: ChooseLandUseHabitRandom.java
 *
 * Copyright (C) 2009  Macaulay Institute
 *
 * This file is part of OWLAPITest1.
 *
 * OWLAPITest1 is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3 of 
 * the License, or (at your option) any later version.
 *
 * OWLAPITest1 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with OWLAPITest1. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * Contact information:
 *   Gary Polhill
 *   Macaulay Institute, Craigiebuckler, Aberdeen. AB15 8QH. UK.
 *   g.polhill@macaulay.ac.uk
 */
package uk.ac.macaulay.fearlusOWL;

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
 * ChooseLandUseHabitRandom
 *
 * 
 *
 * @author Gary Polhill
 */
public class ChooseLandUseHabitRandom extends AbstractOntologyEditor {
  /**
   * <!-- main -->
   *
   * @param args
   */
  public static void main(String[] args) {
    ChooseLandUseHabitRandom obj = new ChooseLandUseHabitRandom();
    System.exit(obj.run(args));
  }

  public void step(Set<OWLAxiom> add, Set<OWLAxiom> remove) {
    OWLClass landManagerClass = factory.getOWLClass(FearlusOntology.LAND_MANAGER_CLASS_URI);
    Set<OWLIndividual> landManagers = landManagerClass.getIndividuals(ontology);
    OWLObjectProperty ownsProp = factory.getOWLObjectProperty(FearlusOntology.OWNS_PARCELS_PROP_URI);
    
    OWLClass landUseClass = factory.getOWLClass(FearlusOntology.LAND_USE_CLASS_URI);
    Set<OWLIndividual> landUses = landUseClass.getIndividuals(ontology);
    OWLIndividual landUseArr[] = landUses.toArray(new OWLIndividual[0]);
    
    for(OWLIndividual landManager: landManagers) {
      Map<OWLObjectPropertyExpression, Set<OWLIndividual>> mgrProp = landManager.getObjectPropertyValues(ontology);
      Map<OWLDataPropertyExpression, Set<OWLConstant>> mgrData = landManager.getDataPropertyValues(ontology);
      
      if(managerSatisfices(landManager, mgrData)) {
        Set<OWLIndividual> landParcels = mgrProp.get(ownsProp);
        
        for(OWLIndividual landParcel: landParcels) {       
          updateLandUse(landParcel, chooseLandUseRandomly(landUseArr), add, remove);
        }
      }
    }
  }
  
  boolean managerSatisfices(OWLIndividual manager, Map<OWLDataPropertyExpression, Set<OWLConstant>> mgrData) {
    OWLDataProperty profitData = factory.getOWLDataProperty(FearlusOntology.LAST_PROFIT_DATA_URI);
    OWLDataProperty aspirationData = factory.getOWLDataProperty(FearlusOntology.ASPIRATION_DATA_URI);
    double profit = getFunctionalDouble(mgrData, profitData);
    double aspiration = getFunctionalDouble(mgrData, aspirationData);
    return profit < aspiration;
  }
  
  OWLIndividual chooseLandUseRandomly(Set<OWLIndividual> landUses) {
    return chooseLandUseRandomly(landUses.toArray(new OWLIndividual[0]));
  }
  
  OWLIndividual chooseLandUseRandomly(OWLIndividual[] landUseArr) {
    return landUseArr[rand.nextInt(landUseArr.length)];
  }
  
  void updateLandUse(OWLIndividual parcel, OWLIndividual landUse, Set<OWLAxiom> add, Set<OWLAxiom> remove) {
    OWLObjectProperty hasLandUseProp = factory.getOWLObjectProperty(FearlusOntology.HAS_LAND_USE_PROP_URI);

    Map<OWLObjectPropertyExpression, Set<OWLIndividual>> lpProp = parcel.getObjectPropertyValues(ontology);
    OWLIndividual prevLandUse = getFunctionalObject(lpProp, hasLandUseProp);
    
    if(!prevLandUse.equals(landUse)) {
      remove.add(factory.getOWLObjectPropertyAssertionAxiom(parcel, hasLandUseProp, prevLandUse));
      add.add(factory.getOWLObjectPropertyAssertionAxiom(parcel, hasLandUseProp, landUse));
    }

  }
}