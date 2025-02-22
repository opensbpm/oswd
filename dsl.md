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