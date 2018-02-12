/*
 * uk.ac.macaulay.fearlusOWL: SetUpFearlus.java
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

/**
 * SetUpFearlus
 * 
 * 
 * 
 * @author Gary Polhill
 */
public class SetUpFearlus extends AbstractOntologyEditor {

  GridASCIIReader landParcelGrid = null;
  GridASCIIReader landUseGrid = null;
  GridASCIIReader landManagerGrid = null;
  GridASCIIReader subpopulationGrid = null;
  GridASCIIReader biophysGrid = null;
  Double breakEvenThreshold = null;
  Set<GridASCIIReader> allGrids = null;
  GridASCIIReader anyGrid = null;
  Integer argNXCells = null;
  Integer argNYCells = null;
  Double argCellArea = null;

  SetUpFearlus() {
    allGrids = new HashSet<GridASCIIReader>();
  }

  /**
   * <!-- main -->
   * 
   * @param args
   */
  public static void main(String[] args) {
    SetUpFearlus obj = new SetUpFearlus();
    System.exit(obj.run(args));
  }

  @Override
  int parseOpt(String[] args, int i) {
    try {
      if(args[i].equals("-parcels")) {
        landParcelGrid = new GridASCIIReader(args[++i], allGrids);
        allGrids.add(landParcelGrid);
        anyGrid = landParcelGrid;
      }
      else if(args[i].equals("-uses")) {
        landUseGrid = new GridASCIIReader(args[++i], allGrids);
        allGrids.add(landUseGrid);
        anyGrid = landUseGrid;
      }
      else if(args[i].equals("-managers")) {
        landManagerGrid = new GridASCIIReader(args[++i], allGrids);
        allGrids.add(landManagerGrid);
        anyGrid = landManagerGrid;
      }
      else if(args[i].equals("-subpops")) {
        subpopulationGrid = new GridASCIIReader(args[++i], allGrids);
        allGrids.add(subpopulationGrid);
        anyGrid = subpopulationGrid;
      }
      else if(args[i].equals("-biophys")) {
        biophysGrid = new GridASCIIReader(args[++i], allGrids);
        allGrids.add(biophysGrid);
        anyGrid = biophysGrid;
      }
      else if(args[i].equals("-BET")) {
        breakEvenThreshold = Double.parseDouble(args[++i]);
      }
      else if(args[i].equals("-nXCells")) {
        argNXCells = Integer.parseInt(args[++i]);
      }
      else if(args[i].equals("-nYCells")) {
        argNYCells = Integer.parseInt(args[++i]);
      }
      else if(args[i].equals("-cellArea")) {
        argCellArea = Double.parseDouble(args[++i]);
      }
      else {
        i = super.parseOpt(args, i);
      }
    }
    catch(IOException e) {
      System.err.println("Error loading grid file " + args[i] + ": " + e);
      System.exit(1);
    }
    return i;
  }

