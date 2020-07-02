/**
 * This file is part of nurse-rostering-solver, https://github.com/MatthiasPercelay/Model-PPC
 *
 * Copyright (c) 2020, Université Nice Sophia Antipolis. All rights reserved.
 *
 * Licensed under the BSD 3-clause license.
 * See LICENSE file in the project root for full license information.
 */
package nurses;

import ilog.opl.IloOplErrorHandler;
import ilog.opl.IloOplFactory;
import ilog.opl.IloOplModelDefinition;
import ilog.opl.IloOplModelSource;
import ilog.opl.IloOplSettings;

public class NRSolver {

	IloOplFactory oplF;
	IloOplErrorHandler errHandler;
	IloOplModelSource modelSource;
	IloOplSettings settings;
	IloOplModelDefinition def;
	
	public NRSolver() {}

	protected void setUp(String oplModelFile) {
		oplF = new IloOplFactory();
		errHandler = oplF.createOplErrorHandler(System.out);
		modelSource = oplF.createOplModelSource(oplModelFile);
		settings = oplF.createOplSettings(errHandler);
		def=oplF.createOplModelDefinition(modelSource,settings);

	}
	
	protected void tearDown() {
		// Do not change the instruction order !
		modelSource.end();
		settings.end();
		def.end();
		errHandler.end();
		oplF.end();
	}
}