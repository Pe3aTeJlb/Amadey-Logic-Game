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

package AmadeyLogicGame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

//x0, x1, ~x0 и тд - Входные элементы схемы

//Описание схемы выглядит следующим образом
// [ [,входной элемент 1 прим. x0,вхлдной элемент 2,...,символ операции, имя нового элемента], [аналогично] ,...]

//Про систему индексов входов, выходов элемента. 
/*
 Сначала надо высчитать эти точки - массив getOperativePoint() а любом объекте класса circuitElm
 Последняя точка в этом массиве - выход элемента, всё остальное входы.
 В случае провода - 0 индекс - точка начала провода (левый конец, если провод идёт слева на право), соответственно индекс 1 - точка окончания
 */

/*
 * drawPost отвечает за отрисовку точек
 */

/*	  	   _____________
 * 		0--|комб.логика|--0
 * 			-----------
 * входная переменная     выходная переменная
 */		 

public class CircuitSynthesizer {

	private boolean MDNF, factorize;
	private String basis = "";
	private int funcCount = 0;
	private int varCount = 0;
	private ArrayList<String> sharedVars = new ArrayList<String>();

	private Point input_freeSpace = new Point(50,80); //Точка начала отрисовки входов схемы
	private Point StartPoint = new Point(1,1); // фиксация начальной точки текущей функции
	private Point NextStartPoint = new Point(2,2); //фиксация начальной точки следующей функции
	private Point freeSpace = new Point(3,3); //текущая свободнеая точка
	private int lastElemPos = 0;
	
	ArrayList<ArrayList<String>> list = new ArrayList<>();
	
	private Map<String,CircuitElm> dictionary = new HashMap<>();
	
	public Vector<CircuitElm> elmList = new Vector<>();//все элементы схемы. Нужны для отрисовки
	
	private ArrayList<String> splittedWires = new ArrayList<>();//см логику соединения
	
	//Для удаления неиспользуемых входных переменных
	private ArrayList<ArrayList<CircuitElm>> UnusedInputs = new ArrayList<>();
	private ArrayList<String> UnusedVarNames = new ArrayList<>();
	private ArrayList<ArrayList<String>> allInputs = new ArrayList<>();
	
	//Для игровой логики
	public ArrayList<SwitchElm> inElems = new ArrayList<>(); //список входных элементов
	public ArrayList<CircuitElm> outElems = new ArrayList<>(); //список выходных элементов

	//AWL = Additional Wire Length
	private final int AWL = 100;
	
	public int width, height;

	private float minTrue = 0.5f;//Устанавливает максимальное и минимальное количество единиц в вектор функции
	private float maxTrue = 0.6f;
	private int dashCirc = 0; //Индекс текущей схемы для смещения
	
	private int wireSplitPoint = 0; //x координата перелома провода
	
	private ArrayList<CircuitElm> LastLogElems = new ArrayList<>();//список последнего лог элемента подсхемы нужен для выравнивания платформ
	
	private BasisConverter converter = new BasisConverter();
	private Factorisator_V_2 factorizator = new Factorisator_V_2();
    private ShuntingYard shuntingYard = new ShuntingYard();
    private LogicFunctionGenerator generator = new LogicFunctionGenerator();

    private String nl = System.getProperty("line.separator");
	private StringBuilder log = new StringBuilder(nl+"<<CircuitSunthesizer>>"+nl);
    
    //Training
	public void Synthesis(int w, int h) {
		
		sharedVars = new ArrayList<>();
		
		width = w;
		height = h;
			
		int circCount = random(1,6);

		log.append("Circ count ").append(circCount).append(nl);


		//minTrue = random(2,6)/10;
		//maxTrue = minTrue+0.2f;
		log.append("Circ count ").append(circCount).append(nl).append("maxT").append(maxTrue).append(nl);
		
		for(int i = 0; i < circCount; i++) {
			
			int Basis =  random(0,2);
			
			int mdnf =  random(0,1);
			if(mdnf == 0) {
				MDNF = false;
			}else {MDNF = true;}
			
			if(Basis == 0) {
				basis = "Default";
				if(MDNF == true) {
					int factr =  random(0,1);
					if(factr == 1) {
						factorize = true;
						varCount =  random(2,4);
					}else {
						factorize = false;
						varCount =  random(2,4);
					}
				}
				else {varCount =  random(2,4);}
			}else if(Basis == 1) {
				basis = "Nor";
				varCount =  random(2,3);
			}else if(Basis == 2) {
				basis = "Nand";
				varCount =  random(2,3);
			}else if(Basis == 3) {
				basis = "Zhegalkin";
				varCount =  random(2,3);
			}
			
			funcCount =  random(1,4);
			//funcCount = random(1,(int)Math.floor(0.5f*Math.log(circDifficult-0.5f)+2));

			log.append("MDNF ").append(MDNF).append(nl).
					append("Factr ").append(factorize).append(nl).
					append("Basis ").append(basis).append(nl).
					append("Function count ").append(funcCount).append(nl).
					append("Var count ").append(varCount).append(nl);

					//Add shared input
					if(i>0 && i < circCount - 1 && circCount>1) {

						log.append("Include shared vars").append(nl);

						int n = random(0,1);
						
						if(n==1) {
							
							int sharedVarsCount = random(1,varCount-1);
							
							for(int j = 0; j<sharedVarsCount; j++) {
								add();
							}
						
						}
						
					} 
			
			dashCirc++;		
			InitializeParametrs();
			input_freeSpace.y += 100;
			freeSpace.y = input_freeSpace.y;
			
			
		}
		DeleteUnusedInputs();
}
	
