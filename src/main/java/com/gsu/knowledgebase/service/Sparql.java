package com.gsu.knowledgebase.service;

import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.QueryResults;
import org.openrdf.query.TupleQuery;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.sparql.SPARQLConnection;
import org.openrdf.repository.sparql.SPARQLRepository;

/**
 * Created by cnytync on 03/05/2017.
 */
public class Sparql {

    public static void main(String[] args) throws Exception {
        SPARQLRepository sparqlRepository = new SPARQLRepository(
                "https://query.wikidata.org/sparql");
        SPARQLConnection sparqlConnection = new SPARQLConnection(
                sparqlRepository);
//        RepositoryConnection sparqlConnection = sparqlRepository.getConnection();

        String query = "SELECT DISTINCT ?property ?propertyLabel ?propertyDescription WHERE { ?property a wikibase:Property . \n" +
                "                                                SERVICE wikibase:label { bd:serviceParam wikibase:language \"en\" } } ORDER BY ?property";

        TupleQuery tupleQuery = sparqlConnection.prepareTupleQuery(
                QueryLanguage.SPARQL, query);
        for (BindingSet bs : QueryResults.asList(tupleQuery.evaluate())) {
            System.out.println(bs);
            String propertyUri = bs.getBinding("property").getValue().toString();
            String propertyLabel = bs.getBinding("propertyLabel").getValue().toString();
            String propertyDescription = bs.getBinding("propertyDescription").getValue().toString();

            String[] arr = propertyUri.split("/");
            String propertyId = arr[arr.length];

        }
    }
}
