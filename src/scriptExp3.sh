
javac -cp ../lib/cplex.jar:../lib/Jama-1.0.3.jar */*.java

for i in {0..99} #general multiple executions
do
    echo "new execution : " + $i
    for j in 0.8 0.9 1 1.1 1.2 1.3
    do 
	for p in  0.15 0.175 0.20 0.225 0.25
	do 
	    echo >&2 "i : $i j : $j p: $p"
	    #for k in 0.25 0.5 0.75 
	    #do
		#java -Djava.library.path=../lib/ -cp .:../lib/Jama-1.0.3.jar:../lib/cplex.jar solver.main 1.0 $p $k $(echo "(1-$k)"|bc -l) $i
		java -Djava.library.path=../lib/ -cp .:../lib/Jama-1.0.3.jar:../lib/cplex.jar solver.main $j $p 0.5 0.5 $i
	    #done
	done
    done
done

