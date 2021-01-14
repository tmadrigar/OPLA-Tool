package br.otimizes.oplatool.core.jmetal4.operators.mutation;

import br.otimizes.oplatool.architecture.representation.*;
import br.otimizes.oplatool.architecture.representation.Class;
import br.otimizes.oplatool.architecture.representation.Package;
import br.otimizes.oplatool.core.jmetal4.core.Solution;
import br.otimizes.oplatool.common.Configuration;
import br.otimizes.oplatool.common.exceptions.JMException;
import br.otimizes.oplatool.core.jmetal4.operators.MutationOperators;
import br.otimizes.oplatool.core.jmetal4.util.PseudoRandom;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

//import java.lang.Class;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PLA feature mutation operator that call another mutation operators included in FeatureMutationOperators enum
 */
public class PLAMutationOperator extends Mutation {

    private static final long serialVersionUID = 9039316729379302747L;
    static Logger LOGGER = LogManager.getLogger(PLAMutationOperator.class.getName());
    private int threshold;
    private int thresholdLc;

    private Double probability = null;
    private List<String> operators;

    public PLAMutationOperator(HashMap<String, Object> parameters, List<String> operators) {
        super(parameters);
        this.operators = operators;

        if (parameters.get("probability") != null) {
            probability = (Double) parameters.get("probability");
        }
    }

    public PLAMutationOperator(Map<String, Object> parameters) {
        super(parameters);
        if (parameters.get("probability") != null) {
            probability = (Double) parameters.get("probability");
        }
    }

    public void doMutation(double probability, Solution solution) throws Exception {
        String scope = "sameComponent"; //allLevels

        if (super.isASP) {
            // Madrigar

            ArrayList<Class> listClassLC = detectLC(solution);
            if(listClassLC.size() > 0){
                System.out.println("Lista Large Class:" + listClassLC.toString());
                System.out.println("Tamanho da Lista Large Class:" + listClassLC.size());

                if (PseudoRandom.randDouble() < 0.5) {
                    System.out.println("Existe LC -> Aplicar Feature-Driven-for-Class");
                    //MutationOperators selectedOperator = MutationOperators.FEATURE_DRIVEN_OPERATOR;
                    int position = PseudoRandom.randInt(0, listClassLC.size() - 1);
                    //featureMutationLargeClass(solution, listClassLC.get(position));
                    System.out.println("Selected Class:" + listClassLC.get(position).getName());
                    MutationOperators selectedOperator = MutationOperators.FEATURE_DRIVEN_OPERATOR_FOR_CLASS;
                    ((FeatureDrivenOperatorForClass)selectedOperator.getOperator()).executeForClass(parameters_, solution, scope, listClassLC.get(position).getId());
                    // se tiver anomalia, executa o operador especifico e sai
                    return;
                }
            }

            System.out.println("Verificar CO");
            // verificar se tem anomalia, se tiver, resolver e dar return para sair da função
            if(detectCO(solution)) {
                // existe CO, usa uma probabilidade para tentar resolver a anomalia
                System.out.println("Existe CO");
                if (PseudoRandom.randDouble() < 0.5) {
                    System.out.println("Existe CO -> Aplicar Feature-Driven");
                    // usa o normal ? ou para classes que nao entendi
                    //MutationOperators selectedOperator = MutationOperators.FEATURE_DRIVEN_OPERATOR;
                    MutationOperators selectedOperator = MutationOperators.FEATURE_DRIVEN_OPERATOR_FOR_CLASS;
                    selectedOperator.getOperator().execute(parameters_, solution, scope);
                    // se tiver anomalia, executa o operador especifico e sai
                    return;
                }
            }

            // se não tiver anomalia, seguir curso normal;

        }
        System.out.println("Curso normal");
        // curso normal
        int r = PseudoRandom.randInt(0, this.operators.size() - 1);
        HashMap<Integer, String> operatorMap = new HashMap<>();
        for (int i = 0; i < this.operators.size(); i++)
            operatorMap.put(i, this.operators.get(i));
        MutationOperators selectedOperator = MutationOperators.valueOf(operatorMap.get(r));
        selectedOperator.getOperator().execute(parameters_, solution, scope);

    }


    public Object execute(Object object) throws Exception {
        Solution solution = (Solution) object;
        Double probability = (Double) getParameter("probability");

        if (probability == null) {
            Configuration.logger_.severe("FeatureMutation.execute: probability not specified");
            java.lang.Class<String> cls = java.lang.String.class;
            String name = cls.getName();
            throw new JMException("Exception in " + name + ".execute()");
        }
        this.doMutation(this.probability, solution);

        if (!MutationUtils.isValidSolution(((Architecture) solution.getDecisionVariables()[0]))) {
            Architecture clone;
            clone = ((Architecture) solution.getDecisionVariables()[0]).deepClone();
            solution.getDecisionVariables()[0] = clone;
        }

        return solution;
    }

