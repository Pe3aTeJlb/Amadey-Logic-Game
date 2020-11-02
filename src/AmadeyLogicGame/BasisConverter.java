/*    
Copyright (C) Pplos Studio
    
    This file is a part of Avrora Logic Game, which based on CircuitJS1
    https://github.com/Pe3aTeJlb/Avrora-logic-game
    
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

package AmadeyLogicGame;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;

public class BasisConverter {

    public String outFunction = "";
    public ArrayList<ArrayList<String>> list = new ArrayList<>();
    private ArrayList<String> operands = new ArrayList<>();
    private ArrayList<String> terms = new ArrayList<>();
    private boolean debug = false;
    public String dmp;
    public boolean Has1 = false;
    
    public BasisConverter(boolean dbg){debug = dbg;}

    //Приведение к базису и-не
    public void ToNand(String func, boolean sum_of_prod) {
    
    	dmp = "";
    	list.clear();
    	terms.clear();
    	outFunction = "";
    	
        if(sum_of_prod){

            String buffFuncName = "";

            func = func.replace(" ","");
            func = func.replace("(","");
            func = func.replace(")","");
            String[] tmp = func.split("\\+");

            for(int i = 0; i < tmp.length; i++){
                operands = new ArrayList<>();
                String funcName = "";
                String[] t = tmp[i].split("\\*");

                if(tmp[i].length()<=3){
                    funcName = tmp[i];
                }else{
                    funcName = "~("+tmp[i]+")";
                    for(int j = 0; j < t.length; j++){
                        operands.add(t[j]);
                    }
                    operands.add("Nand");
                    operands.add(funcName);
                }
                terms.add(funcName);
                buffFuncName += funcName + "*";
                if(operands.size()!=0)list.add(operands);
            }
            if(tmp.length>1) {
            buffFuncName = removeByIndex(buffFuncName, buffFuncName.length()-1);
            buffFuncName = "~("+buffFuncName+")";
            terms.add("Nand");
            terms.add(buffFuncName);
            list.add(terms);
            }


        }else{

            String buffFuncName = "";

            func = func.replace(" ","");
            func = func.replace("(","");
            func = func.replace(")","");
            String[] tmp = func.split("\\*");

            for(int i = 0; i < tmp.length; i++){

                operands = new ArrayList<>();
                String funcName = "";
                String[] t = tmp[i].split("\\+");

                if(tmp[i].length()<=3){
                    funcName = tmp[i];
                }else{
                    for(int j = 0; j < t.length; j++){
                        if(t[j].charAt(0) == '~'){
                            t[j] = removeByIndex(t[j],0);
                            funcName += t[j];
                            funcName += "*";
                        }else if(t[j].charAt(0) != '~'){
                            t[j] = "~" + t[j];
                            funcName += t[j];
                            funcName += "*";
                        }
                        operands.add(t[j]);
                    }

                    funcName = removeByIndex(funcName, funcName.length()-1);
                    funcName = "~("+funcName+")";
                    operands.add("Nand");
                    operands.add(funcName);
                }

                terms.add(funcName);
                buffFuncName += funcName + "*";
                if(operands.size()!=0)list.add(operands);
            }

            buffFuncName = removeByIndex(buffFuncName, buffFuncName.length()-1);
            buffFuncName = "~("+buffFuncName+")";
            if(terms.size()==1) {
            	terms.add(terms.get(0));
            }
            terms.add("Nand");
            terms.add(buffFuncName);
            list.add(terms);

            //инверсия через элемент или-не
            terms = new ArrayList<>();
            terms.add(buffFuncName);
            terms.add(buffFuncName);
            terms.add("Nand");
            terms.add("~("+buffFuncName+")");
            list.add(terms);
        }

    }

    //Приведение к базису Или-не
    public void ToNor(String func, boolean sum_of_prod) {
    	
    	dmp = "";
    	list.clear();
    	terms.clear();
    	outFunction = "";
    	
        if(sum_of_prod){

            String buffFuncName = "";

            func = func.replace(" ","");
            func = func.replace("(","");
            func = func.replace(")","");
            String[] tmp = func.split("\\+");

            for(int i = 0; i < tmp.length; i++){

                operands = new ArrayList<>();
                String funcName = "";
                String[] t = tmp[i].split("\\*");

                if(tmp[i].length()<=3){
                    funcName = tmp[i];
                }else{
                    for(int j = 0; j < t.length; j++){
                        if(t[j].charAt(0) == '~'){
                            t[j] = removeByIndex(t[j],0);
                            funcName += t[j];
                            funcName += "+";
                        }else if(t[j].charAt(0) != '~'){
                            t[j] = "~" + t[j];
                            funcName += t[j];
                            funcName += "+";
                        }
                        operands.add(t[j]);
                    }
                    funcName = removeByIndex(funcName, funcName.length()-1);
                    funcName = "~("+funcName+")";
                    operands.add("Nor");
                    operands.add(funcName);
                }

                terms.add(funcName);
                buffFuncName += funcName + "+";
                if(operands.size()!=0)list.add(operands);
            }

            buffFuncName = removeByIndex(buffFuncName, buffFuncName.length()-1);
            if(tmp.length>1) {
            buffFuncName = "~("+buffFuncName+")";
            terms.add("Nor");
            terms.add(buffFuncName);
            list.add(terms);
            }
            //инверсия через элемент или-не
            terms = new ArrayList<>();
            terms.add(buffFuncName);
            terms.add(buffFuncName);
            terms.add("Nor");
            terms.add("~("+buffFuncName+")");
            list.add(terms);
        }else{

            //[[x0, x1, Nor, ~(x0+x1)], [~x1, ~x2, Nor, ~(~x1+~x2)], [~(x0+x1), ~(~x1+~x2), Nor, ~(~(x0+x1)+~(~x1+~x2))]]
            String buffFuncName = "";

            func = func.replace(" ","");
            func = func.replace("(","");
            func = func.replace(")","");
            String[] tmp = func.split("\\*");

            for(int i = 0; i < tmp.length; i++){
               
                operands = new ArrayList<>();
                String funcName = "";
                String[] t = tmp[i].split("\\+");

                if(tmp[i].length()<=3){

                    funcName = tmp[i];

                }else{

                    funcName = "~("+tmp[i]+")";
                    for(int j = 0; j < t.length; j++){
                            operands.add(t[j]);
                    }
                    operands.add("Nor");
                    operands.add(funcName);
                }
                
                terms.add(funcName);
                buffFuncName += funcName + "+";
                //GWT.log("FuncName " + buffFuncName);
                if(operands.size()!=0)list.add(operands);
            }
            if(tmp.length>1) { 
            buffFuncName = removeByIndex(buffFuncName, buffFuncName.length()-1);
            buffFuncName = "~("+buffFuncName+")";
            terms.add("Nor");
            terms.add(buffFuncName);
            
            list.add(terms);
            }
            
        }

    }

    //Приведение к полиному Жегалкина методом треугольника
    public void ToZhegalkinPolynomial(char[][] functionVector, int row, String[] varNames) {
    	
    	dmp = "";
    	list.clear();
    	terms.clear();
    	outFunction = "";
    	
        ArrayList<Integer> vectorFunc = new ArrayList<>();
        ArrayList<Integer> ZhegalikinIndexes = new ArrayList<>();

        for(int i = 0; i<functionVector.length;i++){
            vectorFunc.add(Integer.parseInt(Character.toString(functionVector[i][row])));
        }

        if(debug)GWT.log("Init vf "+ vectorFunc.toString());
        dmp+=vectorFunc.toString()+"\n";

        int m = vectorFunc.size();
        ZhegalikinIndexes.add(vectorFunc.get(0));

        for(int k = 0; k<m-1; k++) {
        	
            if(k == 0 && vectorFunc.get(0) == 1){
                //outFunction += "1@";
                Has1 = true;
            }

            for (int i = 1; i < vectorFunc.size(); i++) {

                if (i == 1) {

                    if(vectorFunc.get(0) == 1){
                        operands = new ArrayList<>();
                        String v = Integer.toBinaryString(k);
                        //if(debug)GWT.log("term "+v);
                        //dmp+=v+"\n";
                        String tmp = "";
                        int y = 0;
                        //Если мы берём нулевую комбинацию, то всё разваливается. с другой стороны, в Жегалкине 0 комбинация станет константной единицей в схеме, что не реализовано, а это делать мне лень
                        if(!v.equals("0")) {
	                        for(int l = varNames.length-v.length(); l<varNames.length; l++){
	                            if(v.charAt(y) == '1'){
	                                tmp += varNames[l]+"*";
	                                operands.add(varNames[l]);
	                            }
	                            y++;
	                        }
                        

                            tmp = removeByIndex(tmp,tmp.length()-1);

                            if(operands.size()>1){
                                operands.add("*");
                                operands.add(tmp);
                                terms.add(tmp);
                                list.add(operands);
                            }else{terms.add(operands.get(0));}

                            outFunction += tmp+"@";
                            if(debug)GWT.log(outFunction);
                        }
                    }

                }
                int j = vectorFunc.get(i - 1) ^ vectorFunc.get(i);
                vectorFunc.set(i-1,j);
            }
           
            vectorFunc.remove(vectorFunc.size()-1);
            
            dmp += vectorFunc.toString() + "\n";
            if(debug)GWT.log(vectorFunc.toString());
            ZhegalikinIndexes.add(vectorFunc.get(0));
        }
        
        //дополнительный прогон оставшегося списка
        if(vectorFunc.get(0) == 1){
            operands = new ArrayList<>();
            String v = Integer.toBinaryString(m-1);
            if(debug)GWT.log(v);
            //dmp+=v+"\n";
            String tmp = "";
            int y = 0;
            for(int l = varNames.length-v.length(); l<varNames.length; l++){
                if(v.charAt(y) == '1'){
                    tmp += varNames[l]+"*";
                    operands.add(varNames[l]);
                }
                y++;
            }

                tmp = removeByIndex(tmp,tmp.length()-1);

                if(operands.size()>1){
                    operands.add("*");
                    operands.add(tmp);
                    terms.add(tmp);
                    list.add(operands);
                }else{terms.add(operands.get(0));}

                outFunction += tmp+"@";
                if(debug)GWT.log(outFunction);
        }

        outFunction = removeByIndex(outFunction, outFunction.length()-1);
        terms.add("Xor");
        terms.add(outFunction);
        list.add(terms);
      

        //Сортировка схемы соединения последнего блока для корректного отображения
        //Входные элементы схемы должны подключаться последними
        
       
        for(int i = 0; i< list.get(list.size()-1).size()-3; ++i) {
        	for(int j = 0; j < list.get(list.size()-1).size()-3 - i; j++) {
        		if(list.get(list.size()-1).get(j).length() < list.get(list.size()-1).get(j+1).length()) {
	        			String tmp = list.get(list.size()-1).get(j);
	        			String tmp2 = list.get(list.size()-1).get(j+1);
	        			list.get(list.size()-1).set(j, tmp2);
	        			list.get(list.size()-1).set(j+1, tmp);
	        			GWT.log("!!!!!!!!!!!!!!0"+list.get(list.size()-1).get(j));
        			}
        		}
        	}
        
        if(debug)GWT.log(ZhegalikinIndexes.toString());
        if(debug)GWT.log(outFunction);
        if(debug)GWT.log(list.toString());
        GWT.log(""+list.get(list.size()-1).get(list.get(list.size()-1).size()-3));
        dmp+=ZhegalikinIndexes.toString()+"\n"+outFunction+"\n"+list.toString()+"\n";
        	

        	
    }

    private String removeByIndex(String str, int index) {
        return str.substring(0,index)+str.substring(index+1);
    }

}
