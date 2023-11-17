import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

class Airline {
    String id;
    String name;
    List<Flight> flights;

    public Airline(String id, String name) {
        this.id = id;
        this.name = name;
        this.flights = new ArrayList<>();
    }
}

class Flight {
    String id;
    String destination;

    public Flight(String id, String destination) {
        this.id = id;
        this.destination = destination;
    }
}

class AirportDomainXml {
    public static void main(String[] args) {
        try {
            // Create or Load XML file
            File file = new File("src/airport_domain.xml");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Create document or load existing
            Document document;
            if (file.exists()) {
                document = builder.parse(file);
            } else {
                document = builder.newDocument();
                Element rootElement = document.createElement("data");
                document.appendChild(rootElement);
            }

            // Create Airlines and Flights
            Airline airline1 = new Airline("A001", "Airline1");
            airline1.flights.add(new Flight("F001", "Destination1"));
            airline1.flights.add(new Flight("F002", "Destination2"));

            Airline airline2 = new Airline("A002", "Airline2");
            airline2.flights.add(new Flight("F003", "Destination3"));

            // Add Airlines and Flights to XML
            addAirlineToXml(document, airline1);
            addAirlineToXml(document, airline2);

            // Read and display data
            readAirlinesAndFlights(document);

            // Update a flight
            updateFlight(document, "F002", "UpdatedDestination");

            // Read and display updated data
            readAirlinesAndFlights(document);

            // Delete a flight
            deleteFlight(document, "F001");

            // Read and display final data
            readAirlinesAndFlights(document);

            // Save changes to the XML file
            saveDocument(document, file);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addAirlineToXml(Document document, Airline airline) {
        Element rootElement = document.getDocumentElement();
        Element airlineElement = document.createElement("airline");
        airlineElement.setAttribute("id", airline.id);

        Element nameElement = document.createElement("name");
        nameElement.appendChild(document.createTextNode(airline.name));

        airlineElement.appendChild(nameElement);

        for (Flight flight : airline.flights) {
            Element flightElement = document.createElement("flight");
            flightElement.setAttribute("id", flight.id);

            Element destinationElement = document.createElement("destination");
            destinationElement.appendChild(document.createTextNode(flight.destination));

            flightElement.appendChild(destinationElement);
            airlineElement.appendChild(flightElement);
        }

        rootElement.appendChild(airlineElement);
        System.out.println("\nAirline created: " + airline.name);
    }

    private static void readAirlinesAndFlights(Document document) {
        NodeList airlineList = document.getElementsByTagName("airline");

        System.out.println("\nAirlines and Flights:");
        for (int i = 0; i < airlineList.getLength(); i++) {
            Element airline = (Element) airlineList.item(i);
            System.out.println("ID: " + airline.getAttribute("id"));
            System.out.println("Name: " + airline.getElementsByTagName("name").item(0).getTextContent());

            NodeList flightList = airline.getElementsByTagName("flight");
            if (flightList.getLength() > 0) {
                System.out.println("Flights:");
                for (int j = 0; j < flightList.getLength(); j++) {
                    Element flight = (Element) flightList.item(j);
                    System.out.println("  - ID: " + flight.getAttribute("id"));
                    System.out.println("    Destination: " + flight.getElementsByTagName("destination").item(0).getTextContent());
                }
            }
            System.out.println();
        }
    }

    private static void updateFlight(Document document, String flightId, String updatedDestination) {
        NodeList flightList = document.getElementsByTagName("flight");

        for (int i = 0; i < flightList.getLength(); i++) {
            Element flight = (Element) flightList.item(i);
            if (flight.getAttribute("id").equals(flightId)) {
                flight.getElementsByTagName("destination").item(0).setTextContent(updatedDestination);
                System.out.println("\nUpdating Flight: " + flightId);
                return;
            }
        }

        System.out.println("\nFlight not found for update: " + flightId);
    }

    private static void deleteFlight(Document document, String flightId) {
        NodeList flightList = document.getElementsByTagName("flight");

        for (int i = 0; i < flightList.getLength(); i++) {
            Element flight = (Element) flightList.item(i);
            if (flight.getAttribute("id").equals(flightId)) {
                flight.getParentNode().removeChild(flight);
                System.out.println("\nDeleting Flight: " + flightId);
                return;
            }
        }

        System.out.println("\nFlight not found for deletion: " + flightId);
    }

    private static void saveDocument(Document document, File file) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(file);
            transformer.transform(source, result);
            System.out.println("\nChanges saved to the XML file.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
