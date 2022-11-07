package Algorithm;

import util.MatrixX;

import java.util.ArrayList;
import java.util.HashMap;

public class DOOSubsNM1 {

    public DOOSubsNM1(){

    }

    public double f(ArrayList<Double> x,ArrayList<Double> y) throws Exception {
        if (x.size() != y.size()){
            throw new Exception("x et y n'ont pas la même dimension!");
        }
        double S = 0;
        for (int i = 0; i<x.size();i++){
            S+= x.get(i) + y.get(i);
        }
        return S;
    }

    public double sommeVector(ArrayList<Double> vect){
        double S = 0.0;
        for (Double d : vect){
            S+=d;
        }
        return S;
    }
    public double fonctionRecompense(ArrayList<Double> x, ArrayList<Double> y, MatrixX M){
        double S = 0;
        //for (int i = 0; i<x.size();i++){
        //S+= x.get(i)*(i+1);
        //}
        //System.out.println("for x : "+x+ "value : "+S);
        //return S;
        double[][] xVector = new double[x.size()+1][1];
        double[][] yVector = new double[y.size()+1][1];
        int i = 0;
        for (i = 0; i<x.size(); i++){
            xVector[i][0]= x.get(i);
            yVector[i][0] = y.get(i);
        }
        xVector[i][0] = 1 - sommeVector(x);
        yVector[i][0] = 1- sommeVector(y);
        MatrixX X = new MatrixX(xVector);
        MatrixX Y = new MatrixX(yVector);
        //System.out.println("Y : " + Y.toString() + "X : " + X.toString() + "M : " + M.toString());
        return (X.transpose()).times(M.times(Y)).get(0,0);
        //return yVector[0][0]+2*yVector[1][0]+3*yVector[2][0];
    }

    public double f(ArrayList<Double> x, ArrayList<Double> y, MatrixX M){
        return -fonctionRecompense(x,y,M);
    }

    public double fExt(ArrayList<Double> x, HashMap<ArrayList<ArrayList<Double>>,Double> subsInt, int dim, int partionnement, double Lambda, double epsilonInt, MatrixX M){

        return -dOOinterne(x,subsInt,dim,partionnement,Lambda,epsilonInt,M);
    }
    //linspace gotten from the internet.
    public static ArrayList<Double> linspace(double min, double max, int points) {
        ArrayList<Double> d = new ArrayList<Double>();
        for (int i = 0; i < points; i++){
            d.add(i,min + i * (max - min) / (points - 1));
        }
        return d;
    }

    public boolean isInSimplexeDimensionNM1(ArrayList<ArrayList<Double>> subdivision){
        double S = 0.0;
        for (ArrayList<Double> subsInDimensions : subdivision){
            if (subsInDimensions.get(0)<0.0 || subsInDimensions.get(1)>1.0){
                return false;
            }
            S += subsInDimensions.get(0);
        }
        if (S>=1){
            //System.out.println(" not in simplex : " + subdivision + "sum coords : " + S + " Norme inf : " + normeInf((subdivision)));
            return false;
        }
        //System.out.println("not keeping : " + subdivision.toString() + " because, value : " + (S - subdivision.size()*normeInf(subdivision)));
        return true;
    }

    public boolean isProbability(ArrayList<Double> x){
        double S = 0.0;
        for (Double d : x){
            S+= d;
        }
        if (S != 1){
            //System.out.println("not a probability : "+x);
        }
        return (S==1);
    }
    public boolean isInSimplexeDimensionN(ArrayList<ArrayList<Double>> subdivision){
        double Sinf = 0.0;
        double Ssup = 0.0;
        for (ArrayList<Double> subsInDimensions : subdivision){
            if (subsInDimensions.get(0)<0.0 || subsInDimensions.get(1)>1.0){
                return false;
            }
            Sinf += subsInDimensions.get(0);
            Ssup += subsInDimensions.get(1);
        }
        if (Sinf <1 && Ssup>1){
            //System.out.println("je garde : " + subdivision);
            this.isProbability(milieu(subdivision));
            return true;
        }
        if (Ssup<1){
            //System.out.println("coin droite trop petit : "+ subdivision);
        }
        //System.out.println(" not in simplex : " + subdivision + "sum coords : " + Sinf + " Norme inf : " + normeInf((subdivision)));
        //System.out.println("not keeping : " + subdivision.toString() + " because, value : " + (S - subdivision.size()*normeInf(subdivision)));
        return false;
    }

