const recommender = require("../triprecommender/recommender.js");
const fs = require("fs");

jest.mock("../triprecommender/directions.js");

describe("cutTripsByBearing", () => {
    it('should exist', () => {
        expect(recommender.cutTripsByBearing).toBeDefined();
    });
    it('should be a function', () => {
       expect(typeof recommender.cutTripsByBearing).toBe("function"); 
    });
    it('should return empty on null inputs', () => {
        let driverTrip;
        let riderTrips;
        expect(recommender.cutTripsByBearing(driverTrip, riderTrips)).toHaveLength(0);
    });
    it('should return empty on null inputs for ridertrips', () => {
        let driverTrip;
        let riderTrips;
        fs.readFile("./triprecommender/__test__/driverTrip1.json", "utf8", (err, data) => {
            driverTrip = data;
            expect(recommender.cutTripsByBearing(driverTrip, riderTrips)).toHaveLength(0);
        });
    });
    it('should return correct value', () => {
        let driverTrip;
        let riderTrips = [];
        fs.readFile("./__test__/driverTrip1.json", "utf8", (err, data) => {
            driverTrip = JSON.parse(data);
            fs.readFile("./__test__/riderTrip1.json", "utf8", (err, data1) => {
                riderTrips.push(JSON.parse(data1));
                fs.readFile("./__test__/riderTrip2.json", "utf8", (err, data2) => {
                    riderTrips.push(JSON.parse(data2));
                    fs.readFile("./__test__/riderTrip3.json", "utf8", (err, data3) => {
                        riderTrips.push(JSON.parse(data3));
                        expect(recommender.cutTripsByBearing(driverTrip, riderTrips)).toHaveLength(3);
                    });
                });
            });
        });
    });
});

describe("cutTripsByDistance", () => {
    it('should exist', () => {
        expect(recommender.cutTripsByDistance).toBeDefined();
    });
    it('should be a function', () => {
       expect(typeof recommender.cutTripsByDistance).toBe("function"); 
    });
    it('should return empty on null inputs', () => {
        let driverTrip;
        let riderTrips;
        expect(recommender.cutTripsByDistance(driverTrip, riderTrips)).toHaveLength(0);
    });
    it('should return empty on null inputs for ridertrips', () => {
        let driverTrip;
        let riderTrips;
        fs.readFile("./triprecommender/__test__/driverTrip1.json", "utf8", (err, data) => {
            driverTrip = data;
            expect(recommender.cutTripsByDistance(driverTrip, riderTrips)).toHaveLength(0);
        });
    });
    it('should return correct value', () => {
        let driverTrip;
        let riderTrips = [];
        fs.readFile("./__test__/driverTrip1.json", "utf8", (err, data) => {
            driverTrip = JSON.parse(data);
            fs.readFile("./__test__/riderTrip1.json", "utf8", (err, data1) => {
                riderTrips.push(JSON.parse(data1));
                fs.readFile("./__test__/riderTrip2.json", "utf8", (err, data2) => {
                    riderTrips.push(JSON.parse(data2));
                    fs.readFile("./__test__/riderTrip3.json", "utf8", (err, data3) => {
                        riderTrips.push(JSON.parse(data3));
                        expect(recommender.cutTripsByDistance(driverTrip, riderTrips)).toHaveLength(3);
                    });
                });
            });
        });
    });
});

describe("modifyTrip", () => {
    it('should exist', () => {
        expect(recommender.modifyTrip).toBeDefined();
    });
    it('should be function', () => {
        expect(typeof recommender.modifyTrip).toBe("function");
    });
    it('should return driverTrip if no riderTrips', async done => {
        let driverTrip;
        let riderTrips = [];
        fs.readFile("./__test__/driverTrip1.json", "utf8", (err, data) => {
            driverTrip = JSON.parse(data);
            recommender.modifyTrip(driverTrip, riderTrips, (res) => {
                expect(res).toBe(driverTrip.tripRoute);
                done();
            });
        });
    });
    it('should return json with one riderTrip', async done => {
        let driverTrip;
        let riderTrips = [];
        fs.readFile("./__test__/driverTrip1.json", "utf8", (err, data) => {
            driverTrip = JSON.parse(data);
            fs.readFile("./__test__/riderTrip1.json", "utf8", (err, data1) => {
                riderTrips.push(JSON.parse(data1));
                recommender.modifyTrip(driverTrip, riderTrips, (res) => {
                    expect(typeof res).toBe("object");
                    done();
                });
            });
        });
    });
    it('should return proper json with routes with one riderTrip', async done => {
        let driverTrip;
        let riderTrips = [];
        fs.readFile("./__test__/driverTrip1.json", "utf8", (err, data) => {
            driverTrip = JSON.parse(data);
            fs.readFile("./__test__/riderTrip1.json", "utf8", (err, data1) => {
                riderTrips.push(JSON.parse(data1));
                recommender.modifyTrip(driverTrip, riderTrips, (res) => {
                    expect(res.routes).toBeDefined();
                    done();
                });
            });
        });
    });
    it('should return json with 3 riderTrips', async done => {
        let driverTrip;
        let riderTrips = [];
        fs.readFile("./__test__/driverTrip1.json", "utf8", (err, data) => {
            driverTrip = JSON.parse(data);
            fs.readFile("./__test__/riderTrip1.json", "utf8", (err, data1) => {
                riderTrips.push(JSON.parse(data1));
                fs.readFile("./__test__/riderTrip2.json", "utf8", (err, data2) => {
                    riderTrips.push(JSON.parse(data2));
                    fs.readFile("./__test__/riderTrip3.json", "utf8", (err, data3) => {
                        riderTrips.push(JSON.parse(data3));
                        recommender.modifyTrip(driverTrip, riderTrips, (res) => {
                            expect(typeof res).toBe("object");
                            done();
                        });
                    });
                });
            });
        });
    });
    it('should return proper json with routes with 3 riderTrips', async done => {
        let driverTrip;
        let riderTrips = [];
        fs.readFile("./__test__/driverTrip1.json", "utf8", (err, data) => {
            driverTrip = JSON.parse(data);
            fs.readFile("./__test__/riderTrip1.json", "utf8", (err, data1) => {
                riderTrips.push(JSON.parse(data1));
                fs.readFile("./__test__/riderTrip2.json", "utf8", (err, data2) => {
                    riderTrips.push(JSON.parse(data2));
                    fs.readFile("./__test__/riderTrip3.json", "utf8", (err, data3) => {
                        riderTrips.push(JSON.parse(data3));
                        recommender.modifyTrip(driverTrip, riderTrips, (res) => {
                            fs.readFile("./__test__/outputTrip1.json", "utf8", (err, out) => {
                                expect(res).toStrictEqual(JSON.parse(out));
                                done();
                            });

                        });
                    });
                });
            });
        });
    });
});


