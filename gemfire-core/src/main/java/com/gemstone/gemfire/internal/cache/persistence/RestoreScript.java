/*=========================================================================
 * Copyright (c) 2002-2014 Pivotal Software, Inc. All Rights Reserved.
 * This product is protected by U.S. and international copyright
 * and intellectual property laws. Pivotal products are covered by
 * more patents listed at http://www.pivotal.io/patents.
 *=========================================================================
 */
package com.gemstone.gemfire.internal.cache.persistence;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.gemstone.gemfire.internal.FileUtil;

/**
 * This class is used to automatically generate a restore script for a backup. 
 * It keeps a list of files that were backed up, and a list of files that
 * we should test for to avoid overriding when we restore the backup.
 * 
 * It generates either a restore.sh for unix or a restore.bat for windows.
 * 
 * @author dsmith
 *
 */
public class RestoreScript {
  public static final String EXIT_MARKER = "Exit Functions";

  private static final ScriptGenerator UNIX_GENERATOR = new UnixScriptGenerator();
  private static final ScriptGenerator WINDOWS_GENERATOR = new WindowsScriptGenerator();
  
  private Map<File,File> baselineFiles = new HashMap<File,File>();
  private final Map<File, File> backedUpFiles = new LinkedHashMap<File, File>();
  private final List<File> existenceTests = new ArrayList<File>();

  public void addBaselineFiles(Map<File,File> baselineFiles) {
    this.baselineFiles.putAll(baselineFiles);
  }
  
  public void addFile(File originalFile, File backupFile) {
    backedUpFiles.put(backupFile, originalFile.getAbsoluteFile());
  }
  
  public void addExistenceTest(File originalFile) {
    existenceTests.add(originalFile.getAbsoluteFile());
  }
  
  public void generate(File outputDir) throws FileNotFoundException {
    if(isWindows()) {
      generateWindowsScript(outputDir);
    } else {
      generateUnixScript(outputDir);
    }
    
  }
  
  private void generateWindowsScript(File outputDir) throws FileNotFoundException {
    File outputFile = new File(outputDir, "restore.bat");
    generateScript(outputDir, outputFile, WINDOWS_GENERATOR);
  }

  private void generateUnixScript(File outputDir) throws FileNotFoundException {
    File outputFile = new File(outputDir, "restore.sh");
    generateScript(outputDir, outputFile, UNIX_GENERATOR);
  }
  
  private void generateScript(File outputDir, File outputFile, ScriptGenerator osGenerator) throws FileNotFoundException {
    PrintWriter writer = new PrintWriter(outputFile);
    try {
      osGenerator.writePreamble(writer);
      writer.println();
      osGenerator.writeComment(writer, "Restore a backup of gemfire persistent data to the location it was backed up");
      osGenerator.writeComment(writer, "from.");
      osGenerator.writeComment(writer, "This script will refuse to restore if the original data still exists.");
      writer.println();
      osGenerator.writeComment(writer, "This script was automatically generated by the gemfire backup utility.");
      writer.println();
      osGenerator.writeComment(writer, "Test for existing originals. If they exist, do not restore the backup.");
      for(File file: existenceTests) {
        osGenerator.writeExistenceTest(writer, file);
      }
      writer.println();
      osGenerator.writeComment(writer, "Restore data");
      for(Map.Entry<File, File> entry : backedUpFiles.entrySet()) {
        File backup = entry.getKey();
        boolean backupHasFiles = backup.isDirectory() && backup.list().length != 0;
        backup = FileUtil.removeParent(outputDir, backup);
        File original = entry.getValue();
        if(original.isDirectory()) {
          osGenerator.writeCopyDirectoryContents(writer, backup, original, backupHasFiles);
        } else {
          osGenerator.writeCopyFile(writer, backup, original);
        }
      }
      
      // Write out baseline file copies in restore script (if there are any) if this is a restore for an incremental backup
      if(!this.baselineFiles.isEmpty()) {
        writer.println();
        osGenerator.writeComment(writer, "Incremental backup.  Restore baseline originals from previous backups.");
        for(Map.Entry<File, File> entry : this.baselineFiles.entrySet()) {
          osGenerator.writeCopyFile(writer, entry.getKey(), entry.getValue());
        }        
      }

      if(isWindows()) {
        osGenerator.writeExit(writer);
      }

    } finally {
      writer.close();
    }
    outputFile.setExecutable(true, true);
  }

