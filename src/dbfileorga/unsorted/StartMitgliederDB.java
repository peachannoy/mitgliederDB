package dbfileorga.unsorted;

import dbfileorga.Record;

public class StartMitgliederDB {

	public static void main(String[] args) {
			MitgliederDB db = new MitgliederDB();
			Record rec;
			System.out.println("Ausgabe der Anfangsdatenbank-----------------------------------------");
			System.out.println(db);

			System.out.println("Mitgliedsnummer 71, Eintrag löschen----------------------------");
			db.delete(db.findPos(121));
		System.out.println(db);

/*
			//read the a record number e.g. 31 (86;3;13;Brutt;Jasmin;12.12.04;01.01.16;;7,5)
			rec = db.read(31);
			System.out.println("Suche nach Record 31---------------------------------------------");
			System.out.println(rec);

			//find and read a record with a given Mitgliedesnummer e.g 95
			//works with String or int which is only for the Mitgliedsnummer
			System.out.println("Suche nach Mitgliedsnummer 95--------------------------------------------");
			rec = db.read(db.findPos(95));
			System.out.println(rec);
			System.out.println("Suche nach Kappes ------------------------------------------------------------");
			rec=db.read(db.findPos("Kappes"));
			System.out.println(rec);

			//insert Hans Meier
			System.out.println("Hans Meier einfügen----------------------------------------------------");
			int newRecNum = db.insert(new Record("122;2;44;Meier;Hans;07.05.01;01.03.10;120;15"));
			System.out.println(db.read(newRecNum));

			//modify (ID95 Steffi Brahms wird zu ID95 Steffi Bach)
			System.out.println("Steffi Brahms zu Steffi Bach------------------------------------------");
			db.modify(db.findPos(95), new Record("95;3;13;Bach;Steffi;04.04.06;01.02.16;;5"));
			System.out.println(db);

			//delete the record with Mitgliedsnummer 71
			System.out.println("Mitgliedsnummer 71, Eintrag löschen----------------------------");
			db.delete(db.findPos(71));
			System.out.println(db);

 */
	}

}
