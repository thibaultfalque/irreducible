/**
 *
 */

package fr.univartois.cril.approximation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.chocosolver.parser.SetUpException;
import org.chocosolver.solver.constraints.Constraint;

import fr.univartois.cril.approximation.cli.CLI;
import fr.univartois.cril.approximation.solver.ApproximationSolverBuilder;
import fr.univartois.cril.approximation.solver.MyISolverAdapter;
import fr.univartois.cril.approximation.solver.Portfolio;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

/**
 * A factory for creating Portfolio objects.
 */
public class PortfolioFactory {

    /**
     * Prevent instantiation.
     */
    private PortfolioFactory() {
        throw new AssertionError("The class PortfolioFactory should not be instantiated.");
    }

    /**
     * New default portfolio.
     *
     * @param args the args
     *
     * @return the portfolio
     */
    public static Portfolio newDefaultPortfolio(Namespace args) {
        var portfolio = new Portfolio(args.getLong("global_timeout"));
        String filePath = args.getString("portfolio_configuration");
        if (filePath == null) {
            throw new IllegalArgumentException("The portfolio configuration file can't be null");
        }
        try (BufferedReader br = new BufferedReader(
                new FileReader(filePath, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println("c Creating a new solver...");
                System.out.println("c " + line);
                var parser = CLI.createCLIParser(false);
                var arguments = parser.parseArgs(line.trim().replaceAll("\\s+", " ").split(" "));
                System.out.println("c " + arguments);

                List<String> chocoArgs = new ArrayList<>();
                chocoArgs.add(args.<String>get("instance"));
                chocoArgs.addAll(arguments.getList("remaining"));

                System.out.println("c choco args: " + chocoArgs);

                Constraint.currentGroup = 1;
                Constraint.currentBlock = 1;

                var xcsp = new XCSPExtension();
                if (xcsp.setUp(chocoArgs.toArray(new String[chocoArgs.size()]))) {
                    xcsp.createSolver();
                    xcsp.buildModel();
                    xcsp.configureSearch();
                }

                xcsp.removeShutdownHook();
                var model = xcsp.getModel();

                model.getSolver().logWithANSI(!arguments.getBoolean("no_print_color"));
                if (Boolean.TRUE.equals(arguments.getBoolean("approx"))) {
                    var builder = new ApproximationSolverBuilder(model.getSolver())
                            .withSpecificConstraintRemover(
                                    arguments.getString("constraint_remover"))
                            .withSpecificConstraintMeasure(arguments.getString("measure"))
                            .setKeepFalsified(arguments.get("keep_falsified"))
                            .setVerbosity(arguments.getInt("verbosity"));

                    var solver = builder.initState(arguments).build();
                    portfolio.addSolver(solver);
                } else if (Boolean.TRUE.equals(arguments.getBoolean("default"))) {
                    System.out.println("c default solver...");
                    portfolio.addSolver(new MyISolverAdapter(model.getSolver()));
                } else {
                    throw new IllegalArgumentException(
                            "The option --portfolio is not available here.");
                }

            }
        } catch (IOException | ArgumentParserException | SetUpException e) {
            e.printStackTrace();
        }
        return portfolio;
    }

}