    private double valAbs(Double d){
        if (d>0){
            return d;
        }
        return -d;
    }
    private double normeInf(ArrayList<ArrayList<Double>> subdivision) {
        ArrayList<Double> milieu = this.milieu(subdivision);
        //System.out.println("norme inf de : " + subdivision.toString() + " : " + valAbs(subdivision.get(0).get(0) - milieu.get(0)));
        return valAbs(subdivision.get(0).get(0) - milieu.get(0));
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
            if (!this.isInSimplexeDimensionNM1(carapuce)) {
                //System.out.println("Not keeping:"+carapuce.toString());
                ListSuppSimplexe.add(carapuce);
            }
        }
        for (ArrayList<ArrayList<Double>> salameche : ListSuppSimplexe) {
            S.remove(salameche);
        }
        //System.out.println("Returning : "+S.toString() + "\n taille : "+ S.size());
        return S;
    }

    public void showMap(HashMap<ArrayList<ArrayList<Double>>,Double> subs){
        for (ArrayList<ArrayList<Double>> list : subs.keySet()){
            System.out.println("for y :" + list.toString() + " value : " + subs.get(list));
        }
    }
    public double dOOinterne(ArrayList<Double> x, HashMap<ArrayList<ArrayList<Double>>,Double> subsInt, int dim, int partitionnement, double Lambda, double epsilon, MatrixX M){
        //Lambda = 4;
        //System.out.println("x : "+x);
        epsilon = 0.15;
        int N = 0;
        double maj = 100000;
        double minorant = -100000;
        double valYmax = -100000;
        ArrayList<Double> Ymax = milieu(subsInt.entrySet().iterator().next().getKey());
        ArrayList<ArrayList<Double>> subYmax = new ArrayList<>(subsInt.entrySet().iterator().next().getKey());
        subsInt.replace(subsInt.entrySet().iterator().next().getKey(),f(x,Ymax,M)+Lambda* normeInf(subYmax));
        //System.out.println(subsInt.toString());
        //this.showMap(subsInt);
        //HashMap<ArrayList<ArrayList<Double>>,Double> listSubsErased = new HashMap<>();
        while (valAbs(maj - f(x,Ymax,M))> 0.10){
            //System.out.println("N : "+N+"maj : "+maj+"diff : "+valAbs(maj-f(x,Ymax,M))+"Ymax : "+Ymax + " et xmax :" +x);
            ArrayList<Double> b = new ArrayList<>();
            ArrayList<ArrayList<Double>> bestSubsToSubdivise = new ArrayList<>();
            double valueArgmax = -10000000;
            ArrayList<ArrayList<ArrayList<Double>>> toBeSupressed = new ArrayList<>();
            for (ArrayList<ArrayList<Double>> subsDimension : subsInt.keySet()){//adds new values and get the argmax.
                //double valueSubsDimension = f(milieu(subsDimension))+ valAbs((Lambda*(normeInf(subsDimension))));
                //double valueSubsDimension = subsInt.get(subsDimension);
                double valueSubsDimension = f(x,milieu(subsDimension),M) + Lambda* normeInf(subsDimension);
                //System.out.println("new value for x : " + x + " and y : " + subsDimension  + f(x,milieu(subsDimension),M) + " , "
                  //      + Lambda*normeInf(subsDimension) + " , " + f(x,milieu(subsDimension),M)
                    //    + Lambda*normeInf(subsDimension));
                subsInt.replace(subsDimension,valueSubsDimension);
                b.add(valueSubsDimension);
                if (valueSubsDimension>=minorant) {
                    if (valueSubsDimension > valueArgmax) {
                        bestSubsToSubdivise = new ArrayList<>(subsDimension);
                        valueArgmax = valueSubsDimension;
                    }
                }
                else{
                    toBeSupressed.add(subsDimension);
                }
            }
            for (ArrayList<ArrayList<Double>> tmpSupressed : toBeSupressed){
                subsInt.remove(tmpSupressed);
            }
            ArrayList<ArrayList<ArrayList<Double>>> listNewSubdivisions = new ArrayList<>();
            listNewSubdivisions = Subdiviser(bestSubsToSubdivise,dim,partitionnement);
            //listSubsErased.put(bestSubsToSubdivise,valueArgmax);
            subsInt.remove(bestSubsToSubdivise);
            double value;
            for (ArrayList<ArrayList<Double>> newSubToAdd : listNewSubdivisions){
                value = f(x,milieu(newSubToAdd),M) + Lambda * normeInf(newSubToAdd);
                subsInt.put(newSubToAdd,value);
            }
            Ymax = new ArrayList<Double>(milieu(subsInt.entrySet().iterator().next().getKey()));
            //valYmax = f(x,Ymax,M)+Lambda*normeInf(subsInt.entrySet().iterator().next().getKey());
            valYmax = subsInt.get(subsInt.entrySet().iterator().next().getKey()) - Lambda* normeInf(subsInt.entrySet().iterator().next().getKey());
            subYmax = new ArrayList<>(subsInt.entrySet().iterator().next().getKey());
            double valPourMaj = subsInt.get(subsInt.entrySet().iterator().next().getKey());//+ Lambda*normeInf(subsExt.entrySet().iterator().next().getKey());
            double valPourMin = valPourMaj - 2*Lambda* normeInf(subsInt.entrySet().iterator().next().getKey());
            double valSub = -100000;
            //initialize majorant and minorant that have to be recalculated.
            maj = valPourMaj;
            minorant = valPourMin;
            for (ArrayList<ArrayList<Double>> subsToGetMax : subsInt.keySet()){
                //System.out.println("xmax in the boucle : "+xmax);
                valSub = subsInt.get(subsToGetMax);
                if ((valSub - Lambda* normeInf(subsToGetMax)) > valYmax){
                    //System.out.println("changing xmax :" + Ymax + " for xmax: "+ subsToGetMax);
                    Ymax = new ArrayList<Double>(milieu(subsToGetMax));
                    subYmax = new ArrayList<>(subsInt.entrySet().iterator().next().getKey());
                    //System.out.println("xmax now : "+xmax);
                    valYmax = valSub - Lambda* normeInf(subsToGetMax);
                }
                if (valSub>maj){
                    //System.out.println("setting up majorant");
                    maj = valSub;
                }
                if (valSub - 2* Lambda* normeInf(subsToGetMax)<minorant){
                    minorant = valSub - 2* Lambda* normeInf(subsToGetMax);
                }
            }

            //now searching on the list of erased subs if the suppressed middle of a subdivision is interesting
            double valueFromErased = -10000;
            /*
            for (ArrayList<ArrayList<Double>> subsToGetMax : listSubsErased.keySet()){
                valueFromErased = listSubsErased.get(subsToGetMax);
                if ((valueFromErased-Lambda*normeInf(subsToGetMax))>valYmax){
                    //System.out.println("xmax is an erased one : " + subsToGetMax + "valueFromErased:" + valueFromErased + "valXmax : "+ valXmax);
                    valYmax = valueFromErased-Lambda*normeInf(subsToGetMax);
                    Ymax = new ArrayList<Double>(milieu(subsToGetMax));
                    subYmax = new ArrayList<>(subsInt.entrySet().iterator().next().getKey());
                }
            }*/
            N++;
        }
        //System.out.println("returning : " + f(x,Ymax,M) + " found for " + subYmax);
        //System.out.println("valYmax : "+valYmax);
        return f(x,Ymax,M);
    }

    public void dOOexterne(HashMap<ArrayList<ArrayList<Double>>, Double> subsExt, HashMap<ArrayList<ArrayList<Double>>, Double> subsInt, int dim, int partitionnement,
                           double Lambda, double epsilonExt, double epsilonInt, MatrixX M){
        epsilonExt = 0.10;
        int N = 0;
        double maj = 100000;
        double minorant = -100000;
        double valXmax = -100000;
        ArrayList<Double> xmax = milieu(subsExt.entrySet().iterator().next().getKey());
        Lambda = Lambda*xmax.size();
        System.out.println("Lambda : "+Lambda);
        ArrayList<ArrayList<Double>> subMax = new ArrayList<>();
        HashMap<ArrayList<ArrayList<Double>>,Double> listSubsErased = new HashMap<>();
        System.out.println("value :"+fExt(xmax,subsInt,dim,partitionnement,Lambda,epsilonInt,M));
        while (valAbs(maj - fExt(xmax,subsInt,dim,partitionnement,Lambda,epsilonInt,M))> 0.1){
        //while (N<1){
            System.out.println("N : "+N+"maj : "+maj+"diff : "+valAbs(maj-fExt(xmax,subsInt,dim,partitionnement,Lambda,epsilonInt,M))+"xmax : "+xmax);
            ArrayList<Double> b = new ArrayList<>();
            ArrayList<ArrayList<Double>> bestSubsToSubdivise = new ArrayList<>();
            int argmax = -1;
            double valueArgmax = -10000000;
            int indice=0;
            ArrayList<ArrayList<ArrayList<Double>>> toBeSupressed = new ArrayList<>();
            for (ArrayList<ArrayList<Double>> subsDimension : subsExt.keySet()){//adds new values and get the argmax.
                //double valueSubsDimension = f(milieu(subsDimension))+ valAbs((Lambda*(normeInf(subsDimension))));
                double valueSubsDimension = subsExt.get(subsDimension);
                b.add(valueSubsDimension);
                if (valueSubsDimension>=minorant) {
                    if (valueSubsDimension > valueArgmax) {
                        bestSubsToSubdivise = new ArrayList<>(subsDimension);
                        valueArgmax = valueSubsDimension;
                    }
                }
                else{
                    toBeSupressed.add(subsDimension);
                }
                indice ++;
            }
            for (ArrayList<ArrayList<Double>> tmpSupressed : toBeSupressed){
                subsExt.remove(tmpSupressed);
            }
            ArrayList<ArrayList<ArrayList<Double>>> listNewSubdivisions = new ArrayList<>();
            listNewSubdivisions = Subdiviser(bestSubsToSubdivise,dim,partitionnement);
            listSubsErased.put(bestSubsToSubdivise,valueArgmax);
            subsExt.remove(bestSubsToSubdivise);
            double value;
            for (ArrayList<ArrayList<Double>> newSubToAdd : listNewSubdivisions){
                //value = f(milieu(newSubToAdd)) + Lambda * normeInf(newSubToAdd);
                value = fExt(milieu(newSubToAdd),subsInt,dim,partitionnement,Lambda,epsilonInt,M) + Lambda * normeInf(newSubToAdd);
                subsExt.put(newSubToAdd,value);
            }
            xmax = new ArrayList<Double>(milieu(subsExt.entrySet().iterator().next().getKey()));
            //valXmax = f(xmax)-Lambda*normeInf(subsExt.entrySet().iterator().next().getKey());
            valXmax = subsExt.get(subsExt.entrySet().iterator().next().getKey()) - Lambda* normeInf(subsExt.entrySet().iterator().next().getKey());
            subMax = new ArrayList<>(subsExt.entrySet().iterator().next().getKey());
            double valPourMaj = subsExt.get(subsExt.entrySet().iterator().next().getKey());//+ Lambda*normeInf(subsExt.entrySet().iterator().next().getKey());
            double valPourMin = valPourMaj - 2*Lambda* normeInf(subsExt.entrySet().iterator().next().getKey());
            double valPourMax = valAbs(valPourMaj - valPourMin)/2;
            double valSub = -100000;
            //initialize majorant and minorant that have to be recalculated.
            maj = valPourMaj;
            minorant = valPourMin;
            for (ArrayList<ArrayList<Double>> subsToGetMax : subsExt.keySet()){
                //System.out.println("xmax in the boucle : "+xmax);
                valSub = subsExt.get(subsToGetMax);
                if ((valSub - Lambda* normeInf(subsToGetMax)) > valXmax){
                    //System.out.println("changing xmax :" + xmax + " for xmax: "+ subsToGetMax);
                    xmax = new ArrayList<Double>(milieu(subsToGetMax));
                    subMax = new ArrayList<>(subsExt.entrySet().iterator().next().getKey());
                    //System.out.println("xmax now : "+xmax);
                    valXmax = valSub - Lambda* normeInf(subsToGetMax);
                }
                if (valSub>maj){
                    //System.out.println("setting up majorant");
                    maj = valSub;
                }
                if (valSub - 2* Lambda* normeInf(subsToGetMax)<minorant){
                    minorant = valSub - 2* Lambda* normeInf(subsToGetMax);
                }
            }

            //now searching on the list of erased subs if the suppressed middle of a subdivision is interesting
            double valueFromErased = -10000;
            for (ArrayList<ArrayList<Double>> subsToGetMax : listSubsErased.keySet()){
                valueFromErased = listSubsErased.get(subsToGetMax);
                if ((valueFromErased-Lambda* normeInf(subsToGetMax))>valXmax){
                    //System.out.println("xmax is an erased one : " + subsToGetMax + "valueFromErased:" + valueFromErased + "valXmax : "+ valXmax);
                    valXmax = valueFromErased-Lambda* normeInf(subsToGetMax);
                    xmax = new ArrayList<Double>(milieu(subsToGetMax));
                    subMax = new ArrayList<>(subsExt.entrySet().iterator().next().getKey());
                }
            }
            N++;
        }
        System.out.println("N : "+N+"maj : "+maj+"diff : "+valAbs(maj-fExt(xmax,subsInt,dim,partitionnement,Lambda,epsilonInt,M))+"xmax : "+xmax);
        System.out.println("maximum value of the function f : " + valXmax + " found for xmax = "+ xmax //+ " and the corresponding probability is " + getValidProbability(subMax)
        + "and the real maximum of the function f is :" + fExt(xmax,subsInt,dim,partitionnement,Lambda,epsilonInt,M));//f(getValidProbability(subMax)));
    }

    private ArrayList<Double> getValidProbability(ArrayList<ArrayList<Double>> subMax) {
        ArrayList<Double> validProbability = new ArrayList<>();
        double sumCoord = 0.0;
        for (ArrayList<Double> coordCoins: subMax){
            sumCoord += coordCoins.get(0);
        }
        double t = (1-sumCoord)/(2*subMax.size()* normeInf((subMax)));
        //System.out.println("t : "+t);
        for (ArrayList<Double> coordFromSubMax : subMax){
            validProbability.add(coordFromSubMax.get(0) + t *(coordFromSubMax.get(1) - coordFromSubMax.get(0)));
        }
        System.out.println("from : "+subMax+" should be a probability ! ---------------");
        this.isProbability(validProbability);
        System.out.println("--------------------------");
        return validProbability;
    }

}
