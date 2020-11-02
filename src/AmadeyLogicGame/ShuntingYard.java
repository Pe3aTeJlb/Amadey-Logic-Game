/* 	Author of ShuntingYard Борис Марченко
	https://javatalks.ru/topics/5515   
    Modified by Pplos Studio
       
    This file is a part of Avrora Logic Game, which based on CircuitJS1
    https://github.com/Pe3aTeJlb/Avrora-logic-game
      
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

import java.util.Stack;

import com.google.gwt.core.client.GWT;

//import com.google.gwt.core.client.GWT;

import java.util.*;

/**
 * Класс содержит утилиты для разбора и обработки математических выражений.
 *
 * @author Борис Марченко
 * @version $Revision$ $Date$
 */
public class ShuntingYard {

    public ArrayList<ArrayList<String>> list = new ArrayList<>();
    public ArrayList<String> out = new ArrayList<>();
    private boolean debug = false;
    public String dmp = "";

    public ShuntingYard(boolean dbg){
    	debug = dbg;
    }


	/**
     * Основные математические операции и их приоритеты.
     *
     * @see #sortingStation(String, java.util.Map)
     */
    public static final Map<String, Integer> MAIN_MATH_OPERATIONS;

    static {
        MAIN_MATH_OPERATIONS = new HashMap<String, Integer>();

        //MAIN_MATH_OPERATIONS.put("~", 1);
        MAIN_MATH_OPERATIONS.put("*", 2);
        MAIN_MATH_OPERATIONS.put("+", 3);
        MAIN_MATH_OPERATIONS.put("@", 3);
    }

    /**
     * Преобразует выражение из инфиксной нотации в обратную польскую нотацию (ОПН) по алгоритму <i>Сортировочная
     * станция</i> Эдскера Дейкстры. Отличительной особенностью обратной польской нотации является то, что все
     * аргументы (или операнды) расположены перед операцией. Это позволяет избавиться от необходимости использования
     * скобок. Например, выражение, записаное в инфиксной нотации как 3 * (4 + 7), будет выглядеть как 3 4 7 + *
     * в ОПН. Символы скобок могут быть изменены.
     * <a href="http://ru.wikipedia.org/wiki/Обратная_польская_запись">Подробнее об ОПЗ</a>.
     *
     * @param expression выражение в инфиксной форме.
     * @param operations операторы, использующиеся в выражении (ассоциированные, либо лево-ассоциированные).
     * Значениями карты служат приоритеты операции (самый высокий приоритет - 1). Например, для 5
     * основных математических операторов карта будет выглядеть так:
     * <pre>
     *      *   ->   1
     *      /   ->   1
     *      +   ->   2
     *      -   ->   2
     * </pre>
     * Приведенные операторы определены в константе {@link #MAIN_MATH_OPERATIONS}.
     * @param leftBracket открывающая скобка.
     * @param rightBracket закрывающая скобка.
     * @return преобразованное выражение в ОПН.
     */
    public static String sortingStation(String expression, Map<String, Integer> operations, String leftBracket,
                                        String rightBracket) {

        if (expression == null || expression.length() == 0)
            throw new IllegalStateException("Expression isn't specified.");
        if (operations == null || operations.isEmpty())
            throw new IllegalStateException("Operations aren't specified.");

        // Выходная строка, разбитая на "символы" - операции и операнды..
        List<String> out = new ArrayList<String>();
        // Стек операций.
        Stack<String> stack = new Stack<String>();

        // Удаление пробелов из выражения.
        expression = expression.replace(" ", "");

        // Множество "символов", не являющихся операндами (операции и скобки).
        Set<String> operationSymbols = new HashSet<String>(operations.keySet());
        operationSymbols.add(leftBracket);
        operationSymbols.add(rightBracket);

        // Индекс, на котором закончился разбор строки на прошлой итерации.
        int index = 0;
        // Признак необходимости поиска следующего элемента.
        boolean findNext = true;

        while (findNext) {
            int nextOperationIndex = expression.length();
            String nextOperation = "";
            // Поиск следующего оператора или скобки.
            for (String operation : operationSymbols) {
                int i = expression.indexOf(operation, index);
                if (i >= 0 && i < nextOperationIndex) {
                    nextOperation = operation;
                    nextOperationIndex = i;
                }
            }
            // Оператор не найден.
            if (nextOperationIndex == expression.length()) {
                findNext = false;
            } else {
                // Если оператору или скобке предшествует операнд, добавляем его в выходную строку.
                if (index != nextOperationIndex) {
                    out.add(expression.substring(index, nextOperationIndex));
                }
                // Обработка операторов и скобок.
                // Открывающая скобка.
                if (nextOperation.equals(leftBracket)) {
                    stack.push(nextOperation);
                }
                // Закрывающая скобка.
                else if (nextOperation.equals(rightBracket)) {
                    while (!stack.peek().equals(leftBracket)) {
                        out.add(stack.pop());
                        if (stack.empty()) {
                            throw new IllegalArgumentException("Unmatched brackets");
                        }
                    }
                    stack.pop();
                }
                // Операция.
                else {
                    while (!stack.empty() && !stack.peek().equals(leftBracket) &&
                            (operations.get(nextOperation) >= operations.get(stack.peek()))) {
                        out.add(stack.pop());
                    }
                    stack.push(nextOperation);
                }
                index = nextOperationIndex + nextOperation.length();
            }
        }

        // Добавление в выходную строку операндов после последнего операнда.
        if (index != expression.length()) {
            out.add(expression.substring(index));
        }

        // Пробразование выходного списка к выходной строке.
        while (!stack.empty()) {
            out.add(stack.pop());
        }
        StringBuffer result = new StringBuffer();
        if (!out.isEmpty())
            result.append(out.remove(0));
        while (!out.isEmpty())
            result.append(" ").append(out.remove(0));

        return result.toString();
    }

