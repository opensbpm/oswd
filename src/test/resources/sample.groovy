import org.opensbpm.oswd.ProcessBuilder

import static org.opensbpm.oswd.AttributeType.DATE
import static org.opensbpm.oswd.AttributeType.TEXT

new ProcessBuilder().process(name: 'Dienstreiseantrag', version: 1) {
    subject(name: 'Mitarbeiter', role: 'Angestellter') {
        task(name: 'Antrag ausfüllen') {
            object(name: 'DR-Antrag') {
                attribute(name: 'Name', type: TEXT, required: true)
                attribute(name: 'Reisebeginn', type: DATE, required: true)
            }
            proceedTo('Antrag an Vorgesetzter senden')
        }
        send(name: 'Antrag an Vorgesetzter senden',
                message: 'DR-Antrag',
                receiver: 'Vorgesetzter',
                proceedTo: 'Antwort von Vorgesetzter empfangen')
        receive(name: 'Antwort von Vorgesetzter empfangen') {
            message(object: 'Genehmigung', proceedTo: 'DR antreten')
            message(object: 'Ablehnung', proceedTo: 'Abgelehnt')
        }
        task(name: 'DR antreten') {
            object(name: 'DR-Antrag') {
                attribute(name: 'Name', type: TEXT, readonly: true)
                attribute(name: 'Reisebeginn', type: DATE, readonly: true)
            }
            proceedTo('Ende')
        }
        task(name: 'Abgelehnt') {
            object(name: 'Ablehnung') {
                attribute(name: 'Begründung', type: TEXT, readonly: true)
            }
            proceedTo('Ende')
        }
        task(name: 'Ende')
    }
    subject(name: 'Vorgesetzter', role: 'Abteilungsleiter') {
        receive(name: 'DR-Antrag empfangen') {
            message(object: 'DR-Antrag', proceedTo: 'DR-Antrag prüfen')
        }
        task(name: 'DR-Antrag prüfen') {
            object(name: 'DR-Antrag') {
                attribute(name: 'Name', type: TEXT, readonly: true)
                attribute(name: 'Reisebeginn', type: DATE, readonly: true)
                attribute(name: 'Reiseende', type: DATE, readonly: true)
                attribute(name: 'Reiseziel', type: TEXT, readonly: true)
            }
            proceedTo('Genehmigen') {
                object(name: 'Genehmigung') {
                    attribute(name: 'Bemerkung', type: TEXT)
                }
            }
            proceedTo('Ablehnen') {
                object(name: 'Ablehnung') {
                    attribute(name: 'Begründung', type: TEXT, required: true)
                }
            }
        }
        send(name: 'Genehmigen',
                message: 'Genehmigung',
                receiver: 'Mitarbeiter',
                proceedTo: 'Buchung veranlassen')
        send(name: 'Buchung veranlassen',
                message: 'DR-Antrag',
                receiver: 'Reisestelle',
                proceedTo: 'Ende')
        send(name: 'Ablehnen',
                message: 'Ablehnung',
                receiver: 'Mitarbeiter',
                proceedTo: 'Ende')
        task(name: 'Ende')
    }
    subject(name: 'Reisestelle', role: 'Reisestelle') {
        receive(name: 'DR-Antrag empfangen') {
            message(object: 'DR-Antrag', proceedTo: 'Buchen')
        }
        task(name: 'Buchen') {
            object(name: 'DR-Antrag') {
                attribute(name: 'Name', type: TEXT, readonly: true)
                attribute(name: 'Reisebeginn', type: DATE, readonly: true)
                attribute(name: 'Reiseende', type: DATE, readonly: true)
                attribute(name: 'Reiseziel', type: TEXT, readonly: true)
            }
            proceedTo('Reise gebucht')
        }
        task(name: 'Reise gebucht')
    }
}
