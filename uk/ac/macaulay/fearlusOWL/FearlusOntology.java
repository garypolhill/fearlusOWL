/*
 * uk.ac.macaulay.fearlusOWL: FearlusOntology.java
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
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.io.RDFXMLOntologyFormat;
import org.semanticweb.owl.model.AddAxiom;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDataAllRestriction;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataRangeFacetRestriction;
import org.semanticweb.owl.model.OWLDataRangeRestriction;
import org.semanticweb.owl.model.OWLDataSomeRestriction;
import org.semanticweb.owl.model.OWLDataType;
import org.semanticweb.owl.model.OWLObjectAllRestriction;
import org.semanticweb.owl.model.OWLObjectIntersectionOf;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLObjectSomeRestriction;
import org.semanticweb.owl.model.OWLObjectUnionOf;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyChangeException;
import org.semanticweb.owl.model.OWLOntologyCreationException;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.model.OWLOntologyStorageException;
import org.semanticweb.owl.model.UnknownOWLOntologyException;
import org.semanticweb.owl.vocab.OWLRestrictedDataRangeFacetVocabulary;
import org.semanticweb.owl.vocab.XSDVocabulary;

/**
 * FearlusOntology
 * 
 * 
 * @author Gary Polhill
 */
public class FearlusOntology {

  public static final URI ONTOLOGY_URI = URI.create("http://www.macaulay.ac.uk/fearlus/ontologies/fearlus-1.owl");

  public static final URI BID_CLASS_URI = URI.create(ONTOLOGY_URI + "#Bid");

  public static final URI BIOPHYS_CLASS_URI = URI.create(ONTOLOGY_URI + "#BiophysicalCharacteristics");

  public static final URI CLIMATE_CLASS_URI = URI.create(ONTOLOGY_URI + "#Climate");

  public static final URI DIST_CLASS_URI = URI.create(ONTOLOGY_URI + "#Distribution");

  public static final URI NORMAL_DIST_CLASS_URI = URI.create(ONTOLOGY_URI + "#NormalDistribution");

  public static final URI UNIFORM_DIST_CLASS_URI = URI.create(ONTOLOGY_URI + "#UniformDistribution");

  public static final URI ECONOMY_CLASS_URI = URI.create(ONTOLOGY_URI + "#Economy");

  public static final URI ENVIRONMENT_CLASS_URI = URI.create(ONTOLOGY_URI + "#Environment");

  public static final URI LAND_CELL_CLASS_URI = URI.create(ONTOLOGY_URI + "#LandCell");

  public static final URI LAND_MANAGER_CLASS_URI = URI.create(ONTOLOGY_URI + "#LandManager");

  public static final URI BANKRUPT_MANAGER_CLASS_URI = URI.create(ONTOLOGY_URI + "#BankruptManager");

  public static final URI MANAGER_BUYING_LAND_CLASS_URI = URI.create(ONTOLOGY_URI + "#ManagerBuyingLand");

  public static final URI EX_LAND_MANAGER_CLASS_URI = URI.create(ONTOLOGY_URI + "#ExLandManager");

  public static final URI LAND_PARCEL_CLASS_URI = URI.create(ONTOLOGY_URI + "#LandParcel");

  public static final URI PARCEL_FOR_SALE_CLASS_URI = URI.create(ONTOLOGY_URI + "#ParcelForSale");

  public static final URI LAND_USE_CLASS_URI = URI.create(ONTOLOGY_URI + "#LandUse");

  public static final URI SUBPOPULATION_CLASS_URI = URI.create(ONTOLOGY_URI + "#Subpopulation");

  public static final URI OWNS_PARCELS_PROP_URI = URI.create(ONTOLOGY_URI + "#ownsParcels");

  public static final URI CONTAINS_CELLS_PROP_URI = URI.create(ONTOLOGY_URI + "#containsCells");

  public static final URI HAS_BID_PROP_URI = URI.create(ONTOLOGY_URI + "#hasBid");

  public static final URI CONTAINS_MANAGERS_PROP_URI = URI.create(ONTOLOGY_URI + "#containsManagers");

  public static final URI MAKES_BIDS_PROP_URI = URI.create(ONTOLOGY_URI + "#makesBids");

  public static final URI NEIGHBOURING_MGRS_PROP_URI = URI.create(ONTOLOGY_URI + "#neighbouringManagers");

  public static final URI NEIGHBOURING_CELLS_PROP_URI = URI.create(ONTOLOGY_URI + "#neighbouringCells");

  public static final URI NEIGHBOURING_PARCELS_PROP_URI = URI.create(ONTOLOGY_URI + "#neighbouringParcels");

  public static final URI HAS_LAND_PARCELS_PROP_URI = URI.create(ONTOLOGY_URI + "#hasLandParcels");

