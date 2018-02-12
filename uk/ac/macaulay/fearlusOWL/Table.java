/*
 * uk.ac.macaulay.fearlusOWL: Table.java
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

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Table
 * 
 * 
 * 
 * @author Gary Polhill
 */
public class Table<T> implements Iterable<T> {

  private ArrayList<ArrayList<T>> cells;
  int nrows;
  int ncols;

  public Table(int nrows, int ncols) {
    this.nrows = nrows;
    this.ncols = ncols;

    synchronized(this) {
      cells = new ArrayList<ArrayList<T>>(nrows);
      for(int i = 0; i < nrows; i++) {
        ArrayList<T> row = new ArrayList<T>(ncols);
        cells.add(i, row);
        for(int j = 0; j < ncols; j++) {
          row.add(j, null);
        }
      }
    }
  }
  
  public int nrows() {
    return nrows;
  }
  
  public int ncols() {
    return ncols;
  }

  public T atxy(int x, int y) {
    if(x < 0 || x >= ncols || y < 0 || y >= nrows) throw new ArrayIndexOutOfBoundsException();
    return cells.get(y).get(x);
  }

  public T atyx(int x, int y) {
    if(x < 0 || x >= ncols || y < 0 || y >= nrows) throw new ArrayIndexOutOfBoundsException();
    return cells.get((nrows - y) - 1).get(x);
  }

  public T atrc(int row, int col) {
    if(col < 0 || col >= ncols || row < 0 || row >= nrows) throw new ArrayIndexOutOfBoundsException();
    return cells.get(row).get(col);
  }
  
  public ArrayList<T> atrc2c(int row, int colstart, int colend) {
    ArrayList<T> arr = new ArrayList<T>((colend - colstart) + 1);
    
    for(int i = colstart; i <= colend; i++) {
      arr.add(i - colstart, atrc(row, i));
    }
    
    return arr;
  }

  public synchronized void atxy(int x, int y, T value) {
    if(x < 0 || x >= ncols || y < 0 || y >= nrows) throw new ArrayIndexOutOfBoundsException();
    cells.get(y).add(x, value);
  }

  public synchronized void atyx(int x, int y, T value) {
    if(x < 0 || x >= ncols || y < 0 || y >= nrows) throw new ArrayIndexOutOfBoundsException();
    cells.get((nrows - y) - 1).add(x, value);
  }

  public synchronized void atrc(int row, int col, T value) {
    if(col < 0 || col >= ncols || row < 0 || row >= nrows) throw new ArrayIndexOutOfBoundsException();
    cells.get(row).add(col, value);
  }

  public ArrayList<T> flatten() {
    ArrayList<T> flattened = new ArrayList<T>(nrows * ncols);

    int i = 0;
    synchronized(this) {
      for(int r = 0; r < nrows; r++) {
        for(int c = 0; c < ncols; c++) {
          flattened.add(i, cells.get(r).get(c));
          i++;
        }
      }
    }
    return flattened;
  }

  public Iterator<T> iterator() {
    return new TableIterator<T>(this);
  }

  private class TableIterator<U> implements Iterator<U> {
    int r;
    int c;
    Table<U> table;

    TableIterator(Table<U> table) {
      this.table = table;
      r = 0;
      c = 0;
    }

    public boolean hasNext() {
      if(r + 1 < table.nrows()) return true;
      else if(c < table.ncols()) return true;
      else
        return false;
    }

    public U next() {
      if(c < table.ncols()) return table.atrc(r, c++);
      else if(++r < table.nrows()) {
        c = 0;
        return table.atrc(r, c++);
      }
      return null;
    }

    public void remove() {
      throw new UnsupportedOperationException();
    }
  }
}
