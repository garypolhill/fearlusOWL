/*
 * uk.ac.macaulay.fearlusOWL: UpdateManagerNeighbourhoods.java
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
import java.util.Map;
import java.util.Set;

import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLObjectPropertyExpression;

/**
 * UpdateManagerNeighbourhoods
 * 
 * 
 * 
 * @author Gary Polhill
 */
public class UpdateManagerNeighbourhoods extends AbstractOntologyEditor {

  /**
   * <!-- main -->
   * 
   * @param args
   */
  public static void main(String[] args) {
    UpdateManagerNeighbourhoods obj = new UpdateManagerNeighbourhoods();
    System.exit(obj.run(args));
  }

  /**
   * <!-- step -->
   * 
   * @see uk.ac.macaulay.fearlusOWL.AbstractOntologyEditor#step(java.util.Set,
   *      java.util.Set)
   */
  @Override
  void step(Set<OWLAxiom> add, Set<OWLAxiom> remove) {

    // Find out who is currently the neighbour of whom, and who owns what

    Map<OWLIndividual, Set<OWLIndividual>> nbrs = new HashMap<OWLIndividual, Set<OWLIndividual>>();
    Map<OWLIndividual, Set<OWLIndividual>> lmlps = new HashMap<OWLIndividual, Set<OWLIndividual>>();

    OWLClass managerClass = factory.getOWLClass(FearlusOntology.LAND_MANAGER_CLASS_URI);
    OWLObjectProperty nbrMgrProp = factory.getOWLObjectProperty(FearlusOntology.NEIGHBOURING_MGRS_PROP_URI);
    OWLObjectProperty ownsProp = factory.getOWLObjectProperty(FearlusOntology.OWNS_PARCELS_PROP_URI);

    Set<OWLIndividual> mgrs = managerClass.getIndividuals(ontology);

    for(OWLIndividual mgr: mgrs) {
      Map<OWLObjectPropertyExpression, Set<OWLIndividual>> mgrProps = mgr.getObjectPropertyValues(ontology);
      Set<OWLIndividual> mynbrs = mgrProps.get(nbrMgrProp);
      if(mynbrs == null) mynbrs = new HashSet<OWLIndividual>();
      nbrs.put(mgr, mynbrs);
      lmlps.put(mgr, mgrProps.get(ownsProp));
    }

    // Find out which land parcels neighbour other land parcels, and who owns
    // them

    Map<OWLIndividual, Set<OWLIndividual>> lpnbrs = new HashMap<OWLIndividual, Set<OWLIndividual>>();
    Map<OWLIndividual, OWLIndividual> lplm = new HashMap<OWLIndividual, OWLIndividual>();

    OWLClass parcelClass = factory.getOWLClass(FearlusOntology.LAND_PARCEL_CLASS_URI);
    OWLObjectProperty nbrLPProp = factory.getOWLObjectProperty(FearlusOntology.NEIGHBOURING_PARCELS_PROP_URI);
    OWLObjectProperty ownerProp = factory.getOWLObjectProperty(FearlusOntology.OWNED_BY_MGR_PROP_URI);

    Set<OWLIndividual> lps = parcelClass.getIndividuals(ontology);

    for(OWLIndividual lp: lps) {
      Map<OWLObjectPropertyExpression, Set<OWLIndividual>> lpProps = lp.getObjectPropertyValues(ontology);
      lpnbrs.put(lp, lpProps.get(nbrLPProp));
      lplm.put(lp, getFunctionalObject(lpProps, ownerProp));
    }

    // Determine the new neighbourhoods

    for(OWLIndividual mgr: mgrs) {
      Set<OWLIndividual> mynbrs = nbrs.get(mgr);

      // Loop through the current neighbours to see who shouldn't be there

      Set<OWLIndividual> nbrRemove = new HashSet<OWLIndividual>();
      for(OWLIndividual mynbr: mynbrs) {
        if(!isANeighbour(mgr, mynbr, lmlps, lpnbrs)) {
          nbrRemove.add(mynbr);
          remove.add(factory.getOWLObjectPropertyAssertionAxiom(mgr, nbrMgrProp, mynbr));
          remove.add(factory.getOWLObjectPropertyAssertionAxiom(mynbr, nbrMgrProp, mynbr));
        }
      }
      mynbrs.removeAll(nbrRemove);
      for(OWLIndividual mynbr: nbrRemove) {
        nbrs.get(mynbr).remove(mgr);
      }

      // Loop through the parcels owned and add managers who should be
      // neighbours

      for(OWLIndividual mylp: lmlps.get(mgr)) {
        for(OWLIndividual nbrlp: lpnbrs.get(mylp)) {
          OWLIndividual mynbr = lplm.get(nbrlp);
          if(!mynbrs.contains(mynbr)) {
            mynbrs.add(mynbr);
            add.add(factory.getOWLObjectPropertyAssertionAxiom(mgr, nbrMgrProp, mynbr));

            nbrs.get(mynbr).add(mgr);
            add.add(factory.getOWLObjectPropertyAssertionAxiom(mynbr, nbrMgrProp, mgr));
          }
        }
      }
    }
  }

  private boolean isANeighbour(OWLIndividual lm1, OWLIndividual lm2, Map<OWLIndividual, Set<OWLIndividual>> lmlps,
      Map<OWLIndividual, Set<OWLIndividual>> lpnbrs) {
    Set<OWLIndividual> lm2lps = lmlps.get(lm2);

    if(lm2lps == null || lm2lps.size() == 0) return false;

    for(OWLIndividual lp1: lmlps.get(lm1)) {
      for(OWLIndividual lp2: lm2lps) {
        if(lpnbrs.get(lp1).contains(lp2)) {
          return true;
        }
      }
    }

    return false;
  }
}
