package dbfileorga.sorted;

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
		mitgliederDatasets = mitglieder.recordsOrdered;
		for (String currRecord : mitgliederDatasets ){
			appendRecord(new Record(currRecord));
		}	
	}

		
	protected int appendRecord(Record record){
		//search for block where the record should be appended
		int currBlock = getBlockNumOfRecord(getNumberOfRecords()-1);
		int result = db[currBlock].insertRecordAtTheEnd(record);
		if (result != -1 ){ //insert was successful
			return result;
		}else if (currBlock < db.length-1) { // overflow => insert the record into the next block
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

	/**
	 * Returns the number of the given record number within its block starting from 1
	 * @param numRecord the record number to search for
	 * @return the inner number or -1 if record is not found
	 */
	private int getInnerNumber(int numRecord){
		int currentRecordCount;
		for(int i=0;i<db.length;i++){
			currentRecordCount=db[i].getNumberOfRecords();
			if(numRecord<currentRecordCount){
				return numRecord+1;
			}else{
				numRecord-=currentRecordCount;
			}
		}
		return -1;
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
	public int findPos(String searchNumber){return findPos(Integer.parseInt(searchNumber));}
	
	/**
	 * Inserts the record into the file and returns the record number
	 * @param newRecord
	 * @return the record number of the inserted record
	 */
	public int insert(Record newRecord){
		int[] location=insertBefore(newRecord); //find the location of the new record
		if(location==null){ //If the record would be at the end, no location is returned
			appendRecord(newRecord); //so just append it
			return getNumberOfRecords()-1;
		}
		int blockNumber=location[0];
		int innerBlockNumber=location[1];
		Record overflowRecord=null;
		int length=newRecord.length();
		//compare to left space
		if(db[blockNumber].leftSpace()<=length){ //<=because the RECDEL has to fit in
			int overflowRecordNumber=db[blockNumber].getNumberOfRecords(); //if not enough delete last record
			overflowRecord=db[blockNumber].getRecord(overflowRecordNumber);
			deleteNoDefrag(blockNumber , overflowRecordNumber);
		}
		//insert the actual record
		db[blockNumber].insertPushingBack(newRecord, innerBlockNumber);
		//insert the deleted record
		if(overflowRecord!=null)
			insert(overflowRecord);
		return innerBlockNumber; //because the new record pushes the other records back
	}

	/**
	 * Finds the position to insert the record
	 * @param record the new record
	 * @return the new record would be inserted BEFORE the returned record number
	 */
	public int[] insertBefore(Record record){
		for(int i=0; i<db.length ;i++) { //Search in all blocks
			for (int y = 1; y <= db[i].getNumberOfRecords(); y++) { //Search in all records
				if(record.compareTo(db[i].getRecord(y))<=0 ){
					int[] location=new int[2];
					location[0]=i;
					location[1]=y;
					return location;
				}
			}
		}
		return null;
	}

	/**
	 * Deletes the record specified 
	 * @param numRecord number of the record to be deleted
	 */
	public void delete(int numRecord){
		deleteFromBlock(getBlockNumOfRecord(numRecord+1), getInnerNumber(numRecord));
	}

	/**
	 * Deletes the record specified from specific block
	 * @param numRecord number of the record within the block to be deleted
	 * @param blockNumber the block with the record to be deleted
	 */
	private void deleteFromBlock(int blockNumber, int numRecord){
		if(numRecord!=db[blockNumber].getNumberOfRecords()) { //Record nicht am Ende
			db[blockNumber].pullForward(db[blockNumber].getStartingPosition((numRecord + 1)), db[blockNumber].getStartingPosition(numRecord));
		}else{
			db[blockNumber].deleteLastRecord();
		}
		if(blockNumber<db.length-1) {  //nächsten Blöcke vorziehen
			int result = db[blockNumber].insertRecordAtTheEnd(db[blockNumber + 1].getRecord(1));
			if (result != -1)  //insert was successful
				deleteFromBlock(blockNumber+1,1);
		}
	}

	/**
	 * Deletes the record specified from specific block WITHOUT defragmentation
	 * @param numRecord number of the record within the block to be deleted
	 * @param blockNumber the block with the record to be deleted
	 */
	private void deleteNoDefrag(int blockNumber, int numRecord){
		if(numRecord!=db[blockNumber].getNumberOfRecords()) { //Record nicht am Ende
			db[blockNumber].pullForward(db[blockNumber].getStartingPosition((numRecord + 1)), db[blockNumber].getStartingPosition(numRecord));
		}else{
			db[blockNumber].deleteLastRecord();
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
	            }else if (currBlock < db.length-1){ //continue search in the next block
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
