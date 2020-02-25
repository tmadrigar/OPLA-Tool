package jmetal4.metrics;

import jmetal4.results.Execution;
import jmetal4.results.Experiment;

/**
 * @author elf
 */
public class PLAExtensibility extends Metrics {

    private double plaExtensibility;

    public PLAExtensibility(String idSolution, Execution execution, Experiment experiement) {
        super.setExecution(execution);
        super.setExperiement(experiement);
        super.setIdSolution(idSolution);
    }

    public double getPlaExtensibility() {
        return plaExtensibility;
    }

    public void setPlaExtensibility(double plaExtensibility) {
        this.plaExtensibility = plaExtensibility;
    }

    @Override
    public String toString() {
        return "PLAExtensibility [" +
                "plaExtensibility=" + plaExtensibility +
                "]";
    }
}