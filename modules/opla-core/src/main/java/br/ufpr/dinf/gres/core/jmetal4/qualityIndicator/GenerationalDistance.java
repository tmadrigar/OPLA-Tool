//  GenerationalDistance.java
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

package br.ufpr.dinf.gres.core.jmetal4.qualityIndicator;

import br.ufpr.dinf.gres.core.jmetal4.qualityIndicator.util.MetricsUtil;

/**
 * This class implements the generational distance indicator. It can be used also
 * as a command line by typing:
 * "java br.ufpr.dinf.gres.core.jmetal4.qualityIndicator.GenerationalDistance <solutionFrontFile>
 * <trueFrontFile> <numberOfObjectives>"
 * Reference: Van Veldhuizen, D.A., Lamont, G.B.: Multiobjective Evolutionary
 * Algorithm Research: A History and Analysis.
 * Technical Report TR-98-03, Dept. Elec. Comput. Eng., Air Force
 * Inst. Technol. (1998)
 */
public class GenerationalDistance {
    static final double pow_ = 2.0;          //pow. This is the pow used for the
    public MetricsUtil utils_;  //utils_ is used to access to the

    /**
     * Constructor.
     * Creates a new instance of the generational distance metric.
     */
    public GenerationalDistance() {
        utils_ = new br.ufpr.dinf.gres.core.jmetal4.qualityIndicator.util.MetricsUtil();
    } // GenerationalDistance

    /**
     * Returns the generational distance value for a given front
     *
     * @param front           The front
     * @param trueParetoFront The true pareto front
     */
    public double generationalDistance(double[][] front,
                                       double[][] trueParetoFront,
                                       int numberOfObjectives) {

        /**
         * Stores the maximum values of true pareto front.
         */
        double[] maximumValue;

        /**
         * Stores the minimum values of the true pareto front.
         */
        double[] minimumValue;

        /**
         * Stores the normalized front.
         */
        double[][] normalizedFront;

        /**
         * Stores the normalized true Pareto front.
         */
        double[][] normalizedParetoFront;

        // STEP 1. Obtain the maximum and minimum values of the Pareto front
        maximumValue = utils_.getMaximumValues(trueParetoFront, numberOfObjectives);
        minimumValue = utils_.getMinimumValues(trueParetoFront, numberOfObjectives);
        // STEP 2. Get the normalized front and true Pareto fronts
        normalizedFront = utils_.getNormalizedFront(front,
                maximumValue,
                minimumValue);
        normalizedParetoFront = utils_.getNormalizedFront(trueParetoFront,
                maximumValue,
                minimumValue);
        // STEP 3. Sum the distances between each point of the front and the
        // nearest point in the true Pareto front
        double sum = 0.0;
        for (int i = 0; i < front.length; i++)
            sum += Math.pow(utils_.distanceToClosedPoint(normalizedFront[i],
                    normalizedParetoFront),
                    pow_);
        // STEP 4. Obtain the sqrt of the sum
        sum = Math.pow(sum, 1.0 / pow_);
        // STEP 5. Divide the sum by the maximum number of points of the front
        double generationalDistance = sum / normalizedFront.length;

        return generationalDistance;
    } // generationalDistance

} // GenerationalDistance