  @Override
  void step(Set<OWLAxiom> add, Set<OWLAxiom> remove) {

    // Environment class and properties

    OWLClass envClass = factory.getOWLClass(FearlusOntology.ENVIRONMENT_CLASS_URI);
    OWLDataProperty betProp = factory.getOWLDataProperty(FearlusOntology.BET_DATA_URI);
    OWLDataProperty cellAreaProp = factory.getOWLDataProperty(FearlusOntology.CELL_AREA_DATA_URI);
    OWLDataProperty nXCellsProp = factory.getOWLDataProperty(FearlusOntology.N_X_CELLS_DATA_URI);
    OWLDataProperty nYCellsProp = factory.getOWLDataProperty(FearlusOntology.N_Y_CELLS_DATA_URI);
    OWLDataProperty yearProp = factory.getOWLDataProperty(FearlusOntology.YEAR_DATA_URI);
    OWLObjectProperty hasClimateProp = factory.getOWLObjectProperty(FearlusOntology.HAS_CLIMATE_PROP_URI);
    OWLObjectProperty hasEconomyProp = factory.getOWLObjectProperty(FearlusOntology.HAS_ECONOMY_PROP_URI);
    OWLObjectProperty hasLPProp = factory.getOWLObjectProperty(FearlusOntology.HAS_LAND_PARCELS_PROP_URI);

    // Find the environment -- it is allowed to be in the ontology

    OWLIndividual env = null;
    Set<OWLIndividual> envs = envClass.getIndividuals(ontology);
    if(envs.size() == 0) {
      env = factory.getOWLIndividual(getNewIndividualURI("environment"));
      add.add(factory.getOWLClassAssertionAxiom(env, envClass));
    }
    else {
      for(OWLIndividual anEnv: envs) {
        if(env == null) {
          env = anEnv;
        }
        else {
          System.err
              .println("There are too many environments in the model ontology (" + envs.size() + ", should be 1)");
          System.exit(1);
        }
      }
    }

    Map<OWLDataPropertyExpression, Set<OWLConstant>> envData = env.getDataPropertyValues(ontology);

    // The break-even threshold may be specified in the environment individual,
    // or by a parameter to this program

    if(!envData.containsKey(betProp)) {
      if(breakEvenThreshold == null) {
        System.err.println("No break even threshold specified");
        System.exit(1);
      }
      add.add(factory.getOWLDataPropertyAssertionAxiom(env, betProp, breakEvenThreshold));
    }

    // The cell area may be specified in the environment individual, but it must
    // be the same as that in the grid file

    double cellarea;
    if(!envData.containsKey(cellAreaProp)) {
      if(anyGrid == null && argCellArea == null) {
        System.err
            .println("No cell area parameter specified in the ontology, none specified on the command line, and no grid file provided");
        System.exit(1);
      }
      else if(anyGrid != null && argCellArea != null && anyGrid.cellsize() * anyGrid.cellsize() != argCellArea) {
        System.err.println("cellArea given on the command line (" + argCellArea
          + ") is not equal to that derived from the grid file (" + (anyGrid.cellsize() * anyGrid.cellsize())
          + "). The former will be ignored.");
      }
      cellarea = anyGrid != null ? anyGrid.cellsize() * anyGrid.cellsize() : argCellArea;
      add.add(factory.getOWLDataPropertyAssertionAxiom(env, cellAreaProp, cellarea));
    }
    else {
      cellarea = getFunctionalDouble(envData, cellAreaProp);
      if(anyGrid.cellsize() * anyGrid.cellsize() != cellarea) {
        System.err.println("Cellarea in environment (" + cellarea + ") is different from that in grid files ("
          + (anyGrid.cellsize() * anyGrid.cellsize()) + ")");
        System.exit(1);
      }
    }

    // The number of x-cells may be specified in the environment individual, but
    // it must be the same as that in the grid file

    int nXCells;
    if(!envData.containsKey(nXCellsProp)) {
      if(anyGrid == null && argNXCells == null) {
        System.err
            .println("No nXCells parameter specified in the ontology, none given on the command line and no grid file provided");
        System.exit(1);
      }
      else if(anyGrid != null && argNXCells != null && anyGrid.ncols() != argNXCells) {
        System.err.println("nXCells given on the command line (" + argNXCells
          + ") is not equal to the ncols in the grid file (" + anyGrid.ncols() + "). The former will be ignored.");
      }
      nXCells = anyGrid != null ? anyGrid.ncols() : argNXCells;
      add.add(factory.getOWLDataPropertyAssertionAxiom(env, nXCellsProp, nXCells));
    }
    else {
      nXCells = getFunctionalInteger(envData, nXCellsProp);
      if(anyGrid.ncols() != nXCells) {
        System.err.println("NXCells in environment (" + nXCells + ") is different from ncols in grid files ("
          + anyGrid.ncols() + ")");
        System.exit(1);
      }
    }

    // The number of y-cells may be specified in the environment individual, but
    // it must be the same as that in the grid file

    int nYCells;
    if(!envData.containsKey(nYCellsProp)) {
      if(anyGrid == null && argNYCells == null) {
        System.err
            .println("No nYCells parameter specified in the ontology, none given on the command line, and no grid file provided");
        System.exit(1);
      }
      else if(anyGrid != null && argNYCells != null && anyGrid.nrows() != argNYCells) {
        System.err.println("nYCells given on the command line (" + argNYCells
          + ") is not equal to the nrows in the grid file (" + anyGrid.nrows() + "). The former will be ignored.");
      }
      nYCells = anyGrid != null ? anyGrid.nrows() : argNYCells;
      add.add(factory.getOWLDataPropertyAssertionAxiom(env, nYCellsProp, nYCells));
    }
    else {
      nYCells = getFunctionalInteger(envData, nYCellsProp);
      if(landParcelGrid.nrows() != nYCells) {
        System.err.println("NYCells in environment (" + nYCells + ") is different from nrows in grid files ("
          + anyGrid.nrows() + ")");
        System.exit(1);
      }
    }

    // Choose an initial climate for the environment, if this has not been
    // specified already

    if(!envData.containsKey(hasClimateProp)) {
      OWLClass climateClass = factory.getOWLClass(FearlusOntology.CLIMATE_CLASS_URI);
      Set<OWLIndividual> climates = climateClass.getIndividuals(ontology);
      if(climates.size() == 0) {
        System.err.println("There are no climates in the ontology");
        System.exit(1);
      }
      OWLIndividual[] climateArr = climates.toArray(new OWLIndividual[0]);
      OWLIndividual climate = climateArr[rand.nextInt(climateArr.length)];
      add.add(factory.getOWLObjectPropertyAssertionAxiom(env, hasClimateProp, climate));
    }

    // Choose an initial economy for the environment, if this has not been
    // specified already

    if(!envData.containsKey(hasEconomyProp)) {
      OWLClass economyClass = factory.getOWLClass(FearlusOntology.ECONOMY_CLASS_URI);
      Set<OWLIndividual> economies = economyClass.getIndividuals(ontology);
      if(economies.size() == 0) {
        System.err.println("There are no economies in the ontology");
        System.exit(1);
      }
      OWLIndividual[] economyArr = economies.toArray(new OWLIndividual[0]);
      OWLIndividual economy = economyArr[rand.nextInt(economyArr.length)];
      add.add(factory.getOWLObjectPropertyAssertionAxiom(env, hasEconomyProp, economy));
    }

    // Initialise the year in the environment, if not specified already

    if(!envData.containsKey(yearProp)) {
      add.add(factory.getOWLDataPropertyAssertionAxiom(env, yearProp, 0));
    }

    // Get land cell class and properties

    OWLClass landCellClass = factory.getOWLClass(FearlusOntology.LAND_CELL_CLASS_URI);
    OWLObjectProperty partOfParcelProp = factory.getOWLObjectProperty(FearlusOntology.PART_OF_PARCEL_PROP_URI);
    OWLObjectProperty nbrCellsProp = factory.getOWLObjectProperty(FearlusOntology.NEIGHBOURING_CELLS_PROP_URI);
    OWLObjectProperty biophysProp = factory.getOWLObjectProperty(FearlusOntology.HAS_BIOPHYS_PROP_URI);

    // Get land parcel class and properties

    OWLClass landParcelClass = factory.getOWLClass(FearlusOntology.LAND_PARCEL_CLASS_URI);
    OWLObjectProperty containsCellsProp = factory.getOWLObjectProperty(FearlusOntology.CONTAINS_CELLS_PROP_URI);
    OWLObjectProperty nbrParcelProp = factory.getOWLObjectProperty(FearlusOntology.NEIGHBOURING_PARCELS_PROP_URI);
    OWLObjectProperty hasLandUseProp = factory.getOWLObjectProperty(FearlusOntology.HAS_LAND_USE_PROP_URI);
    OWLObjectProperty ownedByProp = factory.getOWLObjectProperty(FearlusOntology.OWNED_BY_MGR_PROP_URI);

    // Create a list of parcels and map from parcel ID to parcel to be used if a
    // parcel grid has been provided

    Set<OWLIndividual> parcels = new HashSet<OWLIndividual>();

    Map<Double, OWLIndividual> parcelGridIDs = new HashMap<Double, OWLIndividual>();

    if(landParcelGrid != null) {
      for(Double id: landParcelGrid.values()) {
        OWLIndividual lp = factory.getOWLIndividual(getNewIndividualURI("landParcel"));
        add.add(factory.getOWLClassAssertionAxiom(lp, landParcelClass));
        add.add(factory.getOWLObjectPropertyAssertionAxiom(env, hasLPProp, lp));
        parcels.add(lp);
        parcelGridIDs.put(id, lp);
      }
    }

    // Get a list of biophysical characteristics (which must be provided) and
    // map their IDs to them for use if a biophysical characteristics grid has
    // been provided. The ID is given after an underscore in the URI of the
    // individual.

    OWLClass biophysClass = factory.getOWLClass(FearlusOntology.BIOPHYS_CLASS_URI);
    Set<OWLIndividual> biophyss = biophysClass.getIndividuals(ontology);
    if(biophyss.size() == 0) {
      System.err.println("There are no biophysical characteristics in the ontology");
      System.exit(1);
    }
    Map<Double, OWLIndividual> bioids = new HashMap<Double, OWLIndividual>();
    if(biophysGrid != null) {
      for(OWLIndividual biophys: biophyss) {
        String idstr = biophys.getURI().toString();
        String[] idstrparts = idstr.split("_");
        Double bioid = Double.parseDouble(idstrparts[idstrparts.length - 1]);
        bioids.put(bioid, biophys);
      }
    }

    // Create arrays to store the cells and parcels in each location

    OWLIndividual[][] cells = new OWLIndividual[nXCells][nYCells];
    OWLIndividual[][] parcells = new OWLIndividual[nXCells][nYCells];

    // Create maps to link parcels to their neighbours and constituent cells

    Map<OWLIndividual, Set<OWLIndividual>> nbrlps = new HashMap<OWLIndividual, Set<OWLIndividual>>();
    Map<OWLIndividual, Set<OWLIndividual>> lplcs = new HashMap<OWLIndividual, Set<OWLIndividual>>();

    for(OWLIndividual lp: parcels) {
      // These will only be initialised here if the landParcelGrid is not null
      nbrlps.put(lp, new HashSet<OWLIndividual>());
      lplcs.put(lp, new HashSet<OWLIndividual>());
    }

    // Create maps to store x and y coordinates of each cell

    Map<OWLIndividual, Integer> xcells = new HashMap<OWLIndividual, Integer>();
    Map<OWLIndividual, Integer> ycells = new HashMap<OWLIndividual, Integer>();

    // Create the cells, link them to parcels, and define cell and parcel
    // neighbourhoods

    for(int x = 0; x < nXCells; x++) {
      for(int y = 0; y < nYCells; y++) {
        OWLIndividual lp;

        // Obtain the land parcel--either one per cell or use the grid file

        if(landParcelGrid == null) {
          lp = factory.getOWLIndividual(getNewIndividualURI("landParcel"));
          add.add(factory.getOWLClassAssertionAxiom(lp, landParcelClass));
          add.add(factory.getOWLObjectPropertyAssertionAxiom(env, hasLPProp, lp));
          parcels.add(lp);
          nbrlps.put(lp, new HashSet<OWLIndividual>());
          lplcs.put(lp, new HashSet<OWLIndividual>());
        }
        else {
          Double lpid = landParcelGrid.at(x, y);
          if(lpid == null) {
            cells[x][y] = null;
            parcells[x][y] = null;
            continue;
          }
          lp = parcelGridIDs.get(lpid);
        }

        // Create the land cell

        OWLIndividual lc = factory.getOWLIndividual(getNewIndividualURI("landCell"));
        add.add(factory.getOWLClassAssertionAxiom(lc, landCellClass));

        // Link the land cell to the parcel

        add.add(factory.getOWLObjectPropertyAssertionAxiom(lc, partOfParcelProp, lp));
        add.add(factory.getOWLObjectPropertyAssertionAxiom(lp, containsCellsProp, lc));

        // Find the biophysical characteristics of the cell

        OWLIndividual biophys;

        if(biophysGrid == null) {
          OWLIndividual[] biophysArr = biophyss.toArray(new OWLIndividual[0]);
          biophys = biophysArr[rand.nextInt(biophysArr.length)];
        }
        else {
          Double bioid = biophysGrid.at(x, y);
          if(!bioids.containsKey(bioid)) {
            System.err.println("Cannot find biophysical characteristics to match id " + bioid + " at (" + x + ", " + y
              + ") in biophysical characteristics grid" + biophysGrid);
            System.exit(1);
          }
          biophys = bioids.get(bioid);
        }

        add.add(factory.getOWLObjectPropertyAssertionAxiom(lc, biophysProp, biophys));

        // Maintain arrays and maps

        cells[x][y] = lc;
        parcells[x][y] = lp;
        lplcs.get(lp).add(lc);
        xcells.put(lc, x);
        ycells.put(lc, y);

        // Define parcel and cell neighbourhoods (Von-Neumann Toroidal)

        if(x > 0) {
          if(cells[x - 1][y] != null) {
            assertNbr(add, lc, cells[x - 1][y], lp, parcells[x - 1][y], nbrlps, nbrCellsProp, nbrParcelProp);
          }
          if(x == nXCells - 1 && cells[0][y] != null) {
            assertNbr(add, lc, cells[0][y], lp, parcells[0][y], nbrlps, nbrCellsProp, nbrParcelProp);
          }
        }
        if(y > 0) {
          if(cells[x][y - 1] != null) {
            assertNbr(add, lc, cells[x][y - 1], lp, parcells[x][y - 1], nbrlps, nbrCellsProp, nbrParcelProp);
          }
          if(y == nYCells - 1 && cells[x][0] != null) {
            assertNbr(add, lc, cells[x][0], lp, parcells[x][0], nbrlps, nbrCellsProp, nbrParcelProp);
          }
        }
      }
    }

    // Land use class. Like the biophysical characteristics, climate and
    // economy, these must be defined in the ontology.
    // To initialise using a grid file, land use URIs must end with _id, where
    // id is the number in the grid file used for that land use.

    OWLClass landUseClass = factory.getOWLClass(FearlusOntology.LAND_USE_CLASS_URI);
    Set<OWLIndividual> landUses = landUseClass.getIndividuals(ontology);

    if(landUses.size() == 0) {
      System.err.println("Ontology contains no land uses");
      System.exit(1);
    }

    if(landUseGrid == null) {
      // No grid file specified--just assign a random land use
      OWLIndividual[] luarr = landUses.toArray(new OWLIndividual[0]);
      for(OWLIndividual lp: parcels) {
        OWLIndividual lu = luarr[rand.nextInt(luarr.length)];

        add.add(factory.getOWLObjectPropertyAssertionAxiom(lp, hasLandUseProp, lu));
      }
    }
    else {
      // Grid file specified. Create a map from ID derived from the URI to land
      // use individual, and assign land uses from the grid file. All cells
      // belonging to a parcel must have the same land use.

      Map<Double, OWLIndividual> luids = new HashMap<Double, OWLIndividual>();
      for(OWLIndividual landUse: landUses) {
        String idstr = landUse.getURI().toString();
        String[] idstrparts = idstr.split("_");
        Double luid = Double.parseDouble(idstrparts[idstrparts.length - 1]);
        luids.put(luid, landUse);
      }

      for(OWLIndividual lp: parcels) {
        Set<OWLIndividual> lcs = lplcs.get(lp);

        Double luid = null;
        for(OWLIndividual lc: lcs) {
          if(luid == null) {
            luid = landUseGrid.at(xcells.get(lc), ycells.get(lc));
            if(!luids.containsKey(luid)) {
              System.err.println("Unable to find a land use for ID " + luid + " at (" + xcells.get(lc) + ", "
                + ycells.get(lc) + ")");
              System.exit(1);
            }
            OWLIndividual lu = luids.get(luid);
            add.add(factory.getOWLObjectPropertyAssertionAxiom(lp, hasLandUseProp, lu));
          }
          else {
            Double thisluid = landUseGrid.at(xcells.get(lc), ycells.get(lc));
            if(thisluid != luid) {
              System.err.println("Land use ID at (" + xcells.get(lc) + ", " + ycells.get(lc) + ") (" + thisluid
                + ") is not the same as that for other cells belonging to this land parcel (" + luid + ")");
              System.exit(1);
            }
          }
        }
      }
    }

    // Subpopulation class, individuals and properties. Subpopulations must be
    // pre-defined in the ontology. To use them in a grid file, their URIs must
    // end with _id, where id is the number used in the grid file for that
    // subpopulation.

    OWLClass subpopClass = factory.getOWLClass(FearlusOntology.SUBPOPULATION_CLASS_URI);
    Set<OWLIndividual> subpops = subpopClass.getIndividuals(ontology);
    if(subpops.size() == 0) {
      System.err.println("There are no subpopulations in the ontology");
      System.exit(1);
    }

    // Create the map from subpop id to subpop (only used with subpop grids)

    Map<Double, OWLIndividual> spids = new HashMap<Double, OWLIndividual>();

    if(subpopulationGrid != null) {
      for(OWLIndividual subpop: subpops) {
        String idstr = subpop.getURI().toString();
        String[] idstrparts = idstr.split("_");
        Double spid = Double.parseDouble(idstrparts[idstrparts.length - 1]);
        spids.put(spid, subpop);
      }
    }

    // Land manager class and properties

    OWLClass lmgrClass = factory.getOWLClass(FearlusOntology.LAND_MANAGER_CLASS_URI);
    OWLObjectProperty ownsProp = factory.getOWLObjectProperty(FearlusOntology.OWNS_PARCELS_PROP_URI);

    // Map of land manager IDs to individuals, to be used if a land manager grid
    // file is provided

    Map<Double, OWLIndividual> mgrids = new HashMap<Double, OWLIndividual>();

    // Loop through the parcels and assign them to managers--either one each or
    // as per grid file

    LandManagerCreator lmCreate = new LandManagerCreator(factory, ontology, this, rand);

    for(OWLIndividual lp: parcels) {
      OWLIndividual lm;

      // Get the land manager--creating one if necessary (which it is if there's
      // no manager grid file)
      if(landManagerGrid == null) {
        lm = factory.getOWLIndividual(getNewIndividualURI("landManager"));
        add.add(factory.getOWLClassAssertionAxiom(lm, lmgrClass));
      }
      else {
        Double lmid = null;
        for(OWLIndividual lc: lplcs.get(lp)) {
          if(lmid == null) {
            lmid = landManagerGrid.at(xcells.get(lc), ycells.get(lc));
          }
          else {
            double this_lmid = landManagerGrid.at(xcells.get(lc), ycells.get(lc));
            if(lmid != this_lmid) {
              System.err.println("Manager ID " + this_lmid + " at (" + xcells.get(lc) + ", " + ycells.get(lc)
                + ") is not the same as that for other cells in the parcel: " + lmid);
              System.exit(1);
            }
          }
        }
        if(mgrids.containsKey(lmid)) {
          lm = mgrids.get(lmid);
        }
        else {
          lm = factory.getOWLIndividual(getNewIndividualURI("landManager"));
          add.add(factory.getOWLClassAssertionAxiom(lm, lmgrClass));
          mgrids.put(lmid, lm);
        }
      }

      // Assign the parcel to the manager

      add.add(factory.getOWLObjectPropertyAssertionAxiom(lm, ownsProp, lp));
      add.add(factory.getOWLObjectPropertyAssertionAxiom(lp, ownedByProp, lm));

      // Get a subpopulation for the manager, and use it to assign aspiration
      // and land offer thresholds

      OWLIndividual subpop = getASubpop(lp, subpops, lplcs, xcells, ycells, spids);

      lmCreate.getManager(add, remove, subpop, lm);

    }
  }

