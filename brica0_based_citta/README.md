CITTA
======

Sourcecode of CITTA


To build BriCA-based CITTA,

```
$ cd brica0
brica0$ mvn install
$ cd ../brica0_based_citta
brica0_based_citta$ mvn package 
```

Type the following at the same directory as this file resides to run the DoorKeyDemo:

```
$ (cd src/demo/doorkeydemo/ && mvn -f ../../../pom.xml exec:java -Dexec.mainClass=wba.citta.doorkeydemo.DoorKeyDemo -Dexec.args=test.prop)
```

For Brica-integrated version, do the following:

```
$ (cd src/demo/doorkeydemo/ && mvn -f ../../../pom.xml exec:java -Dexec.mainClass=wba.citta.doorkeydemo.BricaDoorKeyDemo -Dexec.args=test.prop)
```


