package org.generationcp.middleware.reports;

import java.util.Arrays;
import java.util.List;

import org.generationcp.middleware.pojos.report.GermplasmEntry;
import org.generationcp.middleware.pojos.report.Occurrence;

public class UtilFiller {

	static{
		occ1 = new Occurrence();
		occ2 = new Occurrence();
		initOccurrences();
	}
	
	private static Occurrence occ1;
	private static Occurrence occ2;
	
	
	private static void initOccurrences(){
		List<Occurrence> occ = Arrays.asList(
				new Occurrence()
		);
		
		List<Occurrence> occs = Arrays.asList(
				new Occurrence(11),
				new Occurrence(22),
				new Occurrence(33),
				new Occurrence(44)
		);
		
		occ1.setOcurrencesList(occ);
		occ2.setOcurrencesList(occs);
		
		List<GermplasmEntry> entries = Arrays.asList(
				new GermplasmEntry(1),
				new GermplasmEntry(2),
				new GermplasmEntry(3),
				new GermplasmEntry(4),
				new GermplasmEntry(5),
				new GermplasmEntry(6),
				new GermplasmEntry(7),
				new GermplasmEntry(8),
				new GermplasmEntry(9),
				new GermplasmEntry(10),
				new GermplasmEntry(11),
				new GermplasmEntry(12),
				new GermplasmEntry(13),
				new GermplasmEntry(14),
				new GermplasmEntry(15),
				new GermplasmEntry(16),
				new GermplasmEntry(17),
				new GermplasmEntry(18),
				new GermplasmEntry(19),
				new GermplasmEntry(20),
				new GermplasmEntry(21),
				new GermplasmEntry(22),
				new GermplasmEntry(23)
		);
		
		for(GermplasmEntry e : entries){
			e.setS_ent(11);
//			e.setEntryNum(22);
			e.setS_tabbr("dummy_t_abbr");
			e.setSlocycle("_sloCycle");
			e.setLinea1("lineaA");
			e.setLinea2("lineaB");
			e.setLinea3("lineaC");
			e.setLinea4("lineaD");
			e.setLinea5("lineaE");
			e.setCode28("123");
			e.setIntrid("999");
			e.setPlot(e.getEntryNum());
			
			e.setF_lid(123);
			e.setF_cycle("F_14A");
			e.setF_tabbr("F_tabbr");
			e.setF_ent(1579);
			e.setM_lid(456);
			e.setM_cycle("M_14B");
			e.setM_tabbr("M_tabbr");
			e.setM_ent(2579);
		}

		for(Occurrence o : occ1.getOcurrencesList()){
			o.setEntriesList2(entries);
		}
		
		for(Occurrence o : occ2.getOcurrencesList()){
			o.setEntriesList2(entries); //report 23
			o.setEntriesList(entries); //report 22
		}
		
		occ1.setOrganization("my organization");
		occ1.setProgram("some program");
		occ1.setOffset(2000);
		
		
	}
	
	public static Occurrence getSingleOccData(){
		return occ1;
	}
	public static Occurrence getMultiOccData(){
		return occ2;
	}
	
}
