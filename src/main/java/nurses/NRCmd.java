/**
 * This file is part of nurse-rostering-solver, https://github.com/MatthiasPercelay/Model-PPC
 *
 * Copyright (c) 2020, Université Nice Sophia Antipolis. All rights reserved.
 *
 * Licensed under the BSD 3-clause license.
 * See LICENSE file in the project root for full license information.
 */
package nurses;

import java.io.File;
import java.util.Arrays;
import java.util.Locale;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import ilog.concert.IloException;
import ilog.opl.IloCplex;
import ilog.opl.IloOplDataHandler;
import ilog.opl.IloOplDataSource;
import ilog.opl.IloOplErrorHandler;
import ilog.opl.IloOplFactory;
import ilog.opl.IloOplModel;
import ilog.opl.IloOplModelDefinition;
import ilog.opl.IloOplModelSource;
import ilog.opl.IloOplRunConfiguration;
import ilog.opl.IloOplSettings;
import nurses.pareto.ParetoArchiveL;
import nurses.pareto.TimetableReports;
import nurses.specs.IParetoArchive;
import nurses.specs.IProblemInstance;
import nurses.specs.IShiftSolver;
import nurses.specs.ITimetableReports;
import nurses.specs.IWorkdaySolver;

public class NRCmd  {

	public final static Logger LOGGER = Logger.getLogger(NRCmd.class.getName());

	static {
		LOGGER.setUseParentHandlers(false);
		final StreamHandler handler = new StreamHandler(
				System.out, 
				new Formatter() {
					@Override
					public String format(LogRecord record) {
						return formatMessage(record)+ "\n";
					}
				});
		handler.setLevel(Level.ALL);	
		LOGGER.addHandler(handler);
		LOGGER.setLevel(Level.INFO);
	}

	private final static void flushlogs() {
		for (Handler handler: LOGGER.getHandlers()) {
			handler.flush();
		}
	}
	private final CmdLineParser parser;

	@Option(name = "-f", aliases = { "-file", "--file" }, usage = "XLS Problem Instance File.", required = true)
	private File instanceFile;

	@Option(name = "-s", aliases = { "-seed", "--seed" }, usage = "Random Seed.")
	private long seed = 0;

	@Option(name = "-v", aliases = { "--verbosity" }, usage = "Verbosity level.")
	private String level = Level.CONFIG.getName();

	@Option(name = "-t", aliases = { "--threads" }, usage = "Number of threads.")
	private int numThreads = 4;

	private long runtime;

	public NRCmd() {
		super();
		parser = new CmdLineParser(this);
	}

	private void readArgs(String... args) throws CmdLineException {
		try {
			parser.parseArgument(args);
		} catch (CmdLineException e) {
			LOGGER.log(Level.SEVERE, "Invalid Command Line Arguments : " + Arrays.toString(args) + "\n\ncmd...[FAIL]", e);
			throw e;
		}
	}

	public void setUp(String... args) throws CmdLineException { 
		readArgs(args);
		LOGGER.setLevel(Level.parse(level));
		if (LOGGER.isLoggable(Level.INFO)) {
			LOGGER.info(String.format(Locale.US, "i %s\nc SEED %d", "TODO", seed));			
		}
	}


	public final static IProblemInstance parseInstance(File instanceFile) {
		return new NRProblemInstance(instanceFile);
	}

	public final static IParetoArchive createArchive() {
		return new ParetoArchiveL();
	}

	public final static IWorkdaySolver createWorkdaySolver(IProblemInstance instance) {
		return new WorkdaySolver();
	}

	public final static IShiftSolver createShiftSolver() {
		return new ShiftSolver();
	}

	private final ITimetableReports createTimetableReports() {
		return new TimetableReports();
	}

	public void execute() {
		IloOplFactory.setDebugMode(true);
		runtime = - System.nanoTime();
		final IProblemInstance instance = parseInstance(instanceFile);
		final IWorkdaySolver workdaySolver = createWorkdaySolver(instance);
		final IParetoArchive workdayArchive = createArchive();
		workdaySolver.solve(instance, workdayArchive);
		final IShiftSolver shiftSolver = createShiftSolver();	
		final IParetoArchive shiftArchive = createArchive();
		shiftSolver.solve(instance, workdayArchive, shiftArchive);	
		final ITimetableReports reporter = createTimetableReports();
		reporter.generateReports(instance, shiftArchive);
		runtime += System.nanoTime();

	}

	private final static long NS2MS = 1000000;

	public void tearDown() {
		if (LOGGER.isLoggable(Level.INFO)) {
			LOGGER.info(String.format(Locale.US, "d RUNTIME %d",  runtime / NS2MS)); 
			flushlogs();
		}
	}

	public static void main(String[] args) {
		final NRCmd par = new NRCmd();
		try {
			par.setUp(args);
			par.execute();
			par.tearDown();
		} catch (CmdLineException e) {
			par.parser.printUsage(System.out);
		}

	}
}