describe("getInterestSimilarity", () => {
    it('should exist', () => {
        expect(recommender.getInterestSimilarity).toBeDefined();
    });
    it('should be function', () => {
        expect(typeof recommender.getInterestSimilarity).toBe("function");
    });
    it('should fail on invalid interests', () => {
        let user1 = {
            interests: [1, 2, 3, 4, 5]
        }
        let user2 = {
            interests: [5, 4, 3, 2]
        }
        expect(() => recommender.getInterestSimilarity(user1, user2)).toThrow("Error");
    });
    it('should fail on empty interests', () => {
        let user1 = {
            interests: []
        }
        let user2 = {
            interests: []
        }
        expect(() => recommender.getInterestSimilarity(user1, user2)).toThrow("Error");
    });
    it('should fail on no interests', () => {
        let user1 = {
        }
        let user2 = {
        }
        expect(() => recommender.getInterestSimilarity(user1, user2)).toThrow("Error");
    });
    it('should return number on valid input', () => {
        let user1 = {
            interests: [1, 2, 3, 4, 5]
        }
        let user2 = {
            interests: [5, 4, 3, 2, 1]
        }
        expect(typeof recommender.getInterestSimilarity(user1, user2)).toBe("number");
    });
    it('should return correct output on valid input', () => {
        let user1 = {
            interests: [1, 2, 3, 4, 5]
        }
        let user2 = {
            interests: [5, 4, 3, 2, 1]
        }
        expect(recommender.getInterestSimilarity(user1, user2)).toBeGreaterThan(0.65);
        expect(recommender.getInterestSimilarity(user1, user2)).toBeLessThan(0.66);

    });
});

describe("getRiderTripSimilarity", () => {
    it('should exist', () => {
        expect(recommender.getRiderTripSimilarity).toBeDefined();
    });
    it('should be function', () => {
        expect(typeof recommender.getRiderTripSimilarity).toBe("function");
    });
    it('should return empty array on bad input', async done => {
        let driverTrip;
        let riderTrips;
        recommender.getRiderTripSimilarity(driverTrip, riderTrips, (res) => {
            expect(res).toHaveLength(0);
            done();
        });
    });
});

describe("getRiderTrips", () => {
    it('should exist', () => {
        expect(recommender.getRiderTrips).toBeDefined();
    });
    it('should be function', () => {
        expect(typeof recommender.getRiderTrips).toBe("function");
    });
});
/*
describe("driverTripHandler", () => {

});
*/

describe("tripHandler", () => {
    it('should exist', () => {
        expect(recommender.tripHandler).toBeDefined();
    });
    it('should be a function', () => {
       expect(typeof recommender.tripHandler).toBe("function"); 
    });
    it('should return on valid input', async done => {
        let trip = {
            origin: 'Indigo, 2505 Granville St, Vancouver, BC V6H 3G7',
            destination: 'AMS Student Nest, 6133 University Blvd, Vancouver, BC V6T 1Z1'
        };
        expect(recommender.tripHandler(trip, (res) => {
            done();
        }));
    });
    it('should throw error on invalid input', async done => {
        let trip = {};
        try {
            recommender.tripHandler(trip, (res) => {

            });
        } catch (e) {
            done();
        }
    });
    it('should return a valid object on valid input', async done => {
        let trip = {
            origin: 'Indigo, 2505 Granville St, Vancouver, BC V6H 3G7',
            destination: 'AMS Student Nest, 6133 University Blvd, Vancouver, BC V6T 1Z1'
        };
        expect(recommender.tripHandler(trip, (res) => {
            expect(res.routes).toBeDefined();
            done();
        }));
    });
});