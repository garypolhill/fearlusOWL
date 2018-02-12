/*
 * uk.ac.macaulay.fearlusOWL: StringLookupTable.java
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
import java.util.Iterator;
import java.util.Map;

/**
 * StringLookupTable
 * 
 * This is a subclass of the generic LookupTable, which maps string keys onto
 * string outcomes. It provides constructors that take a Table&lt;String&gt; as
 * input, or load the table in from a CSV file (assumed to have a header row).
 * Additionally, outcomes can be multi-dimensional, each having a specified
 * label, and methods are provided to access each dimension uniquely.
 * 
 * @author Gary Polhill
 */
public class StringLookupTable extends LookupTable<String, String[]> {
  /**
   * The number of outcome dimensions this lookup table has
   */
  int noutcome;

  /**
   * Labels for the outcome dimensions
   */
  String[] outcomeLabels;

  /**
   * Map from label name to the number of the dimension of outcome the label
   * corresponds to.
   */
  Map<String, Integer> outcome2id;

  /**
   * Constructor taking a table as argument, which assumes one dimension of
   * outcome, assumed to be the rightmost column of the table.
   * 
   * @param table Table from which to build the lookup table
   */
  public StringLookupTable(final Table<String> table) {
    this(table, 1);
  }

  /**
   * Constructor allowing multiple dimensions of outcome, assumed to be the
   * rightmost <code>noutcome</code> columns of data in the table.
   * 
   * @param table The table from which to build the lookup table
   * @param noutcome The number of outcome dimensions.
   */
  public StringLookupTable(final Table<String> table, int noutcome) {
    super(table.atrc2c(0, 0, table.ncols() - (noutcome + 1)).toArray(new String[0]));
    outcomeLabels = table.atrc2c(0, table.ncols() - noutcome, table.ncols() - 1).toArray(new String[0]);
    Map<String, Integer> outcome2id = new HashMap<String, Integer>();
    this.noutcome = noutcome;
    for(int i = 0; i < outcomeLabels.length; i++) {
      outcome2id.put(outcomeLabels[i], i);
    }

    for(int row = 1; row < table.nrows(); row++) {
      String[] input = table.atrc2c(row, 0, table.ncols() - (noutcome + 1)).toArray(new String[0]);
      String[] outcome = table.atrc2c(row, table.ncols() - noutcome, table.ncols() - 1).toArray(new String[0]);
      add(outcome, input);
    }
  }

  /**
   * Load a table from a CSV file, with the default one dimension of outcome
   * 
   * @param csvFileName The CSV file from which to load the lookup table
   * @throws IOException
   * @throws CSVException
   */
  public StringLookupTable(String csvFileName) throws IOException, CSVException {
    this(csvFileName, 1);
  }

  /**
   * Load a table from a CSV file, with a specified number of dimensions of
   * outcome
   * 
   * @param csvFileName The CSV file from which to load the lookup table
   * @param noutcome The number of dimensions of outcome
   * @throws IOException
   * @throws CSVException
   */
  public StringLookupTable(String csvFileName, int noutcome) throws IOException, CSVException {
    this((new CSVReader(csvFileName)).getTable(), noutcome);
  }

  /**
   * <!-- lookup -->
   * 
   * Lookup a single dimension of outcome given by its label
   * 
   * @param outcomeLabel The label of the outcome dimension required
   * @param input The keys to use
   * @return The outcome
   */
  public String lookup(String outcomeLabel, String... input) {
    String[] outcome = lookup(input);
    return outcome[outcome2id.get(outcomeLabel)];
  }

  /**
   * <!-- lookup -->
   * 
   * Lookup a single dimension of outcome with its label, using an iterable
   * collection of input keys
   * 
   * @param outcomeLabel The label of the outcome dimension required
   * @param input The keys to use
   * @return The outcome
   */
  public String lookup(String outcomeLabel, final Iterable<String> input) {
    String[] outcome = lookup(input);
    return outcome[outcome2id.get(outcomeLabel)];
  }

  /**
   * <!-- lookup -->
   * 
   * Lookup a single dimension of outcome with its label, using an iterator over
   * a collection of input keys
   * 
   * @param outcomeLabel The label of the outcome dimension required
   * @param input The keys to use
   * @return The outcome
   */
  public String lookup(String outcomeLabel, Iterator<String> input) {
    String[] outcome = lookup(input);
    return outcome[outcome2id.get(outcomeLabel)];
  }
}
