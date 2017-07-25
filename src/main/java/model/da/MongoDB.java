package model.da;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import model.enums.ServiceType;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by kasra on 2/22/2017.
 */

@WebListener
public class MongoDB implements ServletContextListener {
    public static final String geoLocationAddressesCollectionName = "geoLocationAddresses";
    public static final String tripsLocationCollectionName = "tripsLocation";
    public static final String activationKeyCollectionName = "activationKeys";
    public static final String onlineTripsCollectionName = "trips";
    private static MongoClient mongoClient;
    private static MongoDatabase db;

    public static MongoDatabase getDatabase() {
        return db;
    }

    public void contextInitialized(ServletContextEvent servletContextEvent) {
        mongoClient = new MongoClient("localhost");
        db = mongoClient.getDatabase("GenoTSWebService");
        mongoDBInit();
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        mongoClient.close();
    }

    private void mongoDBInit() {
        List<String> collectionNames = db.listCollectionNames().into(new ArrayList<>());
        for (ServiceType serviceType : ServiceType.values()) {
            if (collectionNames.contains(serviceType.toString())) {
                continue;
            }
            db.createCollection(serviceType.toString());
            (db.getCollection(serviceType.toString())).createIndex(Indexes.geo2dsphere("loc"));
            (db.getCollection(serviceType.toString())).createIndex(Indexes.ascending("username"));
            (db.getCollection(serviceType.toString())).createIndex(Indexes.ascending("createdAt"), new IndexOptions().expireAfter((long) 30, TimeUnit.MINUTES));
        }//create a collection in mongodb for every service type.

        if (!collectionNames.contains(geoLocationAddressesCollectionName)) {
            db.createCollection(geoLocationAddressesCollectionName);
            (db.getCollection(geoLocationAddressesCollectionName)).createIndex(Indexes.geo2dsphere("loc"));
            (db.getCollection(geoLocationAddressesCollectionName)).createIndex(Indexes.ascending("createdAt"), new IndexOptions().expireAfter((long) 90, TimeUnit.DAYS));
        }
        if (!collectionNames.contains(tripsLocationCollectionName)) {
            db.createCollection(tripsLocationCollectionName);
            (db.getCollection(tripsLocationCollectionName)).createIndex(Indexes.geo2dsphere("originLoc"));
            (db.getCollection(tripsLocationCollectionName)).createIndex(Indexes.geo2dsphere("destinationLoc"));
            (db.getCollection(tripsLocationCollectionName)).createIndex(Indexes.ascending("createdAt"), new IndexOptions().expireAfter((long) 90, TimeUnit.MINUTES));
        }
        if (!collectionNames.contains(activationKeyCollectionName)) {
            db.createCollection(activationKeyCollectionName);
            (db.getCollection(activationKeyCollectionName)).createIndex(Indexes.ascending("createdAt"), new IndexOptions().expireAfter((long) 120, TimeUnit.DAYS));
        }

        if (!collectionNames.contains(onlineTripsCollectionName)) {
            db.createCollection(onlineTripsCollectionName);
            (db.getCollection(onlineTripsCollectionName)).createIndex(Indexes.ascending("createdAt"), new IndexOptions().expireAfter((long) 5, TimeUnit.DAYS));
        }
    }
}
