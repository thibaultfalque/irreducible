/**
 * 
 */
package fr.univartois.cril.approximation;

import org.chocosolver.parser.xcsp.XCSP;

/**
 * 
 */
class XCSPExtension extends XCSP {
	public void removeShutdownHook() {
		Runtime.getRuntime().removeShutdownHook(statOnKill);
	}
}
