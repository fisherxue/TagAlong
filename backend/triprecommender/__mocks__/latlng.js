const latlng = jest.genMockFromModule("../latlng.js");

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

function getLatLngDistance(lat1, lng1, lat2, lng2) {
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

function getLatLngBearing(lat1, lng1, lat2, lng2) {
    let llat1 = toRadians(lat1);
    let llat2 = toRadians(lat2);
    let llng1 = toRadians(lng1);
    let llng2 = toRadians(lng2);

    let y = Math.sin(llng2-llng1) * Math.cos(llat2);
    let x = Math.cos(llat1) * Math.sin(llat2) -
            Math.sin(llat1) * Math.cos(llat2) * Math.cos(llng2-llng1);
    return toDegrees(Math.atan2(y, x));
}

function getLatLngShortestDistanceLinePoint(lat1, lng1, lat2, lng2, lat3, lng3) {
    let bearingAB = toRadians(getLatLngBearing(lat1, lng1, lat2, lng2));
    let bearingAC = toRadians(getLatLngBearing(lat1, lng1, lat3, lng3));
    let distanceAC = getLatLngDistance(lat1, lng1, lat3, lng3);

    return Math.abs(Math.asin(Math.sin(distanceAC/R) * Math.sin(bearingAC-bearingAB)) * R);
}

latlng.getLatLngBearing = getLatLngBearing;
latlng.getLatLngDistance = getLatLngDistance;
latlng.getLatLngShortestDistanceLinePoint = getLatLngShortestDistanceLinePoint;

module.exports = latlng;