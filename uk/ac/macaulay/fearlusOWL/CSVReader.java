/* uk.ac.macaulay.fearlusOWL: CSVReader.java
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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.LinkedList;

/**
 * CSVReader
 *
 * 
 *
 * @author Gary Polhill
 */
public class CSVReader {
  LinkedList<LinkedList<String>> cells;
  int nrows;
  int ncols;
  String filename;
  
  private static String working_on_file = null;
  private static int working_on_row;
  
  CSVReader(String filename) throws IOException, CSVException {
    this.filename = filename;
    InputStreamReader read;
    if(filename.substring(0, 6).equalsIgnoreCase("http://")
      || filename.substring(0, 7).equalsIgnoreCase("https://")) {
      URL urlp = new URL(filename);
      read = new InputStreamReader(urlp.openStream());
    }
    else {
      FileInputStream fp = new FileInputStream(filename);
      read = new InputStreamReader(fp);
    }
    BufferedReader buff = new BufferedReader(read);
    cells = new LinkedList<LinkedList<String>>();
    nrows = 0;
    ncols = 0;
    String line;
    while((line = buff.readLine()) != null) {
      LinkedList<String> row = parseCells(line, filename, nrows + 1);
      if(row.size() > ncols) ncols = row.size();
      cells.addLast(row);
      nrows++;
    }
  }
  
  public Table<String> getTable() {
    Table<String> table = new Table<String>(nrows, ncols);
    
    int row_n = 0;
    for(LinkedList<String> row: cells) {
      int col_n = 0;
      
      for(String cell: row) {
        table.atrc(row_n, col_n, cell);
        col_n++;
      }
      row_n++;
    }
    
    return table;
  }
  
  public static LinkedList<String> tokenize(String line) {
    char[] chars = line.toCharArray();
    StringBuffer buf = new StringBuffer();
    LinkedList<String> tokens = new LinkedList<String>();
    for(int i = 0; i < chars.length; i++) {
      if(chars[i] == '"' || chars[i] == ',') {
        if(buf.length() > 0) {
          tokens.addLast(buf.toString());
          buf = new StringBuffer();
        }
        tokens.addLast(new String(new char[] { chars[i] }));
      }
      else {
        buf.append(chars[i]);
      }
    }
    if(buf.length() > 0) tokens.addLast(buf.toString());
    
    return tokens;
  }
  
  public static synchronized LinkedList<String> parseCells(String line, String file, int row) throws CSVException {
    working_on_file = file;
    working_on_row = row;
    LinkedList<String> rowcells = parseCells(line);
    working_on_file = null;
    working_on_row = 0;
    return rowcells;
  }
  
  public static synchronized LinkedList<String> parseCells(String line) throws CSVException {
    LinkedList<String> tokens = tokenize(line);
    LinkedList<String> row = new LinkedList<String>();
    parseCells(tokens, row);
    return row;
  }
  
  private static void parseCells(LinkedList<String> tokens, LinkedList<String> row) throws CSVException {
    parseCells(tokens, row, new StringBuffer());
  }
  
  private static void parseCells(LinkedList<String> tokens, LinkedList<String> row, StringBuffer cell) throws CSVException {
    if(tokens.size() == 0) {
      row.addLast(cell.toString());
      return;
    }
    String token = tokens.removeFirst();
    if(token.equals(",")) {
      row.addLast(cell.toString());
      parseCells(tokens, row, new StringBuffer());
    }
    else if(token.equals("\"")) {
      parseQuotedCell(tokens, row, cell);
    }
    else {
      cell.append(token);
      parseCells(tokens, row, cell);
    }
  }
  
  private static void parseQuotedCell(LinkedList<String> tokens, LinkedList<String> row, StringBuffer cell) throws CSVException {
    if(tokens.size() == 0) {
      throw new CSVException("\" (quote)", "end of line", working_on_file, working_on_row, row.size() + 1);
    }
    String token = tokens.removeFirst();
    if(token.equals("\"")) {
      if(tokens.size() == 0) return;
      String next_token = tokens.removeFirst();
      if(next_token.equals("\"")) {
        cell.append(token);
        parseQuotedCell(tokens, row, cell);
      }
      else if(next_token.equals(",")) {
        row.addLast(cell.toString());
        parseCells(tokens, row, new StringBuffer());
      }
      else {
        throw new CSVException("\" (quote) or , (comma)", token, working_on_file, working_on_row, row.size() + 1);
      }
    }
    else {
      cell.append(token);
      parseQuotedCell(tokens, row, cell);
    }
  }
}
