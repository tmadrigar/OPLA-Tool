//  FitnessComparator.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package br.otimizes.oplatool.core.jmetal4.util.comparators;

import br.otimizes.oplatool.core.jmetal4.core.Solution;

import java.util.Comparator;

/**
 * This class implements a <code>Comparator</code> (a method for comparing
 * <code>Solution</code> objects) based on the fitness value returned by the
 * method <code>getFitness</code>.
 */
public class FitnessComparator implements Comparator {

    /**
     * Compares two solutions.
     *
     * @param o1 Object representing the first <code>Solution</code>.
     * @param o2 Object representing the second <code>Solution</code>.
     * @return -1, or 0, or 1 if o1 is less than, equal, or greater than o2,
     * respectively.
     */
    public int compare(Object o1, Object o2) {
        if (o1 == null)
            return 1;
        else if (o2 == null)
            return -1;

        double fitness1 = ((Solution) o1).getFitness();
        double fitness2 = ((Solution) o2).getFitness();
        if (fitness1 < fitness2) {
            return -1;
        }

        if (fitness1 > fitness2) {
            return 1;
        }

        return 0;
    } // compare
} // FitnessComparator
