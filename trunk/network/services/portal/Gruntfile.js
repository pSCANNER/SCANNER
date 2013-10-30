'use strict';
 
module.exports = function(grunt) {

grunt.initConfig({
  bower: {
    install: {
      options: {
        targetDir: 'src/main/webapp/dataProcessor/components',
        layout: 'byType',
        install: true,
        verbose: false,
        cleanTargetDir: false,
        cleanBowerDir: false
      }
    }
  }
});
 
grunt.loadNpmTasks('grunt-bower-task');
 
// Default task.
grunt.registerTask('default', ['bower']);
 
};