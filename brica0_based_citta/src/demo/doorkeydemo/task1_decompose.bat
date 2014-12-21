set CLASSDIR=g:\java
set CLASSPATH=%CLASSDIR%;%CLASSPATH%

cd %CLASSDIR%\doorkeydemo

java -Xms400m -Xmx500m doorkeydemo.DoorKeyDemo task1_decompose.prop
