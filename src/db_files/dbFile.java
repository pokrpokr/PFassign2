package db_files;
import java.io.*;
import java.nio.charset.*;
import java.util.ArrayList;

public abstract class dbFile<T> {
	private File dbFile;
	
	public dbFile() {
		File dbFile = new File("src/files/"+this.getClass().getSimpleName()+".csv");
		if (!dbFile.exists()) {
			try {
				dbFile.createNewFile();
			} catch (IOException e) {
				System.err.println("Create File faild! "+ e);
			}
		}
		this.dbFile = dbFile;
	}
	
	protected ArrayList<T> readrData() {
		ArrayList<T> data = T.new();
		try {
			BufferedReader csvReader = new BufferedReader(new FileReader(this.dbFile));
			String row = null;
			while ((row = csvReader.readLine()) != null) {
			    String[] data = row.split(",");
			    // do something with the data
			}
			csvReader.close();
		} 
		catch (IOException e) {
			System.err.println("IO Error " + e);
		}
		catch (Exception e) {
			System.err.println("Error! " + e);
		}
	}
	
}
