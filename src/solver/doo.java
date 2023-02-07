package solver;
    
import util.Distribution;
import util.MatrixX;

import java.util.ArrayList;
import java.util.HashMap;

public class doo {
    
    public static ArrayList<Integer> actionsP1 = new ArrayList<Integer>();
    public static ArrayList<Integer> actionsP2 = new ArrayList<Integer>();

    public static double finalValue = 0.0;
    public static HashMap<Integer,Distribution<Integer>> strategyP1 = new HashMap<>();
    public static HashMap<Integer,Distribution<Integer>> strategyP2 = new HashMap<>();

    private int nbActionsP1;
    private int nbActionsP2;

    double[][] m;

    double t = 1.0;
    double optValue;

    public doo(double[][] m, double holderianConstantP1,double holderianConstantP2, double epsilonExt, double epsilonInt){

        this.m = m;

        int nbActionsP1 = 3;
        int nbActionsP2 = 3;
        this.nbActionsP1=nbActionsP1;
        this.nbActionsP2=nbActionsP2;

        for (int k=0; k<nbActionsP1;k++){
            this.actionsP1.add(k);
        }

        for (int k=0; k<nbActionsP2;k++){
            this.actionsP2.add(k);
        }

        int dimJ1 = 1;
        int dimJ2 = 1;

        ArrayList<ArrayList<Double>> dJ1 = new ArrayList<>();
        ArrayList<ArrayList<Double>> dJ2 = new ArrayList<>();
        ArrayList<Double> a = new ArrayList<>();

        a.add(0.0);
        a.add(1.0);
        for (int i = 0;i<nbActionsP1;i++){
            dJ1.add(new ArrayList<>(a));
        }
        for (int i = 0;i<nbActionsP2;i++){
            dJ2.add(new ArrayList<>(a));
        }

        HashMap<Integer,ArrayList<ArrayList<Double>>> temp = new HashMap<>();
        HashMap<Integer,ArrayList<ArrayList<Double>>> tempJ2 = new HashMap<>();

        for (int k=0;k<dimJ1;k++){
            temp.put(k,new ArrayList<ArrayList<Double>>(dJ1));
        }
        
        for (int k=0;k<dimJ2;k++){
            tempJ2.put(k,new ArrayList<ArrayList<Double>>(dJ2));
        }

        HashMap<HashMap<Integer,ArrayList<ArrayList<Double>>>,Double> h = new HashMap<>();
        h.put(temp,-1.0);
        HashMap<HashMap<Integer,ArrayList<ArrayList<Double>>>,Double> arbre = new HashMap();
        //arbre.put( new HashMap<Integer,ArrayList<ArrayList<Double>>>(temp),-1.0);
        arbre.put( new HashMap<Integer,ArrayList<ArrayList<Double>>>(tempJ2),-1.0);
        //System.out.println("DOObackup::temp : " + temp.toString());
        try{
        this.DOOexterne(h,arbre,dJ1.size(),dJ2.size(),2,holderianConstantP1,holderianConstantP2,epsilonExt,epsilonInt);
        }
        catch(Exception e){
            e.printStackTrace();
            System.out.println("Exception raised, optimization failed");
        }
    }

    public double f(ArrayList<Double> x,ArrayList<Double> y) throws Exception {
        if (x.size() != y.size()){
            throw new Exception("x and y have different dimension!");
        }
        double S = 0;
        for (int i = 0; i<x.size();i++){
            S+= x.get(i) + y.get(i);
        }
        return S;
    }

    public double fonctionRecompense(HashMap<Integer,ArrayList<Double>> x, HashMap<Integer,ArrayList<Double>> y,
                                        boolean show) throws Exception {
        //System.out.println("x : " + x.toString());
        //System.out.println("y : " + y.toString());
        /*double value = 0.0;

        value = x.get(0).get(0)  + y.get(0).get(0) - 2* x.get(0).get(0)  + y.get(0).get(0);

        double entropieX =  -(x.get(0).get(0)*Math.log(x.get(0).get(0)) + x.get(0).get(1)*Math.log(x.get(0).get(1)));
        double entropieY = -(y.get(0).get(0)*Math.log(y.get(0).get(0)) + y.get(0).get(1)*Math.log(y.get(0).get(1)));

        value -= 0.5*(entropieX+entropieY);
        return -value;*/
        double S = 0.0;
        for (int i = 0;i<this.nbActionsP1;i++){
            for (int j = 0;j<this.nbActionsP2;j++){
                S+= x.get(0).get(i)*y.get(0).get(j)*this.m[i][j];
            }
        }
        return S;
    }