    /**
     * Преобразует выражение из инфиксной нотации в обратную польскую нотацию (ОПН) по алгоритму <i>Сортировочная
     * станция</i> Эдскера Дейкстры. Отличительной особенностью обратной польской нотации является то, что все
     * аргументы (или операнды) расположены перед операцией. Это позволяет избавиться от необходимости использования
     * скобок. Например, выражение, записаное в инфиксной нотации как 3 * (4 + 7), будет выглядеть как 3 4 7 + *
     * в ОПН.
     * <a href="http://ru.wikipedia.org/wiki/Обратная_польская_запись">Подробнее об ОПЗ</a>.
     *
     * @param expression выражение в инфиксной форме.
     * @param operations операторы, использующиеся в выражении (ассоциированные, либо лево-ассоциированные).
     * Значениями карты служат приоритеты операции (самый высокий приоритет - 1). Например, для 5
     * основных математических операторов карта будет выглядеть так:
     * <pre>
     *      *   ->   1
     *      /   ->   1
     *      +   ->   2
     *      -   ->   2
     * </pre>
     * Приведенные операторы определены в константе {@link #MAIN_MATH_OPERATIONS}.
     * @return преобразованное выражение в ОПН.
     */
    public static String sortingStation(String expression, Map<String, Integer> operations) {
        return sortingStation(expression, operations, "(", ")");
    }

    public void calculateMDNF(String input){

        ArrayList<String> data = new ArrayList<>();
        ArrayList<String> lastTerm = new ArrayList<>();

        input = input.replace(" ", "");
        input = input.replace("(", "");
        input = input.replace(")", "");
        String[] terms = input.split("\\+");

        for(int i = 0; i <terms.length; i++){

            String[] operands = terms[i].split("\\*");

            for(int j = 0; j<operands.length; j++){
                data.add(operands[j]);
            }
            
            data.add("*");
            data.add(terms[i]);
            list.add(data);
            lastTerm.add(terms[i]);
            data = new ArrayList<>();
        }
        if(terms.length>1) {
        	lastTerm.add("+");
        	lastTerm.add(input);
        	list.add(lastTerm);
        }
        
    }
    
