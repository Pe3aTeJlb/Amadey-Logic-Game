/*    
Copyright (C) Pplos Studio
    
    This file is a part of Amadey Logic Game, which based on CircuitJS1
    https://github.com/Pe3aTeJlb/Amadey-Logic-Game
    
    CircuitJS1 was originally written by Paul Falstad.
	http://www.falstad.com/

	JavaScript conversion by Iain Sharp.
	http://lushprojects.com/
    
    Avrora Logic Game is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 1, 2 of the License, or
    (at your option) any later version.
    Avrora Logic Game is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
    You should have received a copy of the GNU General Public License 
    along with Avrora Logic Game.  If not, see <http://www.gnu.org/licenses/>.
*/

package AmadeyLogicGame.circgen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/*
Создание столбца для таблицы истинности
в силу того, что Квайн-Мак-Класки был частично взят из интернета, выходной массив должен быть в формате 
			{
				[],
				[],
				[],
				[],
			}
			
			где первый массив, это первая строка таблицы истинности и тд.
			Первая строка нулевая для любой функции дабы избежать досрочного решения уровня
			В данном столбце/массиве должно выдерживаться процентное содержание 1
			Так же здесь формируются названия входных элементов и выходный. Они так же требуются для Квайна
			
*/

public class LogicFunctionGenerator {

    public char[][] VectorFunctions;
    public String[] VarNames,OutNames;
    private final long seed = 1;
    private float maxTruePercent;
    private float minTruePercent;
    private int lastVarIndex = 1;
    char[][] buffVector;

    private final String nl = System.getProperty("line.separator");
    private StringBuilder log = new StringBuilder(nl+"<<LogicFunctionGenerator>>"+nl);

	public boolean callOnce = false;
	private final ArrayList<String> prevFunc = new ArrayList<>();
	private final ArrayList<String> currFunc= new ArrayList<>();
    
    public LogicFunctionGenerator(){
    	maxTruePercent = 0.3f;
    	minTruePercent = 0.2f;
    }

    //генератор логической функции в виде таблицы истинности
    public void GenerateVectorFunction(int varCount, int funcCount, ArrayList<String> sharedVars,
                                       float min, float max, boolean zhegalkin) {

        log = new StringBuilder(nl+"<<LogicFunctionGenerator>>"+nl);

    	maxTruePercent = max;
    	minTruePercent = min;

        int totalVarCount = (int) Math.pow(2,varCount);

        //VectorFunctions = new char[totalVarCount][varCount];
        VectorFunctions = new char[totalVarCount][funcCount];
        //массивы имён входных переменных и выходных функций. нужны для Квайн-Мак-Класки
        VarNames = new String[varCount];
        OutNames = new String[funcCount];
        
        
    	if(sharedVars.size() > 0) {

            log.append("Shared var length").append(sharedVars.size()).append(nl);

    		for(int i = 0; i < sharedVars.size(); i++) {
    			VarNames[i] = sharedVars.get(i);
    			log.append(sharedVars.get(i)).append(nl);
    		}

    		log.append("End of shared vars").append(nl);
    	}

    	//Создаём имена переменных
        for(int i = 0; i < varCount; i++){
        	
        	if(i > sharedVars.size() - 1 || sharedVars.size() == 0) {
	            String varName = "x"+(lastVarIndex);
	            VarNames[i] = varName;
	            lastVarIndex++;
        	}
        	log.append(VarNames[i]).append(nl);
        }

        
        for(int i = 0; i < funcCount; i++) {
            String outName = "z"+i;
            OutNames[i] = outName;
            
            //на нулевой комбинации значение функции всегда 0, иначе тренировка теряет смысол (кроме первой функции в схеме)
            if(callOnce) {
            	VectorFunctions[0][i] = '1';
            	callOnce = false;

            	 log.append("Function №").append(i).append(nl).append("1").append(nl);
            	 
            	 for(int k = 0; k < totalVarCount - 1; k++) {
            		 prevFunc.add("0");
            	 }
            	 
            }else {
            	VectorFunctions[0][i] = '0';
                log.append("Function №").append(i).append(nl).append("0").append(nl);
            }

            if(zhegalkin){
                ZhegalkinGenerator(totalVarCount, i);
            }else {
                Generator(totalVarCount);
            }

            //заполняем выходной масссив
            for(int j = 1; j < totalVarCount; j++) {
                VectorFunctions[j][i] = buffVector[j][0];
                log.append(VectorFunctions[j][i]).append(nl);
            }
            log.append("End of Function").append(nl);
        }

    }