	//Custom circuit
	public void Synthesis(int w, int h, String url) {
		
		generator.callOnce = true;
		 
		width = w;
		height = h;
	
		//GetConfigurationFile();
		sharedVars = new ArrayList<>();
	
		 MDNF = true;
		 factorize = true;
		 basis = "Default";
		// basis = "Nor";
		// basis = "Nand";
		 //basis = "Zhegalkin";
		 funcCount = 2;
		 varCount = 3;

		log.append("MDNF ").
				append(MDNF).append(nl).
				append("Basis ").
				append(basis).append(nl).
				append("Function count ").append(funcCount).append(nl).
				append("Var count ").append(varCount).append(nl);
		
		InitializeParametrs();
		DeleteUnusedInputs();
		CreateCircuitOutput();
	
}
	
	//Practise
	public void Synthesis(int w, int h, int circDifficult) {
		
		generator.callOnce = true;
		
		sharedVars = new ArrayList<>();
		
		width = w;
		height = h;
			
		int circCount = (int)Math.floor(((0.35f*circDifficult-1.8f)*(0.35f*circDifficult-1.8f)+3));
		
		if(circDifficult <6) {
			minTrue = 0.5f;
			maxTrue = 0.6f;
		}else {
			minTrue = 0.3f;
			maxTrue = 0.4f;
		}

		log.append("Circ count ").append(circCount).append(nl).
				append("maxT").append(maxTrue).append(nl).append(nl);
		
		for(int i = 0; i < circCount; i++) {
			//dashCirc = 0;

			log.append("Circ №").append(i).append(nl).append(nl);

			int Basis = 0;
			if(circDifficult < 5) {

				int maxDif = (int)Math.floor(circDifficult*0.6f);
				if(maxDif>2)maxDif=2;
				Basis =  random(1,maxDif);

			}else {

				Basis =  random(1,3);
				
			}
			int mdnf =  random(0,1);
			if(mdnf == 0) {
				MDNF = false;
			}else {MDNF = true;}
			
			if(Basis == 1) {
				basis = "Default";
				if(MDNF == true) {
					int factr =  random(0,1);
					if(factr == 1) {
						factorize = true;
					}else {
						factorize = false;
					}
				}
			}else if(Basis == 2) {
				//Костыль. Т.к. в первой схеме 1 подсхеме всегда 1, мы не можем использовать NOR
				if(i==0){
					System.out.println("fixed cirsynth ");
					basis = "Nand";
				}else{
					basis = "Nor";
				}
				varCount =  random(2,3);
			}else if(Basis == 3) {
				basis = "Nand";
				varCount =  random(2,3);
			}else if(Basis == 4) {
				basis = "Zhegalkin";
				varCount =  random(2,3);
			}
			
			varCount = (int)(Math.floor(0.0025f*Math.pow((circDifficult-2), 3)+2.9f));
			funcCount =  (int)Math.floor(0.5f*Math.log(circDifficult-0.5f)+2);

			log.append("MDNF ").append(MDNF).append(nl).append("Factr ").append(factorize).append(nl).append("Basis ").append(basis).append(nl).append("Function count ").append(funcCount).append(nl).append("Var count ").append(varCount).append(nl);

			//Add shared input
			if(i>0 && i < circCount - 1 && circCount>1 && circDifficult > 4) {
						sharedVars.clear();

						log.append("Include Shared Vars").append(nl);

						int sharedVarsCount = random(1,varCount-2);
							
						for(int j = 0; j<sharedVarsCount; j++) {
							add();
						}
						
					} 

			try{
				if(!sharedVars.isEmpty())dashCirc++;
				
				InitializeParametrs();
				
				input_freeSpace.y += 100;
				freeSpace.y = input_freeSpace.y;
				if(sharedVars.isEmpty())lastElemPos = 0;
				
			}catch(Exception e){
				log.append(e).append(nl);
			}

			log.append("End of circuit").append(nl).append(nl);

		}
		
		DeleteUnusedInputs();
		CreateCircuitOutput();

	}
	
	//Создание списка наследуемых входных переменных
	private void add() {
		
		int a = random(0,allInputs.size()-1);
		int b = random(0,allInputs.get(a).size()-1);
		if(!sharedVars.contains(allInputs.get(a).get(b))) {
			sharedVars.add(allInputs.get(a).get(b));
		}else {add();}
		
	}
	