    public List<String> getOperators() {
        return operators;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public int getThresholdLc() {
        return thresholdLc;
    }

    public void setThresholdLc(int thresholdLc) {
        this.thresholdLc = thresholdLc;
    }

    public ArrayList<Class> detectLC(Solution solution) throws JMException { //, int THzb) throws JMException {


        ArrayList<Class> listclass = new ArrayList<>(); // lista de classes que excede o theashold

        final Architecture arch = ((Architecture) solution.getDecisionVariables()[0]);


        // para cada classe contAtribMeth da arquitetura, contar a quantidade de metodos e atributos
        for(Class contAtribMeth : arch.getAllClasses()){

            // se ultrapassa o threashold_lc, adiciona a classe na lista
            if((contAtribMeth.getAllMethods().size()+ contAtribMeth.getAllAttributes().size()) > this.thresholdLc) {
                listclass.add(contAtribMeth);

            }
        }


        return listclass;

    }

    //verificar a existencia de CO
    // --------------------------------------------------------------------------
    public boolean detectCO(Solution solution) throws JMException { //, int THzb) throws JMException {


        System.out.println(("a"+threshold));


        final Architecture arch = ((Architecture) solution.getDecisionVariables()[0]);


        // Lista de todos os pacotes
        final List<Package> allPackage = new ArrayList<Package>(arch.getAllPackages());
        if (!allPackage.isEmpty()) {

            for (Package selectedPackage : allPackage) { // para cada pacote da solução


                // verificar todas as classes do pacote
                List<Class> lstClass = new ArrayList<>(selectedPackage.getAllClasses());

                for (Class selectedClass:lstClass) { // para cada classe existente
                    // para cada classe existente, verificar quantas caracteristicas a classe tem
                    List<Concern> lstConcern = new ArrayList<>(selectedClass.getOwnConcerns());

                    //System.out.println(lstConcern.size());

                    if(lstConcern.size() > threshold){// THzb){ // se tiver mais do que THzb, então existe o concern overload
                        return  true;
                    }
                }

                // verificar todas as interfaces do pacote
                List<Interface> lstInterface = new ArrayList<>(selectedPackage.getAllInterfaces());

                for (Interface selectedInterface:lstInterface) {
                    // para cada interface existente, verificar quantas caracteristicas a interface tem
                    // verificar se <<interface>> está nesta lista. se estiver, lstConc.size() - 1, pois <<interface>> não é uma caracteristica, é só um label
                    List<Concern> lstConcern = new ArrayList<>(selectedInterface.getOwnConcerns());

                    if(lstConcern.size() > threshold){// THzb){// se tiver mais do que THzb, então existe o concern overload
                        return  true;
                    }
                }
            }


        }


        return false;

    }


    //Calculat THzb
    // --------------------------------------------------------------------------
    public int calcularTHzb(Solution solution) throws JMException {


        // quantidade de caracteristicas de cada classe e interface da solução
        ArrayList<Integer> lstConcernCount = new ArrayList<>();


        final Architecture arch = ((Architecture) solution.getDecisionVariables()[0]); // solução original


        // Lista de todos os pacotes
        final List<Package> allPackage = new ArrayList<Package>(arch.getAllPackages());
        if (!allPackage.isEmpty()) {

            for (Package selectedPackage : allPackage) { // para cada pacote da solução


                // verificar todas as classes do pacote
                List<Class> lstClass = new ArrayList<>(selectedPackage.getAllClasses());

                for (Class selectedClass:lstClass) { // para cada classe

                    // conta a quantidade de caracteristicas da classe e salva em lista
                    lstConcernCount.add(selectedClass.getAllConcerns().size());

                }

                // verificar todas as interfaces do pacote
                List<Interface> lstInterface = new ArrayList<>(selectedPackage.getAllInterfaces());

                for (Interface selectedInterface:lstInterface) {


                    // conta a quantidade de caracteristicas da interface e salva em lista
                    lstConcernCount.add(selectedInterface.getAllConcerns().size());
                    // OBS.: verificar se <<interface está na lista antes de adicionar>> se estiver, tem que fazer size() -1

                    /*
                    List<Concern> lstConcern = new ArrayList<>(selectedInterface.getAllConcerns());
                    for(Concern concern:lstConcern){
                        System.out.println(concern.getName());
                    }
                    */
                }
            }


        }

        Double meanBrickConcerns = 0.0;
        for(Integer n:lstConcernCount){
            meanBrickConcerns += n;
        }
        meanBrickConcerns = meanBrickConcerns/lstConcernCount.size();


        System.out.println(("desvio padrão"));
        // calculo do desvio padrao
        Double stdDevOfBrickConcerns = getDesvioPadrao(lstConcernCount);

        System.out.println(("soma"));
        // media + desvio padrão
        Double THzb = meanBrickConcerns + stdDevOfBrickConcerns;
        // arredondar THzb para cima e retornar

        return (int) Math.ceil(THzb);


    }

    public strictfp Double getMedia(ArrayList<Integer> valor) {
        try {
            return getSoma(valor) / valor.size();
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("The list has null values");
        }
    }

    public strictfp Double getSoma(List<Integer> valor) {
        Double soma = 0D;
        for (int i = 0; i < valor.size(); i++) {
            soma += valor.get(i);
        }
        return soma;
    }

    public strictfp Double getDesvioPadrao(ArrayList<Integer> valor) {
        Double media = getMedia(valor);
        int tam = valor.size();
        Double desvPadrao = 0D;
        for (Integer vlr : valor) {
            Double aux = vlr - media;
            desvPadrao += aux * aux;
        }
        return Math.sqrt(desvPadrao / (tam - 1));
    }


}