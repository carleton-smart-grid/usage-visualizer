# Purpose

The Graphing Tool is designed to allow a visual representation of the dataflow into the main TA database. It can also be used to view (simple) data analytics. It represents the power usages of all house's as a line graph (time as the domain).



# How to Use
## Running the Graphing Tool
The [graphing tool](https://github.com/carleton-smart-grid/usage-visualizer/blob/master/src/ctrl/UsageControl.java) should be run as a pre-build, executable .jar file from terminal using the command:<br> `java -jar usage-view.jar [data.db] [T]`.


In which, `data.db` is the relative path to the SQLite3 database file, and `T` is the update period (given as this is normally run with a fairly low refresh rate, period is less cumbersome to type) of the graph.

Currently, the maxiumum graph refresh rate is capped to 1 *Hz*. The expected environment the program will be run in will have updates to the database **best case** at a rate of
25 new values (more-or-less) synchronously every 15 minutes, and **worse case** 1 new value every 36 seconds asynchronously. The limited refresh rate is therefore not expected to cause any issues with a real-time display of data.

It should be noted that the update period should be less than the read-write period of the comms. spoofing tool for best results.



## Running the Comms. Spoofer Tool
It is advised **before** running the [comms. spoofer](https://github.com/carleton-smart-grid/usage-visualizer/blob/master/dat/spoofcomms.py) script, the SQLite3 database is cleared. This can be done conveniently through running the SQLite3 script `reset.txt`. This can be done through the terminal command:<br> `sqlite3 database.db < reset.txt`.

The comms. spoofer is a simple python script run from terminal using the command: `python3 spoofcomms.py [dataset.csv] [database.db] [rwp]`.

In which `dataset.csv` is the relative path/file name of the CSV file containing the usage values ,  `database.db` is the relative path/file name of the SQLite3 database file, and `rwp` is the period which entries are read from the CSV file and added to the database.