  public static final URI GOVERNS_MANAGERS_PROP_URI = URI.create(ONTOLOGY_URI + "#governsManagers");

  public static final URI HAS_ECONOMY_PROP_URI = URI.create(ONTOLOGY_URI + "#hasEconomy");

  public static final URI INCOMER_OFFER_DIST_PROP_URI = URI.create(ONTOLOGY_URI + "#incomerOfferPriceDistribution");

  public static final URI LAND_OFFER_DIST_PROP_URI = URI.create(ONTOLOGY_URI + "#landOfferThresholdDistribution");

  public static final URI HAS_LAND_USE_PROP_URI = URI.create(ONTOLOGY_URI + "#hasLandUse");

  public static final URI OWNED_BY_MGR_PROP_URI = URI.create(ONTOLOGY_URI + "#ownedByManager");

  public static final URI GOVERNS_ENV_PROP_URI = URI.create(ONTOLOGY_URI + "#governsEnvironment");

  public static final URI BY_MANAGER_PROP_URI = URI.create(ONTOLOGY_URI + "#byManager");

  public static final URI PART_OF_PARCEL_PROP_URI = URI.create(ONTOLOGY_URI + "#partOfParcel");

  public static final URI FOR_PARCEL_PROP_URI = URI.create(ONTOLOGY_URI + "#forParcel");

  public static final URI HAS_CLIMATE_PROP_URI = URI.create(ONTOLOGY_URI + "#hasClimate");

  public static final URI BELONGS_SUBPOP_PROP_URI = URI.create(ONTOLOGY_URI + "#belongsToSubpopulation");

  public static final URI HAS_BIOPHYS_PROP_URI = URI.create(ONTOLOGY_URI + "#hasBiophysicalCharacteristics");

  public static final URI ASPIRATION_DIST_PROP_URI = URI.create(ONTOLOGY_URI + "#aspirationThresholdDistribution");

  public static final URI IMITATE_P_DIST_PROP_URI = URI.create(ONTOLOGY_URI + "#imitationProbabilityDistribution");

  public static final URI LAST_PROFIT_DATA_URI = URI.create(ONTOLOGY_URI + "#lastMeanProfitPerUnitArea");

  public static final URI IMITATE_P_DATA_URI = URI.create(ONTOLOGY_URI + "#imitatationProbability");

  public static final URI MAXIMUM_DATA_URI = URI.create(ONTOLOGY_URI + "#maximum");

  public static final URI N_X_CELLS_DATA_URI = URI.create(ONTOLOGY_URI + "#nXCells");

  public static final URI AGE_DATA_URI = URI.create(ONTOLOGY_URI + "#age");

  public static final URI BET_DATA_URI = URI.create(ONTOLOGY_URI + "#breakEvenThreshold");

  public static final URI ACCOUNT_DATA_URI = URI.create(ONTOLOGY_URI + "#account");

  public static final URI ASPIRATION_DATA_URI = URI.create(ONTOLOGY_URI + "#aspirationThreshold");

  public static final URI N_Y_CELLS_DATA_URI = URI.create(ONTOLOGY_URI + "#nYCells");

  public static final URI YEAR_DATA_URI = URI.create(ONTOLOGY_URI + "#year");

  public static final URI PRICE_DATA_URI = URI.create(ONTOLOGY_URI + "#price");

  public static final URI INCOME_DATA_URI = URI.create(ONTOLOGY_URI + "#income");

  public static final URI STATE_DATA_URI = URI.create(ONTOLOGY_URI + "#state");

  public static final URI VARIANCE_DATA_URI = URI.create(ONTOLOGY_URI + "#variance");

  public static final URI AMOUNT_DATA_URI = URI.create(ONTOLOGY_URI + "#amount");

  public static final URI MEAN_DATA_URI = URI.create(ONTOLOGY_URI + "#mean");

  public static final URI LAND_OFFER_DATA_URI = URI.create(ONTOLOGY_URI + "#landOfferThreshold");

  public static final URI CELL_AREA_DATA_URI = URI.create(ONTOLOGY_URI + "#cellArea");

  public static final URI YIELD_DATA_URI = URI.create(ONTOLOGY_URI + "#yield");

  public static final URI MINIMUM_DATA_URI = URI.create(ONTOLOGY_URI + "#minimum");