  //TODO prpersist - We've got this code replicated
  //in 10 different places in our product. Maybe we
  //need to put this method somewhere :)
  private boolean isWindows() {
    String os = System.getProperty("os.name");
    if (os != null) {
        if (os.indexOf("Windows") != -1) {
            return true;
        }
    }
    return false;
  }
  
  private static interface ScriptGenerator 
  {

    void writePreamble(PrintWriter writer);

    void writeExit(PrintWriter writer);

    void writeCopyFile(PrintWriter writer, File backup, File original);

    void writeCopyDirectoryContents(PrintWriter writer, File backup, File original, boolean backupHasFiles);

    void writeExistenceTest(PrintWriter writer, File file);

    void writeComment(PrintWriter writer, String string);
    
  };
  
  private static class WindowsScriptGenerator implements ScriptGenerator {
    final String ERROR_CHECK = "IF %ERRORLEVEL% GEQ 4 GOTO Exit_Bad";
    public void writePreamble(PrintWriter writer) {
      writer.println("echo off");
    }

    public void writeComment(PrintWriter writer, String string) {
      writer.println("rem " + string);
    }

    public void writeCopyDirectoryContents(PrintWriter writer, File backup, File original, boolean backupHasFiles) {
      writer.println("mkdir \"" + original + "\"");
      writer.println("C:\\Windows\\System32\\Robocopy.exe \"" + backup + "\" \"" + original + "\" /e /njh /njs");
      writer.println(ERROR_CHECK);
    }

    public void writeCopyFile(PrintWriter writer, File source, File destination) {
      String fileName = source.getName();
      String sourcePath = source.getParent() == null ? "." : source.getParent();
      String destinationPath = destination.getParent() == null ? "." : destination.getParent();
      writer.println("C:\\Windows\\System32\\Robocopy.exe \"" + sourcePath + "\" \"" + destinationPath + "\" " + fileName + " /njh /njs");
      writer.println(ERROR_CHECK);
    }

    public void writeExistenceTest(PrintWriter writer, File file) {
      writer.println("IF EXIST \"" + file + "\" echo \"Backup not restored. Refusing to overwrite " + file + "\" && exit /B 1 ");
    }

    public void writeExit(PrintWriter writer) {
      writeComment(writer, EXIT_MARKER);
      writer.println(":Exit_Good\nexit /B 0\n\n:Exit_Bad\nexit /B 1");
    }
  }
  
  private static class UnixScriptGenerator implements ScriptGenerator {
    public void writePreamble(PrintWriter writer) {
      writer.println("#!/bin/bash -e");
      writer.println("cd `dirname $0`");
    }

    public void writeComment(PrintWriter writer, String string) {
      writer.println("#" + string);
    }

    public void writeCopyDirectoryContents(PrintWriter writer, File backup,
        File original, boolean backupHasFiles) {
      writer.println("mkdir -p '" + original + "'");
      if(backupHasFiles) {
        writer.println("cp -rp '" + backup + "'/* '" + original + "'");
      }
    }

    public void writeCopyFile(PrintWriter writer, File backup, File original) {
      writer.println("cp -p '" + backup + "' '" + original + "'");
    }

    public void writeExistenceTest(PrintWriter writer, File file) {
      writer.println("test -e '" + file + "' && echo 'Backup not restored. Refusing to overwrite " + file + "' && exit 1 ");
    }

    public void writeExit(PrintWriter writer) {
    }
  }

}
