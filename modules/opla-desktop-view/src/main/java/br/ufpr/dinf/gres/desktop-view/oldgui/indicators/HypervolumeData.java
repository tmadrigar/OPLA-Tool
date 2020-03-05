package br.ufpr.dinf.gres.domain.oldgui.indicators;

import br.ufpr.dinf.gres.core.jmetal4.util.MathUtils;

import java.util.Collections;
import java.util.List;

public class HypervolumeData {

    private String idExperiment;
    private String algorithm;
    private String plaName;
    private List<Double> values;

    HypervolumeData(String idExperiment, List<Double> values, String pla, String algorithm) {
        this.idExperiment = idExperiment;
        this.algorithm = algorithm;
        this.plaName = pla;
        this.values = values;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public String getStDev() {
        return String.format("%.9f", MathUtils.stDev(values));
    }

    public String getMean() {
        return calculeMean(values);
    }

    public String getPlaName() {
        return plaName;
    }

    public List<Double> getValues() {
        return Collections.unmodifiableList(this.values);
    }

    private String calculeMean(List<Double> values) {
        return String.format("%.9f", MathUtils.mean(values));
    }

    public String getIdExperiment() {
        return idExperiment;
    }

    public void setIdExperiment(String idExperiment) {
        this.idExperiment = idExperiment;
    }


}