	private void GetConfigurationFile() {
		
		 MDNF = true;
		 factorize = false;
		 basis = "Default";
		 //basis = "Nor";
		// basis = "Zhegalkin";
		 funcCount = 1;
		 varCount = 3;
		
	}
	
	//Инициализация всех объектов и применение файла конфигурации
	private void InitializeParametrs() {
        
        generator.GenerateVectorFunction(varCount, funcCount, sharedVars, minTrue,maxTrue);

        log.append(generator.getLog());
        
        Solver sol = new Solver(
              generator.VectorFunctions,
                				varCount,
                     generator.VarNames,
                     			funcCount,
                     generator.OutNames,
                             MDNF,
                            false,
                             true
        						);
        sol.run();
		
        ArrayList<String> lst = new ArrayList<String>();
        for(int i = 0; i < generator.VarNames.length; i++) {
        	lst.add(generator.VarNames[i]);
        }
        allInputs.add(lst);
		CreateInputElm(generator.VarNames);
        
		String[] functions = sol.getSolution().split("\n");
		
		for(int i = 0; i<functions.length; i++) {
	      		log.append(functions[i]).append(nl);
	      }
		log.append(nl);
		
        for(int i = 0; i<functions.length; i++) {
        	
        	list.clear();
        	
        	StartPoint.y = NextStartPoint.y;
        	StartPoint.x = NextStartPoint.x;
        	
        	freeSpace.y = NextStartPoint.y;
        	freeSpace.x = NextStartPoint.x;
        
	        if(basis.equals("Default")) {

		        if(MDNF) {

		        	if(factorize) {

			        	factorizator.PrepareData(functions[i], varCount);
			        	log.append("Factr out").append(factorizator.output).append(nl);
			        	shuntingYard.calculateExpression(factorizator.output);
			        	log.append(shuntingYard.getLog()).append(nl);

		        	}else {

		        		shuntingYard.calculateMDNF(functions[i]);
		        		log.append(shuntingYard.getLog()).append(nl);

		        	}

		        	list = shuntingYard.list;

		        }
		        else {
		        	shuntingYard.calculateMKNF(functions[i]);
		        	log.append(shuntingYard.getLog());
		        	list = shuntingYard.list;
		        }
	        }
	        else if(basis.equals("Nor")) {
	        	converter.ToNor(functions[i], MDNF);
				log.append(converter.getLog()).append(nl);
	        	list = converter.list;
	        }
	        else if(basis.equals("Nand")) {
	        	converter.ToNand(functions[i], MDNF);
	        	log.append(converter.getLog()).append(nl);
	        	list = converter.list;
	        }
	        else if(basis.equals("Zhegalkin")) {
	        	converter.ToZhegalkinPolynomial(generator.VectorFunctions,i,generator.VarNames);
				log.append(converter.getLog()).append(nl);
	        	list = converter.list;
	        }
	        
	        CreateCircuit(list);
        }
        
        	//DeleteUnusedInputs();
	}
		
