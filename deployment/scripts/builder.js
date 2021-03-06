#!/usr/bin/env node

const {databaseStart, databaseStop, databaseClean} = require("./lib/database");

const {startGui, startApi} = require("./lib/start");
const {build, lint} = require("./lib/build");
const {deploy} = require("./lib/deploy");
const {test} = require("./lib/test");
const {config} = require("./config");

function main() {

    const args = process.argv.map(arg => arg.trim()).slice(2);

    switch (args[0]) {
        case 'test': {
            lint(args, config);
            build(args, config);
            return test(args, config);
        }
        case 'build': {
            return build(args, config);
        }
        case 'start:gui': {
            return startGui(args, config);
        }
        case 'start:api': {
            return startApi(args, config);
        }
        case 'start:databases': {
            return databaseStart(args, config);
        }
        case 'stop:databases': {
            return databaseStop(args, config);
        }
        case 'clean:databases': {
            databaseStop(args, config);
            return databaseClean(args, config);
        }
        case 'clean-restart:databases': {
            databaseStop(args, config);
            databaseClean(args, config);
            return databaseStart(args, config);
        }
        case 'deploy': {
            return deploy(args, config);
        }
        default: {
            throw new Error(`Invalid command: ${args.join(' ')}`)
        }
    }

}

main();
