package br.ufpr.dinf.gres.api.gateway;

import br.ufpr.dinf.gres.api.dto.OptimizationDto;
import br.ufpr.dinf.gres.architecture.io.OPLALogs;
import br.ufpr.dinf.gres.architecture.io.OptimizationInfo;
import br.ufpr.dinf.gres.architecture.io.OptimizationInfoStatus;
import br.ufpr.dinf.gres.architecture.io.ReaderConfig;
import br.ufpr.dinf.gres.architecture.util.UserHome;
import br.ufpr.dinf.gres.core.jmetal4.experiments.ExperimentCommonConfigs;
import br.ufpr.dinf.gres.core.jmetal4.experiments.OPLAConfigs;
import br.ufpr.dinf.gres.core.jmetal4.experiments.base.AlgorithmBase;
import br.ufpr.dinf.gres.core.jmetal4.experiments.base.NSGAIIConfigs;
import br.ufpr.dinf.gres.core.jmetal4.experiments.base.PaesConfigs;
import br.ufpr.dinf.gres.core.jmetal4.operators.FeatureMutationOperators;
import br.ufpr.dinf.gres.domain.OPLAThreadScope;
import br.ufpr.dinf.gres.loglog.LogLogData;
import br.ufpr.dinf.gres.loglog.Logger;
import br.ufpr.dinf.gres.patterns.strategies.scopeselection.impl.ElementsWithSameDesignPatternSelection;

import java.util.List;

public class GatewayUtils {

    public static void log(String error) {
        OPLALogs.lastLogs.get(OPLAThreadScope.mainThreadId.get()).clear();
        OPLALogs.add(new OptimizationInfo(OPLAThreadScope.mainThreadId.get(), error, OptimizationInfoStatus.COMPLETE));
    }

    public static void addListener() {
        Logger.addListener(() -> {
            String s = LogLogData.printLog();
            if (OPLALogs.lastLogs.get(OPLAThreadScope.mainThreadId.get()) != null && OPLALogs.lastLogs.get(OPLAThreadScope.mainThreadId.get()).size() >= 100) {
                OPLALogs.lastLogs.get(OPLAThreadScope.mainThreadId.get()).clear();
            }
            OPLALogs.add(new OptimizationInfo(OPLAThreadScope.mainThreadId.get(), s, OptimizationInfoStatus.RUNNING));
        });
    }


    public static void setOperators(OptimizationDto optimizationDto, ExperimentCommonConfigs configs) {
        if (optimizationDto.getMutation()) {
            List<String> mutationsOperators = optimizationDto.getMutationOperators();
            configs.setMutationOperators(mutationsOperators);
            configs.setMutationProbability(optimizationDto.getMutationProbability());
        }
        if (optimizationDto.getCrossover()) {
            configs.setCrossoverProbability(optimizationDto.getCrossoverProbability());
        } else {
            configs.disableCrossover();
        }
        if (optimizationDto.getMutationOperators().contains(FeatureMutationOperators.DESIGN_PATTERNS.toString())) {
            String[] array = new String[optimizationDto.getPatterns().size()];
            configs.setPatterns(optimizationDto.getPatterns().toArray(array));
            configs.setDesignPatternStrategy(new ElementsWithSameDesignPatternSelection(optimizationDto.getScopeSelection().get()));
        }
    }

    public static void setConfigs(OptimizationDto optimizationDto, ExperimentCommonConfigs configs) {
        configs.setLogger(Logger.getLogger());
        configs.activeLogs();
        configs.setDescription(optimizationDto.getDescription());
        configs.setPlas(optimizationDto.getInputArchitecture());
        configs.setNumberOfRuns(optimizationDto.getNumberRuns());
        configs.setMaxEvaluations(optimizationDto.getMaxEvaluations());
        configs.setArchitectureBuilder(optimizationDto.getArchitectureBuilder());
        GatewayUtils.setOperators(optimizationDto, configs);
        configs.setPathToDb(UserHome.getPathToDb());
        OPLAConfigs oplaConfig = new OPLAConfigs();
        oplaConfig.setSelectedObjectiveFunctions(optimizationDto.getObjectiveFunctions());
        configs.setOplaConfigs(oplaConfig);

        configs.setInteractive(optimizationDto.getInteractive());
        configs.setInteractiveFunction(optimizationDto.getInteractiveFunction());
        configs.setMaxInteractions(optimizationDto.getMaxInteractions());
        configs.setFirstInteraction(optimizationDto.getFirstInteraction());
        configs.setIntervalInteraction(optimizationDto.getIntervalInteraction());
        configs.setClusteringMoment(optimizationDto.getClusteringMoment());
        configs.setClusteringAlgorithm(optimizationDto.getClusteringAlgorithm());
        List<String> operadores = configs.getMutationOperators();
        configs.setMutationOperators(operadores);
    }


}
