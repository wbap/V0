set CLASSDIR=g:\java
set CLASSPATH=%CLASSDIR%;%CLASSPATH%

cd %CLASSDIR%\eventtest

java -Xms400m -Xmx500m doorkeydemo.DoorKeyDemo task1_decompose.prop