	//Проход по всему списку, составление hashmap с созданными эдементами, установка параметров соединения объектов
	private void CreateCircuit(ArrayList<ArrayList<String>> list) {
		
		String blockName = "";
		
		String ll2 = "";
		for(int i = 0; i<list.size(); i++) {
			for(int j = 0; j<list.get(i).size(); j++) {
				ll2+=list.get(i).get(j) + "  ";
			}
			ll2 += " endl "+nl;
		}
		log.append(ll2);
		
		int Yaccum = 1;
		int MaxFreeSpaceY = 0;
		
		for(int i = 0; i<list.size(); i++) {
		
				int inputCount = list.get(i).size()-2;
				String operation= list.get(i).get(list.get(i).size()-2);
				blockName = list.get(i).get(list.get(i).size()-1);
				
	
				/*
				 * Общий смысл алгоритма расположения
				 * для общих случаев Nor Nand Def элементы располагаеются в ряды по операциям.
				 * В Nor Nand есть нюанс с расположением выходного элемента в случае инвертирования ответа через элемент базиса см далее
				 */
				
				//Правила расположения элементов для общего базиса и Жегалкина
				if(basis.equals("Default") && factorize) {
					
					/*
					 * При факторизации выражения:
					 * Если есть два блока вида [x1 x2 * x1*x2] и [x3 x1*x2 +* x3+*x1*x2] торой блок будет сдвинут вправо-вниз
					 */
						
						if(i<list.size()-1) {
						
							if(i==0){		
								freeSpace.x += 75;
								freeSpace.y = StartPoint.y;
							}
							else if(i>=1 && list.get(i).get(0).length()<=3) {
								
								if(!dictionary.containsKey(blockName)) {
									
									freeSpace.x += 125;
									freeSpace.y = MaxFreeSpaceY+100;
								}
								
							}
					//		else if(i>=1 && list.get(i-1).get(list.get(i-1).size()-2) == operation) {
						//		freeSpace.y += 150;
								//freeSpace.x += 25;
						//	}
							else{
								freeSpace.x += 150;
								freeSpace.y = StartPoint.y+(45*Yaccum);
								if(Yaccum>0) {Yaccum= 0;}else{Yaccum= 1;}
							}
							
							//Костыль
							//ни один новый элемент не должен быть левее, чем его входная переменная
							//Ищем такой косяк, ровняем freeSpace.x и накидываем 50 сверху
							
							for(int m = 0; m < list.get(i).size()-2; m++) {
								if(dictionary.containsKey(list.get(i).get(m)) && freeSpace.x < dictionary.get(list.get(i).get(m)).x) {
									freeSpace.x = dictionary.get(list.get(i).get(m)).x + 50;
								}
							}
							
						}
						else if(i==list.size()-1)
						{
							if(MaxFreeSpaceY==0) {
								MaxFreeSpaceY = StartPoint.y;
							}
							
							if(i>=1 && list.get(i).get(0).length()<=3) {
								
								if(!dictionary.containsKey(blockName)) {
									freeSpace.x += 125;
									//freeSpace.y += dictionary.get(list.get(i).get(0)).OperativePoints.get(0).y;
									freeSpace.y = (int)(( MaxFreeSpaceY - StartPoint.y)/2)+StartPoint.y+50;
									NextStartPoint.y = MaxFreeSpaceY;
								}
							}	
							else {
							
								NextStartPoint.y = MaxFreeSpaceY;
								freeSpace.x += 250;
								freeSpace.y = (int)(( MaxFreeSpaceY - StartPoint.y)/2)+StartPoint.y+50;
								
							}
							
							if(freeSpace.x < lastElemPos){
								freeSpace.x = lastElemPos;
							}
							else {lastElemPos = freeSpace.x;}

						}
						
						if(freeSpace.y > MaxFreeSpaceY) {
							MaxFreeSpaceY = freeSpace.y;
						}
					
				
				//Правила расположения элементов для Nor Nand
				} 
				else if(basis.equals("Nor") || basis.equals("Nand") || basis.equals("Default")) {
					
					if(i<list.size()-1) {
						
						if(i>=1 && list.get(i-1).get(list.get(i-1).size()-2) == operation &&
								
								(float)list.get(i-1).get(list.get(i-1).size()-3).length()/list.get(i).get(list.get(i).size()-3).length() > 0.5f //разделение на каскады по длине имени лог элемента
						) {
							
						if(!dictionary.containsKey(blockName))
							freeSpace.y += 150;
							NextStartPoint.y = freeSpace.y;
						}else {
							freeSpace.x += 250;
							freeSpace.y = (int)(( freeSpace.y - StartPoint.y)/2)+StartPoint.y;
						}
						
					}
					else if (i==list.size()-1) {
						
						//Если идёт инвертирование через и-не, или-не, то этот элемент лежит на одной линии с предыдущей
						if( list.get(i).get(list.get(i).size()-4).equals(list.get(i).get(list.get(i).size()-3)) ) {
							freeSpace.x += 100;
						}
						else {
							freeSpace.y = (int)(( freeSpace.y - StartPoint.y)/2)+StartPoint.y;
							freeSpace.x += 250;
						}
						
						if(freeSpace.x < lastElemPos){
							freeSpace.x = lastElemPos;
						}else {lastElemPos = freeSpace.x;}

					}
						
				}else if(basis.equals("Zhegalkin")) {
					
					if(i<list.size()-1) {
						
						if(i>=1 && list.get(i-1).get(list.get(i-1).size()-2) == operation &&
								
								(float)list.get(i-1).get(list.get(i-1).size()-3).length()/list.get(i).get(list.get(i).size()-3).length() > 0.5f //разделение на каскады по длине имени лог элемента
						)
						{
							
							if(!dictionary.containsKey(blockName))
								freeSpace.y += 150;
								NextStartPoint.y = freeSpace.y;
							}else {
								freeSpace.x += 250;
								freeSpace.y = (int)(( freeSpace.y - StartPoint.y)/2)+StartPoint.y;
								NextStartPoint.y = freeSpace.y;
							}
	
					}
						else if (i==list.size()-1) {

								freeSpace.y += 150;
								freeSpace.x += 250;
								NextStartPoint.y = freeSpace.y;

								if(freeSpace.x < lastElemPos){
									freeSpace.x = lastElemPos;
								}else {lastElemPos = freeSpace.x;}

						}
					
				}
				
				
					String ll = "";
					for(int j = 0; j<list.get(i).size(); j++) {
						ll+=list.get(i).get(j) + "  ";
					}
					log.append(ll).append(nl);

				if(!dictionary.containsKey(blockName)) {
				
					CircuitElm newce;
					
					if(converter.Has1 && i==list.size()-1)inputCount+=1;
	
					newce = createCe(operation,freeSpace.x,freeSpace.y,freeSpace.x+60,freeSpace.y, 0, inputCount);
					newce.setPoints();
					newce.getConnectionPoints(false);
					elmList.add(newce);
					
					dictionary.put(blockName, newce);
					
					//сортируем элементы по y
			        for (int m = list.get(i).size() - 3; m >= 1; m--){
			            for (int n = 0; n < m; n++){       
			                if(dictionary.get(list.get(i).get(n)).y > dictionary.get(list.get(i).get(n+1)).y) {               
			                	String buff =  list.get(i).get(n);     
			                	list.get(i).set(n, list.get(i).get(n+1));  
			                	list.get(i).set(n+1, buff);   
			                }
			            }
			        }
					
					wireSplitPoint = newce.OperativePoints.get(0).x;
					
					for(int j = 0; j<list.get(i).size()-2; j++) {
						
						if(UnusedVarNames.contains(list.get(i).get(j))) {
							UnusedVarNames.remove(list.get(i).get(j));
						}
						
						//обработка 1 в Жегалкине
						if(converter.Has1 && i == list.size()-1) {
							
							CircuitElm newce1 = createCe("Linput",dictionary.get(blockName).OperativePoints.get(0).x,
									dictionary.get(blockName).OperativePoints.get(0).y,
									dictionary.get(blockName).OperativePoints.get(0).x-50,
									dictionary.get(blockName).OperativePoints.get(0).y, 0, 2);
							
							newce1.setPoints();
							newce1.getConnectionPoints(false);
							elmList.add(newce1);
							
							SwitchElm c = (SwitchElm)newce1;
							c.position = 1;
							
				  			if(dictionary.get(blockName).OperativePoints.size()>2) {	
				  				dictionary.get(blockName).OperativePoints.remove(0);
				  			}
				  			converter.Has1 = false;
				  			
						}

						//Выбор параметров соежинения: Перелом, ВходнойЭлементСхемы?
							if(dictionary.containsKey(list.get(i).get(j))) 
							{
								
								if(splittedWires.contains(list.get(i).get(j))) 
								{
									
									
									if(list.get(i).get(j).length()<=3) {
										ConnectElements(dictionary.get(list.get(i).get(j)), dictionary.get(blockName),list.get(i).get(j),true,true);
									}else{
										wireSplitPoint = dictionary.get(list.get(i).get(j)).x;
										wireSplitPoint -= 20;
										ConnectElements(dictionary.get(list.get(i).get(j)), dictionary.get(blockName),list.get(i).get(j),true,false);
										}
									
								
								}else 
								{
									
									splittedWires.add(list.get(i).get(j));
									if(list.get(i).get(j).length()<=3) {
										ConnectElements(dictionary.get(list.get(i).get(j)), dictionary.get(blockName),list.get(i).get(j),false,true);
									}else{
										wireSplitPoint -= 20;
										ConnectElements(dictionary.get(list.get(i).get(j)), dictionary.get(blockName),list.get(i).get(j),false,false);
										}
								
								}
								
							}
							else 
							{
								log.append("He he he, error somewhere/ line 142 ").append(list.get(i).get(j)).append(nl);
								ConnectElements(dictionary.get(list.get(i).get(j)), dictionary.get(blockName),list.get(i).get(j),false,false);
							}
					  }

					log.append("End of block").append(nl);
				}
				
		}
		
		NextStartPoint.y += 100;
		
		/*Проверка для случая, когда схема состоит из 1 лог элемента 
		*(тогда все манипуляции с NextStartPoint были проведены зря, т.к. выссоты по y не хватит и схемы будут накладываться)
		*Нижнюю точку текущей схемы сравниваем с последним созданным входным элементом схемы из списка inElms
		*т.к. туда входят только неинвертированные то накидываем 50 для соответствия инвертированному и 75 сверху
		*/
		if(NextStartPoint.y < inElems.get(inElems.size()-1).y+50) {
			
			NextStartPoint.y = inElems.get(inElems.size()-1).y+50+75;
		}
		
		input_freeSpace.y = NextStartPoint.y;
		
		
		LastLogElems.add(dictionary.get(blockName));
		//CreateCircuitOutput(blockName);

		log.append("End of subCirc").append(nl).append(nl);
	}
	
