/*
 * uk.ac.macaulay.fearlusOWL: Main.java
 * 
 * Copyright (C) 2012 Macaulay Institute
 * 
 * This file is part of fearlusOWL.
 * 
 * fearlusOWL is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * fearlusOWL is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with fearlusOWL. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contact information: Gary Polhill Macaulay Institute, Craigiebuckler,
 * Aberdeen. AB15 8QH. UK. g.polhill@macaulay.ac.uk
 */
package uk.ac.macaulay.fearlusOWL;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * <!-- Main -->
 * 
 * @author Gary Polhill
 */
public class Main {
  public static final String TMP_DIR = "fearlusOWL";
  public static final String RESOURCES_DIR = "/resources/";
  public static final String DEFAULT_SCHEDULE = "test2.xml";
  public static final String DEFAULT_RUN_BASE = "test.owl";

  // It would be nicer to read these from the default schedule...
  public static final String DEFAULT_INCOME_LOOKUP = "incomeLookup.csv";
  public static final String DEFAULT_YIELD_LOOKUP = "yieldLookup.csv";

  public static final String INCOME_VAR = "$INCOME_LOOKUP$";
  public static final String YIELD_VAR = "$YIELD_LOOKUP$";

  /**
   * <!-- main -->
   * 
   * @param args
   */
  public static void main(String[] args) {
    File dir =
      new File(System.getProperty("java.io.tmpdir") + File.separator + TMP_DIR + "_" + System.currentTimeMillis());
    if(!dir.mkdir()) {
      System.err.println("Could not create directory: " + dir);
      System.exit(1);
    }
    File outdir = new File(dir + File.separator + "output");
    if(!outdir.mkdir()) {
      System.err.println("Could not create directory: " + dir);
      System.exit(1);
    }
    File base = new File(outdir + File.separator + DEFAULT_RUN_BASE);

    HashMap<String, String> scheduleSubstitution = new HashMap<String, String>();
    scheduleSubstitution.put(INCOME_VAR, dir + DEFAULT_INCOME_LOOKUP);
    scheduleSubstitution.put(YIELD_VAR, dir + DEFAULT_YIELD_LOOKUP);

    copyResource(DEFAULT_INCOME_LOOKUP, dir);
    copyResource(DEFAULT_YIELD_LOOKUP, dir);
    copyResource(DEFAULT_SCHEDULE, dir, scheduleSubstitution);

    File schedule = new File(dir + File.separator + DEFAULT_SCHEDULE);

    RunSequence.main(new String[] { base.toURI().toString(), schedule.toString() });

    System.out.println("Results saved to " + dir);
  }

  public static void copyResource(String resource, File destDir) {
    copyResource(resource, destDir, new HashMap<String, String>());
  }

  public static void copyResource(String resource, File destDir, Map<String, String> replace) {
    InputStream is = Main.class.getResourceAsStream(RESOURCES_DIR + resource);
    if(is == null) {
      System.err.println("Cannot find resource " + RESOURCES_DIR + resource);
      System.exit(1);
    }
    BufferedReader fp = new BufferedReader(new InputStreamReader(is));

    try {
      PrintWriter out = new PrintWriter(destDir + File.separator + resource);
      String line;
      for(line = fp.readLine(); line != null; line = fp.readLine()) {
        for(String key: replace.keySet()) {
          if(line.contains(key)) {
            line = line.replace(key, replace.get(key));
          }
        }
        out.println(line);
      }
      out.close();
      fp.close();
    }
    catch(FileNotFoundException e) {
      System.err.println("Cannot copy resource " + RESOURCES_DIR + resource + " to " + destDir + File.separator
        + resource + ": " + e);
      System.exit(1);
    }
    catch(IOException e) {
      System.err.println("I/O error copying resource " + RESOURCES_DIR + resource + " to " + destDir + File.separator
        + resource + ": " + e);
      System.exit(1);
    }
  }

}