    //Реализация Генератора
    private void Generator (int totalVarCount) {

    	currFunc.clear();
        buffVector = new char[totalVarCount][1];

        int unitCounter = 0;

        for(int j = 1; j < totalVarCount; j++) {
            int random = randomBit(seed);
            if(random == 1){
                unitCounter++;
            }
            buffVector[j][0] = Integer.toString(random).charAt(0);
            currFunc.add(Character.toString(buffVector[j][0]));

        }
        log.append(VectorFunctions[0][0]).append(currFunc.toString()).append(nl);
        //Если функция тривиальна (1 или 0 на всех значениях) или
        // слишком мало комбинаци на которых функция принимает значение истина, то генерируем заново
        if(     unitCounter == 0 || unitCounter == totalVarCount
                || unitCounter > Math.floor(maxTruePercent*totalVarCount)
                || unitCounter < Math.floor(minTruePercent*totalVarCount)
                || currFunc.equals(prevFunc)
        ){
            log.append("regen").append(nl);
            Generator(totalVarCount);
        }else{
        
        	//текущий вектор не должен совпадать с вектором предыдущей функции
        	//Глубокое копирование Текущая функция становится предыдущей
        	prevFunc.clear();
            for(String obj : currFunc) {
                String b = obj;
                prevFunc.add(b);
            }
        	
        }

    }

    //Реализация Генератора
    private void ZhegalkinGenerator (int totalVarCount, int head) {

        ArrayList<Integer> vector = new ArrayList<>();
        ArrayList<Integer> ZhegalikinIndexes = new ArrayList<>();

        currFunc.clear();
        buffVector = new char[totalVarCount][1];
        vector.add(Integer.parseInt(Character.toString(VectorFunctions[0][head])));

        int unitCounter = 0;

        for(int j = 1; j < totalVarCount; j++) {
            int random = randomBit(seed);
            if(random == 1){
                unitCounter++;
            }
            buffVector[j][0] = Integer.toString(random).charAt(0);
            currFunc.add(Character.toString(buffVector[j][0]));
            vector.add(random);
        }

        ZhegalikinIndexes.add(vector.get(0));
        int m = vector.size();

        for(int k = 0; k < m - 1; k++) {

            for (int i = 1; i < vector.size(); i++) {
                int j = vector.get(i - 1) ^ vector.get(i);
                vector.set(i - 1, j);
            }

            vector.remove(vector.size()-1);
            ZhegalikinIndexes.add(vector.get(0));
        }

        ZhegalikinIndexes.removeAll(Collections.singleton(0));
        int zhegalkinIndexesLen = ZhegalikinIndexes.size();
        if(VectorFunctions[0][head] == '1'){
            zhegalkinIndexesLen -= 1;
        }

        //Если функция тривиальна (1 или 0 на всех значениях) или
        // слишком мало комбинаци на которых функция принимает значение истина, то генерируем заново
        if(     unitCounter == 0 || unitCounter == totalVarCount
                || unitCounter > Math.floor(maxTruePercent*totalVarCount)
                || unitCounter < Math.floor(minTruePercent*totalVarCount)
                || currFunc.equals(prevFunc)
                || zhegalkinIndexesLen <= 1
        ){
            ZhegalkinGenerator(totalVarCount, head);
        } else {

            //текущий вектор не должен совпадать с вектором предыдущей функции
            //Глубокое копирование Текущая функция становится предыдущей
            prevFunc.clear();
            for(String obj : currFunc) {
                String b = obj;
                prevFunc.add(b);
            }

        }

    }

    private int randomBit(long seed) {
        return (int) (Math.random() * ++seed);
    }

    public StringBuilder getLog() {
        return  log;
    }

}
