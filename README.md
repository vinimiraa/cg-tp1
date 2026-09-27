# TP1 - Computação Gráfica

- autor: Vinicius Miranda de Araújo
- professora: Profa.: Rosilane Ribeiro da Mota

## Pré-requisitos

- Java 8 ou superior
- Maven 3.6.0 ou superior

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

##  Documentação

- Como utilizar o programa: [Documentação do usuário](docs/USO.md)
- Arquitetura do sistema: [Arquitetura](docs/ARQUITETURA.md)