	/*
	 Создаёт входные элементы схемы, их инвертированный вариант и записывает в hashMap
	 
	 Первый вход схемы не доходит 40 единиц до ряда элементов, каждый последующий на 40 больше, (сюда включаются и инвертированные )
	 */
	private void CreateInputElm(String[] varNames) {
		
		int minus = 0;
		int x = input_freeSpace.x;
		int y = input_freeSpace.y;
		
		freeSpace = new Point(x+AWL+varNames.length*100, y);
		StartPoint = new Point(x+AWL+varNames.length*100, y);
		NextStartPoint = new Point(x+AWL+varNames.length*100, y-45);
		
		//добавить кусок провода длиной в 50
		for(int i = 0; i<varNames.length; i++) {
					
			if(!dictionary.containsKey(varNames[i])) {
				
			ArrayList<CircuitElm> UnusedVar = new ArrayList<CircuitElm>(); //для удаления неиспользуемых входов схемы.
			
			minus += 40;
			
			CircuitElm newce = createCe("Linput",input_freeSpace.x+50,input_freeSpace.y,input_freeSpace.x,input_freeSpace.y, 0, 2);
			newce.setPoints();
			newce.getConnectionPoints(false);
			elmList.add(newce);
			
			CircuitElm newwire = createCe("Wire",newce.OperativePoints.get(0).x,newce.OperativePoints.get(0).y,freeSpace.x+100-minus-(53*dashCirc),newce.OperativePoints.get(0).y, 0, 2);
			newwire.setPoints();
			newwire.getConnectionPoints(true);
			elmList.add(newwire);
			
			dictionary.put(varNames[i], newwire);
			input_freeSpace.y += 100;
			
			UnusedVar.add(newwire);
			UnusedInputs.add(UnusedVar);
			inElems.add((SwitchElm)newce);
			
			minus += 40;
			
			CircuitElm wire = createCe("Wire",newce.OperativePoints.get(0).x, newce.OperativePoints.get(0).y, newce.OperativePoints.get(0).x, newce.OperativePoints.get(0).y+50, 0, 0);
			wire.setPoints();
			wire.getConnectionPoints(true);
			elmList.add(wire);
			
			CircuitElm inverted = createCe("Invertor",newce.OperativePoints.get(0).x, newce.OperativePoints.get(0).y+50, newce.OperativePoints.get(0).x+100, newce.OperativePoints.get(0).y+50, 0, 0);
			inverted.setPoints();
			inverted.getConnectionPoints(true);
			elmList.add(inverted);
			
			CircuitElm wire2 = createCe("Wire",inverted.OperativePoints.get(1).x,inverted.OperativePoints.get(1).y,freeSpace.x+100-minus-(53*dashCirc),inverted.OperativePoints.get(1).y, 0, 2);
			wire2.setPoints();
			wire2.getConnectionPoints(true);
			elmList.add(wire2);
			
			String InverseInput = "~"+varNames[i];
			
			dictionary.put(InverseInput, wire2);
			
			UnusedVar = new ArrayList<CircuitElm>();
			
			UnusedVar.add(wire);
			UnusedVar.add(inverted);
			UnusedVar.add(wire2);
			UnusedInputs.add(UnusedVar);
			
			UnusedVarNames.add(varNames[i]);
			UnusedVarNames.add(InverseInput);

		  }
			
		}
		
	}
		
