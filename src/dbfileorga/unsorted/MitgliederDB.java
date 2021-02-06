package dbfileorga.unsorted;

import dbfileorga.DBBlock;
import dbfileorga.MitgliederTableAsArray;
import dbfileorga.Record;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class MitgliederDB implements Iterable<Record>
{

	protected DBBlock db[] = new DBBlock[8];


	public MitgliederDB(){
		initDB();
		insertMitgliederIntoDB();
	}

	private void initDB() {
		for (int i = 0; i<db.length; ++i){
			db[i]= new DBBlock();
		}
		
	}
	private void insertMitgliederIntoDB() {
		MitgliederTableAsArray mitglieder = new MitgliederTableAsArray();
		String mitgliederDatasets[];
		mitgliederDatasets = mitglieder.records;
		for (String currRecord : mitgliederDatasets ){
			appendRecord(new Record(currRecord));
		}	
	}

		
	protected int appendRecord(Record record){
		//search for block where the record should be appended
		int currBlock = getBlockNumOfRecord(getNumberOfRecords());
		int result = db[currBlock].insertRecordAtTheEnd(record);
		if (result != -1 ){ //insert was successful
			return result;
		}else if (currBlock < db.length) { // overflow => insert the record into the next block
			return db[currBlock+1].insertRecordAtTheEnd(record);
		}
		return -1;
	}
	

	@Override
	public String toString(){
		String result = new String();
		for (int i = 0; i< db.length ; ++i){
			result += "Block "+i+"\n";
			result += db[i].toString();
			result += "-------------------------------------------------------------------------------------\n";
		}
		return result;
	}
	
	/**
	 * Returns the number of Records in the Database
	 * @return number of records stored in the database
	 */
	public int getNumberOfRecords(){
		int result = 0;
		for (DBBlock currBlock: db){
			result += currBlock.getNumberOfRecords();
		}
		return result;
	}
	
	/**
	 * Returns the block number of the given record number 
	 * @param recNum the record number to search for
	 * @return the block number or -1 if record is not found
	 */
	public int getBlockNumOfRecord(int recNum){
		int recCounter = 0;
		for (int i = 0; i< db.length; ++i){
			if (recNum <= (recCounter+db[i].getNumberOfRecords())){
				return i ;
			}else{
				recCounter += db[i].getNumberOfRecords();
			}
		}
		return -1;
	}
		
	public DBBlock getBlock(int i){
		return db[i];
	}
	
	
	/**
	 * Returns the record matching the record number
	 * @param recNum the term to search for
	 * @return the record matching the search term
	 */
	public Record read(int recNum){
		if (recNum < 0 || recNum > this.getNumberOfRecords()) { //ungültige Eingaben werden nicht angenommen
			return null;
		}
		for (Record record : this) { //geht anhand des Iterators die DB durch
			if (recNum==0) { //recNum bedeutet wie viele Schritte weiter gegangen werden muss
				return record; //falls recNum==0 also keine schritte weiter, wird der aktuelle record ausgegeben
			}
			recNum--; //durch das "einen Schritt weitergehen" verringert sich recNum um eins
		}
		return null;
	}
	/**
	 * Returns the number of the first record that matches the search term
	 * @param searchTerm the term to search for
	 * @return the number of the record in the DB -1 if not found
	 */
	public int findPos(String searchTerm){
		int positionCounter = 0; //aktuelle position wird auf 0 gesetzt
		for (Record record : this) { //DB wird anhand des Iterators durchgegangen
			if (record.toString().contains(searchTerm)) { //beinhaltet der aktuelle record den Suchbegriff
				return positionCounter; //wird die aktuelle position ausgegeben
			}
			positionCounter++; //ansonsten wird ein Schritt weitergegangen
		}
		return -1;
	}
	/**
	 * Returns the number of the first record that matches the search for Mitgliedsnummer
	 * @param searchNumber the term to search for
	 * @return the number of the record in the DB -1 if not found
	 */
	public int findPos(int searchNumber){
		int positionCounter = 0; //aktuelle position wird auf 0 gesetzt

		for (Record record : this) { //DB wird anhand des Iterators durchgegangen
			if (Integer.parseInt(record.getAttribute(1))==searchNumber) { //beinhaltet der aktuelle record die Mitgliedsnummer
				return positionCounter; //wird die aktuelle position ausgegeben
			}
			positionCounter++; //ansonsten wird ein Schritt weitergegangen
		}
		return -1;
	}
	
	/**
	 * Inserts the record into the file and returns the record number
	 * @param record
	 * @return the record number of the inserted record
	 */
	public int insert(Record record){
		appendRecord(record);
		return getNumberOfRecords()-1; //-1 weils bei 0 anfängt
	}
	
	/**
	 * Deletes the record specified 
	 * @param numRecord number of the record to be deleted
	 */
	public void delete(int numRecord){
		int blockNumberWithRecord=0;
		int recCounter = 0; //getBlockNumOfRecord wird nicht verwendet: diese Methode ist so gedacht, dass sie bei Überlauf, also ein 6. Record, noch den alten Block ausgibt. Für diesen Fall nicht geeignet
		for (int i = 0; i< db.length; ++i){ //suche den Block mit dem enthaltenen Record raus
			if (numRecord < (recCounter+db[i].getNumberOfRecords())){
				blockNumberWithRecord=i;
				break;
			}else{
				recCounter += db[i].getNumberOfRecords();
			}
		}
		DBBlock temp=db[blockNumberWithRecord]; //Block wird zwischengespeichert
		Record oldRecord=read(numRecord);
		for(int i=blockNumberWithRecord; i<7 ;i++){
			db[i]=db[i+1]; //Alle Blöcke werden um eins nach vorne verschoben
		}
		db[7]=new DBBlock(); //der letzte Block wäre doppelt muss also überschrieben werden
		for(Record record : temp){
			if(!record.equals(oldRecord)){ //alle Einträge aus dem alten Block werden einzeln wieder eingefügt, so werden Lücken gefüllt
				insert(record); 			//außer der gesuchte Record
			}
		}
	}
	
	/**
	 * Replaces the record at the specified position with the given one.
	 * @param numRecord the position of the old record in the db
	 * @param record the new record
	 * 
	 */
	public void modify(int numRecord, Record record){
		delete(numRecord);
		insert(record);
	}

	
	@Override
	public Iterator<Record> iterator() {
		return new DBIterator();
	}
 
	private class DBIterator implements Iterator<Record> {

		    private int currBlock = 0;
		    private Iterator<Record> currBlockIter= db[currBlock].iterator();
	 
	        public boolean hasNext() {
	            if (currBlockIter.hasNext()){
	                return true;
	            }else if (currBlock < db.length){ //continue search in the next block
	            	return db[currBlock+1].iterator().hasNext();
	            }else{ 
	                return false;
	            }
	        }
	 
	        public Record next() {	        	
	        	if (currBlockIter.hasNext()){
	        		return currBlockIter.next();
	        	}else if (currBlock < db.length){ //continue search in the next block
	        		currBlockIter= db[++currBlock].iterator();
	        		return currBlockIter.next();
	        	}else{
	        		throw new NoSuchElementException();
	        	}
	        }
	 
	        @Override
	        public void remove() {
	        	throw new UnsupportedOperationException();
	        }
	    } 
	 

}
