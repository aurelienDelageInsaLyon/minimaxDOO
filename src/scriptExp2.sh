
javac -cp ../lib/cplex.jar:../lib/Jama-1.0.3.jar */*.java

for i in {0..6} {8..9} #general multiple executions
do
    echo "new execution : " + $i
    for j in 1 1.1 1.2 1.3 1.4 1.5
    do 
	for p in  0.1 0.125 0.15 0.175 0.20
	do 
	    #for k in 0.1 0.25 0.5 
	    #do
		#java -Djava.library.path=../lib/ -cp .:../lib/Jama-1.0.3.jar:../lib/cplex.jar solver.main $j $p $k $(echo "(1-$k)"|bc -l) $i
		java -Djava.library.path=../lib/ -cp .:../lib/Jama-1.0.3.jar:../lib/cplex.jar solver.main $j $p 0.5 0.5 $i
	    #done
	done
    done
done

