package AmadeyLogicGame.circgen;

import java.util.ArrayList;
import java.util.Collections;

public class Factorizator {

    private String expression;

    //Символы разделители
    private final String splitSymbol = "\\+";
    private final String splitSymbol2 = "\\*";
    private final String interstitialSeparator = "+";
    private final String insideSeparator = "*";

    private String[] initTerms;

    //массив операндов и представление термов в виде биарных строк
    private ArrayList<String> operands = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> occurrences = new ArrayList<>();
    private ArrayList<OperandData> opData = new ArrayList<>();

    public String output = "";

    private String nl = System.getProperty("line.separator");
    private StringBuilder log = new StringBuilder(nl+"<<CircuitSunthesizer>>"+nl);

    public void prepareData(String expression){

        log = new StringBuilder(nl+"<<Factoriser>>"+nl);

        this.expression = expression.replace(" ","");

        operands.clear();
        occurrences.clear();
        opData.clear();

        initTerms = this.expression.split(splitSymbol);

        //Заполняем алфавит и одновременно чистим initTerms от пробелов
        for(int i = 0; i < initTerms.length; i++){
            initTerms[i] = initTerms[i].strip().trim();
            String[] buffer = initTerms[i].split(splitSymbol2);
            for(int j = 0; j < buffer.length; j++){
                String buff = buffer[j].strip().trim();
                if(!operands.contains(buff)) {
                    operands.add(buff);
                    occurrences.add(new ArrayList<>());
                }
            }
        }

        //лексико-графическая сортировка алфавита
        for(int i = operands.size() - 1; i > 0; i--){
            for(int j = 0; j < i; j++) {
                if (operands.get(j).charAt(operands.get(j).length() - 1) > operands.get(j + 1).charAt(operands.get(j + 1).length() - 1)) {
                    String a = operands.get(j);
                    operands.set(j, operands.get(j + 1));
                    operands.set(j + 1, a);
                }
            }
        }

        //Создаем список вхождений каждого операнда в термы
        for(int i = 0; i < initTerms.length; i++){
            String[] oprnds = initTerms[i].split(splitSymbol2);
            for(int j = 0; j < oprnds.length; j++){
                for(int k = 0; k < operands.size(); k++){
                    if(operands.get(k).equals(oprnds[j])){
                        occurrences.get(k).add(i);
                    }
                }
            }
        }

        for(int i = 0; i < operands.size(); i++){
            opData.add(new OperandData(operands.get(i), occurrences.get(i)));
        }

        log.append(expression).append(nl);
        log.append(operands).append(nl);
        log.append(occurrences).append(nl);
        log.append(opData.toString()).append(nl);

        output = factorize(opData);
        output = output.substring(0, output.length()-1);

        log.append("factr ").append(output).append(nl);

    }

    private String factorize(ArrayList<OperandData> opData){

        String tmp = "";
        if(opData.isEmpty()){
            return ")";
        }

        //Сортируем операнды по количеству вхождений, либо лексико-графически если одинаковы
        opData.sort((o1, o2) -> {

            if (o1.occurrences.size() != o2.occurrences.size()) {
                return o2.occurrences.size() - o1.occurrences.size();
            } else {
                if (o1.operand.charAt(o1.operand.length() - 1) > o2.operand.charAt(o2.operand.length() - 1)) {
                    return 1;
                } else {
                    return 0;
                }
            }

        });

        log.append("after sort ").append(opData.toString()).append(nl);

        //По дефолту выносим всегда 1 операнд из opData
        //Но посмотрим, есть ли операнды с одиноковым вхождением
        int maxIndex = opData.get(0).occurrences.size();
        ArrayList<OperandData> alloc = new ArrayList<>();
        for (OperandData op: opData){
            if(op.occurrences.size() == maxIndex && op.occurrences.containsAll(opData.get(0).occurrences))
                alloc.add(op);
        }
        log.append("Allocated terms ").append(alloc.toString()).append(nl);

        //Все что не попало в alloc уходит в невостребованные термы
        ArrayList<OperandData> unclaimed = new ArrayList<>();
        for(OperandData op: opData){
            if(Collections.disjoint(alloc.get(0).occurrences, op.occurrences))
                unclaimed.add(op);
        }
        log.append("Unclaimed terms ").append(unclaimed).append(nl);
        opData.removeAll(unclaimed);

        //Составляем терм текущего уровня
        for(int i = 0; i < alloc.size(); i++){
            if(i != alloc.size()-1) {
                tmp += alloc.get(i).operand + insideSeparator;
            }else{
                tmp += alloc.get(i).operand;
            }
        }
        opData.removeAll(alloc);

        //Обработка кросс-коллизий между opData и unclaimed
        ArrayList<OperandData> opDataBuff = new ArrayList<>(opData);
        ArrayList<OperandData> unclaimedBuff = new ArrayList<>(unclaimed);
        for(int i = 0; i < opDataBuff.size(); i++){
            for (OperandData unclmd: unclaimedBuff){
                if (!Collections.disjoint(opDataBuff.get(i).occurrences, unclmd.occurrences)){
                    OperandData newUnclmd = opDataBuff.get(i).clone();
                    ArrayList<Integer> unclmdOccurences = new ArrayList<>();
                    for(int j = 0; j < opDataBuff.get(i).occurrences.size(); j++){
                        if(unclmd.occurrences.contains(opDataBuff.get(i).occurrences.get(j))){
                            unclmdOccurences.add(opDataBuff.get(i).occurrences.get(j));
                        }
                    }
                    newUnclmd.setOccurrences(unclmdOccurences);
                    if(!unclaimedBuff.contains(newUnclmd)) {
                        unclaimed.add(newUnclmd);
                    }
                }
                opDataBuff.get(i).occurrences.removeAll(unclmd.occurrences);
            }
        }
        log.append("cross collision ").append(unclaimed.toString()).append(nl);

        //Расставляем скобки и знаки
        String newFactr = factorize(opData);
        String unclmd = factorize(unclaimed);
        if(!newFactr.isEmpty()) {
            if(!newFactr.equals(")")){
                tmp += insideSeparator;
                tmp += "(";
                if(newFactr.length() > 3){
                   // tmp += "(";
                }
                tmp += newFactr;
            }
        }

        if(!unclmd.isEmpty()){
            if(!unclmd.equals(")")) {
                tmp += interstitialSeparator;
                tmp += unclmd;
                if(newFactr.length() > 3){
                   // tmp += ")";
                }
            }else {
                tmp += unclmd;
            }
        }

        return tmp;

    }

    public StringBuilder getLog() {
        return  log;
    }

    static class OperandData {

        public String operand;
        public ArrayList<Integer> occurrences;
        private int occurSize;

        public OperandData(String operand, ArrayList<Integer> occurrences){
            this.operand = operand;
            this.occurrences = occurrences;
            occurSize = this.occurrences.size();
        }

        public void setOccurrences(ArrayList<Integer> occurrences){
            this.occurrences = occurrences;
            occurSize = this.occurrences.size();
        }

        public OperandData clone() {
            try {
                return (OperandData) super.clone();
            } catch (CloneNotSupportedException e) {
                return new OperandData(this.operand, this.occurrences);
            }
        }

        public String toString(){
            return operand + "-" + occurrences.toString() + "-" + occurSize;
        }

    }

}
