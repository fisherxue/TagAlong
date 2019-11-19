const directions = require("../triprecommender/directions.js");

describe('getDirections', () => {
    it('should exist', () => {
        expect(directions.getDirections).toBeDefined();
    });
    it('should be function', () => {
        expect(typeof directions.getDirections).toBe("function");
    });
    it('should throw an error for empty input', async done => {
        let inputs = {

        };
        try {
            directions.getDirections(inputs, (err, res) => {
            });
        } catch(e) {
            done();
        }
    });
    it('should return something', async done => {
        let inputs = {
            origin: 'Indigo, 2505 Granville St, Vancouver, BC V6H 3G7',
            destination: 'AMS Student Nest, 6133 University Blvd, Vancouver, BC V6T 1Z1',
            mode: "driving",
        };
        directions.getDirections(inputs, (err, res) => {
            expect(typeof res).toBe("object");
            done();
        });
    });
    it('should return something with routes', async done => {
        let inputs = {
            origin: 'Indigo, 2505 Granville St, Vancouver, BC V6H 3G7',
            destination: 'AMS Student Nest, 6133 University Blvd, Vancouver, BC V6T 1Z1',
            mode: "driving",
        };
        directions.getDirections(inputs, (err, res) => {
            expect(res.json.routes).toBeDefined();
            done();
        });
    });
});

describe('getDirectionsWithWaypoints', () => {
    it('should exist', () => {
        expect(directions.getDirectionsWithWaypoints).toBeDefined();
    });
    it('should be function', () => {
        expect(typeof directions.getDirectionsWithWaypoints).toBe("function");
    });
    it('should throw an error for empty input', async (done) => {
        let inputs = {

        };
        try {
            directions.getDirectionsWithWaypoints(inputs, (err, res) => {
            });
        } catch(e) {
            done();
        }
    });
    it('should return something', async done => {
        let inputs = {
            origin: 'Indigo, 2505 Granville St, Vancouver, BC V6H 3G7',
            destination: 'AMS Student Nest, 6133 University Blvd, Vancouver, BC V6T 1Z1',
            waypoints: '3939 W 16th Ave, Vancouver, BC V6R 2C9',
            mode: "driving"
        };
        directions.getDirectionsWithWaypoints(inputs, (err, res) => {
            expect(typeof res).toBe("object");
            done();
        });
    });
    it('should return something with routes', async done => {
        let inputs = {
            origin: 'Indigo, 2505 Granville St, Vancouver, BC V6H 3G7',
            destination: 'AMS Student Nest, 6133 University Blvd, Vancouver, BC V6T 1Z1',
            waypoints: '3939 W 16th Ave, Vancouver, BC V6R 2C9',            
            mode: "driving"
        };
        directions.getDirectionsWithWaypoints(inputs, (err, res) => {
            expect(res.json.routes).toBeDefined();
            done();
        });
    });
});