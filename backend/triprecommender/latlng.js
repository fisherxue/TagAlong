const R = 6371000;

function toRadians(val) {
    return val * Math.PI / 180;
}

function toDegrees(val) {
    return val / Math.PI * 180;
}

function isValidLat(lat) {
    return lat >= -90 && lat <= 90;
}

function isValidLng(lng) {
    return lng >= -180 && lng <= 180;
}

/*
 * Given latitude and longitude in degrees,
 * returns distance between them in meters
 */ 
function getLatLngDistance(lat1, lng1, lat2, lng2) {
    if (typeof(lat1) !== "number" || typeof(lat2) !== "number" || typeof(lng1) !== "number" || typeof(lng2) !== "number") {
        throw new TypeError("Invalid types, expected Number");
    }
    if (!isValidLat(lat1) || !isValidLat(lat2) || !isValidLng(lng1) || !isValidLng(lng2)) {
        throw new RangeError("Invalid coordinates");
    }

    let llat1 = toRadians(lat1);
    let llat2 = toRadians(lat2);
    let dlat = toRadians((lat2-lat1));
    let dlng = toRadians((lng2-lng1));
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
    if (typeof(lat1) !== "number" || typeof(lat2) !== "number" || typeof(lng1) !== "number" || typeof(lng2) !== "number") {
        throw new TypeError("Invalid types, expected Number");
    }
    if (!isValidLat(lat1) || !isValidLat(lat2) || !isValidLng(lng1) || !isValidLng(lng2)) {
        throw new RangeError("Invalid coordinates");
    }

    let llat1 = toRadians(lat1);
    let llat2 = toRadians(lat2);
    let llng1 = toRadians(lng1);
    let llng2 = toRadians(lng2);

    let y = Math.sin(llng2-llng1) * Math.cos(llat2);
    let x = Math.cos(llat1) * Math.sin(llat2) -
            Math.sin(llat1) * Math.cos(llat2) * Math.cos(llng2-llng1);
    return toDegrees(Math.atan2(y, x));
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
    if (typeof(lat1) !== "number" || typeof(lat2) !== "number" || typeof(lng1) !== "number" || typeof(lng2) !== "number" || typeof(lat3) !== "number" || typeof(lng3) !== "number") {
        throw new TypeError("Invalid types, expected Number");
    }
    if (!isValidLat(lat1) || !isValidLat(lat2) || !isValidLng(lng1) || !isValidLng(lng2) || !isValidLat(lat3) || !isValidLng(lng3)) {
        throw new RangeError("Invalid coordinates");
    }
    
    let bearingAB = toRadians(getLatLngBearing(lat1, lng1, lat2, lng2));
    let bearingAC = toRadians(getLatLngBearing(lat1, lng1, lat3, lng3));
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
