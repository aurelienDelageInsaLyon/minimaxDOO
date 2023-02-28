

javac -cp ../lib/cplex.jar:../lib/Jama-1.0.3.jar */*.java
for i in {0..9} #general multiple executions
do
    for j in 1 1.2 1.4 1.6 1.8 2.0
    do 
	for p in  0.05 0.075 0.1
	do 
	    for k in 0.25 0.5 0.75 
	    do
		java -Djava.library.path=../lib/ -cp .:../lib/Jama-1.0.3.jar:../lib/cplex.jar solver.main $j $p $k $(echo "(1-$k)"|bc -l) $i
	    done
	done
    done
done