	//Создаёт выходной элемент схемы на последнем лог. элементе
	private void CreateCircuitOutput() {
		
		//String lastBlock
		/*
		Point lastBlockOut = dictionary.get(lastBlock).OperativePoints.get(dictionary.get(lastBlock).OperativePoints.size()-1);
		
		//CircuitElm newce4 = createCe("Loutput",lastBlockOut.x+50, lastBlockOut.y, lastBlockOut.x+100, lastBlockOut.y, 0, 2);
		//newce4.setPoints();
		//newce4.getConnectionPoints(true);
		//elmList.add(newce4);
		
		CircuitElm newce4 = createCe("Platform",lastBlockOut.x+50, lastBlockOut.y, lastBlockOut.x+50, lastBlockOut.y, 0, 2);
		//CircuitElm newce4 = createCe("Platform",1200, lastBlockOut.y, 1200, lastBlockOut.y, 0, 2);
		newce4.setPoints();
		newce4.getConnectionPoints(true);
		elmList.add(newce4);
		
		outElems.add(newce4);
		wireSplitPoint = newce4.OperativePoints.get(0).x-20;
		ConnectElements(dictionary.get(lastBlock),newce4,lastBlock,false,false);
		*/
		

		int MaxX = 0;
		
		for (int m =0; m < LastLogElems.size(); m++){         
                if(MaxX < LastLogElems.get(m).OperativePoints.get(LastLogElems.get(m).OperativePoints.size()-1).x+50) {               
                	MaxX = LastLogElems.get(m).OperativePoints.get(LastLogElems.get(m).OperativePoints.size()-1).x+50;
                }
            
        }
		
		for(int i = 0; i < LastLogElems.size(); i++) {
			
			CircuitElm newce4 = createCe("Platform",MaxX, LastLogElems.get(i).y, MaxX, LastLogElems.get(i).y, 0, 2);
			newce4.setPoints();
			newce4.getConnectionPoints(true);
			elmList.add(newce4);
			
			outElems.add(newce4);
			wireSplitPoint = MaxX-20;
			ConnectElements(LastLogElems.get(i),newce4,"a",false,false);
			
		}
		
	}
	
