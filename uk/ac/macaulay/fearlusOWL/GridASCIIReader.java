/*
 * uk.ac.macaulay.fearlusOWL: GridASCIIReader.java
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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * GridASCIIReader
 * 
 * 
 * 
 * @author Gary Polhill
 */
public class GridASCIIReader {
  String filename;
  int nrows;
  int ncols;
  Double xllcorner = null;
  Double yllcorner = null;
  Double xllcenter = null;
  Double yllcenter = null;
  double cellsize;
  Double nodata_value = null;
  Double[][] grid;
  Set<Double> values;

  GridASCIIReader(String filename) throws IOException {
    boolean got_nrows = false;
    boolean got_ncols = false;
    boolean got_xllcorner = false;
    boolean got_yllcorner = false;
    boolean got_xllcenter = false;
    boolean got_yllcenter = false;
    boolean got_cellsize = false;
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

    String line;
    while((line = buff.readLine()) != null) {
      String words[] = line.split("\\s+");
      if(words[0].equalsIgnoreCase("nrows")) {
        nrows = Integer.parseInt(words[1]);
      }
      else if(words[0].equalsIgnoreCase("ncols")) {
        ncols = Integer.parseInt(words[1]);
      }
      else if(words[0].equalsIgnoreCase("xllcorner")) {
        xllcorner = Double.parseDouble(words[1]);
      }
      else if(words[0].equalsIgnoreCase("yllcorner")) {
        yllcorner = Double.parseDouble(words[1]);
      }
      else if(words[0].equalsIgnoreCase("xllcenter")) {
        xllcenter = Double.parseDouble(words[1]);
      }
      else if(words[0].equalsIgnoreCase("yllcenter")) {
        yllcenter = Double.parseDouble(words[1]);
      }
      else if(words[0].equalsIgnoreCase("cellsize")) {
        cellsize = Double.parseDouble(words[1]);
      }
      else if(words[0].equalsIgnoreCase("nodata_value")) {
        nodata_value = Double.parseDouble(words[1]);
      }
      else
        break;
    }

    if(!((got_nrows && got_ncols && got_xllcorner && got_yllcorner
      && got_cellsize && !got_xllcenter && !got_yllcenter) || (got_nrows
      && got_ncols && got_xllcenter && got_yllcenter && got_cellsize
      && !got_xllcorner && !got_yllcorner))) {
      // TODO exception
    }

    if(line == null) {
      // TODO exception
    }

    grid = new Double[ncols][nrows];
    values = new HashSet<Double>();

    int x = 0;
    int y = 0;
    do {
      String words[] = line.split("\\s+");

      for(int i = 0; i < words.length; i++) {
        if(y == nrows) {
          // TODO exception
        }
        grid[x][y] = Double.parseDouble(words[i]);
        if(((nodata_value != null && grid[x][y] != nodata_value) || (nodata_value == null))
          && !values.contains(grid[x][y])) {
          values.add(grid[x][y]);
        }
        x++;
        if(x == ncols) {
          x = 0;
          y++;
        }
      }
    } while(line != null);
  }
  
  public GridASCIIReader(String filename, final Set<GridASCIIReader> sameHeaders) throws IOException {
    this(filename);
    for(GridASCIIReader other: sameHeaders) {
      if(!equalsHeader(other)) {
        System.err.println("Grid " + filename + " has different georeferencing data than " + other + ", and they are expected to be the same");
        System.exit(1);
      }
    }
  }

  double at(int x, int y) {
    return grid[x][y];
  }

  double atrc(int row, int col) {
    return grid[col][row];
  }

  int nrows() {
    return nrows;
  }

  int ncols() {
    return ncols;
  }

  double xllcorner() {
    return xllcorner == null ? xllcenter - (cellsize / 2.0) : xllcorner;
  }

  double yllcorner() {
    return yllcorner == null ? yllcenter - (cellsize / 2.0) : yllcorner;
  }

  double xllcenter() {
    return xllcenter == null ? xllcorner + (cellsize / 2.0) : xllcenter;
  }

  double yllcenter() {
    return yllcenter == null ? yllcorner + (cellsize / 2.0) : yllcenter;
  }

  double cellsize() {
    return cellsize;
  }

  Double nodata_value() {
    return nodata_value;
  }

  boolean hasNodata_value() {
    return nodata_value != null;
  }

  Set<Double> values() {
    return new HashSet<Double>(values);
  }

  boolean containsValue(double value) {
    return values.contains(value);
  }

  public boolean equals(GridASCIIReader other) {
    if(!equalsHeader(other)) return false;
    for(int x = 0; x < ncols; x++) {
      for(int y = 0; y < nrows; y++) {
        if(grid[x][y] != other.at(x, y)) return false;
      }
    }
    return true;
  }
  
  public boolean equalsHeader(GridASCIIReader other) {
    if(nrows != other.nrows()) return false;
    if(ncols != other.ncols()) return false;
    if(cellsize != other.cellsize()) return false;
    if(xllcorner() != other.xllcorner()) return false;
    if(yllcorner() != other.yllcorner()) return false;
    if(nodata_value != other.nodata_value()) return false;
    return true;
  }
  
  public String toString() {
    return filename;
  }
}
