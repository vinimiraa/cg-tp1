# TP1 - Computação Gráfica

## Build e empacotamento

```bash
mvn clean package
java -jar target/tp1-1.0-SNAPSHOT.jar
```

App-image standalone (pasta com `.exe` + runtime Java embutido, não precisa instalar nada):

```bash
jpackage --type app-image --input target --dest dist --name "cg-tp1-vinicius-miranda" --main-jar tp1-1.0-SNAPSHOT.jar --main-class com.puc.cg.App
```

Instalador Windows de verdade (`.exe`) — **precisa do [WiX Toolset](https://wixtoolset.org) instalado e no PATH** antes de rodar:

```bash
jpackage --type exe --input target --dest dist-installer --name "cg-tp1-vinicius-miranda" --main-jar tp1-1.0-SNAPSHOT.jar --main-class com.puc.cg.App --win-dir-chooser --win-menu --win-shortcut
```