	//Соединение двух элементов
  	private void ConnectElements(CircuitElm out, CircuitElm in, String outName, boolean alreadySplitted, boolean isInputElm) {

  		Point prevOutput = out.OperativePoints.get(out.OperativePoints.size()-1); //выход предыдущего элемента
  		int closestInputIndex = GetClosestInput(prevOutput, in); //поиск индекса ближайшего входа к выходу
  		Point currentInput = in.OperativePoints.get(closestInputIndex);

  		if(prevOutput.y != currentInput.y) {
  		
  			//Если в соединении есть узел
  			if(alreadySplitted) {
  				
  				//GWT.log("###########");
  				int diff1 = Math.abs(out.OperativePoints.get(out.OperativePoints.size()-1).y-currentInput.y);
  				int diff2 =		Math.abs(out.OperativePoints.get(0).y-currentInput.y);
  				
  				//Находим разность в высоте между входами элемента и позицией узла
  				//В зависимости от этого выбираем конец провода, от которого идём
  				//в случае их равенства предпочтение отдаётся точке начала провода (Индекс 0) это для else
  				
  			//В первом блоке обыгрывается ситуация, когда мы уже подключили вход к самому верхнему лог элементу первого каскада схемы, т.е. уже есть провод к минимальной точке по y схемы. 
  			// Раньше происходило наложение двух проводов друг на дуга в связи с тем, что не реализовано соединение простым контактом проводов (токо по прямым координатам)
  			// Теперь, текущий вертикальный провод мы дробим на 2, актуальный для дальнейшего соединения становится нижний кусок	
  				if(currentInput.y < out.OperativePoints.get(0).y && currentInput.y > out.OperativePoints.get(1).y) {
  					
  					CircuitElm wire1 = createCe("Wire",prevOutput.x, out.OperativePoints.get(0).y, prevOutput.x, currentInput.y, 0, 0);
  		  			wire1.setPoints();
  		  			wire1.getConnectionPoints(true);
  		  			elmList.add(wire1);
  					
  					CircuitElm wire2 = createCe("Wire",prevOutput.x, currentInput.y, currentInput.x, currentInput.y, 0, 0);
  					wire2.setPoints();
  		  			elmList.add(wire2);
  					
  					CircuitElm wire3 = createCe("Wire",prevOutput.x, currentInput.y, prevOutput.x, out.OperativePoints.get(1).y, 0, 0);
  					wire3.setPoints();
  		  			elmList.add(wire3);
  		  			
  		  			elmList.remove(out);
  		  			dictionary.replace(outName, wire1);
  					
  				}else {
  					
	  				if(diff1<diff2) {
	  					prevOutput = out.OperativePoints.get(out.OperativePoints.size()-1);
	  				}else if(diff1==diff2){
	  					prevOutput =   out.OperativePoints.get(0);	
	  				}
	  				else {
	  					prevOutput =   out.OperativePoints.get(0);					
	  				}
	  				
	  				CircuitElm wire2 = createCe("Wire",prevOutput.x, prevOutput.y, prevOutput.x, currentInput.y, 0, 0);
	  				wire2.setPoints();
	  				wire2.getConnectionPoints(true);
		  			elmList.add(wire2);
		  			
		  			CircuitElm wire3 = createCe("Wire",prevOutput.x, currentInput.y, currentInput.x, currentInput.y, 0, 0);
		  			wire3.setPoints();
		  			elmList.add(wire3);
	  				
		  			dictionary.replace(outName, wire2);
	  			
  				}
  				
  				
  			}else {
  				
  				//Если нет узла
  				
  				int diap = 0;
  				
  				//Если это входной элемент схемы
  				if(isInputElm) { 
  					  					
  					CircuitElm wire2 = createCe("Wire",prevOutput.x, prevOutput.y, prevOutput.x, currentInput.y, 0, 0);
  					wire2.setPoints();
  					wire2.getConnectionPoints(true);
  		  			elmList.add(wire2);
  		  			
  		  			
  		  			CircuitElm wire3 = createCe("Wire",prevOutput.x, currentInput.y, currentInput.x, currentInput.y, 0, 0);
  		  			wire3.setPoints();
  		  			elmList.add(wire3);	
  		  			
  		  			dictionary.replace(outName, wire2);
  					
  				}
  				else { 
  				  					  					
  					diap = wireSplitPoint;
	  		  		
		  			CircuitElm wire1 = createCe("Wire",prevOutput.x, prevOutput.y, diap, prevOutput.y, 0, 0);
		  			wire1.setPoints();
		  			elmList.add(wire1);
		  			
		  			
		  			CircuitElm wire2 = createCe("Wire",diap, prevOutput.y, diap, currentInput.y, 0, 0);
		  			wire2.setPoints();
		  			wire2.getConnectionPoints(true);
		  			elmList.add(wire2);
		  			
		  			
		  			CircuitElm wire3 = createCe("Wire",diap, currentInput.y, currentInput.x, currentInput.y, 0, 0);
		  			wire3.setPoints();
		  			elmList.add(wire3);
	  		  		
		  			dictionary.replace(outName, wire2);
		  			
  				}

  			}
  		
  			if(in.OperativePoints.size()>2) {	
  				in.OperativePoints.remove(closestInputIndex);
  			}
  			
  		}
  		else { 
  			//Если выход и вход на одной линии
  			//Разделим данное соединение на 2, для случая, когда выходной элемент на одной линии с входным + он же коннектится с третьим
  			//обавить рандомную x координату для сегментации
  			
				if(wireSplitPoint == currentInput.x) {
					wireSplitPoint -= 20;		
				}
  			
		  		CircuitElm newce1 = createCe("Wire",prevOutput.x, prevOutput.y, wireSplitPoint, prevOutput.y, 0, 0);
				newce1.setPoints();
				newce1.getConnectionPoints(true);
				elmList.add(newce1);
				
				
				CircuitElm newce2 = createCe("Wire",wireSplitPoint, prevOutput.y, currentInput.x, currentInput.y, 0, 0);
				newce2.setPoints();
				newce2.getConnectionPoints(true);
				elmList.add(newce2);
			

				dictionary.replace(outName, newce2);
				
				if(in.OperativePoints.size()>1) {
	  				in.OperativePoints.remove(closestInputIndex);
	  			}
  			
  		}
		
  	}
	
