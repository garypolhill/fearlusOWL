/* uk.ac.macaulay.fearlusOWL: AddBiophysClimateEconomyLandUses.java
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

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Set;

import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLIndividual;

/**
 * AddBiophysClimateEconomyLandUses
 *
 * 
 *
 * @author Gary Polhill
 */
public class AddBiophysClimateEconomyLandUses extends AbstractOntologyEditor {
  URIDoubleLookupTable yieldLookup = null;
  URIDoubleLookupTable incomeLookup = null;
  
  /**
   * <!-- main -->
   *
   * @param args
   */
  public static void main(String[] args) {
    AddBiophysClimateEconomyLandUses obj = new AddBiophysClimateEconomyLandUses();
    System.exit(obj.run(args));
  }

  @Override
  int parseOpt(String[] args, int i) {
    try {
      if(args[i].equals("-lookup-yield")) {
        yieldLookup = new URIDoubleLookupTable(args[++i], FearlusOntology.ONTOLOGY_URI.toString() + "#");
      }
      else if(args[i].equals("-lookup-income")) {
        incomeLookup = new URIDoubleLookupTable(args[++i], FearlusOntology.ONTOLOGY_URI.toString() + "#");
      }
      else {
        i = super.parseOpt(args, i);
      }
    }
    catch(IOException e) {
      System.err.println("Error loading CSV file " + args[i] + ": " + e);
      System.exit(1);
    }
    catch(CSVException e) {
      System.err.println("Format error in CSV file " + args[i] + ": " + e);
      System.exit(1);
    }
    catch(URISyntaxException e) {
      System.err.println("URI format error in CSV file " + args[i] + ": " + e);
      System.exit(1);
    }
    return i;
  }

  /**
   * <!-- step -->
   *
   * @see uk.ac.macaulay.fearlusOWL.AbstractOntologyEditor#step(java.util.Set, java.util.Set)
   */
  @Override
  void step(Set<OWLAxiom> add, Set<OWLAxiom> remove) {
    OWLClass biophysClass = factory.getOWLClass(FearlusOntology.BIOPHYS_CLASS_URI);
    OWLClass climateClass = factory.getOWLClass(FearlusOntology.CLIMATE_CLASS_URI);
    OWLClass economyClass = factory.getOWLClass(FearlusOntology.ECONOMY_CLASS_URI);
    OWLClass landUseClass = factory.getOWLClass(FearlusOntology.LAND_USE_CLASS_URI);
    Set<OWLIndividual> biophys = yieldLookup.getInputs(biophysClass, factory);
    Set<OWLIndividual> climate = yieldLookup.getInputs(climateClass, factory);
    Set<OWLIndividual> economy = incomeLookup.getInputs(economyClass, factory);
    Set<OWLIndividual> landUse = incomeLookup.getInputs(landUseClass, factory);
    Set<OWLIndividual> yLandUse = yieldLookup.getInputs(landUseClass, factory);
    
    if(!landUse.containsAll(yLandUse) || !yLandUse.containsAll(landUse)) {
      System.err.println("Yield lookup table doesn't contain the same land uses as the income lookup table");
      System.exit(1);
      throw new RuntimeException("Panic!");
    }
    
    for(OWLIndividual bio: biophys) {
      add.add(factory.getOWLClassAssertionAxiom(bio, biophysClass));
    }
    
    for(OWLIndividual cli: climate) {
      add.add(factory.getOWLClassAssertionAxiom(cli, climateClass));
    }
    
    for(OWLIndividual eco: economy) {
      add.add(factory.getOWLClassAssertionAxiom(eco, economyClass));
    }
    
    for(OWLIndividual lu: landUse) {
      add.add(factory.getOWLClassAssertionAxiom(lu, landUseClass));
    }
  }

}
