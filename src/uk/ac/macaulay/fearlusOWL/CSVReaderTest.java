/* uk.ac.macaulay.fearlusOWL: CSVReaderTest.java
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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import junit.framework.TestCase;

/**
 * CSVReaderTest
 *
 * 
 *
 * @author Gary Polhill
 */
public class CSVReaderTest extends TestCase {

  /**
   * Test method for {@link uk.ac.macaulay.fearlusOWL.CSVReader#CSVReader(java.lang.String)}.
   * @throws IOException 
   * @throws CSVException 
   */
  public void testCSVReader() throws IOException, CSVException {
    File tmp = File.createTempFile("CSVReaderTest", ".csv");
    tmp.deleteOnExit();
    
    FileWriter fp = new FileWriter(tmp);
    PrintWriter pw = new PrintWriter(fp);
    
    int n = 0;
    for(int row = 0; row < 100; row++) {
      for(int col = 0; col < 6; col++) {
        pw.print(++n);
        if(col < 5) pw.print(",");
      }
      pw.println();
    }
    fp.close();
    
    CSVReader reader = new CSVReader(tmp.getCanonicalPath());
    Table<String> t = reader.getTable();
    
    n = 0;
    for(String str: t) {
      assertEquals(++n, Integer.parseInt(str));
    }
    
  }

  /**
   * Test method for {@link uk.ac.macaulay.fearlusOWL.CSVReader#tokenize(java.lang.String)}.
   */
  public void testTokenize() {
    String row = "a,\"b,c\",\"e,\"\"d\"\",f\",g";
    String[] tokens = new String[] { "a", ",", "\"", "b", ",", "c", "\"", ",", "\"", "e", ",", "\"", "\"", "d", "\"", "\"", ",", "f", "\"", ",", "g" };
    
    int i = 0;
    for(String token: CSVReader.tokenize(row)) {
      assertEquals(token, tokens[i++]);
    }
    assertEquals(tokens.length, i);
  }

  /**
   * Test method for {@link uk.ac.macaulay.fearlusOWL.CSVReader#parseCells(java.lang.String, java.lang.String, int)}.
   */
  public void testParseCellsStringStringInt() {
    String row = "a,\"b,c\",\"e,\"\"d\"\",f\",g";
    String[] cells = new String[] { "a", "b,c", "e,\"d\",f", "g" };
    
    int i = 0;
    try {
      for(String token: CSVReader.parseCells(row, "buffer", 0)) {
        assertEquals(cells[i++], token);
      }
    }
    catch(CSVException e) {
      fail(e.toString());
    }
    assertEquals(cells.length, i);
    
    String[] errow = new String[] { "a,b\"c\"d,e", "a,b,c,\"" };
    for(i = 0; i < errow.length; i++) {
      try {
        CSVReader.parseCells(errow[i]);
        fail("Parser parsed illegal CSV string: " + errow[i]);
      }
      catch(CSVException e) {
      }
    }
    
  }

}
