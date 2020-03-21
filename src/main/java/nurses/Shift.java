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
	M("Matin", true, true,"M"),
	J("Jour", true, true, "J"),
	S("Soir", true, true , "S"),
	EX("Externe", true, true,"EX"),
	FO("Formation", true, true,"FO"),
	// Breaks
	RH("Repos Hebdomaidaire", false, true,"RH"),
	RA("Repos Aménagé", false, true,"RA"),
	RC("Repos Compensateur", false, true,"RC"),
	RTT("Réduction du Temps de Travail", false, true,"RTT"),
	JF("Jour Férié", false, true , "JF"),
	CA("Congé Annuel", false, true, "CA"),
	CM("Congé Maladie", false, true, "CM"),
	// Partial assignments
	W("Work", true, false , "W"),
	B("Break", false, true, "B"),
	// Special values
	NA("Not Available", true, false, "NA"),
	ND("No Decision", false, true , "ND");
	
	private final String name;
	
	private final boolean work;
	
	private final boolean instantiated;

	public String pseudo_data;
	
	private Shift(String name, boolean work, boolean instantiated, String pseudo_data) {
		this.name = name;
		this.work = work;
		this.instantiated = instantiated;
		this.pseudo_data = pseudo_data;
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
