/*
 * File: SaveGameReader.java
 * Name: Erick Blankenberg
 * Section Leader: Aleksander Dash
 * ----------------------
 * This class acts as a database reading from the save file. Based off of
 * previous work for the NameSurfer assignment.
 */

import acm.util.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class SaveGameReader implements TorchBearerConstants{
	
	private String file;
	private ArrayList<PlayerMobile> entries = new ArrayList<PlayerMobile>();
		
	/**
	 * Creates a new SaveGameReader and initializes it using the
	 * data in the specified file.  The constructor throws an error
	 * exception if the requested file does not exist or if an error
	 * occurs as the file is being read.
	 */
	public SaveGameReader(String newFile) {
		file = newFile;
		try {
			//A new NameSurferEntry is created for every line.
			BufferedReader lineReader = new BufferedReader(new FileReader(file));
			while(true) {
				String currentLine = lineReader.readLine();
				//Stops if the line is blank.
				if((currentLine==null)) break;
				entries.add(new PlayerMobile(currentLine));
			}
			lineReader.close();
		} catch (IOException ex) {
			throw new ErrorException(ex);
		}
		saveFile();
	}

	/** Returns the number of players the save file contains. */
	public int getSize(){
		return entries.size();
	}
	
	/** Returns the player at the specified index. */
	public PlayerMobile getEntry(int index){
		return entries.get(index);
	}
	
	/** Adds the player to the file as the first position. */
	public void newPlayer(PlayerMobile newMobile){
		entries.add(0,newMobile);
		saveFile();
	}
	
	/** Deletes the player of interest. */
	public void deletePlayer(PlayerMobile trashedMobile){
		entries.remove(trashedMobile);
		saveFile();
	}
	
	/** Saves the current loaded data to the file. */
	public void saveFile(){
		try {
			PrintWriter wr = new PrintWriter(new FileWriter(file));
			for(int i=0;i<entries.size();i++){
				wr.println(entries.get(i).toString());
			}
			wr.close();
		}catch (IOException ex) {
			throw new ErrorException(ex);
		}
	}
}

