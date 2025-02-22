# Konzept für eine domänenspezifische Sprache (DSL) zur Prozessdefinition in OpenSBPM

## Einführung
OpenSBPM ist ein Open-Source-Projekt mit dem Ziel, eine einsatzfähige Applikation für die Ausführung von subjektorientierten 
Geschäftsprozessmodellen zu erstellen. Aktuell besteht das OpenSBPM-Projekt aus einer einfachen Webpräsentation und der auf 
GitHub gehosteten und veröffentlichten OpenSBPM:Engine. OpenSBPM:Engine ist eine freie, unter GPLv3 lizenzierte, Open-Source-Implementierung 
einer Workflow-Engine für subjektorientierte Geschäftsprozessmodelle nach Fleischmann, Schmidt, Stary, u. a. (2011).

OpenSBPM:Engine ist als Java-Library implementiert und verwendet das Spring Boot Framework für Dependency Injection und 
Spring Boot Data JPA für die Persistenz der Workflow-Modelle und der Status der Prozesse. Als Datastore kann jede von 
Jakarta Persistence API unterstützte, relationale Datenbank verwendet werden. Es gibt aber noch keine Open-Source-Serveranwendung 
oder eine REST-API für dieses Projekt.

## Zielsetzung
Die DSL dient der Definition von Geschäftsprozessen in einer natürlichen, leicht verständlichen Syntax. Sie soll die Modellierung 
und Ausführung von Workflows ermöglichen und dabei eine klare Subjekt-Verb-Struktur nutzen. Die DSL wird in _Groovy_ implementiert 
und in **OpenSBPM** integriert zw. werden damit die XML-Definitionen von OpenSBM generiert.

## Grundlegende Struktur
Die DSL orientiert sich an einer **Subjekt-Verb-Syntax** und ermöglicht die Modellierung von Prozessen mit:
- **Rollen** (`with Role`), die die Akteure definieren
- **Erstellung (`creates`) und Empfang (`receives`) von Anträgen**
- **Formularfeldern (`show textfield`, `show datefield`, etc.)** zur Eingabe und Anzeige von Daten
- **Entscheidungen und Weiterleitung (`send ... to ...`)**
- **Prozessende (`end`)**

## DSL-Syntax
Ein Prozess wird mit dem Schlüsselwort `process` definiert und enthält verschiedene Aufgaben (Tasks), die von Akteuren 
(Subjekte) mit bestimmten Rollen ausgeführt werden.

### Beispiel:
(Auszug)
```groovy
process Dienstreiseantrag {
    Mitarbeiter with Role Antragsteller creates DR-Antrag
        show textfield Name default "${user.name}"
        show datefield Reisebeginn mandatory
        show datefield Reiseende mandatory
        show text Reiseziel mandatory
        send to Vorgesetzter

    Vorgesetzter with Role Abteilungsleiter receives DR-Antrag
        show textfield Name readonly
        show datefield Reisebeginn readonly
        show datefield Reiseende readonly
        show text Reiseziel readonly
        send Ablehnung
            show textfield Begründung mandatory
            to Mitarbeiter
        send Genehmigung to Mitarbeiter and Reisestelle

    Mitarbeiter with Role Antragsteller receives Ablehnung
        show textfield Begründung readonly
        end

    Reisestelle with Role Verwaltung receives Genehmigung
        show textfield Name readonly
        show datefield Reisebeginn readonly
        show datefield Reiseende readonly
        show text Reiseziel readonly
}
```

daraus sollte diese XML-Ausgabe (Auszug) generiert werden können:

```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<pm:process
    xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'
    xmlns:pm='http://api.opensbpm.org/processmodel'>

    <name>Dienstreiseantrag Seite/103</name>
    <version>1</version>
    <description>Dienstreiseantrag von 'Subjektorientiertes Prozessmanagment' Seite 103</description>

    <userSubject starter="true">
        <name>Mitarbeiter</name>
        <roles>Angestellte</roles>
        <functionState eventType="START">
            <name>DR-Antrag ausfüllen</name>
            <object>
                <name>DR-Antrag</name>
                <attribute>
                    <name>Name</name>
                    <permission>WRITE</permission>
                    <mandatory>true</mandatory>
                </attribute>
                <attribute>
                    <name>Reisebeginn</name>
                    <permission>WRITE</permission>
                    <mandatory>true</mandatory>
                </attribute>
                <attribute>
                    <name>Reiseende</name>
                    <permission>WRITE</permission>
                    <mandatory>true</mandatory>
                </attribute>
                <attribute>
                    <name>Reiseziel</name>
                    <permission>WRITE</permission>
                    <mandatory>true</mandatory>
                </attribute>
            </object>
            <toState>DR-Antrag an Vorgesetzer senden</toState>
        </functionState>
        <sendState>
            <name>DR-Antrag an Vorgesetzer senden</name>
            <receiver>Vorgesetzter</receiver>
            <message>DR-Antrag</message>
            <toState>Antwort von Vorgesetzter empfangen</toState>
        </sendState>
        <functionState>
            <name>DR antreten</name>
            <toState>DR beendet</toState>
        </functionState>
        <functionState eventType="END">
            <name>DR beendet</name>
        </functionState>
        <functionState eventType="END">
            <name>Abgelehnt</name>
        </functionState>
    </userSubject>
    <userSubject>
        <name>Vorgesetzter</name>
        <roles>Abteilungsleiter</roles>
        <receiveState eventType="START">
            <name>DR-Antrag empfangen</name>
            <message>
                <object>DR-Antrag</object>
                <toState>DR-Antrag prüfen</toState>
            </message>
        </receiveState>
        <functionState>
            <name>DR-Antrag prüfen</name>
            <object>
                <name>DR-Antrag</name>
                <attribute>
                    <name>Name</name>
                    <permission>READ</permission>
                </attribute>
                <attribute>
                    <name>Reisebeginn</name>
                    <permission>WRITE</permission>
                    <mandatory>true</mandatory>
                </attribute>
                <attribute>
                    <name>Reiseende</name>
                    <permission>WRITE</permission>
                    <mandatory>true</mandatory>
                </attribute>
                <attribute>
                    <name>Reiseziel</name>
                    <permission>READ</permission>
                </attribute>
            </object>
            <toState>Genehmigen</toState>
            <toState>Ablehnen</toState>
        </functionState>
  ...
</pm:process>
```

Vollständiges XML auf [GitHub](https://github.com/opensbpm/engine/blob/main/examples/src/main/resources/org/opensbpm/engine/examples/Dienstreiseantrag.xml )
einsehbar.


## Elemente der DSL
### Prozessdefinition
- Ein Prozess beginnt mit `process Prozessname { ... }`

### Rollen und Akteure
- `Subjekt with Role Rollenname` definiert den Akteur und dessen Rolle.

### Erstellung und Empfang von Anträgen
- `creates <Document>` beschreibt die Erstellung eines Objekts/Dokument.
- `receives <Document>` beschreibt den Empfang eines Objekts/Dokument.

### Formularfelder
- `show textfield Feldname [Attribute]`
- `show datefield Feldname [Attribute]`
- `show textfield Feldname [Attribute]`
- Mögliche Attribute:
  - `default "Wert"`
  - `mandatory`
  - `readonly`

Formularfelder für die Basistypen Text, Zahl (mit Formatierung) Datum, Boolean

### Entscheidungswege
- `send Entscheidungstyp to Empfänger`
- `send Entscheidungstyp { Formularfelder } to Empfänger`
- Mehrere Empfänger werden mit `and` getrennt

Die "send" werden in der GUI als Action-Buttons dargestellt und können optional wiederum Formularfelder enthalten damit  
die Nachrichten parametrisiert werden können. 

### Prozessende
- `end` markiert das Ende für einen Akteur

## Implementierung in Groovy
Dien OpenSBPM:Engine verwendet bereits Groovy. Es können bereits für Default-Wert Groovy-Scripts in den Prozessdefinitionen
hinterlegt werden. Es würde sich deshalb anbieten die DSL-Implementierung ebenfalls in Groovy zu erstellen.

## Erweiterungsmöglichkeiten
Die DSL kann in Zukunft um folgende Features erweitert werden:
- Validierungsregeln für Formularfelder (z. B. `Reisebeginn < Reiseende`)
- Dynamische Bedingungsprüfung für `send`-Aktionen

## Fazit
Diese DSL ermöglicht eine **intuitive Modellierung von Geschäftsprozessen** mit einer **Subjekt-Verb-Form**. Sie reduziert 
die Komplexität und erleichtert die Umsetzung von Prozessen in OpenSBPM durch eine deklarative, gut lesbare Syntax.

