
class Trip:
    def __init__(self, arrivalTime, length, similarity):
        self.arrivalTime = arrivalTime
        self.length = length
        self.weight = length * similarity
    
    def __str__(self):
        string = "arrive: " + str(self.arrivalTime) + "\n"
        string = string + "len: " + str(self.length) + "\n"
        string = string + "weight: " + str(self.weight) + "\n"
        return string

def main():
    list = []
    list.append(Trip(0, 12, 1))
    list.append(Trip(1, 17, 1))
    list.append(Trip(2, 4, 1))
    list.append(Trip(3, 2, 2))
    list.append(Trip(4, 12, 1))
    list.append(Trip(5, 12, 1))
    list.append(Trip(0, 12, 1))

    list.sort(key=lambda x: x.arrivalTime)
    
    table = [0 for i in range(len(list))]

    table[0] = list[0].weight

    for i in range(1, len(list)):
        weight = list[i].weight
        latest = -1
        for j in range(0, i): # find latest trip that does not conflict with current trip
            if (list[j].arrivalTime > list[i].arrivalTime - list[i].length):
                latest = latest
            else:
                latest = j
        if (latest != -1):
            weight += table[latest]
        table[i] = max(weight, table[i-1]) # compare current weight with weight if we took the previous trip instead
        print(table)

    print(table[len(list)-1])

    set = []

    for i in range(len(list) - 1, 0, -1):
        if (table[i] > table[i-1]):
            set.append(list[i])
            print(list[i])

main()