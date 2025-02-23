# Sample DSL

```
process Dienstreiseantrag
    version 1
    description "Dienstreiseantrag"

    Mitarbeiter with role Angestellte
        DR-Antrag ausfüllen show DR-Antrag
            with Name as required text and default "${user.name}"
            with Reisebeginn as required date
            with Reiseende as required date
            with Reisezeil as required text
            proceed to DR-Antrag an Vorgesetzer senden
        
        DR-Antrag an Vorgesetzer senden send DR-Antrag to Vorgesetzter
            proceed to Antwort von Vorgesetzter empfangen
        
        Antwort von Vorgesetzter empfangen receive
            Genehmigung proceed to Buchung von Reisestelle empfangen
            Ablehnung proceed to DR-Antrag abgelehnt

        Buchung von Reisestelle empfangen receive
            Buchung proceed to DR antreten

        DR antreten show DR-Antrag
            with Name as readonly text
            with Reisebeginn as readonly date
            with Reiseende as readonly date
            with Reiseziel as readonly text
            and Genehmigung
            with Bemerkung as readonly text
            and Buchung  
            with Hotel as readonly text
            proceed to DR beendet
        
        DR-Antrag abgelehnt show Ablehnung
            with Name as readonly text
            proceed to DR beendet

        DR beendet end process
        
    Vorgesetzter with role Abteilungsleiter
        DR-Antrag empfangen receive DR-Antrag
            proceed to DR-Antrag prüfen
            
        DR-Antrag prüfen show DR-Antrag
            with Name as readonly text
            with Reisebeginn as readonly date
            with Reiseende as readonly date
            with Reiseziel as readonly text
            proceed to Genehmigen show Genehmigung 
                with Bemerkung as text
            proceed to Ablehnen show Ablehnung 
                with Begründung as required text

        Genehmigen send Genehmigung to Mitarbeiter
            proceed to Buchung veranlassen

        Buchung veranlassen send DR-Antrag to Reisestelle

        Ablehnen send Ablehnung to Mitarbeiter
    
    Reisestelle with Role Reisestelle
        DR-Antrag empfangen receive DR-Antrag
            proceed to Buchen

        Buchen show Buchung
            with Hotel as mandatory text
            proceed to Reise gebucht
        
        Reise gebucht send Buchung to Mitarbeiter
```