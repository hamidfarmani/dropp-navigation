package model.da;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.geojson.Point;
import com.mongodb.client.model.geojson.Position;
import model.entity.persistent.Device;
import model.entity.persistent.Driver;
import model.entity.persistent.Location;
import model.enums.ServiceType;
import model.enums.UserRole;
import model.struct.DistanceInfo;
import model.struct.GeoAddress;
import org.bson.Document;
import util.IOCContainer;
import util.Provider;
import util.converter.UserRoleConverter;

import java.util.Date;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

/**
 * Created by kasra on 3/8/2017.
 */

public class MongoDBQueries {

    public MongoCursor<Document> findNearBy(ServiceType serviceType, Location location, int sizeLimit) {

        return MongoDB.getDatabase().getCollection(serviceType.toString())
                .find(near(
                        "loc", new Point(new Position(location.getLongitude(), location.getLatitude())), Provider.getInstance().getMAX_DISTANCE_FROM_TAXI_1(), 0.0)
                )
                .limit(sizeLimit)
                .iterator();
    }

    public MongoCursor<Document> findNearBy(ServiceType serviceType, Location location, int sizeLimit, String projectionOn) {


        return MongoDB.getDatabase().getCollection(serviceType.toString())
                .find(near(
                        "loc", new Point(new Position(location.getLongitude(), location.getLatitude())), Provider.getInstance().getMAX_DISTANCE_FROM_TAXI_1(), 0.0)
                )
                .projection(Projections.include(projectionOn))
                .limit(sizeLimit)
                .iterator();

    }


    public void insertDriver(Driver driver, double rate, Device device, Location location) {

        double[] longLat = {location.getLongitude(), location.getLatitude()};
        BasicDBObject locObject = new BasicDBObject();
        locObject.put("type", "Point");
        locObject.put("coordinates", longLat);
        BasicDBObject driverDoc = new BasicDBObject("loc", locObject)
                .append("username", driver.getUsername())
                .append("firstName", driver.getFirstName())
                .append("lastName", driver.getLastName())
                .append("phoneNumber", driver.getPhoneNumber())
                .append("vehicleModel", driver.getVehicle().getCar().getName())
                .append("vehicleLicencePlate", driver.getVehicle().getLicencePlate())
                .append("vehicleColor", driver.getVehicle().getColor())
                .append("deviceFCMKey", device.getFCMToken())
                .append("rate",rate)
                .append("createdAt", new Date());


        MongoDB.getDatabase().getCollection(driver.getServiceType().toString(), BasicDBObject.class).insertOne(driverDoc);
    }

    public void updateDriverCoordinates(String username, ServiceType serviceType, Location location) {
        double[] longLat = {location.getLongitude(), location.getLatitude()};
        BasicDBObject locObject = new BasicDBObject();
        locObject.put("type", "Point");
        locObject.put("coordinates", longLat);
        MongoDB.getDatabase().getCollection(serviceType.toString(), BasicDBObject.class).updateOne(
                eq("username", username), combine(set("loc", locObject), set("createdAt", new Date()))
        );
    }

    public void removeDriver(String username, ServiceType serviceType) {
        MongoDB.getDatabase().getCollection(serviceType.toString(), BasicDBObject.class).deleteOne(eq("username", username));
    }

    public BasicDBObject fetchDriverInfo(String username, ServiceType serviceType) {

        MongoCursor<BasicDBObject> cursor = MongoDB.getDatabase().getCollection(serviceType.toString(), BasicDBObject.class)
                .find(eq("username", username))
                .projection(Projections.exclude("driverDeviceFCMToken", "_id"))
                .limit(1)
                .iterator();
        if ((cursor.hasNext())) {
            return cursor.next();
        }
        return null;
    }

    public BasicDBObject isDriverExist(String username, ServiceType serviceType) {
        MongoCursor<BasicDBObject> cursor = MongoDB.getDatabase().getCollection(serviceType.toString(), BasicDBObject.class)
                .find(eq("username", username))
                .projection(Projections.include("_id"))
                .limit(1)
                .iterator();
        if ((cursor.hasNext())) {
            return cursor.next();
        }
        return null;
    }

    public void insertGeoAddress(Location location, GeoAddress geoAddress) {
        double[] longLat = {location.getLongitude(), location.getLatitude()};
        BasicDBObject locObject = new BasicDBObject();

        locObject.put("type", "Point");
        locObject.put("coordinates", longLat);
        BasicDBObject addressDoc = new BasicDBObject("loc", locObject)
                .append("street_address", geoAddress.getStreet_address())
                .append("route", geoAddress.getRoute())
                .append("country", geoAddress.getCountry())
                .append("state", geoAddress.getState())
                .append("city", geoAddress.getCity())
                .append("createdAt", new Date());

        MongoDB.getDatabase().getCollection(MongoDB.geoLocationAddressesCollectionName, BasicDBObject.class).insertOne(addressDoc);
    }

