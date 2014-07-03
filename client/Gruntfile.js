module.exports = function (grunt) {

  function configZip(widgetFileName, mainJs) {
    return {
      router: function (fname) {
        if (fname === mainJs) {
          return 'main.js';
        }
        return fname.replace(/^widget\//, '');
      },
      src: [
        'widget/config.xml',
        'widget/index.html',
        'widget/style.css',
        'widget/main.build.js'
      ],
      dest: widgetFileName,
      compression: 'DEFLATE'
    };
  }

  grunt.initConfig({

    browserify: {
      dev: {
        options: {
          bundleOptions: { debug: true }
        },
        files: {
          'widget/main.build.js': [ 'src/main.js' ]
        }
      },
      release: {
        files: {
          'widget/main.build.js': [ 'src/main.js' ]
        }
      }
    },

    clean: ['widget/' ],

    jshint: {
      all: [ 'Gruntfile.js', 'src/**/*.js' ],
      options: {
        jshintrc: true
      }
    },

    uglify: {
      options: {
        mangle: true
      },
      release: {
        files: {
          'widget/main.min.js': [ 'widget/main.build.js' ]
        }
      }
    },

    zip: {
      dev: configZip('cobalt-widget-dev.wgt', 'widget/main.build.js'),
      release: configZip('cobalt-widget-release.wgt', 'widget/main.min.js')
    }

  });

  grunt.loadNpmTasks('grunt-browserify');
  grunt.loadNpmTasks('grunt-contrib-clean');
  grunt.loadNpmTasks('grunt-contrib-jshint');
  grunt.loadNpmTasks('grunt-contrib-uglify');
  grunt.loadNpmTasks('grunt-zip');

  grunt.registerTask('widget-dev', [
    'jshint',
    'browserify:dev',
    'zip:dev'
  ]);

  grunt.registerTask('widget-release', [
    'jshint',
    'browserify:release',
    'uglify:release',
    'zip:release'
  ]);

};

