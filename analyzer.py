file = open("6.c", "r")

broken_up_list = []
string_holder = ""
is_in_string = False

for line in file:
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
        try:
            if(token[0] == '"'):
                is_in_string = True
        except IndexError:
            pass#Probably a space
        if(is_in_string):
            string_holder += token
            try:
                if(token[-1] == '"'):
                    is_in_string = False
                    broken_up_list.append(string_holder)
                    string_holder = ""
            except IndexError:
                pass#Probably a space
        else:
            broken_up_list.append(token)

if(is_in_string):
    print("Error, string is opened, but no closed")

varibles = []
getting_varible_name = False

for token in broken_up_list:
    print(token)
    if(getting_varible_name):
        varibles.append([token, getting_varible_name])
        getting_varible_name = False
    if(token == "int"):
        getting_varible_name = token
        continue

for varible in varibles:
    print(varible[0], varible[1])

