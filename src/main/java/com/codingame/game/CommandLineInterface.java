package com.codingame.game;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.Properties;

import org.apache.commons.cli.*;

import com.codingame.gameengine.runner.GameRunner;
import com.codingame.gameengine.runner.dto.GameResult;
import com.google.common.io.Files;

public class CommandLineInterface {

  public static void main(String[] args) {
    try {
      Options options = new Options();
  
      options.addOption("h", false, "Print the help")
             .addOption("p1", true, "Required. Player 1 command line.")
             .addOption("p2", true, "Required. Player 2 command line.")
             .addOption("p3", true, "Player 3 command line.")
             .addOption("p4", true, "Player 4 command line.")
             .addOption("s", false, "Server mode")
             .addOption("l", true, "File output for logs");
      
      CommandLine cmd = new DefaultParser().parse(options, args);
      
      if (cmd.hasOption("h") || !cmd.hasOption("p1") || !cmd.hasOption("p2")) {
        new HelpFormatter().printHelp("-p1 <player1 command line> -p2 <player2 command line> [-s -l <File output for logs>]", options);
        System.exit(0);
      }
      
      GameRunner runner = new GameRunner();
  
      int playerCount = 0;
      
      for (int i = 1; i <= 4; ++i) {
        if (cmd.hasOption("p" + i)) {
          runner.addAgent(cmd.getOptionValue("p" + i));
          playerCount += 1;
        }
      }
      
      if (cmd.hasOption("s")) {
        runner.start();
      } else {
        Method initialize = GameRunner.class.getDeclaredMethod("initialize", Properties.class);
        initialize.setAccessible(true);
        initialize.invoke(runner, new Properties());
    
        Method run = GameRunner.class.getDeclaredMethod("run");
        run.setAccessible(true);
        run.invoke(runner);
        
        if (cmd.hasOption("l")) {
          Method getJSONResult = GameRunner.class.getDeclaredMethod("getJSONResult");
          getJSONResult.setAccessible(true);
          
          Files.asCharSink(Paths.get(cmd.getOptionValue("l")).toFile(), Charset.defaultCharset()).write((String) getJSONResult.invoke(runner));
        }
        
        Field field = GameRunner.class.getDeclaredField("gameResult");
        field.setAccessible(true);
        GameResult result = (GameResult) field.get(runner);
        
        for (int i = 0; i < playerCount; ++i) {
          System.out.println(result.scores.get(i));
        }
        
        System.out.println(String.join("\n", result.uinput));
      }
    } catch (Exception e) {
      System.err.println(e);
      e.printStackTrace(System.err);
      System.exit(1);
    }
  }

}
