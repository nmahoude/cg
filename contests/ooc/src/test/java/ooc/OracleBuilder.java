package ooc;

public class OracleBuilder {
	Oracle oracle;
	
	private OracleBuilder(OOCMap map) {
		oracle=  new Oracle(map);
	}
	public static OracleBuilder fromMap(OOCMap map) {
		return new OracleBuilder(map);
	}
	
}
