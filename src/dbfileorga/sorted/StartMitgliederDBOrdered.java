package dbfileorga.sorted;

import dbfileorga.Record;

public class StartMitgliederDBOrdered {

	public static void main(String[] args) {
		MitgliederDB db = new MitgliederDB();
		System.out.println(db);

		// read the a record number e.g. 32 (119;2;44;Albers;Hans;07.10.75;01.05.89;90;25)
		Record rec = db.read(32);
		System.out.println("I found the following record for 32: "+rec);

		//find and read a record with a given Mitgliedesnummer e.g 95 / without binary search
		rec = db.read(db.findPos("95"));
		System.out.println("I found the following record for Mitgliedsnummer 95: "+rec);

		//modify (ID95 Steffi Brahms wird zu ID 95 Steffi Bach)
		db.modify(db.findPos("95"), new Record("95;3;13;Bach;Steffi;04.04.06;01.02.16;;5"));
		System.out.println("I modified the record "+db.read(db.findPos("95")));

		//insert Hans Meier
		int newRecNum = db.insert(new Record("122;2;44;Meier;Hans;07.05.01;01.03.10;120;15"));
		System.out.println("I added "+db.read(newRecNum));

		//delete the record with Mitgliedsnummer 121 (121;4;;Meller;Peter;24.08.64;01.03.04;230;25)
		db.delete(db.findPos("121"));
		System.out.println("I deleted the record:");
		System.out.println(db);
	}
}
