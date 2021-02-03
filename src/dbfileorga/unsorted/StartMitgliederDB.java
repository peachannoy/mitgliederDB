package dbfileorga.unsorted;

import dbfileorga.Record;

public class StartMitgliederDB {

	public static void main(String[] args) {
			MitgliederDB db = new MitgliederDB(false);
		Record rec;
			/*
			 System.out.println(db); //Works

			// TODO test your implementation with the following use cases

			//read the a record number e.g. 31 (86;3;13;Brutt;Jasmin;12.12.04;01.01.16;;7,5) works
			rec = db.read(31);
			System.out.println(rec);

			//find and read a record with a given Mitgliedesnummer e.g 95  //works for general terms TODO adjust for just Mitgliedsnummer
			rec = db.read(db.findPos("Kappes"));
			System.out.println(rec);

			//insert Hans Meier works!!
			int newRecNum = db.insert(new Record("122;2;44;Meier;Hans;07.05.01;01.03.10;120;15"));
			System.out.println(db.read(newRecNum-1));

			//modify (ID95 Steffi Brahms wird zu ID 95 Steffi Bach)
			db.modify(db.findPos("95"), new Record("95;3;13;Bach;Steffi;04.04.06;01.02.16;;5"));
			System.out.println(db);
				*/
			//delete the record with Mitgliedsnummer 95 
			//db.delete(db.findPos("95")); //TODO with Mitgliedsnummer
		System.out.println(db);
		db.delete(29);
			System.out.println(db);
			

	}

}
