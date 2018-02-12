/*
 * uk.ac.macaulay.fearlusOWL: Harvest.java
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

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
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
 * Harvest
 * 
 * 
 * 
 * @author Gary Polhill
 */
public class Harvest extends AbstractOntologyEditor {

  URIDoubleLookupTable yieldLookup = null;
  URIDoubleLookupTable incomeLookup = null;

  /**
   * <!-- main -->
   * 
   * @param args
   */
  public static void main(String[] args) {
    Harvest obj = new Harvest();
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
   * @see uk.ac.macaulay.fearlusOWL.AbstractOntologyEditor#step(java.util.Set,
   *      java.util.Set)
   */
  @Override
  void step(Set<OWLAxiom> add, Set<OWLAxiom> remove) {
    OWLClass landMgrClass = factory.getOWLClass(FearlusOntology.LAND_MANAGER_CLASS_URI);
    Set<OWLIndividual> lms = landMgrClass.getIndividuals(ontology);
    OWLObjectProperty ownsParcelsProp = factory.getOWLObjectProperty(FearlusOntology.OWNS_PARCELS_PROP_URI);
    OWLDataProperty accountProp = factory.getOWLDataProperty(FearlusOntology.ACCOUNT_DATA_URI);
    OWLDataProperty lastProfitProp = factory.getOWLDataProperty(FearlusOntology.LAST_PROFIT_DATA_URI);

    OWLObjectProperty hasLandUseProp = factory.getOWLObjectProperty(FearlusOntology.HAS_LAND_USE_PROP_URI);
    OWLObjectProperty containsCellsProp = factory.getOWLObjectProperty(FearlusOntology.CONTAINS_CELLS_PROP_URI);

    OWLDataProperty yieldProp = factory.getOWLDataProperty(FearlusOntology.YIELD_DATA_URI);
    OWLDataProperty incomeProp = factory.getOWLDataProperty(FearlusOntology.INCOME_DATA_URI);

    OWLObjectProperty biophysProp = factory.getOWLObjectProperty(FearlusOntology.HAS_BIOPHYS_PROP_URI);

    OWLClass envClass = factory.getOWLClass(FearlusOntology.ENVIRONMENT_CLASS_URI);
    Set<OWLIndividual> envs = envClass.getIndividuals(ontology);
    OWLObjectProperty hasClimateProp = factory.getOWLObjectProperty(FearlusOntology.HAS_CLIMATE_PROP_URI);
    OWLObjectProperty hasEconomyProp = factory.getOWLObjectProperty(FearlusOntology.HAS_ECONOMY_PROP_URI);
    OWLDataProperty breakEvenThresholdProp = factory.getOWLDataProperty(FearlusOntology.BET_DATA_URI);
    OWLDataProperty cellAreaProp = factory.getOWLDataProperty(FearlusOntology.CELL_AREA_DATA_URI);

    OWLClass landUseClass = factory.getOWLClass(FearlusOntology.LAND_USE_CLASS_URI);
    OWLClass biophysClass = factory.getOWLClass(FearlusOntology.BIOPHYS_CLASS_URI);
    OWLClass climateClass = factory.getOWLClass(FearlusOntology.CLIMATE_CLASS_URI);
    OWLClass economyClass = factory.getOWLClass(FearlusOntology.ECONOMY_CLASS_URI);

    if(envs.size() != 1) {
      if(envs.size() == 0) {
        System.err.println("There are no environments in the ontology!");
        System.exit(1);
      }
      else {
        System.err.println("There are too many environments in the ontology (" + envs.size() + "--expecting just 1)");
        System.exit(1);
      }
    }

    Double breakEvenThreshold = null;
    Double cellArea = null;
    OWLIndividual climate = null;
    OWLIndividual economy = null;

    for(OWLIndividual env: envs) {
      Map<OWLObjectPropertyExpression, Set<OWLIndividual>> envObjProps = env.getObjectPropertyValues(ontology);
      Map<OWLDataPropertyExpression, Set<OWLConstant>> envDataProps = env.getDataPropertyValues(ontology);

      breakEvenThreshold = getFunctionalDouble(envDataProps, breakEvenThresholdProp);
      cellArea = getFunctionalDouble(envDataProps, cellAreaProp);

      climate = getFunctionalObject(envObjProps, hasClimateProp);
      economy = getFunctionalObject(envObjProps, hasEconomyProp);
    }

    assert (breakEvenThreshold != null);
    assert (cellArea != null);
    assert (climate != null);
    assert (economy != null);

    for(OWLIndividual lm: lms) {
      Map<OWLObjectPropertyExpression, Set<OWLIndividual>> lmObjProps = lm.getObjectPropertyValues(ontology);
      Map<OWLDataPropertyExpression, Set<OWLConstant>> lmDataProps = lm.getDataPropertyValues(ontology);

      Set<OWLIndividual> lps = lmObjProps.get(ownsParcelsProp);

      double account = getFunctionalDouble(lmDataProps, accountProp);
      double profit = 0.0;
      double farm_area = 0.0;

      for(OWLIndividual lp: lps) {
        Map<OWLObjectPropertyExpression, Set<OWLIndividual>> lpObjProps = lp.getObjectPropertyValues(ontology);
        Map<OWLDataPropertyExpression, Set<OWLConstant>> lpDataProps = lp.getDataPropertyValues(ontology);

        Set<OWLIndividual> lcs = lpObjProps.get(containsCellsProp);
        OWLIndividual landUse = getFunctionalObject(lpObjProps, hasLandUseProp);

        double yield = 0.0;
        double area = 0.0;

        for(OWLIndividual lc: lcs) {
          OWLIndividual biophys = getFunctionalObject(lc.getObjectPropertyValues(ontology), biophysProp);

          Map<OWLClass, OWLIndividual> yieldMap = new HashMap<OWLClass, OWLIndividual>();
          yieldMap.put(landUseClass, landUse);
          yieldMap.put(climateClass, climate);
          yieldMap.put(biophysClass, biophys);

          double lc_yield;
          try {
            lc_yield = yieldLookup.lookup(yieldProp, yieldMap);
          }
          catch(NullPointerException e) {
            System.err.println("Cannot find yield for land use " + landUse.getURI() + ", climate " + climate.getURI()
              + ", biophysical characteristics " + biophys.getURI());
            yieldLookup.print();
            System.exit(1);
            throw new RuntimeException("Panic!");
          }

          Map<OWLDataPropertyExpression, Set<OWLConstant>> lcDataProps = lc.getDataPropertyValues(ontology);
          if(lcDataProps.containsKey(yieldProp)) {
            double cur_lc_yield = getFunctionalDouble(lcDataProps, yieldProp);
            if(lc_yield != cur_lc_yield) {
              remove.add(factory.getOWLDataPropertyAssertionAxiom(lc, yieldProp, cur_lc_yield));
              add.add(factory.getOWLDataPropertyAssertionAxiom(lc, yieldProp, lc_yield));
            }
          }
          else {
            add.add(factory.getOWLDataPropertyAssertionAxiom(lc, yieldProp, lc_yield));
          }

          yield += lc_yield;
          area += cellArea;
        }

        if(lpDataProps.containsKey(yieldProp)) {
          double cur_yield = getFunctionalDouble(lpDataProps, yieldProp);
          if(yield != cur_yield) {
            remove.add(factory.getOWLDataPropertyAssertionAxiom(lp, yieldProp, cur_yield));
            add.add(factory.getOWLDataPropertyAssertionAxiom(lp, yieldProp, yield));
          }
        }
        else {
          add.add(factory.getOWLDataPropertyAssertionAxiom(lp, yieldProp, yield));
        }

        Map<OWLClass, OWLIndividual> incomeMap = new HashMap<OWLClass, OWLIndividual>();
        incomeMap.put(landUseClass, landUse);
        incomeMap.put(economyClass, economy);

        double income;
        
        try {
          income = incomeLookup.lookup(incomeProp, incomeMap) * yield;
        }
        catch(NullPointerException e) {
          System.err.println("Cannot find income for land use " + landUse.getURI() + ", and economy " + economy.getURI());
          incomeLookup.print();
          System.exit(1);
          throw new RuntimeException("Panic!");
        }

        if(lpDataProps.containsKey(incomeProp)) {
          double cur_income = getFunctionalDouble(lpDataProps, incomeProp);
          if(income != cur_income) {
            remove.add(factory.getOWLDataPropertyAssertionAxiom(lp, incomeProp, cur_income));
            add.add(factory.getOWLDataPropertyAssertionAxiom(lp, incomeProp, income));
          }
        }
        else {
          add.add(factory.getOWLDataPropertyAssertionAxiom(lp, incomeProp, income));
        }

        profit += income - (breakEvenThreshold * area);
        farm_area += area;
      }

      if(profit != 0.0) {
        remove.add(factory.getOWLDataPropertyAssertionAxiom(lm, accountProp, account));
        add.add(factory.getOWLDataPropertyAssertionAxiom(lm, accountProp, account + profit));
      }

      double mean_profit = profit / farm_area;

      if(lmDataProps.containsKey(lastProfitProp)) {
        double cur_profit = getFunctionalDouble(lmDataProps, lastProfitProp);
        if(cur_profit != mean_profit) {
          remove.add(factory.getOWLDataPropertyAssertionAxiom(lm, lastProfitProp, cur_profit));
          add.add(factory.getOWLDataPropertyAssertionAxiom(lm, lastProfitProp, mean_profit));
        }
      }
      else {
        add.add(factory.getOWLDataPropertyAssertionAxiom(lm, lastProfitProp, mean_profit));
      }
    }

  }
}
