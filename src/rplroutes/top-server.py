# Jason Van Kerkhoven
# 17/01/2018



# generic imports
import socket
import sys
import time


# declaring program constants
BUFFER = 1024




#############################################################################
# DECLARING FUNCTIONS
#############################################################################
# verbose print
def printv(string):
    if verbose:
        print(string)


#receieve pings on PORT
def receive():
    try:
        data, src = sock.recvfrom(BUFFER)
        printv('Receieved', len(data), 'bytes from', src[0])
        return data.decode('utf-8')
    finally:
        sock.close()




###############################################################################
# PROGRAM START
###############################################################################

# init program variables
sock = None
port = 5000
verbose = False
tfile = str(sys.argv[1])

# check for flags
flags = sys.argv
for i in range(0, 2):
    del flags[0]
while (len(flags) > 0):
    flag = flags.pop(0)

    # verbose flag
    if (flag == '-v' or flag == '--verbose'):
        verbose = True
    # port flag
    elif (flag == '-p' or flag == '--port'):
        port = int(flags.pop(0))
    # unknown flag
    else:
        print('Unknown flag "' + flag + '"\nExiting...')
        sys.exit()

# init UDP socket
printv('Binding socket on port ' + str(port) + '...')
scope_id = socket.AF_INET6 #socket.if_nametoindex('lowpan0')
sock = socket.socket(socket.AF_INET6, socket.SOCK_DGRAM)
sock.bind(('', port, 0, scope_id))

# main run loops
while(True):
    printv('Waiting for data...')

    # wait for lowpan node data
    nodeInfo = receive()
