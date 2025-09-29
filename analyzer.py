file = open("6.c", "r")

broken_up_list = []
string_holder = ""
is_in_string = False
previous_token = ''
lineCount = 0#Start at 0, but increased to 1 immediately in loop
latestLine = 0
varibles = []
getting_varible_name = False
dataTypes = ['int','short','long','float','double','char','void','string']
declarations = [';','.','(',')','+','=']

for line in file:
    lineCount += 1
    print(line.strip()) 

    line = line.replace("("," ( ")
    line = line.replace(")"," ) ")
    line = line.replace(":"," : ")
    line = line.replace(";"," ; ")
    line = line.replace("."," . ")
    print(line)
    tokens = line.split(" ")

    for token in tokens:
        print(token)

        if(not is_in_string):
            try:
                if(token[0] == '"'):
                    is_in_string = True
                    print("String opened")
            except IndexError:
                pass#Probably a space


        if(is_in_string):
            string_holder += token
            try:
                if(token[-1] == '"'):
                    print("String closed")
                    is_in_string = False
                    broken_up_list.append(string_holder)
                    print(string_holder)#This is the string
                    string_holder = ""
                    latestLine = lineCount
            except IndexError:
                pass#Probably a space
        if(getting_varible_name):
            if (token in declarations):
                print(f"Error, var type is declared at line {latestLine}, but given a declaration and not a varible")
            else:
                varibles.append([token, getting_varible_name])
                getting_varible_name = False
                print('Varible name determined')
        elif(token in dataTypes):
            print('Varible declaration detected')
            getting_varible_name = token
            continue
        elif(token in declarations):
            if(token == "."):
                print("Using object property")
            broken_up_list.append(token)
        elif(token.strip() == ""):
            print("Empty/spacing here")
            continue
        elif(token in varibles[0]):
            print(f"Varible '{token}', used at line {latestLine}")
        else:
            print(f"Error, unknown item '{token}' declared at line {latestLine}, this is not a varible or imported")
            broken_up_list.append(token)
    latestLine = lineCount

if(is_in_string):
    print(f"Error, string is opened at line {latestLine}, but not closed even at the end of program")

for varible in varibles:
    print(varible[0], varible[1])