    public double f(HashMap<Integer,ArrayList<Double>> x, HashMap<Integer,ArrayList<Double>> y, boolean show) throws Exception {
        return -fonctionRecompense(x,y, show);
    }

    public double fExt(HashMap<Integer,ArrayList<ArrayList<Double>>> x,
                       HashMap<HashMap<Integer,ArrayList<ArrayList<Double>>>,Double> subsInt,
                       int dim, int partionnement, double Lambda, double epsilonInt, boolean show) throws Exception {

    //return 0;
        return -DOOinterne(getValidProbability(x),subsInt,dim,partionnement,Lambda,epsilonInt,show);

    }

    public static ArrayList<Double> linspace(double min, double max, int points) {
        ArrayList<Double> d = new ArrayList<Double>();
        for (int i = 0; i < points; i++){
            d.add(i,min + i * (max - min) / (points - 1));
        }
        return d;
    }

    public boolean IsInSimplexeDimensionNM1(ArrayList<ArrayList<Double>> subdivision){
        double S = 0.0;
        for (ArrayList<Double> subsInDimensions : subdivision){
            if (subsInDimensions.get(0)<0.0 || subsInDimensions.get(1)>1.0){
                return false;
            }
            S += subsInDimensions.get(0);
        }
        if (S>=1){
            //System.out.println(" not in simplex : " + subdivision + "sum coords : " + S + " Norme inf : " + NormeInf((subdivision)));
            return false;
        }
        //System.out.println("not keeping : " + subdivision.toString() + " because, value : " + (S - subdivision.size()*NormeInf(subdivision)));
        return true;
    }

    public boolean isProbability(ArrayList<Double> x){
        double S = 0.0;
        for (Double d : x){
            S+= d;
        }
        if (S < 0.98  || S>1.02){
            //System.out.println("not a probability : "+x);
            System.out.println("sum : " + S);
	    System.exit(1);
	    return false;
	}
        //return (S==1);
    	return true;
    }
    public boolean IsInSimplexeDimensionN(ArrayList<ArrayList<Double>> subdivision){
        //return true;
        double Sinf = 0.0;
        double Ssup = 0.0;
        for (ArrayList<Double> subsInDimensions : subdivision){
            if (subsInDimensions.get(0)<0.0 || subsInDimensions.get(1)>1.0){
                return false;
            }
            Sinf += subsInDimensions.get(0);
            Ssup += subsInDimensions.get(1);
        }
        if (Sinf <1 && Ssup>=1){
            //System.out.println("I keep: " + subdivision);
            //this.isProbability(milieu(subdivision));
            return true;
        }
        if (Ssup<1){
            //System.out.println("coin droite trop petit : "+ subdivision);
        }
        //System.out.println(" not in simplex : " + subdivision + "sum coords : " + Sinf + " Norme inf : " + NormeInf((subdivision)));
        //System.out.println("not keeping : " + subdivision.toString() + " because, value : " + (S - subdivision.size()*NormeInf(subdivision)));
        return false;
        
    }

    private double valAbs(Double d){
        if (d>0){
            return d;
        }
        return -d;
    }
    private double NormeInf(ArrayList<ArrayList<Double>> subdivision) {
        ArrayList<Double> milieu = this.milieu(subdivision);
        //System.out.println("norme inf de : " + subdivision.toString() + " : " + valAbs(subdivision.get(0).get(0) - milieu.get(0)));
        return 2*valAbs(subdivision.get(0).get(0) - milieu.get(0));
    }

    private ArrayList<Double> milieu(ArrayList<ArrayList<Double>> subdivision) {
        ArrayList<Double> milieu = new ArrayList<>();
        for (ArrayList<Double> list : subdivision){
            milieu.add(0.5*(list.get(1)+list.get(0)));
        }
        //System.out.println("milieu de : " + subdivision.toString() + " : " + milieu.toString());
	return milieu;
    }

