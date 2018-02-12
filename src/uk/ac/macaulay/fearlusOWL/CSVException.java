/* uk.ac.macaulay.fearlusOWL: CSVException.java
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

/**
 * CSVException
 *
 * 
 *
 * @author Gary Polhill
 */
public class CSVException extends Exception {
  /**
   * 
   */
  private static final long serialVersionUID = -3283281942437471968L;
  String expected;
  String found;
  int row;
  int col;
  String filename = null;
  
  public CSVException(String expected, String found, String filename, int row, int col) {
    this.expected = expected;
    this.found = found;
    this.filename = filename;
    this.row = row;
    this.col = col;
  }
  
  public String getMessage() {
    if(filename == null) {
      return "CSV error at column " + col + ": expected \"" + expected + "\", found \"" + found + "\"";
    }
    return "Error in CSV file " + filename + " at row " + row + ", column " + col + ": expected \"" + expected + "\", found \"" + found + "\"";
  }
}
