/**
 * This file is part of nurse-rostering-solver, https://github.com/MatthiasPercelay/Model-PPC
 *
 * Copyright (c) 2020, Université Nice Sophia Antipolis. All rights reserved.
 *
 * Licensed under the BSD 3-clause license.
 * See LICENSE file in the project root for full license information.
 */
package nurses;

public enum Shift {
	
	// Work days
	M("Matin", true, true),
	J("Jour", true, true),
	S("Soir", true, true),
	EX("Externe", true, true),
	FO("Formation", true, true),
	// Breaks
	RH("Repos Hebdomaidaire", false, true),
	RA("Repos Aménagé", false, true),
	RC("Repos Compensateur", false, true),
	RTT("Réduction du Temps de Travail", false, true),
	JF("Jour Férié", false, true),
	CA("Congé Annuel", false, true),
	CM("Congé Maladie", false, true),
	// Partial assignments
	W("Work", true, false),
	B("Break", false, true),
	// Special values
	NA("Not Available", true, false),
	ND("No Decision", false, true);
	
	private final String name;
	
	private final boolean work;
	
	private final boolean instantiated;

	
	private Shift(String name, boolean work, boolean instantiated) {
		this.name = name;
		this.work = work;
		this.instantiated = instantiated;
	}

	public final boolean isWork() {
		return work;
	}
	
	public final boolean isBreak() {
		return !work;
	}


	public final boolean isInstantiated() {
		return instantiated;
	}

	public final String getName() {
		return name;
	}
	
}