    public ArrayList<ArrayList<ArrayList<Double>>> Subdiviser(ArrayList<ArrayList<Double>> ToBeSubdivised, int dimension, int partitionnement){
        partitionnement ++; //artificial use.
        ArrayList<Double> a = ToBeSubdivised.get(0);
        ArrayList<Double> domaine = this.linspace(a.get(0),a.get(1),partitionnement);
        ArrayList<ArrayList<ArrayList<Double>>> S = new ArrayList<>();
        for (int k=0;k<domaine.size()-1;k++) {
            ArrayList<ArrayList<Double>> dd = new ArrayList<>();
            ArrayList<Double> ddd = new ArrayList<>();
            ddd.add(domaine.get(k));
            ddd.add(domaine.get(k+1));
            dd.add(ddd);
            S.add(dd);
        }
        //System.out.println("Subdivising!");
        //System.out.println("S : "+ S.toString());
        int i = 1;
        while (i<dimension) {
            a = ToBeSubdivised.get(i);
            domaine = this.linspace(a.get(0), a.get(1), partitionnement);
            ArrayList<ArrayList<ArrayList<Double>>> SS = new ArrayList<>();
            for (int indice =0;indice<S.size();indice++){
                ArrayList<ArrayList<Double>> temp = new ArrayList<>(S.get(indice));
                for (int elt=0; elt<domaine.size()-1;elt++){
                    ArrayList<ArrayList<Double>> tempBis = new ArrayList<>(temp);
                    //tempBis.add([domaine[elt], domaine[elt + 1]])
                    ArrayList<Double> toBeAdded = new ArrayList<>();
                    toBeAdded.add(domaine.get(elt));
                    toBeAdded.add(domaine.get(elt+1));
                    tempBis.add(toBeAdded);
                    SS.add(tempBis);
                }
            }
            S = SS;
            i++;
        }
        ArrayList<ArrayList<ArrayList<Double>>> ListSuppSimplexe = new ArrayList<>();
        for (ArrayList<ArrayList<Double>> carapuce : S) {
            if (!this.IsInSimplexeDimensionN(carapuce)) {
                //System.out.println("Not keeping:"+carapuce.toString());
                ListSuppSimplexe.add(carapuce);
            }
        }
        for (ArrayList<ArrayList<Double>> salameche : ListSuppSimplexe) {
            S.remove(salameche);
        }
        //System.out.println("done subdivising");
        //System.out.println("Returning : "+S.toString() + "\n taille : "+ S.size());
        return S;
    }

    public ArrayList<HashMap<Integer,ArrayList<ArrayList<Double>>>>
    SubdiviserPdtCartesien(HashMap<Integer,ArrayList<ArrayList<Double>>> toBeSubdivised, int dimension, int partitionnement){

        //partitionnement ++;
        //System.out.println(toBeSubdivised.toString());
        ArrayList<ArrayList<ArrayList<Double>>> a = Subdiviser(toBeSubdivised.get(toBeSubdivised.keySet().iterator().next()),dimension,partitionnement);
        ArrayList<HashMap<Integer,ArrayList<ArrayList<Double>>>> S = new ArrayList<>();
        Integer o1 = toBeSubdivised.keySet().iterator().next();
        //System.out.println("a : "+a.toString());
        for (ArrayList<ArrayList<Double>> temp : a) {//parcours de toutes les subdivisions possible pour le premier simplexe.
            HashMap<Integer, ArrayList<ArrayList<Double>>> tempHashmap = new HashMap<>();
            tempHashmap.put(o1,temp);
            S.add(tempHashmap);
        }
        int i = 1;
        HashMap<Integer,ArrayList<ArrayList<Double>>> b = new HashMap<>();
        //System.out.println("i = 1, S : " + S.toString());
        while (i<toBeSubdivised.size()){
            Integer o = (Integer) toBeSubdivised.keySet().toArray()[i];
            a = Subdiviser(toBeSubdivised.get(o),dimension,partitionnement);
            ArrayList<HashMap<Integer,ArrayList<ArrayList<Double>>>> SS = new ArrayList<>();
            for (int indice =0;indice<S.size();indice++){
                b = S.get(indice);//b is a possible subdivision for i first simplexes. To be completed for the other simplexes.
                HashMap<Integer,ArrayList<ArrayList<Double>>> temp = new HashMap<>(b);
                for (ArrayList<ArrayList<Double>> temp1 : a) {
                    HashMap<Integer, ArrayList<ArrayList<Double>>> tempHashmap = new HashMap<>(temp);
                    tempHashmap.put(o,temp1);
                    SS.add(tempHashmap);
                }
            }
            S = SS;
            i++;
        }
        return S;
    }


