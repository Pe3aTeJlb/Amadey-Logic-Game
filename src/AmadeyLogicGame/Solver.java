/*    
Copyright (C) Luis Paulo Laus
	Modified by Pplos Studio
    
    This file is a part of Avrora Logic Game, which based on CircuitJS1
    https://github.com/Pe3aTeJlb/Avrora-logic-game
    
    JQM Java Quine McCluskey
    https://sourceforge.net/projects/jqm-java-quine-mccluskey/
    
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
import java.util.Arrays;
import java.util.Collections;

public class Solver implements Runnable {

    private static final int MAX_VAR_MEM = 10;       // used to allocate memory in an amount sufficient to guaranty this number of input variables, note that 10 variables requites 59.049 registries and 16 variables, 43.046.721

    private final char[][] values;

    private  int number_of_in_var;
    private final int number_of_out_var;

    private final String[] in_var_names;
    //private final String[] out_var_names;

    private final boolean sum_of_products_not_product_of_sums;
    private final boolean all_possible_not_just_one;
    private final boolean optimize_number_of_terms_not_variables;

    private final StringBuilder solution;
    private ArrayList<Solutions> solutions;


    public Solver(
            char[][] values,
            int number_of_in_var,
            String[] in_var_names,
            int number_of_out_var,
            String[] out_var_names,
            boolean sum_of_products_not_product_of_sums,
            boolean all_possible_not_just_one,
            boolean optimize_number_of_terms_not_variables
    ) {
        this.values = values;
        this.number_of_in_var = number_of_in_var;
        this.in_var_names = in_var_names;
        this.number_of_out_var = number_of_out_var;
        //this.out_var_names = out_var_names;
        this.sum_of_products_not_product_of_sums = sum_of_products_not_product_of_sums;
        this.all_possible_not_just_one = all_possible_not_just_one;
        this.optimize_number_of_terms_not_variables = optimize_number_of_terms_not_variables;
        solution = new StringBuilder();

    }

    @Override
    public void run() {
        Solve();
        Implicant.startExpression(number_of_in_var, in_var_names);
        Format();
    }

    public void Solve() {

        solutions = new ArrayList<Solutions>(number_of_out_var);

        int maxs; // maximum number of implicants
        Implicant [] lstPrimes;


        for (int f = 0; f < number_of_out_var; f++) {

            ArrayList<Integer> lstOnes = new ArrayList<>(1 << number_of_in_var);
            solutions.add(new Solutions());
            if (number_of_in_var > MAX_VAR_MEM) {
                maxs = (int)Math.pow(3, MAX_VAR_MEM);
            }
            else {
                maxs = (int)Math.pow(3, number_of_in_var);
            }
            lstPrimes = new Implicant [maxs];
            // fill arrays with data from the truth table
            int mx = 1 << number_of_in_var;
            //int mx =  number_of_in_var;
            int ct1 = 0;   // number of ones
            int ctdc1 = 0;  // mumber of dont care and one
            for (int i = 0; i < mx; i++) {
                if (sum_of_products_not_product_of_sums) {
                    if (values[i][f] == '1') {
                        lstOnes.add(i);
                        ct1++;
                    }
                    if (values[i][f] != '0') {
                        lstPrimes[ctdc1] = new Implicant(i);
                        ctdc1++;
                    }
                } else {
                    if (values[i][f] == '0') {
                        lstOnes.add(i);
                        ct1++;
                    }
                    if (values[i][f] != '1') {
                        lstPrimes[ctdc1] = new Implicant(i);
                        ctdc1++;
                    }
                }
            }
            if ((ctdc1 == mx) || (ct1 == 0)) {
                continue;
            }

            // sort the list of implicants by the number of ones, then value
            Arrays.sort(lstPrimes, 0, ctdc1, (Implicant r1, Implicant r2) ->
            {
                int r1b = r1.bitCount_v();
                int r2b = r2.bitCount_v();
                if(r1b != r2b)
                    return r1b - r2b;
                return r1.getV() - r2.getV();
            });
            // begin and end of each sublist
            int [] e = new int [number_of_in_var + 2];
            int epos = 1;
            int ct = 0;
            e[0] = 0;
            for(int i = 0; i < ctdc1; i++) {
                int ctt = lstPrimes[i].bitCount_v();
                while(ctt > ct) {
                    ct++;
                    e[epos] = i;
                    epos++;
                }
            }
            e[epos] = ctdc1;

            //t1Time[f] = System.nanoTime() - startTime ; // time test
//            System.out.println("Sorting time: " + (endTime - startTime) + " for " + number_of_in_var + " variables");

            boolean grouped = true;
            ct = number_of_in_var;
            while (grouped) {
            	
                grouped = false;
                
                for(int i = 0; i < ct; i++) {
                	
                    if(e[i] == e[i+1])
                        continue;
                    int newstart = ctdc1;
//                    System.out.println("at " + i + " from " + e[i] + " to " + (e[i+1]-1) + " and from " + e[i+1] + " to " + (e[i+2]-1));
                    
                    for(int j = e[i]; j < e[i+1]; j++) {
                        Implicant tempj = lstPrimes[j];
                        
                        for(int k = e[i+1]; k < e[i+1]; k++) { //!!!!!!!!!!!!!!!!!!!!!! i+2
                            if(tempj.getM() == lstPrimes[k].getM()) { // same mask?
                                int d = tempj.getV() ^ lstPrimes[k].getV();
                                if (Integer.bitCount(d) == 1) { // Hamming distance is 1?
                                    Implicant np = new Implicant(tempj.getV(), tempj.getM() | d);
                                    // look for this prime implicant in the already found list of
                                    // prime implicants, but only in this section of the list
/*
                                    // linear search
                                    int x;
                                    for (x = newstart; x < ctdc1; x++) {
                                        if (np.equals(lstPrimes[x])) {
                                            break;
                                        }
                                    }
                                    if(x == ctdc1) { // new prime implicant found
                                            if (maxs == ctdc1)
                                                throw new OutOfMemoryError(java.util.ResourceBundle.getBundle("QMC").getString("LSTPRIME"));

                                            lstPrimes[ctdc1] = np;
                                            ctdc1++;
                                    }
*/
                                    d = Arrays.binarySearch(lstPrimes, newstart, ctdc1, np, (Implicant r1, Implicant r2) ->
                                    {
                                        if(r1 != null && r2 != null) {
                                    		return  (((r1.getV() - r2.getV()) << Implicant.MAX_IN_VAR) + r1.getM() - r2.getM());
                                        }else {System.out.println("Shit is null"); return 2;}
                                    });
