/*
 * uk.ac.macaulay.fearlusOWL: URIDoubleLookupTable.java
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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLIndividual;

/**
 * URIDoubleLookupTable
 * 
 * This is a lookup table for working with ontological entities. It can read
 * information in from a CSV file, which is assumed to have a header row. The
 * cells in the header row are URIs, which if OWL access is used, are assumed to
 * be URIs of OWL properties. The cells acting as keys are assumed to be URIs of
 * OWL individuals, and the outcome is a multidimensional array of Doubles.
 * 
 * <!-- There are various ways this could be improved. This class could be an
 * abstract class, where the type of outcome is generic. Concrete subclasses
 * would implement the equivalent of the makeDoubles method, constructing the
 * outcome dimensions from the string array supplied.
 * 
 * A possibility for a slightly different class is that the individuals given as
 * keys have values (obtained from an ontology) in the properties given as key
 * dimension headings which are to act as the basis for looking up the outcome.
 * Here, the input type would have to be string, which would then be converted
 * into the appropriate type. This would allow for the possibility of lookup
 * tables having ranges of values in keys, albeit that accessing the key from
 * the value supplied could be challenging algorithmically. -->
 * 
 * @author Gary Polhill
 */
public class StringDoubleLookupTable extends LookupTable<String, Double[]> {
  /**
   * The number of outcome dimensions
   */
  int noutcome;

  /**
   * Outcome dimension labels as URIs
   */
  URI[] outcomeLabels;

  /**
   * Map from outcome dimension labels to index in the array of outcomes
   */
  Map<URI, Integer> outcome2id;

  /**
   * A constant defining the default number of outcomes (1)
   */
  private final static int DEFAULT_N_OUTCOMES = 1;

  /**
   * A prefix to put on the front of column headings to obtain the full URI
   */
  String prefix;

  /**
   * Create an instance using a table with the default number of outcomes and no
   * prefix on the column headings.
   * 
   * @param table Table to use to build the lookup table
   * @throws URISyntaxException
   */
  public StringDoubleLookupTable(Table<String> table) throws URISyntaxException {
    this(table, "", DEFAULT_N_OUTCOMES);
  }

  /**
   * Create an instance using a table with the default number of outcomes and a
   * prefix on the column headings
   * 
   * @param table Table to use to build the lookup table
   * @param prefix Prefix to put on the front of each heading (e.g.
   *          "http://web.address.ac.uk/ontologies/amodel.owl#")
   * @throws URISyntaxException
   */
  public StringDoubleLookupTable(Table<String> table, String prefix) throws URISyntaxException {
    this(table, prefix, DEFAULT_N_OUTCOMES);
  }

  /**
   * Create an instance using a specified number of outcomes and no prefix on
   * the column headings
   * 
   * @param table Table to use to build the lookup table
   * @param noutcome Number of dimensions of outcome
   * @throws URISyntaxException
   */
  public StringDoubleLookupTable(Table<String> table, int noutcome) throws URISyntaxException {
    this(table, "", noutcome);
  }

  /**
   * Create an instance using a specified number of outcomes and prefix on the
   * column headings
   * 
   * @param table Table to use to build the lookup table
   * @param prefix Prefix to put on the front of each heading
   * @param noutcome Number of dimensions of outcome
   * @throws URISyntaxException
   */
  public StringDoubleLookupTable(Table<String> table, String prefix, int noutcome) throws URISyntaxException {
    super(prefix, table.atrc2c(0, 0, table.ncols() - (noutcome + 1)).toArray(new String[0]));

    this.prefix = prefix;

    outcomeLabels =
      makeURIs(prefix, table.atrc2c(0, table.ncols() - noutcome, table.ncols() - 1).toArray(new String[0]));
    Map<URI, Integer> outcome2id = new HashMap<URI, Integer>();
    this.noutcome = noutcome;
    for(int i = 0; i < outcomeLabels.length; i++) {
      outcome2id.put(outcomeLabels[i], i);
    }

    for(int row = 1; row < table.nrows(); row++) {
      String[] input = makeStringURIs(makeURIs(prefix, table.atrc2c(row, 0, table.ncols() - (noutcome + 1)).toArray(new String[0])));
      Double[] outcome =
        makeDoubles(table.atrc2c(row, table.ncols() - noutcome, table.ncols() - 1).toArray(new String[0]));
      add(outcome, input);
    }
  }

  /**
   * Build a lookup table from a CSV file with no prefix and default number of
   * outcome dimensions
   * 
   * @param csvFileName CSV file name
   * @throws IOException
   * @throws CSVException
   * @throws URISyntaxException
   */
  public StringDoubleLookupTable(String csvFileName) throws IOException, CSVException, URISyntaxException {
    this(csvFileName, "", DEFAULT_N_OUTCOMES);
  }