    public double DOOinterne(HashMap<Integer,ArrayList<Double>> x,
                             HashMap<HashMap<Integer,ArrayList<ArrayList<Double>>>,Double> subsInt
            ,int dim, int partitionnement, double Lambda, double epsilon, boolean show) throws Exception {

        int N = 0;
        double maj = 100000;
        double minorant = -100000;
        double valYmax = -100000;
        
	HashMap<Integer,ArrayList<Double>> Ymax = this.getValidProbability(subsInt.entrySet().iterator().next().getKey());
        HashMap<Integer,ArrayList<ArrayList<Double>>> subYmax = new HashMap<>(subsInt.entrySet().iterator().next().getKey());
        HashMap<HashMap<Integer,ArrayList<ArrayList<Double>>>,Double> listSubsErased = new HashMap<>();
        subsInt.replace(subsInt.entrySet().iterator().next().getKey(),f(x,Ymax,false)+Lambda*NormeInf(subYmax));
        
	while (valAbs(maj - f(x,Ymax,false))> epsilon){
            //System.out.println("Nint : "+N+"maj : "+maj+"diff : "+valAbs(maj-f(x,Ymax,false))+"xmax : "+Ymax);
            //ArrayList<Double> b = new ArrayList<>();
            HashMap<Integer,ArrayList<ArrayList<Double>>> bestSubsToSubdivise = new HashMap<>();
            int argmax = -1;
            double valueArgmax = -10000000;
            int indice=0;
            ArrayList<HashMap<Integer,ArrayList<ArrayList<Double>>>> toBeSupressed = new ArrayList<>();
            for (HashMap<Integer,ArrayList<ArrayList<Double>>> subsDimension : subsInt.keySet()){//adds new values and get the argmax.
                
		double valueSubsDimension = f(x,this.getValidProbability(subsDimension),false) + Lambda*NormeInf(subsDimension);
                subsInt.replace(subsDimension,valueSubsDimension);
                if (true||valueSubsDimension+Lambda*NormeInf(subsDimension)>=minorant) {
                    if (valueSubsDimension > valueArgmax) {
                        bestSubsToSubdivise = new HashMap<>(subsDimension);
                        valueArgmax = valueSubsDimension;
                    }
                }
                else{
                    System.exit(1);
		    toBeSupressed.add(subsDimension);
                }
                indice ++;
            }
            for (HashMap<Integer,ArrayList<ArrayList<Double>>> tmpSupressed : toBeSupressed){
                subsInt.remove(tmpSupressed);
            }
            ArrayList<HashMap<Integer,ArrayList<ArrayList<Double>>>> listNewSubdivisions = new ArrayList<>();
	    listNewSubdivisions = SubdiviserPdtCartesien(bestSubsToSubdivise,dim,partitionnement);
            listSubsErased.put(bestSubsToSubdivise,valueArgmax);
            subsInt.remove(bestSubsToSubdivise);
            double value;
            for (HashMap<Integer,ArrayList<ArrayList<Double>>> newSubToAdd : listNewSubdivisions){
                value = f(x,this.getValidProbability(newSubToAdd),false) + Lambda * NormeInf(newSubToAdd);
                subsInt.put(newSubToAdd,value);
            }
            Ymax = new HashMap<>(this.getValidProbability(subsInt.entrySet().iterator().next().getKey()));
            valYmax = f(x,Ymax,false)-Lambda*NormeInf(subsInt.entrySet().iterator().next().getKey());
            subYmax = new HashMap<>(subsInt.entrySet().iterator().next().getKey());
            double valPourMaj = subsInt.get(subsInt.entrySet().iterator().next().getKey());//+ Lambda*NormeInf(subsExt.entrySet().iterator().next().getKey());
            double valPourMin = valPourMaj - 2*Lambda*NormeInf(subsInt.entrySet().iterator().next().getKey());
            double valPourMax = valAbs(valPourMaj - valPourMin)/2;
            double valSub = -100000;
            //initialize majorant and minorant that have to be recalculated.
            maj = valPourMaj;
            minorant = valPourMin;
            for (HashMap<Integer,ArrayList<ArrayList<Double>>> subsToGetMax : subsInt.keySet()){
                //System.out.println("xmax in the boucle : "+xmax);
                valSub = subsInt.get(subsToGetMax);
                if ((valSub - Lambda*NormeInf(subsToGetMax)) > valYmax){
                    //System.out.println("changing xmax :" + Ymax + " for xmax: "+ subsToGetMax);
                    Ymax = new HashMap<>(getValidProbability(subsToGetMax));
                    subYmax = new HashMap<>(subsToGetMax);
                    //System.out.println("xmax now : "+xmax);
                    valYmax = valSub - Lambda*NormeInf(subsToGetMax);
                }
                if (valSub>maj){
                    //System.out.println("setting up majorant");
                    maj = valSub;
                }
                if (valSub -  Lambda*NormeInf(subsToGetMax)>minorant){
                    minorant = valSub - Lambda*NormeInf(subsToGetMax);
                }
            }

            //now searching on the list of erased subs if the suppressed middle of a subdivision is interesting
            /*
	    double valueFromErased = -10000;
            for (HashMap<Integer,ArrayList<ArrayList<Double>>> subsToGetMax : listSubsErased.keySet()){
                valueFromErased = listSubsErased.get(subsToGetMax);
                if ((valueFromErased-Lambda*NormeInf(subsToGetMax))>valYmax){
                    //System.out.println("xmax is an erased one : " + subsToGetMax + "valueFromErased:" + valueFromErased + "valXmax : "+ valXmax);
                    //valYmax = valueFromErased-Lambda*NormeInf(subsToGetMax);
                    //Ymax = new HashMap<>(milieu(subsToGetMax));
                    //subYmax = new HashMap<>(subsToGetMax);
                }
            }
	    */
            N++;
        }

        ///!\ TODO
        this.strategyP2 = getDistributionFromArrayList(getValidProbability(subYmax),1);
        //System.out.println("best response :" + getValidProbability(subYmax));
        return f(x,getValidProbability(subYmax),show);
        //return valYmax;

    }

