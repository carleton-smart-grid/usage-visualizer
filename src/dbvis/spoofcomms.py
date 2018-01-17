##############################################################################
#
# Class:             spoofcomms.py
# Project:           N/A
# Author:            Jason Van Kerkhoven
# Date of Update:    14/10/2017
# Version:           1.0.0
#
# Purpose:           Load a new CSV entry into the database on a fixed period.
#
# Update Log:        v1.0.0
#                       - null
#
##############################################################################


# import libraries
import csv
import sqlite3
import time
import sys


# Program constants
YEAR_CONSTANT = '20'
TIMEOUT_SECONDS = 60


# call using spoofcomms.py usagedat.csv database.db (INT)read_write_freq_in_sec
# get program arguments
csvPath = sys.argv[1]
dbPath = sys.argv[2]
rwFreq = int(sys.argv[3])

# check if pause nessesary
waitFlag = rwFreq > 0

# open database connection
dbConnection = sqlite3.connect(dbPath, TIMEOUT_SECONDS)
dbCurser = dbConnection.cursor()

# open the csv file and skip headers
csvFile = open(csvPath, 'r')
reader = csv.reader(csvFile)
next(reader, None)

# read-write loop
while(True):
    # get current csv data
    current = next(reader, None)
    timeRaw = current[0]
    usages = [current[1], current[2], current[3], current[4], current[5]]

    # parse date-time into correct format
    dateTime = timeRaw.split(' ')
    dmy = dateTime[0].split('-')
    dateFormated = YEAR_CONSTANT + dmy[2] + '-' + dmy[1] + '-' + dmy[0]

    # insert each houseIDs usages (1, 2, 3, 4, 5)
    for id in range(1, 6):
        insert = "INSERT INTO usages values(date('" + dateFormated + "'),time('" + dateTime[1] + "')," + str(id) + "," + str(usages[id-1]) + ")"
        print(insert)
        dbCurser.execute(insert)
        dbConnection.commit()

    # wait
    print("")
    if(waitFlag):
        time.sleep(rwFreq)
