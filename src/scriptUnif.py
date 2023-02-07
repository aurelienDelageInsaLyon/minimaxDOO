import numpy as np

# Get the file handler
fhand = open('tmpForScript')

turn = 'd'
timeD = []
timeU = []
# Loop through each line via file handler
for line in fhand:
  print(line)

  line = line.split(" ")
  print(line)
  if (turn=='d'):
      timeD.append(float(line[2][0:len(line[2])-1]))
      turn='u'
  else:
      timeU.append(float(line[2][0:len(line[2])-1]))
      turn = 'd'

print("size doo : " + str(len(timeD)))
print("size unif : " + str(len(timeU)))
print("time doo : " + str(np.mean(timeD)) + "ecart-type :"  + str(np.std(timeD)))
print("time unif : " + str(np.mean(timeU)) + "ecart-type :"  + str(np.std(timeU)))
