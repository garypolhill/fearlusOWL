/* uk.ac.macaulay.fearlusOWL: HandleBankruptcies.java
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

import java.util.Set;

import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectProperty;

/**
 * HandleBankruptcies
 *
 * 
 *
 * @author Gary Polhill
 */
public class HandleBankruptcies extends AbstractOntologyEditor {
  
  /**
   * <!-- main -->
   *
   * @param args
   */
  public static void main(String[] args) {
    HandleBankruptcies obj = new HandleBankruptcies();
    System.exit(obj.run(args));
  }

  /**
   * <!-- step -->
   *
   * @see uk.ac.macaulay.fearlusOWL.AbstractOntologyEditor#step(java.util.Set, java.util.Set)
   */
  @Override
  void step(Set<OWLAxiom> add, Set<OWLAxiom> remove) {
    OWLClass managerClass = factory.getOWLClass(FearlusOntology.LAND_MANAGER_CLASS_URI);
    Set<OWLIndividual> managers = managerClass.getIndividuals(ontology);
    
    OWLDataProperty accountProp = factory.getOWLDataProperty(FearlusOntology.ACCOUNT_DATA_URI);
    OWLObjectProperty ownsProp = factory.getOWLObjectProperty(FearlusOntology.OWNS_PARCELS_PROP_URI);
    
    OWLClass bankruptClass = factory.getOWLClass(FearlusOntology.BANKRUPT_MANAGER_CLASS_URI);
    OWLClass parcelForSaleClass = factory.getOWLClass(FearlusOntology.PARCEL_FOR_SALE_CLASS_URI);
    
    for(OWLIndividual manager: managers) {
      double account = getFunctionalDouble(manager.getDataPropertyValues(ontology), accountProp);
      if(account < 0.0) {
        Set<OWLDescription> curClasses = manager.getTypes(ontology);
        if(!curClasses.contains(bankruptClass)) {
          add.add(factory.getOWLClassAssertionAxiom(manager, bankruptClass));
          
          for(OWLIndividual parcel: manager.getObjectPropertyValues(ontology).get(ownsProp)) {
            Set<OWLDescription> curLPClasses = parcel.getTypes(ontology);
            if(!curLPClasses.contains(parcelForSaleClass)) {
              add.add(factory.getOWLClassAssertionAxiom(parcel, parcelForSaleClass));
            }
          }
        }
      }
    }
  }
  
}
