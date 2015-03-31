CITTA
======

Sourcecode of CITTA

Type the following at the same directory as this file resides to run the DoorKeyDemo:

```
$ (cd src/demo/doorkeydemo/ && mvn -f ../../../pom.xml exec:java -Dexec.mainClass=wba.citta.doorkeydemo.DoorKeyDemo -Dexec.args=test.prop)
```

For Brica-integrated version, do the following:

```
$ (cd src/demo/doorkeydemo/ && mvn -f ../../../pom.xml exec:java -Dexec.mainClass=wba.citta.doorkeydemo.BricaDoorKeyDemo -Dexec.args=test.prop)
```
