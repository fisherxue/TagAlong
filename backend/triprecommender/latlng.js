const R = 6371e3

Number.prototype.toRadians = function () { return this * Math.PI / 180; }
Number.prototype.toDegrees = function() { return this / Math.PI * 180; }

/*
 * Latitude Longitude wrapper object
 * Need to actually use this: not for MVP
 */
function LatLng(lat, lng) {
    this.lat = lat;
    this.lng = lng;
}

/*
 * Given latitude and longitude in degrees,
 * returns distance between them in meters
 */ 
function getLatLngDistance(lat1, lng1, lat2, lng2) {
    let llat1 = lat1.toRadians();
    let llat2 = lat2.toRadians();
    let dlat = (lat2-lat1).toRadians();
    let dlng = (lng2-lng1).toRadians();
    let a = Math.sin(dlat/2) * Math.sin(dlat/2) +
            Math.cos(llat1) * Math.cos(llat2) *
            Math.sin(dlng/2) * Math.sin(dlng/2);
    let c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    return R * c;
}

/*
 * Given latitude and longitude in degrees,
 * returns angle between them in degrees
 */
function getLatLngBearing(lat1, lng1, lat2, lng2) {
    let llat1 = lat1.toRadians();
    let llat2 = lat2.toRadians();
    let llng1 = lng1.toRadians();
    let llng2 = lng2.toRadians();

    let y = Math.sin(llng2-llng1) * Math.cos(llat2);
    let x = Math.cos(llat1) * Math.sin(llat2) -
            Math.sin(llat1) * Math.cos(llat2) * Math.cos(llng2-llng1);
    return Math.atan2(y, x).toDegrees();
}

/* 
 * Given points A, B, C as their latitude and longitude in degrees, 
 * returns the shortest distance between 
 * the line A->B and the point C
 * lat1, lng1: A
 * lat2, lng2: B
 * lat3, lng3: C
 */ 
function getLatLngShortestDistanceLinePoint(lat1, lng1, lat2, lng2, lat3, lng3) {
    let bearingAB = getLatLngBearing(lat1, lng1, lat2, lng2).toRadians();
    let bearingAC = getLatLngBearing(lat1, lng1, lat3, lng3).toRadians();
    let distanceAC = getLatLngDistance(lat1, lng1, lat3, lng3);

    return Math.abs(Math.asin(Math.sin(distanceAC/R) * Math.sin(bearingAC-bearingAB)) * R);
}

module.exports = {
    getLatLngDistance, 
    getLatLngBearing, 
    getLatLngShortestDistanceLinePoint
};

// sample usage
/*
var test_A = { // AMS Nest
    lat: 49.2658168,
    lng: -123.2493856
}

var test_B = { // Indigo on Granville
    lat: 49.2633676,
    lng: -123.1385974
}

var test_C = { // Lord Byng SS
    lat: 49.2581269,
    lng: -123.1921067
}

console.log(getLatLngDistance(test_A.lat, test_A.lng, test_B.lat, test_B.lng));
console.log(getLatLngBearing(test_A.lat, test_A.lng, test_B.lat, test_B.lng));
console.log(getLatLngShortestDistanceLinePoint(test_A.lat, test_A.lng, 
        test_B.lat, test_B.lng, test_C.lat, test_C.lng));
*/