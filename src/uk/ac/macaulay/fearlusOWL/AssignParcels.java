/*
 * uk.ac.macaulay.fearlusOWL: AssignParcels.java
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
import org.semanticweb.owl.model.OWLConstant;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataPropertyExpression;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLObjectPropertyExpression;

/**
 * AssignParcels
 * 
 * 
 * 
 * @author Gary Polhill
 */
public class AssignParcels extends AbstractOntologyEditor {

  Double parcelPrice = null;

  /**
   * <!-- main -->
   * 
   * @param args
   */
  public static void main(String[] args) {
    AssignParcels obj = new AssignParcels();
    System.exit(obj.run(args));
  }

  @Override
  int parseOpt(String[] args, int i) {
    if(args[i].equals("-landParcelPrice")) {
      parcelPrice = new Double(args[++i]);
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
    if(parcelPrice == null) {
      System.err.println("You must specify a landParcelPrice");
      System.exit(1);
    }

    OWLClass bankruptClass = factory.getOWLClass(FearlusOntology.BANKRUPT_MANAGER_CLASS_URI);
    Set<OWLIndividual> sellers = bankruptClass.getIndividuals(ontology);

    if(sellers.size() == 0) return;

    OWLClass parcelsForSaleClass = factory.getOWLClass(FearlusOntology.PARCEL_FOR_SALE_CLASS_URI);
    Set<OWLIndividual> parcelsForSale = parcelsForSaleClass.getIndividuals(ontology);

    if(parcelsForSale.size() == 0) return;

    OWLClass managerClass = factory.getOWLClass(FearlusOntology.LAND_MANAGER_CLASS_URI);
    Set<OWLIndividual> managers = managerClass.getIndividuals(ontology);

    Map<OWLIndividual, Double> accounts = new HashMap<OWLIndividual, Double>();
    Map<OWLIndividual, Double> curAccounts = new HashMap<OWLIndividual, Double>();
    Map<OWLIndividual, Double> offerThresholds = new HashMap<OWLIndividual, Double>();
    Map<OWLIndividual, Set<OWLIndividual>> neighbours = new HashMap<OWLIndividual, Set<OWLIndividual>>();

    OWLDataProperty accountProp = factory.getOWLDataProperty(FearlusOntology.ACCOUNT_DATA_URI);
    OWLDataProperty offerProp = factory.getOWLDataProperty(FearlusOntology.LAND_OFFER_DATA_URI);

    OWLObjectProperty nbrProp = factory.getOWLObjectProperty(FearlusOntology.NEIGHBOURING_MGRS_PROP_URI);
    OWLObjectProperty ownsProp = factory.getOWLObjectProperty(FearlusOntology.OWNS_PARCELS_PROP_URI);
    OWLObjectProperty ownedByProp = factory.getOWLObjectProperty(FearlusOntology.OWNED_BY_MGR_PROP_URI);

    Set<OWLIndividual> buyers = new HashSet<OWLIndividual>();

    for(OWLIndividual manager: managers) {
      Map<OWLDataPropertyExpression, Set<OWLConstant>> props = manager.getDataPropertyValues(ontology);
      double account = getFunctionalDouble(props, accountProp);
      accounts.put(manager, account);
      curAccounts.put(manager, account);

      double offerThreshold = getFunctionalDouble(props, offerProp);
      offerThresholds.put(manager, offerThreshold);

      if(account > 0.0 && account > offerThreshold) {
        buyers.add(manager);
      }

      Map<OWLObjectPropertyExpression, Set<OWLIndividual>> objProps = manager.getObjectPropertyValues(ontology);
      neighbours.put(manager, objProps.get(nbrProp));
    }

    OWLObjectProperty ownerProp = factory.getOWLObjectProperty(FearlusOntology.OWNED_BY_MGR_PROP_URI);
    OWLDataProperty priceProp = factory.getOWLDataProperty(FearlusOntology.PRICE_DATA_URI);

    LandManagerCreator lmCreate = new LandManagerCreator(factory, ontology, this, rand);

    for(OWLIndividual parcelForSale: parcelsForSale) {
      Map<OWLObjectPropertyExpression, Set<OWLIndividual>> lpObjProps = parcelForSale.getObjectPropertyValues(ontology);
      Map<OWLDataPropertyExpression, Set<OWLConstant>> lpDataProps = parcelForSale.getDataPropertyValues(ontology);

      OWLIndividual seller = getFunctionalObject(lpObjProps, ownerProp);
      if(!sellers.contains(seller)) {
        System.err.println("Ontology is in an invalid state: Parcel " + parcelForSale.getURI()
          + " is for sale, but owner " + seller.getURI() + " is not bankrupt");
        System.exit(1);
      }

      Set<OWLIndividual> possibleBuyers = new HashSet<OWLIndividual>();

      OWLIndividual newbie = factory.getOWLIndividual(getNewIndividualURI("landManager"));
      possibleBuyers.add(newbie);

      for(OWLIndividual neighbour: neighbours.get(seller)) {
        if(buyers.contains(neighbour)) possibleBuyers.add(neighbour);
      }

      OWLIndividual[] possibleBuyersArr = possibleBuyers.toArray(new OWLIndividual[0]);

      OWLIndividual buyer = possibleBuyersArr[rand.nextInt(possibleBuyersArr.length)];

      if(!buyer.equals(newbie)) {
        double buyerAccount = accounts.get(buyer);
        buyerAccount -= parcelPrice;
        accounts.put(buyer, buyerAccount);
        if(buyerAccount < offerThresholds.get(buyer)) buyers.remove(buyer);
      }
      
      double sellerAccount = accounts.get(seller);
      sellerAccount += parcelPrice;
      accounts.put(seller, sellerAccount);

      remove.add(factory.getOWLObjectPropertyAssertionAxiom(seller, ownsProp, parcelForSale));
      remove.add(factory.getOWLObjectPropertyAssertionAxiom(parcelForSale, ownedByProp, seller));
      add.add(factory.getOWLObjectPropertyAssertionAxiom(buyer, ownsProp, parcelForSale));
      add.add(factory.getOWLObjectPropertyAssertionAxiom(parcelForSale, ownedByProp, buyer));
      
      remove.add(factory.getOWLClassAssertionAxiom(parcelForSale, parcelsForSaleClass));

      if(buyer.equals(newbie)) {
        lmCreate.getManager(add, remove, newbie);
        add.add(factory.getOWLClassAssertionAxiom(newbie, managerClass));
      }

      Double price = getFunctionalDouble(lpDataProps, priceProp);
      if(price != parcelPrice) {
        if(price != null) remove.add(factory.getOWLDataPropertyAssertionAxiom(parcelForSale, priceProp, price));
        add.add(factory.getOWLDataPropertyAssertionAxiom(parcelForSale, priceProp, parcelPrice));
      }
    }

    // Update the accounts where these have changed. Remove assertion of
    // BankruptManager
    
    OWLDataProperty profitProp = factory.getOWLDataProperty(FearlusOntology.LAST_PROFIT_DATA_URI);
    OWLClass exManagerClass = factory.getOWLClass(FearlusOntology.EX_LAND_MANAGER_CLASS_URI);
    
    for(OWLIndividual bankrupt: sellers) {
      remove.add(factory.getOWLClassAssertionAxiom(bankrupt, bankruptClass));
      remove.add(factory.getOWLClassAssertionAxiom(bankrupt, managerClass));
      remove.add(factory.getOWLDataPropertyAssertionAxiom(bankrupt, profitProp, getFunctionalDouble(bankrupt.getDataPropertyValues(ontology), profitProp)));
      for(OWLIndividual nbr: neighbours.get(bankrupt)) {
        remove.add(factory.getOWLObjectPropertyAssertionAxiom(bankrupt, nbrProp, nbr));
        if(!sellers.contains(nbr)) remove.add(factory.getOWLObjectPropertyAssertionAxiom(nbr, nbrProp, bankrupt));
      }
      add.add(factory.getOWLClassAssertionAxiom(bankrupt, exManagerClass));
    }
    
    for(OWLIndividual manager: managers) {
      if(accounts.get(manager) != curAccounts.get(manager)) {
        remove.add(factory.getOWLDataPropertyAssertionAxiom(manager, accountProp, curAccounts.get(manager)));
        add.add(factory.getOWLDataPropertyAssertionAxiom(manager, accountProp, accounts.get(manager)));
      }
    }
  }

}
