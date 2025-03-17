
package fr.univartois.cril.approximation;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.chocosolver.parser.SetUpException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.univartois.cril.approximation.cli.CLI;
import fr.univartois.cril.approximation.solver.ApproximationSolverBuilder;
import net.sourceforge.argparse4j.inf.ArgumentParserException;

/**
 * The Class SolverTest.
 */
class SolverTest {

    /** The instance path. */
    private String instancePath;

    /**
     * Sets the up.
     *
     * @throws Exception the exception
     */
    @BeforeEach
    void setUp() throws Exception {
        // Récupérer l'URL du fichier de test dans les ressources
        URL resource = getClass().getClassLoader()
                .getResource("AircraftAssemblyLine-3-628-020-1_c24.xml.lzma");
        if (resource == null) {
            throw new IllegalStateException("Test instance file not found in resources");
        }
        instancePath = Paths.get(resource.getPath()).toAbsolutePath().toString();
    }

    /**
     * Test solver configuration and execution.
     *
     * @throws ArgumentParserException the argument parser exception
     * @throws SetUpException the set up exception
     * @throws InterruptedException the interrupted exception
     */
    @Test
    void testSolverConfigurationAndExecution()
            throws ArgumentParserException, SetUpException, InterruptedException {
        // Construire la ligne de commande
        String[] args = {
                "-i", instancePath,
                "--global-timeout", "100s",
                "--path-strategy", "APPROX_NORMAL",
                "--measure", "WdegFiltering",
                "--n-runs-normal", "500",
                "--n-runs-approx", "500",
                "--", "-limit=[100s]", "-f", "-varh", "DOMWDEG", "-valsel", "[MIN,true,32,true]",
                "-restarts", "[luby,500,5000,true]", "-lc", "1"
        };

        var parser = CLI.createCLIParser(true);
        var arguments = parser.parseArgs(args);

        List<String> chocoArgs = new ArrayList<>();
        chocoArgs.add(arguments.<String>get("instance"));
        chocoArgs.addAll(arguments.getList("remaining"));

        var xcsp = new XCSPExtension();
        if (xcsp.setUp(chocoArgs.toArray(new String[0]))) {
            xcsp.createSolver();
            xcsp.buildModel();
            xcsp.configureSearch();
        }
        xcsp.removeShutdownHook();
        var model = xcsp.getModel();

        var builder = new ApproximationSolverBuilder(model.getSolver())
                .withSpecificConstraintRemover(arguments.getString("constraint_remover"))
                .withSpecificConstraintMeasure(arguments.getString("measure"))
                .setKeepFalsified(arguments.get("keep_falsified"))
                .setVerbosity(arguments.getInt("verbosity"))
                .setTimeout(arguments.getString("global_timeout"));

        var solver = builder.initState(arguments).build();
        assertNotNull(solver, "The solver should be successfully configured and built");

        // Exécuter le solveur
        solver.solve();
    }

}
