/**
 * This file is part of nurse-rostering-solver, https://github.com/MatthiasPercelay/Model-PPC
 *
 * Copyright (c) 2020, Université Nice Sophia Antipolis. All rights reserved.
 *
 * Licensed under the BSD 3-clause license.
 * See LICENSE file in the project root for full license information.
 */
package nurses;

public class NRExtargs {
    public int n;
    public int c;
    public int useRelaxation;
    public int OBJECTIVE_WORKDAY_USE_BALANCE;
    public int OBJECTIVE_SHIFT;
    public int OBJECTIVE_SHIFT_USE_AVERAGE;

    NRExtargs(int n, int c, int r, int wdayBalance, int shiftObj, int shiftBalance){
        this.n = n;
        this.c = c;
        useRelaxation = r;
        OBJECTIVE_WORKDAY_USE_BALANCE = wdayBalance;
        OBJECTIVE_SHIFT = shiftObj;
        OBJECTIVE_SHIFT_USE_AVERAGE = shiftBalance;
    }
}