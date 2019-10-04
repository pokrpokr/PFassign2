package db_files;
import java.io.*;
import java.nio.charset.*;
import java.util.ArrayList;

public abstract class dbFile {
	public dbFile() {
		File dbFile = new File("src/files/"+this.getClass().getSimpleName()+".csv");
	}
	
}
