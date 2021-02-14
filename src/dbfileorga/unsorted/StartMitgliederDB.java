package dbfileorga.unsorted;

import dbfileorga.Record;

public class StartMitgliederDB {

	public static void main(String[] args) {
		MitgliederDB db = new MitgliederDB();
		System.out.println(db);

		// read the a record number e.g. 31 (86;3;13;Brutt;Jasmin;12.12.04;01.01.16;;7,5)
		Record rec = db.read(31);
		System.out.println("I found the following record for 31: "+rec);

		//find and read a record with a given Mitgliedesnummer e.g 97
		rec = db.read(db.findPos("97"));
		System.out.println("I found the following record for Mitgliedsnummer 97: "+rec);

		//insert Hans Meier
		int newRecNum = db.insert(new Record("122;2;44;Meier;Hans;07.05.01;01.03.10;120;15"));
		System.out.println("I added "+db.read(newRecNum));

		//modify (ID95 Steffi Brahms wird zu ID 95 Steffi Bach)
		db.modify(db.findPos("95"), new Record("95;3;13;Bach;Steffi;04.04.06;01.02.16;;5"));
		System.out.println("The record has been modified:");
		System.out.println(db);

		//delete the record with Mitgliedsnummer 95
		db.delete(db.findPos("125"));
		System.out.println("I deleted the record:");
		System.out.println(db);

	}

}
