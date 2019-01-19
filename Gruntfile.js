module.exports = function(grunt) {

    grunt.initConfig({
        pkg: grunt.file.readJSON('package.json'),
        "download-electron": {
            version: "4.0.1",
            outputDir: "./electron",
            // if you want to download electron into project directory
            // downloadDir: ".electron-download",
            rebuild: true
        }
    });

    grunt.loadNpmTasks('grunt-download-electron');

};