  /**
   * Build a lookup table from a CSV file with no prefix and a specified number
   * of outcome dimensions
   * 
   * @param csvFileName CSV file name
   * @param noutcome Number of dimensions of outcome
   * @throws IOException
   * @throws CSVException
   * @throws URISyntaxException
   */
  public StringDoubleLookupTable(String csvFileName, int noutcome) throws IOException, CSVException, URISyntaxException {
    this(csvFileName, "", noutcome);
  }

  /**
   * Build a lookup table from a CSV file with a given prefix and default number
   * of outcome dimensions
   * 
   * @param csvFileName CSV file name
   * @param prefix Prefix to use on column headings to obtain full URI
   * @throws IOException
   * @throws CSVException
   * @throws URISyntaxException
   */
  public StringDoubleLookupTable(String csvFileName, String prefix) throws IOException, CSVException, URISyntaxException {
    this(csvFileName, prefix, DEFAULT_N_OUTCOMES);
  }

  /**
   * Build a lookup table from a CSV file with a given prefix and specified
   * number of outcome dimensions
   * 
   * @param csvFileName CSV file name
   * @param prefix Prefix to use on column headings to obtain full URI
   * @param noutcome Number of dimensions of outcome
   * @throws IOException
   * @throws CSVException
   * @throws URISyntaxException
   */
  public StringDoubleLookupTable(String csvFileName, String prefix, int noutcome) throws IOException, CSVException,
      URISyntaxException {
    this((new CSVReader(csvFileName)).getTable(), prefix, noutcome);
  }

  /**
   * <!-- makeURIs -->
   * 
   * Private method to build a list of URIs from a list of strings with a given
   * prefix to put on each string to get the full URI
   * 
   * @param prefix The prefix, if any
   * @param strs The strings to use
   * @return An array of URIs
   * @throws URISyntaxException
   */
  private static URI[] makeURIs(String prefix, String[] strs) throws URISyntaxException {
    URI[] uris = new URI[strs.length];
    for(int i = 0; i < strs.length; i++) {
      URI uri = new URI(prefix + strs[i]);
      uris[i] = uri;
    }
    return uris;
  }

  /**
   * <!-- makeURIs -->
   * 
   * Private method to build a list of URIs from a list of OWL entities
   * 
   * @param entities The OWL entities from which to obtain the URIs
   * @return An array of URIs
   */
  private static URI[] makeURIs(OWLEntity[] entities) {
    URI[] uris = new URI[entities.length];
    for(int i = 0; i < entities.length; i++) {
      uris[i] = entities[i].getURI();
    }
    return uris;
  }
  
  private static String[] makeStringURIs(URI[] uris) {
    String[] strs = new String[uris.length];
    for(int i = 0; i < strs.length; i++) {
      strs[i] = uris[i].toString();
    }
    return strs;
  }

  /**
   * <!-- makeDoubles -->
   * 
   * Private method to make an array of Doubles from an array of Strings
   * 
   * <!-- This method should be abstract and implemented by subclasses to allow
   * generic outcome with this class -->
   * 
   * @param strs The array of strings
   * @return
   */
  private static Double[] makeDoubles(String[] strs) {
    Double[] ds = new Double[strs.length];
    for(int i = 0; i < strs.length; i++) {
      if(strs[i] == null) ds[i] = null;
      else ds[i] = new Double(strs[i]);
    }
    return ds;
  }

  /**
   * <!-- makeURIs -->
   * 
   * Private method to build a list of URIs from an iterable collection of OWL
   * entities
   * 
   * @param entities The entities from which to build the list
   * @return A list of URIs
   */
  private static LinkedList<URI> makeURIs(Iterable<? extends OWLEntity> entities) {
    LinkedList<URI> uris = new LinkedList<URI>();
    for(OWLEntity entity: entities) {
      uris.addLast(entity.getURI());
    }
    return uris;
  }

  /**
   * <!-- getInputs -->
   * 
   * Return the set of URI inputs for the URI dimension label
   * 
   * @param label The URI dimension label
   * @return The set of URI inputs
   */
  public Set<URI> getInputs(URI label) {
    Set<String> strInputs = getInputs(label.toString());
    Set<URI> uriInputs = new HashSet<URI>();
    for(String str: strInputs) {
      uriInputs.add(URI.create(str));
    }
    return uriInputs;
  }

  /**
   * <!-- getInputs -->
   * 
   * Return the set of URI inputs for the OWLEntity dimension label
   * 
   * @param label The OWLEntity dimension label
   * @return The set of URI inputs
   */
  public Set<URI> getInputs(OWLEntity label) {
    return getInputs(label.getURI());
  }

