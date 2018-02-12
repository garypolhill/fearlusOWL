/* uk.ac.macaulay.fearlusOWL: LookupTableTest.java
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

import junit.framework.TestCase;

/**
 * LookupTableTest
 *
 * 
 *
 * @author Gary Polhill
 */
public class LookupTableTest extends TestCase {

  /**
   * Test method for {@link uk.ac.macaulay.fearlusOWL.LookupTable#LookupTable()}.
   */
  public void testLookupTable() {
    LookupTable<String, String> lookupTable = new LookupTable<String, String>();
    String[][] data = new String[][] { { "a", "b", "ab" }, { "a", "c", "e", "ace" }, { "c", "d", "e", "cde" } };
    String[][] lookups = new String[data.length][];
    String[] outcomes = new String[data.length];
    for(int i = 0; i < data.length; i++) {
      lookups[i] = new String[data[i].length - 1];
      for(int j = 0; j < data[i].length - 1; j++) {
        lookups[i][j] = data[i][j];
      }
      outcomes[i] = data[i][data[i].length - 1];
      lookupTable.add(outcomes[i], lookups[i]);
    }
    
    for(int i = 0; i < data.length; i++) {
      String lookupOutcome = lookupTable.lookup(lookups[i]);
      assertEquals(outcomes[i], lookupOutcome);
    }
  }

  /**
   * Test method for {@link uk.ac.macaulay.fearlusOWL.LookupTable#LookupTable(java.lang.String[])}.
   */
  public void testLookupTableStringArray() {
    fail("Not yet implemented");
  }

  /**
   * Test method for {@link uk.ac.macaulay.fearlusOWL.LookupTable#getInputs(java.lang.String)}.
   */
  public void testGetInputs() {
    fail("Not yet implemented");
  }

  /**
   * Test method for {@link uk.ac.macaulay.fearlusOWL.LookupTable#add(java.lang.Object, I[])}.
   */
  public void testAddOIArray() {
    fail("Not yet implemented");
  }

  /**
   * Test method for {@link uk.ac.macaulay.fearlusOWL.LookupTable#add(java.lang.Object, java.lang.Iterable)}.
   */
  public void testAddOIterableOfI() {
    fail("Not yet implemented");
  }

  /**
   * Test method for {@link uk.ac.macaulay.fearlusOWL.LookupTable#add(java.lang.Object, java.util.Iterator)}.
   */
  public void testAddOIteratorOfI() {
    fail("Not yet implemented");
  }

  /**
   * Test method for {@link uk.ac.macaulay.fearlusOWL.LookupTable#add(java.lang.Object, java.util.Map)}.
   */
  public void testAddOMapOfStringI() {
    fail("Not yet implemented");
  }

  /**
   * Test method for {@link uk.ac.macaulay.fearlusOWL.LookupTable#lookup(java.util.Map)}.
   */
  public void testLookupMapOfStringI() {
    fail("Not yet implemented");
  }

  /**
   * Test method for {@link uk.ac.macaulay.fearlusOWL.LookupTable#lookup(java.lang.Object)}.
   */
  public void testLookupI() {
    fail("Not yet implemented");
  }

  /**
   * Test method for {@link uk.ac.macaulay.fearlusOWL.LookupTable#lookup(I[])}.
   */
  public void testLookupIArray() {
    fail("Not yet implemented");
  }

  /**
   * Test method for {@link uk.ac.macaulay.fearlusOWL.LookupTable#lookup(java.lang.Iterable)}.
   */
  public void testLookupIterableOfI() {
    fail("Not yet implemented");
  }

  /**
   * Test method for {@link uk.ac.macaulay.fearlusOWL.LookupTable#lookup(java.util.Iterator)}.
   */
  public void testLookupIteratorOfI() {
    fail("Not yet implemented");
  }

}
