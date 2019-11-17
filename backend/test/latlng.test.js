const latlng = require('../triprecommender/latlng.js');

describe('getLatLngDistance', () => {
    it('should exist', () => {
        expect(latlng.getLatLngDistance).toBeDefined();
    });
    it('should be function', () => {
        expect(typeof latlng.getLatLngDistance).toBe("function");
    });
    it('should return number for input (1, 1, 1, 1)', () => {
        const result = latlng.getLatLngDistance(1, 1, 1, 1);
        expect(result).toBeDefined();
        expect(typeof result).toBe('number');
    });
    it('should throw TypeError for input ("hel", "lo", "wo", "rld")', () => {
        expect(() => latlng.getLatLngDistance("hel", "lo", "wo", "rld")).toThrow(TypeError);
    });
    it('should throw ValueError for input (1000, 1000, 0, 0)', () => {
        expect(() => latlng.getLatLngDistance(1000, 1000, 0, 0)).toThrow(RangeError);
    });
    it('should return 0 for input (90, 90, 90, 90)', () => {
        expect(latlng.getLatLngDistance(90, 90, 90, 90)).toBe(0);
    });
    it('should return 314403 for input (1, 2, 3, 4)', () => {
        expect(latlng.getLatLngDistance(1, 2, 3, 4)).toBeGreaterThan(314400);
        expect(latlng.getLatLngDistance(1, 2, 3, 4)).toBeLessThan(314405);
    });
    it('should return 157 for input (7.5, 7.5, 7.501, 7.501)', () => {
        expect(latlng.getLatLngDistance(7.5, 7.5, 7.501, 7.501)).toBeGreaterThan(156);
        expect(latlng.getLatLngDistance(7.5, 7.5, 7.501, 7.501)).toBeLessThan(157);
    })
});

describe('getLatLngBearing', () => {
    it('should exist', () => {
        expect(latlng.getLatLngBearing).toBeDefined();
    });
    it('should be function', () => {
        expect(typeof latlng.getLatLngBearing).toBe("function");
    });
    it('should return number for input (1, 1, 1, 1)', () => {
        const result = latlng.getLatLngBearing(1, 1, 1, 1);
        expect(result).toBeDefined();
        expect(typeof result).toBe('number');
    });
    it('should throw TypeError for input ("hel", "lo", "wo", "rld")', () => {
        expect(() => latlng.getLatLngBearing("hel", "lo", "wo", "rld")).toThrow(TypeError);
    });
    it('should throw ValueError for input (1000, 1000, 0, 0)', () => {
        expect(() => latlng.getLatLngBearing(1000, 1000, 0, 0)).toThrow(RangeError);
    });
    it('should return 0 for input (90, 90, 90, 90)', () => {
        expect(latlng.getLatLngBearing(90, 90, 90, 90)).toBe(0);
    });
    it('should return 45 for input (7.5, 7.5, 7.501, 7.501)', () => {
        expect(latlng.getLatLngBearing(7.5, 7.5, 7.501, 7.501)).toBeLessThan(45);
        expect(latlng.getLatLngBearing(7.5, 7.5, 7.501, 7.501)).toBeGreaterThan(44);
    });
    it('should return -117 for input (1, 2, 0, 0)', () => {
        expect(latlng.getLatLngBearing(1, 2, 0, 0)).toBeGreaterThan(-120);
        expect(latlng.getLatLngBearing(1, 2, 0, 0)).toBeLessThan(-115);
    });
});

describe('getLatLngShortestDistanceLinePoint', () => {
    it('should exist', () => {
        expect(latlng.getLatLngShortestDistanceLinePoint).toBeDefined();
    });
    it('should be function', () => {
        expect(typeof latlng.getLatLngShortestDistanceLinePoint).toBe("function");
    });
    it('should return number for input (1, 1, 1, 1)', () => {
        const result = latlng.getLatLngShortestDistanceLinePoint(1, 1, 1, 1, 1, 1);
        expect(result).toBeDefined();
        expect(typeof result).toBe('number');
    });
    it('should throw TypeError for input ("hel", "lo", "wo", "rld", "1", "1")', () => {
        expect(() => latlng.getLatLngShortestDistanceLinePoint("hel", "lo", "wo", "rld", 1, 1)).toThrow(TypeError);
    });
    it('should throw ValueError for input (1000, 1000, 0, 0, 0, 0)', () => {
        expect(() => latlng.getLatLngShortestDistanceLinePoint(1000, 1000, 0, 0, 0, 0)).toThrow(RangeError);
    });
    it('should return 0 for input (90, 90, 90, 90, 90, 90)', () => {
        expect(latlng.getLatLngShortestDistanceLinePoint(90, 90, 90, 90, 90, 90)).toBe(0);
    });
    it('should return 642 for input (7.5, 7.5, 7.501, 7.501, 9, 9)', () => {
        expect(latlng.getLatLngShortestDistanceLinePoint(7.5, 7.5, 7.501, 7.501, 9, 9)).toBeLessThan(643);
        expect(latlng.getLatLngShortestDistanceLinePoint(7.5, 7.5, 7.501, 7.501, 9, 9)).toBeGreaterThan(642);
    });
    it('should return 390980 for input (18, 94, 12, 13, 17, 18, 19)', () => {
        expect(latlng.getLatLngShortestDistanceLinePoint(18, 94, 12, 13, 17, 18, 19)).toBeGreaterThan(390970);
        expect(latlng.getLatLngShortestDistanceLinePoint(18, 94, 12, 13, 17, 18, 19)).toBeLessThan(390990);
    });
});