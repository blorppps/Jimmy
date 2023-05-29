import random
out = "";
for i in range(15):
    for j in range(random.randint(2,5)):
        build = ""
        build += str(random.randint(50,1100)) + " "
        build += str(random.randint(50,550)) + " "
        if (0 <= i <= 4):
            build += "oak "
        if (5 <= i <= 9):
            build += "darkOak "
        if (10 <= i <= 14):
            build += "spruce "
        out += build

    #out += "-0 0 melee-150 255 150"
    out += "\n"
print(out)
