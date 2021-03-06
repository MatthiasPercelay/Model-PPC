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
import nurses.pareto.TimetableReports;
import nurses.NRExtargs;
import nurses.pareto.MOSolution;

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

	@Option(name = "-n", aliases = {"-number", "--number"}, usage = "Number of agents.")
	private int n = 6;

	@Option(name = "-c", aliases = {"-cycle", "--cycle"}, usage = "Number of cycles.")
	private int c = 2;

	@Option(name = "-wr", aliases = {"-wdayRelaxation", "--wdayRelaxation"}, usage = "Workday assignment uses relaxation.")
	private int wr = 0;

	@Option(name = "-sr", aliases = {"-shiftRelaxation", "--shiftRelaxation"}, usage = "Shift assignment uses relaxation.")
	private int sr = 0;
	
	@Option(name = "-wdayBalance", aliases = {"--wdayBalance"}, usage = "Workday assignment uses balance.")
	private int wdayBalance = 0;

	@Option(name = "-shiftObj", aliases = {"--shiftObj"}, usage = "Shift assignment customized objective.")
	private int shiftObj = 0;

	@Option(name = "-shiftBalance", aliases = {"--shiftBalance"}, usage = "Shift assignment uses balance.")
	private int shiftBalance = 0;

	@Option(name = "-s", aliases = { "-seed", "--seed" }, usage = "Random Seed.")
	private long seed = 0;

	@Option(name = "-v", aliases = { "--verbosity" }, usage = "Verbosity level.")
	private String level = Level.CONFIG.getName();

	@Option(name = "-t", aliases = { "--threads" }, usage = "Number of threads.")
	private int numThreads = 4;

	private long runtime;

	private NRExtargs extArgs;

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
		extArgs = new NRExtargs(n, c, wr, sr, wdayBalance, shiftObj, shiftBalance);
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

	public final static ParetoArchiveL createArchive() {
		return new ParetoArchiveL();
	}

	public final static IWorkdaySolver createWorkdaySolver(IProblemInstance instance) {
		return new WorkdaySolver();
	}

	public final static IShiftSolver createShiftSolver() {
		return new ShiftSolver();
	}

	private final TimetableReports createTimetableReports() {
		return new TimetableReports();
	}

	public void execute() {
		IloOplFactory.setDebugMode(true);
		runtime = - System.nanoTime();
		final IProblemInstance instance = parseInstance(instanceFile);
		instance.setExtArgs(extArgs);
		final IWorkdaySolver workdaySolver = createWorkdaySolver(instance);
		final ParetoArchiveL workdayArchive = createArchive();
		long startTime = System.currentTimeMillis();
		workdaySolver.solve(instance, workdayArchive);
		// if workday assignment yields no solution, use relaxation and re-solve
		if (workdayArchive.getSolutions().size() == 0){
			if (extArgs.useRelaxation1 == 0){
				System.out.println("------------------------- Solving workday assignment with relaxation ---------------------");
				instance.relaxWday();
				workdaySolver.solve(instance, workdayArchive);
			}
			else{
				System.out.println("The options yield no solution even with relaxation. Please consider to change options.");
			}
		}
		long endTime = System.currentTimeMillis();

		final IShiftSolver shiftSolver = createShiftSolver();	
		final ParetoArchiveL shiftArchive = createArchive();
		long startTime1 = System.currentTimeMillis();
		shiftSolver.solve(instance, extArgs, workdayArchive, shiftArchive);	

		// if shift assignment yields no solution to the workday assignment results, relax shift assignment and re-solve
		if (shiftArchive.getSolutions().size() == 0){
			if (extArgs.useRelaxation2 == 0){
				System.out.println("------------------------- Solving shift assignment with relaxation ---------------------");
				extArgs.useRelaxation2 = 1;
				shiftSolver.solve(instance, extArgs, workdayArchive, shiftArchive);	
			}
			else{
				System.out.println("The options yield no solution even with relaxation. Please consider to change options.");
			}
		}
		long endTime1 = System.currentTimeMillis();
		System.out.println("Total execution time: " + ((endTime-startTime)+(endTime1-startTime1)) + "ms");

		final TimetableReports reporter = createTimetableReports();
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