//                                    System.out.print("trying " + tempj + " with " + lstPrimes[k] + " " + d);
                                    if(d < 0) { // new prime implicant found
                                        if (maxs == ctdc1)
                                        	//throw new 
                                           // throw new OutOfMemoryError(java.util.ResourceBundle.getBundle("QMC").getString("LSTPRIME"));

                                        lstPrimes[ctdc1] = np;
                                        ctdc1++;
                                        // sort again, no big deal because the list is already sorted with the exception of the new element
                                        Arrays.sort(lstPrimes, newstart, ctdc1, (Implicant r1, Implicant r2) ->
                                        {
                                            if(r1 != null && r2 != null) {
                                        		return  (((r1.getV() - r2.getV()) << Implicant.MAX_IN_VAR) + r1.getM() - r2.getM());
                                            }else {System.out.println("Shit is null"); return 2;}
                                        });
//                                            System.out.println(" OK " + ctdc1);
                                    }
//                                    else
//                                        System.out.println("");
                                    tempj.setC(true);
                                    lstPrimes[k].setC(true);
                                    grouped = true;
                                }
                            }
                        }
                    }
                    e[i] = newstart;
                }
                e[ct] = ctdc1;
                ct--;
            }
//            System.out.println("Implicantes " + ctdc1);
            // list all prime implicants
            ArrayList<Implicant> lstPrime = new ArrayList<>(number_of_in_var);
            for(int i = ctdc1-1; i >= 0; i--)
                if(lstPrimes[i].isPrime())
                    lstPrime.add(lstPrimes[i]);
            //t2Time[f] = System.nanoTime() - startTime ; // time test

            ArrayList<Implicant> lstPrimeEssentials = new ArrayList<>(lstOnes.size());

            for (int i = 0; i < lstOnes.size(); i++) {
                int pos = -1;
                int j;
                for (j = 0; j < lstPrime.size(); j++) {
//                    System.out.println("("+i+","+j+") "+lstPrime.get(j) + " " + lstOnes.get(i) + " " + lstPrime.get(j).isTrue(lstOnes.get(i)));
                    if (lstPrime.get(j).isTrue(lstOnes.get(i))) {
                        if (pos >= 0) {
                            break;
                        }
                        pos = j;
                    }
                }
                if ((j == lstPrime.size()) && (pos >= 0)) {
                    Implicant epi = lstPrime.remove(pos);
                    // remove all ones covered by the essential prime implicant
                    for (j = lstOnes.size() - 1; j >= 0; j--) {
                        if (epi.isTrue(lstOnes.get(j))) {
                            lstOnes.remove(j);
                        }
                    }
                    lstPrimeEssentials.add(epi);
                    i = -1;
                }
            }

            // register solution (up to now)
            solutions.get(f).setEssentialPI(lstPrimeEssentials);
            // if there is any prime implicant left, Petrick's Method
            // is used to find the minimum solution(s)
            if ((lstPrime.size() > 0) && (lstOnes.size() > 0)) {
               // if (lstPrime.size() > Long.SIZE) {
                    //throw new OutOfMemoryError(resourceBundle.getString("NONESSENTIALPRIMES"));
                //}

                ArrayList<Long> M0 = new ArrayList<>(lstPrime.size());
                ArrayList<Long> M1 = new ArrayList<>(lstPrime.size());

                for (int i = 0; i < lstPrime.size(); i++) {
                    if (lstPrime.get(i).isTrue(lstOnes.get(0))) {
                        M0.add(1L << i);
                    }
                }
                for (int k = 1; k < lstOnes.size(); k++) {
                    M1.clear();
                    for (int i = 0; i < lstPrime.size(); i++) {
                        if (lstPrime.get(i).isTrue(lstOnes.get(k))) {
                            M1.add(1L << i);
                        }
                    }
                    M0 = mul(M0, M1);
                }
//                System.out.println("Expressões: " + M0.size());
                // compute weights according to optimization criterion
                M1.clear();
                long min = Long.MAX_VALUE;
                for (int j = 0; j < M0.size(); j++) {
                    long cr = 0L;
                    for (int i = 0; i < lstPrime.size(); i++) {
                        if ((M0.get(j) & (1L << i)) != 0L) {
                            // prime i belongs to this solution
                            cr += optimize_number_of_terms_not_variables
                                    ? (1L << Implicant.MAX_IN_VAR) + number_of_in_var - lstPrime.get(i).bitCount_m()
                                    : (((long) (number_of_in_var - lstPrime.get(i).bitCount_m())) << Implicant.MAX_IN_VAR) + 1L;
                        }
                    }
                    M1.add(cr);
                    if (cr < min) {
                        min = cr;
                    }
                }
                // remove solution worst than optimum
                for (int j = M0.size() - 1; j >= 0; j--) {
                    if (M1.get(j) > min) {
                        M0.remove(j);
                    }
                }
                // save solution as a list of prime implicants
                if (all_possible_not_just_one) {
                    solutions.get(f).setPIsize(M0.size());
                    for (int j = 0; j < M0.size(); j++) {
                        solutions.get(f).addSolution(Long.bitCount(M0.get(j)));
                        for (int i = 0; i < lstPrime.size(); i++) {
                            if ((M0.get(j) & (1L << i)) != 0L) {
                                // prime i belongs to this solution
                                solutions.get(f).addPI(j, lstPrime.get(i));
                            }
                        }
                    }
                } else {
                    solutions.get(f).setPIsize(1);
                    solutions.get(f).addSolution(Long.bitCount(M0.get(0)));
                    for (int i = 0; i < lstPrime.size(); i++) {
                        if ((M0.get(0) & (1L << i)) != 0L) {
                            // prime i belongs to this solution
                            solutions.get(f).addPI(0, lstPrime.get(i));
                        }
                    }
                }
            }
            //t3Time[f] = System.nanoTime() - startTime ; // time test
        }
    }

    public ArrayList<Long> mul(ArrayList<Long> a, ArrayList<Long> b) {
        ArrayList<Long> v = new ArrayList<>(10);
        for (int i = 0; i < a.size(); i++) {
            for (int j = 0; j < b.size(); j++) {
                long z = a.get(i) | b.get(j);  // expand "and" over "or"
                if (!v.contains(z)) {
                    v.add(z);
                }
            }
        }
//        Collections.sort(v);
        for (int i = 0; i < v.size() - 1; i++) {
            for (int j = v.size() - 1; j > i; j--) {
                long z = v.get(i) & v.get(j);
                if (z == v.get(i)) {
                    v.remove(j);
                } else if (z == v.get(j)) {
                    v.set(i, z);
                    v.remove(j);
                    j = v.size();
                }
            }
        }
        return v;
    }

    public void Format(){

        for (int f = 0; f < number_of_out_var; f++) {

            ArrayList<Implicant> epi = solutions.get(f).getEssentialsPI();

            if (epi == null) {
                //solution.append(out_var_names[f]);
                continue;
            }
            if (sum_of_products_not_product_of_sums) {
                Collections.sort(epi);
            }
            else{
                Collections.sort(epi, (Implicant r1, Implicant r2) ->
                {
                    int r1b = r1.bitCount_m();
                    int r2b = r2.bitCount_m();
                    if(r1b != r2b)
                        return r2b - r1b;
                    r1b = r1.getM();
                    r2b = r2.getM();
                    if(r1b != r2b)
                        return r1b - r2b;
                    return r1.getV() - r2.getV();
                });
            }

            StringBuilder solutioneq = new StringBuilder();
            boolean fl = false;
            ArrayList<ArrayList<Implicant>> primeI = solutions.get(f).getPrimeI();
           // solutioneq.append(out_var_names[f]);
          //  solutioneq.append(" = ");

            for (int j = 0; j < epi.size(); j++) {
                if (sum_of_products_not_product_of_sums) {
                    if (fl)
                        solutioneq.append(" + ");
                        solutioneq.append(epi.get(j).toExpressionProd());
                } else {
                    if (fl)
                        solutioneq.append(" * ");
                    if (((number_of_in_var - epi.get(j).bitCount_m()) > 1) &&
                            ((epi.size() > 1) || (primeI != null))) {
                        solutioneq.append('(');
                        solutioneq.append(epi.get(j).toExpressionSum());
                        solutioneq.append(')');
                    } else {
                        solutioneq.append(epi.get(j).toExpressionSum());
                    }
                }
                fl = true;
            }

            int mx;
            if (primeI == null) {
                mx = 1;
            } else {
                mx = primeI.size();
            }

            for (int i = 0; i < mx; i++) {
                solution.append(solutioneq);
                boolean fll = fl;
                if (primeI != null) {
                    if (sum_of_products_not_product_of_sums) {
                        Collections.sort(primeI.get(i));
                    }
                    else{
                        Collections.sort(primeI.get(i), (Implicant r1, Implicant r2) ->
                        {
                            int r1b = r1.bitCount_m();
                            int r2b = r2.bitCount_m();
                            if(r1b != r2b)
                                return r2b - r1b;
                            r1b = r1.getM();
                            r2b = r2.getM();
                            if(r1b != r2b)
                                return r1b - r2b;
                            return r1.getV() - r2.getV();
                        });
                    }

                    for (int j = 0; j < primeI.get(i).size(); j++) {
                        if (sum_of_products_not_product_of_sums) {
                            if (fll)
                                solution.append(" + ");
                                solution.append(primeI.get(i).get(j).toExpressionProd());
                        } else {
                            if (fll)
                                solution.append(" * ");
                            if (((number_of_in_var - primeI.get(i).get(j).bitCount_m()) > 1) &&
                                    ((fl) || (primeI.get(i).size() > 1))){
                                solution.append('(');
                                solution.append(primeI.get(i).get(j).toExpressionSum());
                                solution.append(')');
                            } else {
                                solution.append(primeI.get(i).get(j).toExpressionSum());
                            }
                        }
                        fll = true;
                    }

                }
            }
            solution.append("\n");
        }

    }

    public String getSolution() {
        return solution.toString();
    }

}
