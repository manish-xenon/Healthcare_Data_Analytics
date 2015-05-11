Step 1: Copy paste CS410_Project into your PC.
Step 2: Modify cs410.config file to have correct path of the csv files according to your
path in step 1
Step 3: Go to command prompt and under "CS410_Project" folder, type 
mvn package
Note: this requires maven to be installed.
Step 4: once done, cd to target folder and type the following
java -jar cs410_project-jar-with-dependencies.jar ../cs410.config

This will create a jpg chart named "Medical_Stat.jpeg" under the target folder PLUS it also pops up the plot for you.

Task for integration to UI:
Your Front end has to change the config file values based on the
drop boxes that you select(like Day difference, file 1 and file 2) and then run the java files as described above.
This will generate the "Medical_Stat.jpeg" under the target folder which you then just show on the front end.

The graph shows the correlation between PAN and CC data for 1 day difference and is shown
in the Graph as R (Correlation Coefficient).
Correlation R = 0.11199042865700409
Since the value of R is close to 0, there is NO Correlation between PAN and CC for day diff = 1

Reference: http://mathbits.com/MathBits/TISection/Statistics2/correlation.htm

Note: If you see the source, there is only 1 java file called MedicalCorrelation.java
which has the actual code.
Another file csv_reader.java is only for testing purposes to learn JFreechart, reading csv files and testing corner cases.