    public void calculateMKNF(String input){

        ArrayList<String> data = new ArrayList<>();
        ArrayList<String> lastTerm = new ArrayList<>();

        input = input.replace(" ", "");
        input = input.replace("(", "");
        input = input.replace(")", "");
        String[] terms = input.split("\\*");

        for(int i = 0; i <terms.length; i++){

            String[] operands = terms[i].split("\\+");

            for(int j = 0; j<operands.length; j++){

                data.add(operands[j]);

            }
            data.add("+");
            data.add(terms[i]);
            list.add(data);
            lastTerm.add(terms[i]);
            data = new ArrayList<>();
        }

        if(terms.length>1) {
        	lastTerm.add("*");
        	lastTerm.add(input);
        	list.add(lastTerm);
        }
    }   

    /**
     * Вычисляет значение выражения, записанного в инфиксной нотации. Выражение может содержать скобки, числа с
     * плавающей точкой, четыре основных математических операндов.
     *
     * @param expression выражение.
     * @return результат вычисления.
     */
    public void calculateExpression(String expression) {
    	
    	 dmp = "";
    	
    	 String rpn = sortingStation(expression, MAIN_MATH_OPERATIONS);
         StringTokenizer tokenizer = new StringTokenizer(rpn, " ");
         if(debug)GWT.log(rpn); dmp += rpn+"\n";
         Stack<String> stack = new Stack<String>();

         ArrayList<String> operands = new ArrayList<>();
         int newTerm = 0;
         int prevStackSize = 0;
         int startStackSize = 0;
         String prevOperation = "";
         String term = "";

         /*
         Выделим 2 случая: когда знак текущей операции совпадает и не совпадает.
         на 0 итерации зафорсим совпадение операций

         на каждой итерации из стека достаём 2 операнда
             если на текущей операции, до вынимания в стек был добавлен новый операнд, см added to stack
             если мы разбираем стек, то см shift stack left

         когда в стек добавляется более 2 операндов, считается что начался новый терм. завершается предыдущий, если он былл, создаётся новый список

         когда операции не совпадают, мы завершаем текущий список, создаём новый и записываем туда 2 первых операнда из стека

          */
         if(debug)GWT.log("");
         if(debug)GWT.log("Shunting Yard");
         if(debug)GWT.log("How to read");
         if(debug)GWT.log("New iter - means new iteration");
         if(debug)GWT.log("Current stack");
         if(debug)GWT.log("prev stack size");
         if(debug)GWT.log("current stack size");
         if(debug)GWT.log("operand 1");
         if(debug)GWT.log("operand 2");
         if(debug)GWT.log("current operation");
         if(debug)GWT.log("prev operation");
         
         while (tokenizer.hasMoreTokens()) {

             String token = tokenizer.nextToken();
             // Операнд.
             if (!MAIN_MATH_OPERATIONS.keySet().contains(token)) {
                 stack.push(new String(token));
                 newTerm++;
             } else {

                 if(prevOperation.equals("")){
                     prevOperation = token;
                 }

                 if(debug)GWT.log("New Iter");
                 if(debug)GWT.log(""+stack);
                 if(debug)GWT.log(""+prevStackSize);
                 if(debug)GWT.log(""+stack.size());
                 dmp+= "New Iter" + "\n" + ""+stack + "\n" + ""+prevStackSize + "\n" + ""+stack.size()+ "\n"; 
                 
                 startStackSize = stack.size();
                 String operand2 = stack.pop();
                 String operand1 = stack.empty() ? "" : stack.pop();
                 
                 if(debug)GWT.log(operand1);
                 if(debug)GWT.log(operand2);
                 if(debug)GWT.log(""+token);
                 if(debug)GWT.log("prev op " + prevOperation);
                 dmp+= operand1 + "\n" + operand2 + "\n" + ""+token + "\n" + "prev op " + prevOperation + "\n";

                 if(token.equals(prevOperation)){
                	 if(debug)GWT.log("Like prev");dmp+="Like prev" + "\n";
                     if(operands.size()>0 && !list.contains(operands)){list.add(operands);}
                      

                     if(newTerm>=2){
                    	 if(debug)GWT.log("new Term");
                    	 dmp+="new Term"+"\n";
                         if(term.length()>0 && (term.charAt(term.length()-1) == '*' || term.charAt(term.length()-1) == '+'))term = removeByIndex(term, term.length()-1);

                         if(!term.equals("")) {

                             operands.add(token);
                             operands.add(term);

                         }

                         operands=new ArrayList<>();
                         list.add(operands);
                         operands.add(operand1);
                         operands.add(operand2);
                         term = "";
                         term = operand1 + token + operand2;

                         if(debug)GWT.log("    " + operands);
                         dmp += "    " + operands+"\n";
                         stack.push(term);
                         newTerm=0;
                         prevOperation = token;
                         prevStackSize = stack.size();
                     }else{

                         if(startStackSize>prevStackSize){
                        // if(prevStackSize <= stack.size()-2){
                        	 if(debug)GWT.log("Operands were added to stack");
                        	 dmp += "Operands were added to stack" + "\n";
                             operands.add(operand2);
                             if(term.length()>2){term += token+operand2;}
                             else {term += token+operand1;}

                             if(debug)GWT.log("    " + operands);
                             dmp += "    " + operands.toString()+"\n";
                             if(term.length()>2){if(term.charAt(term.length()-1) == '*' || term.charAt(term.length()-1) == '+')term = removeByIndex(term, term.length()-1);}
                             stack.push(term);
                             newTerm=0;
                             prevOperation = token;
                             prevStackSize = stack.size();

                         }
                         else{
                        	 if(debug)GWT.log("Stack was shifted left");
                        	 dmp += "Stack was shifted left" + "\n";
                             operands.add(operand1);
                             if(term.length()>2)if(term.charAt(term.length()-1) == '*' || term.charAt(term.length()-1) == '+'){term += operand1;}
                             else {term += token+operand1;}

                             if(debug)GWT.log("    " + operands);
                             dmp += "    " + operands + "\n";
                             // String NewOperand = operand1.concat(token+operand2);
                             //stack.push(NewOperand);
                             if(term.length()>2){if(term.charAt(term.length()-1) == '*' || term.charAt(term.length()-1) == '+')term = removeByIndex(term, term.length()-1);}
                             stack.push(term);
                             newTerm=0;
                             prevOperation = token;
                             prevStackSize = stack.size();

                         }



                     }

                 }else{

                	 if(debug)GWT.log("Not Like prev");
                	 dmp += "Not Like prev" + "\n";

                     if(term.length()>2){if(term.charAt(term.length()-1) == '*' || term.charAt(term.length()-1) == '+')term = removeByIndex(term, term.length()-1);}
                     if(!operands.contains(prevOperation) && operands.size()>=2)operands.add(prevOperation);
                     if(!operands.contains(term) && operands.size()>=2)operands.add(term);
                     if(debug)GWT.log("Result list " +operands);
                     dmp += "Result list " +operands + "\n";

                     term = "";

                     //if(operands.size()>0)list.add(operands);
                     operands = new ArrayList<>();
                     list.add(operands);

                     if(debug)GWT.log("new list");
                     dmp+= "new list" + "\n";
                     String NewOperand = "";

                     if(stack.size()<=2){

                         if(!operand1.equals(""))operands.add(operand1);
                         operands.add(operand2);

                         NewOperand=operand1.concat(token + operand2);

                         term = NewOperand;

                         stack.push(NewOperand);
                         prevStackSize = stack.size();
                         newTerm=0;
                         prevOperation = token;
                         if(debug)GWT.log(operands.toString());
                         dmp += operands.toString()+"\n";
                     }
                     else {

                         if(!operand1.equals(""))operands.add(operand1);
                         operands.add(operand2);

                         if (!operand1.equals("")) {
                             NewOperand = operand1.concat(token + operand2);
                         } else {
                             NewOperand = token + operand2;
                         }
                         term = NewOperand;
                         stack.push(NewOperand);
                         prevStackSize = stack.size();
                         newTerm=0;
                         prevOperation = token;
                         if(debug)GWT.log(term);
                         dmp += term + "\n";
                     }
                 }
             }
         }

         operands.add(prevOperation);
         operands.add(term);
         if(debug)GWT.log(operands.toString());
         if(debug)GWT.log(list.toString());
         if(debug)GWT.log("RPN "+rpn);
         dmp += operands.toString() + "\n" + list.toString()+"\n"+"RPN "+rpn+"\n";
    }
    
    private String removeByIndex(String str, int index) {
        return str.substring(0,index)+str.substring(index+1);
    }
    
}