  	private void DeleteUnusedInputs() {

		log.append("unused vars ").append(UnusedVarNames.toString()).append(nl);
  		
  		if(UnusedVarNames.size()>0) {
  		
	  		for(int i = 0; i<UnusedVarNames.size(); i++) {
	  		
	  			String p = UnusedVarNames.get(i);
	  			if(UnusedVarNames.get(i).charAt(0) =='~') {
	  				p = p.substring(2,p.length());
	  			}else {
	  				p = p.substring(1,p.length());
	  			}

	  			log.append(p).append(nl);
	  			
		  		int IndexToDelete = ((Integer.parseInt(p))-1)*2;
		  		
		  		if(UnusedVarNames.get(i).charAt(0) =='~') {
		  			IndexToDelete +=1;
		  		}  		
		  		
	  			for(int j = 0; j<UnusedInputs.get(IndexToDelete).size(); j++) {
	  				
	  				elmList.remove(UnusedInputs.get(IndexToDelete).get(j));
	  				
	  			}
		  		
	  		}
  		
  		}
  		
  	}
  	
  	//Индекс входа ближайший к выходу
  	private Integer GetClosestInput(Point out, CircuitElm in) {
  		int index = 0;
  		/*
  		
  		Point point = in.OperativePoints.get(0);
  		
  		int delta = Math.abs(out.y-point.y);
  		
  		for(int i = 1; i<in.OperativePoints.size()-1; i++) {
  			
  			int j = i;
  			int currDelta = Math.abs(out.y-in.OperativePoints.get(i).y);
  					
  			if(currDelta < delta) {
  				index = j;
  				point = in.OperativePoints.get(i);
  				delta = currDelta;
  			}else {}
  			
  		}
  		
  		return index;
  		*/
  		index = (in.OperativePoints.size()-2);
  		if(index<0) {index=0;}
  		return index;
  	}
  	
  	private CircuitElm createCe(String marker, int x1, int y1, int x2, int y2, int f, int inputcount) {
    	
    	if(marker.equals("Wire")) {
    		return new WireElm(x1, y1, x2, y2, f);
    	}
    	if(marker.equals("*")) {
        	return new AndGateElm(x1, y1, x2, y2, f, inputcount);
        }
    	if(marker.equals("Linput")) {
    		return new LogicInputElm(x1, y1, x2, y2, f);
    	}
    	if(marker.equals("Loutput")) {
    		return new LogicOutputElm(x1, y1, x2, y2, f);
    	}
    	if(marker.equals("+")) {
    		return new OrGateElm(x1, y1, x2, y2, f, inputcount);
    	}
    	if(marker.equals("Xor")) {
    		return new XorGateElm(x1, y1, x2, y2, f, inputcount);
    	}
    	if(marker.equals("Nor")) {
    		return new NorGateElm(x1, y1, x2, y2, f, inputcount);
    	}
    	if(marker.equals("Nand")) {
    		return new NandGateElm(x1, y1, x2, y2, f, inputcount);
    	}
    	if(marker.equals("Invertor")) {
    		return new InverterElm(x1, y1, x2, y2, f);
    	}
    	if(marker.equals("Platform")) {
    		return new Platform(x1, y1, x2, y2, f);
    	}
    	else {return null;}
    	
    }
	
  	private int random(int min, int max)
  	{
  		max -= min;
  		return (int) (Math.random() * ++max) + min;
  	}
  
  	private boolean RandomOf100(int percent) {
  		
  		int max = 100;
  		
  		int p = (int) (Math.random() * ++max) + 0;
  		
  		if(p<=percent) {
  			return true;
  		}else {return false;}
  		
  	}

	public StringBuilder getLog() {
		return log;
	}
}
