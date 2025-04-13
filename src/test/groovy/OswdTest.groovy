import org.junit.jupiter.api.Test

import org.opensbpm.oswd.ProcessBuilder
//import static org.opensbpm.oswd.ProcessBuilder.process
import static org.opensbpm.oswd.AttributeType.*

class OswdTest {

    @Test
    void x() {
        def process = new ProcessBuilder().process(name: 'Dienstreiseantrag', version: 1) {
            subject(name: 'Mitarbeiter') {
                task(name: 'Antrag ausfüllen', proceedTo: 'Antrag an Vorgesetzer senden') {
                    object(name: 'DR-Antrag'){
                        attribute(name: 'Name', type: TEXT, mandatory: true)
                        attribute(name: 'Reisebeginn', type: DATE, mandatory: true)
                    }
                }
                send(name: 'Antrag an Vorgesetzer senden',
                        message: 'DR-Antrag',
                        receiver: 'Vorgesetzer',
                        proceedTo: 'Antrag genehmigen')
                receive(name: 'Antwort von Vorgesetzter empfangen'){
                    message(object: 'Genehmigen', proceedTo: 'DR antreten')
                    message(object: 'Ablehnung', proceedTo: 'Abgelehnt')
                }
            }
        }

        new NodePrinter().print(process)
    }


}
