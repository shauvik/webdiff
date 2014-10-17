FILEN:=nofile
BUGS:=nobugs
CXXFLAGS =	-O2 -g -Wall -fmessage-length=0
OBJS1=grey_out.o
OBJS=opencv_test.o
OBJS2=colorspp.o


LIBS = -lcv -lhighgui

TARGET1=grey_out
TARGET=opencv_test
TARGET2=colorspp

$(TARGET):	$(OBJS) $(OBJS1) $(OBJS2)
	$(CXX) -o $(TARGET) $(OBJS) $(LIBS)
	$(CXX) -o $(TARGET1) $(OBJS1) $(LIBS)
	$(CXX) -o $(TARGET2) $(OBJS2) $(LIBS)

all: $(TARGET) 

clean:
	rm -f $(OBJS) $(OBJS1) $(OBJS2) $(TARGET) $(TARGET1) $(TARGET2)

runExpts:
	mysql -u husayn -p -h localhost thepin < thepin1.sql 
	cp ../Screenshots/1.png img/1.png
	cp ../Screenshots/2.png img/2.png
	cp ../Screenshots/3.png img/3.png
	cp ../Screenshots/4.png img/4.png
	javac *.java
	./runnd.sh
	cp img/1.png img/1nd.png
	cp img/4.png img/4nd.png
	./run.sh

runExptsG:
	cp ../Screenshots/1.png img/1.png
	cp ../Screenshots/2.png img/2.png
	cp ../Screenshots/3.png img/3.png
	cp ../Screenshots/4.png img/4.png
	javac *.java
	./runnd.sh
	cp img/1.png img/1nd.png
	cp img/4.png img/4nd.png
	./run.sh
runExptsT:
	cp ../Screenshots/1.png img/1.png
	cp ../Screenshots/2.png img/2.png
	cp ../Screenshots/3.png img/3.png
	cp ../Screenshots/4.png img/4.png
	javac *.java
	./run.sh
demo:
	cp ../Screenshots/1.png ../www/demo/ff.png
	cp ../Screenshots/3.png ../www/demo/ie.png
backup:
	mkdir backups/$(FILEN)
	mysqldump --user=husayn --password= -h localhost --databases thepin > backups/$(FILEN)/sqlbkp.SQL
	mkdir backups/$(FILEN)/img
	cp ../Screenshots/1.png backups/$(FILEN)/img/1.png
	cp ../Screenshots/2.png backups/$(FILEN)/img/2.png
	cp ../Screenshots/3.png backups/$(FILEN)/img/3.png
	cp ../Screenshots/4.png backups/$(FILEN)/img/4.png
	cp img/1.png backups/$(FILEN)/img/1r.png
	cp img/2.png backups/$(FILEN)/img/2r.png
	cp img/3.png backups/$(FILEN)/img/3r.png
	cp img/4.png backups/$(FILEN)/img/4r.png
	echo $(BUGS) > backups/$(FILEN)/buglist.txt
	mysql -u husayn -p -h localhost thepin < mktbls.sql

backupStuff:
	mkdir backups/$(FILEN)
	mysqldump --user=husayn --password= -h localhost --databases thepin > backups/$(FILEN)/sqlbkp.SQL
	mkdir backups/$(FILEN)/img
	cp ../Screenshots/1.png backups/$(FILEN)/img/1.png
	cp ../Screenshots/2.png backups/$(FILEN)/img/2.png
	cp ../Screenshots/3.png backups/$(FILEN)/img/3.png
	cp ../Screenshots/4.png backups/$(FILEN)/img/4.png