    public GeoAddress getGeoAddress(Location location) {

        BasicDBObject dbObject = MongoDB.getDatabase().getCollection(MongoDB.geoLocationAddressesCollectionName, BasicDBObject.class)
                .find(near(
                        "loc", new Point(new Position(location.getLongitude(), location.getLatitude())), 20.0, 0.0
                ))
                .limit(1)
                .iterator().tryNext();
        if (dbObject != null) {
            return new GeoAddress(dbObject.getString("street_address"),
                    dbObject.getString("route"),
                    dbObject.getString("country"),
                    dbObject.getString("state"),
                    dbObject.getString("city")
            );
        }
        return null;
    }

    public void insertDistanceInfo(Location origin, Location destination, DistanceInfo distanceInfo) {
        double[] originLongLat = {origin.getLongitude(), origin.getLatitude()};
        double[] destinationLongLat = {destination.getLongitude(), destination.getLatitude()};
        BasicDBObject originLocObject = new BasicDBObject();
        BasicDBObject destinationLocObject = new BasicDBObject();
        originLocObject.put("type", "Point");
        originLocObject.put("coordinates", originLongLat);
        destinationLocObject.put("type", "Point");
        destinationLocObject.put("coordinates", destinationLongLat);

        BasicDBObject distanceInfoDoc = new BasicDBObject("originLoc", originLocObject)
                .append("destinationLoc", destinationLocObject)
                .append("distanceInMeter", distanceInfo.distanceInMeter)
                .append("ETA", distanceInfo.ETA)
                .append("originAddress", distanceInfo.originAddress)
                .append("destAddress", distanceInfo.destAddress)
                .append("createdAt", new Date());

        MongoDB.getDatabase().getCollection(MongoDB.tripsLocationCollectionName, BasicDBObject.class).insertOne(distanceInfoDoc);
    }

    public DistanceInfo getDistanceInfo(Location origin, Location destination) {

        MongoCursor<BasicDBObject> originDbCursor = MongoDB.getDatabase().getCollection(MongoDB.tripsLocationCollectionName, BasicDBObject.class)
                .find(near("originLoc", new Point(new Position(origin.getLongitude(), origin.getLatitude())), 50.0, 0.0))
                .limit(50)
                .iterator();

        MongoCursor<BasicDBObject> destDbCursor = MongoDB.getDatabase().getCollection(MongoDB.tripsLocationCollectionName, BasicDBObject.class)
                .find(near("destinationLoc", new Point(new Position(destination.getLongitude(), destination.getLatitude())), 200.0, 0.0))
                .limit(50)
                .iterator();

        while (originDbCursor.hasNext()) {
            if (!destDbCursor.hasNext()) {
                break;
            }
            BasicDBObject originObject = originDbCursor.next();
            while (destDbCursor.hasNext()) {
                if (originObject.get("_id").equals(destDbCursor.next().get("_id"))) {
                    return new DistanceInfo(originObject.getString("originAddress"),
                            originObject.getString("destAddress"),
                            originObject.getLong("ETA"),
                            originObject.getLong("distanceInMeter")
                    );
                }
            }
        }

        return null;
    }

    public void insertActivationKey(UserRole userRole, String username, String activationKey) {
        char userRoleChar = ((UserRoleConverter) IOCContainer.getBean("userRoleConverter")).convertToDatabaseColumn(userRole);
        BasicDBObject doc = new BasicDBObject("userRole", userRoleChar)
                .append("username", username)
                .append("activationKey", activationKey)
                .append("createdAt", new Date());
        deleteActivationKey(userRole,username);
        MongoDB.getDatabase().getCollection(MongoDB.activationKeyCollectionName, BasicDBObject.class).insertOne(doc);
    }

    public BasicDBObject getActivationKey(UserRole userRole, String username) {
        char userRoleChar = ((UserRoleConverter) IOCContainer.getBean("userRoleConverter")).convertToDatabaseColumn(userRole);
        BasicDBObject basicDBObject = MongoDB.getDatabase().getCollection(MongoDB.activationKeyCollectionName, BasicDBObject.class)
                .find(and(eq("username", username), eq("userRole", userRoleChar)))
                .first();
        return basicDBObject;
    }

    public void deleteActivationKey(UserRole userRole, String username) {
        char userRoleChar = ((UserRoleConverter) IOCContainer.getBean("userRoleConverter")).convertToDatabaseColumn(userRole);
        MongoDB.getDatabase().getCollection(MongoDB.activationKeyCollectionName, BasicDBObject.class)
                .deleteOne(and(eq("username", username), eq("userRole", userRoleChar)));
    }

    public MongoCursor<Document> getDrivers(ServiceType serviceType){
        return(MongoDB.getDatabase().getCollection(serviceType.toString()).find().limit(10).iterator());
    }

    public MongoCursor<Document> getOnlineDrivers(ServiceType serviceType){
        MongoCursor<Document> docs = MongoDB.getDatabase().getCollection(serviceType.toString()).find().limit(10).iterator();
        return docs;
    }

    public MongoCursor<Document> getOnlineTrips(){
        MongoCursor<Document> docs = MongoDB.getDatabase().getCollection("trips").find().iterator();
        return docs;
    }

    public Long getNumberOfOnlineDriversCount(ServiceType serviceType){
        return (MongoDB.getDatabase().getCollection(serviceType.toString()).count());
    }

    public Long getNumberOfOnlineTripsCount(){
        return (MongoDB.getDatabase().getCollection("trips").count());
    }

}