  public static void main(String args[]) {
    String filename;
    boolean owl2 = false;
    boolean farmHousehold = false;
    boolean imitate = false;
    boolean elmm = false;

    int i = 0;
    while(i < args.length && args[i].startsWith("-")) {
      if(args[i].equals("-owl2")) {
        owl2 = true;
      }
      else if(args[i].equals("-farmHousehold")) {
        farmHousehold = true;
      }
      else if(args[i].equals("-imitate")) {
        imitate = true;
      }
      else if(args[i].equals("-elmm")) {
        elmm = true;
      }
      else {
        System.err.println("Unrecognised option: " + args[i]);
        return;
      }
      i++;
    }

    if(i < args.length) {
      filename = args[i];
    }
    else {
      System.err.println("Filename not supplied");
      return;
    }

    OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

    OWLOntology ontology = null;
    try {
      ontology = manager.createOntology(ONTOLOGY_URI);
    }
    catch(OWLOntologyCreationException e) {
      System.err.println("Failed to create an ontology: " + e);
      return;
    }

    OWLDataFactory factory = manager.getOWLDataFactory();

    Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();

    OWLClass bidClass = factory.getOWLClass(BID_CLASS_URI);
    OWLClass biophysClass = factory.getOWLClass(BIOPHYS_CLASS_URI);
    OWLClass climateClass = factory.getOWLClass(CLIMATE_CLASS_URI);
    OWLClass distClass = factory.getOWLClass(DIST_CLASS_URI);
    OWLClass normalDistClass = factory.getOWLClass(NORMAL_DIST_CLASS_URI);
    OWLClass uniformDistClass = factory.getOWLClass(UNIFORM_DIST_CLASS_URI);
    OWLClass economyClass = factory.getOWLClass(ECONOMY_CLASS_URI);
    OWLClass envClass = factory.getOWLClass(ENVIRONMENT_CLASS_URI);
    OWLClass cellClass = factory.getOWLClass(LAND_CELL_CLASS_URI);
    OWLClass managerClass = factory.getOWLClass(LAND_MANAGER_CLASS_URI);
    OWLClass bankruptMgrClass = factory.getOWLClass(BANKRUPT_MANAGER_CLASS_URI);
    OWLClass buyingLandClass = factory.getOWLClass(MANAGER_BUYING_LAND_CLASS_URI);
    OWLClass exManagerClass = factory.getOWLClass(EX_LAND_MANAGER_CLASS_URI);
    OWLClass parcelClass = factory.getOWLClass(LAND_PARCEL_CLASS_URI);
    OWLClass parcelForSaleClass = factory.getOWLClass(PARCEL_FOR_SALE_CLASS_URI);
    OWLClass landUseClass = factory.getOWLClass(LAND_USE_CLASS_URI);
    OWLClass subpopClass = factory.getOWLClass(SUBPOPULATION_CLASS_URI);

    OWLObjectProperty ownsParcelsProp = factory.getOWLObjectProperty(OWNS_PARCELS_PROP_URI);
    OWLObjectProperty containsCellsProp = factory.getOWLObjectProperty(CONTAINS_CELLS_PROP_URI);
    OWLObjectProperty hasBidProp = factory.getOWLObjectProperty(HAS_BID_PROP_URI);
    OWLObjectProperty containsMgrsProp = factory.getOWLObjectProperty(CONTAINS_MANAGERS_PROP_URI);
    OWLObjectProperty makesBidsProp = factory.getOWLObjectProperty(MAKES_BIDS_PROP_URI);
    OWLObjectProperty nbrMgrsProp = factory.getOWLObjectProperty(NEIGHBOURING_MGRS_PROP_URI);
    OWLObjectProperty nbrCellsProp = factory.getOWLObjectProperty(NEIGHBOURING_CELLS_PROP_URI);
    OWLObjectProperty nbrParcelsProp = factory.getOWLObjectProperty(NEIGHBOURING_PARCELS_PROP_URI);
    OWLObjectProperty hasParcelsProp = factory.getOWLObjectProperty(HAS_LAND_PARCELS_PROP_URI);
    OWLObjectProperty hasEconomyProp = factory.getOWLObjectProperty(HAS_ECONOMY_PROP_URI);
    OWLObjectProperty incomerOfferDistProp = factory.getOWLObjectProperty(INCOMER_OFFER_DIST_PROP_URI);
    OWLObjectProperty landOfferDistProp = factory.getOWLObjectProperty(LAND_OFFER_DIST_PROP_URI);
    OWLObjectProperty hasLandUseProp = factory.getOWLObjectProperty(HAS_LAND_USE_PROP_URI);
    OWLObjectProperty ownedByProp = factory.getOWLObjectProperty(OWNED_BY_MGR_PROP_URI);
    OWLObjectProperty byManagerProp = factory.getOWLObjectProperty(BY_MANAGER_PROP_URI);
    OWLObjectProperty partOfParcelProp = factory.getOWLObjectProperty(PART_OF_PARCEL_PROP_URI);
    OWLObjectProperty forParcelProp = factory.getOWLObjectProperty(FOR_PARCEL_PROP_URI);
    OWLObjectProperty hasClimateProp = factory.getOWLObjectProperty(HAS_CLIMATE_PROP_URI);
    OWLObjectProperty belongsToProp = factory.getOWLObjectProperty(BELONGS_SUBPOP_PROP_URI);
    OWLObjectProperty hasBiophysProp = factory.getOWLObjectProperty(HAS_BIOPHYS_PROP_URI);
    OWLObjectProperty aspirationDistProp = factory.getOWLObjectProperty(ASPIRATION_DIST_PROP_URI);
    OWLObjectProperty imitateDistProp = factory.getOWLObjectProperty(IMITATE_P_DIST_PROP_URI);

    OWLDataProperty maxProp = factory.getOWLDataProperty(MAXIMUM_DATA_URI);
    OWLDataProperty incomeProp = factory.getOWLDataProperty(INCOME_DATA_URI);
    OWLDataProperty nXCellsProp = factory.getOWLDataProperty(N_X_CELLS_DATA_URI);
    OWLDataProperty profitProp = factory.getOWLDataProperty(LAST_PROFIT_DATA_URI);
    OWLDataProperty ageProp = factory.getOWLDataProperty(AGE_DATA_URI);
    OWLDataProperty breakEvenProp = factory.getOWLDataProperty(BET_DATA_URI);
    OWLDataProperty accountProp = factory.getOWLDataProperty(ACCOUNT_DATA_URI);
    OWLDataProperty aspirationProp = factory.getOWLDataProperty(ASPIRATION_DATA_URI);
    OWLDataProperty nYCellsProp = factory.getOWLDataProperty(N_Y_CELLS_DATA_URI);
    OWLDataProperty yearProp = factory.getOWLDataProperty(YEAR_DATA_URI);
    OWLDataProperty priceProp = factory.getOWLDataProperty(PRICE_DATA_URI);
    OWLDataProperty stateProp = factory.getOWLDataProperty(STATE_DATA_URI);
    OWLDataProperty varProp = factory.getOWLDataProperty(VARIANCE_DATA_URI);
    OWLDataProperty amountProp = factory.getOWLDataProperty(AMOUNT_DATA_URI);
    OWLDataProperty meanProp = factory.getOWLDataProperty(MEAN_DATA_URI);
    OWLDataProperty landOfferProp = factory.getOWLDataProperty(LAND_OFFER_DATA_URI);
    OWLDataProperty cellAreaProp = factory.getOWLDataProperty(CELL_AREA_DATA_URI);
    OWLDataProperty yieldProp = factory.getOWLDataProperty(YIELD_DATA_URI);
    OWLDataProperty minProp = factory.getOWLDataProperty(MINIMUM_DATA_URI);
    OWLDataProperty imitateProp = factory.getOWLDataProperty(IMITATE_P_DATA_URI);

    Set<OWLClass> disjoints = new HashSet<OWLClass>();

    if(elmm) disjoints.add(bidClass);
    disjoints.add(biophysClass);
    disjoints.add(climateClass);
    disjoints.add(distClass);
    disjoints.add(economyClass);
    disjoints.add(envClass);
    disjoints.add(cellClass);
    disjoints.add(managerClass);
    disjoints.add(exManagerClass);
    disjoints.add(parcelClass);
    disjoints.add(landUseClass);
    disjoints.add(subpopClass);

    if(owl2) {
      axioms.add(factory.getOWLDisjointClassesAxiom(disjoints));
    }
    else {
      for(OWLClass disjoint1: disjoints) {
        for(OWLClass disjoint2: disjoints) {
          if(disjoint1 != disjoint2) {
            axioms.add(factory.getOWLDisjointClassesAxiom(disjoint1, disjoint2));
          }
        }
      }
    }
    axioms.add(factory.getOWLDisjointClassesAxiom(normalDistClass, uniformDistClass));
    axioms.add(factory.getOWLSubClassAxiom(bankruptMgrClass, managerClass));
    axioms.add(factory.getOWLSubClassAxiom(buyingLandClass, managerClass));
    axioms.add(factory.getOWLSubClassAxiom(parcelForSaleClass, parcelClass));
    axioms.add(factory.getOWLSubClassAxiom(normalDistClass, distClass));
    axioms.add(factory.getOWLSubClassAxiom(uniformDistClass, distClass));

    if(farmHousehold) {
      axioms.add(factory.getOWLEquivalentClassesAxiom(managerClass, factory.getOWLClass(URI.create(ONTOLOGY_URI
        + "#FarmHousehold"))));
    }

    OWLObjectUnionOf mgrExMgr = factory.getOWLObjectUnionOf(managerClass, exManagerClass);

    axioms.add(factory.getOWLObjectPropertyDomainAxiom(ownsParcelsProp, managerClass));
    axioms.add(factory.getOWLObjectPropertyDomainAxiom(containsCellsProp, parcelClass));
    axioms.add(factory.getOWLObjectPropertyDomainAxiom(containsMgrsProp, subpopClass));
    axioms.add(factory.getOWLObjectPropertyDomainAxiom(nbrMgrsProp, managerClass));
    axioms.add(factory.getOWLObjectPropertyDomainAxiom(nbrCellsProp, cellClass));
    axioms.add(factory.getOWLObjectPropertyDomainAxiom(nbrParcelsProp, parcelClass));
    axioms.add(factory.getOWLObjectPropertyDomainAxiom(hasParcelsProp, envClass));
    axioms.add(factory.getOWLObjectPropertyDomainAxiom(hasEconomyProp, envClass));
    axioms.add(factory.getOWLObjectPropertyDomainAxiom(incomerOfferDistProp, subpopClass));
    axioms.add(factory.getOWLObjectPropertyDomainAxiom(hasLandUseProp, parcelClass));
    axioms.add(factory.getOWLObjectPropertyDomainAxiom(ownedByProp, parcelClass));
    axioms.add(factory.getOWLObjectPropertyDomainAxiom(partOfParcelProp, cellClass));
    axioms.add(factory.getOWLObjectPropertyDomainAxiom(hasClimateProp, envClass));
    axioms.add(factory.getOWLObjectPropertyDomainAxiom(belongsToProp, mgrExMgr));
    axioms.add(factory.getOWLObjectPropertyDomainAxiom(hasBiophysProp, cellClass));
    axioms.add(factory.getOWLObjectPropertyDomainAxiom(aspirationDistProp, subpopClass));
    axioms.add(factory.getOWLObjectPropertyDomainAxiom(landOfferDistProp, subpopClass));

    if(elmm) {
      axioms.add(factory.getOWLObjectPropertyDomainAxiom(hasBidProp, parcelForSaleClass));
      axioms.add(factory.getOWLObjectPropertyDomainAxiom(makesBidsProp, buyingLandClass));
      axioms.add(factory.getOWLObjectPropertyDomainAxiom(byManagerProp, bidClass));
      axioms.add(factory.getOWLObjectPropertyDomainAxiom(forParcelProp, bidClass));
    }

    if(imitate) {
      axioms.add(factory.getOWLObjectPropertyDomainAxiom(imitateDistProp, subpopClass));
    }

    axioms.add(factory.getOWLObjectPropertyRangeAxiom(ownsParcelsProp, parcelClass));
    axioms.add(factory.getOWLObjectPropertyRangeAxiom(containsCellsProp, cellClass));
    axioms.add(factory.getOWLObjectPropertyRangeAxiom(containsMgrsProp, mgrExMgr));
    axioms.add(factory.getOWLObjectPropertyRangeAxiom(nbrMgrsProp, managerClass));
    axioms.add(factory.getOWLObjectPropertyRangeAxiom(nbrCellsProp, cellClass));
    axioms.add(factory.getOWLObjectPropertyRangeAxiom(nbrParcelsProp, parcelClass));
    axioms.add(factory.getOWLObjectPropertyRangeAxiom(hasParcelsProp, parcelClass));
    axioms.add(factory.getOWLObjectPropertyRangeAxiom(hasEconomyProp, economyClass));
    axioms.add(factory.getOWLObjectPropertyRangeAxiom(incomerOfferDistProp, distClass));
    axioms.add(factory.getOWLObjectPropertyRangeAxiom(hasLandUseProp, landUseClass));
    axioms.add(factory.getOWLObjectPropertyRangeAxiom(ownedByProp, managerClass));
    axioms.add(factory.getOWLObjectPropertyRangeAxiom(partOfParcelProp, parcelClass));
    axioms.add(factory.getOWLObjectPropertyRangeAxiom(hasClimateProp, climateClass));
    axioms.add(factory.getOWLObjectPropertyRangeAxiom(belongsToProp, subpopClass));
    axioms.add(factory.getOWLObjectPropertyRangeAxiom(hasBiophysProp, biophysClass));
    axioms.add(factory.getOWLObjectPropertyRangeAxiom(aspirationDistProp, distClass));
    axioms.add(factory.getOWLObjectPropertyRangeAxiom(landOfferDistProp, distClass));

    if(elmm) {
      axioms.add(factory.getOWLObjectPropertyRangeAxiom(hasBidProp, bidClass));
      axioms.add(factory.getOWLObjectPropertyRangeAxiom(makesBidsProp, bidClass));
      axioms.add(factory.getOWLObjectPropertyRangeAxiom(byManagerProp, buyingLandClass));
      axioms.add(factory.getOWLObjectPropertyRangeAxiom(forParcelProp, parcelForSaleClass));
    }

    if(imitate) {
      axioms.add(factory.getOWLObjectPropertyRangeAxiom(imitateDistProp, distClass));
    }

    axioms.add(factory.getOWLInverseObjectPropertiesAxiom(ownsParcelsProp, ownedByProp));
    axioms.add(factory.getOWLInverseObjectPropertiesAxiom(containsCellsProp, partOfParcelProp));
    axioms.add(factory.getOWLInverseObjectPropertiesAxiom(containsMgrsProp, belongsToProp));

    if(elmm) {
      axioms.add(factory.getOWLInverseObjectPropertiesAxiom(hasBidProp, forParcelProp));
      axioms.add(factory.getOWLInverseObjectPropertiesAxiom(makesBidsProp, byManagerProp));
    }

    axioms.add(factory.getOWLSymmetricObjectPropertyAxiom(nbrMgrsProp));
    axioms.add(factory.getOWLSymmetricObjectPropertyAxiom(nbrCellsProp));
    axioms.add(factory.getOWLSymmetricObjectPropertyAxiom(nbrParcelsProp));

    axioms.add(factory.getOWLFunctionalObjectPropertyAxiom(hasEconomyProp));
    axioms.add(factory.getOWLFunctionalObjectPropertyAxiom(incomerOfferDistProp));
    axioms.add(factory.getOWLFunctionalObjectPropertyAxiom(hasLandUseProp));
    axioms.add(factory.getOWLFunctionalObjectPropertyAxiom(ownedByProp));
    axioms.add(factory.getOWLFunctionalObjectPropertyAxiom(partOfParcelProp));
    axioms.add(factory.getOWLFunctionalObjectPropertyAxiom(hasClimateProp));
    axioms.add(factory.getOWLFunctionalObjectPropertyAxiom(belongsToProp));
    axioms.add(factory.getOWLFunctionalObjectPropertyAxiom(hasBiophysProp));
    axioms.add(factory.getOWLFunctionalObjectPropertyAxiom(aspirationDistProp));
    axioms.add(factory.getOWLFunctionalObjectPropertyAxiom(landOfferDistProp));

    if(imitate) {
      axioms.add(factory.getOWLFunctionalObjectPropertyAxiom(imitateDistProp));
    }

    if(elmm) {
      axioms.add(factory.getOWLFunctionalObjectPropertyAxiom(byManagerProp));
      axioms.add(factory.getOWLFunctionalObjectPropertyAxiom(forParcelProp));
    }

    axioms.add(factory.getOWLInverseFunctionalObjectPropertyAxiom(ownsParcelsProp));
    axioms.add(factory.getOWLInverseFunctionalObjectPropertyAxiom(containsCellsProp));
    axioms.add(factory.getOWLInverseFunctionalObjectPropertyAxiom(containsMgrsProp));

    if(elmm) {
      axioms.add(factory.getOWLInverseFunctionalObjectPropertyAxiom(hasBidProp));
      axioms.add(factory.getOWLInverseFunctionalObjectPropertyAxiom(makesBidsProp));
    }

    if(owl2) {
      Set<OWLObjectProperty> disjointProps = new HashSet<OWLObjectProperty>();
      disjointProps.add(ownsParcelsProp);
      disjointProps.add(containsCellsProp);
      disjointProps.add(containsMgrsProp);
      disjointProps.add(nbrMgrsProp);
      disjointProps.add(nbrCellsProp);
      disjointProps.add(nbrParcelsProp);
      disjointProps.add(hasParcelsProp);
      disjointProps.add(hasEconomyProp);
      disjointProps.add(incomerOfferDistProp);
      disjointProps.add(ownedByProp);
      disjointProps.add(hasLandUseProp);
      disjointProps.add(partOfParcelProp);
      disjointProps.add(hasClimateProp);
      disjointProps.add(belongsToProp);
      disjointProps.add(hasBiophysProp);
      disjointProps.add(aspirationDistProp);
      disjointProps.add(landOfferDistProp);
      if(elmm) {
        disjointProps.add(hasBidProp);
        disjointProps.add(makesBidsProp);
        disjointProps.add(byManagerProp);
        disjointProps.add(forParcelProp);
      }
      if(imitate) disjointProps.add(imitateDistProp);
      axioms.add(factory.getOWLDisjointObjectPropertiesAxiom(disjointProps));
    }

    OWLObjectUnionOf biophysClimateEconomy = factory.getOWLObjectUnionOf(biophysClass, climateClass, economyClass);
    OWLObjectUnionOf parcelCell = factory.getOWLObjectUnionOf(parcelClass, cellClass);

    axioms.add(factory.getOWLDataPropertyDomainAxiom(maxProp, uniformDistClass));
    axioms.add(factory.getOWLDataPropertyDomainAxiom(incomeProp, parcelClass));
    axioms.add(factory.getOWLDataPropertyDomainAxiom(nXCellsProp, envClass));
    axioms.add(factory.getOWLDataPropertyDomainAxiom(profitProp, managerClass));
    axioms.add(factory.getOWLDataPropertyDomainAxiom(ageProp, mgrExMgr));
    axioms.add(factory.getOWLDataPropertyDomainAxiom(breakEvenProp, envClass));
    axioms.add(factory.getOWLDataPropertyDomainAxiom(accountProp, mgrExMgr));
    axioms.add(factory.getOWLDataPropertyDomainAxiom(aspirationProp, mgrExMgr));
    axioms.add(factory.getOWLDataPropertyDomainAxiom(nYCellsProp, envClass));
    axioms.add(factory.getOWLDataPropertyDomainAxiom(yearProp, envClass));
    axioms.add(factory.getOWLDataPropertyDomainAxiom(priceProp, parcelClass));
    axioms.add(factory.getOWLDataPropertyDomainAxiom(stateProp, biophysClimateEconomy));
    axioms.add(factory.getOWLDataPropertyDomainAxiom(varProp, normalDistClass));
    axioms.add(factory.getOWLDataPropertyDomainAxiom(meanProp, normalDistClass));
    axioms.add(factory.getOWLDataPropertyDomainAxiom(landOfferProp, mgrExMgr));
    axioms.add(factory.getOWLDataPropertyDomainAxiom(cellAreaProp, envClass));
    axioms.add(factory.getOWLDataPropertyDomainAxiom(yieldProp, parcelCell));
    axioms.add(factory.getOWLDataPropertyDomainAxiom(minProp, uniformDistClass));

    if(elmm) {
      axioms.add(factory.getOWLDataPropertyDomainAxiom(amountProp, bidClass));
    }

    if(imitate) {
      axioms.add(factory.getOWLDataPropertyDomainAxiom(imitateProp, mgrExMgr));
    }

    OWLDataType doubleType = factory.getOWLDataType(XSDVocabulary.DOUBLE.getURI());
    OWLDataType stringType = factory.getOWLDataType(XSDVocabulary.STRING.getURI());
    OWLDataType intType = factory.getOWLDataType(XSDVocabulary.INT.getURI());

    axioms.add(factory.getOWLDataPropertyRangeAxiom(maxProp, doubleType));
    axioms.add(factory.getOWLDataPropertyRangeAxiom(incomeProp, doubleType));
    axioms.add(factory.getOWLDataPropertyRangeAxiom(nXCellsProp, intType));
    axioms.add(factory.getOWLDataPropertyRangeAxiom(profitProp, doubleType));
    axioms.add(factory.getOWLDataPropertyRangeAxiom(ageProp, intType));
    axioms.add(factory.getOWLDataPropertyRangeAxiom(breakEvenProp, doubleType));
    axioms.add(factory.getOWLDataPropertyRangeAxiom(accountProp, doubleType));
    axioms.add(factory.getOWLDataPropertyRangeAxiom(aspirationProp, doubleType));
    axioms.add(factory.getOWLDataPropertyRangeAxiom(nYCellsProp, intType));
    axioms.add(factory.getOWLDataPropertyRangeAxiom(yearProp, intType));
    axioms.add(factory.getOWLDataPropertyRangeAxiom(priceProp, doubleType));
    axioms.add(factory.getOWLDataPropertyRangeAxiom(stateProp, stringType));
    axioms.add(factory.getOWLDataPropertyRangeAxiom(varProp, doubleType));
    axioms.add(factory.getOWLDataPropertyRangeAxiom(meanProp, doubleType));
    axioms.add(factory.getOWLDataPropertyRangeAxiom(landOfferProp, doubleType));
    axioms.add(factory.getOWLDataPropertyRangeAxiom(cellAreaProp, doubleType));
    axioms.add(factory.getOWLDataPropertyRangeAxiom(yieldProp, doubleType));
    axioms.add(factory.getOWLDataPropertyRangeAxiom(minProp, doubleType));

    if(elmm) {
      axioms.add(factory.getOWLDataPropertyRangeAxiom(amountProp, doubleType));
    }

    if(imitate) {
      axioms.add(factory.getOWLDataPropertyRangeAxiom(imitateProp, doubleType));
    }

    axioms.add(factory.getOWLFunctionalDataPropertyAxiom(maxProp));
    axioms.add(factory.getOWLFunctionalDataPropertyAxiom(incomeProp));
    axioms.add(factory.getOWLFunctionalDataPropertyAxiom(nXCellsProp));
    axioms.add(factory.getOWLFunctionalDataPropertyAxiom(profitProp));
    axioms.add(factory.getOWLFunctionalDataPropertyAxiom(ageProp));
    axioms.add(factory.getOWLFunctionalDataPropertyAxiom(breakEvenProp));
    axioms.add(factory.getOWLFunctionalDataPropertyAxiom(accountProp));
    axioms.add(factory.getOWLFunctionalDataPropertyAxiom(aspirationProp));
    axioms.add(factory.getOWLFunctionalDataPropertyAxiom(nYCellsProp));
    axioms.add(factory.getOWLFunctionalDataPropertyAxiom(yearProp));
    axioms.add(factory.getOWLFunctionalDataPropertyAxiom(priceProp));
    axioms.add(factory.getOWLFunctionalDataPropertyAxiom(stateProp));
    axioms.add(factory.getOWLFunctionalDataPropertyAxiom(varProp));
    axioms.add(factory.getOWLFunctionalDataPropertyAxiom(meanProp));
    axioms.add(factory.getOWLFunctionalDataPropertyAxiom(landOfferProp));
    axioms.add(factory.getOWLFunctionalDataPropertyAxiom(cellAreaProp));
    axioms.add(factory.getOWLFunctionalDataPropertyAxiom(yieldProp));
    axioms.add(factory.getOWLFunctionalDataPropertyAxiom(minProp));

    if(elmm) {
      axioms.add(factory.getOWLFunctionalDataPropertyAxiom(amountProp));
    }

    if(imitate) {
      axioms.add(factory.getOWLFunctionalDataPropertyAxiom(imitateProp));
    }

    if(owl2) {
      Set<OWLDataProperty> disjointProps = new HashSet<OWLDataProperty>();
      disjointProps.add(maxProp);
      disjointProps.add(incomeProp);
      disjointProps.add(nXCellsProp);
      disjointProps.add(profitProp);
      disjointProps.add(ageProp);
      disjointProps.add(breakEvenProp);
      disjointProps.add(accountProp);
      disjointProps.add(aspirationProp);
      disjointProps.add(nYCellsProp);
      disjointProps.add(yearProp);
      disjointProps.add(priceProp);
      disjointProps.add(stateProp);
      disjointProps.add(varProp);
      disjointProps.add(meanProp);
      disjointProps.add(landOfferProp);
      disjointProps.add(cellAreaProp);
      disjointProps.add(yieldProp);
      disjointProps.add(minProp);
      if(elmm) disjointProps.add(amountProp);
      if(imitate) disjointProps.add(imitateProp);
      axioms.add(factory.getOWLDisjointDataPropertiesAxiom(disjointProps));
    }

    OWLObjectAllRestriction ownsPsAllForSale = factory.getOWLObjectAllRestriction(ownsParcelsProp, parcelForSaleClass);
    OWLObjectAllRestriction ownedByAllBankrupt = factory.getOWLObjectAllRestriction(ownedByProp, bankruptMgrClass);
    OWLObjectSomeRestriction ownedBySomeBankrupt = factory.getOWLObjectSomeRestriction(ownedByProp, bankruptMgrClass);
    OWLObjectIntersectionOf ownedByBankrupt =
      factory.getOWLObjectIntersectionOf(ownedByAllBankrupt, ownedBySomeBankrupt);

    axioms.add(factory.getOWLSubClassAxiom(bankruptMgrClass, ownsPsAllForSale));
    axioms.add(factory.getOWLEquivalentClassesAxiom(parcelForSaleClass, ownedByBankrupt));

    if(owl2) {
      OWLDataRangeFacetRestriction ltZero =
        factory.getOWLDataRangeFacetRestriction(OWLRestrictedDataRangeFacetVocabulary.MAX_EXCLUSIVE, 0.0);
      OWLDataRangeRestriction ltZeroDouble =
        factory.getOWLDataRangeRestriction(factory.getOWLDataType(XSDVocabulary.DOUBLE.getURI()), ltZero);
      OWLDataAllRestriction accountAllLTZero = factory.getOWLDataAllRestriction(accountProp, ltZeroDouble);
      OWLDataSomeRestriction accountSomeLTZero = factory.getOWLDataSomeRestriction(accountProp, ltZeroDouble);
      OWLObjectIntersectionOf accountLTZero = factory.getOWLObjectIntersectionOf(accountAllLTZero, accountSomeLTZero);

      axioms.add(factory.getOWLEquivalentClassesAxiom(bankruptMgrClass, accountLTZero));
    }

    List<AddAxiom> addAxioms = new LinkedList<AddAxiom>();

    for(OWLAxiom axiom: axioms) {
      addAxioms.add(new AddAxiom(ontology, axiom));
    }

    try {
      manager.applyChanges(addAxioms);
      manager.saveOntology(ontology, new RDFXMLOntologyFormat(), new URI(filename));
    }
    catch(OWLOntologyChangeException e) {
      System.err.println("Unable to create the FEARLUS ontology axioms: " + e);
      return;
    }
    catch(UnknownOWLOntologyException e) {
      System.err.println("The manager doesn't recognised the ontology created (Panic!): " + e);
      return;
    }
    catch(OWLOntologyStorageException e) {
      System.err.println("Unable to save the ontology at URI " + filename + ": " + e);
      return;
    }
    catch(URISyntaxException e) {
      System.err.println("Unable to create a URI from filename " + filename + ": " + e);
      return;
    }
  }
}
