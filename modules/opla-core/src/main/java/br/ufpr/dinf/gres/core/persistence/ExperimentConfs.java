package br.ufpr.dinf.gres.core.persistence;

import br.ufpr.dinf.gres.core.jmetal4.experiments.ExperimentCommonConfigs;
import br.ufpr.dinf.gres.core.jmetal4.experiments.FeatureMutationOperators;
import br.ufpr.dinf.gres.core.jmetal4.experiments.MutationOperators;
import br.ufpr.dinf.gres.core.jmetal4.experiments.base.NSGAIIConfigs;
import br.ufpr.dinf.gres.core.jmetal4.experiments.base.PaesConfigs;

/**
 * Classe responsável por guardar E persistir/recuperar informações referentes a
 * qual configuração o experimento utilizou.
 * <p>
 * Ex: numero de rodas, funcoes objetivos utilizadas e assim por diante.
 *
 * @author elf
 */
public class ExperimentConfs {

    private String experimentId;
    private ExperimentCommonConfigs configs;
    private String algorithm;

    public ExperimentConfs(String experimentId, String algorithm, ExperimentCommonConfigs configs) {
        this.experimentId = experimentId;
        this.configs = configs;
        this.algorithm = algorithm;
    }

    private static String getString(String str) {
        return (str == null || str.isEmpty()) ? "-" : str;
    }

    private static String getMutationsOperators(String mutationOperators) {
        if (mutationOperators == null || mutationOperators.isEmpty())
            return "";

        return mutationOperators;
    }

    public static String getProbability(String probability) {
        return Double.valueOf(probability) == 0 ? "-" : probability;
    }

    public String getPatterns() {
        StringBuilder patternsList = new StringBuilder();

        if (configs.getMutationOperators().contains(FeatureMutationOperators.DESIGN_PATTERNS.toString())) {
            for (String p : configs.getPatterns()) {
                patternsList.append(p);
                patternsList.append(",");
            }
        }

        if (patternsList.length() > 0)
            patternsList = removeLastComma(patternsList);

        return patternsList.toString();
    }

    public String getMutationOperators() {
        StringBuilder mutationOperatorsList = new StringBuilder();
        for (String operator : configs.getMutationOperators()) {
            mutationOperatorsList.append(operator);
            mutationOperatorsList.append(",");
        }
        if (mutationOperatorsList.length() > 0)
            mutationOperatorsList = removeLastComma(mutationOperatorsList);
        return mutationOperatorsList.toString();
    }

    public static String getInt(String archiveSize) {
        return Integer.valueOf(archiveSize) == 0 ? "-" : archiveSize;
    }


    public int getPopulationSize() {
        if (this.algorithm.equalsIgnoreCase("NSGAII"))
            return ((NSGAIIConfigs) this.configs).getPopulationSize();

        return 0;
    }

    public String getObjectives() {
        return "'" + this.configs.getObjectiveFuncions() + "'";
    }

    /**
     * For PAES
     *
     * @return
     */
    public int getArchiveSize() {
        if (this.algorithm.equalsIgnoreCase("paes"))
            return ((PaesConfigs) this.configs).getArchiveSize();

        return 0;
    }

    public StringBuilder removeLastComma(StringBuilder list) {
        list.delete(list.length() - 1, list.length());
        return list;
    }

    public String getDesignPatternStrategy() {
        if (configs.getMutationOperators().contains(FeatureMutationOperators.DESIGN_PATTERNS.toString())) {
            if (configs.getDesignPatternStrategy() == null)
                return "Random";
            if (configs.getDesignPatternStrategy() != null)
                return "Elements With Same Design Pattern or None";
        }
        return "";
    }

    public String getExperimentId() {
        return experimentId;
    }

    public void setExperimentId(String experimentId) {
        this.experimentId = experimentId;
    }

    public ExperimentCommonConfigs getConfigs() {
        return configs;
    }

    public void setConfigs(ExperimentCommonConfigs configs) {
        this.configs = configs;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }
}
