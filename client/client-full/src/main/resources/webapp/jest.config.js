module.exports = {
	collectCoverage: true,
    collectCoverageFrom: [
        'js/*.js'
    ],
    transform: {
        "^.+\\.[t|j]sx?$": "babel-jest"
    },
    coverageReporters: ["json", "lcov"],
    coverageDirectory: './jscoverage',
};