    public void DOOexterne(HashMap<HashMap<Integer,ArrayList<ArrayList<Double>>>, Double> subsExt,
                           HashMap<HashMap<Integer,ArrayList<ArrayList<Double>>>, Double> subsInt, int dimJ1, int dimJ2, int partitionnement,
                           double holderianConstantP1,double holderianConstantP2, double epsilonExt, double epsilonInt) throws Exception {

        //Lambda = 4;
        //epsilonExt = 0.2;
        //System.out.println("DOObackup:: subsExt : " + subsExt + " subsInt : " + subsInt);
        int N = 0;
        double maj = Double.POSITIVE_INFINITY;
        double minorant = Double.NEGATIVE_INFINITY;
        double valXmax = Double.NEGATIVE_INFINITY;

        HashMap<Integer,ArrayList<ArrayList<Double>>> subMax = new HashMap<>(subsExt.entrySet().iterator().next().getKey());
        HashMap<HashMap<Integer,ArrayList<ArrayList<Double>>>,Double> listSubsErased = new HashMap<>();

        while (valAbs(maj - fExt(subMax,subsInt,dimJ2,partitionnement,holderianConstantP2,epsilonInt,false))> epsilonExt){
            //while(N<100){

            System.out.println("Nmax : "+N+"maj : "+maj+"diff : "+
                valAbs(maj-fExt(subMax,subsInt,dimJ2,partitionnement,holderianConstantP2,epsilonInt,false))+"submax : "+subMax);
            //System.out.println("état de hashmap :  " + subsExt);
            HashMap<Integer,ArrayList<Double>> b = new HashMap<>();
            HashMap<Integer,ArrayList<ArrayList<Double>>> bestSubsToSubdivise = new HashMap();
            int argmax = -1;
            double valueArgmax = -10000000;
            int indice=0;
            ArrayList<HashMap<Integer,ArrayList<ArrayList<Double>>>> toBeSupressed = new ArrayList<>();
            for (HashMap<Integer,ArrayList<ArrayList<Double>>> subsDimension : subsExt.keySet()){//adds new values and get the argmax.
                double valueSubsDimension = subsExt.get(subsDimension);
                //b.add(valueSubsDimension);
                //System.out.println("minorant : " + minorant);

                if (true||valueSubsDimension+holderianConstantP1*NormeInf(subsDimension)>=minorant) {
                    if (valueSubsDimension > valueArgmax) {
                        bestSubsToSubdivise = new HashMap<>(subsDimension);
                        valueArgmax = valueSubsDimension;
                    }
                }
                else{
                    //System.out.println("supressing " + subsDimension + "because value : " + valueSubsDimension+Lambda*NormeInf(subsDimension) + " and minorant : " + minorant);
                    System.exit(1);
                    toBeSupressed.add(subsDimension);
                }
                indice ++;
            }
            for (HashMap<Integer,ArrayList<ArrayList<Double>>> tmpSupressed : toBeSupressed){
                subsExt.remove(tmpSupressed);
            }
            ArrayList<HashMap<Integer,ArrayList<ArrayList<Double>>>> listNewSubdivisions = new ArrayList<>();
            listNewSubdivisions = SubdiviserPdtCartesien(bestSubsToSubdivise,dimJ1,partitionnement);
            listSubsErased.put(bestSubsToSubdivise,valueArgmax);
            subsExt.remove(bestSubsToSubdivise);
            double value;
            for (HashMap<Integer,ArrayList<ArrayList<Double>>> newSubToAdd : listNewSubdivisions){

                value = fExt(newSubToAdd,subsInt,dimJ2,partitionnement,holderianConstantP2,epsilonInt,false) + holderianConstantP1 * NormeInf(newSubToAdd);
                //System.out.println(newSubToAdd.toString());
                //System.out.println("Lambda*normeInt : " + Lambda*NormeInf(newSubToAdd) + "Lambda : " + Lambda + "NormeInf :" + NormeInf(newSubToAdd));
                //System.out.println("value : " + (value -  Lambda*NormeInf(newSubToAdd)) + " + Lambda*normInt : " + Lambda*NormeInf(newSubToAdd) + " = " + value);
                subsExt.put(newSubToAdd,value);
            }
            valXmax = subsExt.get(subsExt.entrySet().iterator().next().getKey()) - holderianConstantP1*NormeInf(subsExt.entrySet().iterator().next().getKey());
            subMax = new HashMap<>(subsExt.entrySet().iterator().next().getKey());
            double valPourMaj = subsExt.get(subsExt.entrySet().iterator().next().getKey());//+ Lambda*NormeInf(subsExt.entrySet().iterator().next().getKey());
            double valPourMin = valPourMaj - 2*holderianConstantP1*NormeInf(subsExt.entrySet().iterator().next().getKey());
            double valPourMax = valAbs(valPourMaj - valPourMin)/2;
            double valSub = -100000;
            //initialize majorant and minorant that have to be recalculated.
            maj = valPourMaj;
            minorant = valPourMin;
            for (HashMap<Integer,ArrayList<ArrayList<Double>>> subsToGetMax : subsExt.keySet()){
                //System.out.println("xmax in the boucle : "+xmax);
                valSub = subsExt.get(subsToGetMax);
                if ((valSub - holderianConstantP1*NormeInf(subsToGetMax)) > valXmax){
                    //System.out.println("changing xmax :" + xmax + " for xmax: "+ subsToGetMax);
                    //xmax = new HashMap<Double>(milieu(subsToGetMax));
                    subMax = new HashMap<>(subsToGetMax);
                    //System.out.println("xmax now : "+xmax);
                    valXmax = valSub - holderianConstantP1*NormeInf(subsToGetMax);
                }
                if (valSub>maj){
                    //System.out.println("setting up majorant");
                    maj = valSub;
                }
                if (valSub -  holderianConstantP1*NormeInf(subsToGetMax)>minorant){
                    minorant = valSub - holderianConstantP1*NormeInf(subsToGetMax);
                }
            }

            //now searching on the list of erased subs if the suppressed middle of a subdivision is interesting
            /*double valueFromErased = -10000;
            for (HashMap<Integer,ArrayList<ArrayList<Double>>> subsToGetMax : listSubsErased.keySet()){
                valueFromErased = listSubsErased.get(subsToGetMax);
                if ((valueFromErased-holderianConstantP1*NormeInf(subsToGetMax))>valXmax){
                    //System.out.println("xmax is an erased one : " + subsToGetMax + "valueFromErased:" + valueFromErased + "valXmax : "+ valXmax);
                    //valXmax = valueFromErased-Lambda*NormeInf(subsToGetMax);
                    //xmax = new ArrayList<Double>(milieu(subsToGetMax));
                    //subMax = new HashMap<>(subsToGetMax);
                }
            }*/
            N++;
        }

        //System.out.println("N : "+N+"maj : "+maj+"diff : "+valAbs(maj-fExt(subMax,subsInt,dimJ2,partitionnement,Lambda,epsilonIntfalse))+"xmax : "+xmax);
        //System.out.println("maximum value of the function f : " + valXmax + " found for xmax = "+ subMax + " and the corresponding probability is " + getValidProbability(subMax)
	//        + "and the real maximum of the function f is :" + fExt(subMax,subsInt,dimJ2,partitionnement,holderianConstantP2,epsilonInt,false));//f(getValidProbability(subMax)));this.politiqueJ2 = getValidProbability(subMax);
        this.strategyP1 = getDistributionFromArrayList(getValidProbability(subMax),0);
        this.finalValue = fExt(subMax,subsInt,dimJ2,partitionnement,holderianConstantP2,epsilonInt,true);
    }
    private HashMap<Integer, Distribution<Integer>> getDistributionFromArrayList(HashMap<Integer, ArrayList<Double>> x, int player) {
        //System.out.println("x : " + x);
        //System.out.println("player : " + player);
        HashMap<Integer,Distribution<Integer>> res = new HashMap<>();
        if (player == 0) {//player 1.
            ArrayList<Integer> ListActions = (ArrayList<Integer>) this.actionsP1;
            for (Integer h : x.keySet()) {
                Distribution<Integer> distrib = new Distribution<>();
                for (int i =0;i<ListActions.size();i++){
                    distrib.addWeight(ListActions.get(i),x.get(h).get(i));
                }
                /*try {
                    distrib.sanityCheck();
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
                res.put(h,distrib);
            }
        } else {
            //System.out.println("DOObackup::x : " + x.toString() + " list actions : " + (POSG.getActionsJ2()));
            ArrayList<Integer> ListActions = (ArrayList<Integer>) this.actionsP2;
            //System.out.println("list actions : " + ListActions);
            for (Integer h : x.keySet()) {
                Distribution<Integer> distrib = new Distribution<>();
                //distrib.addWeight(1,1.0);
                //System.out.println("DOObackup::x.get(h) : " + x.get(h));
                for (int i =0;i<ListActions.size();i++){
                    //System.out.println(" \n i : "+ i + ListActions.get(i) + " : " + x.get(h).get(i));
                    //System.out.println("DOObackup::i : " +x.get(h).get(i));
                    //System.out.println("DOObackup::adding for action : " + ListActions.get(i) + x.get(h).get(i));
                    distrib.addWeight(ListActions.get(i),x.get(h).get(i));
                }
                
                /*try {
                    //System.out.println("DOObackup::distribution : " + distrib.toString());
                    distrib.sanityCheck();
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
                res.put(h,distrib);
            }
        }
        return res;
    }
    private HashMap<Integer, ArrayList<Double>> getValidProbability(HashMap<Integer, ArrayList<ArrayList<Double>>> subMax) {
        HashMap<Integer,ArrayList<Double>> validProbability = new HashMap<>();
        //System.out.println("get valid probability from : " + subMax.get(0));
        for (Integer key : subMax.keySet()){
            validProbability.put(key,getValidProbability(subMax.get(key)));
        }
        //System.out.println("milieu == getValidProbability???" + milieu(subMax).equals(validProbability));
        //System.out.println("returning : " + validProbability);
        return validProbability;
    }

    private double NormeInf(HashMap<Integer, ArrayList<ArrayList<Double>>> subs) {
        double val = 0.0;
        for (Integer key : subs.keySet()){
            double temp = this.NormeInf(subs.get(key));
            if (temp>val){
                val = temp;
            }
        }
        return val;
    }


    private ArrayList<Double> getValidProbability(ArrayList<ArrayList<Double>> subMax) {
        ArrayList<Double> validProbability = new ArrayList<>();
        double sumCoord = 0.0;
        for (ArrayList<Double> coordCoins: subMax){
            sumCoord += coordCoins.get(0);
        }
	this.t = 1.0;
	double t = (1-sumCoord)/(subMax.size()*NormeInf((subMax)));
	this.t=t;
	//System.out.println("t : "+t);
        for (ArrayList<Double> coordFromSubMax : subMax){
            validProbability.add(coordFromSubMax.get(0) + t *(coordFromSubMax.get(1) - coordFromSubMax.get(0)));
        }
	this.isProbability(validProbability);	
	return validProbability;
    }


}
