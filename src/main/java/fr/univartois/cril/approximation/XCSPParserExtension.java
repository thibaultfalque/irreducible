/**
 * 
 */
package fr.univartois.cril.approximation;

import java.util.HashMap;

import org.chocosolver.parser.xcsp.XCSPParser;
import org.chocosolver.solver.variables.IntVar;
import org.xcsp.parser.entries.XVariables.XVar;

/**
 * 
 */
public class XCSPParserExtension extends XCSPParser {
	private XCSPParser decoree;
	
	public XCSPParserExtension(XCSPParser p) {
		decoree=p;
	}
	
	public HashMap<XVar, IntVar> getVarsOfProblem(){
		try {
			var f = XCSPParser.class.getDeclaredField("mvars");
			f.setAccessible(true);
			return (HashMap<XVar, IntVar>) f.get(decoree);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
}