  private OWLIndividual getASubpop(OWLIndividual lp, Set<OWLIndividual> subpops,
      Map<OWLIndividual, Set<OWLIndividual>> lplcs, Map<OWLIndividual, Integer> xcells,
      Map<OWLIndividual, Integer> ycells, Map<Double, OWLIndividual> spids) {
    OWLIndividual subpop = null;
    if(subpopulationGrid == null) {
      OWLIndividual[] subpopArr = subpops.toArray(new OWLIndividual[0]);
      subpop = subpopArr[rand.nextInt(subpopArr.length)];
    }
    else {
      for(OWLIndividual lc: lplcs.get(lp)) {
        int x = xcells.get(lc);
        int y = ycells.get(lc);
        OWLIndividual this_sp = null;
        Double spid = subpopulationGrid.at(x, y);
        if(spids.containsKey(spid)) {
          this_sp = spids.get(spid);
        }
        else {
          System.err.println("Cannot find subpopulation corresponding to id " + spid + " at (" + x + ", " + y
            + ") in subpopulation grid " + subpopulationGrid);
          System.exit(1);
        }
        if(subpop == null) {
          subpop = this_sp;
        }
        else if(!subpop.equals(this_sp)) {
          System.err.println("Subpopulation id " + spid + " at (" + x + ", " + y
            + ") is different from that from other cells in the same parcel");
          System.exit(1);
        }
      }
    }
    return subpop;
  }

  private void assertNbr(Set<OWLAxiom> add, OWLIndividual lc, OWLIndividual lcnbr, OWLIndividual lp,
      OWLIndividual lpnbr, Map<OWLIndividual, Set<OWLIndividual>> nbrlps, OWLObjectProperty nbrCellsProp,
      OWLObjectProperty nbrParcelProp) {

    add.add(factory.getOWLObjectPropertyAssertionAxiom(lc, nbrCellsProp, lcnbr));
    add.add(factory.getOWLObjectPropertyAssertionAxiom(lcnbr, nbrCellsProp, lc));

    if(!lpnbr.equals(lp)) {
      if(!nbrlps.get(lp).contains(lpnbr)) {
        add.add(factory.getOWLObjectPropertyAssertionAxiom(lp, nbrParcelProp, lpnbr));
        nbrlps.get(lp).add(lpnbr);
      }
      if(!nbrlps.get(lpnbr).contains(lp)) {
        add.add(factory.getOWLObjectPropertyAssertionAxiom(lpnbr, nbrParcelProp, lp));
        nbrlps.get(lpnbr).add(lp);
      }
    }

  }
}