  /**
   * <!-- getInputs -->
   * 
   * Return the set of OWLIndividual inputs for the OWLEntity dimension label,
   * using the supplied factory
   * 
   * @param label The OWLEntity dimension label
   * @param factory The factory to use to build the OWLIndividuals
   * @return A set of OWLIndividuals
   */
  public Set<OWLIndividual> getInputs(OWLEntity label, OWLDataFactory factory) {
    Set<URI> uriSet = getInputs(label);
    Set<OWLIndividual> indieSet = new HashSet<OWLIndividual>();

    for(URI uri: uriSet) {
      indieSet.add(factory.getOWLIndividual(uri));
    }

    return indieSet;
  }

  /**
   * <!-- lookup -->
   * 
   * Lookup the outcome using a map of URI dimension labels to URI keys. If a
   * prefix was specified in the constructor it will be removed, as the headers
   * added from the CSV file will not contain the prefix.
   * 
   * @param input Map of URIs of dimensions (e.g. properties) to URIs of key
   *          values (e.g. individuals)
   * @return The outcome for this key
   */
  public Double[] lookupURI(Map<URI, URI> input) {
    Map<String, String> labelInput = new HashMap<String, String>();
    for(URI key: input.keySet()) {
      String keystr = key.toString();
      if(!prefix.equals("") && keystr.startsWith(prefix)) {
        keystr = keystr.substring(prefix.length(), keystr.length());
      }
      labelInput.put(keystr, input.get(key).toString());
    }
    return lookup(labelInput);
  }

  /**
   * <!-- lookup -->
   * 
   * Lookup a particular outcome dimension URI
   * 
   * @param outcomeLabel The URI of the outcome label
   * @param input The URIs of the input keys
   * @return The required dimension of outcome
   */
  public double lookup(URI outcomeLabel, URI... input) {
    Double[] outcome = lookup(makeStringURIs(input));
    return outcome[outcome2id.get(outcomeLabel)];
  }

  /**
   * <!-- lookup -->
   * 
   * Lookup a particular outcome dimension URI using an iterable collection of
   * keys
   * 
   * @param outcomeLabel The URI of the outcome dimension required
   * @param input The keys to that outcome
   * @return The outcome required
   */
  public double lookup(URI outcomeLabel, Iterable<URI> input) {
    LinkedList<String> strInput = new LinkedList<String>();
    for(URI uri: input) {
      strInput.addLast(uri.toString());
    }
    Double[] outcome = lookup(strInput);
    return outcome[outcome2id.get(outcomeLabel)];
  }

  /**
   * <!-- lookup -->
   * 
   * Lookup a particular outcome dimension URI using an iterator over a
   * collection of keys
   * 
   * @param outcomeLabel The URI of the outcome dimension required
   * @param input The keys to that outcome
   * @return The outcome required
   */
  public double lookup(URI outcomeLabel, Iterator<URI> input) {
    LinkedList<String> strInput = new LinkedList<String>();
    while(input.hasNext()) {
      strInput.addLast(input.next().toString());
    }
    Double[] outcome = lookup(strInput.iterator());
    return outcome[outcome2id.get(outcomeLabel)];
  }

  /**
   * <!-- lookup -->
   * 
   * Lookup a particular outcome dimension given as an OWLEntity.
   * 
   * @param entity The OWLEntity to use
   * @param input The input keys to that outcome
   * @return The outcome responding to the specified property for the given keys
   */
  public double lookup(OWLEntity entity, OWLIndividual... input) {
    return lookup(entity.getURI(), makeURIs(input));
  }

  /**
   * <!-- lookup -->
   * 
   * Lookup a particular outcome dimension given as an OWLEntity using an
   * iterable collection of keys
   * 
   * @param entity The OWLEntity to use
   * @param input The input key collection
   * @return The dimension of outcome required that matches the given keys
   */
  public double lookup(OWLEntity entity, Iterable<OWLIndividual> input) {
    return lookup(entity.getURI(), makeURIs(input));
  }

  /**
   * <!-- lookup -->
   * 
   * Lookup a particular outcome dimension with the keys given as a map from an
   * OWLEntity to an individual
   * 
   * @param entity The entity with the outcome dimension required
   * @param input Map of entities for key dimensions to corresponding
   *          OWLIndividual keys
   * @return The outcome required
   */
  public double lookup(OWLEntity entity, Map<? extends OWLEntity, OWLIndividual> input) {
    Map<URI, URI> labelInput = new HashMap<URI, URI>();
    for(OWLEntity key: input.keySet()) {
      labelInput.put(key.getURI(), input.get(key).getURI());
    }
    Double[] outcome = lookupURI(labelInput);
    return outcome[outcome2id.get(entity.getURI())];
  }

}
